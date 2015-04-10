/* $Id$ */
/***************************************************************************
 *					(C) Copyright 2003-2015 - Arianne					   *
 ***************************************************************************
 ***************************************************************************
 *																		   *
 *	 This program is free software; you can redistribute it and/or modify  *
 *	 it under the terms of the GNU General Public License as published by  *
 *	 the Free Software Foundation; either version 2 of the License, or	   *
 *	 (at your option) any later version.								   *
 *																		   *
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
import games.stendhal.common.Direction;
import games.stendhal.common.ItemTools;
import games.stendhal.common.KeyedSlotUtil;
import games.stendhal.common.Level;
import games.stendhal.common.NotificationType;
import games.stendhal.common.TradeState;
import games.stendhal.common.Version;
import games.stendhal.common.constants.Nature;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.SyntaxException;

import org.apache.log4j.Logger;

public class Player extends RPEntity implements UseListener {

	private static final String LAST_PLAYER_KILL_TIME = "last_player_kill_time";
	private static final String[] RECOLORABLE_OUTFIT_PARTS = { "detail",
			"dress", "hair", "body", "head", "eyes" };

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

	/**
	 * A list of away replies sent to players.
	 */
	private final HashMap<String, Long> awayReplies;

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
		/* TODO: Remove condition after ranged stat testing is finished. */
		if (Testing.COMBAT) {
			player.put("ratk", 10);
			player.put("ratk_xp", 0);
		}
		player.put("level", 0);
		player.setXP(0);

		// define outfit
		Outfit outfit = null;
		/* TODO: Remove condition when outfit testing is finished. */
		if (Testing.OUTFITS) {
			if (template != null && template.has("outfit_extended")) {
				outfit = new Outfit(template.getLong("outfit_extended"));
			}
		}
		if (template != null && template.has("outfit")) {
			outfit = new Outfit(template.getInt("outfit"));
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
		awayReplies = new HashMap<String, Long>();

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

		/* TODO: Remove condition when ranged stat testing is finished. */
		if (Testing.COMBAT) {
			/*
			 * TODO: Remove if VOLATILE definition is removed from "ratk" and
			 * "ratk_xp" attributes.
			 */
			if (!this.has("ratk_xp")) {
				/*
				 * If an existing character does not have ranged stat set it at
				 * same level and experience as new character.
				 */
				this.put("ratk", 10);
				this.put("ratk_xp", 0);

				/*
				 * if player's current level is 20 or higher give a buffer based
				 * on about 25% of current atk experience.
				 */
				if (this.getLevel() > 19) {
					/*
					 * Using setRatkXPinternal() instead of setRatkXP() here to
					 * avoid multiple calls to updateModifiedAttributes().
					 */
					// FIXME: Is this formula accurate?
					this.setRatkXPInternal((int) (this.getAtkXP() * 0.0026),
							false);
				}
			}
		}

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

		resetAwayReplies();
	}

	/**
	 * Clear out all recorded away responses.
	 */
	private void resetAwayReplies() {
		awayReplies.clear();
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
	 * Sends a message that only this player can read.
	 *
	 * @param type
	 *            NotificationType
	 * @param text
	 *            the message.
	 */
	@Override
	public void sendPrivateText(final NotificationType type, final String text) {
		this.addEvent(new PrivateTextEvent(type, text));
		this.notifyWorldAboutChanges();
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
	 *            quest
	 * @param states
	 *            valid states
	 * @return true, if the quest is in one of theses states, false otherwise
	 */
	public boolean isQuestInState(final String name, final String... states) {
		return quests.isQuestInState(name, states);
	}

	/**
	 * Is the named quest in one of the listed states?
	 *
	 * @param name
	 *            quest
	 * @param index
	 *            quest index
	 * @param states
	 *            valid states
	 * @return true, if the quest is in one of theses states, false otherwise
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
	 *            of the creature to check.
	 * @return true iff this player has ever killed this creature.
	 */
	public boolean hasKilled(final String name) {
		return killRec.hasKilled(name);
	}

	/**
	 * Checks if the player has ever 'solo killed' a creature, i.e. without the
	 * help of any other player.
	 *
	 * @param name
	 *            of the creature to check.
	 * @return true iff this player has ever killed this creature on his own.
	 */
	public boolean hasKilledSolo(final String name) {
		return killRec.hasKilledSolo(name);
	}

	/**
	 * Checks if the player has ever 'shared killed' a creature, i.e. with the
	 * help of any other player.
	 *
	 * @param name
	 *            of the creature to check.
	 * @return true iff this player has ever killed this creature in a team.
	 */
	public boolean hasKilledShared(final String name) {
		return killRec.hasKilledShared(name);
	}

	/**
	 * Stores that the player has killed 'name' solo. Overwrites shared kills of
	 * 'name'
	 * 
	 * @param name
	 *            of the victim
	 */
	public void setSoloKill(final String name) {
		killRec.setSoloKill(name);
	}

	/**
	 * Stores that the player has killed 'name' with help of others. Does not
	 * overwrite solo kills of 'name'
	 * 
	 * @param name
	 *            of victim
	 *
	 */
	public void setSharedKill(final String name) {
		killRec.setSharedKill(name);
	}

	/**
	 * Returns how much the player has killed 'name' solo.
	 * 
	 * @param name
	 *            of the victim
	 * @return number of solo kills
	 */
	public int getSoloKill(final String name) {
		return killRec.getSoloKill(name);
	}

	/**
	 * Returns how much the player has killed 'name' with help of others.
	 * 
	 * @param name
	 *            of victim
	 * @return number of shared kills
	 */
	public int getSharedKill(final String name) {
		return killRec.getSharedKill(name);
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
	 * @return true iff teleporting was successful
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
	 * Called when player push entity. The entity displacement is handled by the
	 * action itself.
	 *
	 * @param entity
	 */
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

	@Override
	public void setOutfit(final Outfit outfit) {
		setOutfit(outfit, false);
	}

	/**
	 * Makes this player wear the given outfit. If the given outfit contains
	 * null parts, the current outfit will be kept for these parts. If the
	 * outfit change includes any colors, they should be changed <b>after</b>
	 * calling this.
	 *
	 * @param outfit
	 *            The new outfit.
	 * @param temporary
	 *            If true, the original outfit will be stored so that it can be
	 *            restored later.
	 */
	public void setOutfit(final Outfit outfit, final boolean temporary) {
		// if the new outfit is temporary and the player is not wearing
		// a temporary outfit already, store the current outfit in a
		// second slot so that we can return to it later.
		/**
		 * TODO: Remove condition and duplicate code after outfit testing is
		 * finished.
		 */
		if (Testing.OUTFITS) {
			if (temporary && !has("outfit_extended_org")) {
				put("outfit_extended_org", get("outfit_extended"));

				// remember the old color selections.
				for (String part : RECOLORABLE_OUTFIT_PARTS) {
					String tmp = part + "_orig";
					String color = get("outfit_colors", part);
					if (color != null) {
						put("outfit_colors", tmp, color);
						if (!"hair".equals(part)) {
							remove("outfit_colors", part);
						}
					} else if (has("outfit_colors", tmp)) {
						// old saved colors need to be cleared in any case
						remove("outfit_colors", tmp);
					}
				}
			}

		} else {
			if (temporary && !has("outfit_org")) {
				put("outfit_org", get("outfit"));

				// remember the old color selections.
				for (String part : RECOLORABLE_OUTFIT_PARTS) {
					String tmp = part + "_orig";
					String color = get("outfit_colors", part);
					if (color != null) {
						put("outfit_colors", tmp, color);
						if (!"hair".equals(part)) {
							remove("outfit_colors", part);
						}
					} else if (has("outfit_colors", tmp)) {
						// old saved colors need to be cleared in any case
						remove("outfit_colors", tmp);
					}
				}
			}
		}

		/*
		 * TODO: Remove condition and duplicate code after outfit testing is
		 * finished.
		 */
		if (Testing.OUTFITS) {
			if (!temporary && has("outfit_extended_org")) {
				remove("outfit_extended_org");

				// clear colors
				for (String part : RECOLORABLE_OUTFIT_PARTS) {
					if (has("outfit_colors", part)) {
						remove("outfit_colors", part);
					}
				}
			}

		} else {
			// if the new outfit is not temporary, remove the backup
			if (!temporary && has("outfit_org")) {
				remove("outfit_org");

				// clear colors
				for (String part : RECOLORABLE_OUTFIT_PARTS) {
					if (has("outfit_colors", part)) {
						remove("outfit_colors", part);
					}
				}
			}
		}

		// combine the old outfit with the new one, as the new one might
		// contain null parts.
		final Outfit newOutfit = outfit.putOver(getOutfit());
		/* TODO: Remove condition when outfit testing is finished. */
		if (Testing.OUTFITS) {
			put("outfit_extended", newOutfit.getExtendedCode());
		}
		put("outfit", newOutfit.getCode());
		notifyWorldAboutChanges();
	}

	public Outfit getOriginalOutfit() {
		/*
		 * TODO: Remove condition and duplicate code when outfit testing is
		 * finished.
		 */
		if (Testing.OUTFITS) {
			if (has("outfit_extended_org")) {
				return new Outfit(getLong("outfit_extended_org"));
			}
		} else {
			if (has("outfit_org")) {
				return new Outfit(getInt("outfit_org"));
			}
		}
		return null;
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

		final Outfit originalOutfit = getOriginalOutfit();
		if (originalOutfit != null) {

			// do not restore details layer, unless the detail is still present
			if (originalOutfit.getDetail() > 0) {
				final Outfit currentOutfit = getOutfit();
				if (!currentOutfit.getDetail().equals(
						originalOutfit.getDetail())) {
					originalOutfit.removeDetail();
				}
			}

			/* TODO: Remove condition after outfit testing is finished. */
			if (Testing.OUTFITS) {
				remove("outfit_extended_org");
			} else {
				remove("outfit_org");
			}
			setOutfit(originalOutfit, false);

			// restore old colors
			for (String part : RECOLORABLE_OUTFIT_PARTS) {
				String tmp = part + "_orig";
				String color = get("outfit_colors", tmp);
				if (color != null) {
					put("outfit_colors", part, color);
					remove("outfit_colors", tmp);
				} else if (has("outfit_colors", part)) {
					// clear any colors from the temporary outfit if there was
					// no saved color
					remove("outfit_colors", part);
				}
			}
			return true;
		}
		return false;
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
	protected void applyDefXP(final RPEntity entity) {
		if (getsFightXpFrom(entity)) {
			incDefXP();
		}
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
	public void setLevel(int level) {
		final int oldLevel = super.getLevel();
		super.setLevel(level);

		// reward players on level up
		if (oldLevel < level) {
			AchievementNotifier.get().onLevelChange(this);
			this.addEvent(new SoundEvent("tadaa-1", SoundLayer.USER_INTERFACE));
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
	 * Checks if the player has visited the given zone
	 * 
	 * @param zone
	 *            the zone to check for
	 * @return true if player visited the zone
	 */
	public boolean hasVisitedZone(StendhalRPZone zone) {
		return null != getKeyedSlot("!visited", zone.getName());
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
	 * Increases the count of loots for the given item
	 * 
	 * @param item
	 *            the item name
	 * @param count
	 */
	public void incLootForItem(String item, int count) {
		itemCounter.incLootForItem(item, count);
	}

	/**
	 * Increases the count of producings for the given item
	 * 
	 * @param item
	 *            the item name
	 * @param count
	 */
	public void incProducedCountForItem(String item, int count) {
		itemCounter.incProducedForItem(item, count);
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

	/**
	 * Gets the number of repetitions in a substate of quest slot
	 *
	 * @param questname
	 *            The quest's name
	 * @param index
	 *            the index of the sub state to get (separated by ";")
	 * @return the integer value in the index of the quest slot, used to
	 *         represent a number of repetitions
	 */
	public int getNumberOfRepetitions(final String questname, final int index) {
		return quests.getNumberOfRepetitions(questname, index);
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

		ExpireOutfit expireOutfit = new ExpireOutfit(super.getName());
		SingletonRepository.getTurnNotifier().dontNotify(expireOutfit);
		SingletonRepository.getTurnNotifier().notifyInSeconds(
				(newExpire - age) * 60, expireOutfit);
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
		// Blue line in http://sourceforge.net/p/arianne/feature-requests/1330/
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
		// Red line in http://sourceforge.net/p/arianne/feature-requests/1330/
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
	 * Call super method and force player to stop if using auto-walk when path
	 * is finished.
	 */
	@Override
	public void onFinishedPath() {
		super.onFinishedPath();

		/* FIXME: Hack: Removing auto-walk should be handled in setPath() and
		 *        stopping should work when a character's path is finished
		 *        regardless of auto-walk state.
		 */
		if (!this.stopped() && this.has(AUTOWALK)) {
			this.remove(AUTOWALK);
			this.stop();
		}
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
		} else if (Testing.MOVEMENT) { // TODO: Remove condition after auto-walk testing is finished.
			if (this.has(AUTOWALK)) {
				this.remove(AUTOWALK);
			}

			this.stop();
		}
	}
}
