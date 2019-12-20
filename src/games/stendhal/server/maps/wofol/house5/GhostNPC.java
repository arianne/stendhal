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
package games.stendhal.server.maps.wofol.house5;

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
	 * Configures a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC ghost = new GhostNPCBase("Zak") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(3, 4));
				nodes.add(new Node(10, 4));
				nodes.add(new Node(10, 9));
				nodes.add(new Node(8, 9));
				nodes.add(new Node(8, 7));
				nodes.add(new Node(6, 7));
				nodes.add(new Node(6, 5));
				nodes.add(new Node(3, 5));
				setPath(new FixedPath(nodes, true));
			}
		};

		ghost.setDescription("You see a ghostly figure of a man. You have no idea how he died.");
		ghost.setResistance(0);
		ghost.setEntityClass("man_000_npc");
		// he is a ghost so he is see through
		ghost.setVisibility(50);
		ghost.setPosition(3, 4);
		// he has low HP
		ghost.initHP(30);
		ghost.setBaseHP(100);
		ghost.put("no_shadow", "");
		zone.add(ghost);
	}
}
