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
package games.stendhal.server.core.events;

/**
 * Equipable Entities implement this interface EquipListener.
 *
 * @author hendrik
 */
public interface EquipListener {

	/**
	 * Checks whether this object can be equipped in the given slot.
	 *
	 * @param slot
	 *            name of slot
	 * @return true, if it can be equipped; false otherwise
	 */
	boolean canBeEquippedIn(String slot);

}
