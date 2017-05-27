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
package games.stendhal.server.maps.fado.church;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class VergerNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Lukas") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(22, 9));
				nodes.add(new Node(16, 9));
				nodes.add(new Node(16, 4));
				nodes.add(new Node(19, 4));
				nodes.add(new Node(19, 3));
				nodes.add(new Node(22, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome to this place of worship. Are you here to be #married?");
				addJob("I am the church verger. I help with small menial tasks, but I do not mind, as my reward will come in the life beyond.");
				addHelp("My only advice is to love and be kind to one another");
				addQuest("I have eveything I need. But it does bring me pleasure to see people #married.");
				addReply("married", "If you want to be engaged, speak to Sister Benedicta. She'll make sure the priest knows about your plans.");
				addReply(ConversationPhrases.YES_MESSAGES, "Congratulations!");
				addReply(ConversationPhrases.NO_MESSAGES, "A pity. I do hope you find a partner one day.");
				addGoodbye("Goodbye, go safely.");
			}
		};

		npc.setDescription("You see Lukas, the humble church verger.");
		npc.setEntityClass("vergernpc");
		npc.setPosition(22, 9);
		npc.initHP(100);
		zone.add(npc);
	}
}
