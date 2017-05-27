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
import games.stendhal.server.entity.mapstuff.chest.Chest;

/**
 * A slot of a chest which is only accessible, if the chest is open.
 *
 * @author hendrik
 */
public class ChestSlot extends LootableSlot {
	private final Chest chest;

	/**
	 * Creates a ChestSlot
	 *
	 * @param owner
	 *            Chest owning this slot
	 */
	public ChestSlot(final Chest owner) {
		super(owner);
		this.chest = owner;
	}

	@Override
	public boolean isReachableForTakingThingsOutOfBy(final Entity entity) {
		if (!chest.isOpen()) {
			setErrorMessage("This " + ((Entity)getOwner()).getDescriptionName(true) + " is not open.");
			return false;
		}
		return super.isReachableForTakingThingsOutOfBy(entity);
	}
}
