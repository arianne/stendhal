/* $Id$ */
/***************************************************************************
 *                    (C) Copyright 2003-2024 - Arianne                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.player;

import static games.stendhal.common.NotificationType.getServerNotificationType;
import static games.stendhal.common.constants.Actions.ADMINLEVEL;
import static games.stendhal.common.constants.Actions.AUTOWALK;
import static games.stendhal.common.constants.Actions.AWAY;
import static games.stendhal.common.constants.Actions.GHOSTMODE;
import static games.stendhal.common.constants.Actions.GRUMPY;
import static games.stendhal.common.constants.Actions.INVISIBLE;
import static games.stendhal.common.constants.Actions.TELECLICKMODE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import games.stendhal.common.Direction;
import games.stendhal.common.ItemTools;
import games.stendhal.common.KeyedSlotUtil;
import games.stendhal.common.Level;
import games.stendhal.common.NotificationType;
import games.stendhal.common.TradeState;
import games.stendhal.common.Version;
import games.stendhal.common.constants.Nature;
import games.stendhal.common.constants.SoundID;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.constants.Testing;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.WordList;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.events.TutorialNotifier;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.rp.achievement.AchievementNotifier;
import games.stendhal.server.entity.DressedEntity;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Killer;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.DomesticAnimal;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.RingOfLife;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour.ExpireOutfit;
import games.stendhal.server.entity.slot.Slots;
import games.stendhal.server.entity.status.StatusType;
import games.stendhal.server.events.PrivateTextEvent;
import games.stendhal.server.events.SoundEvent;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.SyntaxException;

public class Player extends DressedEntity implements UseListener {

	private static final String LAST_PLAYER_KILL_TIME = "last_player_kill_time";

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(Player.class);

	/**
	 * A random generator (for karma payout).
	 */
	private static final Random KARMA_RANDOMIZER = new Random();

	/**
	 * Currently active client directions (in oldest-newest order).
	 */
	private final List<Direction> directions;

	/**
	 * Karma (luck).
	 */
	private double karma;
	/**
	 * number of successful trades
	 */
	private int tradescore;

	/**
	 * List of portals that have been "unlocked" for this player.
	 */
	private final List<Integer> unlockedPortals;

	private final PlayerQuests quests = new PlayerQuests(this);
	private final PlayerDieer dieer = new PlayerDieer(this);
	private final PlayerTrade trade = new PlayerTrade(this);
	private final KillRecording killRec = new KillRecording(this);
	private final PetOwner petOwner = new PetOwner(this);
	private final PlayerLootedItemsHandler itemCounter = new PlayerLootedItemsHandler(
			this);

	/**
	 * The number of minutes that this player has been logged in on the server.
	 */
	private int age;

	/**
	 * The last player who privately talked to this player using the /tell
	 * command. It needs to be stored non-persistently so that /answer can be
	 * used.
	 */
	private String lastPrivateChatterName;

	private final PlayerChatBucket chatBucket;

	/**
	 * all identifiers of reached achievements, filled on login of player
	 */
	private Set<String> reachedAchievements;

	/**
	 * preferred language
	 */
	private String language;

	/**
	 * version of the client
	 */
	private String clientVersion;
	/**
	 * The turn when the player last time pushed something.
	 */
	private int turnOfLastPush;
	/**
	 * The turn the player started moving using the keyboard. Used for detecting
	 * quick presses ment to move one tile.
	 */
	private int startMoveTurn;

	/**
	 * last client action timestamp
	 */
	private long lastClientActionTimestamp = System.currentTimeMillis();

	public static void generateRPClass() {
		try {
			PlayerRPClass.generateRPClass();
		} catch (final SyntaxException e) {
			logger.error("cannot generateRPClass", e);
		}
	}

	public static Player createZeroLevelPlayer(final String characterName,
			RPObject template) {
		/*
		 * TODO: Update to use Player and RPEntity methods.
		 */
		final Player player = new Player(new RPObject());
		player.setID(RPObject.INVALID_ID);

		player.put("type", "player");
		player.put("name", characterName);
		player.put("base_hp", 100);
		player.put("hp", 100);
		player.put("atk", 10);
		player.put("atk_xp", 0);
		player.put("def", 10);
		player.put("def_xp", 0);
		if (Testing.COMBAT) {
			player.put("ratk", 10);
			player.put("ratk_xp", 0);
		}
		player.put("level", 0);
		player.setXP(0);

		// define outfit
		Outfit outfit = null;
		if (template != null) {
			if (template.has("outfit_ext")) {
				outfit = new Outfit(template.get("outfit_ext"));
			} else if (template.has("outfit")) {
				outfit = new Outfit(Integer.toString(template.getInt("outfit")));
			}
		}

		if (outfit == null || !outfit.isChoosableByPlayers()) {
			outfit = Outfit.getRandomOutfit();
		}

		player.setOutfit(outfit);

		for (final String slot : Arrays.asList("armor", "rhand")) {
			player.addSlot(slot);
		}

		player.update();
		Entity entity = SingletonRepository.getEntityManager().getItem(
				"leather armor");
		RPSlot slot = player.getSlot("armor");
		slot.add(entity);

		entity = SingletonRepository.getEntityManager().getItem("club");
		slot = player.getSlot("rhand");
		slot.add(entity);

		return player;
	}

	public static void destroy(final Player player) {
		final String name = player.getName();

		player.getPetOwner().destroy();
		player.stop();
		player.stopAttack();
		player.trade.cancelTradeBecauseOfLogout();

		/*
		 * Normally a zoneid attribute shouldn't logically exist after an entity
		 * is removed from a zone, but we need to keep it for players so that it
		 * can be serialised.
		 *
		 * TODO: Find a better way to decouple "active" zone info from "resume"
		 * zone info, or save just before removing from zone instead.
		 */

		player.getZone().remove(player);

		player.disconnected = true;

		if (name != null) {
			WordList.getInstance().unregisterSubjectName(name);
		}
	}

	public Player(final RPObject object) {
		super(object);

		setRPClass("player");
		put("type", "player");
		// HACK: postman as NPC
		if (object.has("name") && object.get("name").equals("postman")) {
			put("title_type", "npc");
		}

		if (getAdminLevel() > 1000) {
			chatBucket = new AdminChatBucket();
		} else {
			chatBucket = new PlayerChatBucket();
		}

		setSize(1, 1);

		directions = new ArrayList<Direction>();

		// Beginner's luck (unless overridden by update)
		karma = 10.0;
		tradescore = 0;
		baseSpeed = 1.0;
		update();
		// Ensure that players do not accidentally get stored with zones
		if (isStorable()) {
			unstore();
			logger.error("Player " + getName() + " was marked storable.",
					new Throwable());
		}

		unlockedPortals = new LinkedList<Integer>();
		updateModifiedAttributes();
	}

	/**
	 * Add an active client direction.
	 *
	 * @param direction
	 *            direction
	 */
	public void addClientDirection(final Direction direction) {
		if (hasPath()) {
			clearPath();
		}

		startMoveTurn = SingletonRepository.getRuleProcessor().getTurn();
		directions.remove(direction);
		directions.add(direction);
	}

	/**
	 * Remove an active client direction.
	 *
	 * @param direction
	 *            direction
	 */
	public void removeClientDirection(final Direction direction) {
		directions.remove(direction);
	}

	/**
	 * Apply the most recent active client direction.
	 *
	 * @param stopOnNone
	 *            Stop movement if no (valid) directions are active if
	 *            <code>true</code>.
	 */
	@SuppressWarnings("unused")
	public void applyClientDirection(final boolean stopOnNone) {
		int size;
		Direction direction;

		/*
		 * For now just take last direction.
		 *
		 * Eventually try each (last-to-first) until a non-blocked one is found
		 * (if any).
		 */
		size = directions.size();
		if (size != 0) {
			direction = directions.get(size - 1);

			// as an effect of the poisoning, the player's controls
			// are switched to make it difficult to navigate.
			if (hasStatus(StatusType.POISONED) || has("status_confuse")) {
				direction = direction.oppositeDirection();
			}

			setDirection(direction);
			setSpeed(getBaseSpeed());
		}
	}

	@Override
	public boolean isObstacle(final Entity entity) {
		if (entity instanceof Player) {
			if (getZone().getName().equals(PlayerDieer.DEFAULT_DEAD_AREA)) {
				return false;
			}
		}

		return super.isObstacle(entity);
	}

	/**
	 * Request stopping the player, unless the player started moving the same
	 * turn. Intended for client initiated stops, that should not prevent the
	 * player moving one tile with a quick key press.
	 */
	public void requestStop() {
		int turn = SingletonRepository.getRuleProcessor().getTurn();

		if (turn != startMoveTurn) {
			stop();
		} else {
			TurnNotifier.get().notifyInTurns(1, new TurnListener() {
				@Override
				public void onTurnReached(int currentTurn) {
					/*
					 * Check the turn again. The player may have sent yet
					 * another move command, and we do not want to cancel that.
					 */
					if (currentTurn != startMoveTurn) {
						stop();
					}
				}
			});
		}
	}

	/**
	 * Stop and clear any active directions.
	 */
	@Override
	public void stop() {
		/* Bypass stopping the player if autoWalkState is <b>true</b>. */
		if (!this.has(AUTOWALK)) {
			directions.clear();
			super.stop();
		}
	}

	/**
	 * Forces player to stop moving, bypassing auto-walk
	 */
	public void forceStop() {
		this.remove(AUTOWALK);
		directions.clear();
		super.stop();
	}

	/**
	 * Get the away message.
	 *
	 * @return The away message, or <code>null</code> if unset.
	 */
	public String getAwayMessage() {
		return get(AWAY);
	}

	/**
	 * Set the away message.
	 *
	 * @param message
	 *            An away message, or <code>null</code>.
	 */
	public void setAwayMessage(final String message) {
		if (message != null) {
			put(AWAY, message);
		} else if (has(AWAY)) {
			remove(AWAY);
		}
	}

	/**
	 * Get the grumpy message.
	 *
	 * @return The grumpy message, or <code>null</code> if unset.
	 */
	public String getGrumpyMessage() {
		return get(GRUMPY);
	}

	/**
	 * Set the grumpy message.
	 *
	 * @param message
	 *            A grumpy message, or <code>null</code>.
	 */
	public void setGrumpyMessage(final String message) {
		if (message != null) {
			put(GRUMPY, message);
		} else if (has(GRUMPY)) {
			remove(GRUMPY);
		}

	}

	/**
	 * Give the player some karma (good or bad).
	 *
	 * @param karmaToAdd
	 *            An amount of karma to add/subtract.
	 */
	@Override
	public void addKarma(final double karmaToAdd) {
		this.karma += karmaToAdd;

		put("karma", this.karma);
		new GameEvent(this.getName(), "added karma",
				Integer.toString((int) karmaToAdd),
				Integer.toString((int) karma)).raise();
	}

	/**
	 * Get the current amount of karma.
	 *
	 * @return The current amount of karma.
	 *
	 * @see #addKarma(double)
	 */
	@Override
	public double getKarma() {
		return karma;
	}

	/**
	 * Use some of the player's karma. A positive value indicates good
	 * luck/energy. A negative value indicates bad luck/energy. A value of zero
	 * should cause no change on an action or outcome.
	 *
	 * @param scale
	 *            A positive number.
	 *
	 * @return A number between -scale and scale.
	 */
	@Override
	public double useKarma(final double scale) {
		return useKarma(-scale, scale);
	}

	/**
	 * Use some of the player's karma. A positive value indicates good
	 * luck/energy. A negative value indicates bad luck/energy. A value of zero
	 * should cause no change on an action or outcome. The granularity is
	 * <code>0.01</code> (%1 unit).
	 *
	 * @param negLimit
	 *            The lowest negative value returned.
	 * @param posLimit
	 *            The highest positive value returned.
	 *
	 * @return A number within negLimit &lt;= 0 &lt;= posLimit.
	 */
	@Override
	public double useKarma(final double negLimit, final double posLimit) {
		return useKarma(negLimit, posLimit, 0.01);
	}

	/**
	 * Use some of the player's karma. A positive value indicates good
	 * luck/energy. A negative value indicates bad luck/energy. A value of zero
	 * should cause no change on an action or outcome.
	 *
	 * @param negLimit
	 *            The lowest negative value returned.
	 * @param posLimit
	 *            The highest positive value returned.
	 * @param granularity
	 *            The amount that any extracted karma is a multiple of.
	 *
	 * @return A number within negLimit &lt;= 0 &lt;= posLimit.
	 */
	@Override
	public double useKarma(final double negLimit, final double posLimit,
			final double granularity) {
		double limit;
		double score;

		if (logger.isDebugEnabled()) {
			logger.debug("karma request: " + negLimit + " <= x <= " + posLimit);
		}

		/*
		 * Positive or Negative?
		 */
		if (karma < 0.0) {
			if (negLimit >= 0.0) {
				return 0.0;
			}

			limit = Math.max(negLimit, karma);
		} else {
			if (posLimit <= 0.0) {
				return 0.0;
			}

			limit = Math.min(posLimit, karma);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("karma limit: " + limit);
		}

		/*
		 * Give at least 20% of possible payout
		 */
		score = (0.2 + KARMA_RANDOMIZER.nextDouble() * 0.8) * limit;

		/*
		 * Clip to granularity. Use floor() instead of round() so that the
		 * player never uses more karma than she has.
		 */
		score = Math.floor(score / granularity) * granularity;

		/*
		 * with a lucky charm you use up less karma to be just as lucky
		 */
		if (this.isEquipped("lucky charm")) {
			karma -= 0.5 * score;
		} else {
			karma -= score;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("karma given: " + score);
		}

		put("karma", karma);

		return score;
	}

	/**
	 * increments the number of successful trades by 1
	 */
	public void incrementTradescore() {
		this.tradescore += 1;
		put("tradescore", this.tradescore);
	}

	public int getTradescore() {
		return this.tradescore;
	}

	/**
	 *
	 * @return List of portals that have been unlocked for this player.
	 */
	public List<Integer> getUnlockedPortals() {
		return unlockedPortals;
	}

	/**
	 * Removes the portal from the list of unlocked portals.
	 *
	 * @param ID
	 *            Portal's ID
	 */
	public void lockPortal(final int ID) {
		int index = unlockedPortals.size() - 1;
		if (unlockedPortals.contains(ID)) {
			// Iterate list backwards
			while (index >= 0) {
				if (unlockedPortals.get(index) == ID) {
					unlockedPortals.remove(index);
					logger.debug("Removed portal ID " + Integer.toString(ID)
							+ " from player " + getName() + ".");
				}
				index -= 1;
			}
		}
	}

	/**
	 * Adds a portal ID to a list of "unlocked" portals for player.
	 *
	 * @param ID
	 *            Portal's ID
	 */
	public void unlockPortal(final int ID) {
		if (!unlockedPortals.contains(ID)) {
			unlockedPortals.add(ID);
			logger.debug("Added portal ID " + Integer.toString(ID)
					+ " to unlocked portals for player " + getName() + ".");
		}
	}

	/**
	 * Process changes that to the object attributes. This may be called several
	 * times (unfortunately) due to the requirements of the class's constructor,
	 * sometimes before prereqs are initialised.
	 */
	@Override
	public void update() {
		super.update();

		if (has("xp")) {
			// Force level to be updated.
			updateLevel();
		}

		if (has("age")) {
			age = getInt("age");
		}

		if (has("karma")) {
			karma = getDouble("karma");
		}
		if (has("tradescore")) {
			tradescore = getInt("tradescore");
		}
	}

	/**
	 * Add a player ignore entry.
	 *
	 * @param name
	 *            The player name.
	 * @param duration
	 *            The ignore duration (in minutes), or <code>0</code> for
	 *            infinite.
	 * @param reply
	 *            The reply.
	 *
	 * @return <code>true</code> if value changed, <code>false</code> if there
	 *         was a problem.
	 */
	public boolean addIgnore(final String name, final int duration,
			final String reply) {
		final StringBuilder sbuf = new StringBuilder();

		if (duration != 0) {
			sbuf.append(System.currentTimeMillis() + duration * 60000L);
		}

		sbuf.append(';');

		if (reply != null) {
			sbuf.append(reply);
		}

		return setKeyedSlot("!ignore", "_" + name, sbuf.toString());
	}

	/**
	 * Determine if a player is on the ignore list and return their reply
	 * message.
	 *
	 * @param name
	 *            The player name.
	 *
	 * @return The custom reply message (including an empty string), or
	 *         <code>null</code> if not ignoring.
	 */
	public String getIgnore(final String name) {
		String info = getKeyedSlot("!ignore", "_" + name);
		int i;
		long expiration;

		if (info == null) {
			/*
			 * Special "catch all" fallback
			 */
			info = getKeyedSlot("!ignore", "_*");
			if (info == null) {
				return null;
			}
		}
		i = info.indexOf(';');
		if (i == -1) {
			/*
			 * Do default
			 */
			return "";
		}

		/*
		 * Has expiration?
		 */
		if (i != 0) {
			expiration = Long.parseLong(info.substring(0, i));

			if (System.currentTimeMillis() >= expiration) {
				setKeyedSlot("!ignore", "_" + name, null);
				return null;
			}
		}

		return info.substring(i + 1);
	}

	/**
	 * Remove a player ignore entry.
	 *
	 * @param name
	 *            The player name.
	 *
	 * @return <code>true</code> if value changed, <code>false</code> if there
	 *         was a problem.
	 */
	public boolean removeIgnore(final String name) {
		return setKeyedSlot("!ignore", "_" + name, null);
	}

	/**
	 * @return all buddy names for this player
	 */
	public Set<String> getIgnores() {
		Set<String> res = new HashSet<String>();

		if (!this.hasSlot("!ignore")) {
			return res;
		}

		RPObject ignoreObject = KeyedSlotUtil.getKeyedSlotObject(this,
				"!ignore");

		// character names are prefixed with an "_" to tell them apart from
		// generic attributes such as "id".
		for (String key : ignoreObject) {
			if (key.charAt(0) != '_') {
				continue;
			}

			// skip expired entries
			String info = ignoreObject.get(key);
			int i = info.indexOf(';');
			if (i > 0) {
				long expiration = Long.parseLong(info.substring(0, i));
				if (System.currentTimeMillis() >= expiration) {
					continue;
				}
			}

			res.add(key.substring(1));
		}

		return res;
	}

	/**
	 * Get a named skills value.
	 *
	 * @param key
	 *            The skill key.
	 *
	 * @return The skill value, or <code>null</code> if not set.
	 */
	public String getSkill(final String key) {
		return getKeyedSlot("skills", key);
	}

	/**
	 * Get the current value for the skill of a magic nature
	 *
	 * @param nature
	 *            the nature to get the skill for
	 * @return current skill value
	 */
	public int getMagicSkillXp(final Nature nature) {
		int skillValue = 0;
		String skill = getSkill(nature.toString() + "_xp");
		if (skill != null) {
			try {
				Integer skillInteger = Integer.parseInt(skill);
				skillValue = skillInteger.intValue();
			} catch (NumberFormatException e) {
				logger.error(e, e);
			}
		}
		return skillValue;
	}

	/**
	 * Increase the skill points for a magic nature by a given amount
	 *
	 * @param nature
	 * @param amount
	 */
	public void increaseMagicSkillXp(final Nature nature, int amount) {
		int oldValue = getMagicSkillXp(nature);
		int newValue = oldValue + amount;
		// Handle level changes
		final int newLevel = Level.getLevel(newValue);
		int oldLevel = Level.getLevel(oldValue);
		final int levels = newLevel - (oldLevel - 10);

		// In case we level up several levels at a single time.
		for (int i = 0; i < Math.abs(levels); i++) {
			Integer oneup = getMagicSkill(nature) + (int) Math.signum(levels)
					* 1;
			// set in map
			setSkill(nature.toString(), oneup.toString());
			// log event
			new GameEvent(getName(), "nature-" + nature.toString(),
					oneup.toString()).raise();
		}
		setSkill(nature.toString() + "_xp", Integer.valueOf(newValue)
				.toString());
	}

	private int getMagicSkill(final Nature nature) {
		int skillLevel = 0;
		String skillString = getSkill(nature.toString());
		if (skillString != null) {
			try {
				Integer skillInteger = Integer.parseInt(skillString);
				skillLevel = skillInteger.intValue();
			} catch (NumberFormatException e) {
				logger.error(e, e);
			}
		}
		return skillLevel;
	}

	/**
	 * Set a named skills value.
	 *
	 * @param key
	 *            The skill key.
	 * @param value
	 *            The skill value.
	 *
	 * @return <code>true</code> if value changed, <code>false</code> if there
	 *         was a problem.
	 */
	public boolean setSkill(final String key, final String value) {
		return setKeyedSlot("skills", key, value);
	}

	/**
	 * Get a keyed string value on a named slot.
	 *
	 * @param name
	 *            The slot name.
	 * @param key
	 *            The value key.
	 *
	 * @return The keyed value of the slot, or <code>null</code> if not set.
	 */
	public String getKeyedSlot(final String name, final String key) {
		return KeyedSlotUtil.getKeyedSlot(this, name, key);
	}

	/**
	 * Set a keyed string value on a named slot.
	 *
	 * @param name
	 *            The slot name.
	 * @param key
	 *            The value key.
	 * @param value
	 *            The value to assign (or remove if <code>null</code>).
	 *
	 * @return <code>true</code> if value changed, <code>false</code> if there
	 *         was a problem.
	 */
	public boolean setKeyedSlot(final String name, final String key,
			final String value) {
		return KeyedSlotUtil.setKeyedSlot(this, name, key, value);
	}

	/**
	 * Checks if player has a feature.
	 *
	 * @param name
	 *     The feature mnemonic.
	 * @return <code>true</code> if the feature value is not <code>null</code>.
	 */
	public boolean hasFeature(final String name) {
		return getFeature(name) != null;
	}

	/**
	 * Get a client feature value.
	 *
	 * @param name
	 *            The feature mnemonic.
	 *
	 * @return The feature value, or <code>null</code> is not-enabled.
	 */
	public String getFeature(final String name) {
		return get("features", name);
	}

	/**
	 * Enable/disable a client feature.
	 *
	 * @param name
	 *            The feature mnemonic.
	 * @param enabled
	 *            Flag indicating if enabled.
	 */
	public void setFeature(final String name, final boolean enabled) {
		if (enabled) {
			setFeature(name, "");
		} else {
			unsetFeature(name);
		}
	}

	/**
	 * Sets/removes a client feature.
	 * <p>
	 * <strong>NOTE: The names and values MUST NOT contain <code>=</code>
	 * (equals), or <code>:</code> (colon). </strong>
	 *
	 * @param name
	 *            The feature mnemonic.
	 * @param value
	 *            The feature value, or <code>null</code> to disable.
	 */
	public void setFeature(final String name, final String value) {
		put("features", name, value);
	}

	/**
	 * Unset a client feature
	 *
	 * @param name
	 *            The feature mnemonic
	 */
	public void unsetFeature(final String name) {
		remove("features", name);
	}

	/**
	 * Determine if the entity is invisible to creatures.
	 *
	 * @return <code>true</code> if invisible.
	 */
	@Override
	public boolean isInvisibleToCreatures() {
		return has(INVISIBLE);
	}

	/**
	 * Set whether this player is invisible to creatures.
	 *
	 * @param invisible
	 *            <code>true</code> if invisible.
	 */
	public void setInvisible(final boolean invisible) {
		if (invisible) {
			put(INVISIBLE, "");
		} else if (has(INVISIBLE)) {
			remove(INVISIBLE);
		}
	}

	/**
	 * Sends a message that only this player can read. Used for messages that
	 * should not appear as sent by another player. For messages from other
	 * players (or relevant NPC messages), use sendPrivateText(PRIVMSG, text)
	 *
	 * @param text
	 *            the message.
	 */
	@Override
	public void sendPrivateText(final String text) {
		sendPrivateText(getServerNotificationType(clientVersion), text);
	}

	/**
	 * Sends a message that only this entity can read.
	 *
	 * @param type
	 * 			NotificationType
	 * @param text
	 * 			The message.
	 * @param headless
	 * 			If <code>true</code>, does not draw a chat balloon on canvas.
	 */
	@Override
	public void sendPrivateText(final NotificationType type, final String text) {
		RPEvent event = new PrivateTextEvent(type, text);
		this.addEvent(event);
		this.notifyWorldAboutChanges();
	}

	/**
	 * Sends a message that only this entity can read.
	 *
	 * @param sender
	 *   Name of entity sending message.
	 * @param text
	 *   Message contents.
	 */
	@Override
	public void sendPrivateText(final String sender, final String text) {
		sendPrivateText(getServerNotificationType(clientVersion), sender, text);
	}

	/**
	 * Sends a message that only this entity can read.
	 *
	 * @param type
	 *   NotificationType.
	 * @param sender
	 *   Name of entity sending message.
	 * @param text
	 *   Message contents.
	 */
	@Override
	public void sendPrivateText(final NotificationType type, final String sender, final String text) {
		/*
		RPEvent event = new PrivateTextEvent(type, sender, text);
		this.addEvent(event);
		this.notifyWorldAboutChanges();
		*/
		sendPrivateText(type, sender + " tells you: " + text);
	}

	/**
	 * Sets the name of the last player who privately talked to this player
	 * using the /tell command. It needs to be stored non-persistently so that
	 * /answer can be used.
	 *
	 * @param lastPrivateChatterName
	 */
	public void setLastPrivateChatter(final String lastPrivateChatterName) {
		TutorialNotifier.messaged(this);
		this.lastPrivateChatterName = lastPrivateChatterName;
	}

	/**
	 * Gets the name of the last player who privately talked to this player
	 * using the /tell command, or null if nobody has talked to this player
	 * since he logged in.
	 *
	 * @return name of last player
	 */
	public String getLastPrivateChatter() {
		return lastPrivateChatterName;
	}

	/**
	 * Returns the admin level of this user. See AdministrationAction.java for
	 * details.
	 *
	 * @return adminlevel
	 */
	public int getAdminLevel() {
		// normal user are adminlevel 0.
		if (!has(ADMINLEVEL)) {
			return 0;
		}
		return getInt(ADMINLEVEL);
	}

	/**
	 * Set the player's admin level.
	 *
	 * @param adminlevel
	 *            The new admin level.
	 */
	public void setAdminLevel(final int adminlevel) {
		put(ADMINLEVEL, adminlevel);
	}

	@Override
	public void rememberAttacker(final Entity attacker) {
		TutorialNotifier.attacked(this);
		super.rememberAttacker(attacker);
	}

	@Override
	public void onDead(final Killer killer, final boolean remove) {
		/*
		 * Don't try to kill disconnected players. May be triggered on damage
		 * done from turn listeners.
		 */
		if (isDisconnected()) {
			return;
		}
		// Always use remove=false for players, as documented
		// in RPEntity.onDead()
		super.onDead(killer, false);
		dieer.onDead(killer);
	}

	@Override
	protected void dropItemsOn(final Corpse corpse) {
		dieer.dropItemsOn(corpse);
	}

	public void removeSheep(final Sheep sheep) {
		getPetOwner().removeSheep(sheep);
	}

	public void removePet(final Pet pet) {
		getPetOwner().removePet(pet);
	}

	public boolean hasSheep() {
		return getPetOwner().hasSheep();
	}

	public boolean hasPet() {
		return getPetOwner().hasPet();
	}

	/**
	 * Set the player's pet. This will also set the pet's owner.
	 *
	 * @param pet
	 *            The pet.
	 */
	public void setPet(final Pet pet) {
		getPetOwner().setPet(pet);
	}

	/**
	 * Set the player's sheep. This will also set the sheep's owner.
	 *
	 * @param sheep
	 *            The sheep.
	 */
	public void setSheep(final Sheep sheep) {
		getPetOwner().setSheep(sheep);
	}

	/**
	 * Get the player's sheep.
	 *
	 * @return The sheep.
	 */
	public Sheep getSheep() {
		return getPetOwner().getSheep();
	}

	public Pet getPet() {
		return getPetOwner().getPet();
	}

	/**
	 * Gets the number of minutes that this player has been logged in on the
	 * server.
	 *
	 * @return age of player in minutes
	 */
	public int getAge() {
		return age;
	}

	/**
	 * Is this a new player?
	 *
	 * @return true if it is a new player, false otherwise
	 */
	public boolean isNew() {
		return getAge() < 2 * 60 || getAtk() < 15 || getDef() < 15
				|| getLevel() < 5;
	}

	/**
	 * Sets the number of minutes that this player has been logged in on the
	 * server.
	 *
	 * @param age
	 *            minutes
	 */
	public void setAge(final int age) {
		this.age = age;
		put("age", age);
		TutorialNotifier.aged(this, age);
		SingletonRepository.getAchievementNotifier().onAge(this);
	}

	/**
	 * Updates the last pvp action time with the current time.
	 */
	public void storeLastPVPActionTime() {
		put("last_pvp_action_time", System.currentTimeMillis());
	}

	/**
	 * Returns the time the player last did an PVP action.
	 *
	 * @return time in milliseconds
	 */
	public long getLastPVPActionTime() {
		if (has("last_pvp_action_time")) {
			return (long) Float.parseFloat(get("last_pvp_action_time"));
		}
		return -1;
	}

	/**
	 * Notifies this player that the given player has logged in.
	 *
	 * @param who
	 *            The name of the player who has logged in.
	 */
	public void notifyOnline(final String who) {
		boolean found = false;
		if (containsKey("buddies", who)) {
			put("buddies", who, true);
			found = true;
		}
		if (found) {
			if (has("online")) {
				put("online", get("online") + "," + who);
			} else {
				put("online", who);
			}
		}
	}

	/**
	 * Notifies this player that the given player has logged out.
	 *
	 * @param who
	 *            The name of the player who has logged out.
	 */
	public void notifyOffline(final String who) {
		boolean found = false;
		if (containsKey("buddies", who)) {
			put("buddies", who, false);
			found = true;
		}
		if (found) {
			if (has("offline")) {
				put("offline", get("offline") + "," + who);
			} else {
				put("offline", who);
			}
		}
	}

	/**
	 * Sets the online status for a buddy in the players' buddy list
	 *
	 * @param buddyName
	 * @param isOnline
	 *            buddy is online?
	 */
	public void setBuddyOnlineStatus(String buddyName, boolean isOnline) {
		// maps handling:
		if (containsKey("buddies", buddyName)) {
			put("buddies", buddyName, isOnline);
		}
	}

	/**
	 * @return true iff this player has buddies (considers only map attribute!)
	 */
	public boolean hasBuddies() {
		if (hasMap("buddies")) {
			return !getMap("buddies").isEmpty();
		}

		return false;
	}

	/**
	 * @return all buddy names for this player
	 */
	public Set<String> getBuddies() {
		Set<String> buddies = new HashSet<String>();

		if (hasMap("buddies")) {
			buddies.addAll(getMap("buddies").keySet());
		}

		return buddies;
	}

	public int countBuddies() {
		if (this.hasBuddies()) {
			return getMap("buddies").size();
		}

		return 0;
	}

	/**
	 * Checks whether the player has completed the given quest or not.
	 *
	 * @param name
	 *            The quest's name
	 * @return true iff the quest has been completed by the player
	 */
	public boolean isQuestCompleted(final String name) {
		return quests.isQuestCompleted(name);
	}

	/**
	 * Checks whether the player has made any progress in the given quest or
	 * not. For many quests, this is true right after the quest has been
	 * started.
	 *
	 * @param name
	 *            The quest's name
	 * @return true if the player has made any progress in the quest
	 */
	public boolean hasQuest(final String name) {
		return quests.hasQuest(name);
	}

	/**
	 * Gets the player's current status in the given quest.
	 *
	 * @param name
	 *            The quest's name
	 * @return the player's status in the quest
	 */
	public String getQuest(final String name) {
		return quests.getQuest(name);
	}

	/**
	 * Gets the player's current status in the given quest.
	 *
	 * @param name
	 *            The quest's name
	 * @param index
	 *            the index of the sub state to change (separated by ";")
	 * @return the player's status in the quest
	 */
	public String getQuest(final String name, final int index) {
		return quests.getQuest(name, index);
	}

	/**
	 * Allows to store the player's current status in a quest in a string. This
	 * string may, for instance, be "started", "done", a semicolon- separated
	 * list of items that need to be brought/NPCs that need to be met, or the
	 * number of items that still need to be brought. Note that the string
	 * "done" has a special meaning: see isQuestCompleted().
	 *
	 * @param name
	 *            The quest's name
	 * @param status
	 *            the player's status in the quest. Set it to null to completely
	 *            reset the player's status for the quest.
	 */
	public void setQuest(final String name, final String status) {
		quests.setQuest(name, status);
	}

	/**
	 * Allows to store the player's current status in a quest in a string. This
	 * string may, for instance, be "started", "done", a semicolon- separated
	 * list of items that need to be brought/NPCs that need to be met, or the
	 * number of items that still need to be brought. Note that the string
	 * "done" has a special meaning: see isQuestComplete().
	 *
	 * @param name
	 *            The quest's name
	 * @param index
	 *            the index of the sub state to change (separated by ";")
	 * @param status
	 *            the player's status in the quest. Set it to null to completely
	 *            reset the player's status for the quest.
	 */
	public void setQuest(final String name, final int index, final String status) {
		quests.setQuest(name, index, status);
	}

	public List<String> getQuests() {
		return quests.getQuests();
	}

	public void removeQuest(final String name) {
		quests.removeQuest(name);
	}

	/**
	 * Is the named quest in one of the listed states?
	 *
	 * @param name
	 *     Quest name.
	 * @param states
	 *     Valid states.
	 * @return
	 *     <code>true</code> if the quest is in one of theses states,
	 *     <code>false</code> otherwise.
	 */
	public boolean isQuestInState(final String name, final String... states) {
		return quests.isQuestInState(name, states);
	}

	/**
	 * Is the named quest in one of the listed states?
	 *
	 * @param name
	 *     Quest name.
	 * @param index
	 *     Quest index.
	 * @param states
	 *     Valid states.
	 * @return
	 *     <code>true</code> if the quest is in one of theses states,
	 *     <code>false</code> otherwise.
	 */
	public boolean isQuestInState(final String name, final int index,
			final String... states) {
		return quests.isQuestInState(name, index, states);
	}

	/**
	 * Checks if the player has ever killed a creature, with or without the help
	 * of any other player.
	 *
	 * @param name
	 *     Name of creature to check.
	 * @return
	 *     <code>true</code> if this player has ever killed this creature.
	 */
	public boolean hasKilled(final String name) {
		return killRec.hasKilled(name);
	}

	/**
	 * Checks if the player has ever 'solo killed' a creature, i.e.
	 * without the help of any other player.
	 *
	 * @param name
	 *     Name of creature to check.
	 * @return
	 *     <code>true</code> if this player has ever killed this creature
	 *     without help.
	 */
	public boolean hasKilledSolo(final String name) {
		return killRec.hasKilledSolo(name);
	}

	/**
	 * Checks if the player has ever 'shared killed' a creature, i.e.
	 * with the help of any other player.
	 *
	 * @param name
	 *     Name of creature to check.
	 * @return
	 *     <code>true</code> if this player has ever killed this creature
	 *     with help.
	 */
	public boolean hasKilledShared(final String name) {
		return killRec.hasKilledShared(name);
	}

	/**
	 * Stores that the player has killed 'name' solo. Overwrites shared kills of
	 * 'name'.
	 *
	 * @param name
	 *     Name of the victim.
	 */
	public void setSoloKill(final String name) {
		killRec.setSoloKill(name);
	}

	/**
	 * Stores that the player has killed 'name' with help of others. Does not
	 * overwrite solo kills of 'name'.
	 *
	 * @param name
	 *     Name of victim.
	 */
	public void setSharedKill(final String name) {
		killRec.setSharedKill(name);
	}

	/**
	 * Changes solo kills count to specified value.
	 *
	 * @param name
	 *     Name of victim.
	 * @param count
	 *     Value to set.
	 */
	public void setSoloKillCount(final String name, final int count) {
		killRec.setSoloKillCount(name, count);
	}

	/**
	 * Increments number of counted solo kills by 1.
	 *
	 * @param name
	 *     Name of victim.
	 */
	public void incSoloKillCount(final String name) {
		setSoloKillCount(name, getSoloKill(name) + 1);
	}

	/**
	 * Changes shared kills count to specified value.
	 *
	 * @param name
	 *     Name of victim.
	 * @param count
	 *     Value to set.
	 */
	public void setSharedKillCount(final String name, final int count) {
		killRec.setSharedKillCount(name, count);
	}

	/**
	 * Increments number of counted shared kills by 1.
	 *
	 * @param name
	 *     Name of victim.
	 */
	public void incSharedKillCount(final String name) {
		setSharedKillCount(name, getSharedKill(name) + 1);
	}

	/**
	 * Retrieves number of creatures killed alone by this player.
	 *
	 * @param name
	 *     Name of victim.
	 * @return
	 *     Number of solo kills.
	 */
	public int getSoloKill(final String name) {
		return killRec.getSoloKill(name);
	}

	/**
	 * Retrieves number of creatures killed alone by this player.
	 *
	 * @param name
	 *     Name of victim.
	 * @return
	 *     Number of solo kills.
	 */
	public int getSoloKillCount(final String name) {
		return getSoloKill(name);
	}

	/**
	 * Retrieves number of creatures killed by this player with help
	 * from others.
	 *
	 * @param name
	 *     Name of victim.
	 * @return
	 *     Number of shared kills.
	 */
	public int getSharedKill(final String name) {
		return killRec.getSharedKill(name);
	}

	/**
	 * Retrieves number of creatures killed by this player with help
	 * from others.
	 *
	 * @param name
	 *     Name of victim.
	 * @return
	 *     Number of shared kills.
	 */
	public int getSharedKillCount(final String name) {
		return getSharedKill(name);
	}

	/**
	 * Retrieves number of creatures kill by this player alone and/or
	 * with help from others.
	 *
	 * @param name
	 *     Name of victim.
	 * @return
	 *     Total number of kills.
	 */
	public int getAllKillCount(final String name) {
		return getSoloKill(name) + getSharedKill(name);
	}

	@Override
	public String describe() {

		// A special description was specified
		if (hasDescription()) {
			return getDescription();
		}

		// default description for player includes their name, level and play
		// time
		final String name = getTitle();
		final int hours = age / 60;
		final int minutes = age % 60;
		final String time = hours + " hours and " + minutes + " minutes";
		final String text = "You see " + name + ".\n" + name + " is level "
				+ getLevel() + " and has been playing " + time + ".";
		final StringBuilder sb = new StringBuilder();
		sb.append(text);
		final String awayMessage = getAwayMessage();
		if (awayMessage != null) {
			sb.append("\n" + name + " is away and has left a message: ");
			sb.append(awayMessage);
		}
		final String grumpyMessage = getGrumpyMessage();
		if (grumpyMessage != null) {
			sb.append("\n" + name + " is grumpy and has left a message: ");
			sb.append(grumpyMessage);
		}

		/* Show a sentence set by player if not away or grumpy */
		if ((awayMessage == null) && (grumpyMessage == null)) {
			final String sentence = getSentence();
			if (!sentence.isEmpty()) {
				sb.append("\n");
				sb.append(Grammar.suffix_s(name));
				sb.append(" sentence is: \"" + sentence + "\"");
			}
		}

		return sb.toString();
	}

	/**
	 * Teleports this player to the given destination.
	 *
	 * @param zone
	 *            The zone where this player should be teleported to.
	 * @param x
	 *            The destination's x coordinate
	 * @param y
	 *            The destination's y coordinate
	 * @param dir
	 *            The direction in which the player should look after
	 *            teleporting, or null if the direction shouldn't change
	 * @param teleporter
	 *            The player who initiated the teleporting, or null if no player
	 *            is responsible. This is only to give feedback if something
	 *            goes wrong. If no feedback is wanted, use null.
	 * @return <code>true</code> if teleporting was successful.
	 */
	public boolean teleport(final StendhalRPZone zone, final int x,
			final int y, final Direction dir, final Player teleporter) {
		if (StendhalRPAction.placeat(zone, this, x, y)) {
			if (dir != null) {
				this.setDirection(dir);
			}
			notifyWorldAboutChanges();
			return true;
		} else {
			final String text = "Position [" + x + "," + y + "] is occupied";
			if (teleporter != null) {
				teleporter.sendPrivateText(text);
			} else {
				this.sendPrivateText(text);
			}
			return false;
		}
	}

	/**
	 * Teleports player to given destination using zoneid string.
	 *
	 * @param zoneid
	 * 		<code>String</code> name/ID of zone.
	 * @param x
	 * 		Destination's horizontal coordinate.
	 * @param y
	 * 		Distination's vertical coordinate.
	 * @param dir
	 * 		The direction in which the player should look after
	 * 		teleporting, or null if the direction shouldn't change.
	 * @param teleporter
	 * @return
	 * 		<code>true</code> if teleporting was successful.
	 */
	public boolean teleport(final String zoneid, final int x,
			final int y, final Direction dir, final Player teleporter) {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(zoneid);
		return teleport(zone, x, y, dir, teleporter);
	}

	/**
	 * Called when player push entity. The entity displacement is handled by the
	 * action itself.
	 *
	 * @param entity
	 */
	@SuppressWarnings("unused")
	public void onPush(final RPEntity entity) {
		turnOfLastPush = SingletonRepository.getRuleProcessor().getTurn();
	}

	/**
	 * Return true if player can push entity.
	 *
	 * @param entity
	 * @return true iff pushing is possible
	 */
	public boolean canPush(final RPEntity entity) {
		return this != entity
				&& SingletonRepository.getRuleProcessor().getTurn()
						- turnOfLastPush > 10;
	}

	//
	// DressedEntity
	//

	/**
	 * Sets the player's original outfit. Useful for updating original outfit information while
	 * wearing a temporary outfit.
	 *
	 * @param outfit
	 *     The new outfit.
	 * @param colors
	 *     New outfit colors.
	 */
	public void setOriginalOutfit(final Outfit outfit, final Map<String, Integer> colors) {
		String oattr = "outfit_ext";
		String csuffix = "";
		if (outfitIsTemporary()) {
			oattr += "_orig";
			csuffix = "_orig";
		}
		if (colors != null) {
			for (final String part: getColorableLayers()) {
				// clear old color
				remove("outfit_colors", part + csuffix);
				if (colors.containsKey(part)) {
					put("outfit_colors", part + csuffix, colors.get(part));
				}
			}
		}
		put(oattr, outfit.putOver(getOriginalOutfit()).toString());
		notifyWorldAboutChanges();
	}

	/**
	 * Sets the player's original outfit. Useful for updating original outfit information while
	 * wearing a temporary outfit.
	 *
	 * @param outfit
	 *     The new outfit.
	 */
	public void setOriginalOutfit(final Outfit outfit) {
		setOriginalOutfit(outfit, null);
	}

	/**
	 * Sets the player's original outfit. Useful for updating original outfit information while
	 * wearing a temporary outfit.
	 *
	 * @param outfit
	 *     The new outfit string representation.
	 * @param colors
	 *     New outfit colors string representation.
	 */
	public void setOriginalOutfit(final String outfit, final String colors) {
		Map<String, Integer> colorMap = null;
		if (colors != null) {
			colorMap = new HashMap<>();
			for (final String tmp1: colors.split(",")) {
				try {
					final String[] tmp2 = tmp1.split("=");
					colorMap.put(tmp2[0], Integer.parseInt(tmp2[1]));
				} catch (final NumberFormatException e) {
					logger.error("Cannot convert outfit color", e);
				}
			}
		}
		setOriginalOutfit(new Outfit(outfit), colorMap);
	}

	/**
	 * Sets the player's original outfit. Useful for updating original outfit information while
	 * wearing a temporary outfit.
	 *
	 * @param outfit
	 *     The new outfit string representation.
	 */
	public void setOriginalOutfit(final String outfit) {
		setOriginalOutfit(outfit, null);
	}

	/**
	 * Use this method to add a new temporary outfit or override an old one.
	 *
	 * @param outfit
	 *     The temporary outfit to be worn.
	 * @param expireAge
	 *     Player age when outfit will expire.
	 * @param clearColors
	 *     Should layer color information be forgotten? (default: true)
	 */
	public void setTemporaryOutfit(final Outfit outfit, final int expireAge, final boolean clearColors) {
		// update original outfit only if not currently wearing a temporary one
		if (!has("outfit_ext_orig")) {
			storeOriginalOutfit();
		}
		if (clearColors) {
			clearColors();
		}
		// - combine the old outfit with the new one, as the new one might
		//   contain null parts.
		// - new temporary outfits must remove old ones.
		put("outfit_ext", outfit.putOver(getOriginalOutfit()).toString());
		registerOutfitExpireTime(expireAge);
		notifyWorldAboutChanges();
	}

	/**
	 * Use this method to add a new temporary outfit or override an old one.
	 *
	 * @param outfit
	 *     The temporary outfit to be worn.
	 * @param expireAge
	 *     Player age when outfit will expire.
	 */
	public void setTemporaryOutfit(final Outfit outfit, final int expireAge) {
		setTemporaryOutfit(outfit, expireAge, true);
	}

	/**
	 * Use this method to add a new temporary outfit or override an old one.
	 *
	 * @param outfit
	 *     Temporary outfit string representation.
	 * @param expireAge
	 *     Player age when outfit will expire.
	 * @param clearColors
	 *     Should layer color information be forgotten? (default: true);
	 */
	public void setTemporaryOutfit(final String outfit, final int expireAge, final boolean clearColors) {
		setTemporaryOutfit(new Outfit(outfit), expireAge, clearColors);
	}

	/**
	 * Use this method to add a new temporary outfit or override an old one. Layer color information
	 * is removed.
	 *
	 * @param outfit
	 *     Temporary outfit string representation.
	 * @param expireAge
	 *     Player age when outfit will expire.
	 */
	public void setTemporaryOutfit(final String outfit, final int expireAge) {
		setTemporaryOutfit(outfit, expireAge, true);
	}

	/**
	 * Sets a layer for both the player's original & temporary outfits without changing temporary
	 * expiration.
	 *
	 * @param layer
	 *     Layer name.
	 * @param index
	 *     Index to set layer to.
	 */
	public void setPerpetualOutfitLayer(final String layer, final int index) {
		final Outfit newLayer = new Outfit(layer + "=" + index);
		put("outfit_ext", newLayer.putOver(getOutfit()).toString());
		if (outfitIsTemporary()) {
			put("outfit_ext_orig", newLayer.putOver(getOriginalOutfit()).toString());
		}
		notifyWorldAboutChanges();
	}

	/**
	 * Sets a layer color for both the player's original & temporary outfits.
	 *
	 * @param layer
	 *     Layer name.
	 * @param color
	 *     New color.
	 */
	public void setPerpetualOutfitColor(final String layer, final String color) {
		put("outfit_colors", layer, color);
		if (outfitIsTemporary()) {
			put("outfit_colors", layer + "_orig", color);
		}
	}

	/**
	 * Sets a layer color for both the player's original & temporary outfits.
	 *
	 * @param layer
	 *     Layer name.
	 * @param color
	 *     New color.
	 */
	public void setPerpetualOutfitColor(final String layer, final int color) {
		setPerpetualOutfitColor(layer, String.valueOf(color));
	}

	/**
	 * Checks if entity is wearnig a temporary outfit.
	 *
	 * @return
	 *     True if the entiry has stored an original outfit.
	 */
	public boolean outfitIsTemporary() {
		return has("outfit_ext_orig") || has("outfit_org");
	}

	/**
	 * sets the time a outfit wears off
	 *
	 * @param expire
	 *            expire age
	 */
	public void registerOutfitExpireTime(int expire) {
		// ignore outfits that do not expire
		if (expire < 0) {
			return;
		}

		// currently we keep only track of one expire, so takes the smallest
		// to prevent players from keeping a highly special outfit longer
		// by renting an outfit with a longer expire time later
		int oldExpire = Integer.MAX_VALUE;
		if (has("outfit_expire_age")) {
			oldExpire = getInt("outfit_expire_age");
		}
		if (oldExpire < age) {
			logger.error("oldExpire " + oldExpire + " for age " + age);
			oldExpire = Integer.MAX_VALUE;
		}
		int newExpire = Math.min(expire + age, oldExpire);
		put("outfit_expire_age", newExpire);

		ExpireOutfit expireOutfit = new ExpireOutfit(getName());
		SingletonRepository.getTurnNotifier().dontNotify(expireOutfit);
		SingletonRepository.getTurnNotifier().notifyInSeconds(
				(newExpire - age) * 60, expireOutfit);
	}

	/**
	 * Tries to give the player his original outfit back after he has put on a
	 * temporary outfit. This will only be successful if the original outfit has
	 * been stored.
	 *
	 * @return true iff returning was successful.
	 */
	public boolean returnToOriginalOutfit() {
		removeOutfitExpireNotification();

		final String outfit_orig = get("outfit_ext_orig");
		restoreOriginalOutfit();

		// FIXME: need to check outfit colors as well
		return get("outfit_ext").equals(outfit_orig);
	}

	private void removeOutfitExpireNotification() {
		ExpireOutfit expireOutfit = new ExpireOutfit(getName());
		SingletonRepository.getTurnNotifier().dontNotify(expireOutfit);

		remove("outfit_expire_age");
	}

	//
	// ActiveEntity
	//

	/**
	 * Determine if zone changes are currently allowed via normal means
	 * (non-portal teleportation doesn't count).
	 *
	 * @return <code>true</code> if the entity can change zones.
	 */
	@Override
	public boolean isZoneChangeAllowed() {
		/*
		 * If we are too far from dependents, then disallow zone change
		 */
		final Sheep sheep = getSheep();

		if (sheep != null) {
			if (squaredDistance(sheep) > 7 * 7) {
				return false;
			}
		}

		final Pet pet = getPet();

		if (pet != null) {
			if (squaredDistance(pet) > 7 * 7) {
				return false;
			}
		}

		return true;
	}

	//
	// Entity
	//

	/**
	 * Perform cycle logic.
	 */
	@Override
	public void logic() {
		/*
		 * TODO: Refactor Most of these things can be handled as RPEvents
		 */
		if (has("risk")) {
			remove("risk");
			notifyWorldAboutChanges();
		}

		if (has("damage")) {
			remove("damage");
			notifyWorldAboutChanges();
		}

		if (has("heal")) {
			remove("heal");
			notifyWorldAboutChanges();
		}

		if (has("dead")) {
			remove("dead");
			notifyWorldAboutChanges();
		}

		if (has("online")) {
			remove("online");
			notifyWorldAboutChanges();
		}

		if (has("offline")) {
			remove("offline");
			notifyWorldAboutChanges();
		}

		applyMovement();

		final int turn = SingletonRepository.getRuleProcessor().getTurn();

		if (isAttacking() && turn % getAttackRate() == 0) {
			RPEntity attackTarget = getAttackTarget();

			// Face target if player is not moving
			if (stopped() && isInSight(attackTarget)
					&& !isFacingToward(attackTarget)) {
				faceToward(attackTarget);
				notifyWorldAboutChanges();
			}

			StendhalRPAction.playerAttack(this, attackTarget);
		}

		agePlayer(turn);
	}

	private void agePlayer(final int turn) {
		/*
		 * 200 means 60 seconds x 300mx per turn.
		 */
		if (!isGhost()) {
			if (turn % 200 == 0) {
				setAge(getAge() + 1);
				notifyWorldAboutChanges();
			}
		}
	}

	/**
	 * Checks whether an entity is a ghost (non physically interactive).
	 *
	 * @return <code>true</code> if in ghost mode.
	 */
	@Override
	public boolean isGhost() {
		return has(GHOSTMODE);
	}

	/**
	 * Set whether this player is a ghost (invisible/non-interactive).
	 *
	 * @param ghost
	 *            <code>true</code> if a ghost.
	 */
	public void setGhost(final boolean ghost) {
		if (ghost) {
			put(GHOSTMODE, "");
		} else if (has(GHOSTMODE)) {
			remove(GHOSTMODE);
		}
	}

	/**
	 * Checks whether a player has teleclick enabled.
	 *
	 * @return <code>true</code> if teleclick is enabled.
	 */
	public boolean isTeleclickEnabled() {
		return has(TELECLICKMODE);
	}

	/**
	 * Set whether this player has teleclick enabled.
	 *
	 * @param teleclick
	 *            <code>true</code> if teleclick enabled.
	 */
	public void setTeleclickEnabled(final boolean teleclick) {
		if (teleclick) {
			put(TELECLICKMODE, "");
		} else if (has(TELECLICKMODE)) {
			remove(TELECLICKMODE);
		}
	}

	/**
	 * Called when this object is added to a zone.
	 *
	 * @param zone
	 *            The zone this was added to.
	 */
	@Override
	public void onAdded(final StendhalRPZone zone) {
		super.onAdded(zone);

		final String zoneName = zone.getID().getID();

		/*
		 * If player enters afterlife, make them partially transparent
		 */
		if (zoneName.equals(PlayerDieer.DEFAULT_DEAD_AREA)) {
			setVisibility(50);
		}

		/*
		 * Remember zones we've been in
		 */
		setKeyedSlot("!visited", zoneName,
				Long.toString(System.currentTimeMillis()));
		trade.cancelTrade();
	}

	/**
	 * Called when this object is being removed from a zone.
	 *
	 * @param zone
	 *            The zone this will be removed from.
	 */
	@Override
	public void onRemoved(final StendhalRPZone zone) {
		/*
		 * If player leaves afterlife, make them normal
		 */
		if (zone.getID().getID().equals(PlayerDieer.DEFAULT_DEAD_AREA)) {
			setVisibility(100);
		}

		super.onRemoved(zone);
	}

	//
	// Object
	//
	@Override
	public int hashCode() {
		// player names are unique, so we can use the name's hash code.
		return getName().toLowerCase().hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Player) {
			final Player other = (Player) obj;
			return this.getName().toLowerCase(Locale.ENGLISH)
					.equals(other.getName().toLowerCase(Locale.ENGLISH));
		}
		return false;
	}

	// use a short readable output instead of the completed RPObject with all
	// its attributes and slots. You can use /inspect <player> in game to get
	// that.
	@Override
	public String toString() {
		return "Player [" + getName() + ", " + hashCode() + "]";
	}

	/**
	 * sets the player sentence
	 *
	 * @param sentence
	 *            sentence to store
	 */
	public void setSentence(final String sentence) {
		put("sentence", sentence);
	}

	/**
	 * gets the player sentence displayed on the web site
	 *
	 * @return player sentence
	 */
	public String getSentence() {
		String result = "";
		if (has("sentence")) {
			result = get("sentence");
		}

		return result;
	}

	private boolean disconnected = false;
	private UseListener useListener;

	/**
	 * checks whether this client is flagged as disconnected
	 *
	 * @return true, if the client is disconnected; false otherwise.
	 */
	public boolean isDisconnected() {
		return disconnected;
	}

	/**
	 * sets the client version
	 *
	 * @param version
	 */
	public void setClientVersion(String version) {
		this.clientVersion = version;
	}

	/**
	 * checks if the client is newer than the requested version
	 *
	 * @param version
	 *            requested version
	 * @return check the client is newer
	 */
	public boolean isClientNewerThan(String version) {
		if (clientVersion == null) {
			return false;
		}
		return Version.compare(clientVersion, version) > 0;
	}

	/**
	 * gets a list of all rings of life that are not broken
	 *
	 * @return list of rings of life
	 */
	public List<RingOfLife> getAllEquippedWorkingRingOfLife() {
		final List<RingOfLife> result = new LinkedList<RingOfLife>();

		for (RPSlot slot : this.slots(Slots.CARRYING)) {

			for (final RPObject object : slot) {
				searchForWorkingRingsOfLife(object, result);
			}
		}

		return result;
	}

	/**
	 * Search recursively for working rings of life inside objects and their
	 * content slots.
	 *
	 * @param obj
	 * @param list
	 */
	private void searchForWorkingRingsOfLife(RPObject obj, List<RingOfLife> list) {
		if (obj instanceof RingOfLife) {
			RingOfLife ring = (RingOfLife) obj;
			if (!ring.isBroken()) {
				list.add(ring);
			}
		} else {
			for (RPSlot slot : obj.slots()) {
				for (RPObject subobj : slot) {
					searchForWorkingRingsOfLife(subobj, list);
				}
			}
		}
	}

	/**
	 * Return a list of all animals associated to this player.
	 *
	 * @return List of DomesticalAnmial
	 */
	public List<DomesticAnimal> getAnimals() {
		final List<DomesticAnimal> animals = new ArrayList<DomesticAnimal>();

		if (hasPet()) {
			animals.add(getPet());
		}

		if (hasSheep()) {
			animals.add(getSheep());
		}

		return animals;
	}

	/**
	 * Search for an animal with the given name or type.
	 *
	 * @param name
	 *            the name or type of the pet to search
	 * @param exactly
	 *            <code>true</code> if looking only for matching name instead of
	 *            both name and type.
	 * @return the found pet
	 */
	public DomesticAnimal searchAnimal(final String name, final boolean exactly) {
		final List<DomesticAnimal> animals = getAnimals();

		for (final DomesticAnimal animal : animals) {
			if (animal != null) {
				if (animal.getTitle().equalsIgnoreCase(name)) {
					return animal;
				}

				if (!exactly) {
					final String type = animal.get("type");
					if (type != null
							&& ItemTools.itemNameToDisplayName(type).equals(
									name)) {
						return animal;
					}

					if ("pet".equals(name)) {
						return animal;
					}
				}
			}
		}

		return null;
	}

	@Override
	protected void handleObjectCollision() {
		if (hasPath()) {
			reroute();
		}
	}

	// public boolean isImmune() {
	// return isImmune;
	// }

	void setLastPlayerKill(final long milliseconds) {
		put(LAST_PLAYER_KILL_TIME, milliseconds);
	}

	public boolean isBadBoy() {
		return has(LAST_PLAYER_KILL_TIME);
	}

	/**
	 * Returns the time the player last did a player kill.
	 *
	 * @return time in milliseconds
	 */
	public long getLastPlayerKillTime() {
		if (has(LAST_PLAYER_KILL_TIME)) {
			return (long) Float.parseFloat(get(LAST_PLAYER_KILL_TIME));
		}
		return -1;
	}

	public void rehabilitate() {
		remove(LAST_PLAYER_KILL_TIME);

	}

	@Override
	protected void rewardKillers(final int oldXP) {
		// Don't reward for killing players
		// process tutorial event for first player kill

		for (Entry<Entity, Integer> entry : damageReceived.entrySet()) {
			if (entry.getValue() == 0) {
				continue;
			}
			Player killer = entityAsOnlinePlayer(entry.getKey());
			if (killer != null) {
				TutorialNotifier.killedPlayer(killer);
			}
		}
	}

	public PetOwner getPetOwner() {
		return petOwner;
	}

	public boolean isBoundTo(final Item item) {
		return getName().equals(item.getBoundTo());
	}

	/**
	 * gets the PlayerChatBucket
	 *
	 * @return PlayerChatBucket
	 */
	public PlayerChatBucket getChatBucket() {
		return chatBucket;
	}

	@Override
	public Nature getDamageType() {
		// Use the damage type of arrows, if the player is shooting with them
		if (getRangeWeapon() != null) {
			Item missile = getAmmunition();
			if (missile != null) {
				return missile.getDamageType();
			}
		}
		Item weapon = getWeapon();
		if (weapon != null) {
			return weapon.getDamageType();
		}

		return Nature.CUT;
	}

	@Override
	protected double getSusceptibility(Nature type) {
		double sus = 1.0;
		/*
		 * check weapon and shield separately, so that holding 2 resistant
		 * shields does not help
		 */
		Item weapon = getWeapon();
		if (weapon != null) {
			sus *= weapon.getSusceptibility(type);
		}
		Item shield = getShield();
		if (shield != null) {
			sus *= shield.getSusceptibility(type);
		}

		String[] armorSlots = { "armor", "head", "legs", "feet", "cloak" };
		for (String slot : armorSlots) {
			RPObject object = getSlot(slot).getFirst();
			if (object instanceof Item) {
				sus *= ((Item) object).getSusceptibility(type);
			}
		}

		return sus;
	}

	/**
	 * adds a buddy to the player's buddy list
	 *
	 * @param name
	 *            the name of the buddy
	 * @param online
	 *            if the player is online
	 * @return true if the buddy has been added
	 */
	public boolean addBuddy(String name, boolean online) {
		boolean isNew = !hasMap("buddies")
				|| !getMap("buddies").containsKey(name);

		put("buddies", name, online);

		return isNew;
	}

	/**
	 * removes a buddy to the player's buddy list
	 *
	 * @param name
	 *            the name of the buddy
	 * @return true if a buddy was removed
	 */
	public boolean removeBuddy(String name) {
		return remove("buddies", name) != null;
	}

	@Override
	public void setLevel(final int level) {
		final int oldLevel = getLevel();
		super.setLevel(level);

		// reward players on level up
		if (oldLevel < level) {
			AchievementNotifier.get().onLevelChange(this);
			this.addEvent(new SoundEvent(SoundID.LEVEL_UP, SoundLayer.USER_INTERFACE));
			this.notifyWorldAboutChanges();
		}
	}

	@Override
	protected void setDefInternal(final int def, final boolean notify) {
		final int oldDef = getDef();
		super.setDefInternal(def, notify);

		if (oldDef < def) {
			AchievementNotifier.get().onDefChange(this);
			this.addEvent(new SoundEvent(SoundID.STAT_UP, SoundLayer.USER_INTERFACE));
			this.notifyWorldAboutChanges();
		}
	}

	@Override
	protected void setAtkInternal(final int atk, final boolean notify) {
		final int oldAtk = getAtk();
		super.setAtkInternal(atk, notify);

		if (oldAtk < atk) {
			AchievementNotifier.get().onAtkChange(this);
			this.addEvent(new SoundEvent(SoundID.STAT_UP, SoundLayer.USER_INTERFACE));
			this.notifyWorldAboutChanges();
		}
	}

	@Override
	protected void setRatkInternal(final int ratk, final boolean notify) {
		final int oldRatk = getRatk();
		super.setRatkInternal(ratk, notify);

		if (oldRatk < ratk) {
			AchievementNotifier.get().onRatkChange(this);
			this.addEvent(new SoundEvent(SoundID.STAT_UP, SoundLayer.USER_INTERFACE));
			this.notifyWorldAboutChanges();
		}
	}

	/**
	 * Adds the identifier of an achievement to the reached achievements
	 *
	 * @param identifier
	 */
	public void addReachedAchievement(String identifier) {
		getAchievements().add(identifier);
	}

	private Set<String> getAchievements() {
		return reachedAchievements;
	}

	public void initReachedAchievements() {
		reachedAchievements = new HashSet<String>();
	}

	/**
	 * checks if the achievements of this player object are already loaded
	 *
	 * @return true, if the achievement set is loaded, false otherwise
	 */
	public boolean arePlayerAchievementsLoaded() {
		return reachedAchievements != null;
	}

	/**
	 * Checks if a player has reached the achievement with the given identifier
	 *
	 * @param identifier
	 * @return true if player had reached the achievement with the given
	 *         identifier
	 */
	public boolean hasReachedAchievement(String identifier) {
		if (getAchievements() != null) {
			return getAchievements().contains(identifier);
		} else {
			// if there were no reached achievements at all then the achievement
			// can't have been reached
			return false;
		}
	}

	/**
	 * Checks if the player has visited the given zone.
	 *
	 * @param zoneName
	 *     String name of the zone to check for.
	 * @return
	 *     <code>true</code> if player visited the zone.
	 */
	public boolean hasVisitedZone(final String zoneName) {
		return getKeyedSlot("!visited", zoneName) != null;
	}

	/**
	 * Checks if the player has visited the given zone.
	 *
	 * @param zone
	 *     The zone to check for.
	 * @return
	 *     <code>true</code> if player visited the zone.
	 */
	public boolean hasVisitedZone(final StendhalRPZone zone) {
		return hasVisitedZone(zone.getName());
	}

	/**
	 * offers the other player to start a trading session
	 *
	 * @param partner
	 *            to offer the trade to
	 */
	public void offerTrade(Player partner) {
		trade.offerTrade(partner);
	}

	/**
	 * gets the state of player to player trades
	 *
	 * @return TradeState
	 */
	public TradeState getTradeState() {
		return trade.getTradeState();
	}

	/**
	 * gets the partner of a player to player trade
	 *
	 * @return name of partner or <code>null</code> if no trade is ongoing
	 */
	protected String getTradePartner() {
		return trade.getPartnerName();
	}

	/**
	 * starts a trade with this partner
	 *
	 * @param partner
	 *            partner to trade with
	 */
	protected void startTrade(Player partner) {
		trade.startTrade(partner);
	}

	/**
	 * cancels a trade and moves the items back.
	 *
	 * @param partnerName
	 *            name of partner (to make sure the correct trade offer is
	 *            canceled)
	 */
	public void cancelTradeInternally(String partnerName) {
		trade.cancelTradeInternally(partnerName);
	}

	/**
	 * completes a trade internally.
	 */
	void completeTradeInternally() {
		trade.completeTradeInternally();
	}

	/**
	 * unlocks a trade item offer for example because of some modifications on
	 * the trade slot.
	 */
	public void unlockTradeItemOffer() {
		trade.unlockItemOffer();
	}

	/**
	 * internally unlocks
	 *
	 * @param partnerName
	 *            name of partner (to make sure the correct trade offer is
	 *            canceled)
	 * @return true, if a trade was unlocked, false if it was already unlocked
	 */
	boolean unlockTradeItemOfferInternally(String partnerName) {
		return trade.unlockItemOfferInternally(partnerName);
	}

	/**
	 * locks the item offer.
	 */
	public void lockTrade() {
		trade.lockItemOffer();
	}

	/**
	 * accepts the trade if both offers are locked.
	 */
	public void dealTrade() {
		trade.deal();
	}

	/**
	 * cancels a trade or trade offer.
	 */
	public void cancelTrade() {
		trade.cancelTrade();
	}

	/**
	 * Gets the how often this player has looted the given item
	 *
	 * @param item
	 *            the item name
	 * @return the number of loots from corpses
	 */
	public int getNumberOfLootsForItem(String item) {
		return itemCounter.getNumberOfLootsForItem(item);
	}

	/**
	 * Gets the amount a player as produced of an item
	 *
	 * @param item
	 *            the item name
	 * @return the produced amount
	 */
	public int getQuantityOfProducedItems(String item) {
		return itemCounter.getQuantityOfProducedItems(item);
	}

	/**
	 * Gets the amount a player as mined of an item
	 *
	 * @param item
	 *            the item name
	 * @return the mined amount
	 */
	public int getQuantityOfMinedItems(String item) {
		return itemCounter.getQuantityOfMinedItems(item);
	}

	/**
	 * Retrieve the amount of items sown by player.
	 *
	 * @param item
	 *   Item Name.
	 * @return
	 *   Integer sown quanity.
	 */
	public int getQuantityOfSownItems(String item) {
		return itemCounter.getQuantityOfSownItems(item);
	}

	/**
	 * Gets the amount a player has harvested of an item
	 *
	 * @param item
	 *            the item name
	 * @return the harvested amount
	 */
	public int getQuantityOfHarvestedItems(String item) {
		return itemCounter.getQuantityOfHarvestedItems(item);
	}

	/**
	 * @return the whole number of items a player has obtained from the well
	 */
	public int getQuantityOfObtainedItems() {
		return itemCounter.getQuantityOfObtainedItems();
	}

	/**
	 * Gets the amount of an item bought by player.
	 *
	 * @param item
	 * 		Item name.
	 * @return
	 * 		Number bought of the item.
	 */
	public int getQuantityOfBoughtItems(final String item) {
		return itemCounter.getQuantityOfBoughtItems(item);
	}

	/**
	 * Gets the amount of an item sold by player.
	 *
	 * @param item
	 * 		Item name.
	 * @return
	 * 		Number sold of the item.
	 */
	public int getQuantityOfSoldItems(final String item) {
		return itemCounter.getQuantityOfSoldItems(item);
	}

	/**
	 * Increases the count of loots for the given item
	 *
	 * @param item
	 *            the item name
	 * @param count
	 */
	public void incLootForItem(String item, int count) {
		itemCounter.incLootForItem(item, count);
		// check achievements in item category
		AchievementNotifier.get().onItemLoot(this);
	}

	/**
	 * Increases the count of producings for the given item
	 *
	 * @param item
	 *            the item name
	 * @param count
	 */
	public void incProducedForItem(String item, int count) {
		itemCounter.incProducedForItem(item, count);
		// check achievements in production category
		AchievementNotifier.get().onProduction(this);
	}

	/**
	 * Increases the count of obtains from the well for the given item
	 *
	 * @param name
	 *            the item name
	 * @param quantity
	 */
	public void incObtainedForItem(String name, int quantity) {
		itemCounter.incObtainedForItem(name, quantity);
		// check achievements in obtain category
		AchievementNotifier.get().onObtain(this);
	}

	/**
	 * Increases the count of sales for the given item
	 *
	 * @param name
	 *            the item name
	 * @param quantity
	 */
	public void incSoldForItem(String name, int quantity) {
		itemCounter.incSoldForItem(name, quantity);
		// check achievements in commerce category
		AchievementNotifier.get().onTrade(this);
	}

	/**
	 * Increases the amount of successful minings for the given item
	 *
	 * @param name
	 *            the item name
	 * @param quantity
	 */
	public void incMinedForItem(String name, int quantity) {
		itemCounter.incMinedForItem(name, quantity);
		// check achievements in obtain category
		AchievementNotifier.get().onObtain(this);
	}

	/**
	 * Increses the quanity an item was sown by player.
	 *
	 * @param item
	 *   Item name.
	 * @param count
	 *   Increment amount.
	 */
	public void incSownForItem(String name, int quantity) {
		itemCounter.incSownForItem(name, quantity);
		// this isn't the same as producing with an NPC but production is the most appropriate category
		AchievementNotifier.get().onProduction(this);
	}

	/**
	 * Increases the amount of successful harvestings for the given item
	 *
	 * @param name
	 *            the item name
	 * @param quantity
	 */
	public void incHarvestedForItem(String name, int quantity) {
		itemCounter.incHarvestedForItem(name, quantity);
		// check achievements in obtain category
		AchievementNotifier.get().onObtain(this);
	}

	/**
	 * Increases the amount of successful buyings for the given item
	 *
	 * @param name
	 *            the item name
	 * @param quantity
	 */
	public void incBoughtForItem(String name, int quantity) {
		itemCounter.incBoughtForItem(name, quantity);
		// check achievements in commerce category
		AchievementNotifier.get().onTrade(this);
	}

	/**
	 * Stores information about amount of money used & gained in NPC transactions.
	 *
	 * @param npcName
	 *     Name of NPC with whom transactions is being done.
	 * @param price
	 *     Amount of money exchanged.
	 * @param soldToNPC
	 *     <code>true</code> means player is selling to NPC, <code>false</code> player is buying from.
	 */
	public void incCommerceTransaction(final String npcName, final int price, final boolean soldToNPC) {
		int curAmount = 0;
		if (soldToNPC) {
			if (has("npc_sales", npcName)) {
				curAmount = Integer.parseInt(get("npc_sales", npcName));
			}
		} else {
			if (has("npc_purchases", npcName)) {
				curAmount = Integer.parseInt(get("npc_purchases", npcName));
			}
		}

		if (soldToNPC) {
			put("npc_sales", npcName, curAmount + price);
		} else {
			put("npc_purchases", npcName, curAmount + price);
		}

		AchievementNotifier.get().onTrade(this);
	}

	public int getCommerceTransactionAmount(final String npcName, final boolean soldToNPC) {
		int amount = 0;

		try {
			if (soldToNPC) {
				if (has("npc_sales", npcName)) {
					amount = Integer.parseInt(get("npc_sales", npcName));
				}
			} else {
				if (has("npc_purchases", npcName)) {
					amount = Integer.parseInt(get("npc_purchases", npcName));
				}
			}
		} catch (final NumberFormatException e) {
			logger.error(e, e);
		}

		return amount;
	}

	/**
	 * Gets the recorded item stored in a substate of quest slot
	 *
	 * @param questname
	 *            The quest's name
	 * @param index
	 *            the index of the sub state to get (separated by ";")
	 * @return the name of the required item (no formatting)
	 */
	public String getRequiredItemName(String questname, int index) {
		return quests.getRequiredItemName(questname, index);
	}

	/**
	 * Gets the recorded item quantity stored in a substate of quest slot
	 *
	 * @param questname
	 *            The quest's name
	 * @param index
	 *            the index of the sub state to get (separated by ";")
	 * @return required item quantity
	 */
	public int getRequiredItemQuantity(String questname, int index) {
		return quests.getRequiredItemQuantity(questname, index);
	}

	@Override
	protected void handleLeaveZone(int nx, final int ny) {
		// Players using continuous movement should stop if they cross
		// the zone border when using mouse for movement
		boolean stopAfter = hasPath();
		super.handleLeaveZone(nx, ny);
		if (stopAfter) {
			stop();
		}
	}

	/**
	 * gets the timestmap this client sent the last action
	 *
	 * @return action timestmap
	 */
	public long getLastClientActionTimestamp() {
		return lastClientActionTimestamp;
	}

	/**
	 * sets the timestamp at which this client sent the last action.
	 *
	 * @param lastClientActionTimestamp
	 *            action timestmap
	 */
	public void setLastClientActionTimestamp(long lastClientActionTimestamp) {
		this.lastClientActionTimestamp = lastClientActionTimestamp;
	}

	/**
	 * gets the language
	 *
	 * @return language
	 */
	@Override
	public String getLanguage() {
		return language;
	}

	/**
	 * sets the language
	 *
	 * @param language
	 *            language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * adds a use listener causing the client to add an use action with the
	 * specified name
	 *
	 * @param actionDisplayName
	 *            name of useaction visible in the client
	 * @param listener
	 *            use event listener
	 */
	public void setUseListener(String actionDisplayName, UseListener listener) {
		put("menu", actionDisplayName);
		this.useListener = listener;
	}

	/**
	 * gets the current UseListener
	 *
	 * @return UseListener
	 */
	public UseListener getUseListener() {
		return this.useListener;
	}

	/**
	 * removes a use event listener
	 */
	public void removeUseListener() {
		remove("menu");
		this.useListener = null;
	}

	/**
	 * has the player a use listener?
	 *
	 * @return true if there is a use listener registered, false otherwise
	 */
	public boolean hasUseListener() {
		return this.useListener != null;
	}

	/**
	 * Invoked when the object is used.
	 *
	 * @param user
	 *            the RPEntity who uses the object
	 * @return true if successful
	 */
	@Override
	public boolean onUsed(RPEntity user) {
		if (useListener == null || !(user instanceof Player)) {
			return false;
		}
		return useListener.onUsed(user);
	}

	/**
	 * gets the client version
	 *
	 * @return client version
	 */
	public String getClientVersion() {
		return clientVersion;
	}

	/**
	 * Get the maximum allowed ATK for a level.
	 *
	 * @param level
	 *            checked level
	 * @return maximum ATK
	 */
	private int getMaxAtkForLevel(int level) {
		return (int) (5 * Math.sqrt(level + 10));
	}

	/**
	 * Get the maximum allowed DEF for a level.
	 *
	 * @param level
	 *            checked level
	 * @return maximum DEF
	 */
	private int getMaxDefForLevel(int level) {
		if (level >= 150) {
			// After the stat boost quest
			return (int) (10 * Math.cbrt(level) + 60);
		}
		return getMaxAtkForLevel(level);
	}

	/**
	 * gets the capped atk level, which prevent players from training their atk
	 * way beyond what is reasonable for their level
	 *
	 * @return capped atk
	 */
	@Override
	public int getCappedAtk() {
		// Blue line in https://sourceforge.net/p/arianne/feature-requests/1330/
		// reduced using median instead of average as reference
		return Math.min(this.atk, getMaxAtkForLevel(level));
	}

	/**
	 * gets the capped def level, which prevent players from training their def
	 * way beyond what is reasonable for their level
	 *
	 * @return capped atk
	 */
	@Override
	public int getCappedDef() {
		// Red line in https://sourceforge.net/p/arianne/feature-requests/1330/
		return Math.min(this.def, getMaxDefForLevel(level));
	}

	/**
	 * Gets the capped ratk level, which prevent players from training their
	 * ratk way beyond what is reasonable for their level.
	 *
	 * XXX: Should use getMaxRatkForLevel() method instead?
	 *
	 * @return capped ratk
	 */
	@Override
	public int getCappedRatk() {
		return Math.min(this.ratk, getMaxAtkForLevel(level));
	}

	/**
	 * Collision handling instructions for players.
	 *
	 * @param nx
	 *        New horizontal position
	 * @param ny
	 *        New vertical position
	 */
	@Override
	protected void handleSimpleCollision(final int nx, final int ny) {
		if (isZoneChangeAllowed()) {
			if (getZone().leavesZone(this, nx, ny)) {
				handleLeaveZone(nx, ny);
				return;
			}
		}
		if (isGhost()) {
			this.move(getX(), getY(), nx, ny);
		} else {
			if (this.has(AUTOWALK)) {
				this.remove(AUTOWALK);
			}

			this.stop();
		}
	}

	/**
	 * returns the maximum size of a slot
	 *
	 * @param slot name of slot
	 * @return size, or -1 if no maximum is known
	 */
	public int getMaxSlotSize(String slot) {
		String value = this.getFeature(slot);
		if (value == null || value.equals("")) {
			return -1;
		}
		String[] values = value.split(" ");
		return Integer.parseInt(values[0]) * Integer.parseInt(values[1]);
	}

	/**
	 * Retrieves amount to apply to player ATK to adjust hit chance.
	 *
	 * @return
	 *   Value of player ATK multiplied by percentage defined in item "accuracy_bonus" attribute.
	 */
	private int getAccuracyBonus() {
		double accBonus = 0;
		for (final Item equip: getAllEquipment()) {
			accBonus += equip.has("accuracy_bonus") ? (equip.getDouble("accuracy_bonus") / 100) : 0;
		}
		// base on raw uncapped value
		return (int) Math.round(this.atk * accBonus);
	}

	/**
	 * This handicap increases chance that a player can hit an enemy to
	 * make the game feel more fair. Hit chance is based on raw atk stat,
	 * which is much higher for creatues. In order to avoid drastic
	 * changes to the game's balance, we also need to reduce the amount
	 * of damage done by players. See:
	 *     Player.damageDone.
	 */
	@Override
	protected int calculateRiskForCanHit(final int roll, final int defenderDEF,
			final int attackerATK) {
		// use 30 as multiple for players instead of 20
		return ((int) Math.round(HIT_CHANCE_MULTIPLIER * 1.5)) * (attackerATK + this.getAccuracyBonus())
				- roll * defenderDEF;
	}

	/**
	 * This is overridden to reduce damage done by players to creatures
	 * to make up for the increased hit chance.
	 */
	@Override
	public int damageDone(final RPEntity defender, double attackingWeaponsValue,
			final Nature damageType) {
		// compensate for player hit chance handicap
		return (int) Math.round(super.damageDone(defender, attackingWeaponsValue, damageType) / 1.35);
	}

	/**
	 * Gets an item that is carried by the RPEntity. If the item is stackable, gets all that are on
	 * the first stack that is found.
	 *
	 * @param itemName
	 *   The item's name
	 * @return
	 *   The item, or a stack of stackable items, or an empty list if nothing was found.
	 */
	public List<Item> getAllSubmittableEquipped(String itemName) {
		return getAllEquipped(Item.nameMatchesSubmittable(itemName));
	}

	/**
	 * Gets the number of quest submittable items of the given name that are carried by the player.
	 * The item can be stackable or non-stackable.
	 *
	 * @param itemName
	 *   The item's name
	 * @return
	 *   Number of carried submittable items.
	 */
	public int getNumberOfSubmittableEquipped(String itemName) {
		return equippedStream().filter(Item.nameMatchesSubmittable(itemName))
				.mapToInt(Item::getQuantity).sum();
	}

	/**
	 * Determines if this player is equipped with a minimum quantity of a submittable item.
	 *
	 * @param itemName
	 *   The item's name.
	 * @param amount
	 *   The minimum amount.
	 * @return
	 *   {@code true} if the item is equipped with the minimum number and is submittable.
	 */
	public boolean isSubmittableEquipped(String itemName, int amount) {
		return isEquipped(Item.nameMatchesSubmittable(itemName), amount);
	}

	/**
	 * Removes a specific amount of an item from the RPEntity if it is quest submittable. The item can
	 * either be stackable or non-stackable. The units can be distributed over different slots. If the
	 * player doesn't have enough units of the item, it doesn't remove anything.
	 *
	 * @param itemName
	 *   Name of the item.
	 * @param amount
	 *   Number of units that should be dropped.
	 * @return
	 *   {@code true} if dropping the desired amount was successful.
	 */
	public boolean dropSubmittable(String itemName, int amount) {
		return drop(Item.nameMatchesSubmittable(itemName), amount);
	}
}
