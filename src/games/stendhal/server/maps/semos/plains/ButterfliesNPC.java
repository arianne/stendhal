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
package games.stendhal.server.maps.semos.plains;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.PassiveNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A playful puppy
 * 
 * @author AntumDeluge
 */
public class ButterfliesNPC implements ZoneConfigurator {
	
	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final PassiveNPC b1 = new PassiveNPC() {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(65, 63));
				nodes.add(new Node(65, 54));
				nodes.add(new Node(74, 54));
				nodes.add(new Node(74, 63));
				setPath(new FixedPath(nodes, true));
			}
		};
		
		b1.setPosition(65, 63);
		b1.setDescription("You see a butterfly.");
		b1.setEntityClass("animal/butterfly");
		b1.setBaseSpeed(0.2);
		b1.setResistance(0);
		zone.add(b1);
	}

}