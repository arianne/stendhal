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
package games.stendhal.server.maps.nalwor.assassinhq;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;

/**
 * Builds a Teacher NPC who tries to make disciple assassins behave.
 *
 * @author kymara with modifications by tigertoes
 */
public class SchoolMarmNPC implements ZoneConfigurator {

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
		final SpeakerNPC npc = new SpeakerNPC("Miss Parillaud") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(7, 3));
				nodes.add(new Node(13, 3));
				nodes.add(new Node(13, 18));
				nodes.add(new Node(7, 18));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			    protected void createDialog() {
				add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
						new GreetingMatchesNameCondition(getName()), true,
						ConversationStates.IDLE, "What are you bothering me for. Can't you see I have my hands full! Now, lil johnnnny, I told you not to poke him in the eye!", null);
	 	     }

		};

		npc.setDescription("You see a rather harried school marm. She really has her hands full with these little assassins.");
		npc.setEntityClass("woman_014_npc");
		npc.setPosition(7, 3);
		npc.initHP(100);
		zone.add(npc);
	}
}
