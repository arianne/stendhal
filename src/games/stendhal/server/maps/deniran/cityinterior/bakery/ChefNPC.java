/***************************************************************************
 *                   (C) Copyright 2003-2019 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.cityinterior.bakery;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * The bakery chef. Father of the camping girl.
 * He makes sandwiches for players.
 * He buys cheese.
 *
 * @author daniel
 * @see games.stendhal.server.maps.orril.river.CampingGirlNPC
 * @see games.stendhal.server.maps.quests.PizzaDelivery
 */
public class ChefNPC implements ZoneConfigurator  {

	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Patrick") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(13, 3));
				nodes.add(new Node(26, 3));
				nodes.add(new Node(26, 4));
				nodes.add(new Node(26, 4));
				nodes.add(new Node(26, 4));
				nodes.add(new Node(26, 3));
				nodes.add(new Node(13, 3));
				nodes.add(new Node(13, 7));
				nodes.add(new Node( 5, 7));
				nodes.add(new Node( 5, 2));
				nodes.add(new Node( 5, 2));
				nodes.add(new Node( 5, 2));
				nodes.add(new Node( 5, 7));
				nodes.add(new Node(14, 7));
				nodes.add(new Node(14, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {
				addGreeting("Hello and welcome to Deniran Bakery.");
				addJob("I run Deniran Bakery. ");
				addHelp("Be careful. Don't get drawn into the war.");
				addOffer("Christina handles sales, just talk to her.");

				addGoodbye();

				/*
				// Leander makes sandwiches if you bring him bread, cheese, and ham.
				final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
				requiredResources.put("bread", 1);
				requiredResources.put("cheese", 2);
				requiredResources.put("ham", 1);

				final ProducerBehaviour behaviour = new ProducerBehaviour(
						"leander_make_sandwiches", "make", "sandwich",
						requiredResources, 3 * 60);

				new ProducerAdder().addProducer(this, behaviour,
				"Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.");
				*/

			}};
			npc.setPosition(13, 3);
			npc.setEntityClass("chefnpc");
			npc.setDescription("You see Patrick. He wears a cute chef hat.");
			zone.add(npc);
	}
}
