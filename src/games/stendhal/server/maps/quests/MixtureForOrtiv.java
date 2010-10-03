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

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.ItemCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	protected static final String NEEDED_ITEMS = "flask=1;arandula=2;red lionfish=10;kokuda=1;toadstool=12;licorice=2;apple=10;wine=30;garlic=2";

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I met Ortiv Milquetoast, a retired teacher in his house at Kirdneh River.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("I don't want to help Ortiv at the moment. He should go out and take the ingredients by himself.");
		}
		if ("done".equals(questState)) {
			res.add("I helped Ortiv. Now he can sleep save again in his bed. He rewarded me with some XP and an assassin dagger for my use.");
		}
		return res;
	}

	private void prepareRequestingStep() {
		final SpeakerNPC npc = npcs.get("Ortiv Milquetoast");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(
					new LevelGreaterThanCondition(2),
					new QuestNotStartedCondition(QUEST_SLOT),
					new NotCondition(new QuestInStateCondition(QUEST_SLOT,"rejected"))),
			ConversationStates.QUESTION_1, 
			"Ohh a stranger found my hidden house, welcome! Maybe you can help me with something?", null);

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT,"rejected"),
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
			new MultipleActions(new SetQuestAndModifyKarmaAction(QUEST_SLOT, NEEDED_ITEMS, 5.0),
								new ChatAction() {
									public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
										raiser.say("Oh that will be awesome, stranger! You can maybe rescue my life with that! Please bring me those ingredients: " 
												   + Grammar.enumerateCollection(getMissingItems(player).toStringListWithHash()) + ".");
									}}));

		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"I thought you will maybe help me... But I was wrong, obviously... So wrong as with my students while I was a teacher...",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		npc.add(
				ConversationStates.ATTENDING,
				"apple",
				null,
				ConversationStates.ATTENDING,
				"Apples are the favourite dish of assassins. I saw some apple trees on the east of semos and near to Orril and Nalwor river.",
				null);

			npc.add(
				ConversationStates.ATTENDING,
				"flask",
				null,
				ConversationStates.ATTENDING,
				"I've heard of a young woman in Semos who sells them.",
				null);

			npc.add(
				ConversationStates.ATTENDING,
				"toadstool",
				null,
				ConversationStates.ATTENDING,
				"Toadstools are quite poisonous. I've heard that some hunters in the forests ate a few ones and felt sick for days.",
				null);

			npc.add(
				ConversationStates.ATTENDING,
				"arandula",
				null,
				ConversationStates.ATTENDING,
				"North of Semos, near the tree grove, grows a herb called arandula as some of my old friends told me.",
				null);
			
			npc.add(
					ConversationStates.ATTENDING,
					"red lionfish",
					null,
					ConversationStates.ATTENDING,
					"Red lionfishs are hard to find...They are clad in white stripes alternated with red, maroon, or brown. I've heard about a place in Faiumoni where you can fish for some but be careful, every spine of the lionfish is venomous!",
					null);
			
			npc.add(
					ConversationStates.ATTENDING,
					"kokuda",
					null,
					ConversationStates.ATTENDING,
					"Kokuda is really hard to find. I'm glad if you can try to get one from Athor island...",
					null);

			npc.add(
					ConversationStates.ATTENDING,
					"licorice",
					null,
					ConversationStates.ATTENDING,
					"There is a nice little bar in magic city in which a young girl sells these awesome tasting sweets.",
					null);
			
			npc.add(
					ConversationStates.ATTENDING,
					"glasses of wine",
					null,
					ConversationStates.ATTENDING,
					"Mhhhmm there isn't anything better than mixing stuff together while enjoying a glass of red wine *cough* but I need it of course for my mixture as well... I bet, you can buy wine somewhere, maybe in a tavern or a bar...",
					null);
			
			npc.add(
					ConversationStates.ATTENDING,
					"garlic",
					null,
					ConversationStates.ATTENDING,
					"I know, assassins and bandits aren't vampires, but I'll try to use it against them as well. There is a nice gardener in the Kalavan City Gardens who may sell some of her own planted garlic.",
					null);
	}

	private void prepareBringingStep() {
		final SpeakerNPC npc = npcs.get("Ortiv Milquetoast");
	
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.QUESTION_2,
				"Hello again! I'm glad to see you. Did you bring me any #ingredients for my mixture?",
				null);

		/* player asks what exactly is missing (says ingredients) */
		npc.add(ConversationStates.QUESTION_2, "ingredients", null,
				ConversationStates.QUESTION_2, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						final List<String> needed = getMissingItems(player).toStringListWithHash();
						raiser.say("I need "
								+ Grammar.enumerateCollection(needed)
								+ ". Did you bring something?");
					}
				});

		/* player says he has a required item with him (says yes) */
		npc.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.QUESTION_2, "Awesome, what did you bring?",
				null);

		/* create the ChatAction used for item triggers */
		final ChatAction itemsChatAction = new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
                final String item = sentence.getTriggerExpression().getNormalized();
			    ItemCollection missingItems = getMissingItems(player);
				final Integer missingCount = missingItems.get(item);

				if ((missingCount != null) && (missingCount > 0)) {
					if (dropItems(player, item, missingCount)) {
						missingItems = getMissingItems(player);

						if (missingItems.size() > 0) {
							raiser.say("Wonderful! Did you bring anything else with you?");
						} else {
							raiser.say("Thank you so much! Now I can start mixing the mixture which will hopefully keep me save inside of my own house without the assassins and bandits comming up from downstairs. Here is an assassin dagger for you. I had to take it away from one of my students in the class once and now you can maybe fight and win against them.");
							player.setQuest(QUEST_SLOT, "done");
							final Item reward = (Item) SingletonRepository.getEntityManager().getItem("assassin dagger");
							player.equipOrPutOnGround(reward);
							reward.setBoundTo(player.getName());
							player.addXP(5000);
							player.notifyWorldAboutChanges();
							player.addKarma(25.0);
							raiser.setCurrentState(ConversationStates.ATTENDING);
						}
					} else {
						raiser.say("Oh, you don't have " + item + " with you.");
					}
				} else {
					raiser.say("You brought me that ingredient already.");
				}
			}
		};

		/* add triggers for the item names */
		final ItemCollection items = new ItemCollection();
		items.addFromQuestStateString(NEEDED_ITEMS);
		for (final Map.Entry<String, Integer> item : items.entrySet()) {
			npc.add(ConversationStates.QUESTION_2, item.getKey(), null,
					ConversationStates.QUESTION_2, null, itemsChatAction);
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
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, 
				"Thank you so much! I can sleep savely and calm again now! You rescued me!", null);
		
	}
	
	/**
	 * Returns all items that the given player still has to bring to complete the quest.
	 *
	 * @param player The player doing the quest
	 * @return A list of item names
	 */
	private ItemCollection getMissingItems(final Player player) {
		final ItemCollection missingItems = new ItemCollection();

		missingItems.addFromQuestStateString(player.getQuest(QUEST_SLOT));

		return missingItems;
	}

	/**
	 * Drop specified amount of given item. If player doesn't have enough items,
	 * all carried ones will be dropped and number of missing items is updated.
	 *
	 * @param player
	 * @param itemName
	 * @param itemCount
	 * @return true if something was dropped
	 */
	private boolean dropItems(final Player player, final String itemName, int itemCount) {
		boolean result = false;

		 // parse the quest state into a list of still missing items
		final ItemCollection itemsTodo = new ItemCollection();

		itemsTodo.addFromQuestStateString(player.getQuest(QUEST_SLOT));

		if (player.drop(itemName, itemCount)) {
			if (itemsTodo.removeItem(itemName, itemCount)) {
				result = true;
			}
		} else {
			/*
			 * handle the cases the player has part of the items or all divided
			 * in different slots
			 */
			final List<Item> items = player.getAllEquipped(itemName);
			if (items != null) {
				for (final Item item : items) {
					final int quantity = item.getQuantity();
					final int n = Math.min(itemCount, quantity);

					if (player.drop(itemName, n)) {
						itemCount -= n;

						if (itemsTodo.removeItem(itemName, n)) {
							result = true;
						}
					}

					if (itemCount == 0) {
						result = true;
						break;
					}
				}
			}
		}

		 // update the quest state if some items are handed over
		if (result) {
			player.setQuest(QUEST_SLOT, itemsTodo.toStringForQuestState());
		}

		return result;
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Mixture for Ortiv",
				"Ortiv asks you for some ingredients for a mixture which will help him to keep the assassins and bandits in the cellar.",
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
	
}
