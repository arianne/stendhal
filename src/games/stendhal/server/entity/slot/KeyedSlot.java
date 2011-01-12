/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.slot;

import games.stendhal.server.entity.Entity;

/**
 *
 * Abused slots of players which contain one RPObject used as hashmap.
 *
 * @author hendrik
 */
public class KeyedSlot extends EntitySlot {

	/**
	 * Creates a new keyed slot.
	 *
	 * @param name
	 *            name of slot
	 */
	public KeyedSlot(final String name) {
		super(name, name);
	}

	@Override
	public boolean isItemSlot() {
		return false;
	}

	@Override
	public boolean isReachableForTakingThingsOutOfBy(final Entity entity) {
		setErrorMessage("This " + getName() + " is not for items.");
		return false;
	}

}
