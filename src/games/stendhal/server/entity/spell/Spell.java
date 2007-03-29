
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
package games.stendhal.server.entity.spell;

import games.stendhal.common.Grammar;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spawner.PassiveEntityRespawnPoint;
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
 * The Spell class. Based off the item code.
 * @author timothyb89
 */
public class Spell extends PassiveEntity implements TurnListener, EquipListener {

	/** list of possible slots for this item */
	private List<String> possibleSlots;

	/**
	 * The plant grower where this item was grown, until it has been picked.
	 * null if it wasn't grown by a plant grower, or if it has already been
	 * picked.
	 */
	private PassiveEntityRespawnPoint plantGrower;

	final public static int DEGRADATION_TIMEOUT = 54 * 60; // 54 minutes

	public static void generateRPClass() {
		RPClass entity = new RPClass("spell");
		entity.isA("entity");
                entity.add("class", RPClass.STRING); // the spell class (other purposes, just to code old code for now)
		entity.add("name", RPClass.STRING); // name of spell (such as "heal")
                
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
	public Spell(String name, Map<String, String> attributes) {
		this();

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


	/** no public 'default' item */
	private Spell() throws AttributeNotFoundException {
		super();
		put("type", "spell");
		update();
	}

	/** copy constuctor */
	private Spell(Spell other) throws AttributeNotFoundException {
		super(other);
	}

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
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

	/** returns the type of the spell ||| not valid as there is no need for a subclass yet. */
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
	 * Get spell count.
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

	/** creates a copy */
	public Object copy() {
		return new Spell(this);
	}

	@Override
	public String toString() {
		return "Spell, " + super.toString();
	}

	@Override
	public String describe() {

		String text = "You see " + Grammar.a_noun(getName().replace("_", " ")) + ".";
		return (text);
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

	public boolean canBeEquippedIn(String slot) {
		System.out.println("'" + slot + "'");
		return possibleSlots.contains(slot)
		// when the slot is called "content", it's a personal chest.
		        || slot.equals("content");
	}

    public void onTurnReached(int currentTurn, String message) {
        // remove this object from the zone where it's lying on
	// the ground
	StendhalRPWorld.get().getRPZone(getID()).remove(getID());
    }

}
