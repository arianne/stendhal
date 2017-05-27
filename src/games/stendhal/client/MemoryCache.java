/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Map like cache that uses SoftReferences to store the cached items to allow
 * the garbage collector to reclaim the memory in need. Unlike WeakHashMap, the
 * entries are retained by value rather than by key.
 *
 * @param <K> key type
 * @param <V> value type
 */
public final class MemoryCache<K, V> {
	/** The actual map to store things */
	private final Map<K, Reference<V>> map = new ConcurrentHashMap<K, Reference<V>>();

	/** Queue for the collected references. */
	private final ReferenceQueue<V> queue = new ReferenceQueue<V>();

	/**
	 * Get an object from the cache.
	 *
	 * @param key the key corresponding to the object
	 * @return cached object, or <code>null</code> if there's no object
	 * 	for the key in the cache
	 */
	public V get(K key) {
		pruneMap();
		if (key != null) {
			Reference<V> ref = map.get(key);
			if (ref != null) {
				return ref.get();
			}
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
		pruneMap();
		// Disallow storing null keys and values
		if ((key == null) || (value == null)) {
			return;
		}
		Reference<V> ref = new Entry<K, V>(key, value, queue);
		map.put(key, ref);
	}

	/**
	 * Discard the collected entries.
	 */
	@SuppressWarnings("unchecked")
	private void pruneMap() {
		Reference<? extends V> ref = queue.poll();

		while (ref != null) {
			/*
			 * The cast is guaranteed to be correct as we allow only Entries to
			 * the queue. This would not be needed if ReferenceQueue was typed
			 * according to the reference instead of according to the type of
			 * the referred object.
			 */
			map.remove(((Entry<K, V>) ref).key);
			ref = queue.poll();
		}
	}

	/**
	 * A container for values that remembers the used key to help cleaning
	 * unused keys from the map.
	 *
	 * @param <K> key type
	 * @param <V> value type
	 */
	static class Entry<K, V> extends SoftReference<V> {
		K key;

		/**
		 * Create a new Entry.
		 *
		 * @param key key used for storing the value
		 * @param value stored value
		 * @param queue queue for the garbage collector to place the entry when
		 *	it has been claimed
		 */
		Entry(K key, V value, ReferenceQueue<? super V> queue) {
			super(value, queue);
			this.key = key;
		}
	}
}
