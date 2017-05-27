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
package games.stendhal.server.core.reflectiondebugger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
/**
 * Tests for KeepFirstTreeMap.
 *
 * @author hendrik
 */
public class KeepFirstTreeMapTest {

	@Test
	public void testNormalOperation() {
		Map<String, String> map = new KeepFirstTreeMap<String, String>();
		map.put("a", "b");

		assertThat(map.get("a"), is("b"));
		assertThat(map.get("b"), nullValue());
	}

	/**
	 * Tests for keep.
	 */
	@Test
	public void testKeep() {
		Map<String, String> map = new KeepFirstTreeMap<String, String>();
		map.put("a", "b");
		map.put("a", "c");

		assertThat(map.get("a"), is("b"));
		assertThat(map.get("b"), nullValue());
	}

	/**
	 * Tests for remove.
	 */
	@Test
	public void testRemove() {
		Map<String, String> map = new KeepFirstTreeMap<String, String>();
		map.put("a", "b");
		map.remove("a");
		map.put("a", "c");

		assertThat(map.get("a"), is("c"));
		assertThat(map.get("b"), nullValue());
	}

	/**
	 * Tests for putAll.
	 */
	@Test
	public void testPutAll() {
		Map<String, String> map = new KeepFirstTreeMap<String, String>();
		map.put("a", "b");

		Map<String, String> otherMap = new HashMap<String, String>();
		otherMap.put("a", "c");

		map.putAll(otherMap);

		assertThat(map.get("a"), is("b"));
		assertThat(map.get("b"), nullValue());
	}
}
