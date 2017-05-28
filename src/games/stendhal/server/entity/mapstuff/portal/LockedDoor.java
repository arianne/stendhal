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
package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.server.entity.RPEntity;

/**
 * A locked door is a special kind of portal which requires a key to pass it. If
 * the player carries the key with him, he can use the door just like a normal
 * portal; it will automatically open and close.
 *
 * By using the requiredAmount parameter, it can need more than one item for a
 * key
 *
 * Note that you can link a door with a portal; that way, people only require
 * the key when walking in one direction and can walk in the other direction
 * without any key.
 */
public class LockedDoor extends Door {

	/* the number of items user must be carrying to act as a key */
	protected int requiredAmount;

	/**
	 * Creates a new Locked door which need 1 key item.
	 *
	 * @param key
	 *            The name of the item that is required to use the door
	 * @param clazz
	 *            The class. Responsible for how this door looks like.
	 */
	public LockedDoor(final String key, final String clazz) {
		this(key, clazz, 1);
	}

	/**
	 * Creates a new Locked door.
	 *
	 * @param key
	 *            The name of the item that is required to use the door
	 * @param clazz
	 *            The class. Responsible for how this door looks like.
	 * @param requiredAmount
	 *            The number of key items that are needed
	 */
	public LockedDoor(final String key, final String clazz, final int requiredAmount) {
		super(clazz);
		put("locked", key);
		this.requiredAmount = requiredAmount;
	}

	@Override
	protected boolean isAllowed(final RPEntity user) {
		return (has("locked") && user.isEquipped(get("locked"), requiredAmount));
	}

}
