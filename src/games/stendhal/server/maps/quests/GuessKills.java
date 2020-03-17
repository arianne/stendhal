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

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Expression;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.MinTotalCreaturesKilledCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.TextHasNumberCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.condition.TriggerInListCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.util.TimeUtil;

/**
 * QUEST: The Guessing Game
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Crearid, an old lady found in Nalwor city</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Crearid asks if you want to play a game</li>
 * <li>She picks a random creature you have killed and asks you to guess how
 * many of those you killed</li>
 * <li>You get three guesses and get rewarded if your guess exactly matches the number
 * or a lower reward if your guess is close to the correct number</li>
 * </ul>
 *
 * SLOTS: (subtract from list index to get slot index)
 * <ol>
 * <li>Quest state: done, 1, 2 or 3 (where 1, 2 and 3 represent what guess the player is on)</li>
 * <li>Timestamp: last time quest was completed</li>
 * <li>Creature: the creature that was asked about if quest was not completed</li>
 * </ol>
 *
 * REWARD:
 * <ul>
 * <li>150 XP if guess is exact</li>
 * <li>90 XP if guess is close</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>Weekly</li>
 * </ul>
 */
public class GuessKills extends AbstractQuest {
	public static final String QUEST_SLOT = "guess_kills";

	private static final double ACCURACY = 0.02;
	private static final int MIN_KILLS_REQUIRED = 1000;
	private static final int EXACT_REWARD = 150;
	private static final int CLOSE_REWARD = 90;
	private static final int INTERVAL_BETWEEN_TRIES = MathHelper.MINUTES_IN_ONE_WEEK;
	/** List of existing creatures that may be asked about. */
	private static final List<Creature> POSSIBLE_CREATURES = new ArrayList<Creature>();

	private String CREATURE = "rat";

	/**
	 * Create new quest instance.
	 */
	public GuessKills() {
		for (Creature creature : SingletonRepository.getEntityManager().getCreatures()) {
			if (!creature.isAbnormal()) {
				POSSIBLE_CREATURES.add(creature);
			}
		}
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"The Guessing Game",
				"Crearid plays a game where you guess how many creatures you have killed.",
				true);
		prepareQuestStep();
	}

	@Override
	public String getRegion() {
		return Region.NALWOR_CITY;
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return new AndCondition(
					new QuestCompletedCondition(QUEST_SLOT),
					new TimePassedCondition(QUEST_SLOT, 1, INTERVAL_BETWEEN_TRIES)).fire(player, null, null);
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "GuessKills";
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();

		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}

		final String state = player.getQuest(QUEST_SLOT, 0);
		final String time = player.getQuest(QUEST_SLOT, 1);
		final String creature = player.getQuest(QUEST_SLOT, 2);

		res.add("I have met Crearid. She is an old lady in Nalwor city.");
		res.add("She asked me to guess how many " + Grammar.pluralCreature(creature) + " I have killed.");

		if ("1".equals(state)) {
			res.add("I have three guesses left.");
		} else if ("2".equals(state)) {
			res.add("I have two guesses left.");
		} else if ("3".equals(state)) {
			res.add("I have one guess left.");
		} else if ("done".equals(state)) {
			final long timeRemaining = MathHelper.parseLong(time) + INTERVAL_BETWEEN_TRIES * MathHelper.MILLISECONDS_IN_ONE_MINUTE
					- System.currentTimeMillis();

			if (timeRemaining > 0) {
				res.add("I can go see Crearid for another quiz in about " + TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)));
			} else {
				res.add("I can go see Crearid for another quiz now.");
			}
		}

		return res;
	}

	/**
	 * Sets the FSM for the NPC with all required responses and interactions
	 */
	public void prepareQuestStep() {
		final SpeakerNPC npc = npcs.get("Crearid");

		final ChatCondition requirement = new MinTotalCreaturesKilledCondition(MIN_KILLS_REQUIRED);
		final ChatCondition isNumber = new TextHasNumberCondition(Integer.MIN_VALUE, Integer.MAX_VALUE);
		final ChatCondition enoughTimePassed = new TimePassedCondition(QUEST_SLOT, 1, INTERVAL_BETWEEN_TRIES);
		final ChatCondition wrongAndNotBye = new AndCondition(new NotCondition(isNumber), new NotCondition(new TriggerInListCondition(ConversationPhrases.GOODBYE_MESSAGES)));
		final ChatCondition questNotDone = new OrCondition(new QuestInStateCondition(QUEST_SLOT, 0, "1"),
													 new QuestInStateCondition(QUEST_SLOT, 0, "2"),
													 new QuestInStateCondition(QUEST_SLOT, 0, "3"));

		//checks if guess is exact answer
		final ChatCondition exact = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
				final Expression number = sentence.getNumeral();
				final int kills = player.getSharedKill(CREATURE) + player.getSoloKill(CREATURE);

				if (number != null) {
					final int num = number.getAmount();
					if (num == kills) {
						return true;
					}
				}
				return false;
			}
		};

		//checks if guess is close to actual answer
		final ChatCondition close = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
				final Expression number = sentence.getNumeral();
				final int kills = player.getSharedKill(CREATURE) + player.getSoloKill(CREATURE);

				if (number != null) {
					final int num = number.getAmount();
					if (Math.abs(num - kills) <= Math.max(ACCURACY * kills, 1) && kills != num) {
						return true;
					}
				}
				return false;
			}
		};

		final ConversationStates[] tries = {ConversationStates.QUESTION_1,
	                                      ConversationStates.QUESTION_2,
	                                      ConversationStates.QUESTION_3};

		//gets the creature from unfinished quest
		final ChatAction getSavedCreature = new ChatAction() {
			@Override
			public void fire(Player player, Sentence sentence, EventRaiser npc) {
				CREATURE = player.getQuest(QUEST_SLOT, 2);

				int guesses = 4 - MathHelper.parseIntDefault(player.getQuest(QUEST_SLOT, 0), 1);

				npc.say("Let me see... you have " + Grammar.quantityplnounCreature(guesses, "guess") +
						" left... and if I recall correctly I asked you..." +
						" how many " + Grammar.pluralCreature(CREATURE) + " do think you have killed?");
			}
		};

		final String[] triggers = {"game", "games", "play", "play game", "play games"};

		//if quest not finished and came back
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList(triggers),
				new AndCondition(questNotDone, requirement),
				ConversationStates.QUEST_STARTED,
				"We did not finish our game last time would you like to continue?",
				null);

		//if quest not finished and player wants to continue
		npc.add(ConversationStates.QUEST_STARTED,
				ConversationPhrases.YES_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, 0, "1"),
				ConversationStates.QUESTION_1,
				null,
				getSavedCreature);

		npc.add(ConversationStates.QUEST_STARTED,
				ConversationPhrases.YES_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, 0, "2"),
				ConversationStates.QUESTION_2,
				null,
				getSavedCreature);

		npc.add(ConversationStates.QUEST_STARTED,
				ConversationPhrases.YES_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, 0, "3"),
				ConversationStates.QUESTION_3,
				null,
				getSavedCreature);

		//if quest not finished and player does not want to continue
		npc.add(ConversationStates.QUEST_STARTED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Oh well. Your loss, now what can I do for you?",
				null);

		//if player has not killed enough creatures don't give quest
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList(triggers),
				new NotCondition(requirement),
				ConversationStates.ATTENDING,
				"I'd like some entertainment but you don't look like you're up to it just yet." +
				" Come back when you've gained a bit more experience fighting creatures.",
				null);

		//ask if player would like to take quest if player has killed enough creatures
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList(triggers),
				new AndCondition(requirement, enoughTimePassed, new NotCondition(questNotDone)),
				ConversationStates.QUEST_OFFERED,
				"I'm a little bored at the moment. Would you like to play a game?",
				null);

		//tell player to come back later if one week has not passed
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList(triggers),
				new AndCondition(requirement, new NotCondition(enoughTimePassed), new NotCondition(questNotDone)),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, INTERVAL_BETWEEN_TRIES, "I've had plenty of fun for now, thanks. Come back in, lets say"));

		//ask quest question if quest accepted, also gets the creature type to ask about
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.QUESTION_1,
				null,
				new MultipleActions(
	                new ChatAction() {
	                    @Override
						public void fire(Player player, Sentence sentence, EventRaiser npc) {
	                        do {
	                            CREATURE = Rand.rand(POSSIBLE_CREATURES).getName();
	                        } while(!player.hasKilled(CREATURE));

	                        // This can't be in a SetQuestAction because CREATURE is dynamic
	                        player.setQuest(QUEST_SLOT, 2, CREATURE);

	                        npc.say("I've been counting how many creatures you have killed, now tell me, how many " +
	                                Grammar.pluralCreature(CREATURE) + " do you think you've killed? You have three guesses and I'll accept " +
	                                "guesses that are close to the correct answer.");
	                    }
	                },
	                new SetQuestAction(QUEST_SLOT, 0, "1")));

		//if quest rejected
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Bah, you're no fun at all.",
				null);

		//if invalid answer
		npc.add(ConversationStates.QUESTION_1,
				"",
				wrongAndNotBye,
				ConversationStates.QUESTION_1,
				"How could that possibly be an answer? Give me a proper number.",
				null);

		npc.add(ConversationStates.QUESTION_2,
				"",
				wrongAndNotBye,
				ConversationStates.QUESTION_2,
				"Is that even possible? Give me a valid answer.",
				null);

		npc.add(ConversationStates.QUESTION_3,
				"",
				wrongAndNotBye,
				ConversationStates.QUESTION_3,
				"I've never heard of that number used to describe killings. Give me an explicable answer.",
				null);

		//if goodbye while guessing
		npc.add(tries,
				ConversationPhrases.GOODBYE_MESSAGES,
				null,
				ConversationStates.IDLE,
				"Goodbye, come back when you want to continue.",
				null);

		//if exact answer
		npc.add(tries,
				"",
				new AndCondition(isNumber, exact, new NotCondition(close)),
				ConversationStates.ATTENDING,
				"Stupendous! That is the exact number! Either you're very lucky or you really pay attention.",
				new MultipleActions(
					new SetQuestAction(QUEST_SLOT, 0, "done"),
					new SetQuestToTimeStampAction(QUEST_SLOT, 1),
					new IncreaseXPAction(EXACT_REWARD)));

		//if close answer
		npc.add(tries,
				"",
				new AndCondition(isNumber, close, new NotCondition(exact)),
				ConversationStates.ATTENDING,
				"Wow, that was pretty close. Well done!",
				new MultipleActions(
					new SetQuestAction(QUEST_SLOT, 0, "done"),
					new SetQuestToTimeStampAction(QUEST_SLOT, 1),
					new IncreaseXPAction(CLOSE_REWARD)));

		//if incorrect answer
		npc.add(ConversationStates.QUESTION_1,
				"",
				new AndCondition(isNumber, new NotCondition(close), new NotCondition(exact)),
				ConversationStates.QUESTION_2,
				"Nope, that is not right. Try again.",
				new SetQuestAction(QUEST_SLOT, 0, "2"));

		npc.add(ConversationStates.QUESTION_2,
				"",
				new AndCondition(isNumber, new NotCondition(close), new NotCondition(exact)),
				ConversationStates.QUESTION_3,
				"Wrong again. You have one more try.",
				new SetQuestAction(QUEST_SLOT, 0, "3"));

		npc.add(ConversationStates.QUESTION_3,
				"",
				new AndCondition(isNumber, new NotCondition(close), new NotCondition(exact)),
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(
					new ChatAction() {
						@Override
						public void fire(Player player, Sentence sentence, EventRaiser npc) {
							int exactNumber = player.getSoloKill(CREATURE) + player.getSharedKill(CREATURE);
							npc.say("Unfortunately that is incorrect. The correct answer is in the region of "
							+ (int) Math.max(Math.floor(exactNumber - Math.max(exactNumber * 0.2, 10) + exactNumber * 0.1 * Rand.rand()), 0)
							+ " and " + Math.round(exactNumber + Math.max(exactNumber * 0.2, 10) - exactNumber * 0.1 * Rand.rand())
							+ ". Good effort though.");
						}
					},
					new SetQuestAction(QUEST_SLOT, 0, "done"),
					new SetQuestToTimeStampAction(QUEST_SLOT, 1)));
	}

	@Override
	public String getNPCName() {
		return "Crearid";
	}
}
