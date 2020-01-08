/***************************************************************************
 *                   (C) Copyright 2019 - Stendhal                         *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.antivenom_ring;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.CollectRequestedItemsAction;
import games.stendhal.server.entity.npc.action.DropInfostringItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayRequiredItemsFromCollectionAction;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasInfostringItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.condition.TriggerInListCondition;

public class ApothecaryStage extends AVRStage {
	private final SpeakerNPC apothecary;

	/* infostring that identifies note item */
	private static final String NOTE_INFOSTRING = "note to apothecary";

	/* items taken to apothecary to create antivenom */
	private static final String MIX_ITEMS = "cobra venom=1;mandragora=2;kokuda=1;fairy cake=20";
	private static final List<String> MIX_NAMES = Arrays.asList("cobra venom", "mandragora", "kokuda", "fairy cake");

	// time required (in minutes) to mix the antivenom
	private static final int MIX_TIME = 30;

	private static final String QUEST_STATE_NAME = "mixing";

	public ApothecaryStage(final String npcName, final String questName) {
		super(questName);

		apothecary = SingletonRepository.getNPCList().get(npcName);
	}

	public static String getMixItems() {
		return MIX_ITEMS;
	}

	@Override
	public void addToWorld() {
		addRequestQuestDialogue();
		addGatheringItemsDialogue();
		addBusyMixingDialogue();
		addDoneMixingDialogue();
		addQuestDoneDialogue();
		addGeneralResponsesDialogue();
	}


	/**
	 * Conversation states for NPC before quest is active.
	 */
	private void addRequestQuestDialogue() {
		// Player asks for quest without having Klass's note
		apothecary.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
						new NotCondition(new PlayerHasInfostringItemWithHimCondition("note", NOTE_INFOSTRING)),
						new QuestNotStartedCondition(questName)),
				ConversationStates.ATTENDING,
				"I'm sorry, but I'm much too busy right now. Perhaps you could talk to #Klaas.",
				null);

		// Player speaks to apothecary while carrying note.
		apothecary.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new GreetingMatchesNameCondition(apothecary.getName()),
						new PlayerHasInfostringItemWithHimCondition("note", NOTE_INFOSTRING),
						new QuestNotStartedCondition(questName)),
				ConversationStates.QUEST_OFFERED,
				"Oh, a message from Klaas. Is that for me?",
				null);

		// Player explicitly requests "quest" while carrying note (in case note is dropped before speaking to apothecary).
		apothecary.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
						new GreetingMatchesNameCondition(apothecary.getName()),
						new PlayerHasInfostringItemWithHimCondition("note", NOTE_INFOSTRING),
						new QuestNotStartedCondition(questName)),
				ConversationStates.QUEST_OFFERED,
				"Oh, a message from Klaas. Is that for me?",
				null);

		// Player accepts quest
		apothecary.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				new PlayerHasInfostringItemWithHimCondition("note", NOTE_INFOSTRING),
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(
						new SetQuestAction(questName, MIX_ITEMS),
						new IncreaseKarmaAction(5.0),
						new DropInfostringItemAction("note", NOTE_INFOSTRING),
						new SayRequiredItemsFromCollectionAction(questName,
								"Klaas has asked me to assist you. I can mix an antivenom that can be infused into a ring to increase its resistance to poison."
								+ " I need you to bring me [items].  Do you have any of those with you?",
								false)
				)
		);

		// Player accepts quest but dropped note
		apothecary.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(
					new NotCondition(new QuestInStateCondition(questName, "ringmaker")),
					new NotCondition(new PlayerHasInfostringItemWithHimCondition("note", NOTE_INFOSTRING))
				),
				ConversationStates.ATTENDING,
				"Okay then, I will need you too... wait, where did that note go?",
				null
		);

		// Player tries to leave without accepting/rejecting the quest
		apothecary.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.GOODBYE_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				"That is not a #yes or #no answer. I said, Is that note you are carrying for me?",
				null);

		// Player rejects quest
		apothecary.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				// NPC walks away
				ConversationStates.IDLE,
				"Oh, well, carry on then.",
				new SetQuestAndModifyKarmaAction(questName, "rejected", -5.0));
	}


	/**
	 * Conversation states for NPC while quest is active.
	 */
	private void addGatheringItemsDialogue() {
		final ChatCondition gatheringStateCondition = new AndCondition(
				new QuestActiveCondition(questName),
				new NotCondition(new QuestInStateCondition(questName, 0, QUEST_STATE_NAME)),
				new NotCondition(new QuestInStateCondition(questName, 0, RingMakerStage.QUEST_STATE_NAME)),
				new NotCondition(new QuestInStateCondition(questName, 0, "ringmaker")));

		// Player asks for quest after it is started
		apothecary.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				gatheringStateCondition,
				ConversationStates.ATTENDING,
				null,
				new SayRequiredItemsFromCollectionAction(questName, "I am still waiting for you to bring me [items]. Do you have any of those with you?"));

		// Jameson is waiting for items
		apothecary.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				gatheringStateCondition,
				ConversationStates.ATTENDING,
				"Hello again! Did you bring me the #items I requested?",
				null);

		// player asks what is missing (says "items")
		apothecary.add(ConversationStates.ATTENDING,
				Arrays.asList("item", "items", "ingredient", "ingredients"),
				gatheringStateCondition,
				ConversationStates.ATTENDING,
				null,
				new SayRequiredItemsFromCollectionAction(questName, "I need [items]. Did you bring something?", false));

		// player says has a required item with him (says "yes")
		apothecary.add(ConversationStates.ATTENDING,
				ConversationPhrases.YES_MESSAGES,
				gatheringStateCondition,
				ConversationStates.QUESTION_2,
				"What did you bring?",
				null);

		// Player says has required items (alternate conversation state)
		apothecary.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				gatheringStateCondition,
				ConversationStates.QUESTION_2,
				"What did you bring?",
				null);

		// player says does not have a required item with him (says "no")
		apothecary.add(ConversationStates.ATTENDING,
				ConversationPhrases.NO_MESSAGES,
				gatheringStateCondition,
				ConversationStates.IDLE,
				null,
				new SayRequiredItemsFromCollectionAction(questName, "Okay. I still need [items]", false));

		// Players says does not have required items (alternate conversation state)
		apothecary.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				gatheringStateCondition,
				ConversationStates.IDLE,
				null,
				new SayRequiredItemsFromCollectionAction(questName, "Okay. I still need [items]"));

		List<String> GOODBYE_NO_MESSAGES = new LinkedList<>(ConversationPhrases.GOODBYE_MESSAGES);
		GOODBYE_NO_MESSAGES.addAll(ConversationPhrases.NO_MESSAGES);

		// player says "bye" while listing items
		apothecary.add(ConversationStates.QUESTION_2,
				GOODBYE_NO_MESSAGES,
				gatheringStateCondition,
				ConversationStates.IDLE,
				null,
				new SayRequiredItemsFromCollectionAction(questName, "Okay. I still need [items]", false));

		// player offers item that isn't in the list.
		apothecary.add(ConversationStates.QUESTION_2, "",
			new AndCondition(
					gatheringStateCondition,
					new NotCondition(new TriggerInListCondition(MIX_NAMES))),
			ConversationStates.QUESTION_2,
			"I don't believe I asked for that.", null);

		ChatAction mixAction = new MultipleActions (
		new SetQuestAction(questName, QUEST_STATE_NAME + ";" + Long.toString(System.currentTimeMillis())),
		new SayTextAction("Thank you. I'll get to work on mixing the antivenom right after I enjoy a few of these fairy cakes. Please come back in "
				+ Integer.toString(MIX_TIME) + " minutes.")
		);

		/* add triggers for the item names */
		for (final String iName : MIX_NAMES) {
			apothecary.add(ConversationStates.QUESTION_2,
					iName,
					gatheringStateCondition,
					ConversationStates.QUESTION_2,
					null,
					new CollectRequestedItemsAction(
							iName,
							questName,
							"Excellent! Do you have anything else with you?",
							"You brought me that already.",
							mixAction,
							ConversationStates.IDLE));
		}
	}


	private void addBusyMixingDialogue() {
		// Returned too early; still working
		apothecary.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new GreetingMatchesNameCondition(apothecary.getName()),
						new QuestStateStartsWithCondition(questName, QUEST_STATE_NAME),
						new NotCondition(new TimePassedCondition(questName, 1, MIX_TIME))),
				ConversationStates.IDLE,
				null,
				new SayTimeRemainingAction(questName, 1, MIX_TIME, "I have not finished mixing the antivenom. Please check back in "));

		final List<ChatAction> mixReward = new LinkedList<ChatAction>();
		mixReward.add(new IncreaseXPAction(1000));
		mixReward.add(new IncreaseKarmaAction(50.0));
		mixReward.add(new EquipItemAction("antivenom", 1, true));
		mixReward.add(new SetQuestAction(questName, "ringmaker"));
		mixReward.add(new SetQuestAction(questName + "_extract", null)); // clear sub-quest slot

		apothecary.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(apothecary.getName()),
						new QuestInStateCondition(questName, 0, QUEST_STATE_NAME),
						new TimePassedCondition(questName, 1, MIX_TIME)
				),
			ConversationStates.IDLE,
			"I have finished mixing the antivenom. Ognir is a skilled ring smith. He can infuse the antivenom into rings."
			+ " Ask him about an #'antivenom ring'. Now I'll finish the rest of my fairy cakes if you dont mind.",
			new MultipleActions(mixReward));
	}

	/**
	 * Conversation states for NPC after antivenom has been acquired
	 */
	private void addDoneMixingDialogue() {
		final ChatCondition missingAntivenom = new AndCondition(
				new QuestInStateCondition(questName, 0, "ringmaker"),
				new NotCondition(new PlayerHasItemWithHimCondition("antivenom")));

		apothecary.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(
				new QuestInStateCondition(questName, 0, "ringmaker"),
				new PlayerHasItemWithHimCondition("antivenom")
			),
			ConversationStates.IDLE,
			"Have you not been to see the ring maker Ognir?",
			null);

		// player lost antivenom
		apothecary.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				missingAntivenom,
				ConversationStates.QUEST_OFFERED,
				"What's this? You have lost the antivenom? I can mix another batch, but I will need you to gather the ingredients again. Do you want me to mix another antivenom?",
				null);

		// NPC offers to mix another vial of antivenom

		// this is so player doesn't lose karma by saying "no"
		apothecary.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			missingAntivenom,
			ConversationStates.IDLE,
			"Oh, well, come back to me if you can't find your antivenom.",
			null);

		apothecary.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			missingAntivenom,
			ConversationStates.ATTENDING,
			"Okay, I need you to bring me ",
			new MultipleActions(
				new SetQuestAction(questName, MIX_ITEMS),
				new SayRequiredItemsFromCollectionAction(questName,
					"Okay, I need you to bring me [items]. Do you have any of those with you?",
					false))
			);
	}

	/**
	 * Conversation states for NPC after quest is completed.
	 */
	private void addQuestDoneDialogue() {
		// Quest has previously been completed.
		apothecary.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(questName),
				ConversationStates.QUESTION_1,
				"Thank you so much. It had been so long since I was able to enjoy a fairy cake. Are you enjoying your ring?",
				null);

		// Player is enjoying the ring
		apothecary.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new QuestCompletedCondition(questName),
				ConversationStates.ATTENDING,
				"Wonderful!",
				null);

		// Player is not enjoying the ring
		apothecary.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				new QuestCompletedCondition(questName),
				ConversationStates.ATTENDING,
				"Oh, that's too bad.",
				null);
	}

	private void addGeneralResponsesDialogue() {
		// responses to quest related items/ingredients
		apothecary.addReply(
				Arrays.asList("ring", "rings"),
				"There are many types of rings.");
		apothecary.addReply(
				Arrays.asList("medicinal ring", "medicinal rings"),
				"Some poisonous creatures carry them.");
		apothecary.addReply(
				Arrays.asList("antivenom ring", "antivenom rings"),
				"If you bring me what I need I may be able to strengthen a #medicinal #ring.");
		/* this item is not available
		apothecary.addReply(
				Arrays.asList("antitoxin ring", "antitoxin rings", "gm antitoxin ring", "gm antitoxin rings"),
				"Heh! This is the ultimate protection against poisoning. Good luck getting one!");
		*/
		apothecary.addReply(
				Arrays.asList("venom", "cobra venom"),
				"Someone who specializes with animals might know how to obtain some. I suggest visiting the "
				+ "sanctuary in Ados.");
		apothecary.addReply(
				Arrays.asList("gland", "venom gland", "glands", "venom glands"),
				"Some #snakes have a gland in which their venom is stored.");
		apothecary.addReply(
				Arrays.asList("snake", "snakes", "cobra", "cobras"),
				"I've heard rumor of a newly discovered pit full of snakes somewhere in Ados. But I've never "
					+ "searched for it myself. That kind of work is better left to adventurers.");
		apothecary.addReply(
				Arrays.asList("mandragora", "mandragoras", "root of mandragora", "roots of mandragora",
						"root of mandragoras", "roots of mandragoras"),
				"This is my favorite of all the herbs and one of the most rare. There are only a few places "
				+ "in Faimouni where it grows. Keep a vigilant eye, or you will pass them right up.");
		apothecary.addReply(
				Arrays.asList("cake", "fairy cake"),
				"Oh, fairy cakes are the best treat I have ever tasted. Only the most heavenly creatures "
					+ "could make such angelic food.");
	}
}
