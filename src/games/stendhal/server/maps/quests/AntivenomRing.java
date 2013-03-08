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
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Antivenom Ring Part 1
 * 
 * PARTICIPANTS:
 * <ul>
 * <li>Klaas (the Seaman that takes care of Athor's ferry's cargo)</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li>Klaas asks you to bring him rodent traps.</li>
 * </ul>
 * 
 * REWARD:
 * <ul>
 * <li>1000 XP</li>
 * <li>Can sell rodent traps to Klaas</li>
 * <li>Karma: 10</li>
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 */
public class AntivenomRing extends AbstractQuest {

	public final int REQUIRED_TRAPS = 20;
	
	private static final String QUEST_SLOT = "antivenom_ring";
	

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I have talked to Klaas.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("I do not care to deal with rodents.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "done")) {
			res.add("I promised to gather " + REQUIRED_TRAPS + " rodent traps and bring them to Klaas.");
		}
		if ("done".equals(questState)) {
			res.add("I gave the rodent traps to Klaas. I got some experience and can now sell rodent traps to him.");
		}
		return res;
	}

	private void prepareRequestingStep() {
		final SpeakerNPC npc = npcs.get("Klaas");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, 
			new QuestNotCompletedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED, 
			"The rats down here have been getting into the food storage. Would you bring me " + REQUIRED_TRAPS + " #rodent #traps to help me rid us of the varmints?",
			null);

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING, 
			"Thanks for the traps. Now the food will be safe.",
			null);

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Please hurry! We can't afford to lose anymore food.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 5.0));

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Don't waste my time. I've got to protect the cargo.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		npc.add(
			ConversationStates.QUEST_OFFERED,
			Arrays.asList("rodent trap", "trap"),
			null,
			ConversationStates.QUEST_OFFERED,
			"I don't know of anyone who sells 'em. But I did hear a story once about a fella who killed a large rat and discovered a trap snapped shut on its foot.",
			null);
		
		npc.add(
			ConversationStates.ATTENDING,
			"apothecary",
			null,
			ConversationStates.ATTENDING,
			"I used to know an old apothecary, but don't know where he has settled down. Perhaps someone in Ados would know.",
			null);
	}

	private void prepareBringingStep() {
		final SpeakerNPC npc = npcs.get("Klaas");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestActiveCondition(QUEST_SLOT),
					new PlayerHasItemWithHimCondition("rodent trap")),
			ConversationStates.QUEST_ITEM_BROUGHT, 
			"Did you bring any traps?", null);

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestActiveCondition(QUEST_SLOT),
					new NotCondition(new PlayerHasItemWithHimCondition("rodent trap", 20))),
			ConversationStates.ATTENDING, 
			"I could really use those traps. How can I help you?",
			null);

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("rodent trap", 20));
		reward.add(new EquipItemAction("Klaas's note", 1, true));
		reward.add(new IncreaseXPAction(1000));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));
		reward.add(new IncreaseKarmaAction(10));
		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES,
			new PlayerHasItemWithHimCondition("rodent trap"),
			ConversationStates.ATTENDING,
			"Thanks! I've got to get these set up as quickly as possible. I used to know an old #apothecary. Take this note to him. Maybe he can help you out with something.",
			new MultipleActions(reward));

		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Please hurry! I just found another box of food that's been chewed through.",
			null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Antivenom Ring Part 1",
				"Klaas, the cargo caretaker on the Athor ferry, is in need of some rodent traps.",
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
		return "AntivenomRing";
	}

	public String getTitle() {
		
		return "Antivenom Ring";
	}
	
	@Override
	public int getMinLevel() {
		return 0;
	}

	@Override
	public String getRegion() {
		return Region.ATHOR_ISLAND;
	}
	
	@Override
	public String getNPCName() {
		return "Klaas";
	}
}
