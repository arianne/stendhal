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
package games.stendhal.server.maps.orril.bridge;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds the bridge guard (to fado) NPC.
 *
 * @author kymara
 */
public class GuardNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 *
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Stefan") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(62, 105));
				nodes.add(new Node(63, 105));
				nodes.add(new Node(64, 105));
				nodes.add(new Node(65, 105));
				nodes.add(new Node(64, 105));
				nodes.add(new Node(63, 105));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi, can I #help you?");
				addJob("I guard this bridge and check the road block stays in place.");
				addHelp("The road to Fado is closed until the city is safe.");
				addQuest("I'd like something more interesting to do, too.");
				addGoodbye("Goodbye and come back soon, I get bored here.");
			}
		};

		npc.setDescription("You see a bored looking guard.");
		npc.setEntityClass("recruiter1npc");
		npc.setPosition(62, 105);
		npc.initHP(100);
		zone.add(npc);
	}
}
