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
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Fisherman's license Collector
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Santiago the fisherman</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> The player must bring all kinds of fishes to the fisherman</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> 2000 XP</li>
 * <li> some karma (25 + (5 | -5)) </li>
 * <li> The player gets a fisherman's license (i.e. fishing skills increased by
 *      0.2).</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> No repetitions.</li>
 * </ul>
 *
 * @author dine
 */

public class FishermansLicenseCollector extends AbstractQuest {

	public static final String QUEST_SLOT = "fishermans_license2";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private static final List<String> neededFish =
		Arrays.asList("trout", "perch", "mackerel", "cod", "roach", "char", "clownfish", "surgeonfish");

	/**
	 * Returns a list of the names of all fish that the given player still has
	 * to bring to fulfill the quest.
	 *
	 * @param player
	 *            The player doing the quest
	 * @param hash
	 *            If true, sets a # character in front of every name
	 * @return A list of fish names
	 */
	private List<String> missingFish(final Player player, final boolean hash) {
		final List<String> result = new LinkedList<String>();

		String doneText = player.getQuest(QUEST_SLOT);
		if (doneText == null) {
			doneText = "";
		}

		final List<String> done = Arrays.asList(doneText.split(";"));
		for (final String fish : neededFish) {
			if (!done.contains(fish)) {
				if (hash) {
					result.add("#" + fish);
				} else {
					result.add(fish);
				}
			}
		}
		return result;
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Santiago");

		// player says hi before starting the quest
		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestCompletedCondition(FishermansLicenseQuiz.QUEST_SLOT),
					new QuestNotStartedCondition(QUEST_SLOT)),
			ConversationStates.ATTENDING,
			"Hello again! The second part of your #exam is waiting for you!",
			null);

		// player is willing to help
		npc.add(ConversationStates.QUEST_2_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING,
			"You have to bring me one fish of each #species so that I can see what you have learned so far.",
			new SetQuestAction(QUEST_SLOT, ""));

		// player is not willing to help
		npc.add(ConversationStates.QUEST_2_OFFERED,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"It's okay, then you can excercise a bit more.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		// player asks what exactly is missing
		npc.add(ConversationStates.ATTENDING, "species",
			new QuestActiveCondition(QUEST_SLOT),
			ConversationStates.QUESTION_2, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					final List<String> needed = missingFish(player, true);
					raiser.say("There " + Grammar.isare(needed.size())
							+ " "
							+ Grammar.quantityplnoun(needed.size(), "fish", "one")
							+ " still missing: "
							+ Grammar.enumerateCollection(needed)
							+ ". Do you have such fish with you?");
				}
			});

		// player says he doesn't have required fish with him
		npc.add(ConversationStates.QUESTION_2, ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.IDLE, null, new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					final List<String> missing = missingFish(player, false);
					raiser.say("Let me know as soon as you find "
							+ Grammar.itthem(missing.size()) + ". Goodbye.");
				}
			});

		// player says he has a required fish with him
		npc.add(ConversationStates.QUESTION_2,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.QUESTION_2, "Which fish did you catch?",
			null);

		for(final String itemName : neededFish) {
			npc.add(ConversationStates.QUESTION_2, itemName, null,
				ConversationStates.QUESTION_2, null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						List<String> missing = missingFish(player, false);

						if (missing.contains(itemName)) {
							if (player.drop(itemName)) {
								// register fish as done
								final String doneText = player.getQuest(QUEST_SLOT);
								player.setQuest(QUEST_SLOT, doneText + ";" + itemName);

								// check if the player has brought all fish
								missing = missingFish(player, true);

								if (!missing.isEmpty()) {
									raiser.say("This fish is looking very good! Do you have another one for me?");
								} else {
									player.addXP(2000);
									player.addKarma(25);
									raiser.say("You did a great job! Now you are a real fisherman and you will be much more successful when you catch fish!");
									player.setQuest(QUEST_SLOT, "done");
									// once there are other ways to increase your
									// fishing skills, increase the old skills
									// instead of just setting to 0.2.
									player.setSkill("fishing", Double.toString(0.2));
									player.notifyWorldAboutChanges();
								}
							} else {
								raiser.say("Don't try to cheat! I know that you don't have "
										+ Grammar.a_noun(itemName)
										+ ". What do you really have for me?");
							}
						} else {
							raiser.say("You cannot cheat in this exam! I know that you already gave this fish to me. Do you have other fish for me?");
						}
					}
				});
		}
	}

	private void step_2() {
		// Just find some of the fish somewhere and bring them to Santiago.
	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("Santiago");

		// player returns while quest is still active
		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestActiveCondition(QUEST_SLOT)),
			ConversationStates.ATTENDING,
			"Welcome back. I hope you were not lazy and that you brought me some other fish #species.",
			null);

		// player returns after finishing the quest
		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestCompletedCondition(QUEST_SLOT)),
			ConversationStates.ATTENDING,
			"Welcome fisherman! Nice to see you again. I wish you luck for fishing.",
			null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Fisherman's License part 2",
				"Santiago will grant a fisherman's license to those who can prove their skills.",
				true);
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
			res.add("The second part of my fishing exam is to take Santiago many species of fish.");
			if (!isCompleted(player)) {
				res.add("I still need to bring " + Grammar.enumerateCollection(missingFish(player, false)) + " for Santiago to inspect.");
			} else {
				res.add("I brought every fish Santiago wanted and now I'm a real fisherman! I will have more success when I fish.");
			}
			return res;
	}

	@Override
	public String getName() {
		return "FishermansLicenseCollector";
	}

	@Override
	public String getRegion() {
		return Region.ADOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Santiago";
	}
}
