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

import games.stendhal.common.Direction;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.PlaySoundAction;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/**
 * ZoneConfigurator configuring Rudolph the Red-Nosed Reindeer who clops around Semos city during Christmas season
 */
public class RudolphNPC implements ZoneConfigurator {

	
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		final SpeakerNPC npc = new SpeakerNPC("Rudolph") {

			@Override
			protected void createPath() {
				final List<Node> path = new LinkedList<Node>();
				path.add(new Node(2, 3));
				path.add(new Node(2, 14));
				path.add(new Node(35, 14));
				path.add(new Node(35, 46));
				path.add(new Node(51, 46));
				path.add(new Node(51, 48));
				path.add(new Node(62, 48));
				path.add(new Node(62, 55));
				path.add(new Node(51, 55));
				path.add(new Node(51, 58));
				path.add(new Node(32, 58));
				path.add(new Node(32, 53));
				path.add(new Node(18, 53));
				path.add(new Node(18, 43));
				path.add(new Node(20, 43));
				path.add(new Node(20, 26));
				path.add(new Node(26, 26));
				path.add(new Node(26, 14));
				path.add(new Node(21, 14));
				path.add(new Node(21, 3));
				setPath(new FixedPath(path, true));
			}			
			
			@Override
			public void createDialog() {
				addGreeting("Hi, my jolly friend.  Isn't this such a wonderful time of year?");
				addHelp("Oh, my, I can't help you, sorry.  It's not like i can influence Santa at all.");
				addReply("safety", "When you are standing at a chest to organise your items, any other people or animals will not be able to come near you. A magical aura stops others from using scrolls to arrive near you. You will need to walk out. Lastly let me tell you about safe #trading.");
				addReply("trading", "To start a trade with another player, right-click on them and select 'Trade'. If they also want to trade with you, you'll see a window pop up where you can drag items to offer, and see what is being offered to you. Both click Offer, and then you both need to Accept the offer to complete the trade.");
				addJob("I pull Santa's sleigh on Christmas night. It gives me such pleasure to flash my nose so that Santa can see where he is going.");
				addGoodbye("It was such a pleasure to meet you.");
			
				// remaining behaviour defined in games.stendhal.server.maps.quests.GoodiesForRudolph
			
			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.DOWN);
			}
			
		};
		npc.setPosition(2, 3);
		npc.setDirection(Direction.DOWN);
		npc.setDescription("You see Rudolph the Red-Nosed Reindeer. His nose is so big, bright and flashy.");
		npc.setHP(950);
		npc.setEntityClass("rudolphnpc");
		zone.add(npc);
	}

}