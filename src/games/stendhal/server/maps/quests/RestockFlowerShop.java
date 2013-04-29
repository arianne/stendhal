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

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.CollectRequestedItemsAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayRequiredItemsFromCollectionAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.action.StartItemsCollectionWithLimitsAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.util.ItemCollection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * QUEST: Restock the Flower Shop
 * 
 * PARTICIPANTS:
 * <ul>
 * <li>Seremela, the elf girl who watches over Nalwor's flower shop</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li>Seremela asks you to bring a variety of flowers to restock the flower shop</li>
 * <li>Gather the requested amounts of each flower type and bring them to Seremela</li>
 * </ul>
 * 
 * REWARD:
 * <ul>
 * <li>1000 XP</li>
 * <li>25 karma</li>
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li>Once every 3 days</li>
 * </ul>
 * 
 * @author AntumDeluge
 *
 */
public class RestockFlowerShop extends AbstractQuest {
	
	public static final String QUEST_SLOT = "restock_flowershop";
	
	// Different types of flowers needed in quest
	private static final List<String> flowerTypes = Arrays.asList(
			"daisies", "lilia", "pansy", "rose", "zantedesc");
	public static List<Integer> requestedQuantities = Arrays.asList();
	
	private int MAX_FLOWERS = flowerTypes.size() * 15;
	
	private static String requestedFlowers;
	
	// Time player must wait to repeat quest (3 days)
	private static final int WAIT_TIME = 60 * 24 * 3;
	
	// Quest NPC
	private final SpeakerNPC npc = npcs.get("Seremela");
	
	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		String questState = player.getQuest(QUEST_SLOT);
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		if (player.isQuestInState(QUEST_SLOT, 0, "rejected")) {
			res.add("Flowers make me sneeze.");
		}
		if (player.isQuestInState(QUEST_SLOT, 0, "start")) {
			res.add("I have offered to help " + npc.getName() + " restock the flower shop.");
			
			final ItemCollection remaining = new ItemCollection();
			remaining.addFromQuestStateString(questState);
			
			// Check to avoid ArrayIndexOutOfBoundsException
			if (questState.split(";").length > 1) {
				requestedFlowers = "I still need to bring the following flowers: " + Grammar.enumerateCollection(remaining.toStringList()) + ".";
				/*
				for (int f = 0; f <= flowerTypes.size(); f++) {
					if (player.isQuestInState(QUEST_SLOT, f+1, flowerTypes.get(f))) {
						flowersBrought += " " + flowerTypes.get(f);
						if (f != flowerTypes.size()) {
							flowersBrought += ",";
						} else {
							flowersBrought += ".";
						}
					}
				}*/
				res.add(requestedFlowers);
			}
		}
		
		return res;
	}
	
	
	private void prepareRequestingStep() {
		
		// Player requests quest
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
						new NotCondition(new QuestInStateCondition(QUEST_SLOT, 0, "start")),
						new TimePassedCondition(QUEST_SLOT, flowerTypes.size()+1, WAIT_TIME)
						),
				ConversationStates.QUEST_OFFERED,
				"The flower shop is running low on flowers. Will help me restock it?",
				null);
		
		// Player requests quest after started
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, 0, "start"),
				ConversationStates.ATTENDING,
				"You still haven't brought me the flowers I asked for.",
				null);
		
		// Player accepts quest
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(
						new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 5.0),
						new StartItemsCollectionWithLimitsAction(QUEST_SLOT, flowerTypes, MAX_FLOWERS),
						new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "Great! Here is what I need: [items]"))
		);
		
		// Player rejects quest
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"I am sorry to hear that.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
	}
	
	
	private void prepareBringingStep() {
		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new IncreaseXPAction(1000));
		reward.add(new IncreaseKarmaAction(25.0));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));
		reward.add(new SetQuestToTimeStampAction(QUEST_SLOT));
		
		ChatAction rewardAction = new MultipleActions(reward);
		
		/* add triggers for the item names */
		final ItemCollection items = new ItemCollection();
		items.addFromQuestStateString(QUEST_SLOT);
		for (final Map.Entry<String, Integer> item : items.entrySet()) {
			npc.add(ConversationStates.QUESTION_2,
					item.getKey(),
					new QuestActiveCondition(QUEST_SLOT),
					ConversationStates.QUESTION_2,
					null,
					new CollectRequestedItemsAction(
							item.getKey(),
							QUEST_SLOT,
							"Thank you! Did you bring any other flowers?",
							"I don't need any more of those flowers.",
							rewardAction,
							ConversationStates.IDLE
							)
			);
		}
	}
	
	
	@Override
	public String getNPCName() {
		return npc.getName();
	}
	
	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	
	@Override
	public String getName() {
		return "RestockFlowerShop";
	}
	
	public String getTitle() {
		return "Restock the Flower Shop";
	}
	
	@Override
	public int getMinLevel() {
		return 0;
	}
	
	@Override
	public String getRegion() {
		return Region.NALWOR_CITY;
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				getTitle(),
				getNPCName() + " needs to restock the flower shop in Nalwor City.",
				true);
		prepareRequestingStep();
		prepareBringingStep();
	}
}
