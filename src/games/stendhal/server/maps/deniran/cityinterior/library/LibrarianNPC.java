/***************************************************************************
 *                      (C) Copyright 2019 - Stendhal                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.cityinterior.library;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class LibrarianNPC implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Julia") {
			@Override
			public void createDialog() {
				addGreeting("Hello, and welcome to Deniran Library!");
				addJob("I am the local librarian.");
				addOffer("I just got a shipment of new books and don't have anytime right now to help you.");
				addGoodbye("Please return to enjoy a good book.");
			}

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(17, 14));
				nodes.add(new Node(17, 12));
				nodes.add(new Node(23, 12));
				nodes.add(new Node(23, 14));
				nodes.add(new Node(26, 14));
				nodes.add(new Node(26, 13));
				nodes.add(new Node(26, 13));
				nodes.add(new Node(26, 13));
				nodes.add(new Node(26, 14));
				nodes.add(new Node(31, 14));
				nodes.add(new Node(31, 13));
				nodes.add(new Node(31, 13));
				nodes.add(new Node(31, 13));
				nodes.add(new Node(31, 19));
				nodes.add(new Node(29, 19));
				nodes.add(new Node(29, 18));
				nodes.add(new Node(29, 18));
				nodes.add(new Node(29, 18));
				nodes.add(new Node(29, 19));
				nodes.add(new Node(25, 19));
				nodes.add(new Node(25, 18));
				nodes.add(new Node(25, 18));
				nodes.add(new Node(25, 18));
				nodes.add(new Node(25, 19));
				nodes.add(new Node(22, 19));
				nodes.add(new Node(22, 12));
				nodes.add(new Node(17, 12));
				nodes.add(new Node(17, 12));
				nodes.add(new Node(17, 14));
				nodes.add(new Node(17, 14));
				setPath(new FixedPath(nodes, true));
			}



		};
		npc.setPosition(17, 14);
		npc.setEntityClass("librarian2npc");
		npc.setDescription("You see the Julia, Deniran's local librarian.");
		npc.setDirection(Direction.DOWN);
		zone.add(npc);
	}

}
