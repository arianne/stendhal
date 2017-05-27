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
package games.stendhal.server.maps.athor.ship;

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
import games.stendhal.server.maps.athor.ship.AthorFerry.Status;

/** Factory for cargo worker on Athor Ferry. */

public class CookNPC implements ZoneConfigurator  {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Laura") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
		        // to the oven
				nodes.add(new Node(27,28));
				// to the table
				nodes.add(new Node(27,31));
				// to the dining room
				nodes.add(new Node(18,31));
				// to the barrel
				nodes.add(new Node(28,31));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {
				addGreeting("Ahoy! Welcome to the galley!");
				addJob("I'm running the galley on this ship. I #offer fine foods for the passengers and alcohol for the crew.");
				addHelp("The crew mates drink beer and grog all day. But if you want some more exclusive drinks, go to the cocktail bar at Athor beach.");

				final Map<String, Integer> offerings = new HashMap<String, Integer>();
				offerings.put("beer", 10);
				offerings.put("wine", 15);
				// more expensive than in normal taverns
				offerings.put("ham", 100);
				offerings.put("pie", 150);
				new SellerAdder().addSeller(this, new SellerBehaviour(offerings));

				addGoodbye();

			}};
			new AthorFerry.FerryListener() {
				@Override
				public void onNewFerryState(final Status status) {
					switch (status) {
					case ANCHORED_AT_MAINLAND:
					case ANCHORED_AT_ISLAND:
						npc.say("Attention: We have arrived!");
						break;
					default:
						npc.say("Attention: We have set sail!");
						break;
					}
				}
			};

			npc.setPosition(27, 28);
			npc.setEntityClass("tavernbarmaidnpc");
			npc.setDescription ("Laura runs the galley on the ferry. Talk with her if you are hungry or thirsty.");
			zone.add(npc);

	}
}
