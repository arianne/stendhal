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

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Quest to fetch water for a thirsty woman.
 * You have to check it is clean with someone knowledgeable first
 *
 * @author kymara
 */

public class WaterForJane extends AbstractQuest {

	// constants
	private static final String QUEST_SLOT = "water_for_jane";
	
	private static final String extraTrigger = "water";
	private List<String> questTrigger;

	/** The delay between repeating quests.
	 * 7200 minutes */
	private static final int REQUIRED_MINUTES = 7200;

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	
	private void requestStep() {
		final SpeakerNPC npc = npcs.get("Jane");
		
		// player asks about quest for first time (or rejected)
		npc.add(ConversationStates.ATTENDING,
				questTrigger, 
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED, 
				"I'm terribly thirsty, could you possibly get me some fresh water please?",
				null);
		
		// player can repeat quest
		npc.add(ConversationStates.ATTENDING,
				questTrigger, 
				new AndCondition(new QuestCompletedCondition(QUEST_SLOT), new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)),
				ConversationStates.QUEST_OFFERED, 
				"My throat is dry again, could you fetch me a little more water?",
				null);	
		
		// player can't repeat quest
		npc.add(ConversationStates.ATTENDING,
				questTrigger, 
				new AndCondition(new QuestCompletedCondition(QUEST_SLOT), new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES))),
				ConversationStates.ATTENDING, 
				"Thank you, I don't need anything right now.",
				null);	
		
		// if the quest is active we deal with the response to quest/water in a following step
		
		// Player agrees to get the water
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, 
				null,
				ConversationStates.ATTENDING, 
				"Thank you!",
				new SetQuestAction(QUEST_SLOT, 0, "start"));
		
		// Player says no, they've lost karma
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, 
				null, 
				ConversationStates.IDLE,
				"Well, that's not very charitable.",
				new MultipleActions(
						new SetQuestAction(QUEST_SLOT, 0, "rejected"),
						new DecreaseKarmaAction(5.0)));
	}
		
	
	private void checkWaterStep() {
		// haven't decided for sure it will be this NPC, just a placeholder
		final SpeakerNPC waterNPC = npcs.get("Dr. Feelgood");

		// player gets water checked
		// mark infostring of item to show it's good
		waterNPC.add(ConversationStates.ATTENDING, 
					Arrays.asList("water", "clean", "check"),
					new PlayerHasItemWithHimCondition("water"),
					ConversationStates.ATTENDING, 
					"That water looks clean to me! It must be from a pure source.",
					// take the item and give them a new one with an infostring or mark all?
					null);
		
		// player asks about water but doesn't have it with them
		waterNPC.add(ConversationStates.ATTENDING, 
					Arrays.asList("water", "clean", "check"),
					new NotCondition(new PlayerHasItemWithHimCondition("water")),
					ConversationStates.ATTENDING, 
					"You can gather water from natural mountain springs. If you bring it to me I can check the purity for you.",
					null);

	}

	
	private void finishStep() {
		final SpeakerNPC npc = npcs.get("Jane");
		
		// Player has got water 
		// optionally check if it was actually 
		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("water"));
		reward.add(new EquipItemAction("potion", 3));
		reward.add(new IncreaseXPAction(100));
		reward.add(new IncrementQuestAction(QUEST_SLOT, 2, 1) );
		reward.add(new SetQuestToTimeStampAction(QUEST_SLOT,1));
		reward.add(new SetQuestAction(QUEST_SLOT, 0, "done"));
		reward.add(new IncreaseKarmaAction(5.0));
		
		npc.add(ConversationStates.ATTENDING,
				questTrigger, 
				new AndCondition(
						new QuestActiveCondition(QUEST_SLOT),
						new PlayerHasItemWithHimCondition("water")),
				ConversationStates.ATTENDING, 
				"Thank you ever so much! This water feels deliciously cool. Here, take these potions.",
				new MultipleActions(reward));
		
        // add the other possibilities
		npc.add(ConversationStates.ATTENDING,
				questTrigger, 
				new AndCondition(
						new QuestActiveCondition(QUEST_SLOT),
						new NotCondition(new PlayerHasItemWithHimCondition("water"))),
				ConversationStates.ATTENDING, 
				"I'm waiting for you to bring me some water.",
				null);
	}
	

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Water For Jane",
				"Jane wants some nice fresh water.",
				true);
	    questTrigger = new LinkedList<String>(ConversationPhrases.QUEST_MESSAGES);
		questTrigger.add(extraTrigger);
		requestStep();
		checkWaterStep();
		finishStep();
	}


	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("Jane is thirsty.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("I told Jane I didn't want to fetch water for her.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start") || isCompleted(player)) {
			res.add("I agreed to fetch some water.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start") && player.isEquipped("water") || isCompleted(player)) {
			res.add("I found a source of fresh water.");
		}
		// checked water was clean?
        if (isCompleted(player)) {
            if (isRepeatable(player)) {
                res.add("I took the water to Jane a while ago.");
            } else {
                res.add("I took the water to Jane recently and she gave me some potions.");
            }			
		}
		return res;
	}

	@Override
	public String getName() {
		return "WaterForJane";
	}
	
	// Getting to Athor is not too feasible till this level
	@Override
	public int getMinLevel() {
		return 20;
	}
	
	@Override
	public boolean isRepeatable(final Player player) {
		return new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
				 new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)).fire(player,null, null);
	}
	
}
