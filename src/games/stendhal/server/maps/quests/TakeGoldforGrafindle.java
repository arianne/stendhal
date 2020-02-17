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
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Take gold for Grafindle
 *
 * PARTICIPANTS: <ul>
 * <li> Grafindle
 * <li> Lorithien </ul>
 *
 * STEPS:<ul>
 * <li> Talk with Grafindle to activate the quest.
 * <li> Talk with Lorithien for the money.
 * <li> Return the gold bars to Grafindle</ul>
 *
 * REWARD:<ul>
 * <li> 200 XP
 * <li> some karma (10)
 * <li> key to nalwor bank customer room
 * </ul>
 * REPETITIONS: <ul><li> None.</ul>
 */
public class TakeGoldforGrafindle extends AbstractQuest {

	private static final int GOLD_AMOUNT = 25;

	private static final String QUEST_SLOT = "grafindle_gold";

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
		res.add("I went to the Nalwor bank and met Grafindle.");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("The responsibility I would have with the gold bars was too high for me and I had to reject Grafindle's request.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "lorithien", "done")) {
			res.add("Because I'm a trustworthy person, I promised Grafindle to get the gold from Lorithien.");
		}
		if (questState.equals("lorithien") && player.isEquipped("gold bar",
				GOLD_AMOUNT)
				|| questState.equals("done")) {
			res.add("Wohoo! I collected the gold bars Grafindle needs!");
		}
		if (questState.equals("lorithien")
				&& !player.isEquipped("gold bar", GOLD_AMOUNT)) {
			res.add("Oh no! I lost the gold bars which I had to bring Grafindle!");
		}
		if (questState.equals("done")) {
			res.add("I gave the gold bars to Grafindle and he rewarded me with a key to the customer bank room.");
		}
		return res;
	}

	private void step_1() {

		final SpeakerNPC npc = npcs.get("Grafindle");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestNotCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING, "I need someone who can be trusted with #gold.",
			null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I ask only that you are honest.",
				null);

		/** In case quest is completed */
		npc.add(ConversationStates.ATTENDING, "gold",
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"The bank has the gold safe now. Thank you!", null);

		/** If quest is not started yet, start it. */
		npc.add(
			ConversationStates.ATTENDING,
			"gold",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"One of our customers needs to bank their gold bars here for safety. It's #Lorithien, she cannot close the Post Office so she never has time.",
			null);

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.IDLE,
			"Thank you. I hope to see you soon with the gold bars ... unless you are tempted to keep them.",
			new SetQuestAction(QUEST_SLOT,"start"));

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING,
				"Well, at least you are honest and told me from the start.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		npc.add(
			ConversationStates.QUEST_OFFERED,
			"Lorithien",
			null,
			ConversationStates.QUEST_OFFERED,
			"She works in the post office here in Nalwor. It's a big responsibility, as those gold bars could be sold for a lot of money. Can you be trusted?",
			null);

		/** Remind player about the quest */
		npc.add(
			ConversationStates.ATTENDING,
			"gold",
			new QuestInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.ATTENDING,
			"#Lorithien will be getting so worried with all that gold not safe! Please fetch it!",
			null);

		npc.add(ConversationStates.ATTENDING, "lorithien", null,
			ConversationStates.ATTENDING,
			"She works in the post office here in Nalwor.", null);
	}

	private void step_2() {
		final SpeakerNPC npc = npcs.get("Lorithien");

		/**
		 * If player has quest and is in the correct state, just give him the
		 * gold bars.
		 */
		final List<ChatAction> givegold = new LinkedList<ChatAction>();
		givegold.add(new EquipItemAction("gold bar",GOLD_AMOUNT, true));
		givegold.add(new SetQuestAction(QUEST_SLOT, "lorithien"));

		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(getName()),
					new QuestInStateCondition(QUEST_SLOT, "start")),
			ConversationStates.ATTENDING,
			"I'm so glad you're here! I'll be much happier when this gold is safely in the bank.",
			new MultipleActions(givegold));

		/** If player keep asking for book, just tell him to hurry up */
		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(getName()),
					new QuestInStateCondition(QUEST_SLOT, "lorithien")),
			ConversationStates.ATTENDING,
			"Oh, please take that gold back to #Grafindle before it gets lost!",
			null);

		npc.add(ConversationStates.ATTENDING, "grafindle", null,
			ConversationStates.ATTENDING,
			"Grafindle is the senior banker here in Nalwor, of course!",
			null);

		/** Finally if player didn't start the quest, just ignore him/her */
		npc.add(
			ConversationStates.ATTENDING,
			"gold",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Sorry, I have so many things to remember ... I didn't understand you.",
			null);
	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("Grafindle");

		/** Complete the quest */
		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("gold bar", GOLD_AMOUNT));
		reward.add(new EquipItemAction("nalwor bank key", 1, true));
		reward.add(new IncreaseXPAction(200));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));
		reward.add(new IncreaseKarmaAction(10));

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(getName()),
						new QuestInStateCondition(QUEST_SLOT, "lorithien"),
						new PlayerHasItemWithHimCondition("gold bar", GOLD_AMOUNT)),
				ConversationStates.ATTENDING,
				"Oh, you brought the gold! Wonderful, I knew I could rely on you. Please, have this key to our customer room.",
				new MultipleActions(reward));

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(getName()),
						new QuestInStateCondition(QUEST_SLOT, "lorithien"),
						new NotCondition(new PlayerHasItemWithHimCondition("gold bar", GOLD_AMOUNT))),
				ConversationStates.ATTENDING,
				"Haven't you got the gold bars from #Lorithien yet? Please go get them, quickly!",
				null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Take Gold for Grafindle",
				"Grafindle in the bank of Nalwor, searches for someone he can trust with gold.",
				false);
		step_1();
		step_2();
		step_3();
	}

	@Override
	public String getName() {
		return "TakeGoldforGrafindle";
	}

	// it is not easy to get to Nalwor
	@Override
	public int getMinLevel() {
		return 50;
	}

	@Override
	public String getRegion() {
		return Region.NALWOR_CITY;
	}

	@Override
	public String getNPCName() {
		return "Grafindle";
	}
}
