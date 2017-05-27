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
package games.stendhal.server.maps.ados.bakery;

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
 * Ados Bakery (Inside / Level 0).
 *
 * @author hendrik
 */
public class BakerNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildBakery(zone);
	}

	private void buildBakery(final StendhalRPZone zone) {
		final SpeakerNPC baker = new SpeakerNPC("Arlindo") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				// to the well
				nodes.add(new Node(15, 3));
				// to a barrel
				nodes.add(new Node(15, 8));
				// to the baguette on the table
				nodes.add(new Node(13, 9));
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
				// addGreeting("Hi, most of the people are out of town at the moment.");
				addJob("I'm the local baker. Although we get most of our supplies from Semos City, there is still a lot of work to do.");
				addReply(Arrays.asList("flour", "meat", "carrot"),
				        "Ados is short on supplies. We get most of our food from Semos City which is west of here.");
				addReply(Arrays.asList("mushroom", "button mushroom"),
					    "We got complaints that our pies are not filling enough, so we put more mushrooms in now. You can find them in woods.");
				addHelp("If you have plenty of meat or cheese you can sell it to Siandra in Ados bar.");
				addGoodbye();

				// Arlindo makes pies if you bring him flour, meat, carrot and a mushroom
				// (uses sorted TreeMap instead of HashMap)
				final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
				requiredResources.put("flour", Integer.valueOf(2));
				requiredResources.put("meat", Integer.valueOf(2));
				requiredResources.put("carrot", Integer.valueOf(1));
				requiredResources.put("button mushroom", Integer.valueOf(2));

				final ProducerBehaviour behaviour = new ProducerBehaviour("arlindo_make_pie", "make", "pie",
				        requiredResources, 7 * 60);

				new ProducerAdder().addProducer(this, behaviour,
				        "Hi! I bet you've heard about my famous pie and want me to #make one for you, am I right?");
			}
		};

		baker.setEntityClass("bakernpc");
		baker.setDirection(Direction.DOWN);
		baker.setPosition(15, 3);
		baker.initHP(100);
		baker.setDescription("Arlindo is the official Ados baker who is popular for his excellent pies.");
		zone.add(baker);
	}

}
