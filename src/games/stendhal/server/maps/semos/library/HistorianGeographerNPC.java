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
package games.stendhal.server.maps.semos.library;

import java.util.Arrays;
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
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;

public class HistorianGeographerNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildSemosLibraryArea(zone);
	}

	private void buildSemosLibraryArea(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Zynn Iwuhos") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(15, 3));
				nodes.add(new Node(12, 3));
				nodes.add(new Node(12, 6));
				nodes.add(new Node(13, 6));
				nodes.add(new Node(13, 7));
				nodes.add(new Node(13, 6));
				nodes.add(new Node(15, 6));
				nodes.add(new Node(15, 7));
				nodes.add(new Node(15, 6));
				nodes.add(new Node(17, 6));
				nodes.add(new Node(17, 7));
				nodes.add(new Node(17, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting(null, new SayTextAction("Hi again, [name]. How can I #help you this time?"));
				addGoodbye();

				// A little trick to make NPC remember if it has met
		        // player before and react accordingly
		        // NPC_name quest doesn't exist anywhere else neither is
		        // used for any other purpose
				add(ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new AndCondition(new GreetingMatchesNameCondition(getName()),
								new QuestNotCompletedCondition("Zynn")),
						ConversationStates.ATTENDING,
						"Hi, potential reader! Here you can find records of the history of Semos, and lots of interesting facts about this island of Faiumoni. If you like, I can give you a quick introduction to its #geography and #history! I also keep up with the #news, so feel free to ask me about that.",
						new SetQuestAction("Zynn", "done"));

				addHelp("I can best help you by sharing my knowledge of Faiumoni's #geography and #history, as well as the latest #news.");
				addJob("I'm a historian and geographer, committed to writing down every objective fact about Faiumoni. Did you know I wrote most of the books in this library? Well, apart from \"Know How To Kill Creatures\", of course... Hayunn Naratha wrote that.");

				addQuest("I don't think there's really anything you could do for me right now. But thanks for asking!");

				add(ConversationStates.ATTENDING, Arrays.asList("offer", "buy", "trade", "deal", "scroll", "scrolls", "home", "empty",
				        "marked", "summon", "magic", "wizard", "sorcerer"), null, ConversationStates.ATTENDING,
				        "I don't sell scrolls anymore... I had a big argument with my supplier, #Haizen.", null);

				add(
				        ConversationStates.ATTENDING,
				        Arrays.asList("haizen", "haizen."),
				        null,
				        ConversationStates.ATTENDING,
				        "Haizen? He's a wizard who lives in a small hut between Semos and Ados. I used to sell his scrolls here, but we had an argument... you'll have to go see him yourself, I'm afraid.",
				        null);
			}
		};

		npc.setEntityClass("wisemannpc");
		npc.setDescription("You see Zynn Iwuhos. He looks even older than some of his tattered maps lying around.");
		npc.setPosition(15, 3);
		npc.initHP(100);
		zone.add(npc);
	}
}
