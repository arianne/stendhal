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
package games.stendhal.server.maps.semos.house;


import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.TeleportAction;
import games.stendhal.server.entity.npc.behaviour.adder.HealerAdder;
import games.stendhal.server.entity.npc.condition.AdminCondition;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A young lady (original name: Skye) who is lovely to admins.
 */
public class AdminHelpNPC implements ZoneConfigurator {

	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Skye") {

			@Override
			public void createDialog() {
				addGreeting("Hello! You're looking particularly good today. In fact, you look great every day!");
				addJob("I'm here to make you feel happy. And you can come here easily if you #/teleportto me. Also, I can explain the #portals here.");
				addHelp("I can #heal you if you like. Or I can just say #nice #things. If you need to know about the #portals, just ask.");
				addOffer("I can send you to a #playground to play in!");
				addReply("nice", "Did you know how many players think you're lovely for helping? Well I can tell you, loads of them do.");
				addReply("things", "So you're one of the people who tests all the #blue #words, aren't you? Now wonder you have responsibility!");
				addReply("blue", "Aw, don't be sad :( Put some nice music on, perhaps ... ");
				addReply("words", "Roses are red, violets are blue, Stendhal is great, and so are you!");
				addReply("portals", "The one with the Sun goes to semos city. It shows you where this house really is. The rest are clear, I hope. There is a door to the bank, the jail, and the Death Match in Ados. Of course they are all one way portals so you will not be disturbed by unexpected visitors.");
				addQuest("Now you're really testing how much thought went into making me!");
				add(ConversationStates.ATTENDING,
						"playground",
						new AdminCondition(500),
						ConversationStates.IDLE,
						"Have fun!",
						new TeleportAction("int_admin_playground", 20, 20, Direction.DOWN));
				addGoodbye("Bye, remember to take care of yourself.");
			}

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(6, 11));
				nodes.add(new Node(12,11));
				setPath(new FixedPath(nodes, true));
			}
			
		};
		new HealerAdder().addHealer(npc, 0);
		npc.setPosition(6, 11);
		npc.setEntityClass("beautifulgirlnpc");
		zone.add(npc);
	}
	
}