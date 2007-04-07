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

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

/**
 * This is a stackable item.
 */
public class StackableItem extends Item {
	private int quantity;

	public StackableItem()  {
		quantity = 0;
	}


	//
	// StackableItem
	//

	/**
	 * Get the item quantity.
	 *
	 * @return	The number of items.
	 */
	public int getQuantity() {
		return quantity;
	}


	@Override
	public void onChangedAdded(final RPObject base, final RPObject diff) throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);

		if (diff.has("quantity")) {
			quantity = diff.getInt("quantity");
			updateView();
		}
	}


	//
	// Entity
	//

	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	protected Entity2DView createView() {
		return new StackableItem2DView(this);
	}
}
