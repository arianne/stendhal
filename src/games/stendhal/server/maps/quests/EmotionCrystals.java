/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
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

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Emotion Crystals
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Julius (the Soldier who guards the entrance to Ados City)</li>
 * <li>Crystal NPCs around Faiumoni</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Julius wants some precious stones for his wife.</li>
 * <li>Find the 5 crystals and solve their riddles.</li>
 * <li>Bring the crystals to Julius.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>2000 XP</li>
 * <li>stone legs</li>
 * <li>Karma: 15</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 *
 * @author AntumDeluge
 */
public class EmotionCrystals extends AbstractQuest {
	private static final String QUEST_SLOT = "emotion_crystals";

	private static final String[] crystalColors = { "red", "purple", "yellow", "pink", "blue" };

	// Amount of time, in minutes, player must wait before retrying the riddle (24 hours)
	private static final int WAIT_TIME_WRONG = 24 * 60;
	private static final int WAIT_TIME_RETRY = 7 * 24 * 60;

	private static final int OFFSET_TIMESTAMPS = 1;
	private static final int OFFSET_SUCCESS_MARKER = 6;

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}

		// Only include Julius in the quest log if player has spoken to him
		if (player.isQuestInState(QUEST_SLOT, 0, "start") || player.isQuestInState(QUEST_SLOT, 0,  "rejected")) {
			res.add("I have talked to Julius, the soldier that guards the entrance to Ados.");
			if (player.isQuestInState(QUEST_SLOT, 0, "rejected")) {
				res.add("I'm emotionally incapable and have rejected his request.");
			}
			else {
				res.add("I promised to gather five crystals from all across Faiumoni.");
			}
		}

		List<String> gatheredCrystals = new ArrayList<String>();
		boolean hasAllCrystals = true;

		for (String color : crystalColors) {
			if (player.isEquipped(color + " emotion crystal")) {
				gatheredCrystals.add(color + " emotion crystal");
			} else {
				hasAllCrystals = false;
			}
		}
		if (!gatheredCrystals.isEmpty()) {
			String tell = "I have found the following crystals: ";
			tell += Grammar.enumerateCollection(gatheredCrystals);
			res.add(tell);
		}

		if (hasAllCrystals) {
			res.add("I have obtained all of the emotion crystals and should bring them to Julius in Ados.");
		}

		if (player.isQuestInState(QUEST_SLOT, 0, "done")) {
			res.add("I gave the crystals to Julius for his wife. I got some experience, karma and useful stone legs.");
		}
		return res;
	}

	private void prepareRequestingStep() {
		final SpeakerNPC npc = npcs.get("Julius");


		// Player asks for quest
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new AndCondition(new QuestNotInStateCondition(QUEST_SLOT, 0, "start"),
					new QuestNotCompletedCondition(QUEST_SLOT)),
			ConversationStates.QUEST_OFFERED,
			"I don't get to see my wife very often because I am so busy guarding this entrance. I would like to do something for her. Would you help me?",
			null);

		// Player accepts quest
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Thank you. I would like to gather five #emotion #crystals as a gift for my wife. Please find all that you can and bring them to me.",
			new MultipleActions(
					new SetQuestAction(QUEST_SLOT, 0, "start"),
					new IncreaseKarmaAction(5)));

		// Player rejects quest
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			// Julius walks away
			ConversationStates.IDLE,
			"Hmph! I'll ask someone else then.",
			new MultipleActions(
					new SetQuestAction(QUEST_SLOT, 0, "rejected"),
					new DecreaseKarmaAction(5)));

		// Player tries to leave without accepting/rejecting the quest
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.GOODBYE_MESSAGES,
			null,
			ConversationStates.QUEST_OFFERED,
			"That is not a \"yes\" or \"no\" question. I said, would you do a favor for me?",
			null);

		// Player asks about crystals
		npc.add(
			ConversationStates.ATTENDING,
			Arrays.asList("crystal", "crystals", "emotion", "emotions", "emotion crystal", "emotion crystals", "emotions crystal", "emotions crystals"),
			new QuestActiveCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Don't you know what emotions are? Surely you've experienced joy or sadness. I've heard that there are crystals scattered throughout Faiumoni, special crystals that can bring out any emotion. There are five in all, hidden in dungeons, on mountains and in forests. I've even heard of some that have been found near houses.",
			null);

		// Player asks for quest after completed
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT, 0, "done"),
			ConversationStates.ATTENDING,
			"My wife is sure to love these emotion crystals.",
			null);

		// Player asks for quest after already started
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I believe I already asked you to get me some #crystals.",
				null);
	}


	private void prepareRiddlesStep() {
		// List of NPCs
		final List<SpeakerNPC> npcList = new ArrayList<SpeakerNPC>();

		// Add the crystals to the NPC list with their riddle
		for (String color : crystalColors) {
			npcList.add(npcs.get(Character.toUpperCase(color.charAt(0)) + color.substring(1) + " Crystal"));
		}

		// Riddles
		final List<String> riddles = new ArrayList<String>();
		// Answers to riddles
		final List<List<String>> answers = new ArrayList<List<String>>();

		// Red Crystal (crystal of anger)
		riddles.add("I burn like fire. My presence is not enjoyed. Those who test me will feel my wrath. What am I?");
		answers.add(Arrays.asList("anger", "angry", "mad", "offended", "hostility", "hostile", "hate", "hatred", "animosity"));
		// Purple Crystal (crystal of fear)
		riddles.add("I dare not come out and avoid the consequence. They try and convince my but I shall not. Trembling is my favorite activity. What am I?");
		answers.add(Arrays.asList("fear", "fearful", "fearfullness", "fright", "frightened", "afraid", "scared"));
		// Yellow Crystal (crystal of joy)
		riddles.add("I can't be stopped. Only positive, no negative, can exist in my heart. If you spread me life will be as sunshine. What am I?");
		answers.add(Arrays.asList("joy", "joyful", "joyfulness", "happy", "happiness", "happyness", "cheer", "cheery",
						"cheerful", "cheerfulness"));
		// Pink Crystal (crystal of love)
		riddles.add("I care for all things. I am purest of all. If you share me I'm sure I will be reciprocated. What am I?");
		answers.add(Arrays.asList("love", "amor", "amour", "amity", "compassion"));
		// Blue Crystal (crystal of peace)
		riddles.add("I do not let things bother me. I never get overly energetic. Meditation is my forte. What am I?");
		answers.add(Arrays.asList("peace", "peaceful", "peacefullness", "serenity", "serene", "calmness", "calm"));

		// Add conversation states
		for (int n = 0; n < npcList.size(); n++)
		{
			SpeakerNPC crystalNPC = npcList.get(n);
			String rewardItem = crystalColors[n] + " emotion crystal";
			String crystalRiddle = riddles.get(n);
			List<String> crystalAnswers = answers.get(n);

			// In place of QUEST_SLOT
			//String RIDDLER_SLOT = crystalColors.get(n) + "_crystal_riddle";

			final List<ChatAction> rewardAction = new LinkedList<ChatAction>();
			rewardAction.add(new EquipItemAction(rewardItem,1,true));
			rewardAction.add(new IncreaseKarmaAction(5));
			rewardAction.add(new SetQuestToTimeStampAction(QUEST_SLOT, OFFSET_TIMESTAMPS + n));
			rewardAction.add(new SetQuestAction(QUEST_SLOT, OFFSET_SUCCESS_MARKER + n, "riddle_solved"));

			final List<ChatAction> wrongGuessAction = new LinkedList<ChatAction>();
			wrongGuessAction.add(new SetQuestToTimeStampAction(QUEST_SLOT, OFFSET_TIMESTAMPS + n));
			wrongGuessAction.add(new SetQuestAction(QUEST_SLOT, OFFSET_SUCCESS_MARKER + n, "wrong"));

			// Player asks about riddle
			crystalNPC.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
					new QuestInStateCondition(QUEST_SLOT, 0, "start"),
					new OrCondition(
						new QuestNotInStateCondition(QUEST_SLOT, OFFSET_SUCCESS_MARKER + n, "riddle_solved"),
						new AndCondition(
							new QuestInStateCondition(QUEST_SLOT, OFFSET_SUCCESS_MARKER + n, "riddle_solved"),
							new NotCondition(new PlayerHasItemWithHimCondition(rewardItem)),
							new TimePassedCondition(QUEST_SLOT, OFFSET_TIMESTAMPS + n, WAIT_TIME_RETRY)
						)
					)
				),
				ConversationStates.ATTENDING,
				"Please answer the #riddle which I have for you...",
				null);

			// Player asks about riddle
			crystalNPC.add(ConversationStates.ATTENDING,
					Arrays.asList("riddle", "question", "query", "puzzle"),
					new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, 0, "start"),
						new OrCondition(
							new QuestNotInStateCondition(QUEST_SLOT, OFFSET_SUCCESS_MARKER + n, "riddle_solved"),
							new AndCondition(
								new QuestInStateCondition(QUEST_SLOT, OFFSET_SUCCESS_MARKER + n, "riddle_solved"),
								new NotCondition(new PlayerHasItemWithHimCondition(rewardItem)),
								new TimePassedCondition(QUEST_SLOT, OFFSET_TIMESTAMPS + n, WAIT_TIME_RETRY)
							)
						)
					),
					ConversationStates.ATTENDING,
					crystalRiddle,
					new SetQuestAction(QUEST_SLOT, OFFSET_SUCCESS_MARKER + n, "riddle"));

			// Player gets the riddle right
			crystalNPC.add(ConversationStates.ATTENDING,
					crystalAnswers,
					new QuestInStateCondition(QUEST_SLOT, OFFSET_SUCCESS_MARKER + n, "riddle"),
					ConversationStates.IDLE,
					"That is correct. Take this crystal as a reward.",
					new MultipleActions(rewardAction));


			// Player gets the riddle wrong
			crystalNPC.add(ConversationStates.ATTENDING,
					"",
					new QuestInStateCondition(QUEST_SLOT, OFFSET_SUCCESS_MARKER + n, "riddle"),
					ConversationStates.IDLE,
					"I'm sorry, that is incorrect.",
					new MultipleActions(wrongGuessAction));

			// Player returns before time is up, to get another chance
			crystalNPC.add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, OFFSET_SUCCESS_MARKER + n, "wrong"),
						new NotCondition(new TimePassedCondition(QUEST_SLOT, OFFSET_TIMESTAMPS + n, WAIT_TIME_WRONG))
					),
					ConversationStates.IDLE,
					null,
					new SayTimeRemainingAction(QUEST_SLOT, OFFSET_TIMESTAMPS + n, WAIT_TIME_WRONG, "Think hard on your answer and return to me again in"));

			// Player returns before time is up, to get another crystal
			crystalNPC.add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, OFFSET_SUCCESS_MARKER + n, "riddle_solved"),
						new NotCondition(new PlayerHasItemWithHimCondition(rewardItem)),
						new NotCondition(new TimePassedCondition(QUEST_SLOT, OFFSET_TIMESTAMPS + n, WAIT_TIME_RETRY))
					),
					ConversationStates.IDLE,
					null,
					new SayTimeRemainingAction(QUEST_SLOT, OFFSET_TIMESTAMPS + n, WAIT_TIME_RETRY, "Oh, did you lose my crystal? I can give you a new one in"));

			// Player can't do riddle twice while they still have the reward
			crystalNPC.add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, 0, "start"),
						new PlayerHasItemWithHimCondition(rewardItem)
					),
					ConversationStates.ATTENDING,
					"I hope you make someone happy with your crystal!",
					null);


			// Player asks for quest without talking to Julius first
			crystalNPC.add(ConversationStates.ATTENDING,
					ConversationPhrases.QUEST_MESSAGES,
					new QuestNotStartedCondition(QUEST_SLOT),
					ConversationStates.ATTENDING,
					"Sorry, but I have nothing to offer you. Maybe someone needs a sparkling #crystal soon...",
					null);

			crystalNPC.add(ConversationStates.ATTENDING,
					Arrays.asList("crystal", "sparkling crystal"),
					new QuestNotStartedCondition(QUEST_SLOT),
					ConversationStates.ATTENDING,
					"There is a soldier in Ados who used some beautiful crystals in jewellery for his wife...",
					null);

		}
	}


	private void prepareBringingStep() {
		final SpeakerNPC npc = npcs.get("Julius");

		// Reward
		final List<ChatAction> rewardAction = new LinkedList<ChatAction>();
		for (String color : crystalColors) {
			rewardAction.add(new DropItemAction(color + " emotion crystal"));
		}
		rewardAction.add(new EquipItemAction("stone legs", 1, true));
		rewardAction.add(new IncreaseXPAction(2000));
		rewardAction.add(new IncreaseKarmaAction(15));
		rewardAction.add(new SetQuestAction(QUEST_SLOT, 0, "done"));

		// Player has all crystals
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new QuestActiveCondition(QUEST_SLOT),
						new PlayerHasItemWithHimCondition("red emotion crystal"),
						new PlayerHasItemWithHimCondition("purple emotion crystal"),
						new PlayerHasItemWithHimCondition("yellow emotion crystal"),
						new PlayerHasItemWithHimCondition("pink emotion crystal"),
						new PlayerHasItemWithHimCondition("blue emotion crystal")),
				ConversationStates.QUEST_ITEM_BROUGHT,
				"Did you bring the crystals?",
				null);

		// Player is not carrying all the crystals
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, 0, "start"),
						new OrCondition(
								new NotCondition(new PlayerHasItemWithHimCondition("red emotion crystal")),
								new NotCondition(new PlayerHasItemWithHimCondition("purple emotion crystal")),
								new NotCondition(new PlayerHasItemWithHimCondition("yellow emotion crystal")),
								new NotCondition(new PlayerHasItemWithHimCondition("pink emotion crystal")),
								new NotCondition(new PlayerHasItemWithHimCondition("blue emotion crystal")))),
			ConversationStates.ATTENDING,
			"Please bring me all the emotion crystals you can find.",
			null);

		// Player says "yes" (has brought crystals)
		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.IDLE,
				"Thank you so much! I'm sure these will make my wife feel much better. Please, take these stone legs as a reward.",
				new MultipleActions(rewardAction));

		// Player says "no" (has not brought crystals)
		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Please keep looking. In the meantime, how can I help you?",
				null);

	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Emotion Crystals",
				"Julius needs to get some crystals for his wife which are spread across Faiumoni.",
				false);
		prepareRequestingStep();
		prepareRiddlesStep();
		prepareBringingStep();
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "EmotionCrystals";
	}

	public String getTitle() {

		return "Emotion Crystals";
	}

	@Override
	public int getMinLevel() {
		return 70;
	}

	@Override
	public String getRegion() {
		return Region.ADOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Julius";
	}
}
