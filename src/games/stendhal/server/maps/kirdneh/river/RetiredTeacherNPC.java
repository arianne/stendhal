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
package games.stendhal.server.maps.kirdneh.river;

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
 * Builds a npc in the house at Kirdneh River (name:Ortiv Milquetoast) who is a coward retired teacher
 *
 * @author Vanessa Julius
 *
 */
public class RetiredTeacherNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Ortiv Milquetoast") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(15, 28));
				nodes.add(new Node(27, 28));
                nodes.add(new Node(27, 19));
                nodes.add(new Node(26, 19));
                nodes.add(new Node(26, 16));
                nodes.add(new Node(28, 16));
                nodes.add(new Node(28, 11));
                nodes.add(new Node(24, 11));
                nodes.add(new Node(24, 20));
                nodes.add(new Node(27, 20));
                nodes.add(new Node(27, 26));
                nodes.add(new Node(14, 26));
                nodes.add(new Node(14, 25));
                nodes.add(new Node(13, 25));
                nodes.add(new Node(13, 20));
                nodes.add(new Node(14, 20));
                nodes.add(new Node(14, 14));
                nodes.add(new Node(4, 14));
                nodes.add(new Node(4, 6));
                nodes.add(new Node(10, 6));
                nodes.add(new Node(10, 3));
                nodes.add(new Node(6, 3));
                nodes.add(new Node(6, 6));
                nodes.add(new Node(4, 6));
                nodes.add(new Node(4, 22));
                nodes.add(new Node(13, 22));
                nodes.add(new Node(13, 27));
               	setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Ohh, a stranger found my hidden house, welcome!");
				addHelp("Never ever get into trouble with #assassins when they are stronger than you!");
				addReply("assassins", "They will come and get you if you don't take care! Some of them are waiting downstairs under my basement!");
				addJob("I was a teacher of alchemy once but some of my #students turned into ugly bandits and assassins...");
				addReply("students", "I don't know what happens in Faiumoni at the moment, because I just stay in my safe house the whole day long...");
				addOffer("Sorry, but I can't offer you anything. I have some major problems in my basement at the moment!");
				addQuest("I want to prepare a mixture to keep the assassins and bandits in my cellar.");
				addGoodbye("Take care of yourself and please return soon to visit me again, I'm scared alone!");
			}
		};

		npc.setDescription("You see Ortiv Milquetoast. Even though he has some kind of teacher aura around him, he seems to be quite scared and nervous.");
		npc.setEntityClass("retiredteachernpc");
		npc.setPosition(15, 28);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(100);
		zone.add(npc);
	}
}
