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
package games.stendhal.server.maps.fado.bakery;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;

/**
 * Builds the bakery baker NPC.
 *
 * @author timothyb89/kymara
 */
public class BakerNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

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

	//
	// IL0_BakerNPC
	//

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC baker = new SpeakerNPC("Linzo") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				// to the well
				nodes.add(new Node(15, 3));
				// to a barrel
				nodes.add(new Node(15, 8));
				// to the baguette on the table
				nodes.add(new Node(13, 8));
				// around the table
				nodes.add(new Node(13, 10));
				nodes.add(new Node(10, 10));
				// to the sink
				nodes.add(new Node(10, 12));
				// to the pizza/cake/whatever
				nodes.add(new Node(7, 12));
				nodes.add(new Node(7, 10));
				// to the pot
				nodes.add(new Node(3, 10));
				// towards the oven
				nodes.add(new Node(3, 4));
				nodes.add(new Node(5, 4));
				// to the oven
				nodes.add(new Node(5, 3));
				// one step back
				nodes.add(new Node(5, 4));
				// towards the well
				nodes.add(new Node(15, 4));

				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addJob("I'm the local baker. My speciality is fish and leek pies. I pride myself in making them promptly.");
				addReply(Arrays.asList("cod", "mackerel"),
				        "You can catch cod in Ados. Mackerel may be caught at sea. Perhaps creatures which eat fish might drop them too.");
				addReply("flour", "We get our supplies of flour from Semos");
				addReply("leek", "We're lucky enough to have leeks growing right here in the Fado allotments.");
				addHelp("Ask me to make you a fish and leek pie. They're not stodgy like meat pies so you can eat them a little quicker.");
				addGoodbye();

				// Linzo makes fish pies if you bring him flour, leek, cod and mackerel
				// (uses sorted TreeMap instead of HashMap)
				final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
				requiredResources.put("flour", 1);
				requiredResources.put("cod", 2);
				requiredResources.put("mackerel", 1);
				requiredResources.put("leek", 1);

				final ProducerBehaviour behaviour = new ProducerBehaviour("linzo_make_fish_pie", "make", "fish pie",
				        requiredResources, 5 * 60);

				new ProducerAdder().addProducer(this, behaviour,
				        "Hi there. Have you come to try my fish pies? I can #make one for you.");
			}
		};

		baker.setEntityClass("bakernpc");
		baker.setDirection(Direction.DOWN);
		baker.setPosition(15, 3);
		baker.initHP(1000);
		baker.setDescription("You see Linzo. He is the local Fado baker and a specialist in baking fish and leek pies.");
		zone.add(baker);
	}
}
