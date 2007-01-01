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
import games.stendhal.server.entity.PlantGrower;
import games.stendhal.server.entity.Player;
import games.stendhal.server.events.EquipListener;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.List;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

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
	private PlantGrower plantGrower;

	final public static int DEGRADATION_TIMEOUT = 10800; // 30 minutes at 300
	// ms

	public static void generateRPClass() {
		RPClass entity = new RPClass("item");
		entity.isA("entity");
		entity.add("class",    RPClass.STRING); // class, sword/armor/...
		entity.add("subclass", RPClass.STRING); // subclass, long sword/leather // armor/...
		entity.add("name", RPClass.STRING);     // name of item (ie 'Kings Sword')
		entity.add("atk",  RPClass.SHORT);      // Some items has attack values
		entity.add("def",  RPClass.SHORT);      // Some items has defense values
		entity.add("amount", RPClass.INT);      // Some items(food) has amount of
												//      something (a bottle, a piece of meat).
		entity.add("regen",  RPClass.INT);      // Some items(food) has regeneration speed
		entity.add("frequency", RPClass.INT);   // Some items(food) has regeneration speed
		entity.add("quantity",  RPClass.INT);   // Some items(Stackable) has quantity
		entity.add("infostring", RPClass.STRING); // To store additional info with an item
		entity.add("persistent", RPClass.SHORT);  // Some items have individual values
		entity.add("lifesteal",  RPClass.FLOAT);  // Some items has lifesteal values
		entity.add("bound", RPClass.STRING);      // Some items are quest rewards that other players don't deserve.
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
	public Item(String name, String clazz, String subclass,
			Map<String, String> attributes) {
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
	public void setPlantGrower(PlantGrower plantGrower) {
		this.plantGrower = plantGrower;
	}

	/**
	 * returns the PlantGrower which created this item
	 * or null if no plantgrower was involved.
	 *
	 * @return PlantGrower or null
	 */
	public PlantGrower getPlantGrower() {
		return plantGrower;
	}

	/** no public 'default' item */
	private Item() throws AttributeNotFoundException {
		super();
		put("type", "item");
		update();
	}

	/** copy constuctor */
	private Item(Item other) throws AttributeNotFoundException {
		super(other);
	}

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
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

		throw new IllegalStateException("the item does not have a class: "
				+ this);
	}

	/** returns the type of the item */
	public String getItemSubclass() {
		if (has("subclass")) {
			return get("subclass");
		}

		throw new IllegalStateException("the item does not have a subclass: "
				+ this);
	}

	/** returns the name of the item */
	public String getName() {
		return get("name");
	}

	/** returns the list of possible slots for this item */
	public List<String> getPossibleSlots() {
		return possibleSlots;
	}

	/** creates a copy */
	public Object copy() {
		return new Item(this);
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
			TurnNotifier.get().notifyInTurns(DEGRADATION_TIMEOUT, this, null);
		}
	}
	
	public void onRemoveFromGround() {
		// persistent items don't degrade
		if (!isPersistent()) {
			// stop the timer so that the item won't degrade anymore
			TurnNotifier.get().dontNotify(this, null);
		}
		if (plantGrower != null) {
			plantGrower.onFruitPicked(this);
		}
	}
	
	public void onTurnReached(int currentTurn, String message) {
		// remove this object from the zone where it's lying on
		// the ground
		StendhalRPWorld.get().getRPZone(getID()).remove(getID());
	}

	@Override
	public String describe() {
		String atk = "0";
		String def = "0";
		String amount = "0";
		String text = "You see " + Grammar.a_noun(getName().replace("_", " ")) + ".";
		String stats = "";
		if (hasDescription()) {
			text = getDescription();
		}
		if (has("atk")) {
			atk = get("atk");
		}
		if (has("def")) {
			def = get("def");
		}
		if (has("amount")) {
			amount = get("amount");
		}
		if (!atk.equals("0")) {
			stats += " ATK: " + atk;
		}
		if (!def.equals("0")) {
			stats += " DEF: " + def;
		}
		if (!amount.equals("0")) {
			stats += " HP: " + amount;
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

	public boolean canBeEquiped() {
		return true;
	}
}
