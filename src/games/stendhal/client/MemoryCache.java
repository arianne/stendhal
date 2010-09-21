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
package games.stendhal.client;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * A Map like cache that uses SoftReferences to store the cached items to allow
 * the garbage collector to reclaim the memory in need.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class MemoryCache<K, V> {
	private static final Logger logger = Logger.getLogger(MemoryCache.class);
	/** The actual map to store things */
	private HashMap<K, Reference<V>> map = new HashMap<K, Reference<V>>();

	/**
	 * Get an object from the cache.
	 * 
	 * @param key the key corresponding to the object
	 * @return cached object, or <code>null</code> if there's no object
	 * 	for the key in the cache
	 */
	public V get(K key) {
		Reference<V> ref = map.get(key);
		if (ref != null) {
			V obj = ref.get();
			/*
			 * Keys won't be reclaimed normally, but for any sane usage the
			 * memory use of the keys is insignificant compared to that of the
			 * value objects.
			 */
			if (obj == null) {
				logger.debug("cache cleared: " + key);
				map.remove(key);
			}
			return obj;
		}
		return null;
	}

	/**
	 * Store an object to the cache.
	 * 
	 * @param key key for accessing the object
	 * @param value the object to be stored
	 */
	public void put(K key, V value) {
		// Disallow storing null keys
		if (key == null) {
			return;
		}
		Reference<V> ref = new SoftReference<V>(value);
		map.put(key, ref);
	}
}
