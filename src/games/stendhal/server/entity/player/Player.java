/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
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

import games.stendhal.common.Direction;
import games.stendhal.common.FeatureList;
import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.RaidCreature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.events.TutorialNotifier;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObjectInvalidException;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

public class Player extends RPEntity {

	public static final String DEFAULT_DEAD_AREA = "int_afterlife";

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(Player.class);

	/**
	 * The normal walking speed.
	 */
	private static final double	BASE_SPEED		= 1.0;

	/**
	 * The base log for karma use.
	 */
	private static final double KARMA_BASELOG = Math.log(10.0);

	/**
	 * A random generator (for karma payout).
	 */
	private static final Random karmaRand = new Random();

	/**
	 * The number of minutes that this player has been logged in on the
	 * server.
	 */
	private int age;

	/**
	 * Food, drinks etc. that the player wants to consume and has not finished
	 * with.
	 */
	private List<ConsumableItem> itemsToConsume;

	/**
	 * Poisonous items that the player still has to consume. This also
	 * includes poison that was the result of fighting against a poisonous
	 * creature.
	 */
	private List<ConsumableItem> poisonToConsume;

	/**
	 * Shows if this player is currently under the influence of an
	 * antidote, and thus immune from poison.
	 */
	private boolean isImmune;

	/**
	 * The last player who privately talked to this player using
	 * the /tell command. It needs to be stored non-persistently
	 * so that /answer can be used.
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

	private PlayerSheepManager playerSheepManager = null;

	private PlayerPetManager playerPetManager = null;

	public static void generateRPClass() {
		try {
			PlayerRPClass.generateRPClass();
		} catch (RPClass.SyntaxException e) {
			logger.error("cannot generateRPClass", e);
		}
	}

	public static Player create(RPObject object) {

		// add attributes and slots
		PlayerRPClass.updatePlayerRPObject(object);

		Player player = new Player(object);

		player.stop();
		player.stopAttack();

		if (player.has("away")) {
			player.remove("away");
		}

		PlayerRPClass.readAdminsFromFile(player);
		PlayerRPClass.placePlayerIntoWorldOnLogin(object, player);
		PlayerRPClass.loadItemsIntoSlots(player);

		if (player.getSlot("!buddy").size() > 0) {
			RPObject buddies = player.getSlot("!buddy").iterator().next();
			for (String name : buddies) {
				// what is this underscore supposed to do?
				if (name.charAt(0) == '_') {
					// cut off the strange underscore
					Player buddy = StendhalRPRuleProcessor.get().getPlayer(name.substring(1));
					if (buddy != null && !buddy.isGhost()) {
						player.notifyOnline(buddy.getName());
					} else {
						player.notifyOffline(name.substring(1));
					}
				}
			}
		}
		player.updateItemAtkDef();

		PlayerRPClass.welcome(player);

		logger.debug("Finally player is :" + player);
		return player;
	}

	public static void destroy(Player player) {
		StendhalRPWorld world = StendhalRPWorld.get();

		try {
			if (player.hasSheep()) {
				Sheep sheep = player.getSheep();
				StendhalRPRuleProcessor.get().removeNPC(sheep);
				world.remove(sheep.getID());
				player.playerSheepManager.storeSheep(sheep);
			} else {
				// Bug on pre 0.20 released
				if (player.hasSlot("#flock")) {
					player.removeSlot("#flock");
				}
			}
		} catch (Exception e) /**
		 * No idea how but some players get a sheep but
		 * they don't have it really. Me thinks that it
		 * is a player that has been running for a while
		 * the game and was kicked of server because
		 * shutdown on a pre 1.00 version of Marauroa.
		 * We shouldn't see this anymore.
		 */
		{
			logger.error("Pre 1.00 Marauroa sheep bug. (player = " + player.getName() + ")", e);

			if (player.has("sheep")) {
				player.remove("sheep");
			}

			if (player.hasSlot("#flock")) {
				player.removeSlot("#flock");
			}
		}
		if (player.hasPet()) {
			Pet pet = (Pet) world.remove(player.getPet());
			StendhalRPRuleProcessor.get().removeNPC(pet);
			world.remove(pet.getID());
			player.playerPetManager.storePet(pet);
		}
		player.stop();
		player.stopAttack();

		world.remove(player.getID());
	}

	public Player(RPObject object) throws AttributeNotFoundException {
		super(object);
		playerSheepManager = new PlayerSheepManager(this);
		playerPetManager = new PlayerPetManager(this);
		put("type", "player");
		// HACK: postman as NPC
		if (object.has("name") && object.get("name").equals("postman")) {
			put("title_type", "npc");
		}

		itemsToConsume = new LinkedList<ConsumableItem>();
		poisonToConsume = new LinkedList<ConsumableItem>();
		directions = new ArrayList<Direction>();
		awayReplies = new HashMap<String, Long>();
		features = new FeatureList();

		// Beginner's luck (unless overriden by update)
		karma = 10.0;

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
	 * @param	stopOnNone	Stop movement if no (valid) directions
	 *				are active if <code>true</code>.
	 */
	public void applyClientDirection(boolean stopOnNone) {
		int size;
		Direction direction;

		/*
		 * For now just take last direction.
		 *
		 * Eventually try each (last-to-first) until a non-blocked
		 * one is found (if any).
		 */
		if ((size = directions.size()) != 0) {
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
		if(get("zoneid").equals(DEFAULT_DEAD_AREA) && entity instanceof Player) {
			return false;
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
	 * @return	The away message, or <code>null</code> if unset.
	 */
	public String getAwayMessage() {
		return has("away") ? get("away") : null;
	}

	/**
	 * Check if another player should be notified that this player is
	 * away. This assumes the player has already been checked for away.
	 * Players will be reminded once an hour.
	 *
	 * @param	name		The name of the other player.
	 *
	 * @return	<code>true</code> if the player should be notified.
	 */
	public boolean isAwayNotifyNeeded(String name) {
		long now;
		Long lObj;

		now = System.currentTimeMillis();

		if ((lObj = awayReplies.get(name)) != null) {
			/*
			 * Only notify once an hour
			 */
			if ((now - lObj.longValue()) < (1000L * 60L * 60L)) {
				return false;
			}
		}

		awayReplies.put(name, new Long(now));
		return true;
	}

	/**
	 * Clear out all recorded away respones.
	 */
	public void resetAwayReplies() {
		awayReplies.clear();
	}

	/**
	 * Give the player some karma (good or bad).
	 *
	 * @param	karma		An amount of karma to add/subtract.
	 */
	@Override
	public void addKarma(double karma) {
		this.karma += karma;

		put("karma", karma);
	}

	/**
	 * Get the current amount of karma.
	 *
	 * @return	The current amount of karma.
	 *
	 * @see-also	#addKarma()
	 */
	@Override
	public double getKarma() {
		return karma;
	}

	/**
	 * Use some of the player's karma. A positive value indicates
	 * good luck/energy. A negative value indicates bad luck/energy.
	 * A value of zero should cause no change on an action or outcome.
	 *
	 * @param	scale		A positive number.
	 *
	 * @return	A number between -scale and scale.
	 */
	@Override
	public double useKarma(double scale) {
		return useKarma(-scale, scale);
	}

	/**
	 * Use some of the player's karma. A positive value indicates
	 * good luck/energy. A negative value indicates bad luck/energy.
	 * A value of zero should cause no change on an action or outcome.
	 * The granularity is <code>0.01</code> (%1 unit).
	 *
	 * @param	negLimit	The lowest negative value returned.
	 * @param	posLimit	The highest positive value returned.
	 *
	 * @return	A number within negLimit &lt;= 0 &lt;= posLimit.
	 */
	@Override
	public double useKarma(double negLimit, double posLimit) {
		return useKarma(negLimit, posLimit, 0.01);
	}

	/**
	 * Use some of the player's karma. A positive value indicates
	 * good luck/energy. A negative value indicates bad luck/energy.
	 * A value of zero should cause no change on an action or outcome.
	 *
	 * @param	negLimit	The lowest negative value returned.
	 * @param	posLimit	The highest positive value returned.
	 * @param	granularity	The amount that any extracted
	 *				karma is a multiple of.
	 *
	 * @return	A number within negLimit &lt;= 0 &lt;= posLimit.
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
		score = (0.2 + (karmaRand.nextDouble() * 0.8)) * limit;

		/*
		 * Clip to grandularity
		 */
		score = ((int) (score / granularity)) * granularity;
		
		/*
		 *with a lucky charm you use up less karma to be just as lucky
		 */

		if (this.isEquipped("lucky_charm")){
		    karma -= 0.5*score;
		}
		else {
		    karma -= score;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("karma given: " + score);
		}

		put("karma", karma);

		return score;
	}

	/**
	 * Process changes that to the object attributes. This may be called
	 * several times (unfortunetly) due to the requirements of the class's
	 * contructor, sometimes before prereqs are initialized.
	 */
	@Override
	public void update() throws AttributeNotFoundException {
		super.update();

		if(has("xp")) {
			// I want to force level to be updated.
			addXP(0);
		}

		if (has("age")) {
			age = getInt("age");
		}

		if (has("karma")) {
			karma = getDouble("karma");
		}

		if(features != null) {
			if(has("features")) {
				features.decode(get("features"));
			} else {
				features.clear();
			}
		}
	}

	/**
	 * Add a player ignore entry.
	 *
	 * @param	name		The player name.
	 * @param	duration	The ignore duration (in minutes),
	 *				or <code>0</code> for infinite.
	 * @param	reply		The reply.
	 *
	 * @return	<code>true</code> if value changed, <code>false</code>
	 *		if there was a problem.
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
	 * @param	name		The player name.
	 *
	 * @return	The custom reply message (including an empty string),
	 *		or <code>null</code> if not ignoring.
	 */
	public String getIgnore(String name) {
		String info;
		int i;
		long expiration;

		if ((info = getKeyedSlot("!ignore", "_" + name)) == null) {
			/*
			 * Special "catch all" fallback
			 */
			if ((info = getKeyedSlot("!ignore", "_*")) == null) {
				return null;
			}
		}

		if ((i = info.indexOf(';')) == -1) {
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
	 * @param	name		The player name.
	 *
	 * @return	<code>true</code> if value changed, <code>false</code>
	 *		if there was a problem.
	 */
	public boolean removeIgnore(String name) {
		return setKeyedSlot("!ignore", "_" + name, null);
	}

	/**
	 * Get a named skills value.
	 *
	 * @param	key		The skill key.
	 *
	 * @return	The skill value, or <code>null</code> if not set.
	 */
	public String getSkill(String key) {
		return getKeyedSlot("skills", key);
	}

	/**
	 * Set a named skills value.
	 *
	 * @param	key		The skill key.
	 * @param	value		The skill value.
	 *
	 * @return	<code>true</code> if value changed, <code>false</code>
	 *		if there was a problem.
	 */
	public boolean setSkill(String key, String value) {
		return setKeyedSlot("skills", key, value);
	}

	/**
	 * Get a keyed string value on a named slot.
	 *
	 * @param	name		The slot name.
	 * @param	key		The value key.
	 *
	 * @return	The keyed value of the slot, or <code>null</code>
	 *		if not set.
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
	 * @param	name		The slot name.
	 * @param	key		The value key.
	 * @param	value		The value to assign (or remove if
	 *				<code>null</code>).
	 *
	 * @return	<code>true</code> if value changed, <code>false</code>
	 *		if there was a problem.
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
	 * @param	name		The feature mnemonic.
	 *
	 * @return	The feature value, or <code>null</code> is not-enabled.
	 */
	public String getFeature(String name) {
		return features.get(name);
	}


	/**
	 * Determine if a client feature is enabled.
	 *
	 * @param	name		The feature mnemonic.
	 *
	 * @return	<code>true</code> if the feature is enabled.
	 */
	public boolean hasFeature(String name) {
		return features.has(name);
	}


	/**
	 * Enable/disable a client feature.
	 *
	 * @param	name		The feature mnemonic.
	 * @param	enabled		Flag indicating if enabled.
	 */
	public void setFeature(String name, boolean enabled) {
		if(features.set(name, enabled)) {
			put("features", features.encode());
		}
	}


	/**
	 * Set/remove a client feature.
	 * <strong>NOTE: The names and values MUST NOT contain <code>=</code>
	 * (equals), or <code>:</code> (colon).
	 *
	 * @param	name		The feature mnemonic.
	 * @param	value		The feature value,
	 *				or <code>null</code> to disable.
	 */
	public void setFeature(String name, String value) {
		if(features.set(name, value)) {
			put("features", features.encode());
		}
	}


	/**
	 * Sends a message that only this player can read.
	 * @param text The message.
	 */
	@Override
	public void sendPrivateText(String text) {
		if (has("private_text")) {
			text = get("private_text") + "\r\n" + text;
		}
		put("private_text", text);
		StendhalRPRuleProcessor.get().removePlayerPrivateText(this);
	}

	/**
	 * Sets the name of the last player who privately talked to this player
	 * using the /tell command. It needs to be stored non-persistently
	 * so that /answer can be used.
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
	 * Returns the admin level of this user. See AdministrationAction.java for details.
	 *
	 * @return adminlevel
	 */
	public int getAdminLevel() {
		// normal user are adminlevel 0.
		if (!has("adminlevel")) {
			return 0;
		}
		return getInt("adminlevel");
	}

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y + 1, 1, 1);
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
		StendhalRPWorld world = StendhalRPWorld.get();
		put("dead", "");

		if (hasSheep()) {
			// We make the sheep ownerless so someone can use it
			try {
				getSheep().setOwner(null);
			} catch(RPObjectInvalidException ex) {
				logger.warn("INCOHERENCE: Player has sheep but sheep doesn't exists");
			}

			remove("sheep");
		}
		// TODO Sheep stuff changed above! Must Pet stuff change?
		if (hasPet()) {
			// We make the pet ownerless so someone can use it
			if (world.has(getPet())) {
				Pet pet = (Pet) world.get(getPet());
				pet.setOwner(null);
			} else {
				logger.warn("INCOHERENCE: Player has pet but pet doesn't exist");
			}
			remove("pet");
		}

		// We stop eating anything
		itemsToConsume.clear();
		poisonToConsume.clear();

		if (!(killer instanceof RaidCreature)) {
			Item emeraldRing = getFirstEquipped("emerald_ring");

			if (emeraldRing != null && emeraldRing.getInt("amount") > 0) {
				// Penalize: 1% less experience if wearing that ring
				setXP((int) (getXP() * 0.99));
				setATKXP((int) (getATKXP() * 0.99));
				setDEFXP((int) (getDEFXP() * 0.99));

				/*
				 * We broke now the emerald ring.
				 */
				emeraldRing.put("amount", 0);
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
		// After a tangle with the grim reaper, give some karma
		addKarma(200.0);
        // Penalize: Respawn on afterlive zone and
		StendhalRPZone zone = world.getZone(DEFAULT_DEAD_AREA);

		zone.placeObjectAtEntryPoint(this);
		StendhalRPAction.changeZone(this, zone);
	}

	@Override
	protected void dropItemsOn(Corpse corpse) {
		// drop at least 1 and at most 4 items
		int maxItemsToDrop = Rand.rand(4);

		for (String slotName : CARRYING_SLOTS) {
			if (! hasSlot(slotName)) {
				logger.error("CARRYING_SLOTS contains a slot that player " + getName() + " doesn't have.");
			} else {
				RPSlot slot = getSlot(slotName);

				// a list that will contain the objects that will
				// be dropped.
				List<RPObject> objects = new LinkedList<RPObject>();

				// get a random set of items to drop
				for (RPObject objectInSlot : slot) {
					if (maxItemsToDrop == 0) {
						break;
					}

					// don't drop special quest rewards as there is no way to get them again
					if (objectInSlot.has("bound")) {
						continue;
					}

					if (Rand.throwCoin() == 1) {
						objects.add(objectInSlot);
						maxItemsToDrop--;
					}
				}

				// now actually drop them
				for (RPObject object : objects) {
					if (object instanceof StackableItem) {
						StackableItem item = (StackableItem) object;

						// We won't drop the full quantity, but only a percentage.
						// Get a random percentage between 26 % and 75 % to drop
						double percentage = (Rand.rand(50) + 25) / 100.0;
						int quantityToDrop = (int) Math.round(item.getQuantity() * percentage);

						if (quantityToDrop > 0) {
							StackableItem itemToDrop = item.splitOff(quantityToDrop);
							corpse.add(itemToDrop);
						}
					} else if (object instanceof PassiveEntity) {
						slot.remove(object.getID());

						corpse.add((PassiveEntity) object);
						maxItemsToDrop--;
					}

					if (corpse.isFull()) {
						// This shouldn't happen because we have only chosen
						// 4 items, and corpses can handle this.
						return;
					}
				}
			}

			if (maxItemsToDrop == 0) {
				return;
			}
		}
	}

	public void removeSheep(Sheep sheep) {
		Log4J.startMethod(logger, "removeSheep");
		if (has("sheep")) {
			remove("sheep");
		} else {
			logger.warn("Called removeSheep but player has not sheep: " + this);
		}
		StendhalRPRuleProcessor.get().removeNPC(sheep);

		Log4J.finishMethod(logger, "removeSheep");
	}

	public void removePet(Pet pet) {
		Log4J.startMethod(logger, "removePet");
		if (has("pet")) {
			remove("pet");
		} else {
			logger.warn("Called removePet but player has not pet: " + this);
		}
		StendhalRPRuleProcessor.get().removeNPC(pet);

		Log4J.finishMethod(logger, "removePet");
	}
	public boolean hasSheep() {
		return has("sheep");
	}

	public boolean hasPet() {
		return has("pet");
	}
	public void setPet(Pet pet) {
		Log4J.startMethod(logger, "setPet");
		put("pet", pet.getID().getObjectID());

		StendhalRPRuleProcessor.get().addNPC(pet);

		Log4J.finishMethod(logger, "setPet");
	}
	public void setSheep(Sheep sheep) {
		Log4J.startMethod(logger, "setSheep");
		put("sheep", sheep.getID().getObjectID());

		StendhalRPRuleProcessor.get().addNPC(sheep);

		Log4J.finishMethod(logger, "setSheep");
	}

	private static  class AntidoteEater implements TurnListener {

		WeakReference<Player> ref;
		public AntidoteEater(Player player) {
			ref = new WeakReference<Player>(player);
		}

		public void onTurnReached(int currentTurn, String message) {
			if( ref.get()==null){
				return;
			}
			ref.get().isImmune=false;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null){
				return false;
			}
			if (obj instanceof AntidoteEater) {
				AntidoteEater other = (AntidoteEater) obj;
				return ref.get()== other.ref.get();

			}else{
				return false;
			}

		}

		@Override
		public int hashCode() {

			return ref.hashCode();
		}
	}

	public static class NoSheepException extends RuntimeException {

		private static final long serialVersionUID = -6689072547778842040L;

		public NoSheepException() {
			super();
		}
	}


	/**
	 * Get the player's sheep.
	 *
	 * @return	The sheep.
	 */
	public Sheep getSheep() {
		return (Sheep) StendhalRPWorld.get().get(new RPObject.ID(getInt("sheep"), get("zoneid")));
	}

	public static class NoPetException extends RuntimeException {
	    //does this need to be a different number?
		private static final long serialVersionUID = -6689072547778842040L;

		public NoPetException() {
			super();
		}

		public NoPetException(String except) {
		    super(except);
		}
	}

	public RPObject.ID getPet() throws NoPetException {
		return new RPObject.ID(getInt("pet"), get("zoneid"));
	}

	/**
	 * Gets the number of minutes that this player has been logged in on the
	 * server.
	 */
	public int getAge() {
		return age;
	}

	/**
	 * Sets the number of minutes that this player has been logged in on the
	 * server.
	 * @param age minutes
	 */
	public void setAge(int age) {
		this.age = age;
		put("age", age);
		TutorialNotifier.aged(this, age);
	}

	/**
	 * Notifies this player that the given player has logged in.
	 * @param who The name of the player who has logged in.
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
	 * @param who The name of the player who has logged out.
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
	 * @param name The quest's name
	 * @return true iff the quest has been completed by the player
	 */
	public boolean isQuestCompleted(String name) {
		String	info;


		if((info = getKeyedSlot("!quests", name)) == null) {
			return false;
		}

		return info.equals("done");
	}

	/**
	 * Checks whether the player has made any progress in the given
	 * quest or not. For many quests, this is true right after the quest
	 * has been started.
	 * @param name The quest's name
	 * @return true iff the player has made any progress in the quest
	 */
	public boolean hasQuest(String name) {
		return (getKeyedSlot("!quests", name) != null);
	}

	/**
	 * Gets the player's current status in the given quest.
	 * @param name The quest's name
	 * @return the player's status in the quest
	 */
	public String getQuest(String name) {
		return getKeyedSlot("!quests", name);
	}

	/**
	 * Allows to store the player's current status in a quest in a string.
	 * This string may, for instance, be "started", "done", a semicolon-
	 * separated list of items that need to be brought/NPCs that need to be
	 * met, or the number of items that still need to be brought. Note that
	 * the string "done" has a special meaning: see isQuestComplete().
	 * @param name The quest's name
	 * @param status the player's status in the quest. Set it to null to
	 *        completely reset the player's status for the quest.
	 */
	public void setQuest(String name, String status) {
		String oldStatus = getKeyedSlot("!quests", name);
		setKeyedSlot("!quests", name, status);
		if (status == null || !status.equals(oldStatus)) {
			StendhalRPRuleProcessor.get().addGameEvent(this.getName(), "quest", name, status);
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
	 * @param name   quest
	 * @param states valid states
	 * @return true, if the quest is in one of theses states, false otherwise
	 */
	public boolean isQuestInState(String name, String... states) {
		String	questState;


		if((questState = getQuest(name)) != null) {
			for (String state : states) {
				if(questState.equals(state)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks if the player has ever killed a creature with the given name
	 * without the help of any other player.
	 * @param The name of the creature to check.
	 * @return true iff this player has ever killed this creature on his own.
	 */
	public boolean hasKilledSolo(String name) {
		String	info;

		if((info = getKeyedSlot("!kills", name)) == null) {
			return false;
		}
		return info.equals("solo");
	}

	/**
	 * Checks if the player has ever killed a creature, with or without the
	 * help of any other player.
	 * @param The name of the creature to check.
	 * @return true iff this player has ever killed this creature on his own.
	 */
	public boolean hasKilled(String name) {
		return (getKeyedSlot("!kills", name) != null);
	}

	/**
	 * Checks in which way this player has killed the creature with the given
	 * name.
	 * @param The name of the creature to check.
	 * @return either "solo", "shared", or null.
	 */
	public String getKill(String name) {
		return getKeyedSlot("!kills", name);
	}

	/**
	 * Stores in which way the player has killed a creature with the given
	 * name.
	 *
	 * This should not be called with mode "shared" if this player has already
	 * killed the creature solo.
	 *
	 * @param The name of the killed creature.
	 * @param mode either "solo", "shared", or null.
	 */
	public void setKill(String name, String mode) {
		setKeyedSlot("!kills", name, mode);
	}

	/**
	 * ???
	 * @return ???
	 */
	public List<String> getKills() {
		RPSlot slot = getSlot("!kills");
		RPObject kills = slot.iterator().next();
		List<String> killsList = new LinkedList<String>();
		for (String k : kills) {
			if (!k.equals("id") && !k.equals("zoneid")) {
				killsList.add(k);
			}
		}
		return killsList;
	}

	/**
	 * Makes the game think that this player has never killed a creature
	 * with the given name. Use this for quests where the player should
	 * kill a creature of a specific type.
	 * @param name The name of the creature.
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
	 * Poisons the player with a poisonous item.
	 * Note that this method is also used when a player has been poisoned
	 * while fighting against a poisonous creature.
	 * @param item the poisonous item
	 * @return true iff the poisoning was effective, i.e. iff the player is
	 *         not immune
	 */
	public boolean poison(ConsumableItem item) {
		if (isImmune) {
			return false;
		} else {
			// put("poisoned", "0");
			poisonToConsume.add(item);
			return true;
		}
	}

	public boolean isFull(){
		return itemsToConsume.size() > 5 ;
	}
	public void consumeItem(ConsumableItem item) {
		if (item.getQuantity()>1){
			throw new IllegalArgumentException("consumeItem can only take one item at a time");
		}
		
		if ((item.getRegen() > 0) && isFull() && !item.getName().contains("potion")) {
			sendPrivateText("You can't consume anymore");
			return;
		}


		logger.debug("Consuming item: " + item.getAmount());
		if (item.getRegen() > 0) {
			put("eating", 0);
			itemsToConsume.add(item);
		} else if (item.getRegen() == 0) { // if regen==0, it's an antidote
			poisonToConsume.clear();
			isImmune = true;
			// set a timer to remove the immunity effect after some time
			TurnNotifier notifier = TurnNotifier.get();
			// first remove all effects from previously used immunities to
			// restart the timer
			TurnListener tl = new AntidoteEater(this);
			notifier.dontNotify(tl);
			notifier.notifyInTurns(item.getAmount(), tl);
		} else if (!isImmune) {
			// Player was poisoned and is currently not immune
			poisonToConsume.add(item);
		} else {
			// Player was poisoned, but antidote saved it.
		}

		Collections.sort(itemsToConsume);
		
	}

	public void consume(int turn) {
		


		if ( (poisonToConsume.size() == 0)) {
			if (has("poisoned")){
		
			remove("poisoned");
			
			}
		}else{
			int sum = 0;
			int amount=0;
			for (Iterator<ConsumableItem> it = poisonToConsume.iterator();it.hasNext();){
				ConsumableItem poison = it.next();
				if (turn%poison.getFrecuency() ==0){
					if (poison.consumed()){
						it.remove();
						
					}else{
						amount = poison.consume();
						damage(-amount, poison);
						sum += amount;
						put("poisoned", sum);
						
					}
				}
			
			}
		}
		
		Collections.sort(itemsToConsume);
		if (itemsToConsume.size()>0){
			ConsumableItem food = itemsToConsume.get(0);
			if (food.consumed()){
				itemsToConsume.remove(0);
			}else{
				if(turn % food.getFrecuency()==0){
					logger.debug("Consumed item: " + food);
					 int amount = food.consume();
					put("eating", amount);
					if(heal(amount, true) == 0) {
					itemsToConsume.clear();
					}
	
				
				}
			}
			
		}else{

			if (has("eating")) {
				remove("eating");
				
			}
		}
		notifyWorldAboutChanges();
	}

	// TODO: use the turn notifier for consumable items to get rid of Player.consume().

	@Override
	public String describe() {

		// A special description was specified
		if (hasDescription()) {
			return (getDescription());
		}

		// default description for player includes there name, level and play time
		int hours = age / 60;
		int minutes = age % 60;
		String time = hours + " hours and " + minutes + " minutes";
		String text = "You see " + getName() + ".\n" + getName() + " is level " + getLevel() + " and has been playing "
		        + time + ".";
		return (text);
	}

	/**
	 * Teleports this player to the given destination.
	 * @param zone The zone where this player should be teleported to.
	 * @param x The destination's x coordinate
	 * @param y The destination's y coordinate
	 * @param dir The direction in which the player should look after
	 *            teleporting, or null if the direction shouldn't change
	 * @param teleporter The player who initiated the teleporting, or null
	 *                   if no player is responsible. This is only to give
	 *                   feedback if something goes wrong. If no feedback is
	 *                   wanted, use null.
	 * @return true iff teleporting was successful
	 */
	public boolean teleport(StendhalRPZone zone, int x, int y, Direction dir, Player teleporter) {
		if (StendhalRPAction.placeat(zone, this, x, y)) {
			StendhalRPAction.changeZone(this, zone);
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
	  * Removes all units of an item from the RPEntity. The item can
	  * either be stackable or non-stackable. If the RPEntity doesn't
	  * have any of the item, doesn't remove anything.
	  * @param name The name of the item
	  * @return true iff dropping the item was successful.
	  */
	public boolean dropAll(String name) {
		return drop(name, getNumberOfEquipped(name));
	}

	private int pushCounter=0;

	/**
	 * Called when player push entity.
	 * The entity displacement is handled by the action itself.
	 * @param entity
	 */
	public void onPush(RPEntity entity) {
		pushCounter=StendhalRPRuleProcessor.get().getTurn();
	}

	/**
	 * Return true if player can push entity.
	 * @param entity
	 * @return
	 */
	public boolean canPush(RPEntity entity) {
		return (this!=entity && StendhalRPRuleProcessor.get().getTurn()-pushCounter>10);
	}

	@Override
	public void setOutfit(Outfit outfit) {
		setOutfit(outfit, false);
	}

	/**
	 * Makes this player wear the given outfit. If the given outfit contains
	 * null parts, the current outfit will be kept for these parts.
	 * @param outfit The new outfit.
	 * @param temporary If true, the original outfit will be stored so that
	 *        it can be restored later.
	 */
	public void setOutfit(Outfit outfit, boolean temporary) {
		// if the new outfit is temporary and the player is not wearing
		// a temporary outfit already, store the current outfit in a
		// second slot so that we can return to it later.
		if (temporary && !has("outfit_org")) {
			put("outfit_org", get("outfit"));
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
	 * Tries to give the player his original outfit back after he has put on
	 * a temporary outfit.
	 * This will only be successful if the original outfit has been stored.
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
	// RPEntity
	//

	/**
	 * Get the normal movement speed.
	 *
	 * @return	The normal speed when moving.
	 */
	@Override
	public double getBaseSpeed() {
		return BASE_SPEED;
	}


	//
	// ActiveEntity
	//

	/**
	 * Determine if zone changes are currently allowed via normal means
	 * (non-portal teleportation doesn't count).
	 *
	 * @return	<code>true</code> if the entity can change zones.
	 */
	@Override
	protected boolean isZoneChangeAllowed() {
		/*
		 * If we are too far from our sheep, then disallow zone change
		 */
		if (hasSheep()) {
			Sheep sheep = getSheep();

			if(squaredDistance(sheep) > (7 * 7)) {
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

		// 1 round = 5 turns
		if (isAttacking() && ((turn % StendhalRPAction.getAttackRate(this)) == 0)) {
			StendhalRPAction.attack(this, getAttackTarget());
		}

		if ((turn % 180) == 0) {
			setAge(getAge() + 1);
			notifyWorldAboutChanges();
		}

		consume(turn);
	}


	/**
	 * Called when this object is added to a zone.
	 *
	 * @param	zone		The zone this was added to.
	 */
	@Override
	public void onAdded(StendhalRPZone zone) {
		super.onAdded(zone);


		String zoneName = zone.getID().getID();

		/*
		 * If player enters afterlife, make them partially transparent
		 */
		if(zoneName.equals(DEFAULT_DEAD_AREA)) {
			setVisibility(50);
		}

		/*
		 * Remember zones we've been in
		 */
		setKeyedSlot("!visited", zoneName, Long.toString(System.currentTimeMillis()));
	}


	/**
	 * Called when this object is being removed from a zone.
	 *
	 * @param	zone		The zone this will be removed from.
	 */
	@Override
	public void onRemoved(StendhalRPZone zone) {
		/*
		 * If player leaves afterlife, make them normal
		 */
		if(zone.getID().getID().equals(DEFAULT_DEAD_AREA)) {
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
		if (this == obj) return true;
		if (getClass() != obj.getClass()) return false;
		Player other = (Player) obj;
		return this.getName().equals(other.getName());
	}

	@Override
	public String toString() {
		return "Player [" + getName() + ", " + hashCode() + "]";
	}
}
