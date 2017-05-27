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
package games.stendhal.server.entity.item;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.RPClass.ItemTestHelper;

public class ConsumableItemTest {
	private static ConsumableItem c100_1;

	private static ConsumableItem c4_5;

	private static ConsumableItem c4_6;

	private static ConsumableItem d100_1;

	private static ConsumableItem c50_1;

	private static ConsumableItem c100_2;

	private static ConsumableItem c200_1;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ItemTestHelper.generateRPClasses();
		final Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("amount", "1");
		attributes.put("regen", "200");
		attributes.put("frequency", "1");
		c200_1 = new ConsumableItem("", "", "", attributes);

		attributes.put("regen", "100");
		attributes.put("frequency", "1");
		c100_1 = new ConsumableItem("", "", "", attributes);
		assertEquals(100, c100_1.getRegen());
		assertEquals(1, c100_1.getFrecuency());
		attributes.put("regen", "4");
		attributes.put("frequency", "5");
		c4_5 = new ConsumableItem("", "", "", attributes);
		attributes.put("regen", "4");
		attributes.put("frequency", "6");
		c4_6 = new ConsumableItem("", "", "", attributes);
		attributes.put("regen", "100");
		attributes.put("frequency", "1");
		d100_1 = new ConsumableItem("", "", "", attributes);

		attributes.put("regen", "50");
		attributes.put("frequency", "1");
		c50_1 = new ConsumableItem("", "", "", attributes);
		assertEquals(50, c50_1.getRegen());
		assertEquals(1, c50_1.getFrecuency());

		attributes.put("regen", "100");
		attributes.put("frequency", "2");
		c100_2 = new ConsumableItem("", "", "", attributes);
	}

	/**
	 * Tests for listsort.
	 */
	@Test
	public void testlistsort() {
		final LinkedList<ConsumableItem> items = new LinkedList<ConsumableItem>();
		items.add(c4_6);
		items.add(c50_1);
		items.add(c200_1);
		items.add(c100_1);
		items.add(c4_5);

		Collections.sort(items);
		assertEquals(0, items.indexOf(c200_1));
		assertEquals(1, items.indexOf(c100_1));
		assertEquals(2, items.indexOf(c50_1));
		assertEquals(3, items.indexOf(c4_5));
		assertEquals(4, items.indexOf(c4_6));

	}

	/**
	 * Tests for listsort2.
	 */
	@Test
	public void testlistsort2() {
		final LinkedList<ConsumableItem> items = new LinkedList<ConsumableItem>();
		items.add(c50_1);
		items.add(c200_1);
		items.add(c100_1);
		items.add(c100_2);
		Collections.sort(items);
		assertEquals(0, items.indexOf(c200_1));
		assertEquals(1, items.indexOf(c100_1));
		assertEquals(2, items.indexOf(c50_1));
		assertEquals(3, items.indexOf(c100_2));
	}

	@Test
	public void compareSGNxy_minSGNyx() {
		// sgn(x.compareTo(y)) == -sgn(y.compareTo(x))
		assertTrue(c100_1.compareTo(c50_1) < 0);
		assertTrue(c50_1.compareTo(c100_1) > 0);
		assertTrue(Math.signum(c100_1.compareTo(c50_1)) == -Math.signum(c50_1
				.compareTo(c100_1)));
		assertTrue(c100_2.compareTo(c100_1) > 0);
		assertTrue(c100_1.compareTo(c100_2) < 0);
		assertTrue(Math.signum(c100_1.compareTo(c100_2)) == -Math.signum(c100_2
				.compareTo(c100_1)));
		assertTrue(c4_5.compareTo(c4_6) < 0);
		assertTrue(c4_6.compareTo(c4_5) > 0);
		assertTrue(Math.signum(c4_5.compareTo(c4_6)) == -Math.signum(c4_6
				.compareTo(c4_5)));
	}

	@Test
	public void comparetransient() {
		// (x.compareTo(y)>0 && y.compareTo(z)>0) implies x.compareTo(z)>0.
		assertTrue(c50_1.compareTo(c100_1) > 0);
		assertTrue(c100_1.compareTo(c200_1) > 0);
		assertTrue(c50_1.compareTo(c200_1) > 0);

		assertTrue(c200_1.compareTo(c100_1) < 0);
		assertTrue(c100_1.compareTo(c50_1) < 0);
		assertTrue(c200_1.compareTo(c50_1) < 0);

	}

	@Test
	public void compare_xy_sgnxz_sgnyz() {
		// x.compareTo(y)==0 implies that sgn(x.compareTo(z)) ==
		// sgn(y.compareTo(z)), for all z.
		assertEquals(0, c100_2.compareTo(c50_1));
		assertTrue(Math.signum(c50_1.compareTo(c100_1)) == Math.signum(c100_2
				.compareTo(c100_1)));

	}

	@Test
	public void compareTO_Equals() {
		// (x.compareTo(y)==0) == (x.equals(y)).
		assertEquals(0, c100_1.compareTo(d100_1));
		assertEquals(0, d100_1.compareTo(c100_1));
		assertEquals(0, c100_1.compareTo(c100_1));
	}




}
