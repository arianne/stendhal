package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayRequiredItemsFromCollectionAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.StartItemsCollectionWithLimitsAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class StockFlowerShop extends AbstractQuest {
	
	public static final String QUEST_SLOT = "stock_flowershop";
	
	// Different types of flowers needed in quest
	private static final List<String> flowerTypes = Arrays.asList(
			"daisies", "lilia", "pansy", "rose", "zantedesc");
	public static List<Integer> requestedQuantities = Arrays.asList();
	
	private int MAX_FLOWERS = flowerTypes.size() * 15;
	
	private static String flowersBrought;
	
	// Time (in minutes) player must wait to repeat quest
	private static final int WAIT_TIME = 1;
	
	// Quest NPC
	private final SpeakerNPC npc = npcs.get("Seremela");
	
	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		if (player.isQuestInState(QUEST_SLOT, 0, "rejected")) {
			res.add("Flowers make me sneeze.");
		}
		if (player.isQuestInState(QUEST_SLOT, 0, "start")) {
			res.add("I have offered to help " + npc.getName() + " restock the flower shop.");
			
			// Check to avoid ArrayIndexOutOfBoundsException
			if (QUEST_SLOT.split(",").length > 1) {
				flowersBrought = "I have brought the requested amounts of the following flowers:";
				
				for (int f = 0; f <= flowerTypes.size(); f++) {
					if (player.isQuestInState(QUEST_SLOT, f+1, flowerTypes.get(f))) {
						flowersBrought += " " + flowerTypes.get(f);
						if (f != flowerTypes.size()) {
							flowersBrought += ",";
						} else {
							flowersBrought += ".";
						}
					}
				}
				res.add(flowersBrought);
			}
		}
		
		return res;
	}
	/*
	private void setFlowerQuantities() {
		int flowersLeft = flowerTypes.size() * 15; // Average 15 flowers per type
		int askedAmount;
		
		for (int f = flowerTypes.size(); f > 0; f--) {
			// Generate a random number for each flower type but leave at least 1 for each remaining flower type
			askedAmount = Rand.randUniform(1, flowersLeft - (f-1));
			
			// Add the requested amount for each flower to a list
			requestedQuantities.add(askedAmount);
			
			flowersLeft -= askedAmount;
		}
	}*/
	
	
	private void prepareRequestingStep() {
		
		// Player requests quest
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
						new NotCondition(new QuestInStateCondition(QUEST_SLOT, 0, "start")),
						new TimePassedCondition(QUEST_SLOT, flowerTypes.size()+1, WAIT_TIME)
						),
				ConversationStates.QUEST_OFFERED,
				"My shop is running low on flowers. Will help me restock it?",
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
		return "StockFlowerShop";
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
				getNPCName() + " needs to restock her flower shop in Nalwor City.",
				true);
		prepareRequestingStep();
		prepareBringingStep();
	}
}
