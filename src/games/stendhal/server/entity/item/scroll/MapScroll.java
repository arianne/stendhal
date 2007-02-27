/* $Id$
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
package games.stendhal.server.entity.item.scroll;

import games.stendhal.server.entity.RPEntity;

import java.util.Map;

/**
 * Represents a map (on a scroll).
 */
public class MapScroll extends Scroll {
	/**
	 * Creates a new map.
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public MapScroll(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}


	// maps are scrolls without any special actions
	@Override
	public void onUsed(RPEntity user) {
	}
}
