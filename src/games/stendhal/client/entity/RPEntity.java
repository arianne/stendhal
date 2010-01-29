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
import games.stendhal.client.GameObjects;
import games.stendhal.client.stendhal;
import games.stendhal.client.gui.admin.TransitionDiagram;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.client.gui.chatlog.StandardEventLine;
import games.stendhal.client.gui.chatlog.StandardHeaderedEventLine;
import games.stendhal.client.gui.imageviewer.ItemListImageViewerEvent;
import games.stendhal.client.gui.imageviewer.RPEventImageViewer;
import games.stendhal.client.sound.SoundLayer;
import games.stendhal.client.soundreview.SoundMaster;
import games.stendhal.common.Grammar;
import games.stendhal.common.ItemTools;
import games.stendhal.common.NotificationType;
import games.stendhal.common.Rand;
import games.stendhal.common.constants.Events;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.RPEvent;
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
	protected List<Entity> attackers;
	
	String[] attackSounds = { "punch-1.wav", "punch-2.wav", "punch-3.wav",
			"punch-4.wav", "punch-5.wav", "punch-6.wav", "swingaxe-1.wav",
			"slap-1.wav", "arrow-1.wav" };
	
	private boolean showBladeStrike;

	public enum Resolution {
		HIT,
		BLOCKED,
		MISSED;
	};
	
	

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

	private long combatIconTime;

	private final List<TextIndicator> textIndicators;

	private RPObject.ID attacking;

	private int mana;

	private int base_mana;

	private boolean ghostmode;

	private String guild;

	private String titleType;

	

	/**
	 * The type of effect to show.
	 * 
	 * These are NOT mutually exclusive - Maybe use bitmask and apply in
	 * priority order.
	 */
	private Resolution resolution;

	private int atkXp;

	private int defXp;

	private int atkItem = -1;

	private int defItem = -1;

	/** Creates a new game entity. */
	RPEntity() {
		textIndicators = new LinkedList<TextIndicator>();
		attackTarget = null;
		attackers = new LinkedList<Entity>();
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
	public int getAtkXp() {
		return atkXp;
	}

	/**
	 * @return Returns the base_hp.
	 */
	public int getBase_hp() {
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
	public int getDefXp() {
		return defXp;
	}

	public String getGuild() {
		return guild;
	}

	public int getHP() {
		return hp;
	}

	/**
	 * Get the ratio of HP to base HP.
	 * 
	 * @return The HP ratio (0.0 - 1.0).
	 */
	public float getHPRatio() {
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

	public Resolution getResolution() {
		return resolution;
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
	 * @return Returns the xp.
	 */
	public int getXp() {
		return xp;
	}

	public boolean isAttacking() {
		return (attacking != null);
	}

	public boolean isAttacking(final IEntity defender) {
		if (defender == null) {
			return false;
		}
		
		final ID defenderID = defender.getID();
		return ((attacking != null) && attacking.equals(defenderID));
	}

	public boolean isBeingAttacked() {
		return !attackers.isEmpty();
	}

	public boolean isAttackedBy(final IEntity attacker) {
		return attackers.contains(attacker);
	}

	public boolean isBeingStruck() {
		return showBladeStrike;
	}

	public void doneStriking() {
		showBladeStrike = false;
	}

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

	public void onStartAttack(final IEntity target) {
		attackTarget = (RPEntity) target;
		this.onAttack(target);
		attackTarget.onAttacked(this);
	}
	
	// When this entity attacks target.
	public void onAttack(final IEntity target) {
		attacking = target.getID();
	}

	// When this entity's attack is blocked by the adversary
	public void onAttackBlocked(final IEntity target) {
		showBladeStrike = true;
	}

	// When this entity causes damaged to adversary, with damage amount
	public void onAttackDamage(final IEntity target, final int damage) {
		showBladeStrike = true;
	}

	// When this entity's attack is missing the adversary
	public void onAttackMissed(final IEntity target) {
		showBladeStrike = true;
	}

	// When attacker attacks this entity.
	public void onAttacked(final Entity attacker) {
		attackers.remove(attacker);
		attackers.add(attacker);
	}

	// When this entity blocks the attack by attacker
	public void onBlocked(final IEntity attacker) {
		combatIconTime = System.currentTimeMillis();
		resolution = Resolution.BLOCKED;
	}

	
	
	// When this entity is damaged by attacker with damage amount
	public void onDamaged(final Entity attacker, final int damage) {
		combatIconTime = System.currentTimeMillis();
		resolution = Resolution.HIT;
		try {
			SoundMaster.play(SoundLayer.CREATURE_NOISE, attackSounds[Rand.rand(attackSounds.length)], x, y);
		} catch (final NullPointerException e) {
			// ignore errors
		}

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

	// When entity eats food
	public final void onEat() {
		eating = true;
	}

	public void onStopEating() {
		eating = false;
	}

	// When entity gets healed
	public void onHealed(final int amount) {
		// do nothing for normal rpentities
	}

	// When entity chokes on food
	public final void onChoking() {
		choking = true;
	}

	public void onStopChoking() {
		choking = false;
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
		combatIconTime = System.currentTimeMillis();
		resolution = Resolution.MISSED;
	}

	// When entity is poisoned
	public final void onPoisoned(final int amount) {
		if ((User.squaredDistanceTo(x, y) < 15 * 15)) {
			poisoned = true;
			ClientSingletonRepository.getUserInterface().addEventLine(
					new HeaderLessEventLine(
					getTitle() + " is poisoned, losing "
							+ Grammar.quantityplnoun(amount, "health point")
							+ ".", NotificationType.NEGATIVE));
		}
	}

	public void onPoisonEnd() {
		poisoned = false;
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
	
	public void onStopAttack(final IEntity target) {
		if(attackTarget != null) {
		attackTarget.onStopAttacked(this);
		attackTarget = null;
		}
		this.onStopAttack();
	}
	
	// When attacker stop attacking us
	public void onStopAttacked(final IEntity attacker) {
		attackers.remove(attacker);
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


		for (final RPEvent event : object.events()) {
			/*
			 * Private message
			 */			
			if (event.getName().equals(Events.PRIVATE_TEXT)) {
				onPrivateListen(event.get("texttype"), event.get("text"));
			}
			if (event.getName().equals(Events.OPEN_OFFER_PANEL)) {
				onPrivateListen("normal", "Open Panel!");
			}
			/*
			 * noise / chat message
			 */
			if (event.getName().equals(Events.PUBLIC_TEXT)) {
				onTalk(event.get("text"));
			}
			
			if (event.getName().equals(Events.START_ATTACK)) {
				onStartAttack(GameObjects.getInstance().get(new RPObject.ID(
						event.getInt("target"), object.get("zoneid"))));
			}
			
			if (event.getName().equals(Events.STOP_ATTACK)) {
				onStopAttack(GameObjects.getInstance().get(new RPObject.ID(
						event.getInt("target"), object.get("zoneid"))));				
			}
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
		 * Eating
		 */
		if (object.has("eating")) {
			onEat();
		}

		/*
		 * Choking
		 */
		if (object.has("choking")) {
			onChoking();
		}
		/*
		 * Poisoned
		 */
//		if (object.has("poisoned")) {
//			// TODO: To remove the - sign on poison.
//			// onPoisoned(Math.abs(object.getInt("poisoned")));
//		}

		/*
		 * Ghost mode feature.
		 */
		if (object.has("ghostmode")) {
			ghostmode = true;
		}

		/*
		 * Healed
		 */
		if (object.has("heal")) {
			onHealed(object.getInt("heal"));
		}

		/*
		 * Attack Target
		 */
		/*
		if (object.has("target")) {
			final int target = object.getInt("target");

			final RPObject.ID targetEntityID = new RPObject.ID(target,
					object.get("zoneid"));

			//
			// TODO This is probably meaningless, as create order is
			// unpredictable, and the target entity may not have been added yet XXX
			//
			attackTarget = (RPEntity) GameObjects.getInstance().get(
					targetEntityID);
		
			if (attackTarget != null) {
				onAttack(attackTarget);
				attackTarget.onAttacked(this);
				// attackTarget.onAttacked(this,risk,damage);
			}
		} else {
			attackTarget = null;
		}
		*/
		if (attackTarget != null) {
			evaluateAttack(object, attackTarget);
		}

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

	protected void evaluateAttack(final RPObject object, final RPEntity entity) {
		int risk = 0;
		int damage = 0;

		if (object.has("risk")) {
			risk = object.getInt("risk");
		}
		if (risk == 0) {
			onAttackMissed(attackTarget);
			entity.onMissed(this);
		} else if (risk > 0) {
			if (object.has("damage")) {
				damage = object.getInt("damage");
			}
			if (damage == 0) {
				onAttackBlocked(attackTarget);
				entity.onBlocked(this);
			} else if (damage > 0) {
				onAttackDamage(attackTarget, damage);
				entity.onDamaged(this, damage);
			}
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


			for (final RPEvent event : changes.events()) {
				/*
				 * Private message
				 */
				if (event.getName().equals(Events.PRIVATE_TEXT)) {
					onPrivateListen(event.get("texttype"), event.get("text"));
				}
				/*
				 * noise / chat message
				 */
				if (event.getName().equals(Events.PUBLIC_TEXT)) {
					onTalk(event.get("text"));
				} else if (event.getName().equals(Events.SOUND)) {
					SoundMaster.play(SoundLayer.CREATURE_NOISE, event.get("sound") + ".ogg", getX(), getY());
				}

				if (event.getName().equals("transition_graph")) {
					new TransitionDiagram().showTransitionDiagram(event.get("data"));
				} else if (event.getName().equals("examine")) {
					RPEventImageViewer.viewImage(event);
				} else if (event.getName().equals("show_item_list")) {
					new ItemListImageViewerEvent(event).view();
				}
				
				if (event.getName().equals(Events.START_ATTACK)) {
					onStartAttack(GameObjects.getInstance().get(new RPObject.ID(
							event.getInt("target"), object.get("zoneid"))));
				}
				
				if (event.getName().equals(Events.STOP_ATTACK)) {
					onStopAttack(GameObjects.getInstance().get(new RPObject.ID(
							event.getInt("target"), object.get("zoneid"))));
				}
			}
	
			/*
			 * Outfit
			 */
			if (changes.has("outfit")) {
				outfit = changes.getInt("outfit");
				fireChange(PROP_OUTFIT);
			}

			/*
			 * Eating
			 */
			if (changes.has("eating")) {
				onEat();
			}

			/*
			 * Choking
			 */
			if (changes.has("choking")) {
				onChoking();
			}

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
			}

			/*
			 * Attack Target
			 */
			/*
			if (changes.has("target")) {
				final int target = changes.getInt("target");

				final RPObject.ID targetEntityID = new RPObject.ID(target,
						changes.get("zoneid"));

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
						// attackTarget.onAttacked(this,risk,damage);
					}
				}
			}
			*/
			
			if (attackTarget != null) {
				int risk;
				int damage;

				boolean thereIsEvent = false;

				if (changes.has("risk")) {
					risk = changes.getInt("risk");
					thereIsEvent = true;
				} else if (object.has("risk")) {
					risk = object.getInt("risk");
				} else {
					risk = 0;
				}

				if (changes.has("damage")) {
					damage = changes.getInt("damage");
					thereIsEvent = true;
				} else if (object.has("damage")) {
					damage = object.getInt("damage");
				} else {
					damage = 0;
				}

				if (thereIsEvent) {
					if (risk == 0) {
						onAttackMissed(attackTarget);
						attackTarget.onMissed(this);
					} else if ((risk > 0) && (damage == 0)) {
						onAttackBlocked(attackTarget);
						attackTarget.onBlocked(this);
					} else if ((risk > 0) && (damage > 0)) {
						onAttackDamage(attackTarget, damage);
						attackTarget.onDamaged(this, damage);
					}
				}
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
					|| changes.has("title") || changes.has("type")) {
				fireChange(PROP_TITLE);
			}
		}

		if (changes.has("atk")) {
			atk = changes.getInt("atk");
		}

		if (changes.has("def")) {
			def = changes.getInt("def");
		}

		if (changes.has("xp")) {
			xp = changes.getInt("xp");
		}

		if (changes.has("level")) {
			level = changes.getInt("level");
		}

		if (changes.has("atk_xp")) {
			atkXp = changes.getInt("atk_xp");
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

		if (changes.has("base_mana")) {
			base_mana = changes.getInt("base_mana");
		}

		if (changes.has("ghostmode")) {
			ghostmode = true;
			fireChange(PROP_GHOSTMODE);
		}

		if (changes.has("guild")) {
			guild = changes.get("guild");
		}

		if (changes.has("xp") && object.has("xp")) {
			if (User.squaredDistanceTo(x, y) < 15 * 15) {
				final int amount = (changes.getInt("xp") - object.getInt("xp"));
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

	private void onDeath(final List<Entity> attackers) {
		if (!attackers.isEmpty()) {
			Collection<String> attackerNames = new LinkedList<String>();
			for (Entity ent : attackers) {
					attackerNames.add(ent.getTitle());
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
			onPoisonEnd();
		}

		/*
		 * No longer eating?
		 */
		if (changes.has("eating")) {
			onStopEating();
		}

		/*
		 * No longer choking?
		 */
		if (changes.has("choking")) {
			onStopChoking();
		}

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
