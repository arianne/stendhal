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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SilentNPC;

/**
 * A bunch of butterflies
 *
 * @author AntumDeluge
 */
public class ButterfliesNPC implements ZoneConfigurator {

    // Butterflies
    List<SilentNPC> butterflies = new LinkedList<SilentNPC>();

	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SilentNPC b1 = new SilentNPC() {
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
        final SilentNPC b2 = new SilentNPC() {
            @Override
            protected void createPath() {
                final List<Node> nodes = new LinkedList<Node>();
                nodes.add(new Node(10, 71));
                nodes.add(new Node(33, 71));
                nodes.add(new Node(33, 82));
                nodes.add(new Node(10, 82));
                setPath(new FixedPath(nodes, true));
            }
        };
        b2.setPosition(10, 71);
        butterflies.add(b2);

        // Butterfly 3
        final SilentNPC b3 = new SilentNPC() {
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
        final SilentNPC b4 = new SilentNPC() {
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

        // Butterfly 5
        final SilentNPC b5 = new SilentNPC() {
            @Override
            protected void createPath() {
                List<Node> nodes = new LinkedList<Node>();
                nodes.add(new Node(112, 120));
                nodes.add(new Node(116, 120));
                nodes.add(new Node(116, 123));
                nodes.add(new Node(112, 123));
                setPath(new FixedPath(nodes, true));
            }
        };
        b5.setPosition(116, 120);
        butterflies.add(b5);

        // Butterfly 6
        final SilentNPC b6 = new SilentNPC() {
            @Override
            protected void createPath() {
                List<Node> nodes = new LinkedList<Node>();
                nodes.add(new Node(24, 31));
                nodes.add(new Node(24, 53));
                nodes.add(new Node(54, 53));
                nodes.add(new Node(54, 31));
                setPath(new FixedPath(nodes, true));
            }
        };
        b6.setPosition(24, 31);
        butterflies.add(b6);

        // Butterflies with random paths
        final List<SilentNPC> randomButterflies = new LinkedList<SilentNPC>();

        final SilentNPC br1 = new SilentNPC();
        br1.setPosition(69, 55);
        randomButterflies.add(br1);

        final SilentNPC br2 = new SilentNPC();
        br2.setPosition(70, 55);
        randomButterflies.add(br2);

        final SilentNPC br3 = new SilentNPC();
        br3.setPosition(71, 55);
        randomButterflies.add(br3);

        final SilentNPC br4 = new SilentNPC();
        br4.setPosition(14, 36);
        randomButterflies.add(br4);

        final SilentNPC br5 = new SilentNPC();
        br5.setPosition(25, 17);
        randomButterflies.add(br5);

        for (SilentNPC insect : randomButterflies) {
            insect.moveRandomly();
            butterflies.add(insect);
        }

        // Add butterflies to zone
		for (SilentNPC insect : butterflies) {
    		insect.setDescription("You see a butterfly.");
    		insect.setEntityClass("animal/butterfly");
    		insect.setBaseSpeed(0.2);
    		insect.setResistance(0);
    		zone.add(insect);
		}
	}

}
