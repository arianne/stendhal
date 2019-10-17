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
 * Provides StrandedWizard
 *
 * @author omero
 */
public class StrandedWizardNPC implements ZoneConfigurator {
	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private SpeakerNPC buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("StrandedWizard") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(72, 123));
				nodes.add(new Node(74, 124));
				nodes.add(new Node(77, 124));
				nodes.add(new Node(82, 124));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {
				addGreeting(
						"Ave");
				addHelp(
						"I am stranded, I can not help you... I lost #memory");
				addOffer(
						"I am stranded, I can not offer you anything... I lost #memory");
				addJob(
                        "I am stranded, I do not have any job... I lost #memory");
				addGoodbye(
						"Fortvna!");
				addReply(
						"memory",
						"Laaah lah lah laaah... Laaah lah laah laaaaah... " +
						"Fencing in a mist... Dancing in the fog... Catch those memories... Catch them all! " + 
                        "I vanished... Still here... I am stranded, not lost..." +
                        "Stranded yet not lost");
			}
		};
		
		// Finalize StrandedWizard
		npc.setEntityClass("brownwizardnpc");
		npc.setPosition(65,120);
		npc.initHP(100);
		npc.setCollisionAction(CollisionAction.REROUTE);
		npc.setDescription("You see StrandedWizard");
		zone.add(npc);
		return npc;
	}
}