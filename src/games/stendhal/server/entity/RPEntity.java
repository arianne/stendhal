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
package games.stendhal.server.entity;

import games.stendhal.common.Level;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.rule.ActionManager;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.game.Statistics;

import org.apache.log4j.Logger;

public abstract class RPEntity extends Entity {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(RPEntity.class);

	protected static Statistics stats;

	private String name;

	private int atk;

	private int atk_xp;

	private int def;

	private int def_xp;

	private int base_hp;

	private int hp;

	private int xp;

	private int level;

	private int mana;

	private int base_mana;

	private HashMap<RPEntity, Integer> blood = new HashMap<RPEntity, Integer>();

	/** List of all attackers of this entity */
	private List<Entity> attackSource;

	/** current target */
	private RPEntity attackTarget;

	private Map<Entity, Integer> damageReceived;

	/** list of players which are to reward with xp on killing this creature */
	protected Set<Player> playersToReward;

	private int totalDamageReceived;

	/** the path */
	private List<Path.Node> path;

	/** current position in the path */
	private int pathPosition;

	/** true if the path is a loop */
	private boolean pathLoop;

	private static int TURNS_WHILE_ATK_DEF_XP_INCREASE = 40;

	/**
	 * All the slots considered to be "with" the entity.
	 * Listed in priority order (ie. bag first).
	 */
	public static final String[] CARRYING_SLOTS = { "bag", "head", "rhand", "lhand", "armor", "cloak", "legs", "feet" };

	public static void generateRPClass() {
		stats = Statistics.getStatistics();

		try {
			RPClass entity = new RPClass("rpentity");
			entity.isA("entity");
			entity.add("name", RPClass.STRING);
			entity.add("level", RPClass.SHORT);
			entity.add("xp", RPClass.INT);
			entity.add("mana", RPClass.INT);
			entity.add("base_mana", RPClass.INT);

			entity.add("hp/base_hp", RPClass.FLOAT, RPClass.VOLATILE);
			entity.add("base_hp", RPClass.SHORT, RPClass.PRIVATE);
			entity.add("hp", RPClass.SHORT, RPClass.PRIVATE);

			entity.add("atk", RPClass.SHORT, RPClass.PRIVATE);
			entity.add("atk_xp", RPClass.INT, RPClass.PRIVATE);
			entity.add("def", RPClass.SHORT, RPClass.PRIVATE);
			entity.add("def_xp", RPClass.INT, RPClass.PRIVATE);
			entity.add("atk_item", RPClass.INT, (byte) (RPClass.PRIVATE | RPClass.VOLATILE));
			entity.add("def_item", RPClass.INT, (byte) (RPClass.PRIVATE | RPClass.VOLATILE));

			entity.add("risk", RPClass.BYTE, RPClass.VOLATILE);
			entity.add("damage", RPClass.INT, RPClass.VOLATILE);
			entity.add("heal", RPClass.INT, RPClass.VOLATILE);
			entity.add("target", RPClass.INT, RPClass.VOLATILE);
			entity.add("title_type", RPClass.STRING, RPClass.VOLATILE);

			entity.addRPSlot("head", 1, RPClass.PRIVATE);
			entity.addRPSlot("rhand", 1, RPClass.PRIVATE);
			entity.addRPSlot("lhand", 1, RPClass.PRIVATE);
			entity.addRPSlot("armor", 1, RPClass.PRIVATE);
			entity.addRPSlot("cloak", 1, RPClass.PRIVATE);
			entity.addRPSlot("legs", 1, RPClass.PRIVATE);
			entity.addRPSlot("feet", 1, RPClass.PRIVATE);
			entity.addRPSlot("bag", 12, RPClass.PRIVATE);
		} catch (RPClass.SyntaxException e) {
			logger.error("cannot generateRPClass", e);
		}
	}

	public RPEntity(RPObject object) throws AttributeNotFoundException {
		super(object);
		attackSource = new LinkedList<Entity>();
		damageReceived = new HashMap<Entity, Integer>();
		playersToReward = new HashSet<Player>();
		totalDamageReceived = 0;
	}

	public RPEntity() throws AttributeNotFoundException {
		super();
		attackSource = new LinkedList<Entity>();
		damageReceived = new HashMap<Entity, Integer>();
		playersToReward = new HashSet<Player>();
		totalDamageReceived = 0;
	}

	/**
	 * Give the player some karma (good or bad).
	 *
	 * @param	karma		An amount of karma to add/subtract.
	 */
	public void addKarma(double karma) {
		// No nothing
	}

	/**
	 * Get some of the player's karma. A positive value indicates
	 * good luck/energy. A negative value indicates bad luck/energy.
	 * A value of zero should cause no change on an action or outcome.
	 *
	 * @param	scale		A positive number.
	 *
	 * @return	A number between -scale and scale.
	 */
	public double getKarma(double scale) {
		// No impact
		return 0.0;
	}

	/**
	 * Get some of the player's karma. A positive value indicates
	 * good luck/energy. A negative value indicates bad luck/energy.
	 * A value of zero should cause no change on an action or outcome.
	 *
	 * @param	negLimit	The lowest negative value returned.
	 * @param	posLimit	The highest positive value returned.
	 *
	 * @return	A number within negLimit &lt;= 0 &lt;= posLimit.
	 */
	public double getKarma(double negLimit, double posLimit) {
		// No impact
		return 0.0;
	}

	/**
	 * Determine if this is an obstacle for another entity.
	 *
	 * @param	entity		The entity to check against.
	 *
	 * @return	<code>true</code> if the other entity is an RPEntity.
	 */
	public boolean isObstacle(Entity entity) {
		if (isGhost()) {
			return false;
		}

		return (entity instanceof RPEntity);
	}

	@Override
	public void update() throws AttributeNotFoundException {
		super.update();

		if (has("name")) {
			name = get("name");
		}

		if (has("atk")) {
			atk = getInt("atk");
		}
		if (has("atk_xp")) {
			atk_xp = getInt("atk_xp");
		}

		if (has("def")) {
			def = getInt("def");
		}
		if (has("def_xp")) {
			def_xp = getInt("def_xp");
		}

		if (has("base_hp")) {
			base_hp = getInt("base_hp");
		}
		if (has("hp")) {
			hp = getInt("hp");
		}

		if (has("xp")) {
			xp = getInt("xp");
		}
		if (has("level")) {
			level = getInt("level");
		}
		if (has("mana")) {
			mana = getInt("mana");
		}
		if (has("base_mana")) {
			mana = getInt("base_mana");
		}

		if (base_hp != 0) {
			put("hp/base_hp", (double) hp / (double) base_hp);
		} else {
			put("hp/base_hp", 1);
		}
	}

	public void setName(String name) {
		this.name = name;
		put("name", name);
	}

	public String getName() {
		return name;
	}

	public void setLevel(int level) {
		this.level = level;
		put("level", level);
	}

	public int getLevel() {
		return level;
	}

	public void setATK(int atk) {
		this.atk = atk;
		put("atk", atk);
	}

	public int getATK() {
		return atk;
	}

	public void setATKXP(int atk) {
		this.atk_xp = atk;
		put("atk_xp", atk_xp);
		incATKXP();
	}

	public int getATKXP() {
		return atk_xp;
	}

	public int incATKXP() {
		this.atk_xp++;
		put("atk_xp", atk_xp);

		int newLevel = Level.getLevel(atk_xp);
		int levels = newLevel - (getATK() - 10);

		// In case we level up several levels at a single time.
		for (int i = 0; i < Math.abs(levels); i++) {
			setATK(this.atk + (int) Math.signum(levels) * 1);
			StendhalRPRuleProcessor.get().addGameEvent(getName(), "atk", Integer.toString(getATK()));
		}

		return atk_xp;
	}

	public void setDEF(int def) {
		this.def = def;
		put("def", def);
	}

	public int getDEF() {
		return def;
	}

	public void setDEFXP(int def) {
		this.def_xp = def;
		put("def_xp", def_xp);
		incDEFXP();
	}

	public int getDEFXP() {
		return def_xp;
	}

	public int incDEFXP() {
		this.def_xp++;
		put("def_xp", def_xp);

		int newLevel = Level.getLevel(def_xp);
		int levels = newLevel - (getDEF() - 10);

		// In case we level up several levels at a single time.
		for (int i = 0; i < Math.abs(levels); i++) {
			setDEF(this.def + (int) Math.signum(levels) * 1);
			StendhalRPRuleProcessor.get().addGameEvent(getName(), "def", Integer.toString(getDEF()));
		}

		return def_xp;
	}

	public void initHP(int hp) {
		setBaseHP(hp);
		setHP(hp);
	}

	public void setBaseHP(int newhp) {
		this.base_hp = newhp;
		put("base_hp", newhp);

		if (base_hp != 0) {
			put("hp/base_hp", (double) hp / (double) base_hp);
		} else {
			put("hp/base_hp", 1);
		}
	}

	public int getBaseHP() {
		return base_hp;
	}

	public void setHP(int hp) {
		this.hp = hp;
		put("hp", hp);

		if (base_hp != 0) {
			put("hp/base_hp", (double) hp / (double) base_hp);
		} else {
			put("hp/base_hp", 1);
		}
	}

	public int getHP() {
		return hp;
	}
	

	/**
	 * Gets the mana (magic)
	 *
	 * @return mana
	 */
	public int getMana() {
		return mana;
	}

	/** 
	 * Gets the base mana (like base_hp)
	 *
	 * @return base mana
	 */
	public int getBaseMana() {
		return base_mana;
	}

	/**
	 * sets the available mana
	 *
	 * @param newMana new amount of mana
	 */
	public void setMana(int newMana) {
		mana = newMana;
		put("mana", newMana);
	}

	/**
	 * Sets the base mana (like base_hp)
	 *
	 * @param newBaseMana new amount of base mana
	 */
	public void setBaseMana(int newBaseMana) {
		base_mana = newBaseMana;
		put("base_mana", newBaseMana);
	}

	public void setXP(int newxp) {
		this.xp = newxp;
		put("xp", xp);
	}

	public void subXP(int newxp) {
		addXP(-newxp);
	}

	public void addXP(int newxp) {
		// Increment experience points
		this.xp += newxp;
		put("xp", xp);

		StendhalRPRuleProcessor.get().addGameEvent(getName(), "added xp", Integer.toString(newxp));
		StendhalRPRuleProcessor.get().addGameEvent(getName(), "xp", Integer.toString(xp));

		int newLevel = Level.getLevel(getXP());
		int levels = newLevel - getLevel();

		// In case we level up several levels at a single time.
		for (int i = 0; i < Math.abs(levels); i++) {
			setBaseHP(getBaseHP() + (int) Math.signum(levels) * 10);
			setHP(getHP() + (int) Math.signum(levels) * 10);

			setLevel(newLevel);
		}
	}

	public int getXP() {
		return xp;
	}

	public void sendPrivateText(String text) {
		// Ignore - Sub-classes may use
	}

	/***************************************************************************
	 * * Attack handling code. * *
	 **************************************************************************/

	/** Modify the entity to order to attack the target entity */
	public void attack(RPEntity target) {
		put("target", target.getID().getObjectID());
		attackTarget = target;
	}

	/** Modify the entity to stop attacking */
	public void stopAttack() {
		if (has("risk")) {
			remove("risk");
		}
		if (has("damage")) {
			remove("damage");
		}
		if (has("heal")) {
			remove("heal");
		}
		if (has("target")) {
			remove("target");
		}

		if (attackTarget != null) {
			// Whould doing this in a call to
			// attackTarget.onAttack(this, false) be better?
			attackTarget.attackSource.remove(this);

			// XXX - Trying to remove a List????
			blood.remove(attackTarget.attackSource);

			// XXX - Opponent could attack again, really remove?
			blood.remove(attackTarget);

			attackTarget = null;
		}
	}

	/**
	 * this entity was hurt
	 *
	 * @param source the entity which caused damage
	 */
	public void bloodHappens(RPEntity source) {
		blood.put(source, new Integer(TURNS_WHILE_ATK_DEF_XP_INCREASE));
	}

	/**
	 * keeps track of the number of turns since the last damage
	 *
	 * @param enemy the enemy which may have caused previous damage
	 * @return true, if the last damage is still recent enough, false otherwise
	 */
	public boolean stillHasBlood(RPEntity enemy) {
		Integer integer = blood.get(enemy);
		if (integer != null) {
			int i = integer.intValue();
			if (i > 0) {
				i--;
				blood.put(enemy, new Integer(i));
				return true;
			} else {
				blood.remove(enemy);
				return false;
			}
		}
		return false;
	}

	/**
	 * This method is called on each round when this entity has been attacked by
	 * RPEntity who and status is true to means keep attacking and false mean
	 * stop attacking.
	 */
	public void onAttack(Entity who, boolean status) {
		if (status) {
			// Attacker should manage their own target
			//			who.attackTarget = this;
			if (!attackSource.contains(who)) {
				attackSource.add(who);
			}
		} else {
			if (who.has("target")) {
				who.remove("target");
			}
			// Attacker should manage their own target
			//			who.attackTarget = null;
			attackSource.remove(who);
		}
	}

	/**
	 * This method is called when this entity has been attacked by Entity
	 * who and it has been damaged with damage points.
	 */
	public void onDamage(Entity who, int damage) {
		logger.debug("Damaged " + damage + " points by " + who.getID());

		StendhalRPRuleProcessor.get().addGameEvent(who.getName(), "damaged", getName(), Integer.toString(damage));

		Rectangle2D rect = getArea();
		if (!StendhalRPRuleProcessor.get().bloodAt((int) rect.getX(), (int) rect.getY())) {
			Blood blood = new Blood(this);
			IRPZone zone = getZone();
			zone.assignRPObjectID(blood);
			zone.add(blood);
			StendhalRPRuleProcessor.get().addBlood(blood);
		}

		int leftHP = getHP() - damage;

		totalDamageReceived += damage;

		if (damageReceived.containsKey(who)) {
			damageReceived.put(who, damage + damageReceived.get(who));
		} else {
			damageReceived.put(who, damage);
		}
		addPlayersToReward(who);

		if (leftHP > 0) {
			setHP(leftHP);
		} else {
			kill(who);
		}

		notifyWorldAboutChanges();
	}

	/**
	 * Manages a list of players to reward XP in case this creature is killed.
	 *
	 * @param player Player
	 */
	protected void addPlayersToReward(Entity player) {
		if (player instanceof Player) {
			playersToReward.add((Player) player);
		}
	}

	/**
	 * Kills this RPEntity.
	 * @param killer The killer
	 */
	protected void kill(Entity killer) {
		setHP(0);
		StendhalRPRuleProcessor.get().killRPEntity(this, killer);
	}

	/**
	 * This method is called when the entity has been killed ( hp==0 ).
	 * 
	 * @param killer
	 *            The entity who caused the death
	 */
	public void onDead(Entity killer) {
		onDead(killer, true);
	}

	/**
	 * This method is called when the entity has been killed ( hp==0 ). For
	 * almost everything remove is true and the creature is removed from the
	 * world, except for the players...
	 * 
	 * @param killer
	 *            The entity who caused the death
	 */
	protected void onDead(Entity killer, boolean remove) {
		stopAttack();
		int oldlevel = this.getLevel();
		int oldxp = this.getXP();

		if (killer instanceof RPEntity) {
			((RPEntity) killer).stopAttack();
			StendhalRPRuleProcessor.get().addGameEvent(killer.getName(), "killed", getName());
			killer.notifyWorldAboutChanges();
		}
		if (this instanceof Player) {
			this.setXP((oldxp * 10) / 9);
			oldlevel = this.getLevel();
			oldxp = this.getXP();
			this.setXP((int) (oldxp * 0.9));
		}

		// Establish how much xp points your are rewarded
		if (oldxp > 0) {
			int xpReward = (int) (oldxp * 0.05);

			// for everyone who helped killing this RPEntity:
			for (Player player : playersToReward) {
				Integer temp = damageReceived.get(player);
				if (temp == null) {
					continue;
				}
				int damageDone = temp.intValue();

				if (logger.isDebugEnabled()) {
					String name = player.has("name") ? player.get("name") : player.get("type");

					logger.debug(name + " did " + damageDone + " of " + totalDamageReceived + ". Reward was "
					        + xpReward);
				}

				int xpEarn = (xpReward * damageDone) / totalDamageReceived;

				/** We limit xp gain for up to eight levels difference */
				double gainXpLimitation = 1 + ((oldlevel - player.getLevel()) / (20.0));
				if (gainXpLimitation < 0.0) {
					gainXpLimitation = 0.0;
				} else if (gainXpLimitation > 1.0) {
					gainXpLimitation = 1.0;
				}

				logger.debug("OnDead: " + xpReward + "\t" + damageDone + "\t" + totalDamageReceived + "\t"
				        + gainXpLimitation);

				int reward = (int) (xpEarn * gainXpLimitation);

				// We ensure that the player gets at least 1 experience
				// point, because getting nothing lowers motivation.
				if (reward == 0) {
					reward = 1;
				}

				player.addXP(reward);

				// find out if the player killed this RPEntity on his own
				// TODO: don't overwrite solo with shared.
				if (damageDone == totalDamageReceived) {
					player.setKill(getName(), "solo");
				} else if (!player.hasKilledSolo(getName())) {
					player.setKill(getName(), "shared");
				}
				player.notifyWorldAboutChanges();
			}
		}

		damageReceived.clear();
		playersToReward.clear();
		totalDamageReceived = 0;

		// Stats about dead
		if (has("name")) {
			stats.add("Killed " + get("name"), 1);
		} else {
			stats.add("Killed " + get("type"), 1);
		}

		// Add a corpse
		Corpse corpse = new Corpse(this, killer);

		// Add some reward inside the corpse
		dropItemsOn(corpse);
		updateItemAtkDef();

		IRPZone zone = getZone();
		zone.assignRPObjectID(corpse);
		zone.add(corpse);

		if (remove) {
			StendhalRPWorld.get().remove(getID());
		}
	}

	abstract protected void dropItemsOn(Corpse corpse);

	/** Return true if this entity is attacked */
	public boolean isAttacked() {
		return !attackSource.isEmpty();
	}

	/** Return the Entities that are attacking this character */
	public List<Entity> getAttackSources() {
		return attackSource;
	}

	/** Return the RPEntities that are attacking this character */
	public List<RPEntity> getAttackingRPEntities() {
		List<RPEntity> list = new ArrayList<RPEntity>();

		for (Entity entity : getAttackSources()) {
			if (entity instanceof RPEntity) {
				list.add((RPEntity) entity);
			}
		}

		return list;
	}

	/** Return true if this entity is attacking */
	public boolean isAttacking() {
		return attackTarget != null;
	}

	/** Return the RPEntity that this entity is attacking. */
	public RPEntity getAttackTarget() {
		return attackTarget;
	}

	/***************************************************************************
	 * * Path handling code. * *
	 **************************************************************************/

	/**
	 * Set a path to follow for this entity. A previos path is cleared and the
	 * entity starts at the first node (so the first node should be its
	 * position, of course)
	 * 
	 * @param path
	 *            list of connected nodes
	 * @param cycle
	 *            true, the entity will resume at the start of the path when
	 *            finished; false, it will stop at the last node (and clear the
	 *            path)
	 */
	public void setPath(List<Path.Node> path, boolean cycle) {
		this.path = path;
		this.pathPosition = 0;
		this.pathLoop = cycle;
	}

	/**
	 * Adds some nodes to the path to follow for this entity. The current
	 * path-position is kept.
	 */
	public void addToPath(List<Path.Node> pathNodes) {
		if (path == null) {
			path = new ArrayList<Path.Node>();
		}
		path.addAll(pathNodes);
	}

	/**
	 * Sets the loop-flag of the path. Note that the path should be closed.
	 */
	public void setPathLoop(boolean loop) {
		this.pathLoop = loop;
	}

	public void clearPath() {
		this.path = null;
		this.pathPosition = 0;
		this.pathLoop = false;
	}

	public boolean hasPath() {
		return path != null;
	}

	public List<Path.Node> getPath() {
		return path;
	}

	public boolean isPathLoop() {
		return pathLoop;
	}

	public int getPathPosition() {
		return pathPosition;
	}

	public boolean pathCompleted() {
		return (path != null) && (pathPosition == path.size() - 1);
	}

	public void setPathPosition(int pathPos) {
		this.pathPosition = pathPos;
	}

	/***************************************************************************
	 * * Equipment handling. * *
	 **************************************************************************/

	/**
	 * Tries to equip an item in the appropriate slot.
	 *
	 * @param item the item
	 * @return true if the item can be equipped, else false
	 */
	public boolean equip(Item item) {
		return equip(item, false);
	}

	/**
	 * Tries to equip an item in the appropriate slot.
	 *
	 * @param item the item
	 * @param putOnGroundIfItCannotEquiped put it on ground if it cannot equiped.
	 * @return true if the item can be equipped, else false
	 */
	public boolean equip(Item item, boolean putOnGroundIfItCannotEquiped) {
		ActionManager manager = StendhalRPWorld.get().getRuleManager().getActionManager();

		String slot = manager.canEquip(this, item);
		if (slot != null) {
			return manager.onEquip(this, slot, item);
		}

		if (putOnGroundIfItCannotEquiped) {
			StendhalRPZone zone = getZone();
			zone.assignRPObjectID(item);
			item.setX(getX());
			item.setY(getY() + 1);
			zone.add(item);
			return true;
		}

		// we cannot equip this item
		return false;
	}

	/**
	 * Tries to equip one unit of an item in the given slot.
	 * @param slotName the name of the slot
	 * @param item the item
	 * @return true if the item can be equipped, else false
	 */
	public boolean equip(String slotName, Item item) {
		if (hasSlot(slotName)) {
			RPSlot slot = getSlot(slotName);
			if (slot.isFull()) {
				slot.add(item);
				updateItemAtkDef();
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes a specific amount of an item from the RPEntity. The item can
	 * either be stackable or non-stackable. The units can be distributed
	 * over different slots. If the RPEntity doesn't have enough units of
	 * the item, doesn't remove anything. 
	 * @param name The name of the item
	 * @param amount The number of units that should be dropped
	 * @return true iff dropping the desired amount was successful.
	 */
	public boolean drop(String name, int amount) {
		if (!isEquipped(name, amount)) {
			return false;
		}

		int toDrop = amount;

		for (String slotName : CARRYING_SLOTS) {
			RPSlot slot = getSlot(slotName);

			Iterator<RPObject> objectsIterator = slot.iterator();
			while (objectsIterator.hasNext()) {
				RPObject object = objectsIterator.next();
				if (!(object instanceof Item)) {
					continue;
				}

				Item item = (Item) object;

				if (!item.getName().equals(name)) {
					continue;
				}

				if (item instanceof StackableItem) {
					// The item is stackable, we try to remove
					// multiple ones.
					int quantity = item.getQuantity();
					if (toDrop >= quantity) {
						slot.remove(item.getID());
						toDrop -= quantity;
						// Recreate the iterator to prevent
						// ConcurrentModificationExceptions.
						// This inefficient, but simple.
						objectsIterator = slot.iterator();
					} else {
						((StackableItem) item).setQuantity(quantity - toDrop);
						toDrop = 0;
					}
				} else {
					// The item is not stackable, so we only remove a
					// single one.
					slot.remove(item.getID());
					toDrop--;
					// recreate the iterator to prevent
					// ConcurrentModificationExceptions.
					objectsIterator = slot.iterator();
				}

				if (toDrop == 0) {
					updateItemAtkDef();
					notifyWorldAboutChanges();
					return true;
				}
			}
		}
		// This will never happen because we ran isEquipped() earlier.
		return false;
	}

	/**
	 * Removes one unit of an item from the RPEntity. The item can
	 * either be stackable or non-stackable. If the RPEntity doesn't
	 * have enough the item, doesn't remove anything.
	 * @param name The name of the item
	 * @return true iff dropping the item was successful.
	 */
	public boolean drop(String name) {
		return drop(name, 1);
	}

	/**
	 * Removes the given item from the RPEntity. The item can
	 * either be stackable or non-stackable. If the RPEntity doesn't
	 * have the item, doesn't remove anything.
	 * @param item the item that should be removed
	 * @return true iff dropping the item was successful.
	 */
	public boolean drop(Item item) {
		for (String slotName : CARRYING_SLOTS) {
			RPSlot slot = getSlot(slotName);

			Iterator<RPObject> objectsIterator = slot.iterator();
			while (objectsIterator.hasNext()) {
				RPObject object = objectsIterator.next();
				if (object instanceof Item) {
					if (object == item) {
						slot.remove(object.getID());
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isEquipped(String name, int amount) {
		int found = 0;

		for (String slotName : CARRYING_SLOTS) {
			RPSlot slot = getSlot(slotName);

			for (RPObject object : slot) {
				if (!(object instanceof Item)) {
					continue;
				}

				Item item = (Item) object;

				if (item.getName().equals(name)) {
					found += item.getQuantity();

					if (found >= amount) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isEquipped(String name) {
		return isEquipped(name, 1);
	}

	/**
	 * Gets the number of items of the given name that are carried by the
	 * RPEntity. The item can either be stackable or non-stackable.
	 * @param name The item's name
	 * @return The number of carried items
	 */
	public int getNumberOfEquipped(String name) {
		int result = 0;

		for (String slotName : CARRYING_SLOTS) {
			RPSlot slot = getSlot(slotName);

			for (RPObject object : slot) {
				if (object instanceof Item) {
					Item item = (Item) object;
					if (item.getName().equals(name)) {
						result += item.getQuantity();
					}
				}
			}
		}
		return result;
	}

	/**
	 * Gets an item that is carried by the RPEntity.
	 * If the item is stackable, gets all that are on the first
	 * stack that is found. 
	 * @param name The item's name
	 * @return The item, or a stack of stackable items, or null if nothing
	 *         was found
	 */
	public Item getFirstEquipped(String name) {
		for (String slotName : CARRYING_SLOTS) {
			RPSlot slot = getSlot(slotName);

			for (RPObject object : slot) {
				if (object instanceof Item) {
					Item item = (Item) object;
					if (item.getName().equals(name)) {
						return item;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Gets an item that is carried by the RPEntity.
	 * If the item is stackable, gets all that are on the first
	 * stack that is found. 
	 * @param name The item's name
	 * @return The item, or a stack of stackable items, or null if nothing
	 *         was found
	 */
	public List<Item> getAllEquipped(String name) {
		List<Item> result = new LinkedList<Item>();

		for (String slotName : CARRYING_SLOTS) {
			RPSlot slot = getSlot(slotName);

			for (RPObject object : slot) {
				if (object instanceof Item) {
					Item item = (Item) object;
					if (item.getName().equals(name)) {
						result.add(item);
					}
				}
			}
		}
		return result;
	}

	public Item dropItemClass(String[] slots, String clazz) {
		for (String slotName : slots) {
			RPSlot slot = getSlot(slotName);

			for (RPObject object : slot) {
				if (object instanceof Item) {
					Item item = (Item) object;
					if (item.isOfClass(clazz)) {
						slot.remove(item.getID());
						updateItemAtkDef();
						return item;
					}
				}
			}
		}

		return null;
	}

	/**
	 * checks if an item of class <i>clazz</i> is equipped in slot <i>slot</i>
	 * returns true if it is, else false
	 */
	public boolean isEquippedItemClass(String slot, String clazz) {
		if (hasSlot(slot)) {
			// get slot if the this entity has one
			RPSlot rpslot = getSlot(slot);
			// traverse all slot items
			for (RPObject item : rpslot) {
				if ((item instanceof Item) && ((Item) item).isOfClass(clazz)) {
					return true;
				}
			}
		}
		// no slot, free slot or wrong item type
		return false;
	}

	/**
	 * returns the first item of class <i>clazz</i> from the slot or
	 * <code>null</code> if there is no item with the requested clazz returns
	 * the item or null
	 */
	public Item getEquippedItemClass(String slot, String clazz) {
		if (hasSlot(slot)) {
			// get slot if the this entity has one
			RPSlot rpslot = getSlot(slot);
			// traverse all slot items
			for (RPObject object : rpslot) {
				// is it the right type
				if (object instanceof Item) {
					Item item = (Item) object;
					if (item.isOfClass(clazz)) {
						return item;
					}
				}
			}
		}
		// no slot, free slot or wrong item type
		return null;
	}

	/** returns true if the entity has a weapon equipped */
	public boolean hasWeapon() {
		String[] weaponsClasses = { "club", "sword", "axe", "ranged", "missile" };

		for (String weaponClass : weaponsClasses) {
			if (isEquippedItemClass("lhand", weaponClass) || isEquippedItemClass("rhand", weaponClass)) {
				return true;
			}
		}
		return false;
	}

	private Item getWeapon() {
		String[] weaponsClasses = { "club", "sword", "axe", "ranged", "missile" };

		for (String weaponClass : weaponsClasses) {
			String[] slots = { "lhand", "rhand" };
			for (String slot : slots) {
				Item item = getEquippedItemClass(slot, weaponClass);
				if (item != null) {
					return item;
				}
			}
		}
		return null;
	}

	public List<Item> getWeapons() {
		List<Item> weapons = new ArrayList<Item>();
		Item weaponItem = getWeapon();
		if (weaponItem != null) {
			weapons.add(weaponItem);

			// pair weapons
			if (weaponItem.getName().startsWith("l_hand_")) {
				String rpclass = weaponItem.getItemClass();
				weaponItem = getEquippedItemClass("rhand", rpclass);
				if ((weaponItem != null) && (weaponItem.getName().startsWith("r_hand_"))) {
					weapons.add(weaponItem);
				} else {
					weapons.clear();
				}
			} else {
				if (weaponItem.getName().startsWith("r_hand_")) {
					weapons.clear();
				}
			}
		}
		return weapons;
	}

	private StackableItem getProjectiles() {
		String[] slots = { "lhand", "rhand" };

		for (String slot : slots) {
			StackableItem item = (StackableItem) getEquippedItemClass(slot, "projectiles");
			if (item != null) {
				return item;
			}
		}
		return null;
	}

	public StackableItem getMissile() {
		String[] slots = { "lhand", "rhand" };

		for (String slot : slots) {
			StackableItem item = (StackableItem) getEquippedItemClass(slot, "missile");
			if (item != null) {
				return item;
			}
		}
		return null;
	}
	
	/** returns true if the entity has a shield equipped */
	public boolean hasShield() {
		return isEquippedItemClass("lhand", "shield") || isEquippedItemClass("rhand", "shield");
	}

	public Item getShield() {
		Item item = getEquippedItemClass("lhand", "shield");
		if (item != null) {
			return item;
		} else {
			return getEquippedItemClass("rhand", "shield");
		}
	}

	public boolean hasArmor() {
		return isEquippedItemClass("armor", "armor");
	}

	public Item getArmor() {
		return getEquippedItemClass("armor", "armor");
	}

	public boolean hasHelmet() {
		return isEquippedItemClass("head", "helmet");
	}

	public Item getHelmet() {
		return getEquippedItemClass("head", "helmet");
	}

	public boolean hasLegs() {
		return isEquippedItemClass("legs", "legs");
	}

	public Item getLegs() {
		return getEquippedItemClass("legs", "legs");
	}

	public boolean hasBoots() {
		return isEquippedItemClass("feet", "boots");
	}

	public Item getBoots() {
		return getEquippedItemClass("feet", "boots");
	}

	public boolean hasCloak() {
		return isEquippedItemClass("cloak", "cloak");
	}

	public Item getCloak() {
		return getEquippedItemClass("cloak", "cloak");
	}

	/**
	 * checks if the entity has at least one item of type <i>type</i> in one of
	 * the given slots
	 */
	public boolean hasItem(String[] slots, String type) {
		boolean retVal;
		for (String slot : slots) {
			retVal = isEquippedItemClass(slot, type);
			if (retVal) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String describe() {
		String text = super.describe();
		if (getLevel() > 0) {
			text += " It is level " + getLevel() + ".";
		}
		return text;
	}

	public float getItemAtk() {
		int weapon = 0;
		List<Item> weapons = getWeapons();
		for (Item weaponItem : weapons) {
			weapon += weaponItem.getAttack();
		}

		// range weapons
		StackableItem projectileItem = null;
		if (weapons.size() > 0) {
			if (weapons.get(0).isOfClass("ranged")) {
				projectileItem = getProjectiles();

				if (projectileItem != null) {
					weapon += projectileItem.getAttack();
				} else {
					// If there are no projectiles...
					weapon = 0;
				}
			}
		}
		return 4.0f * weapon;
	}
	
	
	public StackableItem getProjectilesIfRangeCombat() {
		List<Item> weapons = getWeapons();
		if (weapons.size() > 0) {
			if (weapons.get(0).isOfClass("ranged")) {
				return getProjectiles();
			}
		}
		return null;
	}

//	public StackableItem getMissiles() {
//		List<Item> weapons = getWeapons();
//		if (weapons.size() > 0) {
//			if (weapons.get(0).isOfClass("missile")) {
//				return getMissile();
//			}
//		}
//		return null;
//	}
//	
	public float getItemDef() {
		int shield = 0;
		int armor = 0;
		int helmet = 0;
		int legs = 0;
		int boots = 0;
		int cloak = 0;
		int weapon = 0;

		if (hasShield()) {
			shield = getShield().getDefense();
		}

		if (hasArmor()) {
			armor = getArmor().getDefense();
		}

		if (hasHelmet()) {
			helmet = getHelmet().getDefense();
		}

		if (hasLegs()) {
			legs = getLegs().getDefense();
		}

		if (hasBoots()) {
			boots = getBoots().getDefense();
		}

		if (hasCloak()) {
			cloak = getCloak().getDefense();
		}

		List<Item> targetWeapons = getWeapons();
		for (Item weaponItem : targetWeapons) {
			weapon += weaponItem.getDefense();
		}

		return 4.0f * shield + 2.0f * armor + 1.5f * cloak + 1.0f * helmet + 1.0f * legs + 1.0f * boots + 4.0f * weapon;
	}

	/**
	 * recalculate item based atk and def
	 */
	public void updateItemAtkDef() {
		put("atk_item", ((int) getItemAtk()));
		put("def_item", ((int) getItemDef()));
		notifyWorldAboutChanges();
	}

	/**
	 * Can this Entity do a range attack?
	 *
	 * @return true, wenn ein Range compat moeglich ist, sonst false
	 */
	public boolean canDoRangeAttacks() {
		StackableItem projectiles = getProjectilesIfRangeCombat();
		StackableItem missiles = getMissile();
		return ((projectiles != null) && (projectiles.getQuantity() > 0)
				|| (missiles != null) && (missiles.getQuantity() > 0));
	}

	/**
	 * Gets this RPEntity's outfit.
	 * 
	 * Note: some RPEntities (e.g. sheep, many NPC's, all monsters) don't
	 * use the outfit system.
	 * 
	 * @return The outfit, or null if this RPEntity is represented as a single
	 *         sprite rather than an outfit combination.
	 */
	public Outfit getOutfit() {
		if (has("outfit")) {
			return new Outfit(getInt("outfit"));
		}
		return null;
	}

	/**
	 * Sets this RPEntity's outfit.
	 * 
	 * Note: some RPEntities (e.g. sheep, many NPC's, all monsters) don't
	 * use the outfit system.
	 * 
	 * @param outfit The new outfit.
	 */
	public void setOutfit(Outfit outfit) {
		put("outfit", outfit.getCode());
	}

}
