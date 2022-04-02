/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2022 - Marauroa                    *
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

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import games.stendhal.common.EquipActionConsts;
import games.stendhal.common.Level;
import games.stendhal.common.NotificationType;
import games.stendhal.common.Rand;
import games.stendhal.common.constants.Nature;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.constants.Testing;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.WordList;
import games.stendhal.server.actions.equip.DropAction;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.ItemLogger;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.db.StendhalKillLogDAO;
import games.stendhal.server.core.engine.dbcommand.LogKillEventCommand;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TutorialNotifier;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.item.CaptureTheFlagFlag;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.npc.TrainingDummy;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.EntitySlot;
import games.stendhal.server.entity.slot.Slots;
import games.stendhal.server.entity.status.Status;
import games.stendhal.server.entity.status.StatusAttacker;
import games.stendhal.server.entity.status.StatusList;
import games.stendhal.server.entity.status.StatusType;
import games.stendhal.server.events.AttackEvent;
import games.stendhal.server.events.SoundEvent;
import games.stendhal.server.events.TextEvent;
import games.stendhal.server.util.CounterMap;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.SyntaxException;
import marauroa.server.db.command.DBCommandPriority;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.game.Statistics;
import marauroa.server.game.db.DAORegister;

public abstract class RPEntity extends CombatEntity {
	/**
	 * The title attribute name.
	 */
	protected static final String ATTR_TITLE = "title";
	private static final float WEAPON_DEF_MULTIPLIER = 4.0f;
	private static final float BOOTS_DEF_MULTIPLIER = 1.0f;
	private static final float LEG_DEF_MULTIPLIER = 1.0f;
	private static final float HELMET_DEF_MULTIPLIER = 1.0f;
	private static final float CLOAK_DEF_MULTIPLIER = 1.5f;
	private static final float ARMOR_DEF_MULTIPLIER = 2.0f;
	private static final float SHIELD_DEF_MULTIPLIER = 4.0f;
	private static final float RING_DEF_MULTIPLIER = 1.0f;
	/**
	 * To prevent players from gaining attack and defense experience by fighting
	 * against very weak creatures, they only gain atk and def xp for so many
	 * turns after they have actually been damaged by the enemy. //
	 */
	private static final int TURNS_WHILE_FIGHT_XP_INCREASES = 12;
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(RPEntity.class);
	private static Statistics stats;

	private String name;
	protected int atk;
	private int atk_xp;
	protected int def;
	private int def_xp;
	protected int ratk;
	private int ratk_xp;
	private int base_hp;
	private int hp;
	protected int lv_cap;
	private int xp;
	protected int level;
	private int mana;
	private int base_mana;

	private String deathSound;
	private String bloodClass;

	/** Entity uses a status attack */
	protected ImmutableList<StatusAttacker> statusAttackers = ImmutableList.of();
	/** a list of current statuses */
	protected StatusList statusList;
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
	protected CounterMap<Entity> damageReceived;
	protected int totalDamageReceived;

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

	/**
	 * A helper class for building a size limited list of killer names. If there
	 * are more killers than the limit, then "others" is set as the last killer.
	 * Only living creatures and online players are included in the killer name
	 * list. RPEntities on other zones are not included.
	 */
	private class KillerList {
		/** Maximum amount of killer names. */
		private static final int MAX_SIZE = 10;
		/** List of killer names. */
		private final LinkedList<String> list = new LinkedList<>();
		/**
		 * A flag for detecting when the killer list has grown over the
		 * maximum size.
		 */
		private boolean more;

		/**
		 * Add an entity to the killer list.
		 *
		 * @param e entity
		 */
		void addEntity(Entity e) {
			if (e instanceof RPEntity) {
				// Only list the killers on the zone where the death happened.
				if (e.getZone() != getZone()) {
					return;
				}
				// Try to keep player names at the start of the list
				if (e instanceof Player) {
					if (((Player) e).isDisconnected()) {
						return;
					}
					list.addFirst(e.getName());
				} else {
					if (((RPEntity) e).getHP() <= 0) {
						return;
					}
					list.add(e.getName());
				}
			} else {
				list.add(e.getName());
			}
			trim();
		}

		/**
		 * Set the official killer. If the killer was already on the list, move
		 * it first. Otherwise prepend the list with the official killer. This
		 * means that a creature can appear before players if it is the official
		 * killer. Also an item "poison" can be the first on the list this way.
		 * (And, as of this writing (2015-04-01) it is the only way anything but
		 * RPEntities can be shown on the killer list).
		 *
		 * @param killer The official killer
		 */
		void setKiller(String killer) {
			if (list.contains(killer)) {
				list.remove(killer);
				list.addFirst(killer);
			} else {
				list.addFirst(killer);
				trim();
			}
		}

		/**
		 * Keep the name list at most {@link #MAX_SIZE}.
		 */
		private void trim() {
			if (list.size() > MAX_SIZE) {
				list.remove(list.size() - 1);
				more = true;
			}
		}

		/**
		 * Get the name list of the added entities.
		 *
		 * @return name list.
		 */
		List<String> asList() {
			if (more) {
				list.set(list.size() - 1, "others");
			}
			return Collections.unmodifiableList(list);
		}
	}

	@Override
	protected boolean handlePortal(final Portal portal) {
		if (isZoneChangeAllowed()) {
			if (logger.isDebugEnabled() || Testing.DEBUG) {
				logger.debug("Using portal " + portal);
			}

			return portal.onUsed(this);
		}
		return super.handlePortal(portal);
	}

    public static void generateRPClass() {
        try {
            stats = Statistics.getStatistics();
            RPEntityRPClass.generateRPClass(ATTR_TITLE);
        } catch (final SyntaxException e) {
            logger.error("cannot generateRPClass", e);
        }
    }

	public RPEntity(final RPObject object) {
		super(object);
		attackSources = new ArrayList<>();
		damageReceived = new CounterMap<>(true);
		enemiesThatGiveFightXP = new WeakHashMap<>();
		totalDamageReceived = 0;
	}

	public RPEntity() {
		super();
		attackSources = new ArrayList<>();
		damageReceived = new CounterMap<>(true);
		enemiesThatGiveFightXP = new WeakHashMap<>();
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

	/**
	 * Give mana to the entity.
	 *
	 * @param mana
	 * 			The amount of mana to add/substract.
	 * @param tell
	 * 			Whether to tell the entity that mana has been added.
	 *
	 * @return Amount of mana actually refilled.
	 */
	public int addMana(int mana, boolean tell) {
		int old_mana = getMana();
		int new_mana = old_mana + mana;
		int given = 0;

		// no negative mana
		new_mana = Math.max(new_mana, 0);

		// maximum is base_mana
		new_mana = Math.min(new_mana, getBaseMana());

		given = new_mana - old_mana;

		if(tell) {
			//TODO: Add notification for increased mana
		}

		setMana(new_mana);

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

		if (has("atk_xp")) {
			atk_xp = getInt("atk_xp");
			setAtkXpInternal(atk_xp, false);
		}

		if (has("def_xp")) {
			def_xp = getInt("def_xp");
			setDefXpInternal(def_xp, false);
		}

		if (Testing.COMBAT && has("ratk_xp")) {
			ratk_xp = getInt("ratk_xp");
			setRatkXPInternal(ratk_xp, false);
		}

		if (has("base_hp")) {
			base_hp = getInt("base_hp");
		}
		if (has("hp")) {
			hp = getInt("hp");
		}

		if (has("lv_cap")) {
			lv_cap = getInt("lv_cap");
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
			base_mana = getInt("base_mana");
		}
		if (has("base_speed")) {
			setBaseSpeed(getDouble("base_speed"));
		}
	}

	/**
	 * Register the new name in the conversation parser word list.
	 *
	 * @param newName
	 * @param oldName
	 */
	private static void registerNewName(final String newName, final String oldName) {
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
	 * @param attackingWeaponsValue
	 * 			  ATK-value of all attacking weapons/spells
	 * @param damageType nature of damage
	 * @param isRanged <code>true</code> if this is a ranged attack, otherwise
	 * 	<code>false</code>
	 * @param maxRange maximum range of a ranged attack
	 *
	 * @return The number of hitpoints that the target should lose. 0 if the
	 *         attack was completely blocked by the defender.
	 */
	int damageDone(RPEntity defender, double attackingWeaponsValue, Nature damageType,
			boolean isRanged, int maxRange) {
		// Don't start from 0 to mitigate weird behaviour at very low levels
		final int effectiveAttackerLevel = getLevel() + 5;
		final int effectiveDefenderLevel = defender.getLevel() + 5;

		// Defending side
		final double armor = defender.getItemDef();
		final int targetDef = defender.getCappedDef();
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

		// using karma here decreases damage done by enemy
		if (!(effectiveDefenderLevel - levelDifferenceToNotNeedKarmaDefending  > effectiveAttackerLevel)) {
			defence += defence * defender.useKarma(0.1);
		}

		/* Attacking with ranged weapon uses a separate strength value.
		 *
		 * XXX: atkStrength never used outside of debugger.
		 */
		final int atkStrength, sourceAtk;
		if (Testing.COMBAT && isRanged) {
			atkStrength = this.getRatk();
			sourceAtk = this.getCappedRatk();
		} else {
			atkStrength = this.getAtk();
			sourceAtk = this.getCappedAtk();
		}

		// Attacking
		if (logger.isDebugEnabled() || Testing.DEBUG) {
			logger.debug("attacker has " + atkStrength + " (" + getCappedAtk()
					+ ") and uses a weapon of " + getItemAtk());
		}

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

		final double weaponComponent = 1.0 + attackingWeaponsValue;
		// XXX: Is correct to use sourceAtk here instead of atkStrength?
		final double maxAttack = sourceAtk * weaponComponent
				* (1 + LEVEL_ATK * effectiveAttackerLevel) * speedEffect;
		double attack = Rand.rand() * maxAttack;

		/*
		 * Account for karma (+/-10%) But, don't need luck to help you attack if
		 * you're a much higher level than what you attack
		 */
		final int levelDifferenceToNotNeedKarmaAttacking = (int) (IGNORE_KARMA_MULTIPLIER * getLevel());

		// using karma here increases damage to enemy
		if (!(effectiveAttackerLevel - levelDifferenceToNotNeedKarmaAttacking > effectiveDefenderLevel)) {
			attack += attack * useKarma(0.1);
		}

		if (logger.isDebugEnabled() || Testing.DEBUG) {
			logger.debug("DEF MAX: " + maxDefence + "\t DEF VALUE: " + defence);
		}

		// Apply defense and damage type effect
		int damage = (int) (defender.getSusceptibility(damageType)
				* (WEIGHT_ATK * attack - defence) / maxDefence);

		/* FIXME: Can argument be removed and just use
		 *        RPEntity.usingRangedAttack() here?
		 */
		if (isRanged) {
			// The attacker is attacking either using a range weapon with
			// ammunition such as a bow and arrows, or a missile such as a
			// spear.
			damage = applyDistanceAttackModifiers(damage,
					squaredDistance(defender), maxRange);
		}

		return damage;
	}

	/**
	 * Is called when this has hit the given defender. Determines how much
	 * hitpoints the defender will lose, based on this's ATK experience and
	 * weapon(s), the defender's DEF experience and defensive items, and a
	 * random generator.
	 *
	 * @param defender
	 *            The defender.
	 * @param attackingWeaponsValue
	 * 			  ATK-value of all attacking weapons/spells
	 * @param damageType nature of damage
	 * @return The number of hitpoints that the target should lose. 0 if the
	 *         attack was completely blocked by the defender.
	 */
	public int damageDone(final RPEntity defender, double attackingWeaponsValue, Nature damageType) {
		final int maxRange = getMaxRangeForArcher();
		boolean isRanged = ((maxRange > 0) && canDoRangeAttack(defender, maxRange));

		return damageDone(defender, attackingWeaponsValue, damageType, isRanged, maxRange);
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
	 * @param maxrange maximum attack range
	 * @return The damage that will be done with the distance attack.
	 */
	public static int applyDistanceAttackModifiers(final int damage,
			final double squareDistance, final double maxrange) {
		final double maxRangeSquared = maxrange * maxrange;
		if (maxRangeSquared < squareDistance) {
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
	@Override
	public String getName() {
		if (name != null) {
			return name;
		}
		return super.getName();
	}

	@Override
	public void onAdded(final StendhalRPZone zone) {
		super.onAdded(zone);
		this.updateItemAtkDef();
	}


	public void setLevel(final int level) {
		this.level = level;
		put("level", level);
		this.updateModifiedAttributes();
	}

	public int getLevel() {
		return this.level;
	}

	public void setAtk(final int atk) {
		setAtkInternal(atk, true);
	}

	protected void setAtkInternal(final int atk, boolean notify) {
		this.atk = atk;
		put("atk", atk);  // visible atk
		if(notify) {
			this.updateModifiedAttributes();
		}
	}

	public int getAtk() {
		return this.atk;
	}

	/**
	 * gets the capped atk level, which prevent players from training their atk way beyond what is reasonable for their level
	 *
	 * @return capped atk
	 */
	public int getCappedAtk() {
		return this.atk;
	}

	/**
	 * Set attack XP.
	 *
	 * @param atk the new value
	 */
	public void setAtkXP(final int atk) {
		setAtkXpInternal(atk, true);
	}

	private void setAtkXpInternal(final int atk, boolean notify) {
		this.atk_xp = atk;
		put("atk_xp", atk_xp);

		// Handle level changes
		final int newLevel = Level.getLevel(atk_xp);
		final int levels = newLevel - (this.atk - 10);
		if (levels != 0) {
			setAtkInternal(this.atk + levels, notify);
			new GameEvent(getName(), "atk", Integer.toString(getAtk())).raise();
		}
	}

	/**
	 * Adjust entity's ATK XP by specified amount.
	 *
	 * @param xp
	 * 		Amount to add.
	 */
	public void addAtkXP(final int xp) {
		setAtkXP(getAtkXP() + xp);
	}

	public int getAtkXP() {
		return atk_xp;
	}

	/**
	 * Increase attack XP by 1.
	 */
	public void incAtkXP() {
		setAtkXP(atk_xp + 1);
	}

	public void setDef(final int def) {
		setDefInternal(def, true);
	}

	protected void setDefInternal(final int def, boolean notify) {
		this.def = def;
		put("def", def);  // visible def
		if(notify) {
			this.updateModifiedAttributes();
		}
	}

	public int getDef() {
		return this.def;
	}

	/**
	 * gets the capped def level, which prevent players from training their def way beyond what is reasonable for their level
	 *
	 * @return capped def
	 */
	public int getCappedDef() {
		return this.def;
	}

	/**
	 * Set defense XP.
	 *
	 * @param defXp the new value
	 */
	public void setDefXP(final int defXp) {
		setDefXpInternal(defXp, true);
	}

	private void setDefXpInternal(final int defXp, boolean notify) {
		this.def_xp = defXp;
		put("def_xp", def_xp);

		// Handle level changes
		final int newLevel = Level.getLevel(def_xp);
		final int levels = newLevel - (this.def - 10);
		if (levels != 0) {
			setDefInternal(this.def + levels, notify);
			new GameEvent(getName(), "def", Integer.toString(this.def)).raise();
		}
	}

	/**
	 * Adjust entity's DEF XP by specified amount.
	 *
	 * @param xp
	 * 		Amount to add.
	 */
	public void addDefXP(final int xp) {
		setDefXP(getDefXP() + xp);
	}

	public int getDefXP() {
		return def_xp;
	}

	/**
	 * Increase defense XP by 1.
	 */
	public void incDefXP() {
		setDefXP(def_xp + 1);
	}


/* ### --- START RANGED --- ### */

	/**
	 * Set the value of the entity's ranged attack level.
	 *
	 * @param ratk
	 * 		Integer value representing new ranged attack level
	 */
	public void setRatk(final int ratk) {
		setRatkInternal(ratk, true);
	}

	/**
	 * Set the entity's ranged attack level.
	 *
	 * @param ratk
	 * 		Integer value representing new ranged attack level
	 * @param notify
	 * 		Update stat in real-time
	 */
	protected void setRatkInternal(final int ratk, boolean notify) {
		this.ratk = ratk;
		put("ratk", ratk);  // visible ratk
		if(notify) {
			this.updateModifiedAttributes();
		}
	}

	/**
	 * Gets the entity's current ranged attack level.
	 *
	 * @return
	 * 		Integer value of ranged attack level
	 */
	public int getRatk() {
		return this.ratk;
	}

	/**
	 * gets the capped ranged attack level which prevents players from training
	 * ratk way beyond what is reasonable for their level.
	 *
	 * @return
	 * 		The maximum value player's ranged attack level can be at current
	 * 		level
	 */
	public int getCappedRatk() {
		return this.ratk;
	}

	/**
	 * Sets the entity's ranged attack experience.
	 *
	 * @param ratkXP
	 * 		Integer value of the target experience
	 */
	public void setRatkXP(final int ratkXP) {
		setRatkXPInternal(ratkXP, true);
	}

	/**
	 * Sets the entity's ranged attack experience.
	 *
	 * @param ratkXP
	 * 		Integer value of the target experience
	 * @param notify
	 * 		Update ranged attack experience in real-time
	 */
	protected void setRatkXPInternal(final int ratkXP, boolean notify) {
		this.ratk_xp = ratkXP;
		put("ratk_xp", ratk_xp);

		// Handle level changes
		final int newLevel = Level.getLevel(ratk_xp);
		final int levels = newLevel - (this.ratk - 10);

		// In case we level up several levels at a single time.
		if (levels != 0) {
			setRatkInternal(this.ratk + levels, notify);
			new GameEvent(getName(), "ratk", Integer.toString(this.ratk)).raise();
		}
	}

	/**
	 * Adjust entity's RATK XP by specified amount.
	 *
	 * @param xp
	 * 		Amount to add.
	 */
	public void addRatkXP(final int xp) {
		setRatkXP(getRatkXP() + xp);
	}

	/**
	 * Get's the entity's current ranged attack experience.
	 *
	 * @return
	 * 		Integer representation of current experience
	 */
	public int getRatkXP() {
		return ratk_xp;
	}

	/**
	 * Increase ranged XP by 1.
	 */
	public void incRatkXP() {
		setRatkXP(ratk_xp + 1);
	}

/* ### --- END RANGED --- ### */


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
		try {
			put("base_hp", newhp);
		} catch (IllegalArgumentException e) {
			logger.error("Failed to set base HP to " + newhp + ". Entity was: " + this, e);
		}
		this.updateModifiedAttributes();
	}

	/**
	 * Get the base HP.
	 *
	 * @return The current HP.
	 */
	public int getBaseHP() {
		return this.base_hp;
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
		setHpInternal(hp, true);
	}

	private void setHpInternal(final int hp, final boolean notify) {
		this.hp = hp;
		try {
			put("hp", hp);
		} catch (IllegalArgumentException e) {
			logger.error("Failed to set HP to " + hp + ". Entity was: " + this, e);
		}
		if(notify) {
			this.updateModifiedAttributes();
		}
	}

	/**
	 * Get the current HP.
	 *
	 * @return The current HP.
	 */
	public int getHP() {
		return this.hp;
	}

	/**
	 * Get the lv_cap.
	 *
	 * @return The current lv_cap.
	 */
	public int getLVCap() {
		return this.lv_cap;
	}

	/**
	 * Gets the mana (magic).
	 *
	 * @return mana
	 */
	public int getMana() {
		return this.mana;
	}

	/**
	 * Gets the base mana (like base_hp).
	 *
	 * @return base mana
	 */
	public int getBaseMana() {
		return this.base_mana;
	}

	/**
	 * Sets the available mana.
	 *
	 * @param newMana
	 *            new amount of mana
	 */
	public void setMana(final int newMana) {
		setManaInternal(newMana, true);
	}

	private void setManaInternal(final int newMana, boolean notify) {
		mana = newMana;
		put("mana", newMana);
		if(notify) {
			this.updateModifiedAttributes();
		}
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
		this.updateModifiedAttributes();
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

	public void setLVCap(final int newLVCap) {
		lv_cap = newLVCap;
		put("lv_cap", newLVCap);
		this.updateModifiedAttributes();
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
	 * Change the level to match the XP, if needed.
	 */
	protected void updateLevel() {
		final int newLevel = Level.getLevel(getXP());
		final int oldLevel = has("level") ? getInt("level") : 0;
		final int levels = newLevel - oldLevel;

		// In case we level up several levels at a single time.
		for (int i = 0; i < Math.abs(levels); i++) {
			setBaseHP(getBaseHP() + (int) Math.signum(levels) * 10);
			setHP(getBaseHP());
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

	/**
	 * Get the nature of the damage the entity inflicts in ranged attacks.
	 *
	 * @return type of damage
	 */
	protected Nature getRangedDamageType() {
		/*
		 * Default to the same as the base damage type. Entities needing more
		 * complicated behavior (ie. fire breathing dragons) should override the
		 * method.
		 */
		return getDamageType();
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
		if (enemy instanceof TrainingDummy) {
			// training dummies always give fight XP
			return true;
		}

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
	 * sets the blood class
	 *
	 * @param name name of blood class
	 */
	public final void setBlood(final String name) {
		this.bloodClass = name;
	}

	/**
	 * gets the name of the blood class
	 *
	 * @return bloodClass or <code>null</code>
	 */
	public final String getBloodClass() {
		return this.bloodClass;
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
			final Blood blood = new Blood(bloodClass);
			blood.setPosition(bx, by);

			zone.add(blood);
		}
	}

	/**
	 * return list of all droppable items in entity's hands.
	 *
	 * currently only considers items in hands.  no other part of body
	 *
	 * currently, there is only one type of droppable item - CaptureTheFlagFlag.
	 *     need some more general solution
	 *
	 * @return list of droppable items.  returns null if no droppable items found
	 */
	public List<Item> getDroppables() {
		final String[] slots = { "lhand", "rhand" };
		Stream<Item> items = Stream.of(slots).map(this::getSlot).filter(Objects::nonNull).flatMap(this::slotStream);
		return items.filter(CaptureTheFlagFlag.class::isInstance).collect(Collectors.toList());
	}

	/**
	 * Drop specified item from entity's equipment
	 *
	 * note: seems like this.drop(droppable) should work, but
	 *       the item just disappears - does not end up on ground.
	 *
	 * TODO: probably need to refactor this in to the general drop system
	 *       (maybe fixing some of the other code paths)
	 *
	 * @param droppable item to be dropped
	 */
	public void dropDroppableItem(Item droppable) {

		// note: this.drop() does not do all necessary operations -
		//       item disappears from hand, but disappears competely

		Player    player = (Player) this;
		RPObject  parent = droppable.getContainer();
		RPAction  action = new RPAction();

		action.put("type",                        "drop");
		action.put("baseitem",                    droppable.getID().getObjectID());
		action.put(EquipActionConsts.BASE_OBJECT, parent.getID().getObjectID());
		action.put(EquipActionConsts.BASE_SLOT,   droppable.getContainerSlot().getName());

		// TODO: better to drop "behind" the player, if they have been running
		action.put("x", this.getX());
		action.put("y", this.getY() + 1);

		DropAction dropAction = new DropAction();
		dropAction.onAction(player, action);

		// TODO: send message to player - you dropped ...

		this.notifyWorldAboutChanges();
	}

	/**
	 * if defender (this entity) is carrying a droppable item,
	 * then attacker and defender both roll d20, and if attacker
	 * rolls higher, the defender drops the droppable.
	 *
	 * note that separate rolls are performed for each droppable
	 * that the entity is carrying.
	 *
	 * XXX this does not belong here - should be in some Effect framework
	 *
	 * returns string - what happened.  no effect returns null
	 *
	 * @param attacker
	 * @return event description
	 */
	public String maybeDropDroppables(RPEntity attacker) {
		List<Item> droppables = getDroppables();
		if (droppables.isEmpty()) {
			return null;
		}

		for (Item droppable : droppables) {
			// roll two dice, tie goes to defender
			//   TODO: integrate skills, ctf atk/def
			int attackerRoll = Rand.roll1D20();
			int defenderRoll = Rand.roll1D20();

System.out.printf("  drop: %2d %2d\n", attackerRoll, defenderRoll);

			if (attackerRoll > defenderRoll) {
				this.dropDroppableItem(droppable);
				// XXX get description from droppable - what color, ...
				return "dropped the flag";
			}
		}
		return null;
	}

	/**
	 * This method is called when this entity has been attacked by Entity
	 * attacker and it has been damaged with damage points.
	 *
	 * @param attacker
	 * @param damage
	 */
	public void onDamaged(final Entity attacker, final int damage) {
		if (logger.isDebugEnabled() || Testing.DEBUG) {
			logger.debug("Damaged " + damage + " points by " + attacker.getID());
		}

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
		damageReceived.add(attacker, damage);

		if (leftHP > 0) {
			setHP(leftHP);
		} else {
			kill(attacker);
		}

		notifyWorldAboutChanges();
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
	private int damage(final int amount) {
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
	public int damage(final int amount, final Killer attacker) {
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
			@Override
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
	private void kill(final Entity killer) {
		setHP(0);
		SingletonRepository.getRuleProcessor().killRPEntity(this, killer);
	}

	/**
	 * For rewarding killers. Get the entity as a Player, if the entity is a
	 * Player. If the player has logged out, try to get the corresponding online
	 * player.
	 *
	 * @param entity entity to be checked
	 * @return online Player corresponding to the entity, or {@code null} if the
	 * 	entity is not a Player, or if the equivalent player is not online
	 */
	protected Player entityAsOnlinePlayer(Entity entity) {
		if (!(entity instanceof Player)) {
			return null;
		}
		Player killer = (Player) entity;
		if (killer.isDisconnected()) {
			// Try to get the corresponding online player:
			killer = SingletonRepository.getRuleProcessor().getPlayer(killer.getName());
		}
		return killer;
	}

	protected Pet entityAsPet(Entity entity) {
		if (!(entity instanceof Pet)) {
			return null;
		}
		Pet killerPet = (Pet) entity;
		/* isDisconnected is undefined in object Pet;
		if (killer.isDisconnected()) {
			// Try to get the corresponding online player:
			killer = SingletonRepository.getRuleProcessor().getPlayer(killer.getName());
		}
		*/
		return killerPet;
	}

	/**
	 * Gives XP to every player who has helped killing this RPEntity.
	 *
	 * @param oldXP
	 *            The XP that this RPEntity had before being killed.
	 */
	protected void rewardKillers(final int oldXP) {
		final int xpReward = (int) (oldXP * 0.05);

		for (Entry<Entity, Integer> entry : damageReceived.entrySet()) {
			final int damageDone = entry.getValue();
			if (damageDone == 0) {
				continue;
			}

			Player killer = entityAsOnlinePlayer(entry.getKey());
			if (killer == null) {
				continue;
			}

			TutorialNotifier.killedSomething(killer);

			if (logger.isDebugEnabled() || Testing.DEBUG) {
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

			if (logger.isDebugEnabled() || Testing.DEBUG) {
				logger.debug("OnDead: " + xpReward + "\t" + damageDone + "\t"
						+ totalDamageReceived + "\t");
			}

			int reward = xpEarn;

			// We ensure that the player gets at least 1 experience
			// point, because getting nothing lowers motivation.
			if (reward == 0) {
				reward = 1;
			}

			killer.addXP(reward);

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

			SingletonRepository.getAchievementNotifier().onKill(killer);

			killer.notifyWorldAboutChanges();
		}
	}

	/*
	 * Reward pets who kill enemies.  don't perks like AchievementNotifier that players.
	 */
	protected void rewardKillerAnimals(final int oldXP) {
		if (!System.getProperty("stendhal.petleveling", "false").equals("true")) {
			return;
		}
		final int xpReward = (int) (oldXP * 0.05);

		for (Entry<Entity, Integer> entry : damageReceived.entrySet()) {
			final int damageDone = entry.getValue();
			if (damageDone == 0) {
				continue;
			}

			Pet killer = entityAsPet(entry.getKey());
			if (killer == null) {
				continue;
			}

			if (logger.isDebugEnabled() || Testing.DEBUG) {
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

			if (logger.isDebugEnabled() || Testing.DEBUG) {
				logger.debug("OnDead: " + xpReward + "\t" + damageDone + "\t"
						+ totalDamageReceived + "\t");
			}

			int reward = xpEarn;

			// We ensure it gets at least 1 experience
			// point, because getting nothing lowers motivation.
			if (reward == 0) {
				reward = 1;
			}

			if (killer.getLevel() >= killer.getLVCap())
			{
				reward = 0;
			}

			killer.addXP(reward);

			/*
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

			SingletonRepository.getAchievementNotifier().onKill(killer);
			*/

			killer.notifyWorldAboutChanges();
		}
	}

	/**
	 * This method is called when the entity has been killed ( hp==0 ).
	 *
	 * @param killer
	 *            The entity who caused the death
	 */
	public final void onDead(final Killer killer) {
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
	public void onDead(final Killer killer, final boolean remove) {
		StendhalKillLogDAO killLog = DAORegister.get().get(StendhalKillLogDAO.class);
		String killerName = killer.getName();

		if (killer instanceof RPEntity) {
			new GameEvent(killerName, "killed", this.getName(), killLog.entityToType(killer), killLog.entityToType(this)).raise();
		}

		DBCommandQueue.get().enqueue(new LogKillEventCommand(this, killer), DBCommandPriority.LOW);

		die(killer, remove);
	}

	/**
	 * Build a list of killer names.
	 *
	 * @param killerName The "official" killer. This will be always included in
	 *	the list
	 * @return list of killers
	 */
	private List<String> buildKillerList(String killerName) {
		KillerList killers = new KillerList();

		for (Entry<Entity, Integer> entry : damageReceived.entrySet()) {
			final int damageDone = entry.getValue();
			if (damageDone == 0) {
				continue;
			}

			killers.addEntity(entry.getKey());
		}
		if (killerName != null) {
			killers.setKiller(killerName);
		}
		return killers.asList();
	}

	/**
	 * This method is called when this entity has been killed (hp == 0).
	 *
	 * @param killer the "official" killer
	 * @param remove
	 *            <code>true</code> to remove entity from world.
	 */
	private void die(Killer killer, final boolean remove) {
		StendhalRPZone zone = this.getZone();
		if ((zone == null) || !zone.has(this.getID())) {
			logger.warn("RPEntity died but is not in a zone");
			return;
		}

		String killerName = killer.getName();
		// Needs to be done while the killer map still has the contents
		List<String> killers = buildKillerList(killerName);

		final int oldXP = this.getXP();

		// Establish how much xp points your are rewarded
		// give XP to everyone who helped killing this RPEntity
		rewardKillers(oldXP);
		rewardKillerAnimals(oldXP);

		if (!(killer instanceof Player) && !(killer instanceof Status) && !(killer instanceof Pet)) {
			/*
			 * Prettify the killer name for the corpse. Should be done only
			 * after the more plain version has been used for the killer list.
			 * Players are unique, so they should not get an article. Also
			 * statuses should not, so that "killed by poison" does not become
			 * "killed by a bottle of poison".
			 */
			killerName = Grammar.a_noun(killerName);
		}
		// Add a corpse
		final Corpse corpse = makeCorpse(killerName);
		damageReceived.clear();
		totalDamageReceived = 0;

		// Stats about dead
		if (has("name")) {
			stats.add("Killed " + get("name"), 1);
		} else {
			stats.add("Killed " + get("type"), 1);
		}

		// Add some reward inside the corpse
		dropItemsOn(corpse);
		updateItemAtkDef();

		// Adding to zone clears events, so the sound needs to be added after that.
		zone.add(corpse);
		if (deathSound != null) {
			corpse.addEvent(new SoundEvent(deathSound, 23, 100, SoundLayer.FIGHTING_NOISE));
			corpse.notifyWorldAboutChanges();
		}

		StringBuilder deathMessage = new StringBuilder(getName());
		deathMessage.append(" has been killed");
		if (!killers.isEmpty()) {
			deathMessage.append(" by ");
			deathMessage.append(Grammar.enumerateCollection(killers));
		}
		corpse.addEvent(new TextEvent(deathMessage.toString()));

		// Corpse may want to know who this entity was attacking (RaidCreatureCorpse does),
		// so defer stopping.
		stopAttack();
		if (statusList != null) {
			statusList.removeAll();
		}
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

	public String getHarmlessCorpseName() {
		return "harmless_player";
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
		final List<RPEntity> list = new ArrayList<>();

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
		final RPSlot slot = getSlotToEquip(item);
		if (slot != null) {
			return equipIt(slot, item);
		} else {
			return false;
		}
	}

	/**
	 * Check if an object is a stackable item that can be merged to an existing
	 * item stack.
	 *
	 * @param item stackable item
	 * @param object merge candidate
	 * @return <code>true</code> if the items can be merged, <code>false</code>
	 * 	otherwise
	 */
	private boolean canMergeItems(StackableItem item, RPObject object) {
		if (object instanceof StackableItem) {
			final StackableItem other = (StackableItem) object;
			if (other.isStackable(item)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Find slot where an item could be merged, looking recursively inside a
	 * slot and the content slots of the items in that slot.
	 *
	 * @param item item for which the merge location is sought for
	 * @param slot starting location slot
	 * @return slot where the item can be merged, or <code>null</code> if no
	 * 	suitable location was found
	 */
	private RPSlot getSlotToMerge(StackableItem item, RPSlot slot) {
		if (slot instanceof EntitySlot) {
			if (!((EntitySlot) slot).isReachableForThrowingThingsIntoBy(this)) {
				return null;
			}
		}
		// Try first merging the item in the parent slot, so that the item
		// appears as visibly as possible
		if (item.getPossibleSlots().contains(slot.getName())) {
			for (RPObject obj : slot) {
				if (canMergeItems(item, obj)) {
					return slot;
				}
			}
		}
		// Then check the slots of the contained items
		for (RPObject obj : slot) {
			for (RPSlot childSlot : obj.slots()) {
				RPSlot tmp = getSlotToMerge(item, childSlot);
				if (tmp != null) {
					return tmp;
				}
			}
		}

		return null;
	}

	/**
	 * Find a target slot where an item can be equipped. The slots are sought
	 * recursively starting from a specified initial slot, and then proceeding
	 * to the content slots of the items in that slot.
	 *
	 * @param item item to be equipped
	 * @param slot starting slot
	 * @return slot where the item can be equipped, or <code>null</code> if no
	 * 	suitable location was found
	 */
	private RPSlot getSlotToEquip(Item item, RPSlot slot) {
		if (slot instanceof EntitySlot) {
			if (!((EntitySlot) slot).isReachableForThrowingThingsIntoBy(this)) {
				return null;
			}
		}
		if (item.getPossibleSlots().contains(slot.getName())) {
			if (!slot.isFull()) {
				return slot;
			}
		}
		for (RPObject obj : slot) {
			for (RPSlot childSlot : obj.slots()) {
				RPSlot tmp = getSlotToEquip(item, childSlot);
				if (tmp != null) {
					return tmp;
				}
			}
		}

		return null;
	}

	/**
	 * Gets the slot in which the entity can equip the item, preferring
	 * locations where the item can be merged with existing item stacks.
	 *
	 * @param item
	 * @return the slot for the item or null if there is no matching slot
	 *         in the entity
	 */
	public final RPSlot getSlotToEquip(final Item item) {
		if (item instanceof StackableItem) {
			// Try merging the item first
			for (RPSlot slot : slots()) {
				RPSlot tmp = getSlotToMerge((StackableItem) item, slot);
				if (tmp != null) {
					return tmp;
				}
			}
		}

		// We can't stack it on another item. Check if we can simply
		// add it to an empty cell.
		for (RPSlot slot : slots()) {
			RPSlot tmp = getSlotToEquip(item, slot);
			if (tmp != null) {
				return tmp;
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
			this.sendPrivateText("You dropped the new item onto the ground because your bag is full.");
			return false;
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
		RPSlot slot = getSlot(slotName);
		if (equipIt(slot, item)) {
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
		return drop(nameMatches(name), amount);
	}

	private boolean isEquipped(Predicate<Item> condition, int amount) {
		Iterable<Item> matching = getAllEquipped(condition)::iterator;
		int count = 0;
		for (Item item : matching) {
			count += item.getQuantity();
			if (count >= amount) {
				return true;
			}
		}
		return false;
	}

	private boolean drop(Predicate<Item> condition, int amount) {
		if (!isEquipped(condition, amount)) {
			return false;
		}

		int toDrop = amount;
		Iterable<Item> matchingItems = equippedStream().filter(condition)::iterator;
		for (Item item : matchingItems) {
			toDrop -= dropItem(item, toDrop);
			if (toDrop == 0) {
				return true;
			}
		}

		logger.error("Not enough items dropped even though the entity was checked to have them", new Throwable());
		return false;
	}

	/**
	 * Low level drop. <b>Does not check the containing slot or owner. This is
	 * meant to be used only by higher level drop() methods.</b>
	 *
	 * @param item dropped item
	 * @param amount maximum amout to drop
	 * @return dropped amount
	 */
	private int dropItem(Item item, int amount) {
		RPSlot slot = item.getContainerSlot();
		if (item instanceof StackableItem) {
			// The item is stackable, we try to remove
			// multiple ones.
			final int quantity = item.getQuantity();
			if (amount >= quantity) {
				new ItemLogger().destroy(this, slot, item);
				slot.remove(item.getID());
				return quantity;
			} else {
				((StackableItem) item).setQuantity(quantity - amount);
				new ItemLogger().splitOff(this, item, amount);
				return amount;
			}
		} else {
			// The item is not stackable, so we only remove a
			// single one.
			slot.remove(item.getID());
			new ItemLogger().destroy(this, slot, item);
			return 1;
		}
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
		return drop(it -> item == it, 1);
	}

	/**
	 * Removes a specific amount of an item with matching info string from
	 * the RPEntity. The item can either be stackable or non-stackable.
	 * The units can be distributed over different slots. If the RPEntity
	 * doesn't have enough units of the item, doesn't remove anything.
	 *
	 * @param name
	 * 		Name of item to remove.
	 * @param infostring
	 * 		Required item info string to match.
	 * @param amount
	 * 		Number of items to remove from entity.
	 * @return
	 * 		<code>true</code> if dropping the item(s) was successful.
	 */
	public boolean dropWithInfostring(final String name, final String infostring, final int amount) {
		return drop(item -> (name.equals(item.getName()) && infostring.equals(item.getInfoString())), amount);
	}

	/**
	 * Removes a single item with matching info string from the RPEntity.
	 * The item can either be stackable or non-stackable. The units can
	 * be distributed over different slots. If the RPEntity doesn't have
	 * enough units of the item, doesn't remove anything.
	 *
	 * @param name
	 * 		Name of item to remove.
	 * @param infostring
	 * 		Required item info string to match.
	 * @return
	 * 		<code>true</code> if dropping the item(s) was successful.
	 */
	public boolean dropWithInfostring(final String name, final String infostring) {
		return dropWithInfostring(name, infostring, 1);
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
		return isEquipped(nameMatches(name), amount);
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
	 * Checks if entity carry a number of items with specified info string.
	 *
	 * @param name
	 * 		Name of item to check.
	 * @param infostring
	 * 		Info string of item to check.
	 * @param amount
	 * 		Quantity of carried items to check.
	 * @return
	 * 		<code>true</code> if entity is carrying at least specified amount of items matching name & infostring.
	 */
	public boolean isEquippedWithInfostring(final String name, final String infostring, final int amount) {
		return getAllEquippedWithInfostring(name, infostring).size() >= amount;
	}

	/**
	 * Checks if entity carry a number of items with specified info string.
	 *
	 * @param name
	 * 		Name of item to check.
	 * @param infostring
	 * 		Info string of item to check.
	 * @return
	 * 		<code>true</code> if entity is carrying at least one of items matching name & infostring.
	 */
	public boolean isEquippedWithInfostring(final String name, final String infostring) {
		return isEquippedWithInfostring(name, infostring, 1);
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
		return equippedStream().filter(nameMatches(name))
				.mapToInt(Item::getQuantity).sum();
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
		Stream<Item> allItems = slots().stream().flatMap(this::slotStream);
		return allItems.filter(nameMatches(name)).mapToInt(Item::getQuantity).sum();
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
		return equippedStream().filter(nameMatches(name)).findFirst().orElse(null);
	}

	/**
	 * Gets an item that is carried by the RPEntity. If the item is stackable,
	 * gets all that are on the first stack that is found.
	 *
	 * @param name
	 *            The item's name
	 * @return The item, or a stack of stackable items, or an empty list if nothing was
	 *         found
	 */
	public List<Item> getAllEquipped(final String name) {
		return getAllEquipped(nameMatches(name));
	}

	private List<Item> getAllEquipped(Predicate<Item> condition) {
		return equippedStream().filter(condition).collect(Collectors.toList());
	}

	/**
	 * Retrieves all of an item with matching info string.
	 *
	 * @param name
	 * 		Name of item to match.
	 * @param infostring
	 * 		Info string of item to match.
	 * @return
	 * 		List<Item>
	 */
	public List<Item> getAllEquippedWithInfostring(String name, String infostring) {
		return getAllEquipped(item -> name.equals(item.getName())
				&& infostring.equalsIgnoreCase(item.getInfoString()));
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
	 * checks if an item is equipped in a slot
	 *
	 * @param slot
	 * @param item
	 * @return true if so false otherwise
	 */
	public boolean isEquippedItemInSlot(final String slot, final String item) {
		if (hasSlot(slot)) {
			final RPSlot rpslot = getSlot(slot);
			for (final RPObject object : rpslot) {
				if ((object instanceof Item) && ((Item) object).getName().equals(item)) {
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
		final String[] weaponsClasses = {"club", "sword", "axe", "ranged", "missile"};

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
		final List<Item> weapons = new ArrayList<>();
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

	public boolean hasRing() {
		return isEquippedItemClass("finger", "ring");
	}

	public Item getRing() {
		return getEquippedItemClass("finger", "ring");
	}

	@Override
	public String describe() {
		String text = super.describe();
		if (getLevel() > 0) {
			boolean showLevel = true;
			if (this instanceof Creature) {
				/**
				 * Hide level information of chess pieces.
				 *
				 * Don't call "Creature.isAbnormal" method here since it also checks "rare" attribute.
				 */
				if (((Creature) this).getAIProfiles().containsKey("abnormal") &&
						(this.getName().startsWith("chaos") || this.getName().startsWith("madaram"))) {
					showLevel = false;
				}
			}

			if (showLevel) {
				text += " It is level " + getLevel() + ".";
			}
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

	/**
	 * Sends a message that only this player can read.
	 *
	 * @param type
	 *            NotificationType
	 * @param text
	 *            the message.
	 */
	public void sendPrivateText(final NotificationType type, final String text) {
		// does nothing in this implementation.
	}

	/**
	 * Retrieves total ATK value of held weapons.
	 */
	public float getItemAtk() {
		int weapon = 0;
		int ring = 0;

		final List<Item> weapons = getWeapons();
		for (final Item weaponItem : weapons) {
			weapon += weaponItem.getAttack();
		}

		// calculate ammo when not using RATK stat
		if (!Testing.COMBAT && weapons.size() > 0) {
			if (getWeapons().get(0).isOfClass("ranged")) {
				weapon += getAmmoAtk();
			}
		}

		if (hasRing()) {
			ring = getRing().getAttack();
		}

		return weapon + ring;
	}

	/**
	 * Retrieves total range attack value of held weapon & ammunition.
	 */
	public float getItemRatk() {
		float ratk = 0;
		final List<Item> weapons = getWeapons();

		if (weapons.size() > 0) {
			final Item held = getWeapons().get(0);
			ratk += held.getRangedAttack();

			if (held.isOfClass("ranged")) {
				ratk += getAmmoAtk();
			}
		}

		return ratk;
	}

	/**
	 * Retrieves ATK or RATK (depending on testing.combat system property) value of equipped ammunition.
	 */
	private float getAmmoAtk() {
		float ammo = 0;

		final StackableItem ammoItem = getAmmunition();
		if (ammoItem != null) {
			if (Testing.COMBAT) {
				ammo = ammoItem.getRangedAttack();
			} else {
				ammo = ammoItem.getAttack();
			}
		}

		return ammo;
	}

	public float getItemDef() {
		int shield = 0;
		int armor = 0;
		int helmet = 0;
		int legs = 0;
		int boots = 0;
		int cloak = 0;
		int weapon = 0;
		int ring = 0;

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

		if (hasRing()) {
			item = getRing();
			ring = (int) (item.getDefense() / getItemLevelModifier(item));
		}

		final List<Item> targetWeapons = getWeapons();
		for (final Item weaponItem : targetWeapons) {
			weapon += weaponItem.getDefense() / getItemLevelModifier(weaponItem);
		}

		return SHIELD_DEF_MULTIPLIER * shield + ARMOR_DEF_MULTIPLIER * armor
				+ CLOAK_DEF_MULTIPLIER * cloak + HELMET_DEF_MULTIPLIER * helmet
				+ LEG_DEF_MULTIPLIER * legs + BOOTS_DEF_MULTIPLIER * boots
				+ WEAPON_DEF_MULTIPLIER * weapon + RING_DEF_MULTIPLIER * ring;
	}

	/**
	 * get all items that affect a player's defensive value except the weapon
	 *
	 * @return a list of all equipped defensive items
	 */
	public List<Item> getDefenseItems() {
		List<Item> items = new LinkedList<>();
		if (hasShield()) {
			items.add(getShield());
		}
		if (hasArmor()) {
			items.add(getArmor());
		}
		if (hasHelmet()) {
			items.add(getHelmet());
		}
		if (hasLegs()) {
			items.add(getLegs());
		}

		if (hasBoots()) {
			items.add(getBoots());
		}
		if (hasCloak()) {
			items.add(getCloak());
		}
		return items;
	}

	/**
	 * Recalculates item based atk and def.
	 */
	public void updateItemAtkDef() {
		put("atk_item", ((int) getItemAtk()));
		if (Testing.COMBAT) {
			put("ratk_item", ((int) getItemRatk()));
		}
		put("def_item", ((int) getItemDef()));
		notifyWorldAboutChanges();
	}

	/**
	 * Can this entity do a distance attack on the given target?
	 *
	 * @param target
	 * @param maxrange maximum attack distance
	 *
	 * @return true if this entity is armed with a distance weapon and if the
	 *         target is in range.
	 */
	public boolean canDoRangeAttack(final RPEntity target, final int maxrange) {
		// the target's in range
		return (squaredDistance(target) <= maxrange * maxrange);
	}

	/**
	 * Check if the entity has a line of sight to the the center of another
	 * entity. Only static collisions are checked.
	 *
	 * @param target target entity
	 * @return <code>true</code> if there are no collisions blocking the line
	 *	of sight, <code>false</code> otherwise
	 */
	public boolean hasLineOfSight(final Entity target) {
		return !getZone().collidesOnLine((int) (getX() + getWidth() / 2),
				(int) (getY() + getHeight() / 2),
				(int) (target.getX() + target.getWidth() / 2),
				(int) (target.getY() + target.getHeight() / 2));
	}

	/**
	 * Get the maximum distance attack range.
	 *
	 * @return maximum range, or 0 if the entity can't attack from distance
	 */
	public int getMaxRangeForArcher() {
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
		final int defenderDEF = defender.getCappedDef();

		// Check if attacking from distance
		boolean usesRanged = false;
		if (!this.nextTo(defender)) {
			usesRanged = true;
		}

		final int attackerATK;
		if (Testing.COMBAT && usesRanged) {
			attackerATK = this.getCappedRatk(); // player is using ranged weapon
		} else {
			attackerATK = this.getCappedAtk(); // player is using hand-to-hand
		}

		/*
		 * Use some karma unless attacker is much stronger than defender, in
		 * which case attacker doesn't need luck to help him hit.
		 */
		final int levelDifferenceToNotNeedKarmaAttacking = (int) (IGNORE_KARMA_MULTIPLIER * getLevel());

		// using karma here increases chance to hit enemy
		if (!(getLevel() - levelDifferenceToNotNeedKarmaAttacking > defender.getLevel())) {
			final double karmaMultiplier = this.useKarma(0.1);
			// the karma effect must be cast to an integer to affect the roll
			// but in most cases this means the karma use was lost. so multiply by 2 to
			// make the same amount of karma use be more useful
			final double karmaEffect = roll * karmaMultiplier * 2.0;
			roll -= (int) karmaEffect;
		}

		final int risk = calculateRiskForCanHit(roll, defenderDEF, attackerATK);

		if (logger.isDebugEnabled() || Testing.DEBUG) {
			logger.debug("attack from " + this + " to " + defender
					+ ": Risk to strike: " + risk);
		}

		return risk > 0;
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

		boolean meleeDistance = isAttacking() && nextTo(getAttackTarget());

		final List<Item> weapons = getWeapons();

		if (weapons.isEmpty()) {
			return Item.getDefaultAttackRate();
		}
		int best = weapons.get(0).getAttackRate(meleeDistance);
		for (final Item weapon : weapons) {
			final int res = weapon.getAttackRate(meleeDistance);
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

		final int maxRange = getMaxRangeForArcher();
		/*
		 * The second part (damage type check) ensures that normal archers need
		 * distance attack modifiers for melee, but creatures with special
		 * ranged attacks (dragons) pay the price only when using their ranged
		 * powers (yes, it's a bit of a hack).
		 */
		boolean isRanged = ((maxRange > 0) && canDoRangeAttack(defender, maxRange))
			&& (((getDamageType() == getRangedDamageType()) || squaredDistance(defender) > 0));

		Nature nature;
		final float itemAtk;
		if (isRanged) {
			nature = getRangedDamageType();
			itemAtk = getItemRatk();
		} else {
			nature = getDamageType();
			itemAtk = getItemAtk();
		}

		// Try to inflict a status effect
		for (StatusAttacker statusAttacker : statusAttackers) {
			statusAttacker.onAttackAttempt(defender, this);
		}

		// Weapon for the use in the attack event
		Item attackWeapon = getWeapon();
		String weaponName = null;
		if (attackWeapon != null) {
			weaponName = attackWeapon.getWeaponType();
		}

		if (this.canHit(defender)) {
			defender.applyDefXP(this);

			int damage = damageDone(defender, itemAtk, nature, isRanged, maxRange);

			if (damage > 0) {

				// limit damage to target HP
				damage = Math.min(damage, defender.getHP());
				this.handleLifesteal(this, this.getWeapons(), damage);

				defender.onDamaged(this, damage);

				if (logger.isDebugEnabled() || Testing.DEBUG) {
					logger.debug("attack from " + this.getID() + " to "
							+ defender.getID() + ": Damage: " + damage);
				}

				result = true;
			} else {
				// The attack was too weak, it was blocked

				if (logger.isDebugEnabled() || Testing.DEBUG) {
					logger.debug("attack from " + this.getID() + " to "
							+ defender.getID() + ": Damage: " + 0);
				}
			}
			this.addEvent(new AttackEvent(true, damage, nature, weaponName, isRanged));

			// Try to inflict a status effect
			for (StatusAttacker statusAttacker : statusAttackers) {
				statusAttacker.onHit(defender, this, damage);
			}

		} else {
			// Missed
			if (logger.isDebugEnabled() || Testing.DEBUG) {
				logger.debug("attack from " + this.getID() + " to "
						+ defender.getID() + ": Missed");
			}

			this.addEvent(new AttackEvent(false, 0, nature, weaponName, isRanged));
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
	 * @param rpslot
	 * @param item
	 * @return true if successful*/
	private boolean equipIt(final RPSlot rpslot, final Item item) {
		if (rpslot  == null || (item == null)) {
			return false;
		}

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

	/**
	 * Gets the name of the player who deserves the corpse.
	 *
	 * @return name of player who deserves the corpse or <code>null</code>.
	 */
	public String getCorpseDeserver() {
		return null;
	}

	/**
	 * gets the language
	 *
	 * @return language
	 */
	public String getLanguage() {
		return null;
	}

	/**
	 * Sets the sound played at entity death
	 *
	 * @param sound Name of sound
	 */
	public void setDeathSound(final String sound) {
		deathSound = sound;
	}

	/**
	 * @return Name of sound played at entity death
	 */
	public String getDeathSound() {
		return deathSound;
	}

	/**
	 * Add a status attack type to the entity
	 *
	 * @param statusAttacker Status attacker
	 */
	public void addStatusAttacker(final StatusAttacker statusAttacker) {
		// the immutable statusAttackers list is shared between multiple instances of Creatures to reduce memory usage
		Builder<StatusAttacker> builder = ImmutableList.builder();
		statusAttackers = builder.addAll(statusAttackers).add(statusAttacker).build();
	}

	/**
	 * gets the status list
	 *
	 * @return StatusList
	 */
	public StatusList getStatusList() {
		if (statusList == null) {
			statusList = new StatusList(this);
		}
		return statusList;
	}

	/**
	 * Find if the entity has a specified status
	 *
	 * @param statusType the status type to check for
	 * @return true, if the entity has status; false otherwise
	 */
	public boolean hasStatus(StatusType statusType) {
		if (statusList == null) {
			return false;
		}
		return statusList.hasStatus(statusType);
	}

	@Override
	public void onRemoved(StendhalRPZone zone) {
		super.onRemoved(zone);

		// Stop other creatures and players attacks on me.
		// Probably I am dead, and I don't want to die again with a second corpse.
		for (Entity attacker : new LinkedList<>(attackSources)) {
			if (attacker instanceof RPEntity) {
				((RPEntity) attacker).stopAttack();
			}
		}
	}

	/**
	 * Gets an items as a stream of items, followed by any contained items
	 * recursively.
	 *
	 * @param item
	 * @return stream of items
	 */
	private Stream<Item> itemStream(Item item) {
		Stream<Item> stream = Stream.of(item);
		if (item.slots().isEmpty()) {
			return stream;
		}
		Stream<RPSlot> slots = item.slots().stream();
		Stream<Item> internalItems = slots.flatMap(this::slotStream);
		return Stream.concat(stream, internalItems);
	}

	/**
	 * Get a stream of all items in a slot.
	 *
	 * @param slot
	 * @return items in the slot
	 */
	private Stream<Item> slotStream(RPSlot slot) {
		Stream<RPObject> objects = StreamSupport.stream(slot.spliterator(), false);
		Stream<Item> items = objects.filter(Item.class::isInstance).map(Item.class::cast);
		return items.flatMap(this::itemStream);
	}

	/**
	 * Get a stream of all equipped items.
	 *
	 * @return equipped items
	 */
	private Stream<Item> equippedStream() {
		Stream<String> slotNames = Slots.CARRYING.getNames().stream();
		Stream<RPSlot> slots = slotNames.map(this::getSlot).filter(Objects::nonNull);
		return slots.flatMap(this::slotStream);
	}

	/**
	 * A convenience method for getting a method for matching item names.
	 *
	 * @param name name to match
	 * @return a predicate for matching the name
	 */
	private Predicate<Item> nameMatches(String name) {
		return it -> name.equalsIgnoreCase(it.getName());
	}

	/**
	 * Sets the attribute to define the shadow that the client should use.
	 *
	 * @param st
	 * 		String name of the shadow to use.
	 */
	public void setShadowStyle(final String st) {
		if (st == null || st.equals("none")) {
			remove("shadow_style");
			put("no_shadow", "");
			return;
		}

		put("shadow_style", st);
		remove("no_shadow");
	}
}
