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
package games.stendhal.server.maps.kalavan.castle;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a sad scientist NPC who gives a quest to a player.
 *
 * @author kymara
 */
public class SadScientistNPC implements ZoneConfigurator {

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
		final SpeakerNPC npc = new SpeakerNPC("Vasi Elos") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(13, 113));
				nodes.add(new Node(20, 113));
				nodes.add(new Node(20, 115));
				nodes.add(new Node(24, 115));
				nodes.add(new Node(24, 113));
				nodes.add(new Node(31, 113));
				nodes.add(new Node(31, 115));
				nodes.add(new Node(32, 115));
				nodes.add(new Node(32, 119));
				nodes.add(new Node(45, 119));
				nodes.add(new Node(45, 113));
				nodes.add(new Node(36, 113));
				nodes.add(new Node(36, 110));
				nodes.add(new Node(23, 110));
				nodes.add(new Node(23, 113));
				setPath(new FixedPath(nodes, true));
			}
			@Override
		    protected void createDialog() {
				addGoodbye("Go away!");
				// remaining behaviour defined in maps.quests.SadScientist
			}
		};

		npc.setDescription("You see someone that is somewhat strange. Perhaps you shouldn't bother him?");
		npc.setEntityClass("madscientistnpc");
		npc.setPosition(13, 113);
		npc.initHP(100);
		zone.add(npc);
	}
}
