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

import static games.stendhal.common.constants.Actions.*;

import games.stendhal.common.Constants;
import games.stendhal.common.Direction;
import games.stendhal.common.FeatureList;
import games.stendhal.common.ItemTools;
import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TutorialNotifier;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.DomesticAnimal;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.RingOfLife;
import games.stendhal.server.entity.item.Stackable;
import games.stendhal.server.events.PrivateTextEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.SyntaxException;

import org.apache.log4j.Logger;

public class Player extends RPEntity {
	
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
	 * A list of away replies sent to players.
	 */
	protected HashMap<String, Long> awayReplies;
	
	/**
	 * Food, drinks etc. that the player wants to consume and has not finished
	 * with.
	 */
	List<ConsumableItem> itemsToConsume;

	/**
	 * Poisonous items that the player still has to consume. This also includes
	 * poison that was the result of fighting against a poisonous creature.
	 */
	List<ConsumableItem> poisonToConsume;


	private final PlayerQuests quests = new PlayerQuests(this);
	private final PlayerDieer  dieer  = new PlayerDieer(this);
	private final KillRecording killRec = new KillRecording(this);
	private final PetOwner petOwner = new PetOwner(this);

	/**
	 * The number of minutes that this player has been logged in on the server.
	 */
	private int age;


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

	

	public static void generateRPClass() {
		try {
			PlayerRPClass.generateRPClass();
		} catch (final SyntaxException e) {
			logger.error("cannot generateRPClass", e);
		}
	}
	



	public static Player createEmptyZeroLevelPlayer(final String characterName) {
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
		player.setXP(0);


		final Outfit randomoutfit = Outfit.getRandomOutfit();
		player.setOutfit(randomoutfit);

		for (final String slot : Constants.CARRYING_SLOTS) {
			player.addSlot(slot);
		}

		player.update();

        Entity entity = SingletonRepository.getEntityManager().getItem("leather armor");
        RPSlot slot = player.getSlot("armor");
        slot.add(entity);
        
        entity = SingletonRepository.getEntityManager().getItem("club");
        slot = player.getSlot("rhand");
        slot.add(entity);
        
   	
        return player;
	}



	public static void destroy(final Player player) {
		player.getPetOwner().destroy();
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

		player.getZone().remove(player);

		player.disconnected = true;
	}

	public Player(final RPObject object) {
		super(object);

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
	 * @param direction 
	 * 
	 * 
	 */
	public void addClientDirection(final Direction direction) {
		if (hasPath()) {
			clearPath();
		}

		directions.remove(direction);
		directions.add(direction);
	}

	/**
	 * Remove an active client direction.
	 * @param direction 
	 * 
	 * 
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
	public boolean isObstacle(final Entity entity) {
		if (entity instanceof Player) {
			if (getZone().getName().equals(PlayerDieer.DEFAULT_DEAD_AREA)) {
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
		if (has(AWAY)) {
			return get(AWAY);
		} else {
			return null;
		}
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
	 * Check if another player should be notified that this player is away. This
	 * assumes the player has already been checked for away. Players will be
	 * reminded once an hour.
	 * 
	 * @param name
	 *            The name of the other player.
	 * 
	 * @return <code>true</code> if the player should be notified.
	 */
	public boolean isAwayNotifyNeeded(final String name) {
		final long now = System.currentTimeMillis();
		final Long lObj = awayReplies.get(name);

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
		if (has(GRUMPY)) {
			return get(GRUMPY);
		} else {
			return null;
		}
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
	}

	/**
	 * Get the current amount of karma.
	 * 
	 * @return The current amount of karma.
	 * 
	 * @see #addKarma()
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
	public double useKarma(final double negLimit, final double posLimit, final double granularity) {
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
		score = (0.2 + (KARMA_RANDOMIZER.nextDouble() * 0.8)) * limit;

		/*
		 * Clip to granularity. Use floor() instead of round() so that the player 
		 * never uses more karma than she has.
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
	public boolean addIgnore(final String name, final int duration, final String reply) {
		final StringBuilder sbuf = new StringBuilder();

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
	 * @return <code>true</code> if value changed, <code>false</code> if
	 *         there was a problem.
	 */
	public boolean removeIgnore(final String name) {
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
	public String getSkill(final String key) {
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
	public boolean setSkill(final String key, final String value) {
		return setKeyedSlot("skills", key, value);
	}

	/**
	 * Returns the single object of a "keyed slot".
	 * @param object 
	 * 
	 * @param name name of key slot
	 * @return object or <code>null</code> it does not exist
	 */
	static RPObject getKeyedSlotObject(final RPObject object, final String name) {
		if (!object.hasSlot(name)) {
			logger.error("Expected to find " + name + " slot in " + object);
			return null;
		}

		final RPSlot slot = object.getSlot(name);

		if (slot.size() == 0) {
			logger.error("Found empty " + name + " slot" + object);
			return null;
		}

		return slot.iterator().next();
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
		final RPObject object = Player.getKeyedSlotObject(this, name);
		if (object == null) {
			return null;
		}

		if (object.has(key)) {
			return object.get(key);
		} else {
			return null;
		}
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
	public boolean setKeyedSlot(final String name, final String key, final String value) {
		final RPObject object = Player.getKeyedSlotObject(this, name);
		if (object == null) {
			return false;
		}

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
	public String getFeature(final String name) {
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
	public boolean hasFeature(final String name) {
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
	public void setFeature(final String name, final boolean enabled) {
		if (enabled) {
			setFeature(name, "");
		} else {
			setFeature(name, null);
		}
	}

	/**
	 * Sets/removes a client feature.
	 * <p> <strong>NOTE: The names and values MUST NOT
	 * contain <code>=</code> (equals), or <code>:</code> (colon). </strong>
	 * 
	 * @param name
	 *            The feature mnemonic.
	 * @param value
	 *            The feature value, or <code>null</code> to disable.
	 */
	public void setFeature(final String name, final String value) {
		setKeyedSlot("!features", name, value);
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
	 * Sends a message that only this player can read.
	 * 
	 * @param text
	 *            the message.
	 */
	@Override
	public void sendPrivateText(final String text) {
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
	public void sendPrivateText(final NotificationType type, final String text) {
		addEvent(new PrivateTextEvent(type, text));
		this.notifyWorldAboutChanges();
	}

	/**
	 * Sets the name of the last player who privately talked to this player
	 * using the /tell command. It needs to be stored non-persistently so that
	 * /answer can be used.
	 * @param lastPrivateChatterName 
	 */
	public void setLastPrivateChatter(final String lastPrivateChatterName) {
		this.lastPrivateChatterName = lastPrivateChatterName;
	}

	/**
	 * Gets the name of the last player who privately talked to this player
	 * using the /tell command, or null if nobody has talked to this player
	 * since he logged in.
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
	public void onDead(final Entity killer, final boolean remove) {
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
	public void setAge(final int age) {
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
		final String playerOnline = "_" + who;

		boolean found = false;
		final RPSlot slot = getSlot("!buddy");
		if (slot.size() > 0) {
			final RPObject buddies = slot.iterator().next();
			for (final String name : buddies) {
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
	public void notifyOffline(final String who) {
		final String playerOffline = "_" + who;

		boolean found = false;
		final RPSlot slot = getSlot("!buddy");
		if (slot.size() > 0) {
			final RPObject buddies = slot.iterator().next();
			for (final String name : buddies) {
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
	 * @return true iff the player has made any progress in the quest
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
	 * "done" has a special meaning: see isQuestComplete().
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
	 * Checks if the player has ever killed a creature, with or without the help
	 * of any other player.
	 * 
	 * @param name of the creature to check.
	 * @return true iff this player has ever killed this creature.
	 */
	public boolean hasKilled(final String name) {
		return killRec.hasKilled(name);
	}


	/**
	 * Checks if the player has ever 'solo killed' a creature, i.e. without the help
	 * of any other player.
	 * 
	 * @param name of the creature to check.
	 * @return true iff this player has ever killed this creature on his own.
	 */
	public boolean hasKilledSolo(final String name) {
		return killRec.hasKilledSolo(name);
	}
	
	/**
	 * Stores that the player has killed 'name' solo. Overwrites shared kills of
	 * 'name'
	 * @param name of the victim
	 */
	public void setSoloKill(final String name) {
		killRec.setSoloKill(name);
	}

	/**
	 * Stores that the player has killed 'name' with help of others. Does not
	 * overwrite solo kills of 'name'
	 * @param name of victim
	 * 
	 */
	public void setSharedKill(final String name) {
		killRec.setSharedKill(name);
	}

	/**
	 * Makes the game think that this player has never killed a creature with
	 * the given name. Use this for quests where the player should kill a
	 * creature of a specific type.
	 * 
	 * @param name
	 *            The name of the creature.
	 */
	public void removeKill(final String name) {
		killRec.removeKill(name);
	}

	/**
	 * Checks whether the player is still suffering from the effect of a
	 * poisonous item/creature or not.
	 * @return true if player still has poisons to consume
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
	public boolean poison(final ConsumableItem item) {
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
		return itemsToConsume.size() > 4;
	}

	public boolean isChoking() {
		return itemsToConsume.size() > 5;
	}

	public boolean isChokingToDeath() {
		return itemsToConsume.size() > 8;
	}

	public void eat(final ConsumableItem item) {
		if (isChoking()) {
			put("choking", 0);
		} else {
			put("eating", 0);
		}
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

	public void consume(final int turn) {
		Collections.sort(itemsToConsume);
		if (itemsToConsume.size() > 0) {
			final ConsumableItem food = itemsToConsume.get(0);
			if (food.consumed()) {
				itemsToConsume.remove(0);
			} else {
				if (turn % food.getFrecuency() == 0) {
					logger.debug("Consumed item: " + food);
					final int amount = food.consume();
					if (isChoking()) {
						put("choking", amount);
					} else {
						if (has("choking")) {
							remove("choking");
						}
						put("eating", amount);
					}
					if (heal(amount, true) == 0) {
						itemsToConsume.clear();
					}
				}
			}
		} else {
			if (has("eating")) {
				remove("eating");
			}
			if (has("choking")) {
				remove("choking");
			}
		}

		if ((poisonToConsume.size() == 0)) {
			if (has("poisoned")) {
				remove("poisoned");
			}
		} else {
			final List<ConsumableItem> poisonstoRemove = new LinkedList<ConsumableItem>();
			int sum = 0;
			int amount = 0;
			for (final ConsumableItem poison : new LinkedList<ConsumableItem>(
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
			for (final ConsumableItem poison : poisonstoRemove) {
				poisonToConsume.remove(poison);
			}
		}

		notifyWorldAboutChanges();
	}

	@Override
	public String describe() {

		// A special description was specified
		if (hasDescription()) {
			return (getDescription());
		}

		// default description for player includes their name, level and play
		// time
		final int hours = age / 60;
		final int minutes = age % 60;
		final String time = hours + " hours and " + minutes + " minutes";
		final String text = "You see " + getTitle() + ".\n" + getTitle()
				+ " is level " + getLevel() + " and has been playing " + time
				+ ".";
		final StringBuilder sb = new StringBuilder();
		sb.append(text);
		final String awayMessage = getAwayMessage();
		if (awayMessage != null) {
			sb.append("\n" + getTitle() + " is away and has left a message: ");
			sb.append(awayMessage);
		}
		final String grumpyMessage = getGrumpyMessage();
		if (grumpyMessage != null) {
			sb.append("\n" +  getTitle() + " is grumpy and has left a message: ");
			sb.append(grumpyMessage);
		}
		return (sb.toString());
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
	public boolean teleport(final StendhalRPZone zone, final int x, final int y, final Direction dir,
			final Player teleporter) {
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
	 * Removes all units of an item from the RPEntity. The item can either be
	 * stackable or non-stackable. If the RPEntity doesn't have any of the item,
	 * doesn't remove anything.
	 * 
	 * @param name
	 *            The name of the item
	 * @return true iff dropping the item was successful.
	 */
	public boolean dropAll(final String name) {
		return drop(name, getNumberOfEquipped(name));
	}

	private int turnOfLastPush;

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
		return ((this != entity) && (SingletonRepository.getRuleProcessor().getTurn()
				- turnOfLastPush > 10));
	}

	@Override
	public void setOutfit(final Outfit outfit) {
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
	public void setOutfit(final Outfit outfit, final boolean temporary) {
		// if the new outfit is temporary and the player is not wearing
		// a temporary outfit already, store the current outfit in a
		// second slot so that we can return to it later.
		if (temporary && !has("outfit_org")) {
			put("outfit_org", get("outfit"));
		}

		// if the new outfit is not temporary, remove the backup
		if (!temporary && has("outfit_org")) {
			remove("outfit_org");
		}

		// combine the old outfit with the new one, as the new one might
		// contain null parts.
		final Outfit newOutfit = outfit.putOver(getOutfit());
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
		final Outfit originalOutfit = getOriginalOutfit();
		if (originalOutfit != null) {
			remove("outfit_org");
			setOutfit(originalOutfit, false);
			return true;
		}
		return false;
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
			if (squaredDistance(sheep) > (7 * 7)) {
				return false;
			}
		}

		final Pet pet = getPet();

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

		final int turn = SingletonRepository.getRuleProcessor().getTurn();

		if (isAttacking()
				&& ((turn % getAttackRate()) == 0)) {
			StendhalRPAction.playerAttack(this, getAttackTarget());
		}

		agePlayer(turn);

		consume(turn);
	}

	private void agePlayer(final int turn) {
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
			return this.getName().toLowerCase().equals(other.getName().toLowerCase());
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

	public void setSentence(final String arg) {
		put("sentence", arg);		
	}
	
	public String getSentence() {
		String result = "";
		if (has("sentence")) {
			result = get("sentence");
		}
		
		return result;
	}

	private boolean disconnected = false;

	public boolean isDisconnected() {
		return disconnected;
	}

	public List<RingOfLife> getAllEquippedWorkingRingOfLife() {
		final List<RingOfLife> result = new LinkedList<RingOfLife>();

		for (final String slotName : CARRYING_SLOTS) {
			final RPSlot slot = getSlot(slotName);

			for (final RPObject object : slot) {
				if (object instanceof RingOfLife) {
					final RingOfLife ring =  (RingOfLife) object;
					if (!ring.isBroken()) {
						result.add(ring);
					}
					
				}
			}
		}

		return result;
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
	 * @param name the name or type of the pet to search
	 * @param exactly <code>true</code> if looking only for matching 
	 * 	name instead of both name and type.
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
        			if ((type != null) && ItemTools.itemNameToDisplayName(type).equals(name)) {
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
			incDEFXP();
		}
	}
	@Override
	protected void handleObjectCollision() {
		clearPath();
	}


	public void setDamage(final int damage) {
		put("damage", damage);
		
	}


	public boolean isImmune() {
		return isImmune;
	}
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

		for (final String killerName : playersToReward) {
            final Player killer = SingletonRepository.getRuleProcessor().getPlayer(killerName);
            // check logout  
            if (killer != null) {
				TutorialNotifier.killedPlayer(killer);
            }
		}
	}


	public void equip(final Item item, final int amount) {
		if (item instanceof Stackable) {
			((Stackable) item).setQuantity(amount);
			super.equipToInventoryOnly(item);
		} else {
			for (int i = 1; i <= amount; i++) {
				super.equipOrPutOnGround(item);
			}
		}
		super.equipToInventoryOnly(item); 
		
	}


	public PetOwner getPetOwner() {
		return petOwner;
	}

	public boolean isBoundTo(final Item item) {
		return getName().equals(item.getBoundTo());
	}
}
