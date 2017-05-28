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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * A map which assigns a unique id to every entry.
 *
 * @author hendrik
 * @param <V>
 *            value type
 */
public class UniqueIdentifierMap<V> implements Serializable,
		Iterable<Map.Entry<String, V>> {

	private static final long serialVersionUID = -4142274943695729582L;
	private final Map<V, String> map = new HashMap<V, String>();
	private final Map<String, V> mapKeys = new TreeMap<String, V>();
	private final String prefix;
	private int counter = 0;

	/**
	 * Creates a new counting map.
	 *
	 * @param prefix
	 *            prefix
	 */
	public UniqueIdentifierMap(final String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Adds a new entry to the map unless it is already part of it.
	 *
	 * @param value
	 *            entry
	 * @return key
	 */
	public String add(final V value) {
		String key = map.get(value);
		if (key == null) {
			key = prefix + counter;
			map.put(value, key);
			mapKeys.put(key, value);
			counter++;
		}
		return key;
	}

	@Override
	public Iterator<Map.Entry<String, V>> iterator() {
		return mapKeys.entrySet().iterator();
	}
}
