/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.nalwor.forest;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.common.constants.SoundID;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.NPCEmoteAction;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;
import games.stendhal.server.maps.nalwor.forest.TrainingArea.TrainerNPC;


public class Dojo implements ZoneConfigurator {

	/** quest/activity identifier */
	private static final String QUEST_SLOT = "dojo";

	/** time (in minutes) allowed for training session */
	private static final int BASE_TRAIN_TIME = 20;

	/** time player must wait to train again */
	private static final int COOLDOWN = 5;

	/** condition to check if training area is full */
	private ChatCondition dojoFullCondition;

	/** quest states */
	private static final String STATE_ACTIVE = "training";
	private static final String STATE_DONE = "done";

	/** zone info */
	private StendhalRPZone dojoZone;

	/** dojo area */
	private static TrainingArea dojoArea;

	/** NPC that manages dojo area */
	private static final String samuraiName = "Omura Sumitada";
	private TrainerNPC samurai;

	/** phrases used in conversations */
	private static final List<String> TRAIN_PHRASES = Arrays.asList("train", "training", "enter", "start");
	private static final List<String> FEE_PHRASES = Arrays.asList("fee", "cost", "charge");

	/** message when dojo is full */
	private static final String FULL_MESSAGE = "The dojo is full. Come back later.";


	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		dojoZone = zone;

		initNPC();

		dojoArea = new TrainingArea(QUEST_SLOT, zone, 5, 52, 35, 20, samurai, new Point(22, 74), new Point(22, 72), Direction.DOWN);
		dojoArea.setCapacity(16);

		// initialize condition to check if dojo is full
		dojoFullCondition = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				return dojoArea.isFull();
			}
		};

		initDialogue();
	}

	private void initNPC() {
		samurai = new TrainerNPC(samuraiName, FULL_MESSAGE, "Hey %s! You can't just walk into the dojo for free.");
		samurai.setEntityClass("samurai1npc");
		samurai.setIdleDirection(Direction.DOWN);
		samurai.setPosition(24, 74);

		dojoZone.add(samurai);
	}

	/**
	 * Initializes conversation & actions for dojo training.
	 */
	private void initDialogue() {
		samurai.addGreeting("This is the assassins' dojo.");
		samurai.addGoodbye();
		samurai.addJob("I manage this dojo. Ask me if you would like to #train.");
		samurai.addOffer("I can offer you a #training session for a #fee.");
		samurai.addQuest("I don't need any help, but I can let you to #train for a #fee if you have been approved by the assassins' HQ.");
		samurai.addHelp("This is the assassins' dojo. I can let you #train here for a #fee if you're in good with HQ.");

		samurai.add(ConversationStates.ATTENDING,
				FEE_PHRASES,
				null,
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						samurai.say("The fee to #train for your skill level is " + dojoArea.calculateFee(player.getAtk()) + " money.");
					}
				});

		final ChatCondition meetsLevelCapCondition = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				return dojoArea.meetsLevelCap(player, player.getAtk());
			}
		};

		final ChatCondition canAffordFeeCondition = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				return player.isEquipped("money", dojoArea.calculateFee(player.getAtk()));
			}
		};

		final ChatAction startTrainingAction = new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				final int trainTime = calculateTrainTime(player);
				samurai.say("You can train for up to " + trainTime + " minutes. So make good use of your time.");
				player.drop("money", dojoArea.calculateFee(player.getAtk()));
				samurai.addEvent(new SoundEvent(SoundID.COMMERCE, SoundLayer.CREATURE_NOISE));
				dojoArea.startSession(player, trainTime * MathHelper.SECONDS_IN_ONE_MINUTE);
			}
		};


		// player has never trained before
		samurai.add(ConversationStates.ATTENDING,
				TRAIN_PHRASES,
				new AndCondition(
						new QuestNotStartedCondition(QUEST_SLOT),
						new NotCondition(meetsLevelCapCondition),
						new PlayerHasItemWithHimCondition("assassins id"),
						new NotCondition(dojoFullCondition)),
				ConversationStates.QUESTION_1,
				null,
				new MultipleActions(
						new NPCEmoteAction(samuraiName + " looks over your assassins id.", false),
						new ChatAction() {
							@Override
							public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
								samurai.say("Hmmm, I haven't seen you around here before. But you have the proper credentials. Do you want me to"
										+ " open up the dojo? The fee is " + dojoArea.calculateFee(player.getAtk()) + " money.");
							}
						}));

		// player returns after cooldown period is up
		samurai.add(ConversationStates.ATTENDING,
				TRAIN_PHRASES,
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, 0, STATE_DONE),
						new TimePassedCondition(QUEST_SLOT, 1, COOLDOWN),
						new NotCondition(meetsLevelCapCondition),
						new PlayerHasItemWithHimCondition("assassins id")),
				ConversationStates.QUESTION_1,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						samurai.say("At your skill level, it's " + dojoArea.calculateFee(player.getAtk()) + " money to train in the dojo. Would you like to enter?");
					}
				});

		// player returns before cooldown period is up
		samurai.add(ConversationStates.ATTENDING,
				TRAIN_PHRASES,
				new AndCondition(
						new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, COOLDOWN)),
						new NotCondition(meetsLevelCapCondition)),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, COOLDOWN, "You can't train again yet. Come back in"));

		// player's ATK level is too high
		samurai.add(ConversationStates.ATTENDING,
				TRAIN_PHRASES,
				meetsLevelCapCondition,
				ConversationStates.ATTENDING,
				"At your level of experience, your attack strength it too high to train here at this time.",
				null);

		// player does not have an assassins id
		samurai.add(ConversationStates.ATTENDING,
				TRAIN_PHRASES,
				new AndCondition(
						new NotCondition(meetsLevelCapCondition),
						new NotCondition(new PlayerHasItemWithHimCondition("assassins id"))),
				ConversationStates.ATTENDING,
				"You can't train here without permission from the assassins' HQ.",
				null);

		// player training state is active
		samurai.add(ConversationStates.ATTENDING,
				TRAIN_PHRASES,
				new QuestInStateCondition(QUEST_SLOT, 0, STATE_ACTIVE),
				ConversationStates.ATTENDING,
				"Your current training session has not ended.",
				null);

		// player meets requirements but training area is full
		samurai.add(ConversationStates.ATTENDING,
				TRAIN_PHRASES,
				new AndCondition(
						new NotCondition(meetsLevelCapCondition),
						new PlayerHasItemWithHimCondition("assassins id"),
						new NotCondition(new QuestInStateCondition(QUEST_SLOT, 0, STATE_ACTIVE)),
						dojoFullCondition),
				ConversationStates.ATTENDING,
				FULL_MESSAGE,
				null);

		/* player has enough money to begin training
		 *
		 * XXX: If admin alters player's quest slot, timer/notifier is not removed. Which
		 *      could potentially lead to strange behavior. But this should likely never
		 *      happen on live server. In an attempt to prevent such issues, the old
		 *      timer/notifier will be removed if the player begins a new training session.
		 *      Else the timer will simply be removed once it has run its lifespan.
		 */
		samurai.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				canAffordFeeCondition,
				ConversationStates.IDLE,
				null,
				startTrainingAction);

		// player does not have enough money to begin training
		samurai.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new NotCondition(canAffordFeeCondition),
				ConversationStates.ATTENDING,
				"You don't even have enough money for the #fee.",
				null);

		// player does not want to train
		samurai.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Good luck then.",
				null);
	}

	/**
	 * Calculates the amount of time player can spend in dojo.
	 */
	private int calculateTrainTime(final Player player) {
		int trainTime = BASE_TRAIN_TIME;

		if (player.getLevel() >= 60) {
			trainTime = trainTime + 4;
		}

		return trainTime;
	}
}
