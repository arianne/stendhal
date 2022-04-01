/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.blacksmith;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;

/**
 * The blacksmith's young assistant (original name: Hackim Easso).
 * He smuggles out weapons.
 *
 * @see games.stendhal.server.maps.quests.MeetHackim
 */
public class BlacksmithAssistantNPC implements ZoneConfigurator  {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Hackim Easso") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
                nodes.add(new Node(5,2));
                nodes.add(new Node(5,5));
                nodes.add(new Node(10,5));
                nodes.add(new Node(10,9));
                nodes.add(new Node(7,9));
                nodes.add(new Node(7,12));
                nodes.add(new Node(3,12));
                nodes.add(new Node(3,8));
                nodes.add(new Node(9,8));
                nodes.add(new Node(9,5));
                nodes.add(new Node(12,5));
                nodes.add(new Node(12,2));
                nodes.add(new Node(15,2));
                nodes.add(new Node(15,5));
                nodes.add(new Node(5,5));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {

				// A little trick to make NPC remember if it has met
		        // player before and react accordingly
		        // NPC_name quest doesn't exist anywhere else neither is
		        // used for any other purpose
				add(ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new AndCondition(new GreetingMatchesNameCondition(getName()),
								new QuestNotStartedCondition("meet_hackim")),
				        ConversationStates.ATTENDING,
				        "Hi stranger, I'm Hackim Easso, the blacksmith's assistant. Have you come here to buy weapons?",
				        new SetQuestAction("meet_hackim","start"));

				addGreeting(null, new SayTextAction("Hi again, [name]. How can I #help you this time?"));

				addHelp("I'm the blacksmith's assistant. Tell me... Have you come here to buy weapons?");
				addJob("I help Xoderos the blacksmith to make weapons for Deniran's army. I mostly only bring the coal for the fire and put the weapons up on the shelves. Sometimes, when Xoderos isn't looking, I like to use one of the swords to pretend I'm a famous adventurer!");
				addOffer("You might ask Xoderos. He sells some of his selfmade weapons.");
				addGoodbye();
			}

		};
		npc.setPosition(5, 2);
		npc.setCollisionAction(CollisionAction.REVERSE); // prevent trapping players
		npc.setEntityClass("naughtyteennpc");
		npc.setDescription("You see Hackim Easso, the blacksmiths assistant.");
		zone.add(npc);
	}
}
