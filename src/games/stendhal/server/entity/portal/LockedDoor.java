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
package games.stendhal.server.entity.portal;

import games.stendhal.common.Direction;
import games.stendhal.server.entity.Player;

/**
 * A locked door is a special kind of portal which requires a key to pass it.
 * If the player carries the key with him, he can use the door just
 * like a normal portal; it will automatically open and close.
 * 
 * Note that you can link a door with a portal; that way, people only
 * require the key when walking in one direction and can walk in the
 * other direction without any key.
 */
public class LockedDoor extends Door {

	/**
	 * Creates a new locked door.
	 *
	 * @param key   name of required key
	 * @param clazz class of door
	 * @param dir   direction of door
	 */
	public LockedDoor(String key, String clazz, Direction dir) {
		super(clazz, dir);
		put("locked", key);
	}

	@Override
	protected boolean mayBeOpend(Player user) {
		return (has("locked") && user.isEquipped(get("locked")));
	}

}
