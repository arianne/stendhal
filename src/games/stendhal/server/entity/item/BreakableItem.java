/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
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

import java.util.Map;

/**
 * An item that wears & breaks.
 */
public class BreakableItem extends Item {

	public BreakableItem(String name, String clazz, String subclass, Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	public BreakableItem(final BreakableItem item) {
		super(item);
	}

	/**
	 * Increment number of times used.
	 */
	@Override
	public void deteriorate() {
		put("uses", getUses() + 1);
	}

	/**
	 * Checks if the item has no uses remaining.
	 *
	 * @return
	 * 		<code>true</code> if uses are as much or more than base_uses.
	 */
	public boolean isBroken() {
		return getUses() >= getBaseUses();
	}

	public int getBaseUses() {
		return getInt("base_uses");
	}

	public int getUses() {
		return getInt("uses");
	}
}
