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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class MemoryCacheTest {
	/**
	 * Test trying to store an object with a null key to the cache.
	 */
	@Test
	public void testStoreNull() {
		MemoryCache<Object, String> cache = new MemoryCache<Object, String>();
		cache.put(null, "boo");
		String val = cache.get(null);
		assertNull(val);
	}

	/**
	 * Test that items stored to the cache can be retrieved properly.
	 */
	@Test
	public void testStoreNormal() {
		MemoryCache<String, String> cache = new MemoryCache<String, String>();
		// Keep hard references to ensure that the GC won't delete the values
		// during the test
		String val1 = "foo";
		String val2 = "bar";
		String val3 = "baz";
		cache.put("a", val1);
		cache.put("b", val2);
		cache.put("c", val3);
		assertEquals(val1, cache.get("a"));
		assertEquals(val2, cache.get("b"));
		assertEquals(val3, cache.get("c"));
	}

	/**
	 * Test that assigning new values to cache items works properly.
	 */
	@Test
	public void testOverwrite() {
		MemoryCache<String, String> cache = new MemoryCache<String, String>();
		// Keep hard references to ensure that the GC won't delete the values
		// during the test
		String val1 = "foo";
		String val2 = "bar";
		String val3 = "baz";
		cache.put("a", val1);
		cache.put("b", val2);
		cache.put("c", val3);
		cache.put("b", val1);
		assertEquals(val1, cache.get("b"));
		cache.put("b", cache.get("c"));
		assertEquals(val1, cache.get("a"));
		assertEquals(val3, cache.get("b"));
		assertEquals(val3, cache.get("c"));
	}

	/**
	 * A small test for MemoryCache.Entry
	 */
	@Test
	public void testEntry() {
		String val1 = "foo";
		MemoryCache.Entry<String, String> entry = new MemoryCache.Entry<String, String>("a", val1, null);
		assertEquals("Key", "a", entry.key);
		assertEquals("Value", val1, entry.get());
	}
}
