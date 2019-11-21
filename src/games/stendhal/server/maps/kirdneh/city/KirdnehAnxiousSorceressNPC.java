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

package games.stendhal.server.maps.kirdneh.city;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
//import games.stendhal.server.entity.npc.action.StoreMessageAction;

/**
 * Provides Vlamyklela
 *
 * @author omero
 */

public class KirdnehAnxiousSorceressNPC implements ZoneConfigurator {
	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private SpeakerNPC buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Vlamyklela") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(113, 95));
				nodes.add(new Node(117, 89));
				nodes.add(new Node(119, 93));
				nodes.add(new Node(121, 90));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {
				addGreeting(
						"Ave");
				addGoodbye(
						"Fortvna");
                /**
                 * When one has Ad Memoria In Portfolio quest active
                 * Will Convert 1x purple apple into 1x mauve apple
                 */
				addHelp(
					"My stepbrother Brosoklelo... He must be stranded somewhere..." + " " +
					"Brosoklelo likes dueling magical duels... " + " " +
					"Tell me you got a purple apple for me!"
				);
				addOffer(
					"I could turn a purple apple into a mauve apple..." + " " +
					"When you bring me a purple apple, I will know what to do!"
				);
				addJob(
					"I can turn a purple apple into a mauve apple. That is my job!"
				);
				addReply(
                    "apple", //trigger
                    "Apples may come in different colors!" + " " +
                    "You would not think an apple can restore lost memory..." + " " +
                    "Say you are were bestowed with a purple apple...",
                    null
	            );

				/**
				 * additional behavior defined in AdMemoriaInPortfolio quest
				 */

			}
		};

		// Finalize Vlamyklela
		npc.setEntityClass("bluesorceressnpc");
		npc.initHP(100);
		npc.setPosition(113,99);
		npc.setCollisionAction(CollisionAction.REROUTE);
		npc.setDescription("You see Vlamyklela... She seems to be anxiously awaiting news!");
		zone.add(npc);
		return npc;
	}
}
