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

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.common.parser.Expression;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.defaultruleset.DefaultEntityManager;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.*;
import games.stendhal.server.entity.player.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
 * <li>Quest state: done, guess1, guess2 or guess3</li>
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
public class GuessKillsQuest extends AbstractQuest {

    public static final String QUEST_SLOT = "guess_kills";

    private final int MIN_KILLS_REQUIRED = 1000;
    private final double ACCURACY = 0.02;
    private final int EXACT_REWARD = 150;
    private final int CLOSE_REWARD = 90;
    private String CREATURE = "rat";

    @Override
    public void addToWorld() {
        super.addToWorld();
        prepareQuestStep();
    }

    public String getSlotName() {
        return QUEST_SLOT;
    }

    public String getName() {
        return "GuessKillsQuest";
    }

    public List<String> getHistory(final Player player) {
        final List<String> res = new ArrayList<String>();
        return res;
    }

    /**
     * Sets the FSM for the NPC with all required responses and interactions
     */
    public void prepareQuestStep() {
        SpeakerNPC npc = npcs.get("Crearid");

        final DefaultEntityManager manager = (DefaultEntityManager) SingletonRepository.getEntityManager();

        ChatCondition requirement = new MinTotalCreaturesKilledCondition(MIN_KILLS_REQUIRED);
        ChatCondition isNumber = new TextHasNumberCondition(Integer.MIN_VALUE, Integer.MAX_VALUE);
        ChatCondition enoughTimePassed = new TimePassedCondition(QUEST_SLOT, 1, MathHelper.MINUTES_IN_ONE_WEEK);
        ChatCondition wrongAndNotBye = new AndCondition(new NotCondition(isNumber), new NotCondition(new TriggerInListCondition(ConversationPhrases.GOODBYE_MESSAGES)));
        ChatCondition questNotDone = new OrCondition(new QuestInStateCondition(QUEST_SLOT, 0, "guess1"),
													 new QuestInStateCondition(QUEST_SLOT, 0, "guess2"),
													 new QuestInStateCondition(QUEST_SLOT, 0, "guess3"));

        //checks if guess is exact answer
        ChatCondition exact = new ChatCondition() {
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
        ChatCondition close = new ChatCondition() {
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

        ConversationStates[] tries = {ConversationStates.QUESTION_1,
                                      ConversationStates.QUESTION_2,
                                      ConversationStates.QUESTION_3};
        
        //gets the creature from unfinished quest
        ChatAction getSavedCreature = new ChatAction() {
			public void fire(Player player, Sentence sentence, EventRaiser npc) {
				CREATURE = player.getQuest(QUEST_SLOT, 2);
				
				String state = player.getQuest(QUEST_SLOT, 0);
				int guesses = 4 - Integer.parseInt(state.substring(state.length() - 1));
				
				npc.say("Let me see... you have " + guesses + " guess" + (guesses != 1 ? "es" : "") + 
						" left... and if I recall correctly I asked you..." +
						" how many " + CREATURE + "s do think you have killed?");
			}        	
        };
        
        String[] triggers = {"game", "games", "play", "play game", "play games"};
        
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
        		new QuestInStateCondition(QUEST_SLOT, 0, "guess1"),
                ConversationStates.QUESTION_1,
                null,
                getSavedCreature);
        npc.add(ConversationStates.QUEST_STARTED,
        		ConversationPhrases.YES_MESSAGES,
                new QuestInStateCondition(QUEST_SLOT, 0, "guess2"),
                ConversationStates.QUESTION_2,
                null,
                getSavedCreature);
        npc.add(ConversationStates.QUEST_STARTED,
        		ConversationPhrases.YES_MESSAGES,
                new QuestInStateCondition(QUEST_SLOT, 0, "guess3"),
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
                "I've had plenty of fun for now, thanks. Come back some other time.",
                null);

        //ask quest question if quest accepted, also gets the creature type to ask about
        npc.add(ConversationStates.QUEST_OFFERED,
                ConversationPhrases.YES_MESSAGES,
                null,
                ConversationStates.QUESTION_1,
                null,
                new MultipleActions(
	                new ChatAction() {
	                    public void fire(Player player, Sentence sentence, EventRaiser npc) {
	                        do {
	                            CREATURE = ((Creature) Rand.rand(manager.getCreatures().toArray())).getName();
	                        } while(!player.hasKilled(CREATURE));
	                        
	                        // This can't be in a SetQuestAction because CREATURE is dynamic
	                        player.setQuest(QUEST_SLOT, 2, CREATURE);
	
	                        npc.say("I've been counting how many creatures you have killed, now tell me, how many " +
	                                CREATURE + "s do you think you've killed? You have three guesses and I'll accept " +
	                                "guesses that are close to the correct answer.");
	                    }
	                },
	                new SetQuestAction(QUEST_SLOT, 0, "guess1")));

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
                null,
                new MultipleActions(
                        new ChatAction() {
                            public void fire(Player player, Sentence sentence, EventRaiser npc) {
                                npc.say("Wow, that was pretty close. The exact number is " +
                                        (player.getSoloKill(CREATURE) + player.getSharedKill(CREATURE)) + ".");
                            }
                        },
                        new SetQuestAction(QUEST_SLOT, 0, "done"),
                        new SetQuestToTimeStampAction(QUEST_SLOT, 1),
                        new IncreaseXPAction(CLOSE_REWARD)));

        //if incorrect answer
        npc.add(ConversationStates.QUESTION_1,
                "",
                new AndCondition(isNumber, new NotCondition(close), new NotCondition(exact)),
                ConversationStates.QUESTION_2,
                "Nope, that is not even close. Try again.",
                new SetQuestAction(QUEST_SLOT, 0, "guess2"));
        npc.add(ConversationStates.QUESTION_2,
                "",
                new AndCondition(isNumber, new NotCondition(close), new NotCondition(exact)),
                ConversationStates.QUESTION_3,
                "Wrong again. You have one more try.",
                new SetQuestAction(QUEST_SLOT, 0, "guess3"));
        npc.add(ConversationStates.QUESTION_3,
                "",
                new AndCondition(isNumber, new NotCondition(close), new NotCondition(exact)),
                ConversationStates.ATTENDING,
                null,
                new MultipleActions(
                        new ChatAction() {
                            public void fire(Player player, Sentence sentence, EventRaiser npc) {
                                npc.say("Unfortunately you're a ways off. The correct answer is " +
                                        (player.getSoloKill(CREATURE) + player.getSharedKill(CREATURE)) +
                                        ". Good effort though.");
                            }
                        },
                        new SetQuestAction(QUEST_SLOT, 0, "done"),
                        new SetQuestToTimeStampAction(QUEST_SLOT, 1)));
    }
}