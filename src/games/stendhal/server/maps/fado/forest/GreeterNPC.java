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
package games.stendhal.server.maps.fado.forest;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

/**
 * Builds a Greeter NPC.
 *
 * @author kymara
 */
public class GreeterNPC implements ZoneConfigurator {
	private final ShopList shops = SingletonRepository.getShopList();

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
		final SpeakerNPC npc = new SpeakerNPC("Orchiwald") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(3, 12));
				nodes.add(new Node(40, 12));
				nodes.add(new Node(40, 28));
				nodes.add(new Node(58, 28));
				nodes.add(new Node(58, 91));
				nodes.add(new Node(99, 91));
				nodes.add(new Node(99, 76));
				nodes.add(new Node(36, 76));
				nodes.add(new Node(36, 37));
				nodes.add(new Node(3, 37));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
			        addGreeting("Welcome to the humble dwellings of the albino elves.");
				addJob("I just wander around. In fact, albino elves wander around a lot. We're #nomadic, you know.");
				addReply("nomadic", "We don't have a permanent home, we travel instead between forests and glens. When we find a clearing we like, we settle. We liked this one because of the ancient #stones near by.");
				addReply("stones", "They have some mystical quality. We like to be by them for the changing of the seasons.");
				addHelp("I would sell you enchanted scrolls to return to Fado City. I have a source of cheap ones.");
				new SellerAdder().addSeller(this, new SellerBehaviour(shops.get("fadoscrolls")) {

					@Override
					public int getUnitPrice(final String item) {
						// Player gets 20 % rebate
						return (int) (0.80f * priceCalculator.calculatePrice(item, null));
					}
				});
				addQuest("A generous offer, but I require nothing, thank you.");
 				addGoodbye("Bye then.");
			}
		};

		npc.setDescription("You see Orchiwald, an albino elf.");
		npc.setEntityClass("albinoelf2npc");
		npc.setPosition(3, 12);
		npc.initHP(100);
		zone.add(npc);
	}
}
