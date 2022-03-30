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
package games.stendhal.server.entity.item;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.common.constants.Nature;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.ItemLogger;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.EquipListener;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.behavior.UseBehavior;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.status.StatusType;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * This is an item.
 */
public class Item extends PassiveEntity implements TurnListener, EquipListener,
	UseListener {

	private static final int DEFAULT_ATTACK_RATE = 5;

	/**
	 * max degree of deterioration
	 */
	private static final int MAX_DETERIORATION = 100;

	/**
	 * default value for deterioration implies each item is okay if ne
	 */
	private static final int DEFAULT_DETERIORATION = 0;

	/**
	 * mean time an item is constantly usable in hours
	 */
	private static final long MEAN_LIFETIME = 12 * MathHelper.MILLISECONDS_IN_ONE_HOUR;

	// 10 minutes
	public static final int DEGRADATION_TIMEOUT = 10 * MathHelper.SECONDS_IN_ONE_MINUTE;


	/** list of possible slots for this item. */
	private List<String> possibleSlots;

	/**
	 * The plant grower where this item was grown, until it has been picked.
	 * null if it wasn't grown by a plant grower, or if it has already been
	 * picked.
	 */
	private PassiveEntityRespawnPoint plantGrower;

	/** The damage type of weapons */
	private Nature damageType = Nature.CUT;

	private Map<Nature, Double> susceptibilities;

	private boolean fromCorpse = false;

	/**
	 * Use behavior implementation, or <code>null</code> if the item does not
	 * have any, or if the behavior is implemented in a subclass.
	 */
	private UseBehavior useBehavior;

	/**
	 *
	 * Creates a new Item.
	 *
	 * @param name
	 *            name of item
	 * @param clazz
	 *            class (or type) of item
	 * @param subclass
	 *            subclass of this item
	 * @param attributes
	 *            attributes (like attack). may be empty or <code>null</code>
	 */
	public Item(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		this();

		setEntityClass(clazz);
		setEntitySubclass(subclass);

		put("name", name);

		if (attributes != null) {
			// store all attributes
			for (final Entry<String, String> entry : attributes.entrySet()) {
				put(entry.getKey(), entry.getValue());
			}
		}

		update();
	}

	/** no public 'default' item. */
	private Item() {
		setRPClass("item");
		put("type", "item");
		possibleSlots = new LinkedList<String>();
		update();
	}

	/**
	 * copy constructor.
	 *
	 * @param item
	 *            item to copy
	 */
	public Item(final Item item) {
		super(item);
		setRPClass("item");
		possibleSlots = new ArrayList<String>(item.possibleSlots);
		damageType = item.damageType;
		susceptibilities = item.susceptibilities;
	}

	public static void generateRPClass() {
		final RPClass entity = new RPClass("item");
		entity.isA("entity");

		// class, sword/armor/...
		entity.addAttribute("class", Type.STRING);

		// subclass, long sword/leather/armor/...
		entity.addAttribute("subclass", Type.STRING);

		// name of item (ie 'Kings Sword')
		entity.addAttribute("name", Type.STRING);

		// Some items have attack values
		entity.addAttribute("atk", Type.SHORT, Definition.HIDDEN);

		// Some items have ranged attack values
		entity.addAttribute("ratk", Type.SHORT, Definition.HIDDEN);

		// Some items indicate how often you can attack.
		entity.addAttribute("rate", Type.SHORT, Definition.HIDDEN);

		// Some items have defense values
		entity.addAttribute("def", Type.SHORT, Definition.HIDDEN);

		//Some items can be damaged in combat or during use. This rises the degree of deterioration
		entity.addAttribute("deterioration", Type.INT, (byte) (Definition.HIDDEN | Definition.VOLATILE));

		// Some items(food) have amount of something
		// (a bottle, a piece of meat).
		entity.addAttribute("amount", Type.INT);

		// Some items (range weapons, ammunition, missiles)
		// have a range.
		entity.addAttribute("range", Type.SHORT, Definition.HIDDEN);

		// Some items(food) have regeneration
		entity.addAttribute("regen", Type.INT, Definition.HIDDEN);

		// Some items(food) have regeneration speed
		entity.addAttribute("frequency", Type.INT, Definition.HIDDEN);

		// Some items(Stackable) have quantity
		entity.addAttribute("quantity", Type.INT);

		// Some items (Stackable) have maximum quantity
		entity.addAttribute("max_quantity", Type.INT, Definition.HIDDEN);

		// Some items have minimum level to prevent spoiling
		// the fun for new players
		entity.addAttribute("min_level", Type.INT, Definition.HIDDEN);

		// To store addAttributeitional info with an item
		entity.addAttribute("infostring", Type.STRING, Definition.HIDDEN);

		// Some items have individual values
		entity.addAttribute("persistent", Type.SHORT, Definition.HIDDEN);

		// Some items have lifesteal values
		entity.addAttribute("lifesteal", Type.FLOAT, Definition.HIDDEN);

		// Items that protect against poisoning
		entity.addAttribute("antipoison", Type.FLOAT, (byte) (Definition.HIDDEN | Definition.VOLATILE));

		// Consumable items the heas status effects
		entity.addAttribute("immunization", Type.STRING, (byte) (Definition.HIDDEN | Definition.VOLATILE));

		// Some items are quest rewards that other players
		// don't deserve. Not hidden because the client uses it for an anti
		// theft hack
		entity.addAttribute("bound", Type.STRING);

		// Some items should not be dropped on death
		entity.addAttribute("undroppableondeath", Type.SHORT, Definition.HIDDEN);

		// Offers life support for certain environments (e. g. scuba gear)
		entity.addAttribute("life_support", Type.STRING, Definition.HIDDEN);

		// Unique database ID for logging
		entity.addAttribute("logid", Type.INT, Definition.HIDDEN);

		// Name of the container slot, if the item has one
		entity.addAttribute("slot_name", Type.STRING, (byte) (Definition.HIDDEN | Definition.VOLATILE));

		// Size of the container slot, if the item has one, and the size is
		// other than the default
		entity.addAttribute("slot_size", Type.STRING, Definition.VOLATILE);

		// Container slot
		entity.addRPSlot("content", -1, Definition.PRIVATE);

		// True for items that should be bound automatically at loot (or login)
		entity.addAttribute("autobind", Type.FLAG, (byte) (Definition.HIDDEN | Definition.VOLATILE));

		// Number of uses for BreakableItem
		entity.addAttribute("durability", Type.INT, Definition.VOLATILE);
		entity.addAttribute("uses", Type.INT);

		// player that owns the item
		entity.addAttribute("owner", Type.STRING, Definition.HIDDEN);

		// name of itemset
		entity.addAttribute("itemset", Type.STRING);

		// sound played when item is used
		entity.addAttribute("use_sound", Type.STRING, Definition.VOLATILE);

		// marked scroll
		entity.addAttribute("dest", Type.STRING, Definition.VOLATILE);
	}


	/**
	 * on which slots may this item be equipped.
	 *
	 * @param slots
	 *            list of allowed slots
	 */
	public void setEquipableSlots(final List<String> slots) {
		// save slots
		possibleSlots = slots;
	}

	/**
	 * If this item is created by a PlantGrower, the item will notify it when
	 * picked from the ground.
	 *
	 * @param plantGrower
	 *            a plant grower
	 */
	public void setPlantGrower(final PassiveEntityRespawnPoint plantGrower) {
		this.plantGrower = plantGrower;
	}

	/**
	 * returns the PlantGrower which created this item or null if no plantgrower
	 * was involved.
	 *
	 * @return PlantGrower or null
	 */
	public PassiveEntityRespawnPoint getPlantGrower() {
		return plantGrower;
	}

	/**
	 * Returns the attack points of this item. Positive and negative values are
	 * allowed. If this item doesn't modify the attack it should return '0'.
	 *
	 * @return attack points
	 */
	public int getAttack() {
		if (has("atk") && getDeterioration() <= MAX_DETERIORATION) {
			return getInt("atk");
		}

		return 0;
	}

	/**
	 * Returns the defense points of this item. Positive and negative values are
	 * allowed. If this item doesn't modify the defense it should return '0'.
	 *
	 * @return defense points
	 */
	public int getDefense() {
		if (has("def") && getDeterioration() <= MAX_DETERIORATION) {
			return getInt("def");
		}

		return 0;
	}

	/**
	 * Returns the ranged attack points of this item. Positive and negative
	 * values are allowed. If this item doesn't modify the ranged attack
	 * it should return '0'.
	 *
	 * @return ranged attack points
	 */
	public int getRangedAttack() {
		if (has("ratk") && getDeterioration() <= MAX_DETERIORATION) {
			return getInt("ratk");
		}

		return 0;
	}

	/**
	 * Returns each how many turns this item can attack.
	 *
	 * @return each how many turns this item can attack.
	 */
	public int getAttackRate() {
		if (has("rate")) {
			return getInt("rate");
		}

		return DEFAULT_ATTACK_RATE;
	}

	/**
	 * Returns turn delay for this item's attack.
	 *
	 * @param meleeDistance
	 * 		<code>true</code> if the entity is standing next to its attack target.
	 * @return
	 * 		Turn delay for this items attack.
	 */
	public int getAttackRate(final boolean meleeDistance) {
		// range weapons should not use modified attack rate for melee attacks
		if (meleeDistance && has("range")) {
			return DEFAULT_ATTACK_RATE;
		}

		return getAttackRate();
	}

	/**
	 * Retrieves default attack rate for items.
	 */
	public static int getDefaultAttackRate() {
		return DEFAULT_ATTACK_RATE;
	}

	/**
	 * Each Item is subject to deterioration that weakens the item
	 *
	 * @return the current degree of deterioration
	 */
	public int getDeterioration() {
		if(has("deterioration")) {
			return getInt("deterioration");
		}
		return DEFAULT_DETERIORATION;
	}

	/**
	 * Propose increase the degree of deterioration. If degree increases is decided by random
	 */
	public void deteriorate() {
		double propabilityForMeanExp = Rand.propabilityForMeanExp(MEAN_LIFETIME / 300 * this.getAttackRate());
		if(Rand.flipCoin(propabilityForMeanExp) && getDeterioration() <= MAX_DETERIORATION) {
			Logger.getLogger(getClass()).debug("The item"+ this.getName() +"deteriorated from "+this.getDeterioration()+".");
//			this.add("deterioration", 1);
		} else {
			Logger.getLogger(getClass()).debug("The item"+ this.getName() +"did not deteriorate from "+this.getDeterioration()+".");
		}
	}

	/**
	 * Can be overridden to handle actions on user entity.
	 *
	 * @param user
	 * 		Entity using the item.
	 */
	public void deteriorate(@SuppressWarnings("unused") final RPEntity user) {
		deteriorate();
	}

	/**
	 * repair this item
	 */
	public void repair() {
		if(has("deterioration")) {
			put("deterioration", DEFAULT_DETERIORATION);
		}
	}

	/**
	 * Returns if the item is persistent. Persistent items do not update their
	 * stats from the item database and thus can have individual stats
	 *
	 * @return true if item is persistent
	 */
	public boolean isPersistent() {
		if (has("persistent")) {
			return (getInt("persistent") == 1);
		}

		return false;
	}

	/**
	 * Set the item's persistence.
	 *
	 * @param persistent
	 *            If the item's stats are persistent.
	 */
	public void setPersistent(final boolean persistent) {
		if (persistent) {
			put("persistent", 1);
		} else if (has("persistent")) {
			remove("persistent");
		}
	}

	/**
	 * Checks if the item is of type <i>type</i> .
	 *
	 * @param clazz
	 *            the class to check
	 * @return true if the type matches, else false
	 */
	public boolean isOfClass(final String clazz) {
		return getItemClass().equals(clazz);
	}

	/** @return the type of the item */
	public String getItemClass() {
		if (has("class")) {
			return get("class");
		}

		throw new IllegalStateException("the item does not have a class: "
				+ this);
	}

	/** @return the subclass of the item */
	public String getItemSubclass() {
		if (has("subclass")) {
			return get("subclass");
		}

		throw new IllegalStateException("the item does not have a subclass: "
				+ this);
	}

	/**
	 * Gets the name of the item.
	 *
	 * @return The programmatic item name.
	 */
	@Override
	public String getName() {
		return get("name");
	}

	/**
	 * Get item count.
	 *
	 * @return 1.
	 */
	public int getQuantity() {
		return 1;
	}

	/** @return the list of possible slots for this item */
	public List<String> getPossibleSlots() {
		return possibleSlots;
	}

	/**
	 * Get the player this is bound to. A bound item can only be used by that
	 * player.
	 *
	 * @return The player name, or <code>null</code>.
	 */
	public String getBoundTo() {
			return get("bound");
	}

	/**
	 * Check if the item is bound to a player
	 *
	 * @return <code>true</code> if the item is bound, otherwise
	 * 	<code>false</code>
	 */
	public boolean isBound() {
		return has("bound");
	}

	/**
	 * Bind the item to a player, if the item is one that should be
	 * automatically bound, and the item is not already bound.
	 *
	 * @param player player name
	 */
	public void autobind(String player) {
		if (!isBound() && has("autobind")) {
			setBoundTo(player);
		}
	}

	/**
	 * Get the item's infoString. The infoString contains context specific
	 * information that is used by the implementation.
	 *
	 * @return The infoString.
	 */
	public String getInfoString() {
		if (has("infostring")) {
			return get("infostring");
		} else {
			return null;
		}
	}

	/**
	 * Bind this item to a player. A bound item can only be used by that player.
	 *
	 * @param name
	 *            The player name, or <code>null</code>.
	 */
	public void setBoundTo(final String name) {
		if (name != null) {
			put("bound", name);
		} else if (has("bound")) {
			remove("bound");
		}
	}

	/**
	 * Is the item undroppable.
	 *
	 * On player death items carried may be dropped into the players corpse.
	 * unless this method returns true.
	 *
	 *
	 * @return true if item may not be dropped on death of players.
	 */
	public boolean isUndroppableOnDeath() {
		if (has("undroppableondeath")) {
			return (getInt("undroppableondeath") == 1);
		}

		return false;
	}

	/**
	 * Set is the item undroppable when player dies.
	 *
	 * @param unDroppableOnDeath
	 *            If true, the item won't be dropped if the player dies.
	 */
	public void setUndroppableOnDeath(final boolean unDroppableOnDeath) {
		if (unDroppableOnDeath) {
			put("undroppableondeath", 1);
		} else if (has("undroppableondeath")) {
			remove("undroppableondeath");
		}
	}

	/**
	 * Set behavior to follow when the item is used.
	 *
	 * @param behavior new behavior
	 */
	public void setUseBehavior(UseBehavior behavior) {
		useBehavior = behavior;
	}

	/**
	 * Set the item's infostring. The infostring contains context specific
	 * information that is used by the implementation.
	 *
	 * @param infostring
	 *            The item's infostring.
	 */
	public void setInfoString(final String infostring) {
		if (infostring != null) {
			put("infostring", infostring);
		} else if (has("infostring")) {
			remove("infostring");
		}
	}

	/**
	 * Get the type of damage inflicted by this item.
	 *
	 * @return type of damage
	 */
	public Nature getDamageType() {
		return damageType;
	}

	/**
	 * Set the type of damage inflicted by this item
	 *
	 * @param type type of damage
	 */
	public void setDamageType(Nature type) {
		damageType = type;
	}

	/**
	 * Get this item's contribution to susceptibility to a
	 * type of damage.
	 *
	 * @param type type of damage to be checked
	 * @return susceptibility to damage of type <code>type</code>
	 */
	public double getSusceptibility(Nature type) {
		double value = 1.0;
		if (susceptibilities != null) {
			Double sus = susceptibilities.get(type);
			if (sus != null) {
				value = sus.doubleValue();
			}
		}

		return value;
	}

	/**
	 * Set the susceptibility data of this item.
	 *
	 * @param susceptibilities susceptibilities to be used
	 */
	public void setSusceptibilities(Map<Nature, Double> susceptibilities) {
		this.susceptibilities = susceptibilities;
	}

	/**
	 * Override in SlotActivatedItem sub-class.
	 */
	public void initializeActiveSlotsList(final List<String> slotList) {
		// Do nothing
	}

	/**
	 * Override in StatusResistantItem sub-class.
	 */
	public void initializeStatusResistancesList(Map<StatusType,
			Double> resistanceList) {
		// Do nothing
	}

	/**
	 * Get a weapon type identifier that can be used in attack events, so that
	 * the client can draw an appropriate item image for the attack.
	 *
	 * @return weapon identifier
	 */
	public String getWeaponType() {
		return getItemClass();
	}

	@Override
	public String toString() {
		return "Item, " + super.toString();
	}

	/**
	 * Is called when the item is moved to the ground.
	 *
	 * @param player the player who drops the Item.
	 *
	 */
	public void onPutOnGround(final Player player) {
		onPutOnGround(true);
	}

	/**
	 * Is called when the item is created.
	 *
	 * @param expire
	 * 		Set true if the item should expire normally, false otherwise.
	 */
	public void onPutOnGround(final boolean expire) {
		if (expire) {
			SingletonRepository.getTurnNotifier().notifyInSeconds(DEGRADATION_TIMEOUT, this);
		}
	}

	public void onRemoveFromGround() {
		// stop the timer so that the item won't degrade anymore
		SingletonRepository.getTurnNotifier().dontNotify(this);
		if (plantGrower != null) {
			plantGrower.onFruitPicked(this);
		}
	}

	public void onPickedUp(Player player) {
		if (fromCorpse && this.plantGrower != null) {
			this.plantGrower.onItemPickedUp(player);
			this.fromCorpse = false;
		}
	}

	@Override
	public void onTurnReached(final int currentTurn) {
		// remove this object from the zone where it's lying on the ground
		if (getZone() != null) {
			if (this.hasSlot("content")) {
				for (RPObject obj : getSlot("content")) {
					new ItemLogger().timeout((Item) obj);
				}
			}
			getZone().remove(getID());
			new ItemLogger().timeout(this);
		}
	}

	@Override
	public String describe() {
		String text = "You see " + Grammar.a_noun(getTitle()) + ".";
		StringBuilder stats = new StringBuilder();
		String levelwarning = "";
		if (hasDescription()) {
			text = getDescription();
		}
		// Highlight the item name
		text = text.replace(getTitle(), "§'" + getTitle() + "'");

		final String boundTo = getBoundTo();

		if (boundTo != null) {
			text = text + " It is a special reward for " + boundTo
					+ ", and cannot be used by others.";
		}

		if (has("atk")) {
			stats.append("ATK: ");
			stats.append(get("atk"));
			// Show only special types
			if (getDamageType() != Nature.CUT) {
				stats.append(" [");
				stats.append(getDamageType());
				stats.append("]");
			}
		}
		if (has("def")) {
			stats.append(" DEF: ");
			stats.append(get("def"));
		}
		if (has("ratk")) {
			stats.append(" RATK: ");
			stats.append(get("ratk"));
			// Show only special types
			if (getDamageType() != Nature.CUT) {
				stats.append(" [");
				stats.append(getDamageType());
				stats.append("]");
			}
		}
		if (has("rate")) {
			stats.append(" RATE: ");
			stats.append(get("rate"));
		}
		if (has("amount")) {
			stats.append(" HP: ");
			stats.append(get("amount"));
		}
		if (has("range")) {
			stats.append(" RANGE: ");
			stats.append(get("range"));
		}
		if (has("lifesteal")) {
			stats.append(" LIFESTEAL: ");
			stats.append(get("lifesteal"));
		}
		if ((susceptibilities != null) && !susceptibilities.isEmpty()) {
			for (Entry<Nature, Double> entry : susceptibilities.entrySet()) {
				stats.append(" ");
				stats.append(entry.getKey());
				stats.append(": ");
				stats.append(Math.round(100/entry.getValue()));
				stats.append("%");
			}
		}

		if (has("min_level")) {
			stats.append(" MIN-LEVEL: ");
			stats.append(get("min_level"));
		}
		if (has("life_support")) {
			stats.append(" LIFE-SUPPORT: ");
			stats.append(get("life_support"));
		}
		String statString = "";
		if (stats.length() > 0) {
			statString =  " Stats are (" + stats.toString().trim() + ").";
		}
		return (text + levelwarning + statString);
	}

	/**
	 * Removes the item. In case of StackableItems only one is removed.
	 */
	public void removeOne() {
		removeFromWorld();
	}

	@Override
	public boolean canBeEquippedIn(final String slot) {
		if (slot == null) {
			// ground
			return true;
		}

		return possibleSlots.contains(slot);
	}


	public void removeFromWorld() {

		this.onUnequipped();

		if (isContained()) {
			// We modify the base container if the object change.
			RPObject base = getContainer();

			while (base.isContained()) {
				base = base.getContainer();
			}

			final RPSlot slot = getContainerSlot();
			slot.remove(getID());

			SingletonRepository.getRPWorld().modify(base);
		} else {
			SingletonRepository.getRPWorld().remove(getID());
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
	 *            <code>true</code> for "the", and <code>false</code> for
	 *            "a/an" in case the entity has no name.
	 *
	 * @return The description name.
	 */
	@Override
	public String getDescriptionName(final boolean definite) {
		final String name = getName();

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
		final String name = getName();

		if (name != null) {
			return name;
		} else {
			return super.getTitle();
		}
	}

	public void setFromCorpse(boolean fromCorpse) {
		this.fromCorpse = fromCorpse;
	}

	public boolean isFromCorpse() {
		return fromCorpse;
	}

	/**
	 * gets the min level
	 *
	 * @return minLevel
	 */
	public int getMinLevel() {
		if (super.has("min_level")) {
			return super.getInt("min_level");
		} else {
			return 0;
		}
	}


	/**
	 * opportunity to affect the player when equipped
	 *
	 * currently returns boolean, to indicate whether it made any change
	 *
	 * TODO: should this return some sort of undoable thing (if it can be undone?)
	 *
	 * @param equipper entity equipping the item
	 * @param slot slot where the item is equipped
	 * @return unknown, see the note above
	 */
	public boolean onEquipped(RPEntity equipper, String slot) {

		// this.prevEntity = equipper;
		// this.prevSlot   = slot;

		return false;
	}

	/**
	 * item is about to be removed from owner.
	 * at this time, we are still owned by the original owner.
	 * opportunity to affect the player when equipped
	 *
	 * note: use this.getContainerOwner(), rather than the equipper param
	 *
	 * TODO: i don't think the parameters are necessary.  can get
	 *       owner and slot from Item api
	 * @return needs documenting
	 */
	public boolean onUnequipped() {
		return false;
	}

	@Override
	public boolean onUsed(RPEntity user) {
		if (useBehavior != null) {
			return useBehavior.use(user, this);
		}

		return false;
	}
}
