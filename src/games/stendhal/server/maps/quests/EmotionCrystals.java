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
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.WordUtils;

/**
 * QUEST: Emotion Crystals
 * 
 * PARTICIPANTS:
 * <ul>
 * <li>Julius (the Soldier who guards the entrance to Ados City)</li>
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
	
	private final List<String> crystalColors = Arrays.asList("red", "purple", "yellow", "pink", "blue");
	/*
	private final List<String> requiredCrystals = Arrays.asList("red emotion crystal", "purple emotion crystal",
			"yellow emotion crystal", "pink emotion crystal", "blue emotion crystal");*/
	
	private List<String> gatheredCrystals = new ArrayList<String>();
	
	// Amount of time, in minutes, player must wait before retrying the riddle (24 hours)
	final int WAIT_TIME = 24 * 60;
	
	@Override
	public List<String> getHistory(final Player player) {
		
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		
		final String questState = player.getQuest(QUEST_SLOT);
		
		// Only include Julius in the quest log if player has spoken to him
		if (player.isQuestInState(QUEST_SLOT, "start") || player.isQuestInState(QUEST_SLOT,  "rejected")) {
			res.add("I have talked to Julius, the soldier that guards the entrance to Ados.");
			if ("rejected".equals(questState)) {
				res.add("I'm emotionally incapable and have rejected his request.");
			}
			else {
				res.add("I promised to gather crystals from all across Faimouni.");
			}
		}
		
		gatheredCrystals.clear();
		boolean foundCrystal = false;
		boolean hasAllCrystals = true;
		
		for (int x1 = 0; x1 < crystalColors.size(); x1++) {
			
			if (player.isEquipped(crystalColors.get(x1) + " emotion crystal")) {
				gatheredCrystals.add(crystalColors.get(x1) + " emotion crystal");
				foundCrystal = true;
			}
			else {
				hasAllCrystals = false;
			}
		}
		if (foundCrystal) {
			String tell = "I have found the following crystals: ";
			for (int x2 = 0; x2 < gatheredCrystals.size(); x2++) {
				// First crystal will not be preceded by ","
				if (x2 == 0) {
					tell += gatheredCrystals.get(x2);
					}
				else {
					tell += ", " + gatheredCrystals.get(x2);
				}
			}
			res.add(tell);
		}
		
		if (hasAllCrystals) {
			res.add("I have obtained all of the emotion crystals");
			if (player.isQuestInState(QUEST_SLOT, "start")) {
				res.add("I should bring them to Julius.");
			}
			else {
				res.add("I should find someone who can use them.");
			}
		}
		
		if ("done".equals(questState)) {
			res.add("I gave the crystals to Julius for his wife. I got some experience and karma.");
		}
		return res;
	}
	
	private void prepareRiddlesStep() {
		
		// List of NPCs
		final List<SpeakerNPC> npcList = new ArrayList<SpeakerNPC>();
		
		// Add the crystals to the NPC list with their riddle
		for (int c = 0; c < crystalColors.size(); c++) {
			npcList.add(npcs.get(WordUtils.capitalize(crystalColors.get(c)) + " Crystal"));
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
		riddles.add("I do not let things bother me. I never get overly energetic. Meditation is my fortay. What am I?");
		answers.add(Arrays.asList("peace", "peaceful", "peacefullness", "serenity", "serene", "calmness", "calm"));
		
		// Add conversation states
		for (int n = 0; n < npcList.size(); n++)
		{
			SpeakerNPC crystalNPC = npcList.get(n);
			String rewardItem = crystalColors.get(n) + " emotion crystal";
			String crystalRiddle = riddles.get(n); 
			List<String> crystalAnswers = answers.get(n);
			
			// In place of QUEST_SLOT
			//String RIDDLER_SLOT = crystalColors.get(n) + "_crystal_riddle";
			
			final List<ChatAction> rewardAction = new LinkedList<ChatAction>();
			rewardAction.add(new EquipItemAction(rewardItem));
			rewardAction.add(new IncreaseKarmaAction(5));
			rewardAction.add(new SetQuestToTimeStampAction(QUEST_SLOT, n+1));
			
			// The player speaks to crystal and receives riddle
			crystalNPC.add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					crystalRiddle,
					null);
			
			// Player says "bye"
			crystalNPC.add(ConversationStates.ATTENDING,
					ConversationPhrases.GOODBYE_MESSAGES,
					null,
					ConversationStates.IDLE,
					"Farewell, return to me when you have found the answer to my riddle.",
					null);
			
			// Player gets the riddle right
			crystalNPC.add(ConversationStates.ATTENDING,
					crystalAnswers,
					null,
					ConversationStates.IDLE,
					"That is correct. Take this crystal as a reward.",
					new MultipleActions(rewardAction));
			
			// Player gets the riddle wrong
			crystalNPC.add(ConversationStates.ATTENDING,
					"",
					null,
					ConversationStates.IDLE,
					"I'm sorry, that is incorrect.",
					new SetQuestToTimeStampAction(QUEST_SLOT, n+1));
			
			// Player returns before time is up
			crystalNPC.add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new NotCondition(new TimePassedCondition(QUEST_SLOT, n+1, WAIT_TIME)),
					ConversationStates.IDLE,
					null,
					new SayTimeRemainingAction(QUEST_SLOT, n+1, WAIT_TIME, "Think hard on your answer and return to me again in"));
			
			// Player can do riddle but already has crystal reward
			crystalNPC.add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					new PlayerHasItemWithHimCondition(rewardItem),
					ConversationStates.IDLE,
					"I have nothing left to offer you.",
					null);
			
			// Player asks for quest
			crystalNPC.add(ConversationStates.ATTENDING,
					ConversationPhrases.QUEST_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"If you solve my #riddle you shall have your reward.",
					null);
			
			// Player asks about riddle
			crystalNPC.add(ConversationStates.ATTENDING,
					Arrays.asList("riddle", "question", "query", "puzzle"),
					null,
					ConversationStates.ATTENDING,
					crystalRiddle,
					null);
			
			// Player asks about job
			crystalNPC.add(ConversationStates.ATTENDING,
					ConversationPhrases.JOB_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"I am a crystal. What more can I say?",
					null);
			
			// Player asks for help
			crystalNPC.add(ConversationStates.ATTENDING,
					ConversationPhrases.HELP_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"I cannot offer help, but I can offer a #riddle",
					null);
			
			// Player asks for offer
			crystalNPC.add(ConversationStates.ATTENDING,
					ConversationPhrases.OFFER_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"I will offer you up this #riddle.",
					null);
		}
	}

	private void prepareRequestingStep() {
		final SpeakerNPC npc = npcs.get("Julius");
		
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
				new QuestInStateCondition(QUEST_SLOT, 0, "start"),
				ConversationStates.ATTENDING,
				"I believe I already asked you to get me some #crystals.",
				null);
		
		// Player asks for quest
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, 
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED, 
			"I don't get to see my wife very often because I am so busy guarding this entrance. I would like to do something for her. Would you help me?",
			null);
		
		// Player accepts quest
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Thank you. I would like to gather the #emotion #crystals as a gift for my wife. Please find all that you can and bring them to me.",
			new MultipleActions(
					new SetQuestAction(QUEST_SLOT, 0, "start"),
					new IncreaseKarmaAction(5)));
		
		// Player rejects quest
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			// Klaas walks away
			ConversationStates.IDLE,
			"Hmph!",
			new MultipleActions(
					new SetQuestAction(QUEST_SLOT, 0, "rejected"),
					new DecreaseKarmaAction(5)));
		
		// Player asks about emotions
		npc.add(
			ConversationStates.ATTENDING,
			Arrays.asList("emotion", "emotions"),
			new QuestActiveCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Don't you know what emotions are? Surely you've experienced joy or sadness.",
			null);
		
		// Player asks about crystals
		npc.add(
			ConversationStates.ATTENDING,
			Arrays.asList("crystal", "crystals", "emotion crystal", "emotion crystals", "emotions crystal", "emotions crystals"),
			null,
			ConversationStates.ATTENDING,
			"I've heard that there are crystals scattered throughout Faimouni, special crystals that can bring out any emotion.",
			null);
	}

	private void prepareBringingStep() {
		final SpeakerNPC npc = npcs.get("Julius");
		
		// Reward
		final List<ChatAction> rewardAction = new LinkedList<ChatAction>();
		for (int x = 0; x < crystalColors.size(); x++) {
			rewardAction.add(new DropItemAction(crystalColors.get(x) + " emotion crystal"));
		}
		rewardAction.add(new EquipItemAction("stone legs", 1, true));
		rewardAction.add(new IncreaseXPAction(2000));
		rewardAction.add(new IncreaseKarmaAction(15));
		rewardAction.add(new SetQuestAction(QUEST_SLOT, 0, "done"));
		
		// Player has all crystals
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, "start"),
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
		super.addToWorld();
		fillQuestInfo(
				"Emotion Crystals",
				"Solve the riddles of the crystals spread across Faimouni.",
				false);
		prepareRiddlesStep();
		prepareRequestingStep();
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
		return 0;
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
