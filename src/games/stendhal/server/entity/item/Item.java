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

import games.stendhal.common.Grammar;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spawner.PassiveEntityRespawnPoint;
import games.stendhal.server.events.EquipListener;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.Definition.Type;

/**
 * This is an item.
 */
public class Item extends PassiveEntity implements TurnListener, EquipListener {

	/** list of possible slots for this item */
	private List<String> possibleSlots;

	/**
	 * The plant grower where this item was grown, until it has been picked.
	 * null if it wasn't grown by a plant grower, or if it has already been
	 * picked.
	 */
	private PassiveEntityRespawnPoint plantGrower;

	public final static int DEGRADATION_TIMEOUT = 10 * 60; // 10 minutes

	public static void generateRPClass() {
		RPClass entity = new RPClass("item");
		entity.isA("entity");
		entity.addAttribute("class", Type.STRING); // class, sword/armor/...
		entity.addAttribute("subclass", Type.STRING); // subclass, long sword/leather // armor/...
		entity.addAttribute("name", Type.STRING); // name of item (ie 'Kings Sword')
		entity.addAttribute("atk", Type.SHORT); // Some items have attack values
		entity.addAttribute("rate", Type.SHORT); // Some items indicate how often you can attack.
		entity.addAttribute("def", Type.SHORT); // Some items have defense values
		entity.addAttribute("amount", Type.INT); // Some items(food) have amount of something (a bottle, a piece of meat).
		entity.addAttribute("range", Type.SHORT); // Some items (range weapons, ammunition, missiles) have a range.
		entity.addAttribute("regen", Type.INT); // Some items(food) have regeneration speed
		entity.addAttribute("frequency", Type.INT); // Some items(food) have regeneration speed
		entity.addAttribute("quantity", Type.INT); // Some items(Stackable) have quantity
		entity.addAttribute("max_quantity", Type.INT); // Some items (Stackable) have maximum quantity
		entity.addAttribute("min_level", Type.INT); // Some items have minimum level to prevent spoiling the fun for new players
		entity.addAttribute("infostring", Type.STRING); // To store addAttributeitional info with an item
		entity.addAttribute("persistent", Type.SHORT); // Some items have individual values
		entity.addAttribute("lifesteal", Type.FLOAT); // Some items have lifesteal values
		entity.addAttribute("bound", Type.STRING); // Some items are quest rewards that other players don't deserve.
	}

	/**
	 *
	 * Creates a new Item.
	 *
	 * @param name name of item
	 * @param clazz class (or type) of item
	 * @param subclass subclass of this item
	 * @param attributes attributes (like attack). may be empty or <code>null</code>
	 */
	public Item(String name, String clazz, String subclass, Map<String, String> attributes) {
		this();
		put("class", clazz);
		put("subclass", subclass);
		put("name", name);

		if (attributes != null) {
			// store all attributes
			for (String key : attributes.keySet()) {
				put(key, attributes.get(key));
			}
		}
	}

	/** no public 'default' item */
	private Item() {
		super();
		setRPClass("item");
		put("type", "item");
		possibleSlots = new LinkedList<String>();
		update();
	}

	/**
	 * copy constuctor
	 *
	 * @param item item to copy
	 */
	public Item(Item item) {
		super(item);
		setRPClass("item");
		possibleSlots = new ArrayList<String>(item.possibleSlots);
	}


	/**
	 * on which slots may this item be equiped
	 *
	 * @param slots list of allowed slots
	 */
	public void setEquipableSlots(List<String> slots) {
		// save slots
		possibleSlots = slots;
	}

	/**
	 * If this item is created by a PlantGrower,
	 * the item will notify it when picked from the ground
	 *
	 * @param plantGrower a plant grower
	 */
	public void setPlantGrower(PassiveEntityRespawnPoint plantGrower) {
		this.plantGrower = plantGrower;
	}

	/**
	 * returns the PlantGrower which created this item
	 * or null if no plantgrower was involved.
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
		if (has("atk")) {
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
		if (has("def")) {
			return getInt("def");
		}

		return 0;
	}

	/**
	 * Returns each how many turns this item can attack.
	 * @return each how many turns this item can attack.
	 */
	public int getAttackRate() {
		if (has("rate")) {
			return getInt("rate");
		}

		/* Default attack rate is 5. */
		return 5;
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
	 * Checks if the item is of type <i>type</i>
	 *
	 * @param clazz
	 *            the class to check
	 * @return true if the type matches, else false
	 */
	public boolean isOfClass(String clazz) {
		return getItemClass().equals(clazz);
	}

	/** returns the type of the item */
	public String getItemClass() {
		if (has("class")) {
			return get("class");
		}

		throw new IllegalStateException("the item does not have a class: " + this);
	}

	/** returns the type of the item */
	public String getItemSubclass() {
		if (has("subclass")) {
			return get("subclass");
		}

		throw new IllegalStateException("the item does not have a subclass: " + this);
	}

	/** returns the name of the item */
	@Override
	public String getName() {
		return get("name");
	}

	/**
	 * Get item count.
	 *
	 * @return	1.
	 */
	public int getQuantity() {
		return 1;
	}

	/** returns the list of possible slots for this item */
	public List<String> getPossibleSlots() {
		return possibleSlots;
	}

	@Override
	public String toString() {
		return "Item, " + super.toString();
	}

	/**
	 * Is called when the item is created, moved to the ground, or moved on
	 * the ground.
	 *
	 * @param player The player who moved the item, or null if it wasn't moved
	 *               by a player.
	 */
	public void onPutOnGround(Player player) {
		// persistent items don't degrade
		if (!isPersistent()) {
			TurnNotifier.get().notifyInSeconds(DEGRADATION_TIMEOUT, this);
		}
	}

	public void onRemoveFromGround() {
		// persistent items don't degrade
		if (!isPersistent()) {
			// stop the timer so that the item won't degrade anymore
			TurnNotifier.get().dontNotify(this);
		}
		if (plantGrower != null) {
			plantGrower.onFruitPicked(this);
		}
	}

	public void onTurnReached(int currentTurn, String message) {
		// remove this object from the zone where it's lying on
		// the ground
		if (getZone()!=null){
		getZone().remove(getID());
		}
	}

	@Override
	public String describe() {
		String text = "You see " + Grammar.a_noun(getName().replace("_", " ")) + ".";
		String stats = "";
		if (hasDescription()) {
			text = getDescription();
		}
		if (has("bound")) {
			text = text + " It is a special quest reward for " + get("bound") + " and cannot be used by others.";
		}

		if (has("atk")) {
			stats += " ATK: " + get("atk");
		}
		if (has("def")) {
			stats += " DEF: " + get("def");
		}
		if (has("rate")) {
			stats += " RATE: " + get("rate");
		}
		if (has("amount")) {
			stats += " HP: " + get("amount");
		}
		if (stats.length() > 0) {
			stats = " Stats are (" + stats.trim() + ").";
		}
		return (text + stats);
	}

	/**
	 * Removes the item. I case of StackableItems only one is removed.
	 */
	public void removeOne() {
		removeFromWorld();
	}

	public boolean canBeEquippedIn(String slot) {
		if (slot == null) {
			return true; // ground
		}
		return possibleSlots.contains(slot)
		// when the slot is called "content", it's a personal chest.
		        || slot.equals("content");
	}

	public void removeFromWorld() {
		if (isContained()) {
			// We modify the base container if the object change.
			RPObject base = getContainer();

			while (base.isContained()) {
				base = base.getContainer();
			}

			RPSlot slot = getContainerSlot();
			slot.remove(getID());

			StendhalRPWorld.get().modify(base);
		} else {
			StendhalRPWorld.get().remove(getID());
		}
	}
}
