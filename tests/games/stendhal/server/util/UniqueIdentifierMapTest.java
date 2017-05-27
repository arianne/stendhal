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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

/**
 * Tests the CountingMap class.
 *
 * @author Martin Fuchs
 */
public class UniqueIdentifierMapTest {

	@Test
	public void test() {
		final UniqueIdentifierMap<String> a = new UniqueIdentifierMap<String>("prefix");

		final String key1 = a.add("ABC 123");
		assertEquals("prefix0", key1);

		final String key2 = a.add("xyz");
		assertEquals("prefix1", key2);

		assertTrue(!key1.equals(key2));

		// count map entries
		int size = 0;
		for (final Map.Entry<String, String> it : a) {
			it.toString();
			++size;
		}
		assertEquals(2, size);
	}

}
