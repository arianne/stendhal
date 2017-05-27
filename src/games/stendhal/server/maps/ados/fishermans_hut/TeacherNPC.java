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
package games.stendhal.server.maps.ados.fishermans_hut;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Ados Fisherman's (Inside / Level 0).
 *
 * @author dine
 */
public class TeacherNPC implements ZoneConfigurator {

	private static final int TIME_OUT = 60;

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildTeacher(zone);
	}

	private void buildTeacher(final StendhalRPZone zone) {
		final SpeakerNPC fisherman = new SpeakerNPC("Santiago") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				// from left
				nodes.add(new Node(3, 3));
				// to right
				nodes.add(new Node(12, 3));
				// to left
				nodes.add(new Node(3, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello greenhorn!");
				addJob("I'm a teacher for fishermen. People come to me to take their #exams.");
				addHelp("If you explore Faiumoni you will find several excellent fishing spots.");
				addReply("oil", "You've got the wrong fisherman. Go ask Pequod about oil, he's in the next hut.");
			}
		};
		fisherman.setPlayerChatTimeout(TIME_OUT);
		fisherman.setEntityClass("fishermannpc");
		fisherman.setDirection(Direction.DOWN);
		fisherman.setPosition(3, 3);
		fisherman.initHP(100);
		fisherman.setDescription("You see Santiago. All fishermen take trust in his experiences.");
		zone.add(fisherman);
	}
}
