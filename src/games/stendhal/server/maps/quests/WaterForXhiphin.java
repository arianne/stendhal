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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.action.DropInfostringItemAction;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.npc.action.InflictStatusOnNPCAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasInfostringItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * Quest to fetch water for a thirsty person.
 * You have to check it is clean with someone knowledgeable first
 *
 * @author kymara
 *
 *
 * QUEST: Water for Xhiphin
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Xhiphin Zohos </li>
 * <li> Stefan </li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Ask Xhiphin Zohos for a quest. </li>
 * <li> Get some fresh water. </li>
 * <li> Xhiphin Zohos wants to assure that the water is clean. Show the water to Stefan and he will check it. </li>
 * <li> Return the water to Xhiphin Zohos who will then enjoy it. </li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> 100 XP </li>
 * <li> some karma (5 + (5 | -5)) </li>
 * <li> 3 potions </li>
 * </ul>
 *
 * REPEATABLE:
 */

public class WaterForXhiphin extends AbstractQuest {

	// constants
	private static final String QUEST_SLOT = "water_for_xhiphin";

	/** To combine with the quest triggers */
	private static final String EXTRA_TRIGGER = "water";

	/** The delay between repeating quests.
	 * 7200 minutes */
	private static final int REQUIRED_MINUTES = 7200;

	/** How the water is marked as clean */
	private static final String CLEAN_WATER_INFOSTRING = "clean";


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void requestStep() {
		final SpeakerNPC npc = npcs.get("Xhiphin Zohos");

		// player asks about quest for first time (or rejected)
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.combine(ConversationPhrases.QUEST_MESSAGES, EXTRA_TRIGGER),
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"I'm really thirsty, could you possibly get me some fresh water please?",
				null);

		// player can repeat quest
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.combine(ConversationPhrases.QUEST_MESSAGES, EXTRA_TRIGGER),
				new AndCondition(new QuestCompletedCondition(QUEST_SLOT), new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)),
				ConversationStates.QUEST_OFFERED,
				"My throat is dry again from all this talking, could you fetch me a little more water?",
				null);

		// player can't repeat quest
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.combine(ConversationPhrases.QUEST_MESSAGES, EXTRA_TRIGGER),
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
				"Thank you! Natural spring water is best, the river that runs from Fado to Nal'wor might provide a source.",
				new MultipleActions(
				        new SetQuestAction(QUEST_SLOT, 0, "start"),
				        new IncreaseKarmaAction(5.0)));

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
		final SpeakerNPC waterNPC = npcs.get("Stefan");

		// player gets water checked
		// mark infostring of item to show it's good
		final List<ChatAction> actions = new LinkedList<ChatAction>();
		// for now Stefan is just able to check one water at a time (even from a stack) and he always says it's fine and clean
		// if you go to him with one checked and one unchecked he might just check the checked one again - depends what sits first in bag
		actions.add(new DropItemAction("water",1));
		actions.add(new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				final Item water = SingletonRepository.getEntityManager().getItem("water");
				water.setInfoString(CLEAN_WATER_INFOSTRING);
				water.setDescription("You see a bottle of fresh spring water. It's really tasty and fresh. Stefan checked it.");
				// remember the description
				water.setPersistent(true);
				player.equipOrPutOnGround(water);
			}
		});
		waterNPC.add(ConversationStates.ATTENDING,
					Arrays.asList("water", "clean", "check"),
					new PlayerHasItemWithHimCondition("water"),
					ConversationStates.ATTENDING,
					"That water looks clean to me! It must be from a pure source.",
					// take the item and give them a new one with an infostring or mark all?
					new MultipleActions(actions));

		// player asks about water but doesn't have it with them
		waterNPC.add(ConversationStates.ATTENDING,
					Arrays.asList("water", "clean", "check"),
					new NotCondition(new PlayerHasItemWithHimCondition("water")),
					ConversationStates.ATTENDING,
					"You can gather water from natural mountain springs or bigger springs like next to waterfalls. If you bring it to me I can check the purity for you.",
					null);

	}


	private void finishStep() {
		final SpeakerNPC npc = npcs.get("Xhiphin Zohos");

		// Player has got water and it has been checked
		final List<ChatAction> reward = new LinkedList<ChatAction>();
		// make sure we drop the checked water not any other water
		reward.add(new DropInfostringItemAction("water", CLEAN_WATER_INFOSTRING));
		reward.add(new EquipItemAction("potion", 3));
		reward.add(new IncreaseXPAction(100));
		reward.add(new IncrementQuestAction(QUEST_SLOT, 2, 1) );
		reward.add(new SetQuestToTimeStampAction(QUEST_SLOT,1));
		reward.add(new SetQuestAction(QUEST_SLOT, 0, "done"));
		reward.add(new IncreaseKarmaAction(5.0));
		reward.add(new InflictStatusOnNPCAction("water"));

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.combine(ConversationPhrases.QUEST_MESSAGES, EXTRA_TRIGGER),
				new AndCondition(
						new QuestActiveCondition(QUEST_SLOT),
						new PlayerHasInfostringItemWithHimCondition("water", CLEAN_WATER_INFOSTRING)),
				ConversationStates.ATTENDING,
				"Thank you ever so much! That's just what I wanted! Here, take these potions that Sarzina gave me - I hardly have use for them here.",
				new MultipleActions(reward));

        // player returns with no water at all.
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.combine(ConversationPhrases.QUEST_MESSAGES, EXTRA_TRIGGER),
				new AndCondition(
						new QuestActiveCondition(QUEST_SLOT),
						new NotCondition(new PlayerHasItemWithHimCondition("water"))),
				ConversationStates.ATTENDING,
				"I'm waiting for you to bring me some drinking water, this sun is so hot.",
				null);

        // add the other possibilities
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.combine(ConversationPhrases.QUEST_MESSAGES, EXTRA_TRIGGER),
				new AndCondition(
						new QuestActiveCondition(QUEST_SLOT),
						new PlayerHasItemWithHimCondition("water"),
						new NotCondition(new PlayerHasInfostringItemWithHimCondition("water", CLEAN_WATER_INFOSTRING))),
				ConversationStates.ATTENDING,
				"Hmm... it's not that I don't trust you, but I'm not sure that water is okay to drink. Could you go and ask #Stefan to #check it please?",
				null);

		npc.addReply("Stefan", "Stefan is a chef over in the restaurant at Fado Hotel. I'd trust him to check if anything is safe to eat or drink, he's a professional.");
		npc.addReply("check", "Sorry, I'm no expert on food or drink myself, try asking #Stefan.");

	}



	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Water for Xhiphin Zohos",
				"Xhiphin Zohos wants some nice fresh water.",
				true);
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
		res.add("Xhiphin Zohos is thirsty from standing out in the warm sun all day.");
		final String questState = player.getQuest(QUEST_SLOT, 0);
		if ("rejected".equals(questState)) {
			res.add("I told Xhiphin Zohos I didn't want to fetch water for him.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start") || isCompleted(player)) {
			res.add("I agreed to fetch some water to quench Xhiphin Zohos's thirst.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start") && player.isEquipped("water") && new NotCondition(new PlayerHasInfostringItemWithHimCondition("water", CLEAN_WATER_INFOSTRING)).fire(player, null, null) || isCompleted(player)) {
			res.add("I found a source of fresh water, but I can't be completely sure it's safe for Xhiphin to drink.");
		}
		if (new PlayerHasInfostringItemWithHimCondition("water", CLEAN_WATER_INFOSTRING).fire(player, null, null) || isCompleted(player)) {
			res.add("Stefan, the chef in Fado hotel, checked the water I collected and it is clean and safe to drink.");
		}
		// checked water was clean?
        if (isCompleted(player)) {
            if (isRepeatable(player)) {
                res.add("I took the water to Xhiphin Zohos a while ago.");
            } else {
                res.add("I took the water to Xhiphin Zohos recently and he gave me some potions.");
            }
		}
		return res;
	}

	@Override
	public String getName() {
		return "WaterForXhiphin";
	}

	@Override
	public int getMinLevel() {
		return 5;
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
				 new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)).fire(player,null, null);
	}

	@Override
	public String getRegion() {
		return Region.FADO_CITY;
	}

	@Override
	public String getNPCName() {
		return "Xhiphin Zohos";
	}

}
