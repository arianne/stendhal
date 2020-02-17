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
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Suntan Cream for Zara
 * <p>
 * PARTICIPANTS:
 * <li> Zara, a woman at the Athos beach
 * <li> David or Pam, the lifeguards.
 * <p>
 * STEPS:
 * <li> Zara asks you to bring her some suntan cream from the lifeguards.
 * <li> Pam or David want to have some ingredients. After you brought it to them
 * they mix a cream.
 * <li> Zara sees your suntan cream and asks for it and then thanks you.
 * <p>
 * REWARD:
 * <li> 1000 XP
 * <li> some karma (15)
 * <li> The key for a house in Ados where a personal chest with new slots is
 * inside
 * <p>
 * REPETITIONS: - None.
 */
public class SuntanCreamForZara extends AbstractQuest {

	private static final String QUEST_SLOT = "suntan_cream_zara";

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
		res.add("I have met Zara on Athor Island.");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("I do not want to help Zara. She can burn.");
		}
		if (questState.equals("start") ||  questState.equals("done")) {
			res.add("I want to help Zara soothe her skin. I need to get suntan cream from the lifeguards.");
		}
		if (player.isEquipped("suntan cream") && questState.equals("start")
				|| questState.equals("done")) {
			res.add("I got the suntan cream.");
		}
		if (questState.equals("done")) {
			res.add("I took the suntan cream to Zara and she let me have a key to her house in Ados City North. She says it is the one at the far end of the lower row.");
		}
		return res;
	}

	private void createRequestingStep() {
		final SpeakerNPC zara = npcs.get("Zara");

		zara.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"I don't have a new task for you. But thank you for the suntan cream. I feel my skin is getting better already!",
			null);

		zara.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "rejected"),
				ConversationStates.QUEST_OFFERED,
				"You refused to help me last time and my skin is getting worse. "
				+ "Please can you bring me the magic #'suntan cream' that the #lifeguards produce?",
				null);

		zara.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Did you forget that you promised me to ask the #lifeguards for #'suntan cream'?",
				null);

		zara.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestNotStartedCondition(QUEST_SLOT), new QuestNotInStateCondition(QUEST_SLOT, "rejected")),
				ConversationStates.QUEST_OFFERED,
				"I fell asleep in the sun and now my skin is burnt. Can you bring me the magic #'suntan cream' that the #lifeguards produce?",
				null);

		zara.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Thank you very much. I'll be waiting here for your return!",
			new SetQuestAction(QUEST_SLOT, "start"));

		zara.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Ok, but I would have had a nice reward for you...",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		zara.add(
			ConversationStates.QUEST_OFFERED,
			Arrays.asList("suntan cream", "suntan", "cream"),
			null,
			ConversationStates.QUEST_OFFERED,
			"The #lifeguards make a great cream to protect from the sun and to heal sunburns at the same time. Now, will you get it for me?",
			null);

		zara.add(
			ConversationStates.QUEST_OFFERED,
			"lifeguard",
			null,
			ConversationStates.QUEST_OFFERED,
			"The lifeguards are called Pam and David. I think they are in the dressing rooms. So, will you ask them for me?",
			null);

		zara.addReply(
			Arrays.asList("suntan cream", "suntan", "cream"),
			"The #lifeguards make a great cream to protect from the sun and to heal sunburns at the same time.");

		zara.addReply(
			"lifeguard",
			"The lifeguards are called Pam and David. I think they are in the dressing rooms.");

	}

	private void createBringingStep() {
		final SpeakerNPC zara = npcs.get("Zara");

		zara.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(zara.getName()),
					new QuestInStateCondition(QUEST_SLOT, "start"),
					new PlayerHasItemWithHimCondition("suntan cream")),
			ConversationStates.QUEST_ITEM_BROUGHT,
			"Great! You got the suntan cream! Is it for me?",
			null);

		zara.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(zara.getName()),
						new QuestInStateCondition(QUEST_SLOT, "start"),
						new NotCondition(new PlayerHasItemWithHimCondition("suntan cream"))),
				ConversationStates.ATTENDING,
				"I know that the #'suntan cream' is hard to get, but I hope that you didn't forget my painful problem...",
				null);

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("suntan cream"));
		reward.add(new EquipItemAction("small key", 1, true));
		reward.add(new IncreaseXPAction(1000));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));
		reward.add(new IncreaseKarmaAction(15));

		zara.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES,
			// make sure the player isn't cheating by putting the
			// cream away and then saying "yes"
			new PlayerHasItemWithHimCondition("suntan cream"),
			ConversationStates.ATTENDING,
			"Thank you! I feel much better immediately! Here, take this key to my row house in Ados. Feel at home as long as I'm still here!",
			new MultipleActions(reward));

		zara.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"No? Look at me! I cannot believe that you're so selfish!",
			null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Suntan Cream for Zara",
				"Zara is burning under the hot Athor sun.",
				false);
		createRequestingStep();
		createBringingStep();
	}

	@Override
	public String getName() {
		return "SuntanCreamForZara";
	}

	@Override
	public int getMinLevel() {
		return 50;
	}

	@Override
	public String getNPCName() {
		return "Zara";
	}

	@Override
	public String getRegion() {
		return Region.ATHOR_ISLAND;
	}
}
