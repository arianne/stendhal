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
import games.stendhal.server.core.events.EquipListener;
import games.stendhal.server.entity.PassiveEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.Type;

/**
 * The Spell class. Based off the item code.
 * 
 * @author timothyb89
 */
public class Spell extends PassiveEntity implements EquipListener {
	/**
	 * The spell name attribute name.
	 */
	protected static final String ATTR_NAME = "name";

	/** list of possible slots for this item. */
	private final List<String> possibleSlots = Arrays.asList("spells");

	/**
	 * The plant grower where this item was grown, until it has been picked.
	 * null if it wasn't grown by a plant grower, or if it has already been
	 * picked.
	 */
	public static void generateRPClass() {
		final RPClass entity = new RPClass("spell");
		entity.isA("entity");

		// the spell class (other purposes, just to code old code for now)
		entity.addAttribute("class", Type.STRING);
 
		// name of spell (such as "heal")
		entity.addAttribute(ATTR_NAME, Type.STRING);
	}

	/**
	 * 
	 * Creates a new Item.
	 * 
	 * @param name
	 *            name of item
	 * @param attributes
	 *            attributes (like attack). may be empty or <code>null</code>
	 */
	public Spell(final String name, final Map<String, String> attributes) {
		this();

		put(ATTR_NAME, name);

		if (attributes != null) {
			// store all attributes
			for (final String key : attributes.keySet()) {
				put(key, attributes.get(key));
			}
		}
	}

	/** no public 'default' item. */
	private Spell() {
		setRPClass("spell");
		put("type", "spell");
		update();
	}

	@Override
	public String toString() {
		return "Spell, " + super.toString();
	}

	@Override
	public String describe() {
		final String name = getName();

		if (name != null) {
			return "You see " + Grammar.a_noun(name) + ".";
		} else {
			return super.describe();
		}
	}

	public boolean canBeEquippedIn(final String slot) {
		return possibleSlots.contains(slot);
	}

	/**
	 * Get the entity name.
	 * 
	 * @return The entity's name, or <code>null</code> if undefined.
	 */
	public String getName() {
		if (has(ATTR_NAME)) {
			return get(ATTR_NAME);
		} else {
			return null;
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
}
