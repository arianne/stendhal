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
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.common.constants.Testing;
import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.core.events.LogoutListener;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.mapstuff.portal.ConditionAndActionPortal;
import games.stendhal.server.entity.mapstuff.portal.Gate;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.TrainingDummy;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.NPCEmoteAction;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.TeleportAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;


public class Dojo implements ZoneConfigurator,LoginListener,LogoutListener {

	/** quest/activity identifier */
	private static final String QUEST_SLOT = "dojo";

	/** cost to use dojo */
	private static final int COST = 5000;

	/** time (in seconds) allowed for training session */
	private static final int TRAIN_TIME = 15 * MathHelper.SECONDS_IN_ONE_MINUTE;

	/** time player must wait to train again */
	private static final int COOLDOWN = 15;

	/** condition to check if training area is full */
	private ChatCondition dojoFullCondition;

	/** quest states */
	private static final String STATE_ACTIVE = "training";
	private static final String STATE_DONE = "done";

	/** position of gate that manages access to training area */
	private static final Point GATE_POS = new Point(22, 72);
	/** position where player is teleported after session ends */
	private static final Point END_POS = new Point(22, 74);

	/** zone info */
	private StendhalRPZone dojoZone;
	private String dojoZoneID;

	/** dojo area */
	private static TrainingArea dojoArea;

	/** NPC that manages dojo area */
	private static final String samuraiName = "Omura Sumitada";
	private SpeakerNPC samurai;

	/** phrases used in conversations */
	private static final List<String> TRAIN_PHRASES = Arrays.asList("train", "training");
	private static final List<String> FEE_PHRASES = Arrays.asList("fee", "cost", "charge");

	/** message when dojo is full */
	private static final String FULL_MESSAGE = "The dojo is full. Come back later.";

	/** message when player tries to enter without paying */
	private static final String NO_ACCESS_MESSAGE = "Hey %s! You can't just walk into the dojo for free.";


	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		// set up the login/logout notifiers
		SingletonRepository.getLoginNotifier().addListener(this);
		SingletonRepository.getLogoutNotifier().addListener(this);

		dojoZone = zone;
		dojoZoneID = zone.getName();
		dojoArea = new TrainingArea(zone, 5, 52, 35, 20);
		dojoArea.setCapacity(16);

		// initialize condition to check if dojo is full
		dojoFullCondition = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				return dojoArea.isFull();
			}
		};

		initEntrance();
		initNPC();
		initDialogue();

		if (Testing.COMBAT) {
			initTrainingDummies();
		}
	}

	/**
	 * Initializes portal & gate entities that manage access to the training area.
	 */
	private void initEntrance() {
		// prevents players who haven't paid from entering if gate is open (must be added before gate)
		dojoZone.add(new DojoConditionAndActionPortal());

		// gate to enter
		final Gate gate = new Gate("h", "palisade_gate", new QuestInStateCondition("dojo", 0, STATE_ACTIVE)) {

			@Override
			protected boolean isAllowed(final RPEntity user) {
				// don't worry about players trying to leave
				if (user.getDirectionToward(this) != Direction.UP) {
					return true;
				}

				// check if player has paid
				if (!super.isAllowed(user)) {
					samurai.say(NO_ACCESS_MESSAGE.replace("%s", user.getName()));
					return false;
				}

				// check if dojo is full
				if (dojoArea.isFull()) {
					samurai.say(FULL_MESSAGE);
					return false;
				}

				return true;
			}

			@Override
			public boolean onUsed(final RPEntity user) {
				if (this.nextTo(user)) {
					if (isAllowed(user)) {
						setOpen(!isOpen());
						return true;
					}
				}
				return false;
			}
		};
		gate.setAutoCloseDelay(2);
		gate.setPosition(GATE_POS.x, GATE_POS.y);
		dojoZone.add(gate);
	}

	private void initNPC() {
		samurai = new SpeakerNPC(samuraiName);
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
		samurai.addReply(FEE_PHRASES, "The fee to #train is " + Integer.toString(COST) + " money.");

		final ChatCondition meetsLevelCapCondition = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				return dojoArea.meetsLevelCap(player, player.getAtk());
			}
		};

		// player has never trained before
		samurai.add(ConversationStates.ATTENDING,
				TRAIN_PHRASES,
				new AndCondition(
						new QuestNotStartedCondition(QUEST_SLOT),
						new NotCondition(meetsLevelCapCondition),
						new PlayerHasItemWithHimCondition("assassins id")),
				ConversationStates.QUESTION_1,
				null,
				new MultipleActions(
						new NPCEmoteAction(samuraiName + " looks over your assassins id.", false),
						new SayTextAction("Hmmm, I haven't seen you around here before."
								+ " But you have the proper credentials. Do you want me to"
								+ " open up the dojo? The fee is " + Integer.toString(COST)
								+ " money.")));

		// player returns after cooldown period is up
		samurai.add(ConversationStates.ATTENDING,
				TRAIN_PHRASES,
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, 0, STATE_DONE),
						new TimePassedCondition(QUEST_SLOT, 1, COOLDOWN),
						new NotCondition(meetsLevelCapCondition),
						new PlayerHasItemWithHimCondition("assassins id")),
				ConversationStates.QUESTION_1,
				"It's " + Integer.toString(COST) + " money to train in the dojo. Woul you like to enter?",
				null);

		// player returns before cooldown period is up
		samurai.add(ConversationStates.ATTENDING,
				TRAIN_PHRASES,
				new AndCondition(
						new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, COOLDOWN)),
						new NotCondition(meetsLevelCapCondition)),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, COOLDOWN, "You can't train again yet. Come back in"));

		// player's RATK level is too high
		samurai.add(ConversationStates.ATTENDING,
				TRAIN_PHRASES,
				meetsLevelCapCondition,
				ConversationStates.ATTENDING,
				"You are too skilled to train here.",
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
				new PlayerHasItemWithHimCondition("money", COST),
				ConversationStates.IDLE,
				"You can train for up to " + Integer.toString(TRAIN_TIME / MathHelper.SECONDS_IN_ONE_MINUTE) + " minutes. So make good use of yer time.",
				new MultipleActions(
						new DropItemAction("money", COST),
						new SetQuestAction(QUEST_SLOT, STATE_ACTIVE + ";" + Integer.toString(TRAIN_TIME)),
						new DojoTimerAction()));

		// player does not have enough money to begin training
		samurai.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new NotCondition(new PlayerHasItemWithHimCondition("money", COST)),
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

	private void initTrainingDummies() {
		// normally added in tiled, but instantiated here so that can be disabled with "Testing.COMBAT"

		// locations of targets
		final List<Node> nodes = Arrays.asList(
				new Node(10, 66),
				new Node(15, 57),
				new Node(28, 57),
				new Node(33, 66));

		for (final Node node: nodes) {
			final TrainingDummy target = new TrainingDummy();
			target.setPosition(node.getX(), node.getY());
			dojoZone.add(target);
		}
	}

	/**
	 * Allows time remaining to be altered by changing quest slot.
	 */
	private Integer updateTimeRemaining(final Player player) {
		try {
			final int timeRemaining = Integer.parseInt(player.getQuest(QUEST_SLOT, 1)) - 1;
			player.setQuest(QUEST_SLOT, 1, Integer.toString(timeRemaining));
			return timeRemaining;
		} catch (NumberFormatException e) {
			// couldn't get time remaining from quest state
			SingletonRepository.getTurnNotifier().dontNotify(new Timer(player));

			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Teleports player out of archery range training area.
	 */
	private void endTrainingSession(final Player player) {
		if (player.get("zoneid").equals(dojoZoneID)) {
			samurai.say("Your training time is up " + player.getName() + ".");
		}
		if (dojoArea.contains(player)) {
			player.teleport(dojoZoneID, END_POS.x, END_POS.y, null, null);
		}

		player.setQuest(QUEST_SLOT, STATE_DONE + ";" + Long.toString(System.currentTimeMillis()));
	}

	@Override
	public void onLoggedIn(final Player player) {
		// don't allow players to login within archery range area boundaries
		if (dojoArea.contains(player) || (player.getX() == GATE_POS.x && player.getY() == GATE_POS.y)) {
			player.teleport(dojoZoneID, END_POS.x, END_POS.y, null, null);
		}

		final String sessionState = player.getQuest(QUEST_SLOT, 0);
		if (sessionState != null && sessionState.equals(STATE_ACTIVE)) {
			final String sessionTimeString = player.getQuest(QUEST_SLOT, 1);
			if (sessionTimeString != null) {
				// re-initialize turn notifier if player still has active training session
				new DojoTimerAction().fire(player, null, null);
			}
		}
	}

	@Override
	public void onLoggedOut(final Player player) {
		// disable timer/notifier
		SingletonRepository.getTurnNotifier().dontNotify(new Timer(player));
	}


	/**
	 * Notifies player of time remaining for training & ends training session.
	 */
	private class Timer implements TurnListener {

		private final WeakReference<Player> timedPlayer;

		private Integer timeRemaining = 0;

		protected Timer(final Player player) {
			timedPlayer = new WeakReference<Player>(player);

			try {
				final String questState = timedPlayer.get().getQuest(QUEST_SLOT, 0);
				if (questState != null && questState.equals(STATE_ACTIVE)) {
					// set player's time remaining from quest slot value
					timeRemaining = Integer.parseInt(timedPlayer.get().getQuest(QUEST_SLOT, 1));
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onTurnReached(int currentTurn) {
			final Player playerTemp = timedPlayer.get();

			if (playerTemp != null) {
				if (timeRemaining != null && timeRemaining > 0) {
					// notify players at 10 minute mark & every minute after 5 minute mark
					if (timeRemaining == 10 * MathHelper.SECONDS_IN_ONE_MINUTE ||
							(timeRemaining <= 5 * MathHelper.SECONDS_IN_ONE_MINUTE && timeRemaining % 60 == 0)) {
						samurai.say(playerTemp.getName() + ", you have " + TimeUtil.timeUntil(timeRemaining) + " left.");
					}
					// remaining time needs to be updated every second in order to be saved if player logs out
					timeRemaining = updateTimeRemaining(playerTemp);
					SingletonRepository.getTurnNotifier().notifyInSeconds(1, this);
				} else {
					endTrainingSession(playerTemp);
				}
			}
		}

		@Override
		public int hashCode() {
			final Player player = timedPlayer.get();

			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((player == null) ? 0 : player.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			final Player player = timedPlayer.get();

			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Timer other = (Timer) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (player == null) {
				if (other.timedPlayer.get() != null) {
					return false;
				}
			} else if (!player.equals(other.timedPlayer.get())) {
				return false;
			}
			return true;
		}

		private Dojo getOuterType() {
			return Dojo.this;
		}
	}

	/**
	 * Action that notifies
	 */
	private class DojoTimerAction implements ChatAction {

		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
			// remove any existing notifiers
			SingletonRepository.getTurnNotifier().dontNotify(new Timer(player));

			// create the new notifier
			SingletonRepository.getTurnNotifier().notifyInTurns(0, new Timer(player));
		}
	}

	/**
	 * Special portal for checking multiple conditions.
	 */
	private class DojoConditionAndActionPortal extends ConditionAndActionPortal {

		/** messages for different rejection reasons */
		private final Map<ChatCondition, List<String>> rejections;

		/** message for when player is pushed into training area by another player */
		private final String pushMessage = "Hey %s! Don't push!";

		/** determines if entity was pushed onto portal */
		private boolean wasPushed = false;
		private RPEntity pusher = null;


		public DojoConditionAndActionPortal() {
			super(null, null);

			rejections = new LinkedHashMap<>();
			rejections.put(
					new QuestInStateCondition(QUEST_SLOT, 0, STATE_ACTIVE),
					Arrays.asList(
							NO_ACCESS_MESSAGE,
							pushMessage));
			rejections.put(
					new NotCondition(dojoFullCondition),
					Arrays.asList(
							FULL_MESSAGE,
							pushMessage));

			setPosition(GATE_POS.x, GATE_POS.y);
			setIgnoreNoDestination(true);
			setResistance(0);
			setForceStop(true);
		}

		private String formatMessage(String message, final RPEntity user) {
			return String.format(message, user.getName());
		}

		/**
		 * Checks the list of conditions & sets the rejection message text.
		 */
		@Override
		protected boolean isAllowed(final RPEntity user) {
			int msgIndex = 0;
			RPEntity msgTarget = user;
			if (wasPushed && pusher != null) {
				msgIndex = 1;
				msgTarget = pusher;
			}

			final Sentence sentence = ConversationParser.parse(user.get("text"));
			for (final ChatCondition cond : rejections.keySet()) {
				if (!cond.fire((Player) user, sentence, this)) {
					setRejectedAction(new MultipleActions(
							new TeleportAction(dojoZoneID, GATE_POS.x, GATE_POS.y + 1, null),
							new SayTextAction(formatMessage(rejections.get(cond).get(msgIndex), msgTarget))));
					return false;
				}
			}

			return true;
		}

		@Override
		public boolean onUsed(final RPEntity user) {
			boolean res = false;

			// don't worry about players trying to leave
			final Direction dir = user.getDirectionToward(this);
			if (dir == Direction.UP) {
				res = super.onUsed(user);
			}

			return res;
		}

		/**
		 * Check access for players pushed onto portal.
		 */
		@Override
		public void onPushedOntoFrom(final RPEntity pushed, final RPEntity pusher, final Point prevPos) {
			wasPushed = true;
			if (pusher != null) {
				this.pusher = pusher;
			}

			// check if entity is being pushed from the right
			if (prevPos.x == getX() + 1) {
				super.onUsed(pushed);
			}

			// reset pushed status
			wasPushed = false;
			this.pusher = null;
		}

		/**
		 * Override to avoid java.lang.NullPointerException.
		 */
		@Override
		protected void rejected(final RPEntity user) {
			if (user instanceof Player) {
				final Player player = (Player) user;

				if (rejectedAction != null) {
					rejectedAction.fire(player, ConversationParser.parse(user.get("text")), new EventRaiser(samurai));
				}

				if (forceStop) {
					player.forceStop();
					return;
				}
			}

			user.stop();
		}
	}
}
