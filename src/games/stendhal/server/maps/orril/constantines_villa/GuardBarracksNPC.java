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

package games.stendhal.server.maps.orril.constantines_villa;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a npc in Constantines Villa (name:Cameron) who is a librarian
 *
 * @author storyteller (idea) and Vanessa Julius (implemented)
 *
 */

public class GuardBarracksNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Silvester") {


			//NPC walks around in the barracks of Constantines villa
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(14,27));
				nodes.add(new Node(10,27));
				nodes.add(new Node(10,22));
				nodes.add(new Node(12,22));
				nodes.add(new Node(12,18));
				nodes.add(new Node(7,18));
				nodes.add(new Node(7,22));
				nodes.add(new Node(9,22));
				nodes.add(new Node(9,27));
				nodes.add(new Node(2,27));
				nodes.add(new Node(2,12));
				nodes.add(new Node(3,12));
				nodes.add(new Node(3,8));
				nodes.add(new Node(1,8));
				nodes.add(new Node(1,4));
				nodes.add(new Node(2,4));
				nodes.add(new Node(2,7));
				nodes.add(new Node(4,7));
				nodes.add(new Node(4,12));
				nodes.add(new Node(19,12));
				nodes.add(new Node(19,18));
				nodes.add(new Node(17,18));
				nodes.add(new Node(17,23));
				nodes.add(new Node(22,23));
				nodes.add(new Node(22,18));
				nodes.add(new Node(20,18));
				nodes.add(new Node(20,12));
				nodes.add(new Node(29,12));
				nodes.add(new Node(29,6));
				nodes.add(new Node(11,6));
				nodes.add(new Node(11,4));
				nodes.add(new Node(22,4));
				nodes.add(new Node(22,5));
				nodes.add(new Node(34,5));
				nodes.add(new Node(34,2));
				nodes.add(new Node(42,2));
				nodes.add(new Node(42,7));
				nodes.add(new Node(38,7));
				nodes.add(new Node(38,6));
				nodes.add(new Node(31,6));
				nodes.add(new Node(31,12));
				nodes.add(new Node(40,12));
				nodes.add(new Node(40,18));
				nodes.add(new Node(39,18));
				nodes.add(new Node(39,22));
				nodes.add(new Node(43,22));
				nodes.add(new Node(43,18));
				nodes.add(new Node(41,18));
				nodes.add(new Node(41,12));
				nodes.add(new Node(48,12));
				nodes.add(new Node(48,27));
				nodes.add(new Node(30,27));
				nodes.add(new Node(30,21));
				nodes.add(new Node(32,21));
				nodes.add(new Node(32,17));
				nodes.add(new Node(31,17));
				nodes.add(new Node(31,16));
				nodes.add(new Node(29,16));
				nodes.add(new Node(29,18));
				nodes.add(new Node(28,18));
				nodes.add(new Node(28,22));
				nodes.add(new Node(30,22));
				nodes.add(new Node(30,27));

               	setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Hey, [name]! Why are you sneaking around in here? Out with you, NOW!");

			}

		};

		npc.setDescription("You see one of Constantines powerful guards, Silvester. Better don't jump into his side!");
		npc.setEntityClass("nightguardbownpc");
		npc.setPosition(14, 27);
		npc.initHP(100);
		zone.add(npc);
	}
}
