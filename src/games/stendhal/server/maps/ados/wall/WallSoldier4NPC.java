/***************************************************************************
 *                     (C) Copyright 2017 - Stendhal                       *
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
 * Soldiers on the wall
 *
 * @author snowflake
 * @author hendrik
 */
public class WallSoldier4NPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildAdosWallSoldier(zone);
	}

	/**
	 * Creatures a soldier on the city wall
	 *
	 * @param zone StendhalRPZone
	 */
	private void buildAdosWallSoldier(final StendhalRPZone zone) {

		final SpeakerNPC npc = new SpeakerNPC("Xinderus") {

			@Override
			protected void createPath() {
				final List<Node> path = new LinkedList<Node>();
				path.add(new Node(76, 63));
				path.add(new Node(76, 80));
				path.add(new Node(79, 80));
				path.add(new Node(79, 63));
				setPath(new FixedPath(path, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello citizen!");
				addJob("I'm sworn to protect these walls with my own life if needed.");
				addHelp("Ask Julius, he guards the main entrance of the city. He will help you.");
				addQuest("I don't have any quests for you.");
				addGoodbye("Move along, citizen.");
			}
		};

		npc.setEntityClass("youngsoldiernpc");
		npc.setPosition(76, 63);
		npc.initHP(100);
		npc.setDescription("You see Xinderus, a soldier who guards the city wall of Ados.");
		zone.add(npc);
	}
}
