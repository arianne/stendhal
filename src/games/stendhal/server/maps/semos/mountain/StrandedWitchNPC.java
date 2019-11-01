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

package games.stendhal.server.maps.semos.mountain;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Provides StrandedWitchNPC
 *
 * @author omero
 */

public class StrandedWitchNPC implements ZoneConfigurator {
	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private SpeakerNPC buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Blasyklela") {

			//Blasyklela position is temporary, will relocate in Kirdneh
			//Blasyklela position orbits around 0_semos_mountain_n2_w at (84,111)
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(81, 115));
				nodes.add(new Node(88, 115));
				nodes.add(new Node(88, 115));
				nodes.add(new Node(88, 111));
				nodes.add(new Node(84, 105));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {
				addGreeting(
						"Ave");
				addGoodbye(
						"Fortvna");
                /**
                 * Will Convert 1x purple apple into 1x mauve apple 
                 * Only when one has Ad Memoria In Portfolio quest
                 */				
				addHelp("I am worried about my stepbrother Brosoklelo... He likes dueling magical duels! Tell me you got a purple apple... ");
				addOffer("I could turn a purple apple into a mauve apple... When you bring me a purple apple I will know...");
				addJob("I am awaiting a purple apple from my stepbrother Brosoklelo... That is my job!");
				
				/**
				 * additional behavior defined in AdMemoriaInPortfolio quest
				 */
				
			}
		};
		
		// Finalize Blasyklela
		//Blasyklela position is temporary, will relocate in Kirdneh
		//Blasyklela position orbits around 0_semos_mountain_n2_w at (84,111)
		npc.setEntityClass("bluesorceressnpc");
		npc.setPosition(84,116);
		npc.initHP(100);
		npc.setCollisionAction(CollisionAction.REROUTE);
		npc.setDescription("You see Blasyklela... She seems anxiously awaiting news!");
		zone.add(npc);
		return npc;
	}
}
