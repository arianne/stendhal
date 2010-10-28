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


import games.stendhal.common.Constants;
import games.stendhal.common.Grammar;
import games.stendhal.common.Level;
import games.stendhal.common.Rand;
import games.stendhal.common.constants.Nature;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.ItemLogger;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.db.StendhalKillLogDAO;
import games.stendhal.server.core.engine.dbcommand.LogKillEventCommand;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TutorialNotifier;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.Stackable;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.npc.parser.WordList;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.EntitySlot;
import games.stendhal.server.events.AttackEvent;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.SyntaxException;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.game.Statistics;
import marauroa.server.game.db.DAORegister;

import org.apache.log4j.Logger;

public abstract class RPEntity extends GuidedEntity {
	
	private static final float WEAPON_DEF_MULTIPLIER = 4.0f;

	private static final float BOOTS_DEF_MULTIPLIER = 1.0f;

	private static final float LEG_DEF_MULTIPLIER = 1.0f;

	private static final float HELMET_DEF_MULTIPLIER = 1.0f;

	private static final float CLOAK_DEF_MULTIPLIER = 1.5f;

	private static final float ARMOR_DEF_MULTIPLIER = 2.0f;

	private static final float SHIELD_DEF_MULTIPLIER = 4.0f;

	/**
	 * The title attribute name.
	 */
	protected static final String ATTR_TITLE = "title";

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(RPEntity.class);

	private static Statistics stats;

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

	/**
	 * Maps each enemy which has recently damaged this RPEntity to the turn when
	 * the last damage has occurred.
	 * 
	 * You only get ATK and DEF experience by fighting against a creature that
	 * is in this list.
	 */
	private final Map<RPEntity, Integer> enemiesThatGiveFightXP;

	/** List of all enemies that are currently attacking this entity. */
	private final List<Entity> attackSources;

	/** the enemy that is currently attacked by this entity. */
	private RPEntity attackTarget;

	/**
	 * Maps each attacker to the sum of hitpoint loss it has caused to this
	 * RPEntity.
	 */
	protected Map<Entity, Integer> damageReceived;

	/** list of players which are to reward with xp on killing this creature. */
	protected Set<String> playersToReward;

	protected int totalDamageReceived;

	/**
	 * To prevent players from gaining attack and defense experience by fighting
	 * against very weak creatures, they only gain atk and def xp for so many
	 * turns after they have actually been damaged by the enemy. //
	 */
	private static final int TURNS_WHILE_FIGHT_XP_INCREASES = 12;

	/**
	 * To avoid using karma for damage calculations when the natural ability of
	 * the fighters would mean they need no luck, we only use karma when the
	 * levels are significantly different.
	 */
	
	private static final double IGNORE_KARMA_MULTIPLIER = 0.2;

	/**
	 * Level bonus for defence given to everyone. Prevents newbies killing each
	 * other too fast.
	 */
	private static final double NEWBIE_DEF = 10.0;
	/**
	 * Armor value of no armor. Prevents unarmored or lightly armored entities
	 * from being completely helpless
	 */
	private static final double SKIN_DEF = 10.0;
	/** Adjusts the weight of level. Larger means weight more */
	private static final double LEVEL_ATK = 0.03;
	/** Adjusts the weight of level. Larger means weight more */
	private static final double LEVEL_DEF = 0.03;
	/** General parameter for damage. Larger means more damage. */
	private static final double WEIGHT_ATK = 8.0;
	/** the level where relative damage curves start being linear. */
	private static final double EVEN_POINT = 1.2;
	/**
	 * Steepness of the damage vs level curves. The maximum bonus/penalty with
	 * weak enemies
	 */
	private static final double WEIGHT_EFFECT = 0.5;

	@Override
	protected boolean handlePortal(final Portal portal) {
		if (isZoneChangeAllowed()) {
			logger.debug("Using portal " + portal);
			return portal.onUsed(this);
		}
		return super.handlePortal(portal);
	}

	public static void generateRPClass() {
		try {
			stats = Statistics.getStatistics();
			final RPClass entity = new RPClass("rpentity");
			entity.isA("active_entity");
			entity.addAttribute("name", Type.STRING);
			entity.addAttribute(ATTR_TITLE, Type.STRING);
			entity.addAttribute("level", Type.SHORT);
			entity.addAttribute("xp", Type.INT);
			entity.addAttribute("mana", Type.INT);
			entity.addAttribute("base_mana", Type.INT);

			entity.addAttribute("base_hp", Type.SHORT);
			entity.addAttribute("hp", Type.SHORT);

			entity.addAttribute("atk", Type.SHORT, Definition.PRIVATE);
			entity.addAttribute("atk_xp", Type.INT, Definition.PRIVATE);
			entity.addAttribute("def", Type.SHORT, Definition.PRIVATE);
			entity.addAttribute("def_xp", Type.INT, Definition.PRIVATE);
			entity.addAttribute("atk_item", Type.INT,
					(byte) (Definition.PRIVATE | Definition.VOLATILE));
			entity.addAttribute("def_item", Type.INT,
					(byte) (Definition.PRIVATE | Definition.VOLATILE));

			entity.addAttribute("risk", Type.BYTE, Definition.VOLATILE); // obsolete, do not use
			entity.addAttribute("damage", Type.INT, Definition.VOLATILE); // obsolete, do not use
			entity.addAttribute("heal", Type.INT, Definition.VOLATILE);
			// TODO: check that the binary representation of old saved players is compatible when this is changed into a list.
			entity.addAttribute("target", Type.INT, Definition.VOLATILE);
			entity.addAttribute("title_type", Type.STRING, Definition.VOLATILE);

			entity.addRPSlot("head", 1, Definition.PRIVATE);
			entity.addRPSlot("rhand", 1, Definition.PRIVATE);
			entity.addRPSlot("lhand", 1, Definition.PRIVATE);
			entity.addRPSlot("armor", 1, Definition.PRIVATE);
			entity.addRPSlot("finger", 1, Definition.PRIVATE);
			entity.addRPSlot("cloak", 1, Definition.PRIVATE);
			entity.addRPSlot("legs", 1, Definition.PRIVATE);
			entity.addRPSlot("feet", 1, Definition.PRIVATE);
			entity.addRPSlot("bag", 12, Definition.PRIVATE);
			entity.addRPSlot("keyring", 8, Definition.PRIVATE);

			entity.addRPEvent("attack", Definition.VOLATILE);
		} catch (final SyntaxException e) {
			logger.error("cannot generateRPClass", e);
		}
	}

	public RPEntity(final RPObject object) {
		super(object);
		attackSources = new ArrayList<Entity>();
		damageReceived = new WeakHashMap<Entity, Integer>();
		playersToReward = new HashSet<String>();
		enemiesThatGiveFightXP = new WeakHashMap<RPEntity, Integer>();
		totalDamageReceived = 0;
	}

	public RPEntity() {
		super();
		attackSources = new ArrayList<Entity>();
		damageReceived = new WeakHashMap<Entity, Integer>();
		playersToReward = new HashSet<String>();
		enemiesThatGiveFightXP = new WeakHashMap<RPEntity, Integer>();
		totalDamageReceived = 0;
	}

	/**
	 * Give the player some karma (good or bad).
	 * 
	 * @param karma
	 *            An amount of karma to add/subtract.
	 */
	public void addKarma(final double karma) {
		// No nothing
	}

	/**
	 * Get the current amount of karma.
	 * 
	 * @return The current amount of karma.
	 * 
	 * @see #addKarma(double)
	 */
	public double getKarma() {
		// No karma (yet)
		return 0.0;
	}

	/**
	 * Get some of the player's karma. A positive value indicates good
	 * luck/energy. A negative value indicates bad luck/energy. A value of zero
	 * should cause no change on an action or outcome.
	 * 
	 * @param scale
	 *            A positive number.
	 * 
	 * @return A number between -scale and scale.
	 */
	public double useKarma(final double scale) {
		// No impact
		return 0.0;
	}

	/**
	 * Get some of the player's karma. A positive value indicates good
	 * luck/energy. A negative value indicates bad luck/energy. A value of zero
	 * should cause no change on an action or outcome.
	 * 
	 * @param negLimit
	 *            The lowest negative value returned.
	 * @param posLimit
	 *            The highest positive value returned.
	 * 
	 * @return A number within negLimit &lt;= 0 &lt;= posLimit.
	 */
	public double useKarma(final double negLimit, final double posLimit) {
		// No impact
		return 0.0;
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
	public double useKarma(final double negLimit, final double posLimit,
			final double granularity) {
		// No impact
		return 0.0;
	}

	/**
	 * Heal this entity completely.
	 * 
	 * @return The amount actually healed.
	 */
	public int heal() {
		final int baseHP = getBaseHP();
		final int given = baseHP - getHP();

		if (given != 0) {
			put("heal", given);
			setHP(baseHP);
		}

		return given;
	}

	/**
	 * Heal this entity.
	 * 
	 * @param amount
	 *            The [maximum] amount to heal by.
	 * 
	 * @return The amount actually healed.
	 */
	public int heal(final int amount) {
		return heal(amount, false);
	}

	/**
	 * Heal this entity.
	 * 
	 * @param amount
	 *            The [maximum] amount to heal by.
	 * @param tell
	 *            Whether to tell the entity they've been healed.
	 * 
	 * @return The amount actually healed.
	 */
	public int heal(final int amount, final boolean tell) {
		int tempHp = getHP();
		int given = 0;

		// Avoid creating zombies out of dead creatures
		if (tempHp > 0) {
			given = Math.min(amount, getBaseHP() - tempHp);

			if (given != 0) {
				tempHp += given;

				if (tell) {
					put("heal", given);
				}

				setHP(tempHp);
			}
		}

		return given;
	}

	@Override
	public void update() {
		super.update();

		if (has("name")) {
			final String newName = get("name");

			registerNewName(newName, name);

			name = newName;
		}

		if (has("atk")) {
			atk = getInt("atk");
		}
		if (has("atk_xp")) {
			atk_xp = getInt("atk_xp");
			setATKXP(atk_xp);
		}

		if (has("def")) {
			def = getInt("def");
		}
		if (has("def_xp")) {
			def_xp = getInt("def_xp");
			setDEFXP(def_xp);
		}

		if (has("base_hp")) {
			base_hp = getInt("base_hp");
		}
		if (has("hp")) {
			hp = getInt("hp");
		}

		if (has("level")) {
			level = getInt("level");
		}
		if (has("xp")) {
			xp = getInt("xp");
		}
		if (has("mana")) {
			mana = getInt("mana");
		}
		if (has("base_mana")) {
			mana = getInt("base_mana");
		}
	}

	/**
	 * Register the new name in the conversation parser word list.
	 * 
	 * @param newName
	 * @param oldName
	 */
	private static void registerNewName(final String newName,
			final String oldName) {
		if ((oldName != null) && !oldName.equals(newName)) {
			WordList.getInstance().unregisterSubjectName(oldName);
		}

		if ((oldName == null) || !oldName.equals(newName)) {
			WordList.getInstance().registerSubjectName(newName);
		}
	}

	/**
	 * Is called when this has hit the given defender. Determines how much
	 * hitpoints the defender will lose, based on this's ATK experience and
	 * weapon(s), the defender's DEF experience and defensive items, and a
	 * random generator.
	 * 
	 * @param defender
	 *            The defender.
	 * @return The number of hitpoints that the target should lose. 0 if the
	 *         attack was completely blocked by the defender.
	 */
	public int damageDone(final RPEntity defender) {
		// Don't start from 0 to mitigate weird behaviour at very low levels
		final int effectiveAttackerLevel = getLevel() + 5;
		final int effectiveDefenderLevel = defender.getLevel() + 5;

		// Defending side
		final double armor = defender.getItemDef();
		final int targetDef = defender.getDEF();
		// Even strong players are vulnerable without any armor.
		// Armor def gets much higher with high level players unlike
		// weapon atk, so it can not be treated similarly. Using geometric
		// / mean to balance things a bit.
		final double maxDefence = Math.sqrt(targetDef * (SKIN_DEF + armor))
				* (NEWBIE_DEF + LEVEL_DEF * effectiveDefenderLevel);

		double defence = Rand.rand() * maxDefence;
		/*
		 * Account for karma (+/-10%) But, the defender doesn't need luck to
		 * help him defend if he's a much higher level than this attacker
		 */
		final int levelDifferenceToNotNeedKarmaDefending = (int) (IGNORE_KARMA_MULTIPLIER * defender.getLevel());
		
		if (!(effectiveDefenderLevel - levelDifferenceToNotNeedKarmaDefending  > effectiveAttackerLevel)) {
			defence += defence * defender.useKarma(0.1);
		}

		// Attacking
		if (logger.isDebugEnabled()) {
			logger.debug("attacker has " + getATK() + " and uses a weapon of "
					+ getItemAtk());
		}
		final int sourceAtk = getATK();

		// Make fast weapons efficient against weak enemies, and heavy
		// better against strong enemies.
		// Half a parabola; desceding for rate < 5; ascending for > 5
		double speedEffect = 1.0;
		if (effectiveDefenderLevel < EVEN_POINT * effectiveAttackerLevel) {
			final double levelPart = 1.0 - effectiveDefenderLevel
					/ (EVEN_POINT * effectiveAttackerLevel);
			// Gets values -1 at rate = 1, 0 at rate = 5,
			// and approaches 1 when rate approaches infinity.
			// We can't use a much simpler function as long as we need
			// to deal with open ended rate values.
			final double speedPart = 1 - 8 / (getAttackRate() + 3.0);

			speedEffect = 1.0 - WEIGHT_EFFECT * speedPart * levelPart
					* levelPart;
		}

		final double weaponComponent = 1.0 + getItemAtk();
		final double maxAttack = sourceAtk * weaponComponent
				* (1 + LEVEL_ATK * effectiveAttackerLevel) * speedEffect;
		double attack = Rand.rand() * maxAttack;

		/*
		 * Account for karma (+/-10%) But, don't need luck to help you attack if
		 * you're a much higher level than what you attack
		 */
		final int levelDifferenceToNotNeedKarmaAttacking = (int) (IGNORE_KARMA_MULTIPLIER * getLevel());
		if (!(effectiveAttackerLevel - levelDifferenceToNotNeedKarmaAttacking > effectiveDefenderLevel)) {
			attack += attack * useKarma(0.1);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("DEF MAX: " + maxDefence + "\t DEF VALUE: " + defence);
		}

		// Apply defense and damage type effect
		int damage = (int) (defender.getSusceptibility(getDamageType()) 
				* (WEIGHT_ATK * attack - defence) / maxDefence);

		if (canDoRangeAttack(defender)) {
			// The attacker is attacking either using a range weapon with
			// ammunition such as a bow and arrows, or a missile such as a
			// spear.
			damage = applyDistanceAttackModifiers(damage,
					squaredDistance(defender));
		}

		return damage;
	}

	/**
	 * Calculates the damage that will be done in a distance attack (bow and
	 * arrows, spear, etc.).
	 * 
	 * @param damage
	 *            The damage that would have been done if there would be no
	 *            modifiers for distance attacks.
	 * @param squareDistance
	 *            the distance
	 * @return The damage that will be done with the distance attack.
	 */
	public static int applyDistanceAttackModifiers(final int damage,
			final double squareDistance) {
		final double maxrange = 7;
		final double maxrangeSquared = maxrange * maxrange;
		if (maxrangeSquared < squareDistance) {
			return 0;
		} else if (squareDistance == 0) {
			// as a special case, make archers switch to melee when the enemy is
			// next to them
			return (int) (0.8 * damage);
		}

		final double outOfRange = maxrange + 1;
		final double distance = Math.sqrt(squareDistance);

		// a downward parabola with zero points at 0 and outOfRange
		return (int) (damage * ((distance * 4) / outOfRange - 4
				* squareDistance / (outOfRange * outOfRange)));
	}

	/**
	 * Set the entity's name.
	 * 
	 * @param name
	 *            The new name.
	 */
	public void setName(final String name) {
		registerNewName(name, this.name);

		this.name = name;
		put("name", name);
	}

	/**
	 * Get the entity's name.
	 * 
	 * @return The entity's name.
	 */
	public String getName() {
		return name;
	}

	public void setLevel(final int level) {
		this.level = level;
		put("level", level);
	}

	public int getLevel() {
		return level;
	}

	public void setATK(final int atk) {
		this.atk = atk;
		put("atk", atk);
	}

	public int getATK() {
		return atk;
	}

	/**
	 * Set attack XP.
	 * 
	 * @param atk the new value
	 */
	public void setATKXP(final int atk) {
		this.atk_xp = atk;
		put("atk_xp", atk_xp);

		// Handle level changes
		final int newLevel = Level.getLevel(atk_xp);
		final int levels = newLevel - (getATK() - 10);

		// In case we level up several levels at a single time.
		for (int i = 0; i < Math.abs(levels); i++) {
			setATK(this.atk + (int) Math.signum(levels) * 1);
			new GameEvent(getName(), "atk", Integer.toString(getATK())).raise();
		}
	}

	public int getATKXP() {
		return atk_xp;
	}

	/**
	 * Increase attack XP by 1.
	 */
	public void incATKXP() {
		setATKXP(atk_xp + 1);
	}

	public void setDEF(final int def) {
		this.def = def;
		put("def", def);
	}

	public int getDEF() {
		return def;
	}

	/**
	 * Set defense XP.
	 * 
	 * @param def the new value
	 */
	public void setDEFXP(final int def) {
		this.def_xp = def;
		put("def_xp", def_xp);
		
		// Handle level changes
		final int newLevel = Level.getLevel(def_xp);
		final int levels = newLevel - (getDEF() - 10);

		// In case we level up several levels at a single time.
		for (int i = 0; i < Math.abs(levels); i++) {
			setDEF(this.def + (int) Math.signum(levels) * 1);
			new GameEvent(getName(), "def", Integer.toString(getDEF())).raise();
		}
	}

	public int getDEFXP() {
		return def_xp;
	}

	/**
	 * Increase defense XP by 1.
	 */
	public void incDEFXP() {
		setDEFXP(def_xp + 1);
	}

	/**
	 * Set the base and current HP.
	 * 
	 * @param hp
	 *            The HP to set.
	 */
	public void initHP(final int hp) {
		setBaseHP(hp);
		setHP(hp);
	}

	/**
	 * Set the base HP.
	 * 
	 * @param newhp
	 *            The base HP to set.
	 */
	public void setBaseHP(final int newhp) {
		this.base_hp = newhp;
		put("base_hp", newhp);
	}

	/**
	 * Get the base HP.
	 * 
	 * @return The current HP.
	 */
	public int getBaseHP() {
		return base_hp;
	}

	/**
	 * Set the HP. <br>
	 * DO NOT USE THIS UNLESS YOU REALLY KNOW WHAT YOU ARE DOING. <br>
	 * Use the appropriate damage(), and heal() methods instead.
	 * 
	 * @param hp
	 *            The HP to set.
	 */
	public void setHP(final int hp) {
		this.hp = hp;
		put("hp", hp);
	}

	/**
	 * Get the current HP.
	 * 
	 * @return The current HP.
	 */
	public int getHP() {
		return hp;
	}

	/**
	 * Gets the mana (magic).
	 * 
	 * @return mana
	 */
	public int getMana() {
		return mana;
	}

	/**
	 * Gets the base mana (like base_hp).
	 * 
	 * @return base mana
	 */
	public int getBaseMana() {
		return base_mana;
	}

	/**
	 * sets the available mana.
	 * 
	 * @param newMana
	 *            new amount of mana
	 */
	public void setMana(final int newMana) {
		mana = newMana;
		put("mana", newMana);
	}

	/**
	 * Sets the base mana (like base_hp).
	 * 
	 * @param newBaseMana
	 *            new amount of base mana
	 */
	public void setBaseMana(final int newBaseMana) {
		base_mana = newBaseMana;
		put("base_mana", newBaseMana);
	}

	/**
	 * adds to base mana (like addXP).
	 * 
	 * @param newBaseMana
	 *            amount of base mana to be added
	 */
	public void addBaseMana(final int newBaseMana) {
		base_mana += newBaseMana;
		put("base_mana", base_mana);
	}

	public final void setXP(final int newxp) {
		if (newxp < 0) {
			return;
		}
		this.xp = newxp;
		put("xp", xp);
	}

	public void subXP(final int newxp) {
		addXP(-newxp);
	}

	public void addXP(final int newxp) {

		if (Integer.MAX_VALUE - this.xp <= newxp) {
			return;
		}
		if (newxp == 0) {
			return;
		}

		// Increment experience points
		this.xp += newxp;
		put("xp", xp);
		String[] params = { Integer.toString(newxp) };

		new GameEvent(getName(), "added xp", params).raise();
		new GameEvent(getName(), "xp", String.valueOf(xp)).raise();
		
		updateLevel();
	}
	
	/**
	 * Change the level to match the XP, if needed
	 */
	protected void updateLevel() {
		final int newLevel = Level.getLevel(getXP());
		final int oldLevel = getLevel();
		final int levels = newLevel - oldLevel;

		// In case we level up several levels at a single time.
		for (int i = 0; i < Math.abs(levels); i++) {
			setBaseHP(getBaseHP() + (int) Math.signum(levels) * 10);
			setHP(getHP() + (int) Math.signum(levels) * 10);
			new GameEvent(getName(), "level", Integer.toString(oldLevel+(i+1)*((int) Math.signum(levels)))).raise();
			setLevel(newLevel);
		}
	}

	public int getXP() {
		return xp;
	}
	
	/**
	 * Get a multiplier for a given damage type when this
	 * entity is damaged.
	 * 
	 * @param type Type of the damage
	 * @return damage multiplier
	 */
	protected double getSusceptibility(Nature type) {
		return 1.0;
	}
	
	/**
	 * Get the type of the damage this entity inflicts
	 * 
	 * @return type of damage
	 */
	protected Nature getDamageType() {
		return Nature.CUT;
	}

	/***************************************************************************
	 * * Attack handling code. * *
	 **************************************************************************/

	/**
	 * @return true if this RPEntity is attackable.
	 */
	public boolean isAttackable() {
		return true;
	}

	/**
	 * Modify the entity to order to attack the target entity.
	 * 
	 * @param target
	 */
	public void setTarget(final RPEntity target) {
		put("target", target.getID().getObjectID());
		if (attackTarget != null) {
			attackTarget.attackSources.remove(this);
		}
		attackTarget = target;
	}

	/** Modify the entity to stop attacking. */
	public void stopAttack() {
		if (has("heal")) {
			remove("heal");
		}
		if (has("target")) {
			remove("target");
		}

		if (attackTarget != null) {
			attackTarget.attackSources.remove(this);

			// remove opponent here to avoid memory leak
			enemiesThatGiveFightXP.remove(attackTarget);

			attackTarget = null;
		}
	}

	public boolean getsFightXpFrom(final RPEntity enemy) {
		final Integer turnWhenLastDamaged = enemiesThatGiveFightXP.get(enemy);
		if (turnWhenLastDamaged == null) {
			return false;
		}
		final int currentTurn = SingletonRepository.getRuleProcessor()
				.getTurn();
		if (currentTurn - turnWhenLastDamaged > TURNS_WHILE_FIGHT_XP_INCREASES) {
			enemiesThatGiveFightXP.remove(enemy);
			return false;
		}
		return true;
	}

	public void stopAttacking(final Entity attacker) {
		if (attacker.has("target")) {
			attacker.remove("target");
		}
	}

	public void rememberAttacker(final Entity attacker) {
		if (!attackSources.contains(attacker)) {
			attackSources.add(attacker);
		}
	}

	/**
	 * Creates a blood pool on the ground under this entity, but only if there
	 * isn't a blood pool at that position already.
	 */
	private void bleedOnGround() {
		final Rectangle2D rect = getArea();
		final int bx = (int) rect.getX();
		final int by = (int) rect.getY();
		final StendhalRPZone zone = getZone();

		if (zone.getBlood(bx, by) == null) {
			final Blood blood = new Blood();
			blood.setPosition(bx, by);

			zone.add(blood);
		}
	}

	/**
	 * This method is called when this entity has been attacked by Entity
	 * attacker and it has been damaged with damage points.
	 * 
	 * @param attacker
	 * @param damage
	 */
	public void onDamaged(final Entity attacker, final int damage) {
		logger.debug("Damaged " + damage + " points by " + attacker.getID());

		bleedOnGround();
		if (attacker instanceof RPEntity) {
			final int currentTurn = SingletonRepository.getRuleProcessor()
					.getTurn();
			enemiesThatGiveFightXP.put((RPEntity) attacker, currentTurn);
		}

		final int leftHP = getHP() - damage;

		totalDamageReceived += damage;

		// remember the damage done so that the attacker can later be rewarded
		// XP etc.
		final Integer oldDamage = damageReceived.get(attacker);
		if (oldDamage != null) {
			damageReceived.put(attacker, damage + oldDamage);
		} else {
			damageReceived.put(attacker, damage);
		}
		addPlayersToReward(attacker);

		if (leftHP > 0) {
			setHP(leftHP);
		} else {
			kill(attacker);
		}

		notifyWorldAboutChanges();
	}

	/**
	 * Manages a list of players to reward XP in case this creature is killed.
	 * 
	 * @param player
	 *            Player
	 */
	protected void addPlayersToReward(final Entity player) {
		if (player instanceof Player) {
			playersToReward.add(((Player) player).getName());
		}
	}

	/**
	 * Apply damage to this entity. This is normally called from one of the
	 * other damage() methods to account for death.
	 * 
	 * @param amount
	 *            The HP to take.
	 * 
	 * @return The damage actually taken (in case HP was < amount).
	 */
	protected int damage(final int amount) {
		int tempHp = getHP();
		final int taken = Math.min(amount, tempHp);

		tempHp -= taken;
		setHP(tempHp);

		return taken;
	}

	/**
	 * Apply damage to this entity, and call onDead() if HP reaches 0.
	 * 
	 * @param amount
	 *            The HP to take.
	 * @param attacker
	 *            The attacking entity.
	 * 
	 * @return The damage actually taken (in case HP was < amount).
	 */
	public int damage(final int amount, final Entity attacker) {
		final int taken = damage(amount);

		if (hp <= 0) {
			onDead(attacker);
		}

		return taken;
	}

	/**
	 * Apply damage to this entity, delaying the damage to happen in a turn 
	 * notifier. To be used when dying could result in concurrent modification
	 * in the zone's entity list, such as sheep starving. Call onDead() if HP 
	 * reaches 0.
	 * 
	 * @param amount
	 *            The HP to take.
	 * @param attackerName
	 *            The name of the attacker.
	 */
	public void delayedDamage(final int amount, final String attackerName) {
		final RPEntity me = this;
		/*
		 * Use a dummy damager rpentity, so that we can follow the 
		 * normal code path. Important when dying. 
		 */
		final Entity attacker = new RPEntity(this) {
			@Override
			public String getTitle() {
				return attackerName;
			}

			@Override
			protected void dropItemsOn(Corpse corpse) {
				
			}

			@Override
			public void logic() {
				
			}
		};
		
		SingletonRepository.getTurnNotifier().notifyInTurns(1, new TurnListener() {
			public void onTurnReached(int turn) {
				me.damage(amount, attacker);
			}
		}); 
	}

	/**
	 * Kills this RPEntity.
	 * 
	 * @param killer
	 *            The killer
	 */
	protected void kill(final Entity killer) {
		setHP(0);
		SingletonRepository.getRuleProcessor().killRPEntity(this, killer);
	}

	/**
	 * Gives XP to every player who has helped killing this RPEntity.
	 * 
	 * @param oldXP
	 *            The XP that this RPEntity had before being killed.
	 */
	protected void rewardKillers(final int oldXP) {
		final int xpReward = (int) (oldXP * 0.05);

		for (final String killerName : playersToReward) {
			final Player killer = SingletonRepository.getRuleProcessor()
					.getPlayer(killerName);
			// check logout
			if (killer == null) {
				continue;
			}

			TutorialNotifier.killedSomething(killer);

			final Integer damageDone = damageReceived.get(killer);
			if (damageDone == null) {
				continue;
			}

			if (logger.isDebugEnabled()) {
				final String killName;
				if (killer.has("name")) {
					killName = killer.get("name");
				} else {
					killName = killer.get("type");
				}

				logger.debug(killName + " did " + damageDone + " of "
						+ totalDamageReceived + ". Reward was " + xpReward);
			}

			final int xpEarn = (int) (xpReward * ((float) damageDone / (float) totalDamageReceived));

			logger.debug("OnDead: " + xpReward + "\t" + damageDone + "\t"
					+ totalDamageReceived + "\t");

			int reward = xpEarn;

			// We ensure that the player gets at least 1 experience
			// point, because getting nothing lowers motivation.
			if (reward == 0) {
				reward = 1;
			}

			killer.addXP(reward);
			SingletonRepository.getAchievementNotifier().onKill(killer);
			// For some quests etc., it is required that the player kills a
			// certain creature without the help of others.
			// Find out if the player killed this RPEntity on his own, but
			// don't overwrite solo with shared.
			final String killedName = getName();

			if (killedName == null) {
				logger.warn("This entity returns null as name: " + this);
			} else {
				if (damageDone == totalDamageReceived) {
					killer.setSoloKill(killedName);
				} else {
					killer.setSharedKill(killedName);
				}
			}

			killer.notifyWorldAboutChanges();
		}
	}

	/**
	 * This method is called when the entity has been killed ( hp==0 ).
	 * 
	 * @param killer
	 *            The entity who caused the death
	 */
	public final void onDead(final Entity killer) {
		onDead(killer, true);
	}

	/**
	 * This method is called when this entity has been killed (hp == 0).
	 * 
	 * @param killer
	 *            The entity who caused the death, i.e. who did the last hit.
	 * @param remove
	 *            true iff this entity should be removed from the world. For
	 *            almost everything remove is true, but not for the players, who
	 *            are instead moved to afterlife ("reborn").
	 */
	public void onDead(final Entity killer, final boolean remove) {
		StendhalKillLogDAO killLog = DAORegister.get().get(StendhalKillLogDAO.class);
		String killerName = killLog.getEntityName(killer);

		if (killer instanceof RPEntity) {
			new GameEvent(killerName, "killed", killLog.getEntityName(this), killLog.entityToType(killer), killLog.entityToType(this)).raise();
		}

		DBCommandQueue.get().enqueue(new LogKillEventCommand(this, killer));

		// Players are unique, so they should not get an article. 
		if (!(killer instanceof Player)) {
			killerName = Grammar.a_noun(killerName);
		}
		onDead(killerName, remove);
	}

	/**
	 * This method is called when this entity has been killed (hp == 0).
	 * 
	 * @param killerName
	 *            The killer's name (a phrase suitable in the expression "
	 *            <code>by</code> <em>killerName</em>".
	 * @param remove
	 *            <code>true</code> to remove entity from world.
	 */
	protected final void onDead(final String killerName, final boolean remove) {
		final int oldXP = this.getXP();

		// Establish how much xp points your are rewarded
		if (oldXP > 0) {
			// give XP to everyone who helped killing this RPEntity
			rewardKillers(oldXP);
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
		final Corpse corpse = makeCorpse(killerName);

		// Add some reward inside the corpse
		dropItemsOn(corpse);
		updateItemAtkDef();

		final StendhalRPZone zone = getZone();
		zone.add(corpse);

		// Corpse may want to know who this entity was attacking (RaidCreatureCorpse does), 
		// so defer stopping. 
		stopAttack();
		if (remove) {
			zone.remove(this);
		}
	}
	
	/**
	 * Make a corpse belonging to this entity
	 * 
	 * @param killer Name of the killer
	 * @return The corpse of a dead RPEntity
	 */
	protected Corpse makeCorpse(String killer) {
		return new Corpse(this, killer);
	}
	
	/**
	 * Get the corpse image name to be used for the entity.
	 * Defaults to a player corpse.
	 * 
	 * @return Identification string for corpse. This is the corpse 
	 * image shown by the client without the path or file extension.  
	 */
	public String getCorpseName() {
		return "player";
	}
	
	public int getCorpseWidth() {
		return 1;
	}
	
	public int getCorpseHeight() {
		return 1;
	}

	protected abstract void dropItemsOn(Corpse corpse);

	/**
	 * Determine if the entity is invisible to creatures.
	 * 
	 * @return <code>true</code> if invisible.
	 */
	public boolean isInvisibleToCreatures() {
		return false;
	}

	/**
	 * Return true if this entity is attacked.
	 * 
	 * @return true if no attack sources found
	 */
	public boolean isAttacked() {
		return !attackSources.isEmpty();
	}

	/**
	 * Returns the Entities that are attacking this character.
	 * 
	 * @return list of all attacking entities
	 */
	public List<Entity> getAttackSources() {
		return attackSources;
	}

	/**
	 * Returns the RPEntities that are attacking this character.
	 * 
	 * @return list of all attacking RPEntities
	 */
	public List<RPEntity> getAttackingRPEntities() {
		final List<RPEntity> list = new ArrayList<RPEntity>();

		for (final Entity entity : getAttackSources()) {
			if (entity instanceof RPEntity) {
				list.add((RPEntity) entity);
			}
		}
		return list;
	}

	/**
	 * Checks whether the attacktarget is null. Sets attacktarget to null if hp
	 * of attacktarget <=0;
	 * 
	 * @return true if attacktarget != null and not dead
	 */
	public boolean isAttacking() {
		if (attackTarget != null) {
			if (attackTarget.getHP() <= 0) {
				attackTarget = null;
			}
		} else {
			return false;
		}
		return attackTarget != null;
	}

	/**
	 * Return the RPEntity that this entity is attacking.
	 * 
	 * @return the attack target of this
	 */
	public RPEntity getAttackTarget() {
		return attackTarget;
	}

	/***************************************************************************
	 * * Equipment handling. * *
	 **************************************************************************/

	/**
	 * Tries to equip an item in the appropriate slot.
	 * 
	 * @param item
	 *            the item
	 * @return true if the item can be equipped, else false
	 */
	public final boolean equipToInventoryOnly(final Item item) {
		final String slot = getSlotNameToEquip(item);
		if (slot != null) {
			return equipIt(slot, item);
		} else {
			return false;
		}
	}

	
	/**
	 * gets the name of the slot in which the entity can equip the item.
	 *
	 * @param item 
	 * @return the slot name for the item or null if there is no matching slot
	 *         in the entity
	 */
	@SuppressWarnings("unchecked")
	public final String getSlotNameToEquip(final Item item) {
		// get all possible slots for this item
		final List<String> slotNames = item.getPossibleSlots();

		if (item instanceof Stackable<?>) {
			// first try to put the item on an existing stack
			final Stackable<?> stackEntity = (Stackable<?>) item;
			for (final String slotName : slotNames) {
				if (hasSlot(slotName)) {
					final RPSlot rpslot = getSlot(slotName);
					for (final RPObject object : rpslot) {
						if (object instanceof Stackable<?>) {
							// found another stackable
							final Stackable other = (Stackable<?>) object;
							if (other.isStackable(stackEntity)) {
								return slotName;
							}
						}
					}
				}
			}
		}

		// We can't stack it on another item. Check if we can simply
		// add it to an empty cell.
		for (final String slot : slotNames) {
			if (hasSlot(slot)) {
				final RPSlot rpslot = getSlot(slot);

				// check that this slot is reachable (e. g. fixed keyring)
				if (rpslot instanceof EntitySlot) {
					EntitySlot entitySlot = (EntitySlot) rpslot;
					if (!entitySlot.isReachableForThrowingThingsIntoBy(this)) {
						continue;
					}
				}

				if (!rpslot.isFull()) {
					return slot;
				}
			}
		}
		return null;
	}

	/**
	 * Tries to equip an item in the appropriate slot.
	 * 
	 * @param item the item
	 * @return true if the item can be equipped, else false
	 */
	public final boolean equipOrPutOnGround(final Item item) {
		if (equipToInventoryOnly(item)) {
			return true;
		} else {
			item.setPosition(getX(), getY());
			getZone().add(item);
			return true;
		}
	}

	

	/**
	 * Tries to equip one unit of an item in the given slot. Note: This doesn't
	 * check if it is allowed to put the given item into the given slot, e.g. it
	 * is possible to wear your helmet at your feet using this method.
	 * 
	 * @param slotName
	 *            the name of the slot
	 * @param item
	 *            the item
	 * @return true if the item can be equipped, else false
	 */
	public final boolean equip(final String slotName, final Item item) {
		
		if (equipIt(slotName, item)) {
			updateItemAtkDef();
			return true;
		}
		return false;
	}

	/**
	 * Removes a specific amount of an item from the RPEntity. The item can
	 * either be stackable or non-stackable. The units can be distributed over
	 * different slots. If the RPEntity doesn't have enough units of the item,
	 * doesn't remove anything.
	 * 
	 * @param name
	 *            The name of the item
	 * @param amount
	 *            The number of units that should be dropped
	 * @return true iff dropping the desired amount was successful.
	 */
	public boolean drop(final String name, final int amount) {
		// first of all we check that this RPEntity has enough of the
		// specified item. We need to do this to ensure an atomic transaction
		// semantic later on because the required amount may be distributed
		// to several stacks.
		if (!isEquipped(name, amount)) {
			return false;
		}

		int toDrop = amount;

		for (final String slotName : Constants.CARRYING_SLOTS) {
			final RPSlot slot = getSlot(slotName);

			Iterator<RPObject> objectsIterator = slot.iterator();
			while (objectsIterator.hasNext()) {
				final RPObject object = objectsIterator.next();
				if (!(object instanceof Item)) {
					continue;
				}

				final Item item = (Item) object;

				if (!item.getName().equals(name)) {
					continue;
				}

				if (item instanceof StackableItem) {
					// The item is stackable, we try to remove
					// multiple ones.
					final int quantity = item.getQuantity();
					if (toDrop >= quantity) {
						new ItemLogger().destroy(this, slot, item);
						slot.remove(item.getID());
						toDrop -= quantity;
						// Recreate the iterator to prevent
						// ConcurrentModificationExceptions.
						// This inefficient, but simple.
						objectsIterator = slot.iterator();
					} else {
						((StackableItem) item).setQuantity(quantity - toDrop);
						new ItemLogger().splitOff(this, item, toDrop);
						toDrop = 0;
					}
				} else {
					// The item is not stackable, so we only remove a
					// single one.
					slot.remove(item.getID());
					new ItemLogger().destroy(this, slot, item);
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
	 * Removes one unit of an item from the RPEntity. The item can either be
	 * stackable or non-stackable. If the RPEntity doesn't have enough the item,
	 * doesn't remove anything.
	 * 
	 * @param name
	 *            The name of the item
	 * @return true iff dropping the item was successful.
	 */
	public boolean drop(final String name) {
		return drop(name, 1);
	}

	/**
	 * Removes the given item from the RPEntity. The item can either be
	 * stackable or non-stackable. If the RPEntity doesn't have the item,
	 * doesn't remove anything.
	 * 
	 * @param item
	 *            the item that should be removed
	 * @return true iff dropping the item was successful.
	 */
	public boolean drop(final Item item) {
		for (final String slotName : Constants.CARRYING_SLOTS) {
			final RPSlot slot = getSlot(slotName);

			final Iterator<RPObject> objectsIterator = slot.iterator();
			while (objectsIterator.hasNext()) {
				final RPObject object = objectsIterator.next();
				if (object instanceof Item) {
					if (object == item) {
						slot.remove(object.getID());
						new ItemLogger().destroy(this, slot, item);
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Determine if this entity is equipped with a minimum quantity of an item.
	 * 
	 * @param name
	 *            The item name.
	 * @param amount
	 *            The minimum amount.
	 * 
	 * @return <code>true</code> if the item is equipped with the minimum
	 *         number.
	 */
	public boolean isEquipped(final String name, final int amount) {
		if (amount <= 0) {
			return false;
		}
		int found = 0;

		for (final String slotName : Constants.CARRYING_SLOTS) {
			final RPSlot slot = getSlot(slotName);

			for (final RPObject object : slot) {
				if (!(object instanceof Item)) {
					continue;
				}

				final Item item = (Item) object;

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

	/**
	 * Determine if this entity is equipped with an item.
	 * 
	 * @param name
	 *            The item name.
	 * 
	 * @return <code>true</code> if the item is equipped.
	 */
	public boolean isEquipped(final String name) {
		return isEquipped(name, 1);
	}

	/**
	 * Gets the number of items of the given name that are carried by the
	 * RPEntity. The item can either be stackable or non-stackable.
	 * 
	 * @param name
	 *            The item's name
	 * @return The number of carried items
	 */
	public int getNumberOfEquipped(final String name) {
		int result = 0;

		for (final String slotName : Constants.CARRYING_SLOTS) {
			final RPSlot slot = getSlot(slotName);

			for (final RPObject object : slot) {
				if (object instanceof Item) {
					final Item item = (Item) object;
					if (item.getName().equals(name)) {
						result += item.getQuantity();
					}
				}
			}
		}
		return result;
	}


	/**
	 * Gets the number of items of the given name including bank.
	 * The item can either be stackable or non-stackable.
	 * 
	 * @param name
	 *            The item's name
	 * @return The number of carried items
	 */
	public int getTotalNumberOf(final String name) {
		int result = 0;

		for (final RPSlot slot : slots()) {
			for (final RPObject object : slot) {
				if (object instanceof Item) {
					final Item item = (Item) object;
					if (item.getName().equals(name)) {
						result += item.getQuantity();
					}
				}
			}
		}
		return result;
	}

	/**
	 * Gets an item that is carried by the RPEntity. If the item is stackable,
	 * gets all that are on the first stack that is found.
	 * 
	 * @param name
	 *            The item's name
	 * @return The item, or a stack of stackable items, or null if nothing was
	 *         found
	 */
	public Item getFirstEquipped(final String name) {
		for (final String slotName : Constants.CARRYING_SLOTS) {
			final RPSlot slot = getSlot(slotName);

			for (final RPObject object : slot) {
				if (object instanceof Item) {
					final Item item = (Item) object;
					if (item.getName().equals(name)) {
						return item;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Gets an item that is carried by the RPEntity. If the item is stackable,
	 * gets all that are on the first stack that is found.
	 * 
	 * @param name
	 *            The item's name
	 * @return The item, or a stack of stackable items, or null if nothing was
	 *         found
	 */
	public List<Item> getAllEquipped(final String name) {
		final List<Item> result = new LinkedList<Item>();

		for (final String slotName : Constants.CARRYING_SLOTS) {
			final RPSlot slot = getSlot(slotName);

			for (final RPObject object : slot) {
				if (object instanceof Item) {
					final Item item = (Item) object;
					if (item.getName().equals(name)) {
						result.add(item);
					}
				}
			}
		}
		return result;
	}

	/**
	 * checks if an item of class <i>clazz</i> is equipped in slot <i>slot</i>
	 * returns true if it is, else false.
	 * 
	 * @param slot
	 * @param clazz
	 * @return true if so false otherwise
	 */
	public boolean isEquippedItemClass(final String slot, final String clazz) {
		if (hasSlot(slot)) {
			// get slot if the this entity has one
			final RPSlot rpslot = getSlot(slot);
			// traverse all slot items
			for (final RPObject item : rpslot) {
				if ((item instanceof Item) && ((Item) item).isOfClass(clazz)) {
					return true;
				}
			}
		}
		// no slot, free slot or wrong item type
		return false;
	}

	/**
	 * Finds the first item of class <i>clazz</i> from the slot.
	 * 
	 * @param slot
	 * @param clazz
	 * @return the item or <code>null</code> if there is no item with the
	 *         requested clazz.
	 */
	public Item getEquippedItemClass(final String slot, final String clazz) {
		if (hasSlot(slot)) {
			// get slot if the this entity has one
			final RPSlot rpslot = getSlot(slot);
			// traverse all slot items
			for (final RPObject object : rpslot) {
				// is it the right type
				if (object instanceof Item) {
					final Item item = (Item) object;
					if (item.isOfClass(clazz)) {
						return item;
					}
				}
			}
		}
		// no slot, free slot or wrong item type
		return null;
	}

	/**
	 * Gets the weapon that this entity is holding in its hands.
	 * 
	 * @return The weapon, or null if this entity is not holding a weapon. If
	 *         the entity has a weapon in each hand, returns the weapon in its
	 *         left hand.
	 */
	public Item getWeapon() {

		final String[] weaponsClasses = { "club", "sword", "axe", "ranged",
				"missile" };

		for (final String weaponClass : weaponsClasses) {
			final String[] slots = { "lhand", "rhand" };
			for (final String slot : slots) {
				final Item item = getEquippedItemClass(slot, weaponClass);
				if (item != null) {
					return item;
				}
			}
		}
		return null;
	}

	public List<Item> getWeapons() {

		final List<Item> weapons = new ArrayList<Item>();
		Item weaponItem = getWeapon();
		if (weaponItem != null) {
			weapons.add(weaponItem);

			// pair weapons
			if (weaponItem.getName().startsWith("l hand ")) {
				// check if there is a matching right-hand weapon in
				// the other hand.
				final String rpclass = weaponItem.getItemClass();
				weaponItem = getEquippedItemClass("rhand", rpclass);
				if ((weaponItem != null)
						&& (weaponItem.getName().startsWith("r hand "))) {
					weapons.add(weaponItem);
				} else {
					// You can't use a left-hand weapon without the matching
					// right-hand weapon. Hmmm... but why not?
					weapons.clear();
				}
			} else {
				// You can't hold a right-hand weapon with your left hand, for
				// ergonomic reasons ;)
				if (weaponItem.getName().startsWith("r hand ")) {
					weapons.clear();
				}
			}
		}
		return weapons;
	}

	/**
	 * Gets the range weapon (bow etc.) that this entity is holding in its
	 * hands.
	 * 
	 * @return The range weapon, or null if this entity is not holding a range
	 *         weapon. If the entity has a range weapon in each hand, returns
	 *         one in its left hand.
	 */
	public Item getRangeWeapon() {
		for (final Item weapon : getWeapons()) {
			if (weapon.isOfClass("ranged")) {
				return weapon;
			}
		}
		return null;
	}

	/**
	 * Gets the stack of ammunition (arrows or similar) that this entity is
	 * holding in its hands.
	 * 
	 * @return The ammunition, or null if this entity is not holding ammunition.
	 *         If the entity has ammunition in each hand, returns the ammunition
	 *         in its left hand.
	 */
	public StackableItem getAmmunition() {
		final String[] slots = { "lhand", "rhand" };

		for (final String slot : slots) {
			final StackableItem item = (StackableItem) getEquippedItemClass(
					slot, "ammunition");
			if (item != null) {
				return item;
			}
		}
		return null;
	}

	/**
	 * Gets the stack of missiles (spears or similar) that this entity is
	 * holding in its hands, but only if it is not holding another, non-missile
	 * weapon in the other hand.
	 * 
	 * You can only throw missiles while you're not holding another weapon. This
	 * restriction is a workaround because of the way attack strength is
	 * determined; otherwise, one could increase one's spear attack strength by
	 * holding an ice sword in the other hand.
	 * 
	 * @return The missiles, or null if this entity is not holding missiles. If
	 *         the entity has missiles in each hand, returns the missiles in its
	 *         left hand.
	 */
	public StackableItem getMissileIfNotHoldingOtherWeapon() {
		StackableItem missileWeaponItem = null;
		boolean holdsOtherWeapon = false;

		for (final Item weaponItem : getWeapons()) {
			if (weaponItem.isOfClass("missile")) {
				missileWeaponItem = (StackableItem) weaponItem;
			} else {
				holdsOtherWeapon = true;
			}
		}
		if (holdsOtherWeapon) {
			return null;
		} else {
			return missileWeaponItem;
		}
	}

	/** @return true if the entity has an item of class shield equipped. */
	public boolean hasShield() {
		return isEquippedItemClass("lhand", "shield")
				|| isEquippedItemClass("rhand", "shield");
	}

	public Item getShield() {
		final Item item = getEquippedItemClass("lhand", "shield");
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

	@Override
	public String describe() {
		String text = super.describe();
		if (getLevel() > 0) {
			text += " It is level " + getLevel() + ".";
		}
		return text;
	}

	/**
	 * Sends a message that only this RPEntity can read. In this default
	 * implementation, this method does nothing; it can be overridden in
	 * subclasses.
	 * 
	 * @param text
	 *            The message.
	 */
	public void sendPrivateText(final String text) {
		// does nothing in this implementation.
	}

	public float getItemAtk() {
		int weapon = 0;
		final List<Item> weapons = getWeapons();
		for (final Item weaponItem : weapons) {
			weapon += weaponItem.getAttack();
		}

		// range weapons
		StackableItem ammunitionItem = null;
		if (weapons.size() > 0) {
			if (weapons.get(0).isOfClass("ranged")) {
				ammunitionItem = getAmmunition();

				if (ammunitionItem != null) {
					weapon += ammunitionItem.getAttack();
				} else {
					// If there is no ammunition...
					weapon = 0;
				}
			}
		}

		return weapon;
	}

	public float getItemDef() {
		int shield = 0;
		int armor = 0;
		int helmet = 0;
		int legs = 0;
		int boots = 0;
		int cloak = 0;
		int weapon = 0;

		Item item;
		
		if (hasShield()) {
			item = getShield();
			shield = (int) (item.getDefense() / getItemLevelModifier(item));
		}

		if (hasArmor()) {
			item = getArmor();
			armor = (int) (item.getDefense() / getItemLevelModifier(item));
		}

		if (hasHelmet()) {
			item = getHelmet();
			helmet = (int) (item.getDefense() / getItemLevelModifier(item));
		}

		if (hasLegs()) {
			item = getLegs();
			legs = (int) (item.getDefense() / getItemLevelModifier(item));
		}

		if (hasBoots()) {
			item = getBoots();
			boots = (int) (item.getDefense() / getItemLevelModifier(item));
		}

		if (hasCloak()) {
			item = getCloak();
			cloak = (int) (item.getDefense() / getItemLevelModifier(item));
		}

		final List<Item> targetWeapons = getWeapons();
		for (final Item weaponItem : targetWeapons) {
			weapon += weaponItem.getDefense() / getItemLevelModifier(weaponItem);
		}

		return SHIELD_DEF_MULTIPLIER * shield + ARMOR_DEF_MULTIPLIER * armor
				+ CLOAK_DEF_MULTIPLIER * cloak + HELMET_DEF_MULTIPLIER * helmet
				+ LEG_DEF_MULTIPLIER * legs + BOOTS_DEF_MULTIPLIER * boots
				+ WEAPON_DEF_MULTIPLIER * weapon;
	}

	/**
	 * Recalculates item based atk and def.
	 */
	public void updateItemAtkDef() {
		put("atk_item", ((int) getItemAtk()));
		put("def_item", ((int) getItemDef()));
		notifyWorldAboutChanges();
	}

	/**
	 * Can this entity do a distance attack on the given target?
	 * 
	 * @param target
	 * 
	 * @return true if this entity is armed with a distance weapon and if the
	 *         target is in range.
	 */
	public boolean canDoRangeAttack(final RPEntity target) {
		final int maxRange = getMaxRangeForArcher();
		// there's at least one square between, and the target's in range
		return (squaredDistance(target) >= 1)
				&& (squaredDistance(target) <= maxRange * maxRange);
	}

	private int getMaxRangeForArcher() {
		final Item rangeWeapon = getRangeWeapon();
		final StackableItem ammunition = getAmmunition();
		final StackableItem missiles = getMissileIfNotHoldingOtherWeapon();
		int maxRange;
		if ((rangeWeapon != null) && (ammunition != null)
				&& (ammunition.getQuantity() > 0)) {
			maxRange = rangeWeapon.getInt("range") + ammunition.getInt("range");
		} else if ((missiles != null) && (missiles.getQuantity() > 0)) {
			maxRange = missiles.getInt("range");
		} else {
			// The entity doesn't hold the necessary distance weapons.
			maxRange = 0;
		}
		return maxRange;
	}

	/**
	 * Gets this RPEntity's outfit.
	 * 
	 * Note: some RPEntities (e.g. sheep, many NPC's, all monsters) don't use
	 * the outfit system.
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
	 * Note: some RPEntities (e.g. sheep, many NPC's, all monsters) don't use
	 * the outfit system.
	 * 
	 * @param outfit
	 *            The new outfit.
	 */
	public void setOutfit(final Outfit outfit) {
		put("outfit", outfit.getCode());
	}

	/**
	 * Set the entity's formatted title.
	 * 
	 * @param title
	 *            The title, or <code>null</code>.
	 */
	public void setTitle(final String title) {
		if (title != null) {
			put(ATTR_TITLE, title);
		} else if (has(ATTR_TITLE)) {
			remove(ATTR_TITLE);
		}
	}

	//
	// Entity
	//

	/**
	 * Returns the name or something that can be used to identify the entity for
	 * the player.
	 * 
	 * @param definite
	 *            <code>true</code> for "the", and <code>false</code> for "a/an"
	 *            in case the entity has no name.
	 * 
	 * @return The description name.
	 */
	@Override
	public String getDescriptionName(final boolean definite) {
		if (name != null) {
			return name;
		} else {
			return super.getDescriptionName(definite);
		}
	}

	/**
	 * Get the nicely formatted entity title/name.
	 * 
	 * @return The title, or <code>null</code> if unknown.
	 */
	@Override
	public String getTitle() {
		if (has(ATTR_TITLE)) {
			return get(ATTR_TITLE);
		} else if (name != null) {
			return name;
		} else {
			return super.getTitle();
		}
	}

	/**
	 * Perform cycle logic.
	 */
	public abstract void logic();

	/**
	 * Chooses randomly if this has hit the defender, or if this missed him.
	 * Note that, even if this method returns true, the damage done might be 0
	 * (if the defender blocks the attack).
	 * 
	 * @param defender
	 *            The attacked RPEntity.
	 * @return true if the attacker has hit the defender (the defender may still
	 *         block this); false if the attacker has missed the defender.
	 */
	public boolean canHit(final RPEntity defender) {
		int roll = Rand.roll1D20();
		final int defenderDEF = defender.getDEF();
		final int attackerATK = this.getATK();

		/*
		 * Use some karma unless attacker is much stronger than defender, in
		 * which case attacker doesn't need luck to help him hit.
		 */
		final int levelDifferenceToNotNeedKarmaAttacking = (int) (IGNORE_KARMA_MULTIPLIER * getLevel());
		if (!(getLevel() - levelDifferenceToNotNeedKarmaAttacking > defender
				.getLevel())) {
			final double karma = this.useKarma(0.1);
			// the karma effect must be cast to an integer to affect the roll 
			// but in most cases this means the karma use was lost. so multiply by 2 to 
			// make the same amount of karma use be more useful
			final double karmaEffect = roll * karma * 2.0;
			roll -= (int) karmaEffect;
		}
		int risk = calculateRiskForCanHit(roll, defenderDEF, attackerATK);

		if (logger.isDebugEnabled()) {
			logger.debug("attack from " + this + " to " + defender
					+ ": Risk to strike: " + risk);
		}

		if (risk < 0) {
			risk = 0;
		}

		if (risk > 1) {
			risk = 1;
		}

		return (risk != 0);
	}

	int calculateRiskForCanHit(final int roll, final int defenderDEF,
			final int attackerATK) {
		return 20 * attackerATK - roll * defenderDEF;
	}

	/**
	 * Returns the attack rate, the lower the better.
	 * 
	 * @return the attack rate
	 */
	public int getAttackRate() {

		final List<Item> weapons = getWeapons();

		if (weapons.isEmpty()) {
			return 5;
		}
		int best = weapons.get(0).getAttackRate();
		for (final Item weapon : weapons) {
			final int res = weapon.getAttackRate();
			if (res < best) {
				best = res;
			}
		}
		
		// Level effect
		best = (int) Math.ceil(best * getItemLevelModifier(weapons.get(0)));
		
		return best;
	}
	
	/**
	 * Get a modifier to be used when an item has a higher min_level
	 * than the entity's level. For any item where the entity's level
	 * is high enough, the modifier is 1. For anything else, > 1 depending
	 * on the ratio between the required, and the possessed level.
	 * 
	 * @param item the item to be examined
	 * @return modifier for item properties
	 */
	private double getItemLevelModifier(Item item) {
		final String minLevelS = item.get("min_level");
		
		if (minLevelS != null) {
			final int minLevel = Integer.parseInt(minLevelS);
			final int level = getLevel();
			if (minLevel > level) {
				return 1 - Math.log(((double) level + 1) / (minLevel + 1));
			}
		}
		
		return 1.0;
	}

	/**
	 * Lets the attacker attack its target.
	 * 
	 * @return true iff the attacker has done damage to the defender.
	 * 
	 */
	public boolean attack() {
		boolean result = false;
		final RPEntity defender = this.getAttackTarget();
		// isInZoneandNotDead(defender);

		defender.rememberAttacker(this);

		if (this.canHit(defender)) {
			defender.applyDefXP(this);

			int damage = this.damageDone(defender);

			if (damage > 0) {

				// limit damage to target HP
				damage = Math.min(damage, defender.getHP());
				this.handleLifesteal(this, this.getWeapons(), damage);

				defender.onDamaged(this, damage);
				logger.debug("attack from " + this.getID() + " to "
						+ defender.getID() + ": Damage: " + damage);

				result = true;
			} else {
				// The attack was too weak, it was blocked
				logger.debug("attack from " + this.getID() + " to "
						+ defender.getID() + ": Damage: " + 0);
			}
			addEvent(new AttackEvent(true, damage, getDamageType()));
		} else {
			// Missed
			logger.debug("attack from " + this.getID() + " to "
					+ defender.getID() + ": Missed");
			addEvent(new AttackEvent(false, 0, getDamageType()));
		}

		this.notifyWorldAboutChanges();

		return result;
	}

	protected void applyDefXP(final RPEntity entity) {
		// implemented in sub classes
	}

	/**
	 * Calculate lifesteal and update hp of source.
	 * 
	 * @param attacker
	 *            the RPEntity doing the hit
	 * @param attackerWeapons
	 *            the weapons of the RPEntity doing the hit
	 * @param damage
	 *            the damage done by this hit.
	 */
	public void handleLifesteal(final RPEntity attacker,
			final List<Item> attackerWeapons, final int damage) {

		// Calculate the lifesteal value based on the configured factor
		// In case of a lifesteal weapon used together with a non-lifesteal
		// weapon,
		// weight it based on the atk-values of the weapons.
		float sumAll = 0;
		float sumLifesteal = 0;

		// Creature with lifesteal profile?
		if (attacker instanceof Creature) {
			sumAll = 1;
			final String value = ((Creature) attacker)
					.getAIProfile("lifesteal");
			if (value == null) {
				// The creature doesn't steal life.
				return;
			}
			sumLifesteal = Float.parseFloat(value);
		} else {
			// weapons with lifesteal attribute for players
			for (final Item weaponItem : attackerWeapons) {
				sumAll += weaponItem.getAttack();
				if (weaponItem.has("lifesteal")) {
					sumLifesteal += weaponItem.getAttack()
							* weaponItem.getDouble("lifesteal");
				}
			}
		}

		// process the lifesteal
		if (sumLifesteal != 0) {
			// 0.5f is used for rounding
			final int lifesteal = (int) (damage * sumLifesteal / sumAll + 0.5f);

			if (lifesteal >= 0) {
				attacker.heal(lifesteal, true);
			} else {
				/*
				 * Negative lifesteal means that we hurt ourselves.
				 */
				attacker.damage(-lifesteal, attacker);
			}

			attacker.notifyWorldAboutChanges();
		}
	}
	
	/**
	 * Equips the item in the specified slot. 
	 * 
	 * @param slotName 
	 * @param item 
	 * @return true if successful*/
	private boolean equipIt(final String slotName, final Item item) {
		if (!hasSlot(slotName)) {
			return false;
		}

		final RPSlot rpslot = getSlot(slotName);

		if (item instanceof StackableItem) {
			final StackableItem stackEntity = (StackableItem) item;
			// find a stackable item of the same type
			for (final RPObject object : rpslot) {
				if (object instanceof StackableItem) {
					// found another stackable
					final StackableItem other = (StackableItem) object;
					if (other.isStackable(stackEntity)) {
						// other is the same type...merge them
						new ItemLogger().merge(this, stackEntity, other);
						other.add(stackEntity);
						updateItemAtkDef();
						return true;
					}
				}
			}
		}

		// We can't stack it on another item. Check if we can simply
		// add it to an empty cell.
		if (rpslot.isFull()) {
			return false;
		} else {
			rpslot.add(item);
			updateItemAtkDef();
			return true;
		}
	}
}
