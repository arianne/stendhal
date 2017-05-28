/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.ExamineChatAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.KilledForQuestCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Speak with Hayunn
 * <p>
 * PARTICIPANTS: <ul><li> Hayunn Naratha</ul>
 *
 * STEPS: <ul>
 * <li> Talk to Hayunn to activate the quest.
 * <li> He asks you to kill a rat, also offering to teach you how
 * <li> Return and get directions to Semos
 * <li> Return and learn how to click move, and get some URLs
 * </ul>
 *
 * REWARD: <ul><li> 20 XP <li> 5 gold coins <li> studded shield </ul>
 *
 * REPETITIONS: <ul><li> Get the URLs as much as wanted but you only get the reward once.</ul>
 */
public class MeetHayunn extends AbstractQuest {

	private static final String QUEST_SLOT = "meet_hayunn";

	private static final int TIME_OUT = 60;

	private static Logger logger = Logger.getLogger(MeetHayunn.class);

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		final String questState = player.getQuest(QUEST_SLOT);
		res.add("Hayunn Naratha is the first guy I ever met in this world, he challenged me to kill a rat.");
		if (player.getQuest(QUEST_SLOT, 0).equals("start") && new KilledForQuestCondition(QUEST_SLOT,1).fire(player, null, null)) {
			res.add("I killed that rat, I should go back to tell him!");
		}
		if (player.getQuest(QUEST_SLOT, 0).equals("start")) {
			return res;
		}
		res.add("I killed the rat. Hayunn will teach me more about the world now.");
		if ("killed".equals(questState)) {
			return res;
		}
		res.add("Hayunn gave me a bit of money and told me to go find Monogenes in Semos City, who will give me a map.");
		if ("taught".equals(questState)) {
			return res;
		}
		res.add("Hayunn told me lots of useful information about how to survive, and gave me a studded shield and some money.");
		if (isCompleted(player)) {
			return res;
		}
		// if things have gone wrong and the quest state didn't match any of the above, debug a bit:
		final List<String> debug = new ArrayList<String>();
		debug.add("Quest state is: " + questState);
		logger.error("History doesn't have a matching quest state for " + questState);
		return debug;
	}

	private void prepareHayunn() {

		final SpeakerNPC npc = npcs.get("Hayunn Naratha");

		// player wants to learn how to attack
		npc.add(
				ConversationStates.ATTENDING,
				ConversationPhrases.YES_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, 0, "start"),
				ConversationStates.ATTENDING,
				"Well, back when I was a young adventurer, I clicked on my enemies to attack them. I'm sure that will work for you, too. Good luck, and come back once you are done.",
				null);

		//player doesn't want to learn how to attack
		npc.add(
				ConversationStates.ATTENDING,
				ConversationPhrases.NO_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, 0, "start"),
				ConversationStates.ATTENDING,
				"Fine, you seem like an intelligent type. I'm sure you'll work it out!",
				null);

		//player returns to Hayunn not having killed a rat
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, 0, "start"),
						new NotCondition(new KilledForQuestCondition(QUEST_SLOT,1))),
				ConversationStates.ATTENDING,
				"I see you haven't managed to kill a rat yet. Do you need me to tell you how to fight them?",
				null);

		//player returns to Hayunn having killed a rat
		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new IncreaseXPAction(10));
		actions.add(new SetQuestAction(QUEST_SLOT, "killed"));

		npc.add(
				ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, 0, "start"),
						new KilledForQuestCondition(QUEST_SLOT, 1)),
				ConversationStates.INFORMATION_1,
				"You killed the rat! Now, I guess you want to explore. Do you want to know the way to Semos?",
				new MultipleActions(actions));


	   	// The player has had enough info for now. Send them to semos. When they come back they can learn some more tips.

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new EquipItemAction("money", 5));
		reward.add(new IncreaseXPAction(10));
		reward.add(new SetQuestAction(QUEST_SLOT, "taught"));
		reward.add(new ExamineChatAction("monogenes.png", "Monogenes", "North part of Semos city."));

		npc.add(
			ConversationStates.INFORMATION_1,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.IDLE,
			"Follow the path through this village to the east, and you can't miss Semos. If you go and speak to Monogenes, the old man in this picture, he will give you a map. Here's 5 money to get you started. Bye bye!",
			new MultipleActions(reward));

	   	// incase player didn't finish learning everything when he came after killing the rat, he must have another chance. Here it is.
		// 'little tip' is a pun as he gives some money, that is a tip, too.
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "killed")),
				ConversationStates.INFORMATION_1,
		        "You ran off pretty fast after coming to tell me you killed that rat! I was about to give you a little tip. Do you want it?",
				null);

		// Player has returned to say hi again.
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "taught")),
				ConversationStates.INFORMATION_2,
		        "Hello again. Have you come to learn more from me?",
				null);

		npc.add(
			ConversationStates.INFORMATION_2,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_3,
			"Perhaps you have found Semos dungeons by now. The corridors are pretty narrow down there, so there's a trick to moving quickly and accurately, if you'd like to hear it. #Yes?",
			null);

		npc.add(
			ConversationStates.INFORMATION_3,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.INFORMATION_4,
			"Simple, really; just click the place you want to move to. There's a lot more information than I can relate just off the top of my head... do you want to know where to read more?",
			null);

		final String epilog = "You can find answers to frequently asked questions by typing #/faq \nYou can read about some of the currently most powerful and successful warriors at #https://stendhalgame.org\n ";

			//This is used if the player returns, asks for #help and then say #yes
			npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.YES_MESSAGES, new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			epilog + "You know, you remind me of my younger self...",
			null);

		final List<ChatAction> reward2 = new LinkedList<ChatAction>();
		reward2.add(new EquipItemAction("studded shield"));
		reward2.add(new IncreaseXPAction(20));
		reward2.add(new SetQuestAction(QUEST_SLOT, "done"));

		npc.add(ConversationStates.INFORMATION_4,
				ConversationPhrases.YES_MESSAGES, new QuestNotCompletedCondition(QUEST_SLOT),
				ConversationStates.IDLE,
				epilog + "Well, good luck in the dungeons! This shield should help you. Here's hoping you find fame and glory, and keep watch for monsters!",
				new MultipleActions(reward2));

		npc.add(new ConversationStates[] { ConversationStates.ATTENDING,
					ConversationStates.INFORMATION_1,
					ConversationStates.INFORMATION_2,
					ConversationStates.INFORMATION_3,
					ConversationStates.INFORMATION_4},
				ConversationPhrases.NO_MESSAGES, new NotCondition(new QuestInStateCondition(QUEST_SLOT, "start")), ConversationStates.IDLE,
				"Oh well, I'm sure someone else will stop by for a chat soon. Bye...",
				null);

		npc.setPlayerChatTimeout(TIME_OUT);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Meet Hayunn Naratha",
				"Hayunn Naratha can teach young heroes important basics of the Stendhal world.",
				false);
		prepareHayunn();
	}

	@Override
	public String getName() {
		return "MeetHayunn";
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Hayunn Naratha";
	}
}
