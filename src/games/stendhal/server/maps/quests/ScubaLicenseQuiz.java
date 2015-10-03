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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		anwsers.put("When nitrogen bubbles block blood flow in your body after a dive you are experiencing?", "decompression sickness");
		anwsers.put("fisherman Jacky",
						"Don't mistake your trout for your old trout, she wouldn't taste so good.");
		anwsers.put("What percentage of air is oxygen? Just give me a number.",
						"21");
		anwsers.put("fisherman Sody",
				"Devout Crustaceans believe in the One True Cod.");
		anwsers.put("What are waves caused by?",
						"wind");
		anwsers.put("Most scuba diving injuries caused by fish and aquatic animals happen because?",
						"They are a afraid of you.");
		anwsers.put("You should never even consider diving if you currently have a cold because?",
						"you may not be able to equalize pressure");
		anwsers.put("fisherman Ally", "Holy mackerel! These chips are tasty.");
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
						npc.say("Do you know the anwser to my question, " + name + "?");
						npc.setCurrentState(ConversationStates.QUESTION_1);
					} else {
						npc.say("Welcome back!");
					}
				}
			});

		// TODO: rewrite this to use standard conditions and actions
		instructor.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, null,
			ConversationStates.QUEST_OFFERED, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					if (player.isQuestCompleted(QUEST_SLOT)) {
						npc.say("You've already passed the exam!");
						npc.setCurrentState(ConversationStates.ATTENDING);
					} else if (player.hasQuest(QUEST_SLOT)) {
						final String name = player.getQuest(QUEST_SLOT);
						npc.say("I trust you studied up and can answer the question. " + name + "?");
						npc.setCurrentState(ConversationStates.QUESTION_1);
					} else {
						npc.say("Are you ready to take the #test?");
					}
				}
			});

		instructor.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Then I don't do you a favour, either.", null);

		instructor.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final String name = Rand.rand(anwsers.keySet());
					npc.say("Please look up the famous quote by " + name + ".");
					player.setQuest(QUEST_SLOT, name);
				}
			});

		instructor.add(ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.QUESTION_2, "So, what is it?", null);

		instructor.add(ConversationStates.QUESTION_1,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Too bad. You're not qualified to dive until you know the answer.", null);

		// TODO: rewrite this to use standard conditions and actions
		instructor.addMatching(ConversationStates.QUESTION_2, Expression.JOKER, new JokerExprMatcher(), null,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final String name = player.getQuest(QUEST_SLOT);
					final String quote = anwsers.get(name);

					final Sentence answer = sentence.parseAsMatchingSource();
					final Sentence expected = ConversationParser.parse(quote, new SimilarExprMatcher());

					if (answer.matchesFull(expected)) {
						npc.say("Correct, well done! Here is your new diving license!");
						final Item ScubaGear = SingletonRepository.getEntityManager().getItem("scuba gear");
						ScubaGear.setBoundTo(player.getName());
						player.equipOrPutOnGround(ScubaGear);
						player.addXP(100);
						player.addKarma(5);
						player.setQuest(QUEST_SLOT, "done");
						player.notifyWorldAboutChanges();
					} else if (ConversationPhrases.GOODBYE_MESSAGES.contains(sentence.getTriggerExpression().getNormalized())) {
						npc.say("Good bye - see you next time!");
						npc.setCurrentState(ConversationStates.IDLE);
					} else {
						npc.say("Incorrect. #Study up and come back to me.");
						npc.setCurrentState(ConversationStates.IDLE);
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
}
