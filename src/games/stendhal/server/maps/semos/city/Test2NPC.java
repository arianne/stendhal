/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.city;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.PassiveNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A young lady (original name: Carmen) who heals players without charge. 
 */
public class Test2NPC implements ZoneConfigurator {
	
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final PassiveNPC test = new PassiveNPC() {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(5, 51));
				nodes.add(new Node(18, 51));
				setPath(new FixedPath(nodes, true));
			}
		};
		
		//test.setRandomPathFrom(5, 50, 20);
		//test.setMovementRange(3);
		test.setPosition(5, 51);
		test.setDescription("You see kind Carmen. She looks like someone you could ask for help.");
		test.setEntityClass("welcomernpc");
		zone.add(test);
	}

}