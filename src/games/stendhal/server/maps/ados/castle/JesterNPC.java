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
package games.stendhal.server.maps.ados.castle;

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

/**
 * Builds a Jester NPC to inform entrants to the castle.
 *
 * @author kymara
 */
public class JesterNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Huckle Rohn") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(8, 57));
				nodes.add(new Node(8, 45));
				nodes.add(new Node(20, 45));
				nodes.add(new Node(20, 35));
				nodes.add(new Node(10, 35));
				nodes.add(new Node(10, 10));
				nodes.add(new Node(20, 10));
				nodes.add(new Node(20, 45));
				nodes.add(new Node(8, 45));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hail!");
				addJob("I'm the court jester, I can't stop for long! It's just not in my job description to stand and chat.");
				addHelp("Shhh... I could tell you about these shady outlaws... they've taken over the castle while the King is away. I just keep quiet, me. Shhh...");
				add(ConversationStates.ATTENDING, "offer", null, ConversationStates.IDLE,
				        "Nothing for me! Must keep juggling! Goodbye!", null);
				add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
				        ConversationStates.IDLE,
				        "Nothing for me! Must keep juggling! Goodbye!", null);
 				addGoodbye("Bye!");
			}
		};
		npc.setDescription("You see Huckle Rohn, the court jester.");
		npc.setEntityClass("magic_jesternpc");
		npc.setPosition(8, 57);
		npc.initHP(100);
		zone.add(npc);
	}
}
