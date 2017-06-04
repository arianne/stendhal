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
package games.stendhal.server.maps.ados.wall;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Creates a man NPC to help populate Ados
 *
 */
public class HolidayingManNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Martin Farmer") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(70, 52));
				nodes.add(new Node(75, 52));
				nodes.add(new Node(75, 55));
				nodes.add(new Node(70, 55));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi hi.");
				addHelp("The mayor of this town is really nice. I visited him with my wife to get some help.");
				addOffer("What? I'm on holidays!");
				addQuest("Ehm... I don't need help at the moment, but thanks.");
				addJob("No no, I'm on holiday here with my wife Alice.");
				addGoodbye("See you, and take care because of the lions beyond the wall.");

				}
		};

		npc.setEntityClass("man_008_npc");
		npc.setPosition(70, 52);
		npc.initHP(100);
		npc.setDescription("You see Martin Farmer. He is on holidays with his wife Alice.");
		zone.add(npc);
	}
}
