/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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
import games.stendhal.common.parser.ConvCtxForMatchingSource;
import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Expression;
import games.stendhal.common.parser.JokerExprMatcher;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.player.Player;

/**
 * QUEST: Quest to get a recipe for a potion for Imorgen
 * <p>
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Cameron</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> The librarian, Cameron, of Constantines Villa needs to find some books.</li>
 * <li> There are seven books missing in the library shelves.</li>
 * <li> Cameron knows the beginning of a sentence out of each book he is searching for.</li>
 * <li> You have to find the book and tell Cameron the rest of the sentence. He chooses the book randomly.</li>
 * <li> He knows then that you found the book and that these aren't lost.</li>
 * <li> You'll reward you for your efforts.</li>
 * </ul>
 *
 * REWARD:
 *  <ul>
 *  <li>A recipe which Imorgen needs for her potion</li>
 *  </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>no repetitions</li>
 * </ul>
 *
 * @author storyteller and bluelads4
 */

public class TheMissingBooks extends AbstractQuest {
	private static final String QUEST_SLOT = "find_book";

	private static Map<String, String> quotes = new HashMap<String, String>();
	static {
		quotes.put("Down in the deep sea around Athor island...",
						"it lays, hidden under soft sand - the mighty treasure of Faiumoni.");
		quotes.put("As a mighty warrior,...",
						"you always need to wear a powerful armor in fights.");
		quotes.put("You don't believe in magic? The potion...",
						"of love, made made from a magician's hand, even works for Yetis.");
		quotes.put("Hungry? Thirsty? Tired?...",
						"A break might help. Take a look around the lovely nature of Faiumoni and find relaxing places to make a stop at. Even when you are busy during tasks, a healthy snack will power you up.");
		quotes.put("And there they were: two strangers, alone in the tunnel...",
						"of Amazon island, heading out for reaching the right entrance to a life full of joy and peace - at least they hoped for that.");
		quotes.put("Oh my oh my oh my! What for a huge creature I'm looking at! It's red, it's huge,...",
						"it's powerful, it has a hard forehand...Could that be it? It is a balrog!");
		quotes.put("You need some flour, some eggs, some butter, some sugar, some chocolate and some milk...",
						"a hot drink for the winter, some rat to make it silk. Now after waiting for some time and after the job is done: a crepes suzette that is you hold, enjoy it and have fun!");
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
		res.add("I met Cameron in Constantines Villa. He asked me to find a quote out of a book for him.");
		if (!player.isQuestCompleted(QUEST_SLOT)) {
			res.add("The end of the sentence I must find starts with: " + player.getQuest(QUEST_SLOT) + ".");
		} else {
			res.add("I told the sentence to Cameron and he gave me a recipe which Imorgen might need.");
		}
		return res;
	}

	private void createRecipe() {
		final SpeakerNPC npc = npcs.get("Cameron");

		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new GreetingMatchesNameCondition(npc.getName()), true,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					if (!player.hasQuest(QUEST_SLOT)) {
						npc.say("Hello and welcome to my little library! As I see, you must be a friend of Constantine, his guards didn't send you out again. I think, I can trust you. Maybe you can do me a little #favour!");
					} else if (!player.isQuestCompleted(QUEST_SLOT)) {
						final String startsentence = player.getQuest(QUEST_SLOT);
						npc.say("Hello again! Did you find the book I am searching for? What is the rest of the sentence of " + startsentence + "?");
						npc.setCurrentState(ConversationStates.QUESTION_1);
					} else {
						npc.say("Hello again!");
					}
				}
			});

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, null,
			ConversationStates.QUEST_OFFERED, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					if (player.isQuestCompleted(QUEST_SLOT)) {
						npc.say("Thanks but I am happy already. The book you found was one of my most precious ones and it's there.");
						npc.setCurrentState(ConversationStates.ATTENDING);
					} else if (player.hasQuest(QUEST_SLOT)) {
						final String startsentence = player.getQuest(QUEST_SLOT);
						npc.say("Oh, I thought you are searching for my book already. Have you already looked up if the book I'm searching for is there? One of the sentences out of it starts with " + startsentence + ". Please tell me the rest of the sentence.");
						npc.setCurrentState(ConversationStates.QUESTION_1);
					} else {
						npc.say("There are seven books missing in my shelves. I hope that they are just not sorted in correctly! I'm really worried about losing my precioust treasures. Can you find at least one for me please after I give you the begining of a sentence out of it?");
					}
				}
			});

		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"That is a pity, really. I'm quite afraid and my legs are shaking, can't look anything up by myself as though as I'm so confused. I can't reward you then.", null);

		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final String startsentence = Rand.rand(quotes.keySet());
					npc.say("Please search the book which includes a sentence starting with " + startsentence + " and tell me the rest of sentence for showing me that you found it.");
					player.setQuest(QUEST_SLOT, startsentence);
				}
			});

		npc.add(ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.QUESTION_2, "So, what is the end of the sentence?", null);

		npc.add(ConversationStates.QUESTION_1,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Too bad. I would have had a nice reward for you which you might need later.", null);

		// TODO: rewrite this to use standard conditions and actions
		npc.addMatching(ConversationStates.QUESTION_2, Expression.JOKER, new JokerExprMatcher(), null,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final String startsentence = player.getQuest(QUEST_SLOT);
					final String quote = quotes.get(startsentence);

					final Sentence answer = sentence.parseAsMatchingSource();
					final Sentence expected = ConversationParser.parse(quote, new ConvCtxForMatchingSource());

					if (answer.matchesFull(expected)) {
						npc.say("Yes, you found it! I'm so relieved now that at least one of the seven books I lost is still in my library. Here, take this recipe for the effort! I bet, Imorgen will be happy about it! Please don't lose it, it's an original one.");
						final Item recipe = SingletonRepository.getEntityManager().getItem("recipe");
						recipe.setBoundTo(player.getName());
						player.equipOrPutOnGround(recipe);
						player.addXP(500);
						player.setQuest(QUEST_SLOT, "done");
						player.notifyWorldAboutChanges();
					} else if (ConversationPhrases.GOODBYE_MESSAGES.contains(sentence.getTriggerExpression().getNormalized())) {
						npc.say("We read...ehm...see us again soon!");
						npc.setCurrentState(ConversationStates.IDLE);
					} else {
						npc.say("Oh, I don't own a book with such a sentence. Maybe you found the wrong one. Please search for the book and tell me the rest of the sentence.");
						npc.setCurrentState(ConversationStates.IDLE);
					}
				}
			});
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"The Missing Books",
				"Cameron, the librarian of Constantines Villa, searches some of his precious books.",
				false);
		createRecipe();
	}
	@Override
	public String getName() {
		return "TheMissingBooks";

	}
	@Override
	public String getNPCName() {
		return "Cameron";
	}
}
