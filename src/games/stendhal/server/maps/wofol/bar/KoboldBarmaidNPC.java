/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.wofol.bar;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
  * Provides Wrviliza, the kobold barmaid in Wo'fol.
  * She's Wrvil's wife.
  *
  * Offers a quest wich rewards the player with some bottles of V.S.O.P. koboldish torcibud.
  *
  * @author omero
  */
public class KoboldBarmaidNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {

		final SpeakerNPC npc = new SpeakerNPC("Wrviliza") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(14, 2));
				nodes.add(new Node(14, 3));
				nodes.add(new Node(11, 3));
				nodes.add(new Node(11, 2));
				nodes.add(new Node(6, 2));
				nodes.add(new Node(6, 3));
				nodes.add(new Node(9, 3));
				nodes.add(new Node(9, 2));
				setPath(new FixedPath(nodes, true));
			}

			@Override

			protected void createDialog() {
				class TorcibudSellerBehaviour extends SellerBehaviour {
					TorcibudSellerBehaviour(final Map<String, Integer> items) {
						super(items);
					}

					/**
					  * Wrviliza will sell her mild or strong koboldish torcibud
					  * only when the player can afford the price and carries as many empty bottles
					  * as the requested amount in his inventory.
					  */
					@Override
					public boolean transactAgreedDeal(ItemParserResult res, final EventRaiser seller, final Player player) {
						String chosenItemName = res.getChosenItemName();
						final Item item = getAskedItem(chosenItemName);
						int amount = res.getAmount();
						String requiredContainer = "";

						if ("mild koboldish torcibud".equals(chosenItemName)) {
							requiredContainer = "slim bottle";
						} else if ("strong koboldish torcibud".equals(chosenItemName)) {
							requiredContainer = "eared bottle";
						}

						int price = getCharge(res, player);
						if (player.isBadBoy()) {
						        price = (int) (BAD_BOY_BUYING_PENALTY * price);
						}
						
						if ("slim bottle".equals(requiredContainer) || "eared bottle".equals(requiredContainer)) {
							if (!player.isEquipped(requiredContainer, amount) || !player.isEquipped("money", price)) {
								seller.say("Wrauff! I can only sell you "
									+ Grammar.plnoun(amount, chosenItemName)
									+ " if you meet the price of " + price + " and have " + amount + " empty "
									+ Grammar.plnoun(amount, requiredContainer) + ".");
							        return false;
							}
						} else if (!player.isEquipped("money", price)) {
								seller.say("Wruff! I can only sell you "
									+ Grammar.plnoun(amount, chosenItemName)
									+ " if you have enough money.");
						        return false;
						}

                        /**
                         * If the user tries to buy several of a non-stackable item,
                         * he is forced to buy only one.
                         */
                        if (item instanceof StackableItem) {
                            ((StackableItem) item).setQuantity(amount);
                        } else {
                            res.setAmount(1);
                        }
						
						if (player.equipToInventoryOnly(item)) {
							player.drop("money", price);
							if (!"".equals(requiredContainer)) {
						               	player.drop(requiredContainer, amount);
							}
							seller.say("Wroff! Here "
									+ Grammar.isare(amount) + " your "
									+ Grammar.plnoun(amount, chosenItemName) + "!");
							return true;
                        } else {
                            seller.say("Wruff.. You cannot carry any "
                                    + Grammar.plnoun(amount, chosenItemName)
									+ " in your bag now.");
                            return false;
                        }
					}
				}

                // edit prices here and they'll be correct everywhere else
                final int MILD_KOBOLDISH_TORCIBUD_PRICE = 95;
                final int STRONG_KOBOLDISH_TORCIBUD_PRICE = 195;
                
				final Map<String, Integer> items = new HashMap<String, Integer>();
				//beer and wine have higher than average prices here.
				items.put("beer", 18);
				items.put("wine", 25);
				items.put("mild koboldish torcibud", MILD_KOBOLDISH_TORCIBUD_PRICE);
				items.put("strong koboldish torcibud", STRONG_KOBOLDISH_TORCIBUD_PRICE);

				new SellerAdder().addSeller(this, new TorcibudSellerBehaviour(items));

				addGreeting(
					"Wroff! Welcome into the Kobold's Den bar wanderer!"
						+ " I'm Wrviliza, wife of #Wrvil."
						+ " If you want me to #offer you some beverages, just say so!");
				addJob("Wroff! I offer wine, beer and my famous #mild or #strong koboldish #torcibud.");
				addHelp("Wruff... If you are thirsty I can #offer you some beverage. If you didn't notice, this is a bar!");
				addGoodbye("Wroff... Goodbye and good luck!");

				addReply(Arrays.asList("wine","beer"),
						"Wrof! It will quench your thirst for a few coins...");
				addReply("mild",
						"Wrof! Not so #strong koboldish #torcibud. Give an empty #slim #bottle and "
                            + MILD_KOBOLDISH_TORCIBUD_PRICE + " moneys... Wrof!");
				addReply("strong",
						"Wrof! Not so #mild koboldish #torcibud. Give an empty #eared #bottle and "
                            + STRONG_KOBOLDISH_TORCIBUD_PRICE + " moneys... Wrof!");
				addReply("torcibud",
						"Wrof! Real stuff from a secret koboldish recipe! Ask me to #offer you some!");
				addReply("wrvil",
						"Wrof! He be my husband. Runs shop in northern Wo'fol...");
				addReply("eared bottle",
						"Wrof! A large bottle with handles on the neck that resemble ears... It cannot be you have never seen one!");
				addReply("slim bottle",
						"Wrof! A bottle narrower at bottom and a bit wider at the shoulders ... I'm sure you've seen one already!");

                /**
                 * Additional behavior code is in games.stendhal.server.maps.quests.KoboldishTorcibud
                 */
			}
		};

		npc.setEntityClass("koboldbarmaidnpc");
		npc.setPosition(9, 3);
		npc.initHP(100);
		npc.setDescription("You see Wrviliza, the kobold barmaid.");
		zone.add(npc);

	}
}
