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
import marauroa.common.game.RPSlot;

/**
 * A slot which can contain items.
 * 
 * @author hendrik
 */
public interface Slot {

	/**
	 * Is this slot reachable to take things out of?
	 * 
	 * @param entity
	 *            Entity which may be able to reach this slot
	 * @return true, if it is reachable, false otherwise
	 */
	boolean isReachableForTakingThingsOutOfBy(Entity entity);

	/**
	 * Is this slot reachable to put things into?
	 * 
	 * @param entity
	 *            Entity which may be able to reach this slot
	 * @return true, if it is reachable, false otherwise
	 */
	boolean isReachableForThrowingThingsIntoBy(Entity entity);

	/**
	 * Can this slot contain items?
	 * 
	 * @return true, if it can contains items, false otherwise
	 */
	boolean isItemSlot();

	/**
	 * returns the slot which must be used for write access.
	 *
	 * @return RPSlot which should be used
	 */
	RPSlot getWriteableSlot();

	/**
	 * does this slot require that the bounding of items is check on adding items
	 *
	 * @return true, if bound items have to be checked, false otherwise
	 */
	boolean isTargetBoundCheckRequired();
}
