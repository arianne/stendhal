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
package games.stendhal.server.maps.ados.felinashouse;

import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.creature.Cat;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CatSellerNPC implements ZoneConfigurator {

	public static final int BUYING_PRICE = 100;

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildHouseArea(zone);
	}

	private void buildHouseArea(final StendhalRPZone zone) {

		final SpeakerNPC npc = new SpeakerNPC("Felina") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(6, 8));
				nodes.add(new Node(11, 8));
				nodes.add(new Node(11, 17));
				nodes.add(new Node(19, 17));
				nodes.add(new Node(19, 21));
				nodes.add(new Node(14, 21));
				nodes.add(new Node(14, 16));
				nodes.add(new Node(10, 16));
				nodes.add(new Node(10, 4));
				nodes.add(new Node(6, 4));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				class CatSellerBehaviour extends SellerBehaviour {
					CatSellerBehaviour(final Map<String, Integer> items) {
						super(items);
					}

					@Override
					public boolean transactAgreedDeal(ItemParserResult res, final EventRaiser seller, final Player player) {
						if (res.getAmount() > 1) {
							seller.say("Hmm... I just don't think you're cut out for taking care of more than one cat at once.");
							return false;
						} else if (player.hasPet()) {
							say("Well, why don't you make sure you can look after that pet you already have first?");
							return false;
						} else {
							if (!player.drop("money", getCharge(res, player))) {
								seller.say("You don't seem to have enough money.");
								return false;
							}
							seller.say("Here you go, a cute little kitten! Your kitten will eat any piece of chicken or fish you place on the ground. Enjoy her!");

							final Cat cat = new Cat(player);
							
							Entity sellerEntity = seller.getEntity();
							cat.setPosition(sellerEntity.getX(), sellerEntity.getY() + 1);

							player.setPet(cat);
							player.notifyWorldAboutChanges();

							return true;
						}
					}
				}

				final Map<String, Integer> items = new HashMap<String, Integer>();
				items.put("cat", BUYING_PRICE);

				addGreeting();
				addJob("I sell cats. Well, really they are just little kittens when I sell them to you but if you #care for them well they grow into cats.");
				addHelp("I sell cats. To buy one, just tell me you want to #buy #cat. If you're new to this business, I can tell you how to #travel with her and take #care of her. If you find any wild cat, incidentally, you can make them your #own.");
				addGoodbye();
				new SellerAdder().addSeller(this, new CatSellerBehaviour(items));
				addReply("care",
						"Cats love chicken and fish. Just place a piece on the ground and your cat will run over to eat it. You can right-click on her and choose 'Look' at any time, to check up on her weight; she will gain one unit of weight for every piece of chicken she eats.");
				addReply("travel",
						"You'll need your cat to be close by in order for her to follow you when you change zones; you can say #cat to call her if she's not paying attention. If you decide to abandon her instead, you can right-click on yourself and select 'Leave Pet'; but frankly I think that sort of behaviour is disgraceful.");
				addReply("sell",
						"Sell??? What kind of a monster are you? Why would you ever sell your beautiful cat?");
				addReply("own",
						"If you find any wild or abandoned cat, you can right-click on them and select 'Own' to tame them. It will start following you immediately. Cats go a bit crazy without an owner!");
			}
		};

		npc.setEntityClass("woman_009_npc");
		npc.setPosition(6, 8);
		npc.initHP(100);
		npc.setDescription("Felina is walking around while taking care of her kittens. They are purring out of all corners.");
		zone.add(npc);
		
		// Also put a cat in her bedroom (people can't Own it as it is behind a fence)
		final Cat hercat = new Cat();
                hercat.setPosition(19, 3);
                zone.add(hercat);

	}
}
