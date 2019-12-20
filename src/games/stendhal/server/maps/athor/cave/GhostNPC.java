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
package games.stendhal.server.maps.athor.cave;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.GhostNPCBase;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a Ghost NPC.
 *
 * @author kymara
 */
public class GhostNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC ghost = new GhostNPCBase("Mary") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(65, 74));
				nodes.add(new Node(43, 74));
				nodes.add(new Node(43, 69));
				nodes.add(new Node(37, 69));
				nodes.add(new Node(37, 65));
				nodes.add(new Node(34, 65));
				nodes.add(new Node(34, 58));
				nodes.add(new Node(31, 58));
				nodes.add(new Node(31, 52));
				nodes.add(new Node(28, 52));
				nodes.add(new Node(28, 46));
				nodes.add(new Node(26, 46));
				nodes.add(new Node(26, 41));
				nodes.add(new Node(25, 41));
				nodes.add(new Node(25, 38));
				nodes.add(new Node(36, 38));
				nodes.add(new Node(36, 43));
				nodes.add(new Node(45, 43));
				nodes.add(new Node(45, 46));
				nodes.add(new Node(61, 46));
				nodes.add(new Node(61, 54));
				nodes.add(new Node(63, 54));
				nodes.add(new Node(63, 58));
				nodes.add(new Node(64, 58));
				nodes.add(new Node(64, 66));
				nodes.add(new Node(65, 66));
				setPath(new FixedPath(nodes, true));
			}
		};

		ghost.setDescription("You see a ghostly figure of a woman.");
		ghost.setResistance(0);
		ghost.setEntityClass("woman_005_npc");
		// she is a ghost so she is see through
		ghost.setVisibility(50);
		ghost.setPosition(65, 74);
		// she has low HP
		ghost.initHP(30);
		ghost.setBaseHP(100);
		ghost.put("no_shadow", "");
		zone.add(ghost);
	}
}
