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
package games.stendhal.server.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Counts objects.
 *
 * @author hendrik
 * @param <K>
 *            Type of objects to count
 */
public class ObjectCounter<K> {
	private final Map<K, Integer> counter = new HashMap<K, Integer>();

	/**
	 * Clears the counter.
	 */
	public void clear() {
		counter.clear();
	}

	/**
	 * Gets the counter map.
	 *
	 * @return Map
	 */
	public Map<K, Integer> getMap() {
		return counter;
	}

	/**
	 * Adds one to the appropriate entry.
	 *
	 * @param o
	 *            object
	 */
	public void add(final K o) {
		Integer in = counter.get(o);
		if (in == null) {
			in = Integer.valueOf(1);
		} else {
			in += Integer.valueOf(1);
		}
		counter.put(o, in);
	}
}
