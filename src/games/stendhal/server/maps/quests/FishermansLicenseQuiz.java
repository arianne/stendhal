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
import java.util.Collections;
import java.util.List;

import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Expression;
import games.stendhal.common.parser.JokerExprMatcher;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.TriggerInListCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.util.TimeUtil;
import marauroa.common.game.RPObjectNotFoundException;

/**
 * QUEST: Fisherman's license Quiz.
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Santiago the fisherman</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> The fisherman puts all fish species onto the table and the player must
 * 		identify the names of the fish in the correct order.</li>
 * <li> The player has one try per day.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> 500 XP</li>
 * <li> Karma: 15</li>
 * <li> The 2nd part of the exam will be unlocked.</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> If the player has failed the quiz, he can retry after 24 hours.</li>
 * <li> After passing the quiz, no more repetitions are possible.</li>
 * </ul>
 *
 * @author dine
 */

public class FishermansLicenseQuiz extends AbstractQuest {
	static final String QUEST_SLOT = "fishermans_license1";

	// TODO: use standard conditions and actions

	private final List<String> speciesList = Arrays.asList("trout", "perch",
			"mackerel", "cod", "roach", "char", "clownfish", "surgeonfish");

	private int currentSpeciesNo;

	private static StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(
			"int_ados_fishermans_hut_west");

	private Item fishOnTable;


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
		res.add("I met Santiago in a hut in Ados city. If I pass his quiz I get a fishing license.");
		if (!player.isQuestCompleted(QUEST_SLOT)) {
			if (remainingTimeToWait(player)>0) {
				res.add("I failed the last exam and it's too soon to try again.");
			} else {
				res.add("Although I failed the last exam, I could now try again.");
			}
		} else {
			res.add("I got all the names of the fish right and now I'm a better fisherman!");
		}
		return res;
	}

	public void cleanUpTable() {
		if (fishOnTable != null) {
			try {
				zone.remove(fishOnTable);
			} catch (final RPObjectNotFoundException e) {
				// The item timed out, or an admin destroyed it.
				// So no need to clean up the table.
			}
			fishOnTable = null;
		}
	}

	private void startQuiz() {
		Collections.shuffle(speciesList);
		currentSpeciesNo = -1;

		putNextFishOnTable();
	}

	private String getCurrentSpecies() {
		return speciesList.get(currentSpeciesNo);
	}

	private void putNextFishOnTable() {
		currentSpeciesNo++;
		cleanUpTable();
		fishOnTable = SingletonRepository.getEntityManager()
				.getItem(getCurrentSpecies());
		fishOnTable.setDescription("You see a fish.");

		fishOnTable.setPosition(7, 4);
		zone.add(fishOnTable);
	}

	private long remainingTimeToWait(final Player player) {
		if (!player.hasQuest(QUEST_SLOT)) {
			// The player has never tried the quiz before.
			return 0L;
		}
		final long timeLastFailed = Long.parseLong(player.getQuest(QUEST_SLOT));
		final long onedayInMilliseconds = 60 * 60 * 24 * 1000;
		final long timeRemaining = timeLastFailed + onedayInMilliseconds
				- System.currentTimeMillis();

		return timeRemaining;
	}

	private void createQuizStep() {
		final SpeakerNPC fisherman = npcs.get("Santiago");

		// Don't Use condition here, because of FishermansLicenseCollector
		fisherman.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						if (player.isQuestCompleted(FishermansLicenseCollector.QUEST_SLOT)) {
							npc.say("I don't have a task for you, and you already have a fisherman's license.");
						} else {
							npc.say("I don't need anything from you, but if you like, you can do an #exam to get a fisherman's license.");
						}
					}
				});

		fisherman.add(ConversationStates.ATTENDING, "exam", null,
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						if (player.isQuestCompleted(FishermansLicenseCollector.QUEST_SLOT)) {
							npc.say("You have already got your fisherman's license.");
						} else if (player.isQuestCompleted(QUEST_SLOT)) {
							npc.say("Are you ready for the second part of your exam?");
							npc.setCurrentState(ConversationStates.QUEST_2_OFFERED);
						} else {
							final long timeRemaining = remainingTimeToWait(player);
							if (timeRemaining > 0L) {
								npc.say("You can only do the quiz once a day. Come back in "
									+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
									+ ".");
							} else {
								npc.say("Are you ready for the first part of your exam?");
								npc.setCurrentState(ConversationStates.QUEST_OFFERED);
							}
						}
					}
				});

		fisherman.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING, "Come back when you're ready.",
				null);

		fisherman.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.QUESTION_1,
				"Fine. The first question is: What kind of fish is this?",
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						startQuiz();
						player.setQuest(QUEST_SLOT, "" + System.currentTimeMillis());
					}
				});

		fisherman.addMatching(ConversationStates.QUESTION_1, Expression.JOKER, new JokerExprMatcher(),
				new NotCondition(new TriggerInListCondition(ConversationPhrases.GOODBYE_MESSAGES)),
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						String species = getCurrentSpecies();
						String obj = sentence.getObjectName();

						if (obj!=null && obj.contains(species) ||
								sentence.getOriginalText().endsWith(species) ||
								sentence.getNormalized().endsWith(species) ||
								sentence.getTriggerExpression().matches(ConversationParser.createTriggerExpression(species))) {
							if (currentSpeciesNo == speciesList.size() - 1) {
								npc.say("Correct! Congratulations, you have passed the first part of the #exam.");
								cleanUpTable();
								player.setQuest(QUEST_SLOT, "done");
								player.addKarma(15);
								player.addXP(500);
							} else {
								npc.say("Correct! So, what kind of fish is this?");
								putNextFishOnTable();
								npc.setCurrentState(ConversationStates.QUESTION_1);
							}
						} else {
							npc.say("No, that's wrong. Unfortunately you have failed, but you can try again tomorrow.");
							cleanUpTable();
							// remember the current time, as you can't do the
							// quiz twice a day.
							player.setQuest(QUEST_SLOT, "" + System.currentTimeMillis());
						}
					}
				});

		fisherman.add(ConversationStates.ANY, ConversationPhrases.GOODBYE_MESSAGES,
				ConversationStates.IDLE, "Goodbye.", new ChatAction() {

		    // this should be put into a custom ChatAction for this quest when the quest is refactored
			@Override
			public void fire(final Player player, final Sentence sentence,
					final EventRaiser npc) {
				cleanUpTable();
			}
		});
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Fishermans License Quiz",
				"Santiago the fisherman will examine fishermen on their knowledge of fish species.",
				false);
		createQuizStep();
	}

	@Override
	public String getName() {
		return "FishermansLicenseQuiz";
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
