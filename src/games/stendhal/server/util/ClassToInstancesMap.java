/***************************************************************************
 *                   (C) Copyright 2013 Faiumoni e. V.                     *
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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A map from classes to a list of instances
 *
 * @author hendrik
 * @param <V>  type of value
 */
public class ClassToInstancesMap<V> implements Map<Class<V>, List<V>> {
	private final Map<Class<V>, List<V>> maps = new HashMap<Class<V>, List<V>>();

	@Override
	public void clear() {
		maps.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return maps.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		List<V> list = maps.get(value.getClass());
		if (list == null) {
			return false;
		}
		return list.contains(value);
	}

	@Override
	public Set<Map.Entry<Class<V>, List<V>>> entrySet() {
		return maps.entrySet();
	}

	@Override
	public List<V> get(Object key) {
		return maps.get(key);
	}

	@Override
	public boolean isEmpty() {
		return maps.isEmpty();
	}

	@Override
	public Set<Class<V>> keySet() {
		return maps.keySet();
	}

	@Override
	public List<V> put(Class<V> key, List<V> value) {
		return maps.put(key, value);
	}

	@Override
	public void putAll(Map<? extends Class<V>, ? extends List<V>> m) {
		maps.putAll(m);
	}

	@Override
	public List<V> remove(Object key) {
		return maps.remove(key);
	}

	@Override
	public int size() {
		return maps.size();
	}

	@Override
	public Collection<List<V>> values() {
		return maps.values();
	}

	@Override
	public String toString() {
		return maps.toString();
	}

	/**
	 * adds an entry
	 *
	 * @param value
	 *            value
	 */
	@SuppressWarnings("unchecked")
	public void addValue(V value) {
		List<V> values = maps.get(value.getClass());
		if (values == null) {
			values = new LinkedList<V>();
			maps.put((Class<V>) value.getClass(), values);
		}
		values.add(value);
	}


	/**
	 * removes an entry
	 *
	 * @param value
	 *            value
	 */
	public void removeValue(V value) {
		List<V> values = maps.get(value.getClass());
		if (values != null) {
			values.remove(value);
		}
	}
}
