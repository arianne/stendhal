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
package games.stendhal.server.maps.ados.city;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Creates a woman NPC to help populate Ados
 *
 */
public class HolidayingWomanNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Alice Farmer") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(47, 90));
				nodes.add(new Node(3, 90));
				nodes.add(new Node(3, 64));
				nodes.add(new Node(40, 64));
				nodes.add(new Node(40, 75));
				nodes.add(new Node(47, 75));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello.");
				addHelp("I walked around a bit and saw a nice looking tavern. Did you take a look inside already? It smells fantastic there!");
				addOffer("I can only offer this nice weather today. Its really great.");
				addQuest("I have no task for you, sorry."); 
				addJob("Aaaah, I am on holiday here, only walking around.");
				addGoodbye("Bye bye.");

				}
		};

		npc.setEntityClass("woman_016_npc");
		npc.setPosition(47, 90);
		npc.initHP(100);
		npc.setDescription("You see Alice Farmer. She is on holidays in Ados.");
		zone.add(npc);
	}
}
