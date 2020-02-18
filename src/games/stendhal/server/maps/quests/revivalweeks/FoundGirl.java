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
package games.stendhal.server.maps.quests.revivalweeks;

import static games.stendhal.server.maps.ados.rosshouse.LittleGirlNPC.SUSI_OUTFIT;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.Direction;
import games.stendhal.common.constants.SkinColor;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SetQuestToYearAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestSmallerThanCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.TriggerExactlyInListCondition;
import games.stendhal.server.maps.ados.rosshouse.LittleGirlNPC;

public class FoundGirl implements LoadableContent {
	private SpeakerNPC npc;
	private ChatCondition noFriends;
	private ChatCondition anyFriends;
	private ChatCondition oldFriends;
	private ChatCondition currentFriends;

	private void buildConditions() {
		noFriends = new QuestNotStartedCondition("susi");
		anyFriends = new QuestStartedCondition("susi");
		oldFriends = new OrCondition(
				new QuestInStateCondition("susi", "friends"),
				new QuestSmallerThanCondition("susi", Calendar.getInstance().get(Calendar.YEAR)));
		currentFriends = new QuestInStateCondition("susi", Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
	}

	final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("int_semos_frank_house");

	private void createGirlNPC() {

		npc = new SpeakerNPC("Susi") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(4, 17));
				nodes.add(new Node(4, 27));
				nodes.add(new Node(7, 27));
				nodes.add(new Node(7, 17));
				setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				// done outside
			}
		};

		//	npcs.add(npc);
		npc.setOutfit(SUSI_OUTFIT);
		npc.setOutfitColor("skin", SkinColor.LIGHT);
		npc.setPosition(4, 17);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		npc.setSpeed(1.0);
		zone.add(npc);
	}

	private void addDialog() {

		// greeting
		addGreetingDependingOnQuestState();

		npc.addJob("I am just a litte girl waiting for my father to take me out of the house. We will have lots of fun here at the #Mine #Town #Revival #Weeks!");
		npc.addGoodbye("Have fun!");
		npc.addHelp("Just have fun.");
		npc.addOffer("I can offer you my #friendship.");

		// Revival Weeks
		npc.add(
			ConversationStates.ATTENDING,
			Arrays.asList("Mine", "Town", "Revival", "Weeks", "Mine Town",
					"Mine Town Revival", "Mine Town Revival Weeks", "Mine Town", "Revival Weeks"),
			ConversationStates.ATTENDING,
			"During the Revival Weeks we #celebrate the old and now mostly dead Mine Town north of Semos City. "
			+ "The party was cancelled a few years ago because the people of Ados were searching for me after I got lost. "
			+ "Now that I am found we can party again!",
			null);
		npc.add(
			ConversationStates.ATTENDING,
			Arrays.asList("celebrate", "celebration", "party"),
			ConversationStates.ATTENDING,
			"You can get a mask from Fidorea just outside this house or you can try to solve a difficult puzzle in the other house. Or just play a game of Tic Tac Toe against your #friends or ask Maltos about a nice game.",
			null);

		// friends
		npc.add(
			ConversationStates.ATTENDING, Arrays.asList("friend", "friends", "friendship"),
			new QuestInStateCondition("susi", Integer.toString(Calendar.getInstance().get(Calendar.YEAR))),
			ConversationStates.ATTENDING,
			"Thanks for being a friend.", null);

		addFirstQuest();
		addSecondQuest();

		// quest
		addQuest();
	}


	private void addGreetingDependingOnQuestState() {
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						noFriends),
				ConversationStates.ATTENDING,
				"Guess what, we are having another #Town #Revival #Weeks.", null);

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						anyFriends),
				ConversationStates.ATTENDING,
				null, new SayTextAction("Hello [name], nice to meet you again. "
						+ "Guess what, we are having another #Town #Revival #Weeks."));
		// TODO: Tell old friends about renewal
	}


	private void addQuest() {
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				noFriends,
				ConversationStates.ATTENDING, "I need a #friend.", null);
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				oldFriends,
				ConversationStates.ATTENDING, "We should renew our #friendship.", null);
		npc.add(
				ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				currentFriends,
				ConversationStates.ATTENDING,
				"I have made a lot of friends during the #Town #Revival #Weeks.",
				null);
	}

	private void addFirstQuest() {
		// initial friends quest
		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("friend", "friends", "friendship"),
			noFriends,
			ConversationStates.INFORMATION_1,
			"Please repeat:\r\n                        \"A circle is round,\"",
			null);
		npc.add(ConversationStates.INFORMATION_1,
			"",
			new TriggerExactlyInListCondition("A circle is round,", "A circle is round"),
			ConversationStates.INFORMATION_2, "\"it has no end.\"",
			null);
		npc.add(ConversationStates.INFORMATION_2,
			"",
			new TriggerExactlyInListCondition("it has no end.", "it has no end"),
			ConversationStates.INFORMATION_3,
			"\"That's how long,\"", null);
		npc.add(ConversationStates.INFORMATION_3,
			"",
			new TriggerExactlyInListCondition(
				"That's how long,", "That's how long",
				"Thats how long,", "Thats how long"),
			ConversationStates.INFORMATION_4,
			"\"I will be your friend.\"", null);

		ChatAction reward = new MultipleActions(new IncreaseKarmaAction(10), new IncreaseXPAction(25), new SetQuestToYearAction("susi"));
		npc.add(ConversationStates.INFORMATION_4,
			"",
			new TriggerExactlyInListCondition("I will be your friend.", "I will be your friend"),
			ConversationStates.ATTENDING,
			"Yay! We are friends now.",
			reward);
	}

	private void addSecondQuest() {
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("friend", "friends", "friendship"),
				oldFriends,
				ConversationStates.INFORMATION_5,
				"Please repeat:\r\n                        \"Make new friends,\"",
				null);
		npc.add(ConversationStates.INFORMATION_5,
				"",
				new TriggerExactlyInListCondition("Make new friends,", "Make new friends"),
				ConversationStates.INFORMATION_6, "\"but keep the old.\"",
				null);
		npc.add(ConversationStates.INFORMATION_6, "",
				new TriggerExactlyInListCondition("but keep the old.", "but keep the old"),
				ConversationStates.INFORMATION_7, "\"One is silver,\"",
				null);
		npc.add(ConversationStates.INFORMATION_7, "",
				new TriggerExactlyInListCondition("One is silver,", "One is silver"),
				ConversationStates.INFORMATION_8, "\"And the other gold.\"",
				null);

		// lowercase "and" is ignored, even in full match mode
		ChatAction reward = new MultipleActions(new IncreaseKarmaAction(15), new IncreaseXPAction(50), new SetQuestToYearAction("susi"));
		npc.add(ConversationStates.INFORMATION_8, "",
				new TriggerExactlyInListCondition("And the other gold.", "And the other gold", "the other gold.", "the other gold"),
				ConversationStates.ATTENDING,
				"Yay! We are even better friends now.",
				reward);
	}

	/**
	 * removes an NPC from the world and NPC list
	 *
	 * @param name name of NPC
	 */
	private void removeNPC(String name) {
		SpeakerNPC npc = NPCList.get().get(name);
		if (npc == null) {
			return;
		}
		npc.getZone().remove(npc);
	}


	/**
	 * removes Susi from her home in Ados and adds her to the Mine Towns.
	 */
	@Override
	public void addToWorld() {
		removeNPC("Susi");

		buildConditions();
		createGirlNPC();
		addDialog();
	}


	/**
	 * removes Susi from the Mine Town and places her back into her home in Ados.
	 *
	 * @return <code>true</code>, if the content was removed, <code>false</code> otherwise
	 */
	@Override
	public boolean removeFromWorld() {
		removeNPC("Susi");

		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("int_ados_ross_house");
		new LittleGirlNPC().createGirlNPC(zone);

		return true;
	}
}
