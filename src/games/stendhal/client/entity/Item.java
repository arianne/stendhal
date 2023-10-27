/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2023 - Marauroa                    *
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
import marauroa.common.game.RPSlot;

public class Item extends Entity {
	/**
	 * The content slot, or <code>null</code> if the item has none or it's not
	 * accessible.
	 */
	private RPSlot content;

	/** Quantity property. */
	public static final Property PROP_QUANTITY = new Property();
	/** The item quantity. */
	private int quantity;


	/**
	 * Create an item.
	 */
	public Item() {
		quantity = 0;
	}

	/**
	 * Initialize this entity for an object.
	 *
	 * @param object
	 *            The object.
	 *
	 * @see #release()
	 */
	@Override
	public void initialize(final RPObject object) {
		super.initialize(object);

		if (object.hasSlot("content")) {
			content = object.getSlot("content");
		} else {
			content = null;
		}
	}

	/**
	 * Get the content slot.
	 *
	 * @return Content slot or <code>null</code> if the item has none or it's
	 * not accessible.
	 */
	public RPSlot getContent() {
		return content;
	}

	/**
	 * Get the item quantity.
	 *
	 * @return The number of items.
	 */
	public int getQuantity() {
		return quantity;
	}

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

		if (changes.has("state")) {
			fireChange(PROP_STATE);
		}
		if (changes.has("quantity")) {
			quantity = changes.getInt("quantity");
			fireChange(PROP_QUANTITY);
		}
	}

	public int getState() {
		if (rpObject.has("state")) {
			return rpObject.getInt("state");
		}
		return 0;
	}
}
