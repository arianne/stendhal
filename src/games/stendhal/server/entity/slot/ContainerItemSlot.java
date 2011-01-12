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
 * slot used in container items such as a wallet
 *
 * @author hendrik
 */
public class ContainerItemSlot extends EntitySlot {

	/**
	 * creates a new ContainerItemSlot
	 *
	 * @param name
	 * @param contentSlotName
	 */
	public ContainerItemSlot(String name, String contentSlotName) {
		super(name, contentSlotName);
	}

	@Override
	public boolean isReachableForTakingThingsOutOfBy(final Entity entity) {
		// we assume that the content of container items on the ground may not be accessed
		setErrorMessage("Hey, no pickpocketing.");
		return super.hasAsAncestor(entity);
	}

}
