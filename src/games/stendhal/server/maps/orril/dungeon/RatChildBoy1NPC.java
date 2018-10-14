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
package games.stendhal.server.maps.orril.dungeon;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.RatKidsNPCBase;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a Rat Child NPC.
 *
 * @author Norien
 */
public class RatChildBoy1NPC implements ZoneConfigurator {


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
		final SpeakerNPC rat = new RatKidsNPCBase("Cody") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				//path goes here
				nodes.add(new Node(40, 105));
				nodes.add(new Node(40, 109));
				nodes.add(new Node(44 ,109));
				nodes.add(new Node(44, 100));
				nodes.add(new Node(34, 100));
				nodes.add(new Node(34, 104));
				nodes.add(new Node(40, 104));
				setPath(new FixedPath(nodes, true));
			}
		};

		rat.setDescription("You see a rat child.");
		rat.setEntityClass("ratchildboy1npc");
		rat.setPosition(40, 105);
		rat.initHP(100);
		zone.add(rat);
	}
}
