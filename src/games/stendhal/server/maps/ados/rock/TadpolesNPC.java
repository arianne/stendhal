/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.ados.rock;

import java.util.LinkedList;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SilentNPC;


public class TadpolesNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SilentNPC tp1 = new SilentNPC();

		tp1.setPath(new FixedPath(new LinkedList<Node>() {{
			add(new Node(78, 38));
			add(new Node(82, 38));
			add(new Node(82, 34));
			add(new Node(78, 34));
		}}, true));

		tp1.setPosition(78, 34);
		tp1.setEntityClass("animal/tadpole");
		tp1.setVisibility(50);
		tp1.setIgnoresCollision(true);
		tp1.setDescription("You see a tadpole.");
		zone.add(tp1);
	}
}
