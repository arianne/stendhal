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
package games.stendhal.server.maps.orril.dungeon;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides a Ratman chef running the Rat City bakery.
 *
 * @author omero
 */
public class RatChefNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Gaston") {

			@Override
			protected void createPath() {
                final List<Node> nodes = new LinkedList<Node>();
                nodes.add(new Node(12, 3));
                nodes.add(new Node(3, 3));
                nodes.add(new Node(7, 3));
                nodes.add(new Node(7, 6));
                nodes.add(new Node(9, 6));
                nodes.add(new Node(9, 12));
                nodes.add(new Node(16, 12));
                nodes.add(new Node(16, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addJob("I'm the best #crepes suzette au chocolate chef in town. Ask me to #bake one for you!");
				addReply("crepes",
                    "Ah le dessert for a prince... A taste of which, I really believe, would reform a cannibal into a civilized gentleman");
				addReply(Arrays.asList("chocolate","chocolate bar"),
                    "A rarity. It seems only very nasty and murderous folks carry some in their pockets.");
				addReply("flour",
                    "I stea.. ahem.. get all my supplies of flour from the nearby Semos city.");
				addReply("egg",
                    "I'd look for one where hens scratch about.");
				addReply(Arrays.asList("butter","milk"),
                    "A farm would be a good place where one could find that ingredient.");
                addReply("sugar",
                    "That comes by grinding sugar canes, wich you could harvest yourself in cane fields.");
				addOffer("I will serve you crepes suzette au chocolate if you ask me to #bake one!");
				addHelp("Ask me to #bake you my special #chocolate #crepes suzette, that's what I offer.");
				addGoodbye("Au revoire voyageur... And come back visiting me anytime you like!");

				final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
				requiredResources.put("flour", 1);
				requiredResources.put("egg", 1);
				requiredResources.put("butter", 1);
				requiredResources.put("milk", 1);
				requiredResources.put("sugar", 1);
				requiredResources.put("chocolate bar", 1);
				requiredResources.put("fierywater", 1);

				final ProducerBehaviour behaviour = new ProducerBehaviour("gaston_bake_crepesuzette", "bake", "crepes suzette",
				        requiredResources, 5 * 60);

				new ProducerAdder().addProducer(this, behaviour,
				        "Hi there. Have you come to try my #crepes suzette au chocolate? I can #bake some for you.");
			}
		};

		npc.setEntityClass("ratchefnpc");
		npc.setDirection(Direction.DOWN);
		npc.setPosition(16, 3);
		npc.initHP(100);
		npc.setDescription("You see Gaston. He is the best crepes suzette au chocolate chef in all Rat City.");
		zone.add(npc);
	}
}
