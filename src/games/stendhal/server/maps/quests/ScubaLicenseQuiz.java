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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Rand;
import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Expression;
import games.stendhal.common.parser.JokerExprMatcher;
import games.stendhal.common.parser.Sentence;
import games.stendhal.common.parser.SimilarExprMatcher;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Quest to get the scuba gear.
 * <p>
 *
 * PARTICIPANTS: <ul><li> Edward the diving instructor</ul>
 *
 *
 * STEPS: <ul><li> This quest is about players getting the ability to dive and earn the necessary equipment.
 *  The instructor will as a question and once the player answers correctly will reward them with scuba gear.</ul>
 *
 *
 * REWARD:
 * <ul>
 * <li> 100 XP
 * <li> some karma (5)
 * <li> The Scuba Gear
 * </ul>
 *
 * REPETITIONS: <ul><li> no repetitions</ul>
 *
 * @author soniccuz based on (LookUpQuote by dine)
 */

public class ScubaLicenseQuiz extends AbstractQuest {
	private static final String QUEST_SLOT = "get_diving_license";

	private static Map<String, String> anwsers = new HashMap<String, String>();
	static {
		anwsers.put("When nitrogen bubbles block blood flow in your body after a dive, you are experiencing?",
				"decompression sickness");
		anwsers.put("What percentage of air is oxygen? Just give me a number.",
						"21");
		anwsers.put("Waves are caused by ...",
						"wind");
		anwsers.put("Most scuba diving injuries caused by fish and aquatic animals happen because they are ... of you.",
						"afraid");
		anwsers.put("You should never even consider diving when you have a ...",
						"cold");
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
		res.add("I met Edward a former diver who now teaches other people how. If I can pass his exam I'll get a diving license.");
		if (!player.isQuestCompleted(QUEST_SLOT)) {
			res.add("The question I must answer is " + player.getQuest(QUEST_SLOT) + ".");
		} else {
			res.add("I passed Edward's exam and got the diving license.");
		}
		return res;
	}

	private void createLicense() {
		final SpeakerNPC instructor = npcs.get("Edward");

		instructor.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new GreetingMatchesNameCondition(instructor.getName()), true,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					if (!player.hasQuest(QUEST_SLOT)) {
						npc.say("Hi I am Faiumoni's one and only teacher for diving. If you want to explore the wonderful world below the sea you need a #license and #scuba #gear.");
					} else if (!player.isQuestCompleted(QUEST_SLOT)) {
						final String name = player.getQuest(QUEST_SLOT);
						npc.say("You're back! I trust you studied up and can answer the question. " + name);
						npc.setCurrentState(ConversationStates.QUESTION_1);
					} else {
						npc.say("Welcome aboard!");
					}
				}
			});

		instructor.add(ConversationStates.ATTENDING,
				ConversationPhrases.combine(ConversationPhrases.QUEST_MESSAGES, Arrays.asList("exam", "test")),
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"Are you ready to take the test?",
				null);

		// TODO: point to diving location
		instructor.add(ConversationStates.ATTENDING,
				ConversationPhrases.combine(ConversationPhrases.QUEST_MESSAGES, Arrays.asList("exam", "test")),
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"You've already passed the exam! Now find a good spot to explore the ocean.",
				null);

		instructor.add(ConversationStates.ATTENDING,
				ConversationPhrases.combine(ConversationPhrases.QUEST_MESSAGES, Arrays.asList("exam", "test")),
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.QUESTION_1,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						final String name = player.getQuest(QUEST_SLOT);
						npc.say("I trust you studied up and can answer the question. " + name);
					}
				});

		instructor.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Okay, diving is not for everyone, but don't hesitate to come back to me if you change your mind. Feel free to #study in the mean time.", null);

		instructor.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.QUESTION_1, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final String name = Rand.rand(anwsers.keySet());
					npc.say("Very well. Here is your question. " + name);
					player.setQuest(QUEST_SLOT, name);
				}
			});

		/*
		instructor.add(ConversationStates.QUESTION_1,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Too bad. You're not qualified to dive until you know the answer. You should #study.", null);
		*/

		// TODO: rewrite this to use standard conditions and actions
		instructor.addMatching(ConversationStates.QUESTION_1, Expression.JOKER, new JokerExprMatcher(), null,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final String name = player.getQuest(QUEST_SLOT);
					final String quote = anwsers.get(name);

					final Sentence answer = sentence.parseAsMatchingSource();
					final Sentence expected = ConversationParser.parse(quote, new SimilarExprMatcher());

					if (answer.matchesFull(expected)) {
						npc.say("Correct, well done! You are now licensed to go scuba diving! But you'll need #buy set of #scuba #gear first. Afraid I don't give 'em away for free anymore.");
						//Free samples are over.
						player.addXP(100);
						player.addKarma(5);
						player.setQuest(QUEST_SLOT, "done");
						player.notifyWorldAboutChanges();
					} else if (ConversationPhrases.GOODBYE_MESSAGES.contains(sentence.getTriggerExpression().getNormalized())) {
						npc.say("Good bye - see you next time!");
						npc.setCurrentState(ConversationStates.IDLE);
					} else {
						npc.setCurrentState(ConversationStates.ATTENDING);
						npc.say("Incorrect. #Study up and come back to me.");
					}
				}
			});
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Diving License Quiz",
				"Edward hands out diving licenses for passing his exam.",
				false);
		createLicense();
	}
	@Override
	public String getName() {
		return "DivingLicenseQuiz";
	}

	@Override
	public String getRegion() {
		return Region.ATHOR_ISLAND;
	}
	@Override
	public String getNPCName() {
		return "Edward";
	}

	/**
	 * is scuba diving possible?
	 */
	public static class ScubaCondition implements ChatCondition {

        @Override
        public boolean fire(Player player, Sentence sentence, Entity npc) {
            return player.isEquippedItemInSlot("armor", "scuba gear") && player.isQuestCompleted("get_diving_license");
        }

        @Override
        public int hashCode() {
            return -13527181;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ScubaCondition;
        }

        @Override
        public String toString() {
            return "scuba?";
        }
	}
}
