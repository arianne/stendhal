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

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.events.UseListener;

import java.util.Map;

/**
 * This is a common ring. Other rings can extend this one and implement onUsed.
 * 
 * @author miguel
 */
public class Ring extends Item implements UseListener {
	/**
	 * Creates a new ring.
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public Ring(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * copy constructor
	 * 
	 * @param item
	 *            item to copy
	 */
	public Ring(Ring item) {
		super(item);
	}

	public boolean onUsed(RPEntity user) {
		return false;
	}

	/**
	 * Get the entity description.
	 * 
	 * @return The description text.
	 */
	@Override
	public String describe() {
		if (has("amount")) {
			if (getInt("amount") == 0) {
				return "You see the ring of life. The gleam is lost from the stone and it has no powers.";
			} else {
				return "You see the ring of life. Wear it, and you risk less from death.";
			}
		}
		return super.describe();
	}
}
