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

import static games.stendhal.common.constants.Actions.GHOSTMODE;
import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.GameEvent;
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
public class TestNPC implements ZoneConfigurator {
	
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final PassiveNPC test = new PassiveNPC() {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(20, 50));
				nodes.add(new Node(20, 45));
				setPath(new FixedPath(nodes, true));
			}
		};
		
		//test.setRandomPathFrom(5, 50, 20);
		//test.setMovementRange(3);
		test.setPosition(20, 50);
		test.setDescription("You see kind Carmen. She looks like someone you could ask for help.");
		test.setEntityClass("welcomernpc");
		test.setResistance(0);
		//test.setIgnoreCollision(true);
		test.setDirection(Direction.UP);
		zone.add(test);
	}

}