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
import java.util.List;
import java.util.Map;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.CollectRequestedItemsAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayRequiredItemsFromCollectionAction;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.util.ItemCollection;

/**
 * QUEST: Mixture for Ortiv
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Ortiv Milquetoast, the retired teacher who lives in the Kirdneh River house</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Ortiv asks you for some ingredients for a mixture which will help him to keep the assassins and bandits in the cellar</li>
 * <li>Find the ingredients</li>
 * <li>Take the ingredients back to Ortiv</li>
 * <li>Ortiv gives you a reward</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>karma +35</li>
 * <li>5000 XP</li>
 * <li>a bounded assassin dagger</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 *
 * @author Vanessa Julius
 */
public class MixtureForOrtiv extends AbstractQuest {

	public static final String QUEST_SLOT = "mixture_for_ortiv";

	/**
	 * required items for the quest.
	 */
	protected static final String NEEDED_ITEMS = "flask=1;arandula=2;red lionfish=10;kokuda=1;toadstool=12;licorice=2;apple=10;wine=30;garlic=2;pestle and mortar=1";

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I met Ortiv Milquetoast, a retired teacher in his house at Kirdneh River and asked him for a quest.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("I don't want to help Ortiv at the moment. He should go out and take the ingredients by himself.");
		} else if (!"done".equals(questState)) {
			final ItemCollection missingItems = new ItemCollection();
			missingItems.addFromQuestStateString(questState);
			res.add("I still need to bring Ortiv " + Grammar.enumerateCollection(missingItems.toStringList()) + ".");
		} else {
			res.add("I helped Ortiv. Now he can sleep safe again in his bed. He rewarded me with some XP and an assassin dagger for my use.");
		}
		return res;
	}

	private void prepareRequestingStep() {
		final SpeakerNPC npc = npcs.get("Ortiv Milquetoast");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new LevelGreaterThanCondition(2),
					new QuestNotStartedCondition(QUEST_SLOT),
					new NotCondition(new QuestInStateCondition(QUEST_SLOT,"rejected"))),
			ConversationStates.QUESTION_1,
			"Ohh a stranger found my hidden house, welcome! Maybe you can help me with something?", null);

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT,"rejected")),
			ConversationStates.QUEST_OFFERED,
			"Hey, did you think about helping me again? Will you do it?", null);

		npc.add(
			ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES,
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"I currently work on a mixture to keep the rowdy gang downstairs... Maybe you can help me later with getting me some of the #ingredients I'll need.",
			null);

		npc.add(
			ConversationStates.QUESTION_1,
			ConversationPhrases.NO_MESSAGES,
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"I currently work on a mixture to keep the rowdy gang downstairs... Maybe you can help me later with getting me some of the #ingredients I'll need.",
			null);

		npc.add(
			ConversationStates.ATTENDING,
			"ingredients",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"I was a teacher for alchemy once, now I try to mix something together. I need some ingredients for that and hope that you will help me. Will you?",
			null);

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			null,
			new MultipleActions(new SetQuestAction(QUEST_SLOT, NEEDED_ITEMS),
							    new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "Oh that would be wonderful, stranger! You might save my life! Please bring me [items].")));

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"I thought you would maybe help me... But I was wrong, obviously... So wrong as with my students while I was a teacher...",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		npc.addReply("apple", "Apples are the favourite food of assassins. I saw some apple trees on the east " +
				"of semos and near to Orril and Nalwor river.");

			npc.addReply("flask", "I've heard of a young woman in Semos who sells them.");

			npc.addReply("toadstool", "Toadstools are quite poisonous. I've heard that some hunters in the forests ate a few" +
					" and felt sick for days.");

			npc.addReply("arandula", "North of Semos, near the tree grove, grows a herb called arandula as some of my old friends told me.");

			npc.addReply("red lionfish","Red lionfish are hard to find...They are clad in white stripes alternated with red, maroon, " +
					"or brown. I've heard about a place in Faiumoni where you can fish for some but be careful, every spine of the lionfish is venomous!");

			npc.addReply("kokuda","Kokuda is really hard to find. I'm glad if you can try to get one from Athor island...");

			npc.addReply("licorice", "There is a nice little bar in magic city in which a young girl sells this lovely tasting sweet.");

			npc.addReply("wine", "Mhhhmm there isn't anything better than mixing stuff together while enjoying a glass of red wine *cough* but I need it of course for my mixture as well... I bet, you can buy wine somewhere, maybe in a tavern or a bar...");

			npc.addReply("garlic", "I know, assassins and bandits aren't vampires, but I'll try to use it against them as well. There is a nice gardener in the Kalavan City Gardens who may sell some of her own grown garlic.");

			npc.addReply(Arrays.asList("pestle","mortar","pestle and mortar"), "Perhaps some baker or cook would use one of those.");
	}

	private void prepareBringingStep() {
		final SpeakerNPC npc = npcs.get("Ortiv Milquetoast");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestActiveCondition(QUEST_SLOT)),
				ConversationStates.QUESTION_2,
				"Hello again! I'm glad to see you. Did you bring me any #ingredients for my mixture?",
				null);

		/* player asks what exactly is missing (says ingredients) */
		npc.add(ConversationStates.QUESTION_2, "ingredients", null,
				ConversationStates.QUESTION_2, null,
				new SayRequiredItemsFromCollectionAction(QUEST_SLOT, "I need [items]. Did you bring something?"));

		/* player says he has a required item with him (says yes) */
		npc.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.QUESTION_2, "Awesome, what did you bring?",
				null);

		ChatAction completeAction = new  MultipleActions(
				new SetQuestAction(QUEST_SLOT, "done"),
				new SayTextAction("Thank you so much! Now I can start mixing the mixture which will hopefully keep me safe inside of my own house without the assassins and bandits coming up from downstairs. Here is an assassin dagger for you. I had to take it away from one of my students in the class once and now you can maybe fight and win against them."),
				new IncreaseXPAction(5000),
				new IncreaseKarmaAction(25),
				new EquipItemAction("assassin dagger", 1 ,true)
				);
		/* add triggers for the item names */
		final ItemCollection items = new ItemCollection();
		items.addFromQuestStateString(NEEDED_ITEMS);
		for (final Map.Entry<String, Integer> item : items.entrySet()) {
			npc.add(ConversationStates.QUESTION_2, item.getKey(), null,
					ConversationStates.QUESTION_2, null,
					new CollectRequestedItemsAction(
							item.getKey(), QUEST_SLOT,
							"Wonderful! Did you bring anything else with you?", "You brought me that ingredient already.",
							completeAction, ConversationStates.ATTENDING));
		}

		/* player says he didn't bring any items (says no) */
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.NO_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Ok, well I have to be a bit more patient then. Just let me know if I can #help you somehow instead.",
				null);

		/* player says he didn't bring any items to different question */
		npc.add(ConversationStates.QUESTION_2,
				ConversationPhrases.NO_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Ok, well I have to be a bit more patient then. Just let me know if I can #help you somehow instead.", null);

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestCompletedCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING,
				"Thank you so much! I can sleep safely and calm again now! You rescued me!", null);
	}


	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Mixture for Ortiv",
				"Ortiv needs some ingredients for a mixture which will help him to keep the assassins and bandits in the cellar.",
				true);
		prepareRequestingStep();
		prepareBringingStep();
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "MixtureForOrtiv";
	}

	public String getTitle() {

		return "Mixture for Ortiv";
	}

	@Override
	public String getNPCName() {
		return "Ortiv Milquetoast";
	}

	@Override
	public String getRegion() {
		return Region.KIRDNEH;
	}
}
