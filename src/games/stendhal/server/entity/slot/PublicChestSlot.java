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

import games.stendhal.server.entity.mapstuff.chest.Chest;

/**
 * A slot of a chest which is only accessible, if the chest is open.
 *
 * @author hendrik
 */
public class PublicChestSlot extends ChestSlot {

	/**
	 * creates a new PublicChestSlot
	 *
	 * @param owner the chest owning this slot
	 */
	public PublicChestSlot(Chest owner) {
		super(owner);
	}

	@Override
	public boolean isTargetBoundCheckRequired() {
		return true;
	}
}
