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
 * Creates a boy NPC to help populate Ados
 *
 */
public class HolidayingBoyNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Finn Farmer") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(114, 77));
				nodes.add(new Node(98, 77));
				nodes.add(new Node(98, 68));
				nodes.add(new Node(116, 68));
				nodes.add(new Node(116, 77));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hey.");
				addHelp("I visited a tavern in the north with my parents yesterday. The apples were really tasty...");
				addOffer("Ooooh, have you seen the lovely cats from Felina? I hope my parents " +
						"will buy one for me. Would be a great holiday gift :-)");
				addJob("Hey!! I am a little boy!");
				addGoodbye("Good bye.");
				}
		};

		npc.setEntityClass("boynpc");
		npc.setPosition(114, 77);
		npc.initHP(100);
		npc.setDescription("You see Finn Farmer. He is a cute looking boy who has fun while playing in the backyard.");
		zone.add(npc);
	}
}
