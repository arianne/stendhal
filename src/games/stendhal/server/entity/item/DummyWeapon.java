/***************************************************************************
 *                (C) Copyright 2003-2015 - Faiumoni e.V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item;

import java.util.Map;

/**
 * Weapons that can be given to creatures to adjust their rate of attack,
 * without giving them a visible weapon. These should never be seen as actual
 * items in the game.
 */
public class DummyWeapon extends Item {
	public DummyWeapon(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 *
	 * @param item copied item
	 */
	public DummyWeapon(Item item) {
		super(item);
	}

	@Override
	public String getWeaponType() {
		// Null value prevents the client from trying to look up sprites
		// needlessly
		return null;
	}
}
