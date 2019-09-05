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
package games.stendhal.server.maps.semos.city;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.actions.admin.AdministrationAction;
*/

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.SpeakerNPC;

/*
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.action.StoreMessageAction;
import games.stendhal.server.entity.player.Player;
*/

public class TestNPC1 implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("TestNPC1") {

			@Override
			public void createDialog() {
				addGreeting();
				addJob("My job is testnpc1");
				addHelp("Help testing...");
				addGoodbye("So long...");
				addOffer("Offer?");
			}

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(20, 45));
				nodes.add(new Node(35, 45));
				nodes.add(new Node(35, 40));
				nodes.add(new Node(30, 41));
				nodes.add(new Node(22, 41));
				setPath(new FixedPath(nodes, true));
			}

		};
		npc.setEntityClass("fatsellernpc");
		npc.setPosition(25, 45);
		npc.setCollisionAction(CollisionAction.STOP);
		
		npc.setDescription("testnpc1");
		zone.add(npc);
	}
}
