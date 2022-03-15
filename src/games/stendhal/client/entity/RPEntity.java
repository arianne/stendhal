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
package games.stendhal.client.entity;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.GameLoop;
import games.stendhal.client.GameObjects;
import games.stendhal.client.stendhal;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.client.gui.chatlog.StandardHeaderedEventLine;
import games.stendhal.common.ItemTools;
import games.stendhal.common.NotificationType;
import games.stendhal.common.constants.Nature;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.grammar.Grammar;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObject.ID;

/**
 * This class is a link between client graphical objects and server attributes
 * objects.<br>
 * You need to extend this object in order to add new elements to the game.
 */
public abstract class RPEntity extends AudibleEntity {
	private static final Logger LOGGER = Logger.getLogger(RPEntity.class);

	/**
	 * Square of the distance where to observe various events, such as speech.
	 */
	private static final int HEARING_DISTANCE_SQ = 15 * 15;
	/** Turn length in milliseconds. */
	private static final int TURN_LENGTH = 300;
	/** Maximum length of text in entity speech bubbles. */
	private static final int BUBBLE_TEXT_LENGTH = 84;
	/**
	 * Admin Level property.
	 */
	public static final Property PROP_ADMIN_LEVEL = new Property();
	/**
	 * ghostmode property.
	 */
	public static final Property PROP_GHOSTMODE = new Property();
	/**
	 * group membership.
	 */
	public static final Property PROP_GROUP_MEMBERSHIP = new Property();
	/**
	 * Indicator text property. Fired if they are added or removed.
	 */
	public static final Property PROP_TEXT_INDICATORS = new Property();
	/**
	 * Outfit property.
	 */
	public static final Property PROP_OUTFIT = new Property();
	/**
	 * Title Type property.
	 */
	public static final Property PROP_TITLE_TYPE = new Property();
	/**
	 * Hp and max HP property.
	 */
	public static final Property PROP_HP_RATIO = new Property();
	/**
	 * Property for showing and hiding the HP bar.
	 */
	public static final Property PROP_HP_DISPLAY = new Property();

	// Job properties
	/**
	 * Healer
	 */
	public static final Property PROP_HEALER = new Property();
	/**
	 * Merchant
	 */
	public static final Property PROP_MERCHANT = new Property();

    // Status properties
    /**
     * Confused property
     */
    public static final Property PROP_CONFUSED = new Property();
    /**
     * Eating property
     */
    public static final Property PROP_EATING = new Property();
    /**
     * Poisoned property
     */
    public static final Property PROP_POISONED = new Property();
    /**
     * Shock property
     */
    public static final Property PROP_SHOCK = new Property();
    /**
     * Zombie property
     */
    public static final Property PROP_ZOMBIE = new Property();
    /**
     * Heavy property
     */
    public static final Property PROP_HEAVY = new Property();

    private static final Map<StatusID, Property> statusProp;
    static {
        statusProp = new EnumMap<StatusID, Property>(StatusID.class);
        statusProp.put(StatusID.CONFUSE, PROP_CONFUSED);
        statusProp.put(StatusID.POISON, PROP_POISONED);
        statusProp.put(StatusID.SHOCK, PROP_SHOCK);
        statusProp.put(StatusID.ZOMBIE, PROP_ZOMBIE);
        statusProp.put(StatusID.HEAVY, PROP_HEAVY);
    }

	/**
	 * Attacking property. (for attack events)
	 */
	public static final Property PROP_ATTACK = new Property();

	/**
	 * The value of an outfit that isn't set.
	 */
	public static final int OUTFIT_UNSET = -1;


	/**
	 * Entity we are attacking. (need to reconsile this with 'attacking')
	 */
	RPEntity attackTarget;

	/**
	 * The entities attacking this entity.
	 */
	private final Collection<Entity> attackers = new ConcurrentLinkedQueue<Entity>();

	/**
	 * The nature of the current attack done by this entity, or
	 * <code>null</code> if there's no ongoing attack.
	 */
	private Nature attackNature;
	/**
	 * The weapon used in the current attack, or <code>null</code> if no weapon
	 * is specified.
	 */
	private String weapon;
	/**
	 * <code>true</code> if the previously done attack event was ranged,
	 * 	otherwise <code>false</code>.
	 */
	private boolean isDoingRangedAttack;
	/**
	 * Flag for checking attack targets that were added in the zone later than
	 * this entity.
	 */
	private boolean targetUpdated;

	private int atk;

	private int def;

	private int ratk;

	private int xp;

	private int hp;

	private int adminlevel;

	/**
	 * The outfit code.
	 */
	private String outfit_ext;
	private int outfit_old;

	private int baseHP;

	private float hpRatio;

	private int level;

	private boolean eating;

	/** Currently active statuses. */
	private final Set<StatusID> statuses = EnumSet.noneOf(StatusID.class);

	private boolean choking;

	private boolean showTitle = true;

	private boolean showHP = true;

	/**
	 * Time stamp of previous attack event. Volatile to prevent reordering
	 * assignments with <code>resolution</code>.
	 */
	private volatile long combatIconTime;

	private final List<TextIndicator> textIndicators;

	private RPObject.ID attacking;

	private int mana;

	private int baseMana;

	private boolean ghostmode;

	private boolean ignoreCollision;

	private String titleType;

	/**
	 * The result of previous attack against this entity. Volatile to prevent
	 * reordering assignments with <code>combatIconTime</code>.
	 */
	private volatile Resolution resolution;

	private int atkXP;

	private int defXP;

	private int ratkXP;

	private int atkItem = -1;

	private int defItem = -1;

	private int ratkItem = -1;

	/** A flag that gets set once the entity has been released. */
	private boolean released;

	/** entity casts shadow by default */
	private boolean castShadow = true;
	private String shadowStyle;

	private static final boolean testclient = System.getProperty("stendhal.testclient") != null;

	/** Possible attack results. */
	public enum Resolution {
		HIT,
		BLOCKED,
		MISSED;
	}


	/** Creates a new game entity. */
	RPEntity() {
		textIndicators = new LinkedList<TextIndicator>();
		attackTarget = null;
	}

	//
	// RPEntity
	//

	/**
	 * Create/add a text indicator message.
	 *
	 * @param text
	 *            The text message.
	 * @param type
	 *            The indicator type.
	 */
	protected void addTextIndicator(final String text,
			final NotificationType type) {
		textIndicators.add(new TextIndicator(text, type));
		fireChange(PROP_TEXT_INDICATORS);
	}

	/**
	 * Get the admin level.
	 *
	 * @return The admin level.
	 */
	public int getAdminLevel() {
		return adminlevel;
	}

	/**
	 * @return Returns the atk.
	 */
	public int getAtk() {
		return atk;
	}

	/**
	 * @return Returns the atk of items
	 */
	public int getAtkItem() {
		return atkItem;
	}

	/**
	 * @return the attack xp
	 */
	public int getAtkXP() {
		return atkXP;
	}

	/**
	 * @return Returns the base_hp.
	 */
	public int getBaseHP() {
		return baseHP;
	}

	/**
	 * @return Returns the base mana value
	 */
	public int getBaseMana() {
		return baseMana;
	}

	/**
	 * @return Returns the def.
	 */
	public int getDef() {
		return def;
	}

	/**
	 * @return Returns the def of items
	 */
	public int getDefItem() {
		return defItem;
	}

	/**
	 * @return the defence xp
	 */
	public int getDefXP() {
		return defXP;
	}

	/**
	 * @return Returns the ratk.
	 */
	public int getRatk() {
		return ratk;
	}

	/**
	 * @return Returns the ratk of items
	 */
	public int getRatkItem() {
		return ratkItem;
	}

	/**
	 * @return the ranged xp
	 */
	public int getRatkXP() {
		return ratkXP;
	}

	/**
	 * Get the ratio of HP to base HP.
	 *
	 * @return The HP ratio (0.0 - 1.0).
	 */
	public float getHpRatio() {
		return hpRatio;
	}

	/**
	 * Get the list of text indicator elements.
	 *
	 * @return An iterator of text indicators.
	 */
	public Iterator<TextIndicator> getTextIndicators() {
		return textIndicators.iterator();
	}

	/**
	 * Get the entity level.
	 *
	 * @return level
	 */
	int getLevel() {
		return level;
	}

	/**
	 * @return Returns the total mana of a player
	 */
	public int getMana() {
		return mana;
	}

	/**
	 * Get the outfit code.
	 *
	 * @return The outfit code.
	 */
	public String getExtOutfit() {
		return outfit_ext;
	}

	/**
	 * Get the old outfit code.
	 *
	 * @return The outfit code.
	 */
	public int getOldOutfitCode() {
		return outfit_old;
	}

	/**
	 * Get the outfit code.
	 *
	 * @return The outfit code.
	 * @deprecated
	 *     Use {@link #getOldOutfitCode()}.
	 */
	@Deprecated
	public int getOutfit() {
		return getOldOutfitCode();
	}

	/**
	 * The result of previous attack against this entity.
	 *
	 * @return attack result
	 */
	public Resolution getResolution() {
		return resolution;
	}

	/**
	 * Get the attack target of an entity.
	 *
	 * @return the target, or <code>null</code> if there is none
	 */
	public RPEntity getAttackTarget() {
		return attackTarget;
	}

	/**
	 * Update the target.
	 *
	 * @param targetString The target id as a string
	 * @param zoneId zone of the entity
	 */
	private void setTarget(String targetString, String zoneId) {
		final int target = Integer.parseInt(targetString);

		final RPObject.ID targetEntityID = new RPObject.ID(target, zoneId);
		final RPEntity targetEntity = (RPEntity) GameObjects.getInstance().get(
				targetEntityID);

		if (targetEntity != attackTarget) {
			onStopAttack();

			if (attackTarget != null) {
				attackTarget.onStopAttacked(this);
			}

			attackTarget = targetEntity;

			if (attackTarget != null) {
				onAttack(attackTarget);
				attackTarget.onAttacked(this);
			}
		}
	}

	/**
	 * Update the target.
	 *
	 * @param targetString The target id as a string
	 */
	private void setTarget(String targetString) {
		setTarget(targetString, rpObject.get("zoneid"));
	}


	/**
	 * Get the nicely formatted entity title.
	 *
	 * This searches the follow attribute order: title, name (w/o underscore),
	 * class (w/o underscore), type (w/o underscore).
	 *
	 * @return The title, or <code>null</code> if unknown.
	 */
	@Override
	public String getTitle() {
		if (title != null) {
			return title;
		} else if (name != null) {
			return name;
		} else if (clazz != null) {
			// replace underscores in clazz and type without calling the function UpdateConverter.transformItemName() located in server code
			return ItemTools.itemNameToDisplayName(clazz);
		} else if (type != null) {
			return ItemTools.itemNameToDisplayName(type);
		} else {
			return null;
		}
	}

	/**
	 * Get title type.
	 *
	 * @return The title type, or <code>null</code> if the entity has no special
	 * 	title type
	 */
	public String getTitleType() {
		return titleType;
	}

	/**
	 * @return Returns the XP.
	 */
	public int getXP() {
		return xp;
	}

	/**
	 * @return Returns the entities attacking this entity
	 */
	public Collection<Entity> getAttackers() {
		return attackers;
	}

	/**
	 * Get the ID of the current attack target. Try to resolve targets that
	 * have been added to the zone after this. This is meant to be called from
	 * the EDT.
	 *
	 * @return attack target, or <code>null</code> if the entity is not
	 * 	attacking
	 */
	private ID getTargetID() {
		if (!targetUpdated && (attacking == null)) {
			/*
			 * Check for disagreement, and update if needs be.
			 * Can happen when the target is added to the zone after the attacker.
			 *
			 * Fire and forget. The update likely won't be ready for this screen
			 * redraw, but it'll be ready for some redraw later.
			 */
			GameLoop.get().runOnce(new Runnable() {
				@Override
				public void run() {
					if ((attacking == null) && !released) {
						String id = rpObject.get("target");
						if (id != null) {
							setTarget(id);
						}
					}
				}
			});
			targetUpdated = true;
		}
		return attacking;
	}

	/**
	 * Check if the entity is attacking a specified entity. This is meant to be
	 * called from the EDT when drawing entities.
	 *
	 * @param defender the potential target
	 * @return <code>true</code> if defender is attacked by this entity,
	 * 	otherwise <code>false</code>
	 */
	public boolean isAttacking(final IEntity defender) {
		if (defender == null) {
			return false;
		}

		final ID defenderID = defender.getID();
		return defenderID.equals(getTargetID());
	}

	/**
	 * Check if the entity is a target of an attack.
	 *
	 * @return <code>true</code> if the entity is being attacked, otherwise
	 * 	<code>false</code>
	 */
	public boolean isBeingAttacked() {
		return !attackers.isEmpty();
	}

	/**
	 * Check if a specific entity is attacking this RPEntity.
	 *
	 * @param attacker potential attacker
	 * @return <code>true</code> if attacker is attacking this RPEntity,
	 * 	otherwise <code>false</code>
	 */
	public boolean isAttackedBy(final IEntity attacker) {
		return attackers.contains(attacker);
	}

	/**
	 * Get the damage type of the current strike.
	 *
	 * @return type of damage, or <code>null</code> if the entity is not striking
	 */
	public Nature getShownDamageType() {
		return attackNature;
	}

	/**
	 * Get the weapon used in the current attack.
	 *
	 * @return weapon, or <code>null</code> if not specified
	 */
	public String getShownWeapon() {
		return weapon;
	}

	/**
	 * Check if the currently performed attack is ranged.
	 *
	 * @return <code>true</code> if the attack is ranged, <code>false</code>
	 * 	otherwise
	 */
	public boolean isDoingRangedAttack() {
		return isDoingRangedAttack;
	}

	/**
	 * Check if the entity is defending against an attack right now. The entity
	 * is defending if the last attack happened within 1.2s.
	 *
	 * @return <code>true</code> if the entity is defending against an attack,
	 * 	<code>false</code> otherwise
	 */
	public boolean isDefending() {
		return (isBeingAttacked() && (System.currentTimeMillis()
				- combatIconTime < 4 * TURN_LENGTH));
	}

	/**
	 * Check if the entity is eating.
	 *
	 * @return <code>true</code> if the entity is eating, otherwise
	 * 	<code>false</code>
	 */
	public boolean isEating() {
		return eating;
	}

	/**
	 * Determine if in full ghostmode.
	 *
	 * @return <code>true</code> is in full ghostmode.
	 */
	public boolean isGhostMode() {
		return ghostmode;
	}

	/**
	 * Check if the entity can pass through static collisions.
	 *
	 * @return <code>true</code> if the entity can pass through walls, otherwise
	 * 	<code>false</code>
	 */
	public boolean ignoresCollision() {
		return ignoreCollision;
	}

	/**
	 * Check if the entity is confused or poisoned.
	 *
	 * @return <code>true</code> if the entity is confused or poisoned,
	 * 	otherwise <code>false</code>
	 */
	public boolean isConfused() {
		return hasStatus(StatusID.POISON) || hasStatus(StatusID.CONFUSE);
	}

	/**
	 * Check if the entity has a certain status.
	 *
	 * @param status status id
	 * @return <code>true</code> if the entity has the status, otherwise
	 * 	<code>false</code>.
	 */
	public boolean hasStatus(final StatusID status) {
	    return statuses.contains(status);
	}

	/**
	 * Check if the entity is choking.
	 *
	 * @return <code>true</code> if the entity is choking, otherwise
	 * 	<code>false</code>
	 */
	public boolean isChoking() {
		return choking;
	}

	// TODO: this is just an ugly workaround to avoid cyclic dependencies with
	// Creature
	void nonCreatureClientAddEventLine(final String text) {
		ClientSingletonRepository.getUserInterface().addEventLine(new StandardHeaderedEventLine(getTitle(), text));
	}

	/**
	 * Called when this entity attacks target.
	 *
	 * @param target attack target
	 */
	private void onAttack(final IEntity target) {
		attacking = target.getID();
	}

	/**
	 * When this entity performs an attack.
	 *
	 * @param type attack nature
	 * @param ranged <code>true</code> if it's a ranged attack, otherwise
	 * 	<code>false</code>
	 * @param weapon Weapon used in the attack, or <code>null</code> if not
	 * 	specified
	 */
	public void onAttackPerformed(final Nature type, boolean ranged, String weapon) {
		attackNature = type;
		isDoingRangedAttack = ranged;
		this.weapon = weapon;
		fireChange(PROP_ATTACK);
	}

	/**
	 * When attacker attacks this entity.
	 *
	 * @param attacker attacking entity
	 */
	private void onAttacked(final Entity attacker) {
		attackers.remove(attacker);
		attackers.add(attacker);
	}

	/**
	 * Called when this entity blocks the attack by attacker.
	 */
	public void onBlocked() {
		// Resolution must be set before isDefending may return true.
		resolution = Resolution.BLOCKED;
		combatIconTime = System.currentTimeMillis();
	    playRandomSoundFromCategory(SoundLayer.FIGHTING_NOISE.groupName, "block");
	}

	/**
	 * Called when this entity is damaged by attacker with damage amount.
	 *
	 * @param attacker attacking entity
	 * @param damage amount of damage
	 */
	public void onDamaged(final Entity attacker, final int damage) {
		// Resolution must be set before isDefending may return true.
		resolution = Resolution.HIT;
		combatIconTime = System.currentTimeMillis();

		boolean showAttackInfoForPlayer = (this.isUser() || attacker.isUser());
		showAttackInfoForPlayer = showAttackInfoForPlayer
				& (!stendhal.FILTER_ATTACK_MESSAGES);

		if (stendhal.SHOW_EVERYONE_ATTACK_INFO || showAttackInfoForPlayer) {
			ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(
					getTitle() + " suffers "
							+ Grammar.quantityplnoun(damage, "point")
							+ " of damage from " + attacker.getTitle(),
					NotificationType.NEGATIVE));
		}

		// play a sound to indicate successful hit
		playRandomSoundFromCategory(SoundLayer.FIGHTING_NOISE.groupName, "attack");
	}

	/**
	 * Process eating and choking status changes. Avoids firing the PROP_EATING
	 * property more often than needed and ensures both of the properties are
	 * in the new state before firing.
	 *
	 * @param newStatus the status where to change eating or choking, if changes
	 * 	are needed
	 * @param setEat if <code>true</code> then eating status should be set
	 * @param setChoke if <code>true</code> then choking status should be set
	 */
	private void setEatAndChoke(boolean newStatus, boolean setEat, boolean setChoke) {
		boolean changed = false;
		if (setEat && (this.eating != newStatus)) {
			this.eating = newStatus;
			changed = true;
		}
		if (setChoke && (this.choking != newStatus)) {
			this.choking = newStatus;
			changed = true;
		}
		if (changed) {
			fireChange(PROP_EATING);
		}
	}

	/**
	 * Called when the entity gets healed.
	 *
	 * @param amount amount healed
	 */
	public void onHealed(final int amount) {
		// do nothing for normal rpentities
	}

	/**
	 * Called When entity adjusts HP.
	 *
	 * @param amount change amount
	 */
	private void onHPChange(final int amount) {
		if (User.squaredDistanceTo(x, y) < HEARING_DISTANCE_SQ) {
			if (amount > 0) {
				addTextIndicator("+" + amount, NotificationType.POSITIVE);
			} else {
				addTextIndicator(String.valueOf(amount),
						NotificationType.NEGATIVE);
			}
		}
	}

	/**
	 * Called when an attacker misses this entity.
	 */
	public void onMissed() {
		// Resolution must be set before isDefending may return true.
		resolution = Resolution.MISSED;
		combatIconTime = System.currentTimeMillis();
	}

	/**
	 * Called when entity is poisoned.
	 *
	 * @param amount lost HP
	 */
	private void onPoisoned(final int amount) {
		if ((amount > 0) && (User.squaredDistanceTo(x, y) < HEARING_DISTANCE_SQ)) {
			ClientSingletonRepository.getUserInterface().addEventLine(
					new HeaderLessEventLine(
							getTitle() + " is poisoned, losing "
							+ Grammar.quantityplnoun(amount, "health point")
							+ ".", NotificationType.POISON));
		}
	}

    /**
     * Set the status.
     *
     * @param status
     *         New status
     * @param show
     *         Show status overlay
     */
    private void setStatus(final StatusID status, final boolean show) {
        if (show) {
            statuses.add(status);
        } else {
            statuses.remove(status);
        }
        fireChange(statusProp.get(status));
    }

	/**
	 * Called when entity listen to text from talker.
	 *
	 * @param texttype type of talk (normal private talk, administrator message)
	 * @param text message contents
	 */
	public void onPrivateListen(final String texttype, final String text) {
		NotificationType type;
		try {
			type = NotificationType.valueOf(texttype);
		} catch (final RuntimeException e) {
			LOGGER.error("Unkown texttype: ", e);
			type = NotificationType.PRIVMSG;
		}

		ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(text, type));

		// Scene settings messages should not disturb playing, just create some atmosphere
		if (type != NotificationType.SCENE_SETTING) {
			ClientSingletonRepository.getScreenController().addText(
					getX() + (getWidth() / 2.0), getY(),
					text.replace("|", ""), type, false);
		}
	}

	/**
	 * Called when this entity stops attacking.
	 */
	private void onStopAttack() {
		attacking = null;
	}

	/**
	 * Called when attacker stop attacking us.
	 *
	 * @param attacker the attacked that stopped attacking
	 */
	private void onStopAttacked(final IEntity attacker) {
		attackers.remove(attacker);
	}

	/**
	 * Called when the entity reaches an achievement.
	 *
	 * @param achievementTitle title of the achievement
	 * @param achievementDescription description of the achievement
	 * @param achievementCategory achievement category
	 */
	public void onReachAchievement(String achievementTitle, String achievementDescription, String achievementCategory) {
		ClientSingletonRepository.getUserInterface().addAchievementBox(achievementTitle, achievementDescription, achievementCategory);
	}

	/**
	 * Called when entity says something.
	 *
	 * @param text message contents
	 */
	public void onTalk(String text) {
		if (User.isAdmin() || (User.squaredDistanceTo(x, y) < HEARING_DISTANCE_SQ)) {
			//an emote action is changed server side to an chat action with a leading !me
			//this supports also invoking an emote with !me instead of /me
			if (text.startsWith("!me")) {
				text = text.replace("!me", getTitle());
				ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(text, NotificationType.EMOTE));

				return;
			} else {
				//add the original version
				nonCreatureClientAddEventLine(text);
			}

			text = trimText(text);

			if (testclient) {
				// add stationary speech bubble
				ClientSingletonRepository.getScreenController().addText(
					getX() + getWidth(), getY(), text,
					NotificationType.NORMAL, true);
			} else {
				// add speech bubble that follows entity
				ClientSingletonRepository.getScreenController().addText(
					this, text, NotificationType.NORMAL, true);
			}
		}
	}

	/**
	 * Trim text for a speech bubble.
	 *
	 * @param text text to be trimmed
	 * @return text suitably trimmed for a speech bubble
	 */
	private String trimText(String text) {
		if (text.length() > BUBBLE_TEXT_LENGTH) {
			text = text.substring(0, BUBBLE_TEXT_LENGTH);
			// Cut the text if possible at the nearest space etc.
			int n = text.lastIndexOf(' ');
			n = Math.max(n, text.lastIndexOf('-'));
			n = Math.max(n, text.lastIndexOf('.'));
			n = Math.max(n, text.lastIndexOf(','));

			if (n > 0) {
				text = text.substring(0, n);
			}

			text += " ...";
		}
		return text;
	}

	//
	// Entity
	//

	/**
	 * Get the resistance this has on other entities (0-100).
	 *
	 * @return The resistance, or 0 if in ghostmode.
	 */
	@Override
	public int getResistance() {
		if (isGhostMode()) {
			return 0;
		} else {
			return super.getResistance();
		}
	}

	/**
	 * Initialize this entity for an object.
	 *
	 * @param object
	 *            The object.
	 *
	 * @see #release()
	 */
	@Override
	public void initialize(final RPObject object) {
		super.initialize(object);

		/*
		 * Base HP
		 */
		if (object.has("base_hp")) {
			baseHP = object.getInt("base_hp");
		} else {
			baseHP = 0;
		}

		/*
		 * HP
		 */
		if (object.has("hp")) {
			hp = object.getInt("hp");
		} else {
			hp = 0;
		}

		/*
		 * HP ratio
		 */
		if (hp >= baseHP) {
			hpRatio = 1.0f;
		} else if (hp <= 0) {
			hpRatio = 0.0f;
		} else {
			hpRatio = hp / (float) baseHP;
		}

		/*
		 * Public chat
		 */
		if (object.has("text")) {
			onTalk(object.get("text"));
		}

		/*
		 * Outfit
		 */
		if (object.has("outfit_ext")) {
			outfit_ext = object.get("outfit_ext");
		} else {
			outfit_ext = null;
		}

		if (object.has("outfit")) {
			outfit_old = object.getInt("outfit");
		} else {
			outfit_old = OUTFIT_UNSET;
		}

		/*
		 * eating and choking
		 */
		setEatAndChoke(true, object.has("eating"), object.has("choking"));

        /* Statuses */
		for (StatusID id : StatusID.values()) {
			if (object.has(id.getAttribute())) {
				setStatus(id, true);
			}
		}

		/*
		 * Ghost mode feature.
		 */
		ghostmode = object.has("ghostmode");

		/*
		 * Ignoring collision.
		 */
		ignoreCollision = object.has("ignore_collision");

		/*
		 * Healed
		 */
		if (object.has("heal")) {
			onHealed(object.getInt("heal"));
		}

		/*
		 * Attack Target is handled later, as it can not be checked reliably
		 * now anyway.
		 */

		/*
		 * Admin level
		 */
		if (object.has("adminlevel")) {
			adminlevel = object.getInt("adminlevel");
		} else {
			adminlevel = 0;
		}

		/*
		 * Title type
		 */
		titleType = object.get("title_type");

		showTitle = !object.has("unnamed");
		showHP = !object.has("no_hpbar");

		/*
		 * Determine if entity should not cast a shadow
		 */
		if (object.has("no_shadow")) {
			castShadow = false;
		}
		shadowStyle = object.get("shadow_style");

		initializeSounds();
	}

	/**
	 * Initialize the fighting sounds.
	 */
	private void initializeSounds() {
		addSounds(SoundLayer.FIGHTING_NOISE.groupName, "attack",
				"attack-melee-01",	"attack-melee-02",	"attack-melee-03",
				"attack-melee-04",	"attack-melee-05",	"attack-melee-06",
				"attack-melee-07");

		addSounds(SoundLayer.FIGHTING_NOISE.groupName, "block",
				"clang-metallic-1",	"clang-dull-1");
	}

	/**
	 * Release this entity. This should clean anything that isn't automatically
	 * released (such as unregister callbacks, cancel external operations, etc).
	 *
	 * @see #initialize(RPObject)
	 */
	@Override
	public void release() {
		released = true;
		onStopAttack();

		if (attackTarget != null) {
			attackTarget.onStopAttacked(this);
			attackTarget = null;
		}

		super.release();
	}

	/**
	 * Update cycle.
	 *
	 * @param delta
	 *            The time (in ms) since last call.
	 */
	@Override
	public void update(final int delta) {
		super.update(delta);

		if (!textIndicators.isEmpty()) {
			final Iterator<TextIndicator> iter = textIndicators.iterator();

			boolean changed = false;
			while (iter.hasNext()) {
				final TextIndicator textIndicator = iter.next();

				if (textIndicator.addAge(delta) > 2000L) {
					iter.remove();
					changed = true;
				}
			}

			if (changed) {
				fireChange(PROP_TEXT_INDICATORS);
			}
		}
	}

	//
	// RPObjectChangeListener
	//

	/**
	 * The object added/changed attribute(s).
	 *
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	@Override
	public void onChangedAdded(final RPObject object, final RPObject changes) {
		super.onChangedAdded(object, changes);

		if (!inAdd) {
			/*
			 * Public chat
			 */
			if (changes.has("text")) {
				onTalk(changes.get("text"));
			}

			/*
			 * Outfit
			 */
			if (changes.has("outfit_ext") || changes.has("outfit")) {
				if (changes.has("outfit_ext")) {
					outfit_ext = changes.get("outfit_ext");
					fireChange(PROP_OUTFIT);
				}
				if (changes.has("outfit")) {
					outfit_old = changes.getInt("outfit");
				}

				fireChange(PROP_OUTFIT);
			}

			/*
			 * Eating and choking
			 */
			setEatAndChoke(true, changes.has("eating"), changes.has("choking"));

			/* Statuses */
			for (StatusID id : StatusID.values()) {
				String status = id.getAttribute();
				if (changes.has(status)) {
					setStatus(id, true);
					if (status.equals(StatusID.POISON.getAttribute())) {
						// To remove the - sign on poison.
						onPoisoned(Math.abs(changes.getInt(status)));
					}
				}
			}

			/*
			 * Healed
			 */
			if (changes.has("heal")) {
				onHealed(changes.getInt("heal"));
			}

			boolean hpRatioChange = false;

			/*
			 * Base HP
			 */
			if (changes.has("base_hp")) {
				baseHP = changes.getInt("base_hp");
				hpRatioChange = true;
			}
			if (changes.has("modified_base_hp")) {
				baseHP = changes.getInt("modified_base_hp");
				hpRatioChange = true;
			}

			/*
			 * HP
			 */
			if (changes.has("hp")) {
				final int newHP = changes.getInt("hp");
				final int change = newHP - hp;

				hp = newHP;

				if (object.has("hp") && (change != 0)) {
					onHPChange(change);
				}

				hpRatioChange = true;
			}
			if (changes.has("modified_hp")) {
				final int newHP = changes.getInt("modified_hp");
				final int change = newHP - hp;

				hp = newHP;

				if (object.has("hp") && (change != 0)) {
					onHPChange(change);
				}

				hpRatioChange = true;
			}

			/*
			 * HP ratio
			 */
			if (hpRatioChange) {
				if (hp >= baseHP) {
					hpRatio = 1.0f;
				} else if (hp <= 0) {
					hpRatio = 0.0f;
				} else {
					hpRatio = hp / (float) baseHP;
				}
				if (hp == 0) {
					onDeath();
				}
				fireChange(PROP_HP_RATIO);
			}

			if (changes.has("no_hpbar")) {
				showHP = false;
				fireChange(PROP_HP_DISPLAY);
			}

			/*
			 * Attack Target
			 */

			String target = changes.get("target");
			if (target != null) {
				setTarget(target, changes.get("zoneid"));
			}

			/*
			 * Admin level
			 */
			if (changes.has("adminlevel")) {
				adminlevel = changes.getInt("adminlevel");
				fireChange(PROP_ADMIN_LEVEL);
			}

			/*
			 * Title type
			 */
			if (changes.has("title_type")) {
				titleType = changes.get("title_type");
				fireChange(PROP_TITLE_TYPE);
			}

			/*
			 * Title
			 */
			if (changes.has("class") || changes.has("name")
					|| changes.has("title")) {
				fireChange(PROP_TITLE);
			}
			if (changes.has("unnamed")) {
				showTitle = false;
				fireChange(PROP_TITLE);
			}
		}

		if (changes.has("atk")) {
			atk = changes.getInt("atk");
		}
		if (changes.has("modified_atk")) {
			atk = changes.getInt("modified_atk");
		}

		// handle def
		// basic def is overriden by modified def, as it has the last word
		// server side when determining the right value
		if (changes.has("def")) {
			def = changes.getInt("def");
		}
		if (changes.has("modified_def")) {
			def = changes.getInt("modified_def");
		}

		if (changes.has("ratk")) {
			ratk = changes.getInt("ratk");
		}
		if (changes.has("modified_ratk")) {
			ratk = changes.getInt("modified_ratk");
		}

		if (changes.has("level")) {
			level = changes.getInt("level");
		}
		if (changes.has("modified_level")) {
			level = changes.getInt("modified_level");
		}

		if (changes.has("atk_xp")) {
			atkXP = changes.getInt("atk_xp");
		}

		if (changes.has("def_xp")) {
			defXP = changes.getInt("def_xp");
		}

		if (changes.has("ratk_xp")) {
			ratkXP = changes.getInt("ratk_xp");
		}

		if (changes.has("atk_item")) {
			atkItem = changes.getInt("atk_item");
		}

		if (changes.has("def_item")) {
			defItem = changes.getInt("def_item");
		}

		if (changes.has("ratk_item")) {
			ratkItem = changes.getInt("ratk_item");
		}

		if (changes.has("mana")) {
			mana = changes.getInt("mana");
		}
		if (changes.has("modified_mana")) {
			mana = changes.getInt("modified_mana");
		}

		if (changes.has("base_mana")) {
			baseMana = changes.getInt("base_mana");
		}
		if (changes.has("modified_base_mana")) {
			baseMana = changes.getInt("modified_base_mana");
		}

		if (changes.has("ghostmode")) {
			ghostmode = true;
			fireChange(PROP_GHOSTMODE);
		}

		if (changes.has("xp")) {
			int newXp = changes.getInt("xp");

			if (object.has("xp") && (User.squaredDistanceTo(x, y) < HEARING_DISTANCE_SQ)) {
				final int amount = newXp - xp;
				if (amount > 0) {
					addTextIndicator("+" + amount,
							NotificationType.SIGNIFICANT_POSITIVE);
					ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(
							getTitle()
							+ " earns "
							+ Grammar.quantityplnoun(amount,
									"experience point") + ".",
									NotificationType.SIGNIFICANT_POSITIVE));
				} else if (amount < 0) {
					addTextIndicator(Integer.toString(amount),
							NotificationType.SIGNIFICANT_NEGATIVE);
					ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(
							getTitle()
							+ " loses "
							+ Grammar.quantityplnoun(-amount,
									"experience point") + ".",
									NotificationType.SIGNIFICANT_NEGATIVE));
				}
			}

			xp = newXp;
		}

		final Map<String, Integer> statTypes = new LinkedHashMap<>();
		statTypes.put("level", getLevel());
		statTypes.put("def", getDef());
		statTypes.put("atk", getAtk());
		statTypes.put("ratk", getRatk());

		String statChange = null;
		for (final String stype: statTypes.keySet()) {
			if (changes.has(stype) && object.has(stype)) {
				statChange = stype;
				break;
			}
		}

		if (statChange != null && (User.squaredDistanceTo(x, y) < HEARING_DISTANCE_SQ)) {
			final StringBuilder sb = new StringBuilder(getTitle());
			if (!statChange.equals("level")) {
				sb.append("'s " + statChange.toUpperCase());
			}
			sb.append(" reaches level " + Integer.toString(statTypes.get(statChange)));

			final String text = sb.toString();
			ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(text,
					NotificationType.SIGNIFICANT_POSITIVE));

			ClientSingletonRepository.getScreenController().addText(
					getX() + (getWidth() / 2.0), getY(),
					text, NotificationType.SIGNIFICANT_POSITIVE, false);
		}
	}

	/**
	 * Called when the entity dies.
	 */
	private void onDeath() {
	    playSoundFromCategory(SoundLayer.FIGHTING_NOISE.groupName, "death");
	}

	/**
	 * The object removed attribute(s).
	 *
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	@Override
	public void onChangedRemoved(final RPObject object, final RPObject changes) {
		super.onChangedRemoved(object, changes);

		/*
		 * Outfit
		 */
		if (changes.has("outfit_ext") || changes.has("outfit")) {
			if (changes.has("outfit_ext")) {
				outfit_ext = null;
			}
			if (changes.has("outfit")) {
				outfit_old = OUTFIT_UNSET;
				/*
				outfitMouth = OUTFIT_UNSET;
				outfitEyes = OUTFIT_UNSET;
				outfitMask = OUTFIT_UNSET;
				outfitHat = OUTFIT_UNSET;
				*/
			}

			fireChange(PROP_OUTFIT);
		}

		/*
		 * No longer has status. The iterator of EnumSet is safe despite the
		 * modification in the loop.
		 */
		for (StatusID status : statuses) {
			if (changes.has(status.getAttribute())) {
				setStatus(status, false);
			}
		}

		/*
		 * No longer eating or choking?
		 */
		setEatAndChoke(false, changes.has("eating"), changes.has("choking"));

		if (changes.has("ghostmode")) {
			ghostmode = false;
			fireChange(PROP_GHOSTMODE);
		}

		/*
		 * Attack target gone?
		 */
		if (changes.has("target")) {
			onStopAttack();

			if (attackTarget != null) {
				attackTarget.onStopAttacked(this);
				attackTarget = null;
			}
		}

		/*
		 * Title
		 */
		if (changes.has("unnamed")) {
			showTitle = true;
			fireChange(PROP_TITLE);
		}

		if (changes.has("no_hpbar")) {
			showHP = true;
			fireChange(PROP_HP_DISPLAY);
		}
	}

	/**
	 * Check if the entity view should show the title.
	 *
	 * @return <code>true</code>, if the title should be displayed,
	 * 	<code>false</code> if it should be hidden
	 */
	public boolean showTitle() {
		return showTitle;
	}

	/**
	 * Check if the entity view should show the HP indicator.
	 *
	 * @return <code>true</code>, if the HP bar should be displayed,
	 * 	<code>false</code> if it should be hidden
	 */
	public boolean showHPBar() {
		return showHP;
	}

	/**
	 * Check if a shadow should be drawn under the entity.
	 *
	 * @return
	 * 		<code>true</code> if a shadow should be drawn,
	 * 		<code>false</code> if not.
	 */
	public boolean castsShadow() {
		return castShadow;
	}

	/**
	 * Retrieves the name that should be used to override shadow.
	 *
	 * @return
	 * 		String path to shadow file to use or <code>null</code>.
	 */
	public String getShadowStyle() {
		if (shadowStyle == null) {
			return null;
		}

		return "data/sprites/shadow/" + shadowStyle + ".png";
	}
}
