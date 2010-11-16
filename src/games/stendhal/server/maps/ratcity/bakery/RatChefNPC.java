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
package games.stendhal.server.maps.ratcity.bakery;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Provides a Ratman chef running the Rat City bakery.
 * NOTE: the producing code inhibited because
 * one required ingredient for crepes suzette au chocolate
 * is 'sugar' which is not available unless summoned.
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
                addGreeting("Hi there. I hope you haven't come to taste my #crepes suzette au chocolate because I'm still setting up my bakery for that!");
				//addJob("I'm the best #crepes suzette au chocolate chef in town. Ask me to #bake one for you!");
				addJob("I'm busy with the preparations for opening this bakery."
                    + " Soon you may taste the finest #crepes suzette au chocolate in town!");
				addReply("crepes",
                    "Ah le dessert for a prince... A taste of which, I really believe, would reform a cannibal into a civilized gentleman.");
				addReply(Arrays.asList("chocolate","chocolate bar"),
                    "A rarity. It seems only very nasty and murderous folks carry some in their pockets.");
				addReply("flour",
                    "I stea.. ahem.. get all my supplies of flour from the nearby Semos city.");
				addReply("egg",
                    "I'd look for one where hens scratch about.");
				addReply(Arrays.asList("butter","milk"),
                    "A farm would be a good place where one could find that ingredient.");
                addReply("sugar",
                    "That comes from grinding sugar canes, which you could harvest yourself in cane fields.");
				//addOffer("I will serve you crepes suzette au chocolate if you ask me to #bake one!");
				addOffer("Alas, this bakery shop still needs some finishing touches before I can offer you my #crepes suzette au chocolate!");
				//addHelp("Ask me to #bake you my special #chocolate #crepes suzette, that's what I offer.");
				addHelp("My apologies but I'm rather busy making this bakery shop ready, so I cannot be very helpful to you at the moment.");
				addGoodbye("Au revoire voyageur... And come back to visit me anytime you like!");

                /*
                 * the sugar ingredient cannot be obtained at the moment
                 * the producer code is therefore disabled until sugar is made available to players
                 *
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
				        "Hi there. I bet you've come to taste my #crepes suzette au chocolate! I can #bake some for you if you like.");
                */
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
