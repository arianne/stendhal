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
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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

					@Override
					public boolean transactAgreedDeal(final EventRaiser seller, final Player player) {
						final Item item = getAskedItem(chosenItemName);
						String requiredContainer = "";

						if ("mild koboldish torcibud".equals(chosenItemName)) {
							requiredContainer = "bottle";
						} else if ("strong koboldish torcibud".equals(chosenItemName)) {
							requiredContainer = "big bottle";
						}

						int price = getCharge(player);
						
						if (player.isBadBoy()) {
						        price = (int) (BAD_BOY_BUYING_PENALTY * price);
						}
						
						if ("bottle".equals(requiredContainer) || "big bottle".equals(requiredContainer)) {
							if (!player.isEquipped(requiredContainer, amount) || !player.isEquipped("money", price)) {
								seller.say("Wroff! I can only sell you "
									+ Grammar.plnoun(getAmount(), getChosenItemName())
									+ "if you meet the price of " + price + " and have " + amount + " empty "
									+ Grammar.plnoun(getAmount(), requiredContainer));
							        return false;
							}
						} else if (!player.isEquipped("money", price)) {
								seller.say("Wroff! I can only sell you "
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
					                seller.say("Wruff.. You cannot carry "
					                                + Grammar.plnoun(getAmount(), getChosenItemName()) + ".");
					                return false;
					        }
					}
				}

				final Map<String, Integer> items = new HashMap<String, Integer>();

				items.put("beer", 10);
				items.put("wine", 20);
				items.put("mild koboldish torcibud", 3000);
				items.put("strong koboldish torcibud", 5000);

				new SellerAdder().addSeller(this, new TorcibudSellerBehaviour(items));

				addGreeting("Wroff! I'm Wrviliza, wife of #Wrvil... Welcome in Wofol's Den bar wanderer!");
				addJob("Wroff! I offer wine, beer and my famous #mild or #strong koboldish #torcibud");
				addHelp("Wruff... If you are thirsty I can #offer you some beverage. Have you noticed this is a bar?");
				addGoodbye("Wroff... Wroff!");


				addReply("mild",
						"Wrof! Not so #strong koboldish #torcibud.");
				addReply("strong",
						"Wrof! Not so #mild koboldish #torcibud.");
				addReply("torcibud",
						"Wrof! Real stuff! Want me #offer some?");
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
