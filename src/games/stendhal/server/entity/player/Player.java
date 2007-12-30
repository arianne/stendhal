/* $Id$ */
/***************************************************************************
 *						(C) Copyright 2003 - Marauroa					   *
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

import games.stendhal.common.Direction;
import games.stendhal.common.FeatureList;
import games.stendhal.common.NotificationType;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TutorialNotifier;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.RaidCreature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.events.PrivateTextEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import marauroa.common.Pair;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.SyntaxException;

import org.apache.log4j.Logger;

public class Player extends RPEntity {
	/**
	 * The admin level attribute name.
	 */
	protected static final String ATTR_ADMINLEVEL = "adminlevel";

	/**
	 * The away message attribute name.
	 */
	protected static final String ATTR_AWAY = "away";

	/**
	 * The ghostmode attribute name.
	 */
	protected static final String ATTR_GHOSTMODE = "ghostmode";

	/**
	 * The attack invisible attribute name.
	 */
	protected static final String ATTR_INVISIBLE = "invisible";

	/**
	 * The grumpy attribute name.
	 */
	protected static final String ATTR_GRUMPY = "grumpy";

	/**
	 * The pet ID attribute name.
	 */
	protected static final String ATTR_PET = "pet";

	/**
	 * The sheep ID attribute name.
	 */
	protected static final String ATTR_SHEEP = "sheep";

	/**
	 * The teleclick mode attribute name.
	 */
	protected static final String ATTR_TELECLICKMODE = "teleclickmode";

	/**
	 * The name of the zone placed in when killed. TODO: Move to common class
	 * (maybe via method for runtime config)?
	 */
	public static final String DEFAULT_DEAD_AREA = "int_afterlife";

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(Player.class);

	/**
	 * The base log for karma use.
	 */
	private static final double KARMA_BASELOG = Math.log(10.0);

	/**
	 * A random generator (for karma payout).
	 */
	private static final Random KARMA_RANDOMIZER = new Random();

	/**
	 * The number of minutes that this player has been logged in on the server.
	 */
	private int age;

	/**
	 * Food, drinks etc. that the player wants to consume and has not finished
	 * with.
	 */
	private List<ConsumableItem> itemsToConsume;

	/**
	 * Poisonous items that the player still has to consume. This also includes
	 * poison that was the result of fighting against a poisonous creature.
	 */
	private List<ConsumableItem> poisonToConsume;

	/**
	 * Shows if this player is currently under the influence of an antidote, and
	 * thus immune from poison.
	 */
	private boolean isImmune;

	/**
	 * The last player who privately talked to this player using the /tell
	 * command. It needs to be stored non-persistently so that /answer can be
	 * used.
	 */
	private String lastPrivateChatterName;

	/**
	 * Currently active client directions (in oldest-newest order).
	 */
	protected List<Direction> directions;

	/**
	 * Karma (luck).
	 */
	protected double karma;

	/**
	 * A list of enabled client features.
	 */
	protected FeatureList features;

	/**
	 * A list of away replys sent to players.
	 */
	protected HashMap<String, Long> awayReplies;

	private PlayerSheepManager playerSheepManager;

	private PlayerPetManager playerPetManager;

	public static void generateRPClass() {
		try {
			PlayerRPClass.generateRPClass();
		} catch (SyntaxException e) {
			logger.error("cannot generateRPClass", e);
		}
	}

	public static Player create(RPObject object) {

		// add attributes and slots
		PlayerRPClass.updatePlayerRPObject(object);

		Player player = new Player(object);
		player.stop();
		player.stopAttack();

		if (player.has(ATTR_AWAY)) {
			player.remove(ATTR_AWAY);
		}
		// remove grumpy on login to give postman a chance to deliver messages
		// (and in the hope that player is receptive now)
		if (player.has(ATTR_GRUMPY)) {
			player.remove(ATTR_GRUMPY);
		}

		PlayerRPClass.readAdminsFromFile(player);
		PlayerRPClass.placePlayerIntoWorldOnLogin(object, player);
		PlayerRPClass.loadItemsIntoSlots(player);

		if (player.getSlot("!buddy").size() > 0) {
			RPObject buddies = player.getSlot("!buddy").iterator().next();
			for (String buddyName : buddies) {
				// TODO: Remove '_' prefix if ID is made completely virtual
				if (buddyName.charAt(0) == '_') {
					Player buddy = StendhalRPRuleProcessor.get().getPlayer(
							buddyName.substring(1));
					if ((buddy != null) && !buddy.isGhost()) {
						buddies.put(buddyName, 1);
					} else {
						buddies.put(buddyName, 0);
					}
				}
			}
		}

		convertOldfeaturesList(player);

		player.updateItemAtkDef();

		PlayerRPClass.welcome(player);

		logger.debug("Finally player is :" + player);
		return player;
	}

	private static void convertOldfeaturesList(Player player) {
		if (player.has("features")) {
			logger.info("Converting features for " + player.getName() + ": "
					+ player.get("features"));

			FeatureList features = new FeatureList();
			features.decode(player.get("features"));

			for (String name : features) {
				player.setFeature(name, features.get(name));
			}

			player.remove("features");
		}
	}

	public static void destroy(Player player) {
		Sheep sheep = player.getSheep();

		if (sheep != null) {
			sheep.getZone().remove(sheep);

			/*
			 * NOTE: Once the sheep is stored there is no more trace of zoneid.
			 */
			player.playerSheepManager.storeSheep(sheep);
		} else {
			// Bug on pre 0.20 released
			if (player.hasSlot("#flock")) {
				player.removeSlot("#flock");
			}
		}

		Pet pet = player.getPet();

		if (pet != null) {
			pet.getZone().remove(pet);

			/*
			 * NOTE: Once the pet is stored there is no more trace of zoneid.
			 */
			player.playerPetManager.storePet(pet);
		}
		player.stop();
		player.stopAttack();

		/*
		 * Normally a zoneid attribute shouldn't logically exist after an entity
		 * is removed from a zone, but we need to keep it for players so that it
		 * can be serialised.
		 * 
		 * TODO: Find a better way to decouple "active" zone info from "resume"
		 * zone info, or save just before removing from zone instead.
		 */
		// TODO: Create <Entity>.remove(void) ?
		player.getZone().remove(player);

		player.disconnected = true;
	}

	public Player(RPObject object) {
		super(object);
		playerSheepManager = new PlayerSheepManager(this);
		playerPetManager = new PlayerPetManager(this);
		setRPClass("player");
		put("type", "player");
		// HACK: postman as NPC
		if (object.has("name") && object.get("name").equals("postman")) {
			put("title_type", "npc");
		}

		setSize(1, 1);

		itemsToConsume = new LinkedList<ConsumableItem>();
		poisonToConsume = new LinkedList<ConsumableItem>();
		directions = new ArrayList<Direction>();
		awayReplies = new HashMap<String, Long>();

		// Beginner's luck (unless overriden by update)
		karma = 10.0;
		baseSpeed = 1.0;
		update();
	}

	/**
	 * Add an active client direction.
	 * 
	 * 
	 */
	public void addClientDirection(Direction direction) {
		if (hasPath()) {
			clearPath();
		}

		directions.remove(direction);
		directions.add(direction);
	}

	/**
	 * Remove an active client direction.
	 * 
	 * 
	 */
	public void removeClientDirection(Direction direction) {
		directions.remove(direction);
	}

	/**
	 * Apply the most recent active client direction.
	 * 
	 * @param stopOnNone
	 *            Stop movement if no (valid) directions are active if
	 *            <code>true</code>.
	 */
	public void applyClientDirection(boolean stopOnNone) {
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
			if (isPoisoned()) {
				direction = direction.oppositeDirection();
			}

			setDirection(direction);
			setSpeed(getBaseSpeed());
		} else if (stopOnNone) {
			stop();
		}
	}

	@Override
	public boolean isObstacle(Entity entity) {
		if (entity instanceof Player) {
			/*
			 * TODO: Create a world cached reference of this zone for identity
			 * compares instead?
			 */
			if (getZone().getName().equals(DEFAULT_DEAD_AREA)) {
				return false;
			}
		}

		return super.isObstacle(entity);
	}

	/**
	 * Stop and clear any active directions.
	 */
	@Override
	public void stop() {
		directions.clear();
		super.stop();
	}

	/**
	 * Get the away message.
	 * 
	 * @return The away message, or <code>null</code> if unset.
	 */
	public String getAwayMessage() {
		return has(ATTR_AWAY) ? get(ATTR_AWAY) : null;
	}

	/**
	 * Set the away message.
	 * 
	 * @param message
	 *            An away message, or <code>null</code>.
	 */
	public void setAwayMessage(final String message) {
		if (message != null) {
			put(ATTR_AWAY, message);
		} else if (has(ATTR_AWAY)) {
			remove(ATTR_AWAY);
		}

		resetAwayReplies();
	}

	/**
	 * Check if another player should be notified that this player is away. This
	 * assumes the player has already been checked for away. Players will be
	 * reminded once an hour.
	 * 
	 * @param name
	 *            The name of the other player.
	 * 
	 * @return <code>true</code> if the player should be notified.
	 */
	public boolean isAwayNotifyNeeded(String name) {
		long now = System.currentTimeMillis();
		Long lObj = awayReplies.get(name);

		if (lObj != null) {
			/*
			 * Only notify once an hour
			 */
			if ((now - lObj.longValue()) < (1000L * 60L * 60L)) {
				return false;
			}
		}

		awayReplies.put(name, now);
		return true;
	}

	/**
	 * Clear out all recorded away responses.
	 */
	public void resetAwayReplies() {
		awayReplies.clear();
	}

	/**
	 * Get the grumpy message.
	 * 
	 * @return The grumpy message, or <code>null</code> if unset.
	 */
	public String getGrumpyMessage() {
		return has(ATTR_GRUMPY) ? get(ATTR_GRUMPY) : null;
	}

	/**
	 * Set the grumpy message.
	 * 
	 * @param message
	 *            A grumpy message, or <code>null</code>.
	 */
	public void setGrumpyMessage(final String message) {
		if (message != null) {
			put(ATTR_GRUMPY, message);
		} else if (has(ATTR_GRUMPY)) {
			remove(ATTR_GRUMPY);
		}

	}

	/**
	 * Give the player some karma (good or bad).
	 * 
	 * @param karma
	 *            An amount of karma to add/subtract.
	 */
	@Override
	public void addKarma(double karma) {
		this.karma += karma;

		put("karma", karma);
	}

	/**
	 * Get the current amount of karma.
	 * 
	 * @return The current amount of karma.
	 * 
	 * @see-also #addKarma()
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
	public double useKarma(double scale) {
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
	public double useKarma(double negLimit, double posLimit) {
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
	public double useKarma(double negLimit, double posLimit, double granularity) {
		double limit;
		double score;

		if (logger.isDebugEnabled()) {
			logger.debug("karma request: " + negLimit + " <= x <= " + posLimit);
		}

		/*
		 * Calculate the maximum payout (based on what we have)
		 */
		limit = Math.log(Math.abs(karma) + 1.0) / KARMA_BASELOG;

		/*
		 * Positive or Negative?
		 */
		if (karma < 0.0) {
			if (negLimit >= 0.0) {
				return 0.0;
			}

			limit = Math.max(negLimit, -limit);
		} else {
			if (posLimit <= 0.0) {
				return 0.0;
			}

			limit = Math.min(posLimit, limit);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("karma limit: " + limit);
		}

		/*
		 * Give at least 20% of possible payout
		 */
		score = (0.2 + (KARMA_RANDOMIZER.nextDouble() * 0.8)) * limit;

		/*
		 * Clip to grandularity
		 */
		score = ((int) (score / granularity)) * granularity;

		/*
		 * with a lucky charm you use up less karma to be just as lucky
		 */
		if (this.isEquipped("lucky_charm")) {
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
	 * Process changes that to the object attributes. This may be called several
	 * times (unfortunately) due to the requirements of the class's constructor,
	 * sometimes before prereqs are initialised.
	 */
	@Override
	public void update() {
		super.update();

		if (has("xp")) {
			// I want to force level to be updated.
			addXP(0);
		}

		if (has("age")) {
			age = getInt("age");
		}

		if (has("karma")) {
			karma = getDouble("karma");
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
	 * @return <code>true</code> if value changed, <code>false</code> if
	 *         there was a problem.
	 */
	public boolean addIgnore(String name, int duration, String reply) {
		StringBuffer sbuf;

		sbuf = new StringBuffer();

		if (duration != 0) {
			sbuf.append(System.currentTimeMillis() + (duration * 60000L));
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
	public String getIgnore(String name) {
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
	 * @return <code>true</code> if value changed, <code>false</code> if
	 *         there was a problem.
	 */
	public boolean removeIgnore(String name) {
		return setKeyedSlot("!ignore", "_" + name, null);
	}

	/**
	 * Get a named skills value.
	 * 
	 * @param key
	 *            The skill key.
	 * 
	 * @return The skill value, or <code>null</code> if not set.
	 */
	public String getSkill(String key) {
		return getKeyedSlot("skills", key);
	}

	/**
	 * Set a named skills value.
	 * 
	 * @param key
	 *            The skill key.
	 * @param value
	 *            The skill value.
	 * 
	 * @return <code>true</code> if value changed, <code>false</code> if
	 *         there was a problem.
	 */
	public boolean setSkill(String key, String value) {
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
	public String getKeyedSlot(String name, String key) {
		RPSlot slot;
		RPObject object;

		if (!hasSlot(name)) {
			logger.error("Expected to find " + name + " slot");
			return null;
		}

		slot = getSlot(name);

		if (slot.size() == 0) {
			logger.error("Found empty " + name + " slot");
			return null;
		}

		object = slot.iterator().next();

		return object.has(key) ? object.get(key) : null;
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
	 * @return <code>true</code> if value changed, <code>false</code> if
	 *         there was a problem.
	 */
	public boolean setKeyedSlot(String name, String key, String value) {
		RPSlot slot;
		RPObject object;

		if (!hasSlot(name)) {
			logger.error("Expected to find " + name + " slot");
			return false;
		}

		slot = getSlot(name);

		if (slot.size() == 0) {
			logger.error("Found empty " + name + " slot");
			return false;
		}

		object = slot.iterator().next();

		if (value != null) {
			object.put(key, value);
		} else if (object.has(key)) {
			object.remove(key);
		}

		return true;
	}

	/**
	 * Get a client feature value.
	 * 
	 * @param name
	 *            The feature mnemonic.
	 * 
	 * @return The feature value, or <code>null</code> is not-enabled.
	 */
	public String getFeature(String name) {
		return getKeyedSlot("!features", name);
	}

	/**
	 * Determine if a client feature is enabled.
	 * 
	 * @param name
	 *            The feature mnemonic.
	 * 
	 * @return <code>true</code> if the feature is enabled.
	 */
	public boolean hasFeature(String name) {
		return (getKeyedSlot("!features", name) != null);
	}

	/**
	 * Enable/disable a client feature.
	 * 
	 * @param name
	 *            The feature mnemonic.
	 * @param enabled
	 *            Flag indicating if enabled.
	 */
	public void setFeature(String name, boolean enabled) {
		if (enabled) {
			setFeature(name, "");
		} else {
			setFeature(name, null);
		}
	}

	/**
	 * Set/remove a client feature. <strong>NOTE: The names and values MUST NOT
	 * contain <code>=</code> (equals), or <code>:</code> (colon).
	 * 
	 * @param name
	 *            The feature mnemonic.
	 * @param value
	 *            The feature value, or <code>null</code> to disable.
	 */
	public void setFeature(String name, String value) {
		setKeyedSlot("!features", name, value);
	}

	/**
	 * Determine if the entity is invisible to creatures.
	 * 
	 * @return <code>true</code> if invisible.
	 */
	@Override
	public boolean isInvisible() {
		return has(ATTR_INVISIBLE);
	}

	/**
	 * Set whether this player is invisible to creatures.
	 * 
	 * @param invisible
	 *            <code>true</code> if invisible.
	 */
	public void setInvisible(final boolean invisible) {
		if (invisible) {
			put(ATTR_INVISIBLE, "");
		} else if (has(ATTR_INVISIBLE)) {
			remove(ATTR_INVISIBLE);
		}
	}

	/**
	 * Sends a message that only this player can read.
	 * 
	 * @param text
	 *            the message.
	 */
	@Override
	public void sendPrivateText(String text) {
		sendPrivateText(NotificationType.PRIVMSG, text);
	}

	/**
	 * Sends a message that only this player can read.
	 * 
	 * @param type
	 *            NotificationType
	 * @param text
	 *            the message.
	 */
	public void sendPrivateText(NotificationType type, String text) {
		addEvent(new PrivateTextEvent(type, text));
	}

	/**
	 * Sets the name of the last player who privately talked to this player
	 * using the /tell command. It needs to be stored non-persistently so that
	 * /answer can be used.
	 */
	public void setLastPrivateChatter(String lastPrivateChatterName) {
		this.lastPrivateChatterName = lastPrivateChatterName;
	}

	/**
	 * Gets the name of the last player who privately talked to this player
	 * using the /tell command, or null if nobody has talked to this player
	 * since he logged in.
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
		if (!has(ATTR_ADMINLEVEL)) {
			return 0;
		}
		return getInt(ATTR_ADMINLEVEL);
	}

	/**
	 * Set the player's admin level.
	 * 
	 * @param adminlevel
	 *            The new admin level.
	 */
	public void setAdminLevel(final int adminlevel) {
		put(ATTR_ADMINLEVEL, adminlevel);
	}

	@Override
	public void onAttacked(Entity attacker, boolean keepAttacking) {
		super.onAttacked(attacker, keepAttacking);
		if (keepAttacking) {
			TutorialNotifier.attacked(this);
		}
	}

	@Override
	public void onDead(Entity killer) {
		put("dead", "");

		// Abandon dependants
		Sheep sheep = getSheep();

		if (sheep != null) {
			removeSheep(sheep);
		}

		Pet pet = getPet();

		if (pet != null) {
			removePet(pet);
		}

		// We stop eating anything
		itemsToConsume.clear();
		poisonToConsume.clear();

		if (!(killer instanceof RaidCreature)) {

			List<Item> ringList = getAllEquipped("emerald_ring");
			boolean eRingUsed = false;

			for (Item emeraldRing : ringList) {
				int amount = emeraldRing.getInt("amount");
				if (amount > 0) {
					// We broke the emerald ring.
					emeraldRing.put("amount", amount - 1);
					eRingUsed = true;
					break;
				}
			}

			if (eRingUsed) {
				// Penalize: 1% less experience if wearing that ring
				setXP((int) (getXP() * 0.99));
				setATKXP((int) (getATKXP() * 0.99));
				setDEFXP((int) (getDEFXP() * 0.99));
			} else {
				// Penalize: 10% less experience
				setXP((int) (getXP() * 0.9));
				setATKXP((int) (getATKXP() * 0.9));
				setDEFXP((int) (getDEFXP() * 0.9));
			}

			update();
		}

		super.onDead(killer, false);

		setHP(getBaseHP());

		returnToOriginalOutfit();

		// After a tangle with the grim reaper, give some karma,
		// but limit abuse
		if (getKarma() < 75.0) {
			addKarma(100.0);
		}

		// Penalize: Respawn on afterlive zone and
		StendhalRPZone zone = StendhalRPWorld.get().getZone(DEFAULT_DEAD_AREA);

		if (zone != null) {
			if (!zone.placeObjectAtEntryPoint(this)) {
				logger.error("Unable to place player in zone " + zone + ": "
						+ getName());
			}
		} else {
			logger.error("Unable to find dead area [" + DEFAULT_DEAD_AREA
					+ "] for player: " + getName());
		}
	}

	@Override
	protected void dropItemsOn(Corpse corpse) {
		// drop at least 1 and at most 4 items
		int maxItemsToDrop = Rand.rand(4);
		List<Pair<RPObject, RPSlot>> objects = new LinkedList<Pair<RPObject, RPSlot>>();

		for (String slotName : CARRYING_SLOTS) {
			if (!hasSlot(slotName)) {
				logger.error("CARRYING_SLOTS contains a slot that player "
						+ getName() + " doesn't have.");
			} else {
				RPSlot slot = getSlot(slotName);

				// a list that will contain the objects that will
				// be dropped.

				// get a random set of items to drop
				for (RPObject objectInSlot : slot) {
					// don't drop special quest rewards as there is no way to
					// get them again
					// TODO: Assert these as Item's and use getBoundTo() and
					// isUndroppableOnDeath()
					if (objectInSlot.has("bound")
							|| objectInSlot.has("undroppableondeath")) {
						continue;
					}
					objects.add(new Pair<RPObject, RPSlot>(objectInSlot, slot));
				}
			}
		}
		Collections.shuffle(objects);

		for (int i = 0; i < maxItemsToDrop; i++) {
			if (!objects.isEmpty()) {
				Pair<RPObject, RPSlot> object = objects.remove(0);
				if (object.first() instanceof StackableItem) {
					StackableItem item = (StackableItem) object.first();

					// We won't drop the full quantity, but only a
					// percentage.
					// Get a random percentage between 26 % and 75 % to drop
					double percentage = (Rand.rand(50) + 25) / 100.0;
					int quantityToDrop = (int) Math.round(item.getQuantity()
							* percentage);

					if (quantityToDrop > 0) {
						StackableItem itemToDrop = item.splitOff(quantityToDrop);
						corpse.add(itemToDrop);
					}
				} else if (object.first() instanceof PassiveEntity) {
					object.second().remove(object.first().getID());

					corpse.add((PassiveEntity) object.first());
				}
			}
		}
	}

	public void removeSheep(Sheep sheep) {
		sheep.setOwner(null);

		if (has(ATTR_SHEEP)) {
			remove(ATTR_SHEEP);
		} else {
			logger.warn("Called removeSheep but player has not sheep: " + this);
		}
	}

	public void removePet(Pet pet) {
		pet.setOwner(null);

		if (has(ATTR_PET)) {
			remove(ATTR_PET);
		} else {
			logger.warn("Called removePet but player has not pet: " + this);
		}
	}

	public boolean hasSheep() {
		return has(ATTR_SHEEP);
	}

	public boolean hasPet() {
		return has(ATTR_PET);
	}

	/**
	 * Set the player's pet. This will also set the pet's owner.
	 * 
	 * @param pet
	 *            The pet.
	 */
	public void setPet(Pet pet) {
		put(ATTR_PET, pet.getID().getObjectID());
		pet.setOwner(this);
	}

	/**
	 * Set the player's sheep. This will also set the sheep's owner.
	 * 
	 * @param sheep
	 *            The sheep.
	 */
	public void setSheep(Sheep sheep) {
		put(ATTR_SHEEP, sheep.getID().getObjectID());
		sheep.setOwner(this);
	}

	/**
	 * Get the player's sheep.
	 * 
	 * @return The sheep.
	 */
	public Sheep getSheep() {
		if (has(ATTR_SHEEP)) {
			try {
				return (Sheep) StendhalRPWorld.get().get(
						new RPObject.ID(getInt(ATTR_SHEEP), get("zoneid")));
			} catch (Exception e) {
				// TODO: Remove catch after DB reset
				logger.error("Pre 1.00 Marauroa sheep bug. (player = "
						+ getName() + ")", e);

				if (has(ATTR_SHEEP)) {
					remove(ATTR_SHEEP);
				}

				if (hasSlot("#flock")) {
					removeSlot("#flock");
				}

				return null;
			}
		} else {
			return null;
		}
	}

	public Pet getPet() {
		try {
			if (has(ATTR_PET)) {
				return (Pet) StendhalRPWorld.get().get(
						new RPObject.ID(getInt(ATTR_PET), get("zoneid")));
			} else {
				return null;
			}
		} catch (ClassCastException e) {
			remove(ATTR_PET);
			logger.error("removed pets attribute" + e);
			return null;
		}

	}

	/**
	 * Gets the number of minutes that this player has been logged in on the
	 * server.
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
		return (getAge() < 2 * 60) || (getATK() < 15) || (getDEF() < 15)
				|| (getLevel() < 5);
	}

	/**
	 * Sets the number of minutes that this player has been logged in on the
	 * server.
	 * 
	 * @param age
	 *            minutes
	 */
	public void setAge(int age) {
		this.age = age;
		put("age", age);
		TutorialNotifier.aged(this, age);
	}

	/**
	 * Updates the last pvp action time with the current time.
	 */
	public void storeLastPVPActionTime() {
		put("last_pvp_action_time", System.currentTimeMillis());
	}

	/**
	 * returns the time the player last did an PVP action
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
	public void notifyOnline(String who) {
		String playerOnline = "_" + who;

		boolean found = false;
		RPSlot slot = getSlot("!buddy");
		if (slot.size() > 0) {
			RPObject buddies = slot.iterator().next();
			for (String name : buddies) {
				if (playerOnline.equals(name)) {
					buddies.put(playerOnline, 1);
					notifyWorldAboutChanges();
					found = true;
					break;
				}
			}
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
	public void notifyOffline(String who) {
		String playerOffline = "_" + who;

		boolean found = false;
		RPSlot slot = getSlot("!buddy");
		if (slot.size() > 0) {
			RPObject buddies = slot.iterator().next();
			for (String name : buddies) {
				if (playerOffline.equals(name)) {
					buddies.put(playerOffline, 0);
					notifyWorldAboutChanges();
					found = true;
					break;
				}
			}
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
	 * Checks whether the player has completed the given quest or not.
	 * 
	 * @param name
	 *            The quest's name
	 * @return true iff the quest has been completed by the player
	 */
	public boolean isQuestCompleted(String name) {
		String info = getKeyedSlot("!quests", name);

		if (info == null) {
			return false;
		}

		return info.equals("done");
	}

	/**
	 * Checks whether the player has made any progress in the given quest or
	 * not. For many quests, this is true right after the quest has been
	 * started.
	 * 
	 * @param name
	 *            The quest's name
	 * @return true iff the player has made any progress in the quest
	 */
	public boolean hasQuest(String name) {
		return (getKeyedSlot("!quests", name) != null);
	}

	/**
	 * Gets the player's current status in the given quest.
	 * 
	 * @param name
	 *            The quest's name
	 * @return the player's status in the quest
	 */
	public String getQuest(String name) {
		return getKeyedSlot("!quests", name);
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
	 * @param status
	 *            the player's status in the quest. Set it to null to completely
	 *            reset the player's status for the quest.
	 */
	public void setQuest(String name, String status) {
		String oldStatus = getKeyedSlot("!quests", name);
		setKeyedSlot("!quests", name, status);
		if ((status == null) || !status.equals(oldStatus)) {
			StendhalRPRuleProcessor.get().addGameEvent(this.getName(), "quest",
					name, status);
		}
	}

	public List<String> getQuests() {
		RPSlot slot = getSlot("!quests");
		RPObject quests = slot.iterator().next();

		List<String> questsList = new LinkedList<String>();
		for (String quest : quests) {
			if (!quest.equals("id") && !quest.equals("zoneid")) {
				questsList.add(quest);
			}
		}
		return questsList;
	}

	public void removeQuest(String name) {
		setKeyedSlot("!quests", name, null);
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
	public boolean isQuestInState(String name, String... states) {
		String questState = getQuest(name);

		if (questState != null) {
			for (String state : states) {
				if (questState.equals(state)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks if the player has ever killed a creature with the given name
	 * without the help of any other player.
	 * 
	 * @param The
	 *            name of the creature to check.
	 * @return true iff this player has ever killed this creature on his own.
	 */
	public boolean hasKilledSolo(String name) {
		String info = getKeyedSlot("!kills", name);

		if (info == null) {
			return false;
		}
		return "solo".equals(info);
	}

	/**
	 * Checks if the player has ever killed a creature, with or without the help
	 * of any other player.
	 * 
	 * @param The
	 *            name of the creature to check.
	 * @return true iff this player has ever killed this creature on his own.
	 */
	public boolean hasKilled(String name) {
		return (getKeyedSlot("!kills", name) != null);
	}

	/**
	 * Checks in which way this player has killed the creature with the given
	 * name.
	 * 
	 * @param The
	 *            name of the creature to check.
	 * @return either "solo", "shared", or null.
	 */
	public String getKill(String name) {
		return getKeyedSlot("!kills", name);
	}

	/**
	 * Stores in which way the player has killed a creature with the given name.
	 * 
	 * @param The
	 *            name of the killed creature.
	 * @param mode
	 *            either "solo", "shared", or null.
	 */
	private void setKill(String name, String mode) {
		setKeyedSlot("!kills", name, mode);
	}

	/**
	 * Stores that the player has killed 'name' solo. Overwrites shared kills of
	 * 'name'
	 * 
	 */
	public void setSoloKill(String name) {
		setKill(name, "solo");
	}

	/**
	 * Stores that the player has killed 'name' with help of others. Does not
	 * overwrite solo kills of 'name'
	 * 
	 */
	public void setSharedKill(String name) {
		if (!hasKilledSolo(name)) {
			setKill(name, "shared");
		}

	}

	/**
	 * Makes the game think that this player has never killed a creature with
	 * the given name. Use this for quests where the player should kill a
	 * creature of a specific type.
	 * 
	 * @param name
	 *            The name of the creature.
	 */
	public void removeKill(String name) {
		setKeyedSlot("!kills", name, null);
	}

	/**
	 * Checks whether the player is still suffering from the effect of a
	 * poisonous item/creature or not.
	 */
	public boolean isPoisoned() {
		return !(poisonToConsume.size() == 0);
	}

	/**
	 * Disburdens the player from the effect of a poisonous item/creature.
	 */
	public void healPoison() {
		poisonToConsume.clear();
	}

	/**
	 * Poisons the player with a poisonous item. Note that this method is also
	 * used when a player has been poisoned while fighting against a poisonous
	 * creature.
	 * 
	 * @param item
	 *            the poisonous item
	 * @return true iff the poisoning was effective, i.e. iff the player is not
	 *         immune
	 */
	public boolean poison(ConsumableItem item) {
		if (isImmune) {
			return false;
		} else {
			// put("poisoned", "0");
			poisonToConsume.add(item);
			TutorialNotifier.poisoned(this);
			return true;
		}
	}

	public boolean isFull() {
		return itemsToConsume.size() > 5;
	}

	public void eat(ConsumableItem item) {
		put("eating", 0);
		itemsToConsume.add(item);
	}

	public void setImmune() {
		if (has("poisoned")) {

			remove("poisoned");

		}
		poisonToConsume.clear();
		isImmune = true;
	}

	public void removeImmunity() {
		isImmune = false;
		sendPrivateText("You are not immune from poison anymore.");
	}

	public void consume(int turn) {
		Collections.sort(itemsToConsume);
		if (itemsToConsume.size() > 0) {
			ConsumableItem food = itemsToConsume.get(0);
			if (food.consumed()) {
				itemsToConsume.remove(0);
			} else {
				if (turn % food.getFrecuency() == 0) {
					logger.debug("Consumed item: " + food);
					int amount = food.consume();
					put("eating", amount);
					if (heal(amount, true) == 0) {
						itemsToConsume.clear();
					}
				}
			}
		} else {
			if (has("eating")) {
				remove("eating");
			}
		}

		if ((poisonToConsume.size() == 0)) {
			if (has("poisoned")) {
				remove("poisoned");
			}
		} else {
			List<ConsumableItem> poisonstoRemove = new LinkedList<ConsumableItem>();
			int sum = 0;
			int amount = 0;
			for (ConsumableItem poison : new LinkedList<ConsumableItem>(
					poisonToConsume)) {
				if (turn % poison.getFrecuency() == 0) {
					if (poison.consumed()) {
						poisonstoRemove.add(poison);
					} else {
						amount = poison.consume();
						damage(-amount, poison);
						sum += amount;
						put("poisoned", sum);
					}
				}

			}
			for (ConsumableItem poison : poisonstoRemove) {
				poisonToConsume.remove(poison);
			}
		}

		notifyWorldAboutChanges();
	}

	// TODO: use the turn notifier for consumable items to get rid of
	// Player.consume().

	@Override
	public String describe() {

		// A special description was specified
		if (hasDescription()) {
			return (getDescription());
		}

		// default description for player includes there name, level and play
		// time
		int hours = age / 60;
		int minutes = age % 60;
		String time = hours + " hours and " + minutes + " minutes";
		String text = "You see " + getTitle() + ".\n" + getTitle()
				+ " is level " + getLevel() + " and has been playing " + time
				+ ".";
		return (text);
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
	public boolean teleport(StendhalRPZone zone, int x, int y, Direction dir,
			Player teleporter) {
		if (StendhalRPAction.placeat(zone, this, x, y)) {
			if (dir != null) {
				this.setDirection(dir);
			}
			notifyWorldAboutChanges();
			return true;
		} else {
			String text = "Position [" + x + "," + y + "] is occupied";
			if (teleporter != null) {
				teleporter.sendPrivateText(text);
			} else {
				this.sendPrivateText(text);
			}
			return false;
		}

	}

	/**
	 * Removes all units of an item from the RPEntity. The item can either be
	 * stackable or non-stackable. If the RPEntity doesn't have any of the item,
	 * doesn't remove anything.
	 * 
	 * @param name
	 *            The name of the item
	 * @return true iff dropping the item was successful.
	 */
	public boolean dropAll(String name) {
		return drop(name, getNumberOfEquipped(name));
	}

	private int pushCounter;

	/**
	 * Called when player push entity. The entity displacement is handled by the
	 * action itself.
	 * 
	 * @param entity
	 */
	public void onPush(RPEntity entity) {
		pushCounter = StendhalRPRuleProcessor.get().getTurn();
	}

	/**
	 * Return true if player can push entity.
	 * 
	 * @param entity
	 * @return true iff pushing is possible
	 */
	public boolean canPush(RPEntity entity) {
		return ((this != entity) && (StendhalRPRuleProcessor.get().getTurn()
				- pushCounter > 10));
	}

	@Override
	public void setOutfit(Outfit outfit) {
		setOutfit(outfit, false);
	}

	/**
	 * Makes this player wear the given outfit. If the given outfit contains
	 * null parts, the current outfit will be kept for these parts.
	 * 
	 * @param outfit
	 *            The new outfit.
	 * @param temporary
	 *            If true, the original outfit will be stored so that it can be
	 *            restored later.
	 */
	public void setOutfit(Outfit outfit, boolean temporary) {
		// if the new outfit is temporary and the player is not wearing
		// a temporary outfit already, store the current outfit in a
		// second slot so that we can return to it later.
		if (temporary && !has("outfit_org")) {
			put("outfit_org", get("outfit"));
		}

		// if the new outfit is not temporay, remove the backup
		if (!temporary && has("outfit_org")) {
			remove("outfit_org");
		}

		// combine the old outfit with the new one, as the new one might
		// contain null parts.
		Outfit newOutfit = outfit.putOver(getOutfit());
		put("outfit", newOutfit.getCode());
		notifyWorldAboutChanges();
	}

	public Outfit getOriginalOutfit() {
		if (has("outfit_org")) {
			return new Outfit(getInt("outfit_org"));
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
		Outfit originalOutfit = getOriginalOutfit();
		if (originalOutfit != null) {
			remove("outfit_org");
			setOutfit(originalOutfit, false);
			return true;
		}
		return false;
	}

	/**
	 * gets the sheep manager for this player
	 * 
	 * @return PlayerSheepManager
	 */
	public PlayerSheepManager getPlayerSheepManager() {
		return playerSheepManager;
	}

	/**
	 * gets the pet manager for this player
	 * 
	 * @return PlayerPetManager
	 */
	public PlayerPetManager getPlayerPetManager() {
		return playerPetManager;
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
	protected boolean isZoneChangeAllowed() {
		/*
		 * If we are too far from dependents, then disallow zone change
		 */
		Sheep sheep = getSheep();

		if (sheep != null) {
			if (squaredDistance(sheep) > (7 * 7)) {
				return false;
			}
		}

		Pet pet = getPet();

		if (pet != null) {
			if (squaredDistance(pet) > (7 * 7)) {
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

		int turn = StendhalRPRuleProcessor.get().getTurn();

		/*
		 * TODO: Refactor Implement the attack rate into attack itself. Done in
		 * the new RP.
		 */
		if (isAttacking()
				&& ((turn % StendhalRPAction.getAttackRate(this)) == 0)) {
			StendhalRPAction.attack(this, getAttackTarget());
		}

		agePlayer(turn);

		consume(turn);
	}

	private void agePlayer(int turn) {
		/*
		 * 180 means 60 seconds x 3 turns per second.
		 */
		if ((turn % 180) == 0) {
			setAge(getAge() + 1);
			notifyWorldAboutChanges();
		}
	}

	/**
	 * Checks whether an entity is a ghost (non physically interactive).
	 * 
	 * @return <code>true</code> if in ghost mode.
	 */
	@Override
	public boolean isGhost() {
		return has(ATTR_GHOSTMODE);
	}

	/**
	 * Set whether this player is a ghost (invisible/non-interactive).
	 * 
	 * @param ghost
	 *            <code>true</code> if a ghost.
	 */
	public void setGhost(final boolean ghost) {
		if (ghost) {
			put(ATTR_GHOSTMODE, "");
		} else if (has(ATTR_GHOSTMODE)) {
			remove(ATTR_GHOSTMODE);
		}
	}

	/**
	 * Checks whether a player has teleclick enabled.
	 * 
	 * @return <code>true</code> if teleclick is enabled.
	 */
	public boolean isTeleclickEnabled() {
		return has(ATTR_TELECLICKMODE);
	}

	/**
	 * Set whether this player has teleclick enabled.
	 * 
	 * @param teleclick
	 *            <code>true</code> if teleclick enabled.
	 */
	public void setTeleclickEnabled(final boolean teleclick) {
		if (teleclick) {
			put(ATTR_TELECLICKMODE, "");
		} else if (has(ATTR_TELECLICKMODE)) {
			remove(ATTR_TELECLICKMODE);
		}
	}

	/**
	 * Called when this object is added to a zone.
	 * 
	 * @param zone
	 *            The zone this was added to.
	 */
	@Override
	public void onAdded(StendhalRPZone zone) {
		super.onAdded(zone);

		String zoneName = zone.getID().getID();

		/*
		 * If player enters afterlife, make them partially transparent
		 */
		if (zoneName.equals(DEFAULT_DEAD_AREA)) {
			setVisibility(50);
		}

		/*
		 * Remember zones we've been in
		 */
		setKeyedSlot("!visited", zoneName,
				Long.toString(System.currentTimeMillis()));
	}

	/**
	 * Called when this object is being removed from a zone.
	 * 
	 * @param zone
	 *            The zone this will be removed from.
	 */
	@Override
	public void onRemoved(StendhalRPZone zone) {
		/*
		 * If player leaves afterlife, make them normal
		 */
		if (zone.getID().getID().equals(DEFAULT_DEAD_AREA)) {
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
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Player) {
			Player other = (Player) obj;
			return this.getName().equals(other.getName());
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

	public String getSentence() {
		// TODO: Sentence here.
		return "";
	}

	/**
	* @deprecated instead use utilities.TestPlayer to get private text messages
	* in JUnit tests
	*/
	public String getPrivateText() {
		// TODO: remove this hack, it is just a preliminary way to get the test
		// working again
		StringBuilder sb = null;
		for (RPEvent event : events()) {
			if (event.getName().equals("private_text")) {
				if (sb == null) {
					sb = new StringBuilder();
				} else {
					sb.append("\r\n");
				}
				sb.append(event.get("text"));
			}
		}
		if (sb == null) {
			return null;
		}
		return sb.toString();
	}

	private boolean disconnected = false;

	public boolean isDisconnected() {
		return disconnected;
	}
}
