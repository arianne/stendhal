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
package games.stendhal.client.entity;

import marauroa.common.game.RPSlot;

/**
 * Listener for content changes in an Entity's slots.
 */
public interface ContentChangeListener {
	/**
	 * Called when entities are added in slots, or the attributes of the
	 * entities are changed.
	 *
	 * @param added object changes
	 */
	void contentAdded(RPSlot added);
	/**
	 * Called when entities are removed from slots, of attributes of the
	 * entities are removed.
	 *
	 * @param removed object changes
	 */
	void contentRemoved(RPSlot removed);
}
