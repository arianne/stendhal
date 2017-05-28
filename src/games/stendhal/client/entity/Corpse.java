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

//
//

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * A corpse entity.
 */
public class Corpse extends Entity {
	/**
	 * The current content slot.
	 */
	private RPSlot content;

	//
	// Corpse
	//

	/**
	 * Get the corpse contents.
	 *
	 * @return The contents slot.
	 */
	public RPSlot getContent() {
		return content;
	}

	/**
	 * gets the owner of the corpse who may loot.
	 *
	 * @return owner or <code>null</code>
	 */
	public String getCorpseOwner() {
		if (rpObject.has("corpse_owner")) {
			return rpObject.get("corpse_owner");
		}
		return null;
	}

	//
	// Entity
	//

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
	 * Checks if the corpse is empty or not
	 *
	 *  @return true if the corpse is empty
	 */
	public boolean isEmpty() {
		// size() is a method from RPSlot which counts the number of objects in the slot
		return content.size() == 0;
	}

}
