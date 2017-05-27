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
package games.stendhal.server.maps.kirdneh.inn;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

/**
 * Builds the barman in kirdneh.
 *
 * @author kymara
 */
public class BarmanNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC barmanNPC = new SpeakerNPC("Ruarhi") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(15, 4));
				nodes.add(new Node(15, 7));
				nodes.add(new Node(4, 7));
				nodes.add(new Node(4, 20));
				nodes.add(new Node(4, 7));
				nodes.add(new Node(11, 7));
				nodes.add(new Node(11, 11));
				nodes.add(new Node(14, 11));
				nodes.add(new Node(14, 21));
				nodes.add(new Node(14, 8));
				nodes.add(new Node(15, 8));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi there!");
				addJob("I am the barman. If I can #offer you a drink, just say.");
				addHelp("Ssh, can you come close so I can whisper? (I know Katerina there looks a wreck .. but she's actually a summon healer .. and cheap too.)");
				final Map<String, Integer> offerings = new HashMap<String, Integer>();
				offerings.put("beer", 10);
				offerings.put("wine", 15);
				// more expensive than in normal taverns
				offerings.put("bread", 50);
				offerings.put("cheese", 20);
				offerings.put("pie", 160);
				new SellerAdder().addSeller(this, new SellerBehaviour(offerings));
				addGoodbye("Goodbye.");
			}
		};

		barmanNPC.setEntityClass("barman2npc");
		barmanNPC.setPosition(15, 4);
		barmanNPC.initHP(100);
		barmanNPC.setDescription("You see Ruarhi. He looks like a real bar-owner.");
		zone.add(barmanNPC);
	}
}
