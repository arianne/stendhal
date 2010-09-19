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

import java.util.Map;

import org.junit.Test;

/**
 * Tests for ObjectCounter
 */
public class ObjectCounterTest {


	/**
	 * Tests for add.
	 */
	@Test
	public final void testAdd() {
		final ObjectCounter<String> ocString = new ObjectCounter<String>();
		final String bla = "bla";
		final String blub = "blub";
		ocString.add(bla);
		Map<String, Integer> resmap = ocString.getMap();
		assertEquals(Integer.valueOf(1), resmap.get(bla));
		ocString.add(bla);
		resmap = ocString.getMap();
		assertEquals(Integer.valueOf(2), resmap.get(bla));
		ocString.add(bla);
		resmap = ocString.getMap();
		assertEquals(Integer.valueOf(3), resmap.get(bla));
		assertEquals(null, resmap.get(blub));
		ocString.add(blub);
		resmap = ocString.getMap();
		assertEquals(Integer.valueOf(3), resmap.get(bla));
		assertEquals(Integer.valueOf(1), resmap.get(blub));
		ocString.clear();
		resmap = ocString.getMap();
		assertEquals(null, resmap.get(bla));
		assertEquals(null, resmap.get(blub));

	}

}
