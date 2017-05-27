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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.Direction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.ExamineChatAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.TeleportAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Zekiels practical test
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Zekiel, guardian of the wizard's tower </li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Zekiel the guardian asks you to bring him 6 beeswax and 2 iron to make magic candles with. </li>
 * <li> Bring the items to Zekiel. </li>
 * <li> You can start the practical test. </li>
 * <li> Zekiel informs you about the test and wizards. </li>
 * <li> You will be send to 6 levels now at which you have to choose the right creature. </li>
 * <li> If you made the right choices, you'll be able to reach the spire everytime you want. </li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> 9,000 XP total </li>
 * <li> some karma (20 total) </li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> None </li>
 * </ul>
 */
public class ZekielsPracticalTestQuest extends AbstractQuest {

	private static final int REQUIRED_IRON = 2;

	private static final int REQUIRED_BEESWAX = 6;

	private static final String QUEST_SLOT = "zekiels_practical_test";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void prepareQuestOfferingStep() {
		final SpeakerNPC npc = npcs.get("Zekiel the guardian");

		// player asks about quest when he has not started it
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"First you need six magic candles. Bring me six pieces of #beeswax and two pieces of #iron, " +
				"then I will summon the candles for you. After this you can start the practical test.",
				new SetQuestAction(QUEST_SLOT,"start"));

		// player asks about quest when he has already completed it
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"You have already passed the practical test and you are free to explore this tower. I will #teleport you " +
			"to the spire, or I can #help you some other way.",
			null);

		// player asks about quest when he is in the initial bringing candles stage
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.ATTENDING,
				"You haven't brought me the #ingredients for the magic candles.",
				null);

		// player asks about quest when he is in the practical test stage
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "candles_done"),
				ConversationStates.ATTENDING,
				"You haven't finished the practical test. You can #start with it, or you can learn " +
				"more about the #wizards before you begin.",
				null);

		// we should only answer to these ingredients questions if the candles stage is not yet done
		npc.add(ConversationStates.ATTENDING,
				"beeswax",
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.ATTENDING,
			    "I will summon magic candles for you, but I will need beeswax for that. Beekeepers usually sell beeswax.",
			    null);

		// we should only answer to these ingredients questions if the candles stage is not yet done
		npc.add(ConversationStates.ATTENDING,
				"iron",
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.ATTENDING,
				"The candlestick needs to be made of iron. The blacksmith in Semos can help you.",
				null);

		// we should only answer to these ingredients questions if the candles stage is not yet done
		npc.add(ConversationStates.ATTENDING,
				"ingredients",
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.ATTENDING,
				"I will need six pieces of #beeswax and two pieces of #iron to summon the candles.",
				null);
	}

	private void bringItemsStep() {
		final SpeakerNPC npc = npcs.get("Zekiel the guardian");

		// player returns with iron but no beeswax
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(
					new GreetingMatchesNameCondition(npc.getName()),
					new QuestInStateCondition(QUEST_SLOT,"start"),
					new NotCondition(new PlayerHasItemWithHimCondition("beeswax",REQUIRED_BEESWAX)),
					new PlayerHasItemWithHimCondition("iron",REQUIRED_IRON)),
			ConversationStates.ATTENDING,
			"Greetings, I see you have the iron, but I still need six pieces of beeswax. Please come back when you " +
			"have all #ingredients with you.",
			null);

		// player returns with beeswax but no iron
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(
					new GreetingMatchesNameCondition(npc.getName()),
					new QuestInStateCondition(QUEST_SLOT,"start"),
					new NotCondition(new PlayerHasItemWithHimCondition("iron",REQUIRED_IRON)),
					new PlayerHasItemWithHimCondition("beeswax",REQUIRED_BEESWAX)),
			ConversationStates.ATTENDING,
			"Greetings, I see you have the beeswax, but I still need two pieces of iron. Please come back when you " +
			"have all #ingredients with you.",
			null);

		//player returns with beeswax and iron
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(
					new GreetingMatchesNameCondition(npc.getName()),
					new QuestInStateCondition(QUEST_SLOT,"start"),
					new PlayerHasItemWithHimCondition("iron",REQUIRED_IRON),
					new PlayerHasItemWithHimCondition("beeswax",REQUIRED_BEESWAX)),
			ConversationStates.ATTENDING,
			"Greetings, finally you have brought me all ingredients that I need to summon the magic candles. Now you " +
			"can #start with the practical test.",
			new MultipleActions(
					new SetQuestAction(QUEST_SLOT,"candles_done"),
					new DropItemAction("beeswax", 6),
					new DropItemAction("iron", 2),
					new IncreaseXPAction(4000),
					new IncreaseKarmaAction(10)));

		// player returned after climbing the tower partially. reset status to candles done and start again
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new OrCondition(
								new QuestInStateCondition(QUEST_SLOT,"first_step"),
								new QuestInStateCondition(QUEST_SLOT,"second_step"),
								new QuestInStateCondition(QUEST_SLOT,"third_step"),
								new QuestInStateCondition(QUEST_SLOT,"fourth_step"),
								new QuestInStateCondition(QUEST_SLOT,"fifth_step"),
								new QuestInStateCondition(QUEST_SLOT,"sixth_step"),
								new QuestInStateCondition(QUEST_SLOT,"last_step"))),
			ConversationStates.ATTENDING,
			"Greetings! You have so far failed the practical test. Tell me, if you want me to #send you on it again " +
			"right now, or if there is anything you want to #learn about it first.",
			new SetQuestAction(QUEST_SLOT, "candles_done"));
	}

	private void practicalTestStep() {
		final SpeakerNPC npc = npcs.get("Zekiel the guardian");

		// player returns after bringing the candles but hasn't tried to climb tower
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestInStateCondition(QUEST_SLOT,"candles_done")),
			ConversationStates.ATTENDING,
			"Greetings, I suppose you came back to #start with the practical test.",
			null);

		// player asks to start the practical part of the quest
		npc.add(ConversationStates.ATTENDING,
			"start",
			new QuestInStateCondition(QUEST_SLOT,"candles_done"),
			ConversationStates.ATTENDING,
			"First you should #know some important things about the test and the wizards. " +
			"I will #send you to the first step, if you are ready.",
			null);

		// player wants to know how the practical quest works
		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("know", "learn"),
			new QuestInStateCondition(QUEST_SLOT,"candles_done"),
			ConversationStates.ATTENDING,
			"At each step there is a northern, southern, eastern and western cell, which contains a creature." +
			" Choose the creature, that you associate with the #wizards domain and history, by using the magical spot" +
			" between the two warlock statues in front of the cell. Don't worry, you don't have to fight the creature" +
			" that you choose. If you choose wisely, then I will summon a candle for you, if not you will be teleported" +
			" back to me. Use the candle at the shimmering corner of the hexagramm and the step is done. If you want to" +
			" leave the practical test, just use the magical spot in the middle of the hexagramm." +
			" So if you think are ready, I will #send you to the first step.",
			null);

		// player asks about wizards: give him a parchment of information.
		// this overrides the normal answer to wizards if the player is in the correct quest slot
		npc.add(ConversationStates.ATTENDING,
				"wizards",
				new QuestInStateCondition(QUEST_SLOT,"candles_done"),
				ConversationStates.ATTENDING,
				"Take this parchment with hints about the seven wizards, you will need it at each step I #send you on. " +
				"Listen for my message telling you whose domain you entered, at each step, or you cannot choose the correct creature.",
				new ExamineChatAction("wizards-parchment.png", "Parchment", "The wizards circle"));

		// incase the player still has candles, remove them from him
		npc.add(ConversationStates.ATTENDING,
			"send",
			new AndCondition(
					new QuestInStateCondition(QUEST_SLOT,"candles_done"),
					new PlayerHasItemWithHimCondition("candle")),
			ConversationStates.ATTENDING,
			"Before I can send you on the first step, you have to drop any candles you are carrying.",
			null);

		// send the player, so long as he doesn't not have candles, and record which step he is on
		npc.add(ConversationStates.ATTENDING,
			"send",
			new AndCondition(
					new QuestInStateCondition(QUEST_SLOT,"candles_done"),
					new NotCondition(new PlayerHasItemWithHimCondition("candle"))),
			ConversationStates.IDLE,
			null,
			new MultipleActions(
					new SetQuestAction(QUEST_SLOT, "first_step"),
					new TeleportAction("int_semos_wizards_tower_1", 15, 16, Direction.DOWN)));
	}

	private void finishQuestStep() {

		// NOTE: this is a different NPC from Zekiel the guardian used above. This one 'finishes' the quest
		// and is in int_semos_wizards_tower_7, not the basement.
		final SpeakerNPC npc = npcs.get("Zekiel");

		// player got to the last level of the tower
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestInStateCondition(QUEST_SLOT,"last_step")),
			ConversationStates.ATTENDING,
			"Very well, adventurer! You have passed the practical test. You can now enter the spire whenever you want.",
			new MultipleActions(
				new IncreaseXPAction(5000),
				new IncreaseKarmaAction(10),
				new SetQuestAction(QUEST_SLOT, "done")));
	}

	private void questFinished() {

		// this is the basement level normal Zekiel the guardian again
		final SpeakerNPC npc = npcs.get("Zekiel the guardian");

		// player returns having completed the quest
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestCompletedCondition(QUEST_SLOT)),
			ConversationStates.ATTENDING,
			"Greetings adventurer, how can I #help you this time?",
			null);

		// player asks for help, having completed the quest
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.HELP_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"I can #teleport you to the spire and I am also the #storekeeper of the #wizards tower.",
			null);

		// player asks about the store, having completed the quest
		npc.add(ConversationStates.ATTENDING,
			"storekeeper",
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"The store is at the floor under the spire. I will be there when you enter it.",
			null);

		// send a player who has completed the quest to the top spire
		npc.add(ConversationStates.ATTENDING,
			"teleport",
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.IDLE, null,
			new TeleportAction("int_semos_wizards_tower_8", 21, 22, Direction.UP));

		// player who has completed quest asks about the tower or test, offer the teleport or help
		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("tower", "test"),
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"You have already passed the practical test and you are free to explore this tower. I will #teleport you to the spire, or can I #help you some other way?",
			null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Zekiels Practical Test",
				"Zekiel, the guardian of the magic tower, knows all about the wizards domain and history.",
				true);

		prepareQuestOfferingStep();
		bringItemsStep();
		practicalTestStep();
		finishQuestStep();
		questFinished();
	}

	@Override
	public List<String> getHistory(final Player player) {
		LinkedList<String> history = new LinkedList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return history;
		}
		final String questState = player.getQuest(QUEST_SLOT);
		history.add("I entered the Wizards Circle tower. Zekiel the guardian asked me for items to make magic candles.");
		if (questState.equals("start") && player.isEquipped("beeswax", REQUIRED_BEESWAX) && player.isEquipped("iron", REQUIRED_IRON)
				|| questState.equals("candles_done") || questState.endsWith("_step") || questState.equals("done")) {
			history.add("I collected beeswax and iron for the magic candles which I will find on the next steps, if I pass each test.");
		}
		if (questState.endsWith("_step")) {
			history.add("I have reached the " + questState.replace("_", " ") + " of the Wizards Circle Tower. Zekiel will teach me what I have to do here.");
		}
		if (questState.equals("done")) {
			history.add("I completed the Practical Test and can enter the spire or visit the store.");
		}
		return history;
	}

	@Override
	public String getName() {
		return "ZekielsPracticalTest";
	}

	@Override
	public String getNPCName() {
		return "Zekiel the guardian";
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_SURROUNDS;
	}

	@Override
	public int getMinLevel() {
		return 30;
	}
}
