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
import java.util.List;

import games.stendhal.common.MathHelper;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.AddItemToCollectionAction;
import games.stendhal.server.entity.npc.action.CollectRequestedItemsAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayRequiredItemsFromCollectionAction;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.action.StartItemsCollectionWithLimitAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.util.ItemCollection;

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
 * <li>Seremela asks you to bring a variety of flowers to restock the flower shop and 15 bottles of water to maintain them</li>
 * <li>Bring the requested amounts water and each flower type to Seremela</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>1000 XP</li>
 * <li>25 karma</li>
 * <li>5 nalwor city scrolls</li>
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
			"daisies", "lilia", "pansy", "rose", "zantedeschia");

	private static final int MAX_FLOWERS = flowerTypes.size() * 10;

	private static final int REQ_WATER = 15;

	// Time player must wait to repeat quest (3 days)
	private static final int WAIT_TIME = 3 * MathHelper.MINUTES_IN_ONE_DAY;

	// Quest NPC
	private final SpeakerNPC npc = npcs.get("Seremela");

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		String npcName = npc.getName();
		if (player.isQuestInState(QUEST_SLOT, 0, "rejected")) {
			res.add("Flowers make me sneeze.");
		} else if (!player.isQuestInState(QUEST_SLOT, 0, "done")) {
			String questState = player.getQuest(QUEST_SLOT);
			res.add("I have offered to help " + npcName + " restock the flower shop.");

			final ItemCollection remaining = new ItemCollection();
			remaining.addFromQuestStateString(questState);

			if (!remaining.isEmpty()) {
				String requestedFlowers = "I still need to bring the following flowers: " + Grammar.enumerateCollection(remaining.toStringList()) + ".";
				res.add(requestedFlowers);
			}
		} else {
            if (isRepeatable(player)) {
                res.add("It has been a while since I helped " + npcName + ". Perhaps she could use my help again.");
            } else {
                res.add(npcName + " now has a good supply of flowers.");
            }
		}

		return res;
	}


	private void setupBasicResponses() {

		List<List<String>> keywords = Arrays.asList(
				Arrays.asList("flower"),
				ConversationPhrases.HELP_MESSAGES);
		List<String> responses = Arrays.asList(
				"Aren't flowers beautiful?",
				"Hmmmm, I don't think there is anything I can help with.");

		for (int i = 0; i < responses.size(); i++) {
			npc.add(ConversationStates.ANY,
					keywords.get(i),
					new NotCondition(new QuestActiveCondition(QUEST_SLOT)),
					ConversationStates.ATTENDING,
					responses.get(i),
					null);
		}
	}

	private void setupActiveQuestResponses() {

		// Player asks to be reminded of remaining flowers required
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("flower", "remind", "what", "item", "list", "something"),
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.QUESTION_1,
				null,
				new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "I still need [items]. Did you bring any of those?"));

        npc.add(ConversationStates.QUESTION_1,
                Arrays.asList("flower", "remind", "what", "item", "list", "something"),
                new QuestActiveCondition(QUEST_SLOT),
                ConversationStates.QUESTION_1,
                null,
                new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "I still need [items]. Did you bring any of those?"));

        // Player asks to be reminded of remaining flowers required
        npc.add(ConversationStates.QUESTION_1,
                Arrays.asList("flower", "remind", "what", "item", "list"),
                new QuestActiveCondition(QUEST_SLOT),
                ConversationStates.QUESTION_1,
                null,
                new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "I still need [items]. Did you bring any of those?"));

		List<List<String>> keywords = Arrays.asList(
				Arrays.asList("daisy", "bunch of daisies", "bunches of daisies", "lilia", "pansy"),
				Arrays.asList("rose"),
				Arrays.asList("zantedeschia"),
				Arrays.asList("water", "bottle of water"),
				Arrays.asList("who", "where"),
				Arrays.asList("jenny"),
				Arrays.asList("fleur"),
				Arrays.asList("flask"),
				ConversationPhrases.HELP_MESSAGES);
		List<String> responses = Arrays.asList(
				"#Jenny carries seeds for this type of flower.",
				"#Fleur always has the nicest roses.",
				"Zantedeschia is my favorite flower. Some call them arum or calla lilies, though they are not true lilies. Ask #Jenny if she has any bulbs.",
				"I need water to keep the #flowers fresh. You'll need to find a water source and fill up some #flasks. Maybe there is someone who sells water.",
				"#Jenny knows a lot about flowers. You may be able to talk with #Fleur as well.",
				"You can find Jenny around the windmill near Semos where she mills flour.",
				"Fleur works at the market in Kirdneh.",
				"Ask the barmaid in Semos.",
				"I can #remind you of which #flowers I need. I might also be able help you figure out #where you can find some.");

		for (int f = 0; f < responses.size(); f++) {
			npc.add(ConversationStates.ANY,
					keywords.get(f),
					new QuestActiveCondition(QUEST_SLOT),
					ConversationStates.ATTENDING,
					responses.get(f),
					null);
		}
	}

	private void prepareRequestingStep() {

		// Player requests quest
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
						new NotCondition(new QuestActiveCondition(QUEST_SLOT)),
						new TimePassedCondition(QUEST_SLOT, 1, WAIT_TIME)),
				ConversationStates.QUEST_OFFERED,
				"The flower shop is running low on flowers. Will you help me restock it?",
				null);

		// Player requests quest after started
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"You still haven't brought me the #flowers I asked for.",
				null);

		// Player requests quest before wait period ended
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, WAIT_TIME)),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, WAIT_TIME, "The flowers you brought are selling quickly. I may need your help again in"));

		// Player accepts quest
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(
						new StartItemsCollectionWithLimitAction(QUEST_SLOT, 0, flowerTypes, MAX_FLOWERS),
						new AddItemToCollectionAction(QUEST_SLOT, "water", REQ_WATER),
						new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "Great! Here is what I need: [items]."))
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
		List<String> requestedItems = new ArrayList<>(flowerTypes);
		requestedItems.add("water");

		ChatAction rewardAction = new MultipleActions(
				new IncreaseXPAction(1000),
				new IncreaseKarmaAction(25.0),
				new EquipItemAction("nalwor city scroll", 5),
				new SetQuestAction(QUEST_SLOT, "done"),
				new SetQuestToTimeStampAction(QUEST_SLOT, 1),
				new SayTextAction("Thank you so much! Now I can fill all of my orders. Here are some Nalwor City scrolls to show my appreciation."));

		/* add triggers for the item names */
		for (String item : requestedItems) {
			npc.add(ConversationStates.QUESTION_1,
					item,
					new QuestActiveCondition(QUEST_SLOT),
					ConversationStates.QUESTION_1,
					null,
					new CollectRequestedItemsAction(
							item,
							QUEST_SLOT,
							"Thank you! What else did you bring?",
							"I don't need any more of those.",
							rewardAction,
							ConversationStates.IDLE
							));
		}

		// NPC asks if player brought items
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.QUESTION_1,
				"Did you bring #something for the shop?",
				null);

		// Player confirms brought flowers
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.QUESTION_1,
				"What did you bring?",
				null);

		// Player didn't bring flowers
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Don't stop to smell the roses yet. Orders are backing up. I can #remind you of what to bring.",
				null);

		// Player offers item that wasn't requested
		npc.add(ConversationStates.QUESTION_1,
				"",
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.QUESTION_1,
				"I don't think that would look good in the shop.",
				null);

		// Player says "bye" or "no" while listing flowers
		List<String> endDiscussionPhrases = new ArrayList<>(ConversationPhrases.NO_MESSAGES);
		endDiscussionPhrases.addAll(ConversationPhrases.GOODBYE_MESSAGES);

		npc.add(ConversationStates.QUESTION_1,
				endDiscussionPhrases,
				null,
				ConversationStates.IDLE,
				"Please come back when you have found some flowers.",
				null);
	}

	@Override
	public boolean isRepeatable(Player player) {
		return new AndCondition(
				new NotCondition(new QuestActiveCondition(QUEST_SLOT)),
				new TimePassedCondition(QUEST_SLOT, 1, WAIT_TIME)).fire(player, null, null);
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
		fillQuestInfo(
				getTitle(),
				getNPCName() + " needs to restock the flower shop in Nalwor City.",
				true);
		setupBasicResponses();
		setupActiveQuestResponses();
		prepareRequestingStep();
		prepareBringingStep();
	}
}
