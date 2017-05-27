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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Rand;
import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Expression;
import games.stendhal.common.parser.JokerExprMatcher;
import games.stendhal.common.parser.Sentence;
import games.stendhal.common.parser.SimilarExprMatcher;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Quest to get a fishing rod
 * <p>
 *
 * PARTICIPANTS: <ul><li> Pequod the fisherman</ul>
 *
 *
 * STEPS: <ul><li> The fisherman asks you to go to the library to get him a quote of a
 * famous fisherman. <li> The player goes to the library where a book with some
 * quotes lies on the table and looks the correct one up. <li>The player goes back
 * to the fisherman and tells him the quote.</ul>
 *
 *
 * REWARD:
 * <ul>
 * <li> 750 XP
 * <li> some karma (10)
 * <li> A fishing rod
 * </ul>
 *
 * REPETITIONS: <ul><li> no repetitions</ul>
 *
 * @author dine
 */

public class LookUpQuote extends AbstractQuest {
	private static final String QUEST_SLOT = "get_fishing_rod";

	private static Map<String, String> quotes = new HashMap<String, String>();
	static {
		quotes.put("fisherman Bully", "Clownfish are always good for a laugh.");
		quotes.put("fisherman Jacky",
						"Don't mistake your trout for your old trout, she wouldn't taste so good.");
		quotes.put("fisherman Tommy",
						"I wouldn't trust a surgeonfish in a hospital, there's something fishy about them.");
		quotes.put("fisherman Sody",
				"Devout Crustaceans believe in the One True Cod.");
		quotes.put("fisherman Humphrey",
						"I don't understand why no-one buys my fish. The sign says 'Biggest Roaches in town'.");
		quotes.put("fisherman Monty",
						"My parrot doesn't like to sit on a perch. He says it smells fishy.");
		quotes.put("fisherman Charby",
						"That fish restaurant really overcooks everything. It even advertises char fish.");
		quotes.put("fisherman Ally", "Holy mackerel! These chips are tasty.");
	}


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
		res.add("I met Pequod in a hut in Ados city and he asked me to look up a quote by a famous fisherman.");
		if (!player.isQuestCompleted(QUEST_SLOT)) {
			res.add("The quote I must find is by " + player.getQuest(QUEST_SLOT) + ".");
		} else {
			res.add("I got the quote for Pequod and he gave me a fishing rod.");
		}
		return res;
	}

	private void createFishingRod() {
		final SpeakerNPC fisherman = npcs.get("Pequod");

		fisherman.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new GreetingMatchesNameCondition(fisherman.getName()), true,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					if (!player.hasQuest(QUEST_SLOT)) {
						npc.say("Hello newcomer! I can #help you on your way to become a real fisherman!");
					} else if (!player.isQuestCompleted(QUEST_SLOT)) {
						final String name = player.getQuest(QUEST_SLOT);
						npc.say("Welcome back! Did you look up the famous quote by " + name + "?");
						npc.setCurrentState(ConversationStates.QUESTION_1);
					} else {
						npc.say("Welcome back!");
					}
				}
			});

		// TODO: rewrite this to use standard conditions and actions
		fisherman.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, null,
			ConversationStates.QUEST_OFFERED, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					if (player.isQuestCompleted(QUEST_SLOT)) {
						npc.say("No, thanks. I have all I need.");
						npc.setCurrentState(ConversationStates.ATTENDING);
					} else if (player.hasQuest(QUEST_SLOT)) {
						final String name = player.getQuest(QUEST_SLOT);
						npc.say("I already asked you for a favor already! Have you already looked up the famous quote by " + name + "?");
						npc.setCurrentState(ConversationStates.QUESTION_1);
					} else {
						npc.say("Well, I once had a book with quotes of famous fishermen, but I lost it. And now I cannot remember a certain quote. Can you look it up for me?");
					}
				}
			});

		fisherman.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Then I don't do you a favour, either.", null);

		fisherman.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final String name = Rand.rand(quotes.keySet());
					npc.say("Please look up the famous quote by " + name + ".");
					player.setQuest(QUEST_SLOT, name);
				}
			});

		fisherman.add(ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.QUESTION_2, "So, what is it?", null);

		fisherman.add(ConversationStates.QUESTION_1,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Too bad. I would have had a nice reward for you.", null);

		// TODO: rewrite this to use standard conditions and actions
		fisherman.addMatching(ConversationStates.QUESTION_2, Expression.JOKER, new JokerExprMatcher(), null,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final String name = player.getQuest(QUEST_SLOT);
					final String quote = quotes.get(name);

					final Sentence answer = sentence.parseAsMatchingSource();
					final Sentence expected = ConversationParser.parse(quote, new SimilarExprMatcher());

					if (answer.matchesFull(expected)) {
						npc.say("Oh right, that's it! How could I forget this? Here, take this handy fishing rod as an acknowledgement of my gratitude!");
						final Item fishingRod = SingletonRepository.getEntityManager().getItem("fishing rod");
						fishingRod.setBoundTo(player.getName());
						player.equipOrPutOnGround(fishingRod);
						player.addXP(750);
						player.addKarma(10);
						player.setQuest(QUEST_SLOT, "done");
						player.notifyWorldAboutChanges();
					} else if (ConversationPhrases.GOODBYE_MESSAGES.contains(sentence.getTriggerExpression().getNormalized())) {
						npc.say("Good bye - see you next time!");
						npc.setCurrentState(ConversationStates.IDLE);
					} else {
						npc.say("I think you made a mistake. Come back if you can tell me the correct quote.");
						npc.setCurrentState(ConversationStates.IDLE);
					}
				}
			});
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Look Up Quote",
				"Pequod has forgotten a quote by a famous fisherman.",
				false);
		createFishingRod();
	}
	@Override
	public String getName() {
		return "LookUpQuote";
	}

	@Override
	public String getRegion() {
		return Region.ADOS_CITY;
	}
	@Override
	public String getNPCName() {
		return "Pequod";
	}
}
