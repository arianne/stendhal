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
package games.stendhal.server.maps.ados.goldsmith;

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
 * Ados Goldsmith (Inside / Level 0).
 *
 * @author dine
 */
public class GoldsmithNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildGoldsmith(zone);
	}

	private void buildGoldsmith(final StendhalRPZone zone) {
		final SpeakerNPC goldsmith = new SpeakerNPC("Joshua") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				// by the sink
				nodes.add(new Node(5, 7));
				// to the left wall
				nodes.add(new Node(2, 7));
				nodes.add(new Node(11, 7));
				// up one by the armor
				nodes.add(new Node(11, 6));
				nodes.add(new Node(12, 6));
				nodes.add(new Node(12, 5));
				// to the right wall
				nodes.add(new Node(18, 5));
				nodes.add(new Node(18, 3));
				// to the starting point
				nodes.add(new Node(5, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi!");
				addJob("I'm the goldsmith of this city.");
				addHelp("My brother Xoderos is a blacksmith in Semos. Currently he is selling tools. Perhaps he can make a #gold #pan for you.");
				addGoodbye("Bye.");

				// Joshua makes gold if you bring him gold nugget and wood
				final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
				requiredResources.put("wood", 2);
				requiredResources.put("gold nugget", 1);

				final ProducerBehaviour behaviour = new ProducerBehaviour("joshua_cast_gold",
						"cast", "gold bar", requiredResources, 15 * 60);

				new ProducerAdder().addProducer(this, behaviour,
				        "Hi! I'm the local goldsmith. If you require me to #cast you a #'gold bar' just tell me!");
				addReply("wood",
		        		"I need some wood to keep my furnace lit. You can find any amount of it just lying around in the forest.");
				addReply(Arrays.asList("ore", "gold ore", "gold nugget"),
				        "I think there are places in the water where you can find gold ore. But you need a special tool to prospect for gold.");
				addReply(Arrays.asList("gold bar", "gold", "bar"),
				        "After I've casted the gold for you keep it safe. I've heard rumours that Fado city will be safe to travel to again soon. There you can sell or trade gold.");
				addReply("gold pan",
				        "If you had a gold pan, you would be able to prospect for gold at certain places.");
				addReply(Arrays.asList("oil", "can of oil", "buy oil"), "Oh, fishermen supply us with that.");
			}
		};

		goldsmith.setEntityClass("goldsmithnpc");
		goldsmith.setDirection(Direction.DOWN);
		goldsmith.setPosition(18, 3);
		goldsmith.initHP(100);
		goldsmith.setDescription("You see Joshua. His family is popular for forging different materials. Do you know his brother, Xoderos, already?");
		zone.add(goldsmith);
	}
}
