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
package games.stendhal.server.maps.semos.city;

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
public class PuppyNPC implements ZoneConfigurator {
	
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final PassiveNPC dog = new PassiveNPC() {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(23, 54));
				nodes.add(new Node(17, 54));
				nodes.add(new Node(17, 49));
				nodes.add(new Node(19, 49));
				nodes.add(new Node(19, 58));
				nodes.add(new Node(23, 58));
				setPath(new FixedPath(nodes, true));
			}
		};
		
		dog.setPosition(23, 54);
		dog.setDescription("You see a playful puppy.");
		dog.setEntityClass("dog_small");
		dog.setBaseSpeed(0.5);
		zone.add(dog);
	}

}