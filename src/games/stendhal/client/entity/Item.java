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
import marauroa.common.game.RPSlot;

public class Item extends Entity {
	/**
	 * The content slot, or <code>null</code> if the item has none or it's not
	 * accessible.
	 */
	private RPSlot content;

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
}
