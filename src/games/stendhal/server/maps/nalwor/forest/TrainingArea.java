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
import java.awt.Rectangle;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.Direction;
import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.zone.NoTeleportIn;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.core.events.LogoutListener;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.mapstuff.portal.ConditionAndActionPortal;
import games.stendhal.server.entity.mapstuff.portal.Gate;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.TeleportAction;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;
import games.stendhal.server.util.TimeUtil;


/**
 * Representation of an area where a player can train.
 */
public class TrainingArea extends Area implements LoginListener,LogoutListener {

	private static final Logger logger = Logger.getLogger(TrainingArea.class);

	// quest slot
	private final String QUEST_SLOT;
	// point to which player is teleported after sessions
	private final Point END_POS;
	// location of gate
	private final Point GATE_POS;
	private final TrainingAreaGate gate;
	// direction from which entrance is allowed
	private final Direction entersFrom;
	// maximum number of players allowed in area at a single time.
	private Integer maxCapacity;

	private final TrainerNPC trainer;

	/** quest states */
	private static final String STATE_ACTIVE = "training";
	private static final String STATE_DONE = "done";

	private static final Map<String, TrainingTimer> activeTimers = new HashMap<>();


	public TrainingArea(final String slot, final StendhalRPZone zone, final Rectangle shape,
			final TrainerNPC trainer, final Point endPos, final Point gatePos, final Direction entersFrom) {
		super(zone, shape);

		this.QUEST_SLOT = slot;
		this.trainer = trainer;
		/** position where player is teleported after session ends */
		this.END_POS = endPos;
		/** position of gate that manages access to training area */
		this.GATE_POS = gatePos;
		this.entersFrom = entersFrom;

		// prevents players who haven't paid from entering if gate is open (must be added before gate)
		zone.add(new TrainingAreaConditionAndActionPortal());

		String orientation = "v";
		if (entersFrom.equals(Direction.UP) || entersFrom.equals(Direction.DOWN)) {
			orientation = "h";
		}

		gate = new TrainingAreaGate(orientation, "palisade_gate", new QuestInStateCondition(QUEST_SLOT, 0, STATE_ACTIVE));
		gate.setAutoCloseDelay(2);
		gate.setPosition(gatePos.x, gatePos.y);
		zone.add(gate);

		new NoTeleportIn().configureZone(zone, shape);
		new NoTeleportIn().configureZone(getZone(), new Rectangle(gatePos.x, gatePos.y, 1, 1));

		// set up the login/logout notifiers
		SingletonRepository.getLoginNotifier().addListener(this);
		SingletonRepository.getLogoutNotifier().addListener(this);
	}

	public TrainingArea(final String slot, final StendhalRPZone zone, final int x, final int y, final int width, final int height,
			final TrainerNPC trainer, final Point endPos, final Point gatePos, final Direction entersFrom) {
		this(slot, zone, new Rectangle(x, y, width, height), trainer, endPos, gatePos, entersFrom);
	}

	/**
	 * Sets the maximum number of players allow to occupy the area at a single time.
	 *
	 * @param capacity
	 * 		Capacity limit.
	 */
	public void setCapacity(final int capacity) {
		maxCapacity = capacity;
	}

	@Override
	public boolean contains(final Entity entity) {
		if (super.contains(entity)) {
			return true;
		}
		return super.getZone().has(entity.getID()) && (entity.getX() == GATE_POS.x && entity.getY() == GATE_POS.y);
	}

	/**
	 * Checks if a player qualifies for training.
	 *
	 * @param player
	 * 		Player to calculate cap for.
	 * @param statLevel
	 * 		Stat to compare cap against.
	 * @return
	 * 		<code>true</code> if the player's stat/level is too high to train.
	 */
	public boolean meetsLevelCap(final Player player, final int statLevel) {
		return statLevel >= calculateLevelCap(player.getLevel());
	}

	/**
	 * Calculates the stat level cap at which a player cannot train.
	 *
	 * @param playerLevel
	 * 		Level of player to be checked.
	 * @return
	 * 		Capped stat level at which player cannot use area.
	 */
	private int calculateLevelCap(final int playerLevel) {
		if (playerLevel <= 55) {
			return playerLevel;
		}

		return 50 + playerLevel / 10;
	}

	/**
	 * Checks if the area is full.
	 *
	 * @return
	 * 		<code>false</code> if the number of players in area are less than maximum
	 * 		capacity or there is not maximum capacity.
	 */
	public boolean isFull() {
		if (maxCapacity == null) {
			return false;
		}

		return getPlayers().size() >= maxCapacity;
	}

	/**
	 * Checks if the entity is exiting the training area.
	 *
	 * @param trainee
	 * 		Entity to check.
	 * @return
	 * 		<code>true</code> if the entity is traversing the gate in the direction
	 * 		away from training area.
	 */
	public boolean isExiting(final RPEntity trainee) {
		return !trainee.getDirectionToward(gate).oppositeDirection().equals(entersFrom);
	}

	/**
	 * Retrieves the maximum number of players that can occupy the training area
	 * at one time.
	 *
	 * @return
	 * 		Max occupants.
	 */
	public int getMaxCapacity() {
		if (maxCapacity == null) {
			return -1;
		}

		return maxCapacity;
	}

	/**
	 * Calculates a fee to use the area.
	 *
	 * Base fee is 625 & doubles every 20 levels.
	 *
	 * @param statLevel
	 * 		Level of stat to be used as factor.
	 * @return
	 * 		Suggested fee.
	 */
	public int calculateFee(final int statLevel) {
		int operand = ((int) Math.floor(statLevel / 20));
		return ((int) Math.pow(2, operand)) * 625;
	}

	/**
	 * Starts timer to track players session time.
	 *
	 * @param player
	 * 		Player to track.
	 * @param trainTime
	 * 		Amount of time (in seconds) player is allowed to train.
	 */
	private void startTimer(final Player player, final int trainTime) {
		final String playerName = player.getName();

		// deactivate any residual timers
		TrainingTimer timer = activeTimers.get(playerName);
		if (timer != null) {
			activeTimers.remove(playerName);
			SingletonRepository.getTurnNotifier().dontNotify(timer);
		}

		timer = new TrainingTimer(player, trainTime);
		activeTimers.put(playerName, timer);
	}

	/**
	 * Sets quest slot state & starts timer for session.
	 *
	 * @param player
	 * 		Player starting training session.
	 * @param trainTime
	 * 		Amount of time (in seconds) player is allowed to train.
	 */
	public void startSession(final Player player, final int trainTime) {
		player.setQuest(QUEST_SLOT, STATE_ACTIVE + ";" + Integer.toString(trainTime));
		startTimer(player, trainTime);

		final TrainingTimer timer = activeTimers.get(player.getName());
		if (timer != null) {
			timer.skipFirstNotify();
			timer.init();
		}
	}

	/**
	 * Resumes a training session without setting quest slot state nor
	 * skipping first notification to player.
	 *
	 * @param player
	 * 		Player starting training session.
	 * @param trainTime
	 * 		Amount of time (in seconds) player is allowed to train.
	 */
	private void resumeSession(final Player player, final int trainTime) {
		startTimer(player, trainTime);

		final TrainingTimer timer = activeTimers.get(player.getName());
		if (timer != null) {
			timer.init();
		}
	}

	/**
	 * Teleports player out of archery range training area.
	 */
	public void endSession(final Player player) {
		final StendhalRPZone zone = getZone();

		if (player.getZone().equals(zone)) {
			trainer.say("Your training time is up " + player.getName() + ".");
		}
		if (contains(player)) {
			player.teleport(zone, END_POS.x, END_POS.y, null, null);
		}

		setSessionDone(player);
	}

	/**
	 * Sets the training state to "done" & cooldown time.
	 */
	private void setSessionDone(final Player player) {
		player.setQuest(QUEST_SLOT, STATE_DONE + ";" + Long.toString(System.currentTimeMillis()));
		activeTimers.remove(player.getName());
	}

	@Override
	public void onLoggedIn(final Player player) {
		// don't allow players to login within dojo area boundaries
		if (contains(player)) {
			player.teleport(getZone(), END_POS.x, END_POS.y, null, null);
		}

		final String sessionState = player.getQuest(QUEST_SLOT, 0);
		if (sessionState != null && sessionState.equals(STATE_ACTIVE)) {
			final String sessionTimeString = player.getQuest(QUEST_SLOT, 1);
			if (sessionTimeString != null) {
				try {
					resumeSession(player, Integer.parseInt(sessionTimeString));
				} catch (final NumberFormatException e) {
					logger.error("Session time not in number format, cannot resume training. Setting quest slot to \"done\" state.");
					e.printStackTrace();
					setSessionDone(player);
				}
			}
		}
	}

	@Override
	public void onLoggedOut(final Player player) {
		/** disable timer/notifier
		 *
		 *  NOTE: if player gets disconnected without proper logout, the time will restart
		 *  from the previous session start/resume time.
		 *  FIXME: is there a way to catch player timeout?
		 */
		final TrainingTimer timer = activeTimers.get(player.getName());
		final String state = player.getQuest(QUEST_SLOT, 0);
		if (timer != null && (state != null && state.equals(STATE_ACTIVE))) {
			player.setQuest(QUEST_SLOT, 1, Integer.toString(timer.getRemainingSecs()));
			SingletonRepository.getTurnNotifier().dontNotify(timer);
		} else if (state != null && !state.equals(STATE_DONE)) {
			setSessionDone(player);
		}
	}


	/**
	 * Notifies player of time remaining for training & ends training session.
	 */
	private class TrainingTimer implements TurnListener {

		// player that is being timed
		private final WeakReference<Player> timedPlayer;

		// timestamp at which training session will end
		private Long targetTimeStamp = 0L;

		private boolean skipNotify = false;


		/**
		 * Constructor.
		 *
		 * @param player
		 * 		Player to be timed.
		 * @param secsRemaining
		 * 		Time remaining (in seconds) for player to train.
		 */
		private TrainingTimer(final Player player, final int secsRemaining) {
			timedPlayer = new WeakReference<Player>(player);
			targetTimeStamp = System.currentTimeMillis() + (secsRemaining * 1000);
		}

		public void init() {
			onTurnReached(0);
		}

		public void skipFirstNotify() {
			skipNotify = true;
		}

		/**
		 * Retrieves the remaining time in training session.
		 *
		 * @return
		 * 		Remaining time in milliseconds.
		 */
		public long getRemainingMillis() {
			long remaining = targetTimeStamp - System.currentTimeMillis();
			if (remaining < 0) {
				remaining = 0;
			}

			return remaining;
		}

		/**
		 * Retrieves the remaining time in training session.
		 *
		 * @return
		 * 		Remaining time in seconds.
		 */
		public int getRemainingSecs() {
			return (int) Math.floor(getRemainingMillis() / 1000);
		}

		@Override
		public void onTurnReached(final int currentTurn) {
			final Player player = timedPlayer.get();

			final int secsRemain = getRemainingSecs();
			if (secsRemain <= 0) {
				endSession(player);
				return;
			}

			final int minsRemain = Math.round((float) secsRemain / 60);

			int notifyIn;
			if (minsRemain == 1 ) {
				notifyIn = 1;
			} else if (minsRemain > 10) {
				notifyIn = minsRemain % 10;
				if (notifyIn == 0) {
					notifyIn = 10;
				}
			} else if (minsRemain > 5) {
				notifyIn = minsRemain % 5;
				if (notifyIn == 0) {
					notifyIn = 5;
				}
			} else {
				// set default value to notify at last minute mark
				notifyIn = minsRemain - 1;
				if (notifyIn < 0) {
					notifyIn = 0; // don't allow negative
				}
			}

			if (skipNotify) {
				skipNotify = false;
			} else {
				trainer.say(player.getName() + ", you have " + TimeUtil.timeUntil(minsRemain * 60) + " left.");
			}

			SingletonRepository.getTurnNotifier().notifyInSeconds(notifyIn * 60, this);
		}
	}

	/**
	 * Manages access to training area when gate is open.
	 *
	 * This is used more like a conditional walk blocker instead of a portal.
	 */
	private class TrainingAreaConditionAndActionPortal extends ConditionAndActionPortal {

		/** messages for different rejection reasons */
		private final Map<ChatCondition, List<String>> rejections;

		/** message for when player is pushed into training area by another player */
		private final String pushMessage = "Hey %s! Don't push!";

		/** determines if entity was pushed onto portal */
		private boolean wasPushed = false;
		private RPEntity pusher = null;


		public TrainingAreaConditionAndActionPortal() {
			super(null, null);

			rejections = new LinkedHashMap<>();
			rejections.put(
					new QuestInStateCondition(QUEST_SLOT, 0, STATE_ACTIVE),
					Arrays.asList(
							trainer.getRejectMessage(),
							pushMessage));
			rejections.put(
					new ChatCondition() {
						@Override
						public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
							return !isFull();
						}
					},
					Arrays.asList(
							trainer.getFullMessage(),
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

			int exitX = GATE_POS.x;
			int exitY = GATE_POS.y;

			switch (entersFrom) {
			case LEFT:
				exitX--;
				break;
			case RIGHT:
				exitX++;
				break;
			case UP:
				exitY--;
				break;
			case DOWN:
				exitY++;
				break;
			default:
				break;
			}

			final Sentence sentence = ConversationParser.parse(user.get("text"));
			for (final ChatCondition cond : rejections.keySet()) {
				if (!cond.fire((Player) user, sentence, this)) {
					setRejectedAction(new MultipleActions(
							new TeleportAction(getZone().getName(), exitX, exitY, null),
							new SayTextAction(formatMessage(rejections.get(cond).get(msgIndex), msgTarget))));
					return false;
				}
			}

			return true;
		}

		@Override
		public boolean onUsed(final RPEntity user) {
			// don't worry about players trying to leave
			if (isExiting(user)) {
				return false;
			}

			return super.onUsed(user);
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

			// check if entity is being pushed from entering direction
			switch (entersFrom) {
			case UP:
				if (prevPos.y == getY() - 1) {
					super.onUsed(pushed);
				}
				break;
			case DOWN:
				if (prevPos.y == getY() + 1) {
					super.onUsed(pushed);
				}
				break;
			case LEFT:
				if (prevPos.x == getX() - 1) {
					super.onUsed(pushed);
				}
				break;
			case RIGHT:
				if (prevPos.x == getX() + 1) {
					super.onUsed(pushed);
				}
				break;
			default:
				break;
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
					rejectedAction.fire(player, ConversationParser.parse(user.get("text")), new EventRaiser(trainer));
				}

				if (forceStop) {
					player.forceStop();
					return;
				}
			}

			user.stop();
		}
	}

	/**
	 * Gate class that manages access to training area.
	 */
	private class TrainingAreaGate extends Gate {

		private TrainingAreaGate(final String orientation, final String image, final ChatCondition condition) {
			super(orientation, image, condition);
		}

		@Override
		protected boolean isAllowed(final RPEntity user) {
			// don't worry about players trying to leave
			if (isExiting(user)) {
				return true;
			}

			// check if player has paid
			if (!super.isAllowed(user)) {
				trainer.say(trainer.getRejectMessage().replace("%s", user.getName()));
				return false;
			}

			// check if area is full
			if (isFull()) {
				trainer.say(trainer.getFullMessage());
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
	}

	/**
	 * NPC class that manages access to training area.
	 */
	public static class TrainerNPC extends SpeakerNPC {

		private final String fullMessage;
		private final String rejectMessage;


		public TrainerNPC(final String name, final String fullMessage, final String rejectMessage) {
			super(name);

			this.fullMessage = fullMessage;
			this.rejectMessage = rejectMessage;
		}

		public String getFullMessage() {
			return fullMessage;
		}

		public String getRejectMessage() {
			return rejectMessage;
		}
	}
}
