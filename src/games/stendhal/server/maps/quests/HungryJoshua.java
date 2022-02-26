/***************************************************************************
 *                   (C) Copyright 2003-2014 - Stendhal                    *
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

import com.google.common.collect.ImmutableList;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.CreateSlotAction;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EnableFeatureAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.InflictStatusOnNPCAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Hungry Joshua
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Xoderos the blacksmith in Semos</li>
 * <li> Joshua the blacksmith in Ados</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Talk with Xoderos to activate the quest.</li>
 * <li> Make 5 sandwiches.</li>
 * <li> Talk with Joshua to give him the sandwiches.</li>
 * <li> Return to Xoderos with a message from Joshua.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> 200 XP</li>
 * <li> Karma: 12 total (10 + 2)</li>
 * <li> ability to use the keyring</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> None.</li>
 * </ul>
 */
public class HungryJoshua extends AbstractQuest {
	private static final int FOOD_AMOUNT = 5;

	private static final String QUEST_SLOT = "hungry_joshua";


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
		res.add("I have asked Xoderos at Semos blacksmith if he has a quest for me.");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("I do not want to help Xoderos and Joshua.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "joshua", "done")) {
			res.add("I agreed to take 5 sandwiches to Joshua and tell him that I have his 'food'.");
		}
		if (questState.equals("start") && player.isEquipped("sandwich",
				FOOD_AMOUNT)
				|| questState.equals("done")) {
			res.add("I got five sandwiches to take Joshua.");
		}
		if (questState.equals("joshua") || questState.equals("done")) {
			res.add("I took the food to Joshua and he asked me to tell his brother Xoderos that he is ok, by saying 'Joshua'.");
		}
		if (questState.equals("done")) {
			res.add("I passed the message to Xoderos and he has fixed my keyring for me.");
		}
		return res;
	}

	private void step_1() {

		final SpeakerNPC npc = npcs.get("Xoderos");

		/** If quest is not started yet, start it. */
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING, "I'm worried about my brother who lives in Ados. I need someone to take some #food to him.",
			null);

		/** In case quest is completed */
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"My brother has enough food now, many thanks.", null);

		/** In case quest is completed */
		npc.add(ConversationStates.ATTENDING, "food",
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"My brother has enough sandwiches now, thank you.", null);

		/** If quest is not started yet, start it. */
		npc.add(
			ConversationStates.ATTENDING,
			"food",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"I think five sandwiches would be enough. My brother is called #Joshua. Can you help?",
			null);

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Thank you. Please tell him #food or #sandwich so he knows you're not just a customer.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 2.0));

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"So you'd just let him starve! I'll have to hope someone else is more charitable.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		npc.add(
			ConversationStates.QUEST_OFFERED,
			"Joshua",
			null,
			ConversationStates.QUEST_OFFERED,
			"He's the goldsmith in Ados. They're so short of supplies. Will you help?",
			null);

		/** Remind player about the quest */
		npc.add(ConversationStates.ATTENDING, Arrays.asList("food", "sandwich", "sandwiches"),
			new QuestInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.ATTENDING,
			"#Joshua will be getting hungry! Please hurry!", null);

		npc.add(ConversationStates.ATTENDING, "Joshua",
			new QuestInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.ATTENDING,
			"My brother, the goldsmith in Ados.", null);

		/** remind to take the sandwiches */
		npc.add(
			ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.ATTENDING,
			"Please don't forget the five #sandwiches for #Joshua!",
			null);
	}

	private void step_2() {
		final SpeakerNPC npc = npcs.get("Joshua");

		/** If player has quest and has brought the food, ask for it */
		npc.add(
			ConversationStates.ATTENDING,
			Arrays.asList("food", "sandwich", "sandwiches"),
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new PlayerHasItemWithHimCondition("sandwich", FOOD_AMOUNT)),
			ConversationStates.QUEST_ITEM_BROUGHT,
			"Oh great! Did my brother Xoderos send you with those sandwiches?",
			null);

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("sandwich", FOOD_AMOUNT));
		reward.add(new IncreaseXPAction(150));
		reward.add(new SetQuestAction(QUEST_SLOT, "joshua"));
		reward.add(new IncreaseKarmaAction(15));
		reward.add(new InflictStatusOnNPCAction("sandwich"));

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES,
			new PlayerHasItemWithHimCondition("sandwich", FOOD_AMOUNT),
			ConversationStates.ATTENDING,
			"Thank you! Please let Xoderos know that I am fine. Say my name, Joshua, so he knows that you saw me. He will probably give you something in return.",
			new MultipleActions(reward));

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES,
			new NotCondition(new PlayerHasItemWithHimCondition("sandwich", FOOD_AMOUNT)),
			ConversationStates.ATTENDING, "Hey! Where did you put the sandwiches?", null);

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.QUEST_ITEM_BROUGHT,
			"Oh dear, I'm so hungry, please say #yes they are for me.",
			null);
	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("Xoderos");

		/** remind to complete the quest */
		npc.add(
			ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT, "joshua"),
			ConversationStates.ATTENDING,
			"I do hope #Joshua is well ....",
			null);

		/** Remind player about the quest */
		npc.add(ConversationStates.ATTENDING, Arrays.asList("food", "sandwich", "sandwiches"),
			new QuestInStateCondition(QUEST_SLOT, "joshua"),
			ConversationStates.ATTENDING,
			"I wish you could confirm for me that #Joshua is fine ...", null);

		// ideally, make it so that this slot being done means
		// you get a keyring object instead what we currently
		// have - a button in the settings panel
		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new IncreaseXPAction(50));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));
		if (System.getProperty("stendhal.container") != null) {
			reward.add(new CreateSlotAction(ImmutableList.of("belt", "back")));
			reward.add(new EquipItemAction("keyring", 1, true));
		} else {
			reward.add(new EnableFeatureAction("keyring", "2 4"));
		}
		/** Complete the quest */
		npc.add(
			ConversationStates.ATTENDING, "Joshua",
			new QuestInStateCondition(QUEST_SLOT, "joshua"),
			ConversationStates.ATTENDING,
			"I'm glad Joshua is well. Now, what can I do for you? I know, I'll fix that broken key ring that you're carrying ... there, it should work now!",
			new MultipleActions(reward));
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Hungry Joshua",
				"Xoderos is worried about his brother Joshua who lives in Ados because they are short of supplies.",
				false);
		step_1();
		step_2();
		step_3();
	}
	@Override
	public String getName() {
		return "HungryJoshua";
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Xoderos";
	}
}
