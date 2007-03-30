
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
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.events.EquipListener;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;

/**
 * The Spell class. Based off the item code.
 * @author timothyb89
 */
public class Spell extends PassiveEntity implements EquipListener {

	/** list of possible slots for this item */
	private List<String> possibleSlots = Arrays.asList("spells");

	/**
	 * The plant grower where this item was grown, until it has been picked.
	 * null if it wasn't grown by a plant grower, or if it has already been
	 * picked.
	 */
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

	/** no public 'default' item */
	private Spell() throws AttributeNotFoundException {
		super();
		put("type", "spell");
		update();
	}

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
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


	public boolean canBeEquippedIn(String slot) {
		return possibleSlots.contains(slot);
	}
}
