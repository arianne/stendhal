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

import marauroa.common.game.RPObject;

/**
 * This is a stackable item.
 */
public class StackableItem extends Item {
	/**
	 * Quantity property.
	 */
	public static final Property PROP_QUANTITY = new Property();

	/**
	 * The item quantity.
	 */
	private int quantity;

	/**
	 * Create a stackable item.
	 */
	public StackableItem() {
		quantity = 0;
	}

	//
	// StackableItem
	//

	/**
	 * Get the item quantity.
	 *
	 * @return The number of items.
	 */
	public int getQuantity() {
		return quantity;
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

		if (changes.has("quantity")) {
			quantity = changes.getInt("quantity");
			fireChange(PROP_QUANTITY);
		}
	}
}
