/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item.consumption;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import games.stendhal.server.entity.item.Drink;
import utilities.RPClass.ItemTestHelper;

public class FeederFactoryTest {

	/**
	 * Tests for getEaterForSoup.
	 */
	@Test
	public final void testGetEaterForSoup() {
		ItemTestHelper.generateRPClasses();
		final String name = "soup";
		final String clazz = "drink";
		final String subclass = "soup";
		final Map<String, String> map = new HashMap<String, String>();
		map.put("description",
				"You see a bowl of soup. Its contents fill you up.");
		map.put("amount", "2500");
		map.put("frequency", "10");
		map.put("quantity", "1");
		map.put("regen", "25");

		final Drink soup = new Drink(name, clazz, subclass, map);
		assertEquals(Eater.class, FeederFactory.get(soup).getClass());
	}
}
