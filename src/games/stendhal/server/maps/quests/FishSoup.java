/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.condition.TriggerInListCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.status.PoisonStatus;
import games.stendhal.server.maps.Region;
import marauroa.common.game.IRPZone;

/**
 * QUEST: Special Fish Soup.
 * <p>
 * PARTICIPANTS: <ul><li> Florence Boullabaisse on Ados market</ul>
 *
 * STEPS: <ul><li> Florence Boullabaisse tells you the ingredients of a special fish soup <li> You
 * collect the ingredients <li> You bring the ingredients to Florence <li> The soup
 * is served at a market table<li> Eating the soup heals you fully over time <li> Making it adds karma
 * </ul>
 *
 * REWARD: <ul><li>healing soup <li> Karma bonus of 10 (if ingredients given individually)<li>50 XP</ul>
 *
 * REPETITIONS: <ul><li> as many as desired <li> Only possible to repeat once every twenty
 * minutes</ul>
 *
 * @author Vanessa Julius and Krupi (idea)
 */
public class FishSoup extends AbstractQuest {

	private static final List<String> NEEDED_FOOD = Arrays.asList("surgeonfish",
			"cod", "char", "roach", "clownfish", "onion", "mackerel",
			"garlic", "leek", "perch", "tomato");

	private static final String QUEST_SLOT = "fishsoup_maker";

	private static final int REQUIRED_MINUTES = 20;

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	/**
	 * Returns a list of the names of all food that the given player still has
	 * to bring to fulfil the quest.
	 *
	 * @param player
	 *            The player doing the quest
	 * @param hash
	 *            If true, sets a # character in front of every name
	 * @return A list of food item names
	 */
	private List<String> missingFood(final Player player, final boolean hash) {
		final List<String> result = new LinkedList<String>();

		String doneText = player.getQuest(QUEST_SLOT);
		if (doneText == null) {
			doneText = "";
		}
		final List<String> done = Arrays.asList(doneText.split(";"));
		for (String ingredient : NEEDED_FOOD) {
			if (!done.contains(ingredient)) {
				if (hash) {
					ingredient = "#" + ingredient;
				}
				result.add(ingredient);
			}
		}
		return result;
	}

	/**
	 * Serves the soup as a reward for the given player.
	 * @param player to be rewarded
	 */
	private void placeSoupFor(final Player player) {
		final Item soup = SingletonRepository.getEntityManager()
				.getItem("fish soup");
		final IRPZone zone = SingletonRepository.getRPWorld().getZone("0_ados_city_n2");
		// place on market table
		soup.setPosition(64, 15);
		// only allow player who made soup to eat the soup
		soup.setBoundTo(player.getName());
		// here the soup is altered to have the same heal value as the player's
		// base HP. soup is already persistent so it will last.
		soup.put("amount", player.getBaseHP());
		zone.add(soup);
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Florence Boullabaisse");

		// player says hi before starting the quest
		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestNotStartedCondition(QUEST_SLOT)),
			ConversationStates.INFORMATION_1,
			"Hello and welcome on Ados market! I have something really tasty and know what would #revive you.",
			null);

		// player returns after finishing the quest (it is repeatable) after the
		// time as finished
		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestCompletedCondition(QUEST_SLOT),
					new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)),
			ConversationStates.QUEST_OFFERED,
			"Hello again. Have you returned for more of my special fish soup?",
			null);

		// player returns after finishing the quest (it is repeatable) before
		// the time as finished
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestCompletedCondition(QUEST_SLOT),
						new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES))),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, REQUIRED_MINUTES , "Oh I am sorry, I have to wash my cooking pots first before making more soup for you. Please come back in")
			);

		// player responds to word 'revive'
		npc.add(ConversationStates.INFORMATION_1,
				"revive",
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				null,
				new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					if (player.hasQuest(QUEST_SLOT) && player.isQuestCompleted(QUEST_SLOT)) {
						npc.say("I have everything for the fish soup recipe now.");
						npc.setCurrentState(ConversationStates.ATTENDING);
					} else {
						npc.say("My special fish soup has a magic touch. "
								+ "I need you to bring me the #ingredients.");
					}
				}
			});

		// player asks what exactly is missing
		npc.add(ConversationStates.QUEST_OFFERED, "ingredients", null,
			ConversationStates.QUEST_OFFERED, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final List<String> needed = missingFood(player, true);
					npc.say("I need "
							+ Grammar.quantityplnoun(needed.size(),
									"ingredient", "one")
							+ " before I make the soup: "
							+ Grammar.enumerateCollection(needed)
							+ ". Will you collect them?");
				}
			});

		// player is willing to collect
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.QUESTION_1,
			"You made a good choice and I bet you'll not be disappointed. Do you have anything I need already?",
			new SetQuestAction(QUEST_SLOT, ""));

		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING,
				"Oh, I hope you will change your mind another time. You'd definitely miss out!", null);

		// players asks about the ingredients individually
		npc.add(
			ConversationStates.QUEST_OFFERED,
			Arrays.asList("surgeonfish","cod", "char", "roach", "clownfish", "mackerel", "perch"),
			null,
			ConversationStates.QUEST_OFFERED,
			"There are different fishing places all over Faiumoni. If you want to find out where you can find any kind of fish," +
			" take a look in the library here in Ados. Will you fetch the ingredients?",
			null);

		// players asks about the ingredients individually
		npc.add(
			ConversationStates.QUEST_OFFERED,
			Arrays.asList("leek", "onion"),
			null,
			ConversationStates.QUEST_OFFERED,
			"You will find that in allotments in Fado. So will you fetch the ingredients?",
			null);

		// players asks about the ingredients individually
		npc.add(
			ConversationStates.QUEST_OFFERED,
			Arrays.asList("tomato", "garlic"),
			null,
			ConversationStates.QUEST_OFFERED,
			"There is a nice gardener, Sue, in the Kalavan City gardens who sells tomatoes and garlic. "
			+ "So will you fetch the ingredients?", null);
	}

	private void step_2() {
		// Fetch the ingredients and bring them back to Florence.
	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("Florence Boullabaisse");

		// player returns while quest is still active
		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestStartedCondition(QUEST_SLOT),
					new NotCondition(new QuestStateStartsWithCondition(QUEST_SLOT, "done"))),
			ConversationStates.QUESTION_1,
			"Welcome back! I hope you collected some #ingredients for the fish soup, or #everything.",
			null);

		// player asks what exactly is missing
		npc.add(ConversationStates.QUESTION_1, "ingredients",
			new AndCondition(new QuestStartedCondition(QUEST_SLOT), new NotCondition(new QuestStateStartsWithCondition(QUEST_SLOT, "done"))),
			ConversationStates.QUESTION_1, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final List<String> needed = missingFood(player, true);
					npc.say("I still need "
							+ Grammar.quantityplnoun(needed.size(),
									"ingredient", "one") + ": "
							+ Grammar.enumerateCollection(needed)
							+ ". Did you bring anything I need?");
				}
			});

		// player says he has a required ingredient with him
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.QUESTION_1, "What did you bring?", null);

		for(final String itemName : NEEDED_FOOD) {
			npc.add(ConversationStates.QUESTION_1, itemName, null,
				ConversationStates.QUESTION_1, null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						List<String> missing = missingFood(player, false);

						if (missing.contains(itemName)) {
							if (player.drop(itemName)) {
								// register ingredient as done
								final String doneText = player.getQuest(QUEST_SLOT);
								player.setQuest(QUEST_SLOT, doneText + ";" + itemName);

								// check if the player has brought all Food
								missing = missingFood(player, true);

								if (!missing.isEmpty()) {
									npc.say("Thank you very much! What else did you bring?");
								} else {
									player.addKarma(10.0);
									player.addXP(50);
									placeSoupFor(player);
									player.getStatusList().removeAll(PoisonStatus.class);
									npc.say("The soup's on the market table for you. It will heal you. "
											+ "My magical method in making the soup has given you a little karma too.");
									player.setQuest(QUEST_SLOT, "done;"
											+ System.currentTimeMillis());
									player.notifyWorldAboutChanges();
									npc.setCurrentState(ConversationStates.ATTENDING);
								}
							} else {
								npc.say("Oh come on, I don't have time for jokes! You don't have "
									+ Grammar.a_noun(itemName)
									+ " with you.");
							}
						} else {
							npc.say("You brought me that ingredient already.");
						}
					}
				});
		}

		// Perhaps player wants to give all the ingredients at once
		npc.add(ConversationStates.QUESTION_1, "everything",
				null,
				ConversationStates.QUESTION_1,
				null,
				new ChatAction() {
			    @Override
				public void fire(final Player player, final Sentence sentence,
					   final EventRaiser npc) {
			    	checkForAllIngredients(player, npc);
			}
		});

		// player says something which isn't in the needed food list.
		npc.add(ConversationStates.QUESTION_1, "",
			new NotCondition(new TriggerInListCondition(NEEDED_FOOD)),
			ConversationStates.QUESTION_1,
			"I won't put that in your fish soup.", null);

		// allow to say goodbye while Florence is listening for food names
		npc.add(ConversationStates.QUESTION_1, ConversationPhrases.GOODBYE_MESSAGES, null,
				ConversationStates.IDLE, "Bye.", null);

		npc.add(ConversationStates.ATTENDING, ConversationPhrases.NO_MESSAGES,
			new AndCondition(new QuestStartedCondition(QUEST_SLOT), new NotCondition(new QuestStateStartsWithCondition(QUEST_SLOT, "done"))),
			ConversationStates.ATTENDING,
			"I'm not sure what you want from me, then.", null);

		// player says he didn't bring any Food to different question
		npc.add(ConversationStates.QUESTION_1, ConversationPhrases.NO_MESSAGES,
			new AndCondition(new QuestStartedCondition(QUEST_SLOT), new NotCondition(new QuestStateStartsWithCondition(QUEST_SLOT, "done"))),
			ConversationStates.ATTENDING, "Okay then. Come back later.",
			null);
	}

	private void checkForAllIngredients(final Player player, final EventRaiser npc) {
		List<String> missing = missingFood(player, false);
		for (final String food : missing) {
		if (player.drop(food)) {
			// register ingredient as done
			final String doneText = player.getQuest(QUEST_SLOT);
			player.setQuest(QUEST_SLOT, doneText + ";"
			+ food);
			}
		}
		// check if the player has brought all Food
		missing = missingFood(player, true);
		if (missing.size() > 0) {
			npc.say("You didn't have all the ingredients I need. I still need "
							+ Grammar.quantityplnoun(missing.size(),
									"ingredient", "one") + ": "
							+ Grammar.enumerateCollection(missing)
							+ ". You'll get bad karma if you keep making mistakes like that!");
			// to fix bug [ 2517439 ]
			player.addKarma(-5.0);
			return;
		} else {
			// you get less XP if you did it the lazy way
			// and no karma
			player.addXP(30);
			placeSoupFor(player);
			player.getStatusList().removeAll(PoisonStatus.class);
			npc.say("The soup's on the market table for you, it will heal you. Tell me if I can help you with anything else.");
			player.setQuest(QUEST_SLOT, "done;"
					+ System.currentTimeMillis());
			player.notifyWorldAboutChanges();
			npc.setCurrentState(ConversationStates.ATTENDING);
		}
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Fish Soup",
				"Florence Boullabaisse makes a healthy and tasty fish soup, but it needs rather a lot of ingredients.",
				false);
		step_1();
		step_2();
		step_3();
	}

	@Override
	public List<String> getHistory(final Player player) {
			final List<String> res = new ArrayList<String>();
			if (!player.hasQuest(QUEST_SLOT)) {
				return res;
			}
			if (!isCompleted(player)) {
				res.add("I'm collecting ingredients to make fish soup. I still need " + Grammar.enumerateCollection(missingFood(player, false)) + ".");
			} else if(isRepeatable(player)){
				res.add("Florence is ready to make soup for me again!");
			} else {
				res.add("I made some yummy fish soup and Florence is now washing the dishes.");
			}
			return res;
	}

	@Override
	public String getName() {
		return "FishSoup";
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return	new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
						 new TimePassedCondition(QUEST_SLOT,1,REQUIRED_MINUTES)).fire(player, null, null);
	}

	@Override
	public String getRegion() {
		return Region.ADOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Florence Boullabaisse";
	}
}
