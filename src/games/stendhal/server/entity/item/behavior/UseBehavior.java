/***************************************************************************
 *                 (C) Copyright 2003-2013 - Stendhal team                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item.behavior;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;

/**
 * Interface for behavior implementations that can be attached to items.
 */
public interface UseBehavior {
	/**
	 * Called when the item is used.
	 *
	 * @param user entity using the item
	 * @param item used item
	 *
	 * @return <code>true</code> on successful use, <code>false</code> on
	 * 	failure
	 */
	boolean use(RPEntity user, Item item);
}
