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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SilentNPC;

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
		final SilentNPC k1 = new Kitten();

		List<Node> nodes = new LinkedList<Node>();
		nodes.add(new Node(3, 15));
		nodes.add(new Node(12, 15));
		nodes.add(new Node(12, 17));
		nodes.add(new Node(20, 17));
        nodes.add(new Node(20, 22));
        nodes.add(new Node(3, 22));
		k1.setPath(new FixedPath(nodes, true));

        k1.setPosition(3, 15);
        k1.setDescription("You see a kitten exploring.");
        k1.setEntityClass("animal/kitten");
        k1.setBaseSpeed(0.2);
        k1.setSounds(Arrays.asList("kitten-meow-1", "kitten-meow-2", "kitten-meow-3"));
		zone.add(k1);

		// Kitten sitting in chair
        final SilentNPC k2 = new Kitten();

        k2.setPosition(20, 15);
        k2.setDescription("You see a kitten relaxing.");
        k2.setEntityClass("animal/kitten");
        k2.setDirection(Direction.DOWN);
        k2.setSounds(Arrays.asList("kitten-purr-1", "kitten-mew-1"));
        zone.add(k2);

        // Active kitten
        final SilentNPC k3 = new Kitten();

        nodes = new LinkedList<Node>();
        nodes.add(new Node(6, 19));
        nodes.add(new Node(10, 19));
        nodes.add(new Node(10, 20));
        nodes.add(new Node(7, 20));
        nodes.add(new Node(7, 21));
        nodes.add(new Node(6, 21));
        k3.setPath(new FixedPath(nodes, true));

        k3.setPosition(6, 19);
        k3.setDescription("You see an energetic kitten.");
        k3.setEntityClass("animal/kitten");
        k3.setBaseSpeed(0.8);
        k3.setSounds(Arrays.asList("kitten-meow-1", "kitten-meow-2", "kitten-meow-3"));
        zone.add(k3);
	}

	/**
	 * Kitten NPCs with a helpful message when a player tries to adopt them.
	 */
	private static final class Kitten extends SilentNPC implements UseListener {
		private Kitten() {
			setMenu("Own|Use");
		}

		@Override
		public boolean onUsed(RPEntity user) {
			user.sendPrivateText("This kitten is still too young and Felina will not sell her.");
			return false;
		}
	}
}
