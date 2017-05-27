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
package games.stendhal.server.core.rule.defaultruleset;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class LowerCaseMap<V> implements Map<String, V> {
	private Map<String, V> data;

	public LowerCaseMap() {
		data = new HashMap<String, V>();
	}

	@Override
	public void clear() {
		data.clear();

	}

	@Override
	public boolean containsValue(final Object value) {
		return data.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<String, V>> entrySet() {
		return data.entrySet();
	}

	@Override
	public V get(final Object key) {
		if (key instanceof String) {
			final String new_name = (String) key;
			return data.get(new_name.toLowerCase());
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return data.keySet();
	}

	@Override
	public V put(final String key, final V value) {
		return data.put(key.toLowerCase(), value);
	}

	@Override
	public void putAll(final Map< ? extends String, ? extends V> m) {
		   for (final Iterator< ? extends Map.Entry< ? extends String, ? extends V>> i = m.entrySet().iterator(); i.hasNext();) {
			                 final Map.Entry< ? extends String, ? extends V> e = i.next();
			                 put(e.getKey(), e.getValue());
			              }
	}

	@Override
	public V remove(final Object key) {
		if (key instanceof String) {
			final String new_name = (String) key;
			return data.remove(new_name.toLowerCase());
		}
		return null;
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public Collection<V> values() {
		return data.values();
	}

	@Override
	public boolean containsKey(final Object key) {
		if (key instanceof String) {
			final String new_name = (String) key;
			return data.containsKey(new_name.toLowerCase());
		}
		return false;
	}
}
