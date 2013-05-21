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
package games.stendhal.server.maps.ados.felinashouse;

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
 * A cat
 * 
 * @author AntumDeluge
 */
public class KittensNPC implements ZoneConfigurator {
	
	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
	    
	    // Kitten walking around room
		final PassiveNPC k1 = new PassiveNPC() {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(3, 15));
				nodes.add(new Node(12, 15));
				nodes.add(new Node(12, 17));
				nodes.add(new Node(20, 17));
                nodes.add(new Node(20, 22));
                nodes.add(new Node(3, 22));
				setPath(new FixedPath(nodes, true));
			}
		
        };
        
        k1.setPosition(3, 15);
        k1.setDescription("You see a kitten exploring.");
        k1.setEntityClass("animal/kitten");
        k1.setBaseSpeed(0.2);
		zone.add(k1);
		
		// Kitten sitting in chair
        final PassiveNPC k2 = new PassiveNPC() {
            @Override
            protected void createPath() {
                setPath(null);
            }
        
        };
        
        k2.setPosition(20, 15);
        k2.setDescription("You see a kitten relaxing.");
        k2.setEntityClass("animal/kitten");
        k2.setDirection(Direction.DOWN);
        zone.add(k2);
        
        // Active kitten
        final PassiveNPC k3 = new PassiveNPC() {
            @Override
            protected void createPath() {
                final List<Node> nodes = new LinkedList<Node>();
                nodes.add(new Node(6, 19));
                nodes.add(new Node(10, 19));
                nodes.add(new Node(10, 20));
                nodes.add(new Node(7, 20));
                nodes.add(new Node(7, 21));
                nodes.add(new Node(6, 21));
                setPath(new FixedPath(nodes, true));
            }
        
        };
        
        k3.setPosition(6, 19);
        k3.setDescription("You see an energetic kitten.");
        k3.setEntityClass("animal/kitten");
        k3.setBaseSpeed(0.8);
        zone.add(k3);
	}

}