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
package games.stendhal.server.entity.item;

import java.util.Map;

/**
 * A GM only item to help in checking houses.
 * Opens any door that can be used with <code>HouseKey</code>.
 */
public class MasterKey extends HouseKey {

	public MasterKey(final MasterKey key) {
		super(key);
	}

	public MasterKey(final String name, final String clazz, final String subclass, final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);

		setInfoString("any player's house;0;");
	}

	// Open any door that can be opened with HouseKeys
	@Override
	public boolean matches(final String houseId, final int number) {
		return true;
	}

	@Override
	public void setup(final String id, final int lockNumber, String owner) {
		// Ignore any setup requests, do not delegate to super class
	}
}
