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

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.CollectRequestedItemsAction;
import games.stendhal.server.entity.npc.action.DropItemAction;
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
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.condition.TriggerInListCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.util.ItemCollection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * QUEST: Antivenom Ring
 * 
 * PARTICIPANTS:
 * <ul>
 * <li>Jameson (the retired apothecary in semos mountain)</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li>Bring Klass's note and to Jameson.</li>
 * <li>As a favor to Klaas, Jameson will help you to strengthen your medicinal ring.</li>
 * <li>Bring Jameson a medicinal ring, venom gland, 2 mandragora and 5 fairycakes.</li>
 * <li>Jameson concocts a mixture that doubles your rings' resistance against poison.</li>
 * </ul>
 * 
 * REWARD:
 * <ul>
 * <li>20000 XP</li>
 * <li>antivenom ring</li>
 * <li>Karma: 25</li>
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 */
public class AntivenomRing extends AbstractQuest {

	private static final String QUEST_SLOT = "antivenom_ring";
	
	public static final String NEEDED_ITEMS = "medicinal ring=1;venom gland=1;mandragora=2;fairy cake=5";
	
	private static final int REQUIRED_MINUTES = 30;
	
	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I have met the hermit apothecary.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("Poison is too dangerous. I do not want to get hurt.");
		}
		if (player.getQuest(QUEST_SLOT).startsWith("enhancing;")) {
			res.add("Jameson is enhancing my ring.");
		}
		else if (!"done".equals(questState)) {
			final ItemCollection missingItems = new ItemCollection();
			missingItems.addFromQuestStateString(questState);
			res.add("I still need to bring Jameson " + Grammar.enumerateCollection(missingItems.toStringList()) + ".");
		}
		else {
			res.add("I gathered all that Jameson asked for. He applied a special mixture to my ring which made it more resistant to poison. I also got some XP and karma.");
		}
		return res;
	}

	private void prepareRequestingStep() {
		final SpeakerNPC npc = npcs.get("Jameson");
        
		/**
		 * If player has Klaas's note then quest is offered
		 */
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
				new PlayerHasItemWithHimCondition("Klaas's note"),
				new NotCondition(new QuestInStateCondition(QUEST_SLOT,"start"))),
				ConversationStates.QUEST_OFFERED, 
				"Oh, a message from #Klaas. He has asked me to enhance a ring for you. I owe him a big favor. Will you gather the items I need?",
				new SetQuestAction(QUEST_SLOT, "offered"));
        
		/**
		 * Player accepts quest
		 */
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(
						new SetQuestAndModifyKarmaAction(QUEST_SLOT, NEEDED_ITEMS, 5.0),
						new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "Okay, I need you to bring me [items]."),
						new DropItemAction("Klaas's note")
						));
		
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Well, this work isn't for everyone.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
		
		/**
		 * Quest has previously been completed.
		 */
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, 
				"Thank you so much. It had been so long since I was able to enjoy a fairy cake.",
				null);
		
        /**
         * Player asks about ingredients
         */
		npc.add(
				ConversationStates.QUEST_OFFERED,
				Arrays.asList("gland", "venom gland"),
				null,
				ConversationStates.QUEST_OFFERED,
				"#Snakes have a gland in which their venom is stored.",
				null);
		
		npc.add(
				ConversationStates.QUEST_OFFERED,
				"mandragora",
				null,
				ConversationStates.ATTENDING,
				"This is my favorite of all herbs and one of the most rare. Out past Kalavan there is a hidden path in the trees. At the end you will find what you are looking for.",
				null);
		
		npc.add(
				ConversationStates.QUEST_OFFERED,
				Arrays.asList("cake", "fairy cake"),
				null,
				ConversationStates.ATTENDING,
				"Oh, they are the best treat I have ever tasted. Only the most heavenly creatures could make such angelic food.",
				null);
	}

	private void prepareBringingStep() {
		final SpeakerNPC npc = npcs.get("Jameson");

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
				new QuestActiveCondition(QUEST_SLOT)),
				ConversationStates.QUESTION_2,
				"Hello again! Did you bring me the #items I requested?",
				null);
		
		/* player asks what exactly is missing (says items) */
		npc.add(ConversationStates.QUESTION_2,
				"items",
				null,
				ConversationStates.QUESTION_2,
				null,
				new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "I need [items]. Did you bring something?"));

		/* player says he has a required item with him (says yes) */
		npc.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.QUESTION_2, "What did you bring?",
				null);

		// Returned too early; still working
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
				new QuestStateStartsWithCondition(QUEST_SLOT, "enhancing;"),
				new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES))),
				ConversationStates.IDLE,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, REQUIRED_MINUTES, "I have not finished with the ring. Please check back in" + "."));
		
		/* player says he didn't bring any items (says no) */
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.NO_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Ok, no rush.", 
				null);

		/* player says he didn't bring any items to different question */
		npc.add(ConversationStates.QUESTION_2,
				ConversationPhrases.NO_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Ok, no rush.",
				null);

		npc.add(ConversationStates.IDLE, 
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
				new QuestCompletedCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING, 
				"The quest is done!!!!",
				null);
		
		// player offers item that isn't in the list.
		npc.add(ConversationStates.QUESTION_2, "",
			new NotCondition(new TriggerInListCondition(NEEDED_ITEMS)),
			ConversationStates.QUESTION_2,
			"I don't believe I asked for that.", null);

		ChatAction enhanceAction = new MultipleActions (
		new SetQuestAction(QUEST_SLOT, "enhancing"),
		new SetQuestToTimeStampAction(QUEST_SLOT, 1),
		new SayTextAction("Thank you. I'll get to work on your new ring right after I enjoy a few of these fairy cakes. Please come back in " + REQUIRED_MINUTES + " minutes.")
		);
		
		/* add triggers for the item names */
		final ItemCollection items = new ItemCollection();
		items.addFromQuestStateString(NEEDED_ITEMS);
		for (final Map.Entry<String, Integer> item : items.entrySet()) {
			npc.add(ConversationStates.QUESTION_2,
					item.getKey(),
					null,
					ConversationStates.QUESTION_2,
					null,
					new CollectRequestedItemsAction(
							item.getKey(),
							QUEST_SLOT,
							"Excellent! Do you have anything else with you?",
							"You brought me that already.",
							enhanceAction,
							ConversationStates.ATTENDING
							)
			);
		}

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new IncreaseXPAction(20000));
		reward.add(new IncreaseKarmaAction(25.0));
		reward.add(new EquipItemAction("antivenom ring", 1, true));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestStateStartsWithCondition(QUEST_SLOT, "forging;"),
						new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)),
			ConversationStates.IDLE, 
			"I have finished enhancing your ring. Now I'll finish the rest of my fairy cakes.", 
			new MultipleActions(reward));
		
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Antivenom Ring",
				"As a favor to an old friend Jameson, the apothecary, will strengthen the medicinal ring.",
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
		
		return "AntivenomRing";
	}
	
	@Override
	public int getMinLevel() {
		return 0;
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_SURROUNDS;
	}
	
	@Override
	public String getNPCName() {
		return "Jameson";
	}
}
