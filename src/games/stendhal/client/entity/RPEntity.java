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
package games.stendhal.client.entity;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.GameLoop;
import games.stendhal.client.GameObjects;
import games.stendhal.client.stendhal;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.client.gui.chatlog.StandardEventLine;
import games.stendhal.client.gui.chatlog.StandardHeaderedEventLine;
import games.stendhal.common.ItemTools;
import games.stendhal.common.NotificationType;
import games.stendhal.common.constants.Nature;
import games.stendhal.common.grammar.Grammar;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPObject.ID;

import org.apache.log4j.Logger;

/**
 * This class is a link between client graphical objects and server attributes
 * objects.<br>
 * You need to extend this object in order to add new elements to the game.
 */
public abstract class RPEntity extends ActiveEntity {
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
	 * Eating property.
	 */
	public static final Property PROP_EATING = new Property();
	/**
	 * Poisoned property.
	 */
	public static final Property PROP_POISONED = new Property();
	/**
	 * Attacking property. (for attack events)
	 */
	public static final Property PROP_ATTACK = new Property();
	
	/**
	 * The value of an outfit that isn't set.
	 */
	public static final int OUTFIT_UNSET = -1;
	
	private static final Logger LOGGER = Logger.getLogger(RPEntity.class);


	/**
	 * Entity we are attacking. (need to reconsile this with 'attacking')
	 */
	protected RPEntity attackTarget;

	/**
	 * The entities attacking this entity.
	 */
	protected final Collection<Entity> attackers = new ConcurrentLinkedQueue<Entity>();
	
	/**
	 * The nature of the current attack done by this entity, or
	 * <code>null</code> if there's no ongoing attack.
	 */
	private Nature attackNature;
	/**
	 * <code>true</code> if the previously done attack event was ranged,
	 * 	otherwise <code>false</code>. 
	 */
	private boolean isDoingRangedAttack;
	/**
	 * Flag for checking attack targets that were added in the zone later than
	 * this entity.
	 */
	private boolean targetUpdated = false;

	public enum Resolution {
		HIT,
		BLOCKED,
		MISSED;
	}
	
	

	private int atk;

	private int def;

	private int xp;

	private int hp;

	private int adminlevel;

	/**
	 * The outfit code.
	 */
	private int outfit;

	private int base_hp;

	private float hp_base_hp;

	private int level;

	private boolean eating;

	private boolean poisoned;

	private boolean choking;

	/**
	 * Time stamp of previous attack event. Volatile to prevent reordering
	 * assignments with <code>resolution</code>.
	 */
	private volatile long combatIconTime;

	private final List<TextIndicator> textIndicators;

	private RPObject.ID attacking;

	private int mana;

	private int base_mana;

	private boolean ghostmode;
	
	private boolean ignoreCollision;

	private String titleType;

	

	/**
	 * The result of previous attack against this entity. Volatile to prevent
	 * reordering assignments with <code>combatIconTime</code>.
	 */
	private volatile Resolution resolution;

	private int atkXP;

	private int defXp;

	private int atkItem = -1;

	private int defItem = -1;
	
	/** A flag that gets set once the entity has been released */
	private boolean released;

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
		return base_hp;
	}

	/**
	 * @return Returns the base mana value
	 */
	public int getBaseMana() {
		return base_mana;
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
		return defXp;
	}

	public int getHP() {
		return hp;
	}

	/**
	 * Get the ratio of HP to base HP.
	 * 
	 * @return The HP ratio (0.0 - 1.0).
	 */
	public float getHpRatio() {
		return hp_base_hp;
	}

	/**
	 * Get the list of text indicator elements.
	 * 
	 * @return An iterator of text indicators.
	 */
	public Iterator<TextIndicator> getTextIndicators() {
		return textIndicators.iterator();
	}

	public int getLevel() {
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
	public int getOutfit() {
		return outfit;
	}
	
	/**
	 * Get a color that should be for coloring an outfit part.
	 * 
	 * @param part the outfit part
	 * @return color as a string, or <code>null</code> if the outfit part should
	 * 	not use coloring
	 */
	public String getOutfitColor(String part) {
		return rpObject.get("outfit_colors", part);
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
	 * @return The title type.
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

	public boolean isBeingAttacked() {
		return !attackers.isEmpty();
	}

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
				- combatIconTime < 4 * 300));
	}

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

	public boolean ignoresCollision() {
		return ignoreCollision;
	}
	
	public boolean isPoisoned() {
		return poisoned;
	}

	public boolean isChoking() {
		return choking;
	}

	// TODO: this is just an ugly workaround to avoid cyclic dependencies with
	// Creature
	protected void nonCreatureClientAddEventLine(final String text) {
		ClientSingletonRepository.getUserInterface().addEventLine(new StandardHeaderedEventLine(getTitle(), text));
	}

	// When this entity attacks target.
	public void onAttack(final IEntity target) {
		attacking = target.getID();
	}

	/**
	 * When this entity's attack is blocked by the adversary.
	 * 
	 * @param type attack nature
	 * @param ranged
	 */
	public void onAttackBlocked(final Nature type, boolean ranged) {
		attackNature = type;
		isDoingRangedAttack = ranged;
		fireChange(PROP_ATTACK);
	}

	/**
	 * When this entity causes damaged to adversary, with damage amount
	 * 
	 * @param type
	 * @param ranged
	 */
	public void onAttackDamage(final Nature type, boolean ranged) {
		attackNature = type;
		isDoingRangedAttack = ranged;
		fireChange(PROP_ATTACK);
	}

	/**
	 * When this entity's attack is missing the adversary
	 * 
	 * @param type
	 * @param ranged
	 */
	public void onAttackMissed(final Nature type, boolean ranged) {
		attackNature = type;
		isDoingRangedAttack = ranged;
		fireChange(PROP_ATTACK);
	}

	// When attacker attacks this entity.
	public void onAttacked(final Entity attacker) {
		attackers.remove(attacker);
		attackers.add(attacker);
	}

	// When this entity blocks the attack by attacker
	public void onBlocked(final IEntity attacker) {
		// Resolution must be set before isDefending may return true.
		resolution = Resolution.BLOCKED;
		combatIconTime = System.currentTimeMillis();
	}

	
	
	// When this entity is damaged by attacker with damage amount
	public void onDamaged(final Entity attacker, final int damage) {
		// Resolution must be set before isDefending may return true.
		resolution = Resolution.HIT;
		combatIconTime = System.currentTimeMillis();
		/*try {
			SoundSystemFacade.get().play(attackSounds[Rand.rand(attackSounds.length)], x, y, SoundLayer.CREATURE_NOISE, 100);
		} catch (final NullPointerException e) {
			// ignore errors
		}*/

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

	// When entity gets healed
	public void onHealed(final int amount) {
		// do nothing for normal rpentities
	}
	
	// When entity adjusts HP
	public void onHPChange(final int amount) {
		if (User.squaredDistanceTo(x, y) < 15 * 15) {
			if (amount > 0) {
				addTextIndicator("+" + amount, NotificationType.POSITIVE);
			} else {
				addTextIndicator(String.valueOf(amount),
						NotificationType.NEGATIVE);
			}
		}
	}

	// When this entity skip attacker's attack.
	public void onMissed(final IEntity attacker) {
		// Resolution must be set before isDefending may return true.
		resolution = Resolution.MISSED;
		combatIconTime = System.currentTimeMillis();
	}

	// When entity is poisoned
	public final void onPoisoned(final int amount) {
		setPoisoned(true);
		if ((User.squaredDistanceTo(x, y) < 15 * 15)) {
			ClientSingletonRepository.getUserInterface().addEventLine(
					new HeaderLessEventLine(
							getTitle() + " is poisoned, losing "
							+ Grammar.quantityplnoun(amount, "health point")
							+ ".", NotificationType.POISON));
		}
	}
	
	/**
	 * Set the poisoning status.
	 * 
	 * @param poisoned
	 */
	private void setPoisoned(boolean poisoned) {
		if (this.poisoned != poisoned) {
			this.poisoned = poisoned;
			fireChange(PROP_POISONED);
		}
	}

	// Called when entity listen to text from talker
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
			ClientSingletonRepository.getUserInterface().addGameScreenText(
					getX() + (getWidth() / 2.0), getY(),
					text.replace("|", ""), type, false);
		}
	}

	// When this entity stops attacking
	public void onStopAttack() {
		attacking = null;
	}
	
	// When attacker stop attacking us
	public void onStopAttacked(final IEntity attacker) {
		attackers.remove(attacker);
	}
	
	public void onReachAchievement(String achievementTitle, String achievementDescription, String achievementCategory) {
		ClientSingletonRepository.getUserInterface().addAchievementBox(achievementTitle, achievementDescription, achievementCategory);
	}

	// Called when entity says text
	public void onTalk(final String text) {
		if (User.isAdmin() || (User.squaredDistanceTo(x, y) < 15 * 15)) {
			String line = text.replace("|", "");
			
			//an emote action is changed server side to an chat action with a leading !me
			//this supports also invoking an emote with !me instead of /me
			if (text.startsWith("!me")) {
				line = line.replace("!me", getTitle());
				ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(line, NotificationType.EMOTE));
				
				return;
			} else {
				//add the original version
				nonCreatureClientAddEventLine(text);
			}

			// Allow for more characters and cut the text if possible at the
			// nearest space etc.
			if (line.length() > 84) {
				line = line.substring(0, 84);
				int l = line.lastIndexOf(" ");
				int ln = line.lastIndexOf("-");

				if (ln > l) {
					l = ln;
				}

				ln = line.lastIndexOf(".");

				if (ln > l) {
					l = ln;
				}

				ln = line.lastIndexOf(",");

				if (ln > l) {
					l = ln;
				}

				if (l > 0) {
					line = line.substring(0, l);
				}

				line = line + " ...";
			}

			ClientSingletonRepository.getUserInterface().addGameScreenText(
					getX() + getWidth(), getY(), line,
					NotificationType.NORMAL, true);
		}
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
			base_hp = object.getInt("base_hp");
		} else {
			base_hp = 0;
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
		if (hp >= base_hp) {
			hp_base_hp = 1.0f;
		} else if (hp <= 0) {
			hp_base_hp = 0.0f;
		} else {
			hp_base_hp = hp / (float) base_hp;
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
		if (object.has("outfit")) {
			outfit = object.getInt("outfit");
		} else {
			outfit = OUTFIT_UNSET;
		}

		/*
		 * eating and choking
		 */
		setEatAndChoke(true, object.has("eating"), object.has("choking"));

		/*
		 * Poisoned
		 */
		if (object.has("poisoned")) {
			// Don't call onPoisoned to avoid adding event lines; just set
			// poisoned so that views get correctly drawn.
			setPoisoned(true);
		}

		/*
		 * Ghost mode feature.
		 */
		if (object.has("ghostmode")) {
			ghostmode = true;
		}
		
		/*
		 * Ignoring collision.
		 */
		if (object.has("ignore_collision")) {
			ignoreCollision = true;
		}

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
		if (object.has("title_type")) {
			titleType = object.get("title_type");
		} else {
			titleType = null;
		}
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
			if (changes.has("outfit")) {
				outfit = changes.getInt("outfit");
				fireChange(PROP_OUTFIT);
			}

			/*
			 * Eating and choking
			 */
			setEatAndChoke(true, changes.has("eating"), changes.has("choking"));

			/*
			 * Poisoned
			 */
			if (changes.has("poisoned")) {
				// To remove the - sign on poison.
				onPoisoned(Math.abs(changes.getInt("poisoned")));
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
				base_hp = changes.getInt("base_hp");
				hpRatioChange = true;
			}
			if (changes.has("modified_base_hp")) {
				base_hp = changes.getInt("modified_base_hp");
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
				if (hp >= base_hp) {
					hp_base_hp = 1.0f;
				} else if (hp <= 0) {
					hp_base_hp = 0.0f;
				} else {
					hp_base_hp = hp / (float) base_hp;
				}
				if (hp == 0) {
					onDeath(attackers);
				}
				fireChange(PROP_HP_RATIO);
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
			defXp = changes.getInt("def_xp");
		}

		if (changes.has("atk_item")) {
			atkItem = changes.getInt("atk_item");
		}

		if (changes.has("def_item")) {
			defItem = changes.getInt("def_item");
		}

		if (changes.has("mana")) {
			mana = changes.getInt("mana");
		}
		if (changes.has("modified_mana")) {
			mana = changes.getInt("modified_mana");
		}

		if (changes.has("base_mana")) {
			base_mana = changes.getInt("base_mana");
		}
		if (changes.has("modified_base_mana")) {
			base_mana = changes.getInt("modified_base_mana");
		}

		if (changes.has("ghostmode")) {
			ghostmode = true;
			fireChange(PROP_GHOSTMODE);
		}

		if (changes.has("xp")) {
			int newXp = changes.getInt("xp"); 
			
			if (object.has("xp")) {
				if (User.squaredDistanceTo(x, y) < 15 * 15) {
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
						addTextIndicator("" + amount,
								NotificationType.SIGNIFICANT_NEGATIVE);
						ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(
								getTitle()
								+ " loses "
								+ Grammar.quantityplnoun(-amount,
								"experience point") + ".",
								NotificationType.SIGNIFICANT_NEGATIVE));
					}
				}
			}
			
			xp = newXp;
		}

		if (changes.has("level") && object.has("level")) {
			if (User.squaredDistanceTo(x, y) < 15 * 15) {
				final String text = getTitle() + " reaches Level " + getLevel();
				ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(text,
						NotificationType.SIGNIFICANT_POSITIVE));

				ClientSingletonRepository.getUserInterface().addGameScreenText(
						getX() + (getWidth() / 2.0), getY(),
						text, NotificationType.SIGNIFICANT_POSITIVE, false);
			}
		}
	}

	private void onDeath(final Collection<Entity> attackers) {
		if (!attackers.isEmpty()) {
			Collection<String> attackerNames = new LinkedList<String>();
			for(Entity attacker : attackers) {
					attackerNames.add(attacker.getTitle());
			}
			ClientSingletonRepository.getUserInterface().addEventLine(new StandardEventLine(
					getTitle() + " has been killed by " + Grammar.enumerateCollection(attackerNames)));
		}
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
		if (changes.has("outfit")) {
			outfit = OUTFIT_UNSET;
			fireChange(PROP_OUTFIT);
		}

		/*
		 * No longer poisoned?
		 */
		if (changes.has("poisoned")) {
			setPoisoned(false);
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
	}

	//
	//

	public static class TextIndicator {
		/**
		 * The age of the message (in ms).
		 */
		protected int age;

		/**
		 * The message text.
		 */
		protected String text;

		/**
		 * The indicator type.
		 */
		protected NotificationType type;

		/**
		 * Create a floating message.
		 * 
		 * @param text
		 *            The text to drawn.
		 * @param type
		 *            The indicator type.
		 */
		public TextIndicator(final String text, final NotificationType type) {
			this.text = text;
			this.type = type;

			age = 0;
		}

		//
		// TextIndicator
		//

		/**
		 * Add to the age of this message.
		 * 
		 * @param time
		 *            The amout to add.
		 * 
		 * @return The new age (in milliseconds).
		 */
		public int addAge(final int time) {
			age += time;

			return age;
		}

		/**
		 * Get the age of this message.
		 * 
		 * @return The age (in milliseconds).
		 */
		public int getAge() {
			return age;
		}

		/**
		 * Get the text message.
		 * 
		 * @return The text message.
		 */
		public String getText() {
			return text;
		}

		/**
		 * Get the indicator type.
		 * 
		 * @return The indicator type.
		 */
		public NotificationType getType() {
			return type;
		}
	}
}
