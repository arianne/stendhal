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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.MathHelper;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropRecordedItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayRequiredItemAction;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.action.StartRecordingRandomItemCollectionAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasRecordedItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Zoo Food
 * <p>
 * PARTICIPANTS:
 * <ul>
 * <li> Katinka, the keeper at the Ados Wildlife Refuge
 * <li> Dr.Feelgood, the veterinary
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Katinka asks you for food for the animals.
 * <li> You get the food, e.g. by killing other animals ;) or harvesting it
 * <li> You give the food to Katinka.
 * <li> Katinka thanks you.
 * <li> You can then buy cheap medicine from Dr. Feelgood.
 * </ul>
 *
 * REWARD: <ul>
 * <li> 200 XP
 * <li> 7 Karma
 * <li> Supply for cheap medicine and free pet healing for one week
 * </ul>
 * REPETITIONS: - Once per week.
 */
public class ZooFood extends AbstractQuest {

	private static final int REQUIRED_HAM = 10;
	private static final int DELAY = MathHelper.MINUTES_IN_ONE_WEEK;

	private static final String QUEST_SLOT = "zoo_food";

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
		res.add("I have met Katinka at the zoo.");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("I do not have the time for smelly animals and their food issues.");
			return res;
		}
		res.add("I don't want to see those poor animals die! I'll help get the food!");
		if (questState.startsWith("start;")) {
			String questItem = player.getRequiredItemName(QUEST_SLOT,1);
			int amount = player.getRequiredItemQuantity(QUEST_SLOT,1);
			if (!player.isEquipped(questItem, amount)) {
				res.add(String.format("I have been asked to fetch " +Grammar.quantityplnoun(amount, questItem, "a") + " for the animals."));
			} else {
				res.add(String.format("I have " + Grammar.quantityplnoun(amount, questItem, "a")
						+ " to feed the animals, and should deliver " + Grammar.itthem(amount) + " to Katinka."));
			}
		}
		if (isCompleted(player)) {
			if(new TimePassedCondition(QUEST_SLOT, 1, DELAY).fire(player, null, null)) {
				res.add("The animals are hungry again! I need to ask Katinka what they need.");
			} else {
				res.add("The animals are not hungry! Yay, me!");
			}
		}
		return res;
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Katinka");

        // Player has never done the zoo quest
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestNotStartedCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING,
				"Welcome to the Ados Wildlife Refuge! We rescue animals from being slaughtered by evil adventurers. But we need help... maybe you could do a #task for us?",
				null
		);

        // Player returns within one week of completing quest
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestCompletedCondition(QUEST_SLOT),
						new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, DELAY))),
				ConversationStates.ATTENDING, "Welcome back to the Ados Wildlife Refuge! Thanks again for rescuing our animals!",
				null
		);

        // Player returns and longer than a week has passed, ask to help again
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestCompletedCondition(QUEST_SLOT),
						new TimePassedCondition(QUEST_SLOT, 1, DELAY)),
				ConversationStates.QUEST_OFFERED, "Welcome back to the Ados Wildlife "
                + "Refuge! Our animals are hungry again, can you bring some more food please?",
                null);


        // Player has never done the zoo quest, player asks what the task was
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED, "Our animals are hungry. We need " +
						"more food to feed them. Can you help us?",
				null);

	    final Map<String,Integer> items = new HashMap<String, Integer>();
	    items.put("apple",5);
	    items.put("bread",3);
	    items.put("button mushroom",5);
	    items.put("carrot",5);
	    items.put("cheese",10);
	    items.put("cherry",5);
	    items.put("egg",5);
		items.put("grain",20);
	    items.put("ham",10);
	    items.put("honey",5);
		items.put("meat",15);
		items.put("porcini",5);
		items.put("roach",3);
		items.put("salad",10);
		items.put("spinach",5);



        // Player has done quest before and agrees to help again
		npc.add(ConversationStates.QUEST_OFFERED, ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING, null,
                new MultipleActions(new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start;", 5.0),
				new StartRecordingRandomItemCollectionAction(QUEST_SLOT, 1, items, "Oh, thank you! Please help us by"
                + " bringing [item] as soon as you can."))
		);


		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED, ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING, "Oh dear... I guess we're going to have to feed them with the deer...",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0)
		);

        // Player returns within one week of completing quest
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
                                 new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, DELAY))),
				ConversationStates.ATTENDING, null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, DELAY, "Thanks, we have enough food to feed the animals here for another"));

		// player requests quest while quest still active
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				null,
				new SayRequiredItemAction(QUEST_SLOT, 1, "You are already on a quest to fetch [item]."));
	}

	private void step_2() {
		// Just find the food somewhere. It isn't a quest
	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("Katinka");

		// compatibility with old quests:
		// player returns while initial quest is still active, set it to match the new way
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "start")),
				ConversationStates.QUEST_ITEM_BROUGHT,
				"Welcome back! Have you brought the "
						+ Grammar.quantityplnoun(REQUIRED_HAM, "ham", "") + "?",
			new SetQuestAction(QUEST_SLOT,"start;ham=10"));

		// player returns while quest is active
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestStateStartsWithCondition(QUEST_SLOT, "start;")),
				ConversationStates.QUEST_ITEM_BROUGHT,
				null,
				new SayRequiredItemAction(QUEST_SLOT, 1, "Welcome back! Have you brought the [item]?"));

		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new DropRecordedItemAction(QUEST_SLOT,1));
		actions.add(new SetQuestAndModifyKarmaAction(QUEST_SLOT, "done;1", 5.0));
		actions.add(new SetQuestToTimeStampAction(QUEST_SLOT, 1));
		actions.add(new IncreaseXPAction(200));

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES,
			new PlayerHasRecordedItemWithHimCondition(QUEST_SLOT,1),
			ConversationStates.ATTENDING, "Thank you! You have rescued our rare animals.",
			new MultipleActions(actions));

        npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
        		ConversationPhrases.YES_MESSAGES,
        		new NotCondition(new PlayerHasRecordedItemWithHimCondition(QUEST_SLOT,1)),
        		ConversationStates.ATTENDING, null,
        		new SayRequiredItemAction(QUEST_SLOT, 1, "*sigh* I SPECIFICALLY said that we need [item]!")
        		);

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT, ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING, "Well, hurry up! These rare animals are starving!",
				null);
	}

	private void step_4() {
		final SpeakerNPC npc = npcs.get("Dr. Feelgood");

		// player returns while quest is still active
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestCompletedCondition(QUEST_SLOT),
						new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, DELAY))),
			ConversationStates.ATTENDING, "Hello! Now that the animals have enough food, they don't get sick that easily, and I have time for other things. How can I help you?",
				null
		);

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new OrCondition(new QuestNotCompletedCondition(QUEST_SLOT),
								new AndCondition(new QuestCompletedCondition(QUEST_SLOT), new TimePassedCondition(QUEST_SLOT, 1, DELAY))
						)),
				ConversationStates.IDLE, "Sorry, can't stop to chat. The animals are all sick because they don't have enough food. See yourself out, won't you?",
				null
		);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Zoo Food",
				"The animals at the zoo are hungry and need some food!",
				true);
		step_1();
		step_2();
		step_3();
		step_4();
	}

	@Override
	public String getName() {
		return "ZooFood";
	}

	@Override
    public boolean isRepeatable(final Player player) {
		return	new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
						 new TimePassedCondition(QUEST_SLOT,1,DELAY)).fire(player, null, null);
	}

	@Override
	public String getNPCName() {
		return "Katinka";
	}

	@Override
	public String getRegion() {
		return Region.ADOS_SURROUNDS;
	}
}
