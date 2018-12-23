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
	 * Decreases number of remaining uses by 1.
	 */
	@Override
	public void deteriorate() {
		int usesRemaining = getInt("uses");

		if (usesRemaining > 0) {
			usesRemaining -= 1;
			put("uses", usesRemaining);

			return;
		}
	}

	/**
	 * Checks if the item has no uses remaining.
	 *
	 * @return
	 * 		<code>true</code> if uses remaining are 0 or less.
	 */
	public boolean isBroken() {
		return getInt("uses") <= 0;
	}
}
