/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package games.stendhal.server.maps.orril.constantines;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a npc in Constantines Villa (name:Klaus) who is one of Constantines villa guards
 *
 * @author storyteller (idea) and Vanessa Julius (implemented)
 *
 */

public class GuardEntranceNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Klaus") {

			//NPC walks around in the entrance hall of Constantines villa
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(15,46));
				nodes.add(new Node(8,46));
				nodes.add(new Node(8,29));
				nodes.add(new Node(17,29));
				nodes.add(new Node(17,18));
				nodes.add(new Node(21,18));
				nodes.add(new Node(21,3));
				nodes.add(new Node(26,3));
				nodes.add(new Node(26,46));
				nodes.add(new Node(28,46));
				nodes.add(new Node(28,41));
				nodes.add(new Node(27,41));
				nodes.add(new Node(27,30));
				nodes.add(new Node(28,30));
				nodes.add(new Node(28,3));
				nodes.add(new Node(3,3));
				nodes.add(new Node(3,37));
				nodes.add(new Node(4,37));
				nodes.add(new Node(4,47));
				nodes.add(new Node(2,47));
				nodes.add(new Node(2,45));
				nodes.add(new Node(4,45));
				nodes.add(new Node(4,38));
				nodes.add(new Node(2,38));
				nodes.add(new Node(2,3));
				nodes.add(new Node(9,3));
				nodes.add(new Node(9,19));
				nodes.add(new Node(13,19));
				nodes.add(new Node(13,45));
				nodes.add(new Node(15,45));
               	setPath(new FixedPath(nodes, true));

			}

			@Override

			//Greeting and goodbye message in quest code TheMissingBooks.java

			protected void createDialog() {
				addGreeting("Hey, [name]! Why are you sneaking around in here? Out with you, NOW!");


			}
		};

		npc.setDescription("You see Klaus, one of Constantines guards. Better don't step into his side!");
		npc.setEntityClass("nightguardswordnpc");
		npc.setPosition(15, 46);
		npc.initHP(100);
		zone.add(npc);
	}
}
