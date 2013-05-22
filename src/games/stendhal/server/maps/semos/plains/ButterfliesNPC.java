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
	
    // Butterflies
    List<PassiveNPC> butterflies = new LinkedList<PassiveNPC>();
    
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
		butterflies.add(b1);
		
		
        // Butterfly 2
        final PassiveNPC b2 = new PassiveNPC() {
            @Override
            protected void createPath() {
                final List<Node> nodes = new LinkedList<Node>();
                nodes.add(new Node(17, 74));
                nodes.add(new Node(33, 74));
                nodes.add(new Node(33, 82));
                nodes.add(new Node(17, 82));
                setPath(new FixedPath(nodes, true));
            }
        };
        b2.setPosition(17, 74);
        butterflies.add(b2);
        
        // Butterfly 3
        final PassiveNPC b3 = new PassiveNPC() {
            @Override
            protected void createPath() {
                final List<Node> nodes = new LinkedList<Node>();
                nodes.add(new Node(20, 116));
                nodes.add(new Node(20, 125));
                nodes.add(new Node(12, 125));
                nodes.add(new Node(12, 116));
                setPath(new FixedPath(nodes, true));
            }
        };
        b3.setPosition(20, 116);
        butterflies.add(b3);
        
        // Butterfly 4
        final PassiveNPC b4 = new PassiveNPC() {
            @Override
            protected void createPath() {
                final List<Node> nodes = new LinkedList<Node>();
                nodes.add(new Node(115, 118));
                nodes.add(new Node(108, 118));
                nodes.add(new Node(108, 124));
                nodes.add(new Node(115, 124));
                setPath(new FixedPath(nodes, true));
            }
        };
        b4.setPosition(115, 118);
        butterflies.add(b4);
        
        // Add butterflies to zone
		for (PassiveNPC insect : butterflies) {
    		insect.setDescription("You see a butterfly.");
    		insect.setEntityClass("animal/butterfly");
    		insect.setBaseSpeed(0.2);
    		insect.setResistance(0);
    		zone.add(insect);
		}
	}

}