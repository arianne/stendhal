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

import games.stendhal.common.Grammar;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.item.Item;
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
  * @author omero
  */
public class KoboldBarmaidNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}

	private void buildNPC(final StendhalRPZone zone, final Map<String, String> attributes) {

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
					public boolean transactAgreedDeal(final EventRaiser seller, final Player player) {
						final Item item = getAskedItem(chosenItemName);
						String requiredContainer = "";

						if ("mild koboldish torcibud".equals(chosenItemName)) {
							requiredContainer = "slim bottle";
						} else if ("strong koboldish torcibud".equals(chosenItemName)) {
							requiredContainer = "eared bottle";
						}

						int price = getCharge(player);
						
						if (player.isBadBoy()) {
						        price = (int) (BAD_BOY_BUYING_PENALTY * price);
						}
						
						if ("slim bottle".equals(requiredContainer) || "eared bottle".equals(requiredContainer)) {
							if (!player.isEquipped(requiredContainer, amount) || !player.isEquipped("money", price)) {
								seller.say("Wrauff! I can only sell you "
									+ Grammar.plnoun(getAmount(), getChosenItemName())
									+ "if you meet the price of " + price + " and have " + amount + " empty "
									+ Grammar.plnoun(getAmount(), requiredContainer));
							        return false;
							}
						} else if (!player.isEquipped("money", price)) {
								seller.say("Wruff! I can only sell you "
									+ Grammar.plnoun(getAmount(), getChosenItemName())
									+ " when you will have enough money");
						        return false;
						}
						
						if (player.equipToInventoryOnly(item)) {
							player.drop("money", price);
							if (requiredContainer != "") {
						               	player.drop(requiredContainer, amount);
							}
						
							seller.say("Wroff! Here "
									+ Grammar.isare(getAmount()) + " your "
									+ Grammar.plnoun(getAmount(), getChosenItemName()) + "!");
							return true;

					        } else {
					                seller.say("Wruff.. You cannot carry any "
					                                + Grammar.plnoun(getAmount(), getChosenItemName())
									+ " in your bag now.");
					                return false;
					        }
					}
				}

				final Map<String, Integer> items = new HashMap<String, Integer>();

				//beer and wine have higher than average prices here.
				items.put("beer", 18);
				items.put("wine", 25);
				items.put("mild koboldish torcibud", 95);
				items.put("strong koboldish torcibud", 195);

				new SellerAdder().addSeller(this, new TorcibudSellerBehaviour(items));

				addGreeting("Wroff! I'm Wrviliza, wife of #Wrvil... Welcome into Wofol's Den bar wanderer!");
				addJob("Wroff! I offer wine, beer and my famous #mild or #strong koboldish #torcibud");
				addHelp("Wruff... If you are thirsty I can #offer you some beverage. Have you noticed that this is a bar?");
				addGoodbye("Wroff... Wroff!");


				addReply(Arrays.asList("wine","beer"),
						"Wrof! It will quench your thirst for a few coins...");
				addReply("mild",
						"Wrof! Not so #strong koboldish #torcibud. Give a slim bottle and 90 money... Wrof!");
				addReply("strong",
						"Wrof! Not so #mild koboldish #torcibud. Give a eared bottle and 180 money... Wrof!");
				addReply("torcibud",
						"Wrof! Real stuff from a secret koboldish recipe! Want me #offer some?");
				addReply("wrvil",
						"Wrof! He be my husband. Runs shop in northern Wo'fol...");
			}
		};

		npc.setEntityClass("koboldbarmaidnpc");
		npc.setPosition(9, 3);
		npc.initHP(100);
		npc.setDescription("You see Wrviliza, the kobold barmaid.");
		zone.add(npc);

	}
}
