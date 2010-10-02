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
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TriggerInListCondition;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.npc.parser.TriggerList;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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

	private List<String> missingITEMS(final Player player, final boolean hash) {
		final List<String> result = new LinkedList<String>();
		
		String doneText = player.getQuest(QUEST_SLOT);
		if (doneText == null) {
			doneText = "";
		}
		final List<String> done = Arrays.asList(doneText.split(";"));
		for (String ingredient : NEEDED_ITEMS) {
			if (!done.contains(ingredient)) {
				if (hash) {
					ingredient = "#" + ingredient;
				}
				result.add(ingredient);
			}
		}
		return result;
	}
		
	private static final List<String> NEEDED_ITEMS = Arrays.asList("flask=1;arandula=2;red lionfish=10;kokuda;toadstool=12;licorice=2;apple=10;wine=30;garlic=2");

	private static final String QUEST_SLOT = "mixture_for_ortiv";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	
	private void offerQuestStep() {
		final SpeakerNPC npc = npcs.get("Ortiv Milquetoast");
			npc.add(ConversationStates.ATTENDING,
					ConversationPhrases.QUEST_MESSAGES, 
					new QuestNotStartedCondition(QUEST_SLOT),
					ConversationStates.QUEST_OFFERED, 
					"I currently work on a mixture to keep the rowdy gang downstairs... Maybe you can help me later with getting me some of the #ingredients I'll need.",
					null);
			
		// player asks what exactly is missing
		npc.add(ConversationStates.QUEST_OFFERED, "ingredients", null,
			ConversationStates.QUEST_OFFERED, null,
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final List<String> needed = missingITEMS(player, true);
					npc.say("I need "
							+ Grammar.quantityplnoun(needed.size(),
									"ingredient", "one")
							+ " to prepare the mixture which will hopefully save me from the assassins and bandits in my cellar: "
							+ Grammar.enumerateCollection(needed)
							+ ". Will you collect them?");
				}
			});

		// player is willing to collect
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.QUESTION_1, 
			"Oh that will be awesome, stranger! You can maybe rescue my life with that! Do you have anything I need already?",
			new SetQuestAction(QUEST_SLOT, ""));

		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING,
				"I thought you will maybe help me... But I was wrong, obviously... So wrong as with my students while I was a teacher...", null);

		// players asks about the ingredients individually
		npc.add(
			ConversationStates.QUEST_OFFERED,
			Arrays.asList("flask","arandula","red lionfish","kokuda","toadstool","licorice","apple","wine","garlic"),
			null,
			ConversationStates.QUEST_OFFERED,
			"Will you fetch the ingredients?",
			null);

		// players asks about the ingredients individually
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
				Arrays.asList("toadstool"),
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
					"red wine",
					null,
					ConversationStates.ATTENDING,
					"Mhhhmm there isn't anything better than mixing stuff together while enjoying a glass of red wine *cough* but I need it of course for my mixture as well... I bet, you can buy wine somewhere, maybe in a tavern or a bar...",
					null);
			
			npc.add(
					ConversationStates.ATTENDING,
					"garlic",
					null,
					ConversationStates.ATTENDING,
					"I know, assassins and bandits aren't vampires, but I'll try to use it against them as well. There is a nice gardener in the Kalavan City Gardens who may sell some of her own planted garlic." + " So will you fetch the ingredients?",
					null);
	}

	private void step_2() {
		// Fetch the ingredients and bring them back to Ortiv.
	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("Ortiv Milquetoast");

		// player returns while quest is still active
		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new QuestStartedCondition(QUEST_SLOT), new NotCondition(new QuestStateStartsWithCondition(QUEST_SLOT, "done"))),
			ConversationStates.QUESTION_1,
			"Hello again! I'm glad to see you. Did you bring me any #ingredients for my mixture?",
			null);

		// player asks what exactly is missing
		npc.add(ConversationStates.QUESTION_1, "ingredients",
			new AndCondition(new QuestStartedCondition(QUEST_SLOT), new NotCondition(new QuestStateStartsWithCondition(QUEST_SLOT, "done"))),
			ConversationStates.QUESTION_1, null,
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final List<String> needed = missingITEMS(player, true);
					npc.say("I still need "
							+ Grammar.quantityplnoun(needed.size(),
									"ingredient", "one") + ": "
							+ Grammar.enumerateCollection(needed)
							+ ". Did you bring anything I need for my mixture?");
				}
			});

		// player says he has a required ingredient with him
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.QUESTION_1, "Great, what did you bring?", null);

		npc.add(ConversationStates.QUESTION_1, NEEDED_ITEMS, null,
			ConversationStates.QUESTION_1, null,
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final Expression item = sentence.getTriggerExpression();

					TriggerList missing = new TriggerList(missingITEMS(player, false));

					final Expression found = missing.find(item);
					if (found != null) {
						final String itemName = found.getOriginal();

						if (player.drop(itemName)) {
							// register ingredient as done
							final String doneText = player.getQuest(QUEST_SLOT);
							player.setQuest(QUEST_SLOT, doneText + ";" + itemName);

							// check if the player has brought all Food
							missing = new TriggerList(missingITEMS(player, true));

							if (missing.size() > 0) {
								npc.say("Wonderful! Did you bring anything else with you?");
							} else {
								player.addKarma(25.0);
								player.addXP(5000);
								npc.say("Here is an assassin dagger for you. I had to take it away from one of my students in the class once.");
								player.setQuest(QUEST_SLOT, "done;");
								player.notifyWorldAboutChanges();
								npc.setCurrentState(ConversationStates.ATTENDING);
							}
						} else {
							npc.say("Oh you don't have "
								+ Grammar.a_noun(itemName)
								+ " with you.");
						}
					} else {
						npc.say("You brought me that ingredient already.");
					}
				}
			});
		
		// Perhaps player wants to give all the ingredients at once
		npc.add(ConversationStates.QUESTION_1, "everything",
				null,
				ConversationStates.QUESTION_1,
				null,
				new ChatAction() {
			    public void fire(final Player player, final Sentence sentence,
					   final EventRaiser npc) {
			    	checkForAllIngredients(player, npc);
			}
		});

		// player says something which isn't in the needed food list.
		npc.add(ConversationStates.QUESTION_1, "",
			new NotCondition(new TriggerInListCondition(NEEDED_ITEMS)),
			ConversationStates.QUESTION_1,
			"I won't put that in the mixture.", null);

		// allow to say goodbye while Ortiv is listening for item names
		npc.add(ConversationStates.QUESTION_1, ConversationPhrases.GOODBYE_MESSAGES, null,
				ConversationStates.IDLE, "Bye.", null);

		npc.add(ConversationStates.ATTENDING, ConversationPhrases.NO_MESSAGES,
			new AndCondition(new QuestStartedCondition(QUEST_SLOT), new NotCondition(new QuestStateStartsWithCondition(QUEST_SLOT, "done"))),
			ConversationStates.ATTENDING,
			"Ok, well I have to be a bit more patient then.", null);

		// player says he didn't bring any Items to different question
		npc.add(ConversationStates.QUESTION_1, ConversationPhrases.NO_MESSAGES,
			new AndCondition(new QuestStartedCondition(QUEST_SLOT), new NotCondition(new QuestStateStartsWithCondition(QUEST_SLOT, "done"))),
			ConversationStates.ATTENDING, "Okay then. I'll wait for your return.",
			null);
	}

	private void checkForAllIngredients(final Player player, final EventRaiser npc) {
		List<String> missing = missingITEMS(player, false);
		for (final String item : missing) {
		if (player.drop(item)) {							
			// register ingredient as done
			final String doneText = player.getQuest(QUEST_SLOT);
			player.setQuest(QUEST_SLOT, doneText + ";"
			+ item);
			}
		}
		// check if the player has brought all ingredients
		missing = missingITEMS(player, true);
		if (missing.size() > 0) {
			npc.say("You didn't have all the ingredients I need. I still need "
							+ Grammar.quantityplnoun(missing.size(),
									"ingredient", "one") + ": "
									+ Grammar.enumerateCollection(missing)
									+ ". Please come back, when you have everything with you.");
			return;
		} else {
			// you get less XP if you did it the lazy way
			// and no karma
			player.addXP(5000);
			npc.say("Here is an assassin dagger for you. I had to take it away from one of my students in the class once.");
			player.setQuest(QUEST_SLOT, "done;");
			player.notifyWorldAboutChanges();
			npc.setCurrentState(ConversationStates.ATTENDING);
		}
	}
	
	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Mixture for Ortiv",
				"Ortiv asks you for some ingredients for a mixture which will help him to keep the assassins and bandits in the cellar",
				false);
		offerQuestStep();
		step_2();
		step_3();
	}

	@Override
	public String getName() {
		return "MixtureForOrtiv";
	}
	
	
	@Override
	public int getMinLevel() {
		return 20;
	}
	
}
