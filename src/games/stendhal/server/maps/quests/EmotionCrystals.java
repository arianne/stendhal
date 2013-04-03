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
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
	
	private final List<String> requiredCrystals = Arrays.asList("red emotion crystal", "purple emotion crystal",
			"yellow emotion crystal", "pink emotion crystal", "blue emotion crystal");
	
	private List<String> gatheredCrystals = new ArrayList<String>();
	

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I have talked to Julius, the soldier that guards the entrance to Ados.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("I'm emotionally incapable.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "done")) {
			res.add("I promised to gather crystals from all across Faimouni.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start")) {
			boolean foundCrystal = false;
			boolean hasAllCrystals = true;
			
			for (int x = 0; x < requiredCrystals.size(); x++) {
				if (player.isEquipped(requiredCrystals.get(x))) {
					gatheredCrystals.add(requiredCrystals.get(x));
					foundCrystal = true;
				}
				else {
					hasAllCrystals = false;
				}
			}
			if (foundCrystal) {
				String tell = "I have found the following crystals: ";
				for (int x = 0; x < gatheredCrystals.size(); x++) {
					// First crystal will be on a new line and not have ","
					if (x == 0) {
						tell += gatheredCrystals.get(x);
						}
					else {
						tell += ", " + gatheredCrystals.get(x);
					}
				}
				res.add(tell);
			}
			if (hasAllCrystals) {
				res.add("I have obtained all of the emotion crystals");
			}
		}
		if ("done".equals(questState)) {
			res.add("I gave the crystals to Julius for his wife. I got some experience and karma.");
		}
		return res;
	}

	private void prepareRequestingStep() {
		final SpeakerNPC npc = npcs.get("Julius");
		
		// Player asks for quest
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, 
			new QuestNotCompletedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED, 
			"I don't get to see my wife very often because I am so busy gaurding this entrance. I would like to do something for her. Would you help me?",
			null);
		
		// Player asks for quest after completed
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT, "done"),
			ConversationStates.ATTENDING, 
			"My wife is sure to love these emotion crystals.",
			null);
		
		// Player asks for quest after already started
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.ATTENDING,
				"I believe I already asked you to do something for me",
				null);
		
		// Player accepts quest
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Thank you. I would like to gather the #emotion #crystals as a gift for my wife. Please find all that you can and bring them to me.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 5.0));
		
		// Player rejects quest
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			// Klaas walks away
			ConversationStates.IDLE,
			"Hmph!",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
		
		// Player asks about emotions
		npc.add(
			ConversationStates.ATTENDING,
			Arrays.asList("emotion", "emotions"),
			new QuestActiveCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Don't you know what emotions are? Surely you've experienct joy or sadness.",
			null);
		
		// Player asks about crystals
		npc.add(
			ConversationStates.ATTENDING,
			Arrays.asList("crystal", "crystals", "emotion crystal", "emotion crystals", "emotions crystal", "emotions crystals"),
			null,
			ConversationStates.ATTENDING,
			"I've heard that there crystals scattered throughout Faimouni, special crystals that can bring out any emotion.",
			null);
	}

	private void prepareBringingStep() {
		final SpeakerNPC npc = npcs.get("Julius");
		
		// Reward
		final List<ChatAction> rewardAction = new LinkedList<ChatAction>();
		for (int x = 0; x < requiredCrystals.size(); x++) {
			rewardAction.add(new DropItemAction(requiredCrystals.get(x)));
		}
		rewardAction.add(new EquipItemAction("stone legs", 1, true));
		rewardAction.add(new IncreaseXPAction(2000));
		rewardAction.add(new IncreaseKarmaAction(15));
		rewardAction.add(new SetQuestAction(QUEST_SLOT, "done"));
		
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
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
						new OrCondition(new NotCondition(new PlayerHasItemWithHimCondition("red emotion crystal")),
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
				"Thanks you so much! I'm sure these will make my wife feel much better. Please, take these stone legs as a reward.",
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
				"Julius wants to cheer up his wife.",
				false);
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
