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
package games.stendhal.server.maps.ados.outside;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class CloaksCollectorNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildZooSub3Area(zone);
	}

	private void buildZooSub3Area(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Bario") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				// to stove
				nodes.add(new Node(7, 44));
				// to table
				nodes.add(new Node(7, 52));
				// around couch
				nodes.add(new Node(14, 57));
				nodes.add(new Node(22, 57));
				// into the floor
				nodes.add(new Node(18, 50));
				nodes.add(new Node(19, 42));
				// into the bathroom
				nodes.add(new Node(39, 42));
				// into the floor
				nodes.add(new Node(18, 42));
				// into the bedroom
				nodes.add(new Node(18, 29));
				// to the chest
				nodes.add(new Node(17, 24));
				// through the floor
				nodes.add(new Node(18, 34));
				nodes.add(new Node(18, 51));
				// back to the kitchen
				nodes.add(new Node(7, 51));
				nodes.add(new Node(4, 44));
				nodes.add(new Node(4, 47));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addJob("There is a quite high unemployment rate down here.");
				addHelp("I have heard rumors that an elven city lies South West of here, in a vast forest. The locals call it Nalwor.");
				addGoodbye();
				// remaining behaviour is defined in maps.quests.CloaksForBario.
			}
		};

		npc.setEntityClass("beardmannpc");
		npc.setPosition(4, 47);
		npc.initHP(100);
		npc.setDescription("You see Bario. His face is a bit blue and his knees are shaking.");
		zone.add(npc);
	}
}
