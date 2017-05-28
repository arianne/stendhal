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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.quests.houses.HouseUtilities;

public class HouseKeyTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		HouseUtilities.clearCache();
	}

	/**
	 * Tests for creation.
	 */
	@Test
	public void testCreation() {
		final Item key = SingletonRepository.getEntityManager().getItem("house key");
		assertNotNull("Generated item is not null", key);
		assertTrue("The key is not a HouseKey", key instanceof HouseKey);
		assertTrue("The key is not persistent", key.isPersistent());
	}

	/**
	 * Tests for copy.
	 */
	@Test
	public void testCopy() {
		final HouseKey key = (HouseKey) SingletonRepository.getEntityManager().getItem("house key");
		final HouseKey key2 = new HouseKey(key);

		assertEquals(key, key2);
		assertFalse(key == key2);
	}

	/**
	 * Tests for description.
	 */
	@Test
	public void testDescription() {
		final HouseKey key = (HouseKey) SingletonRepository.getEntityManager().getItem("house key");
		key.setup("henhouse", 42, null);
		assertEquals(key.describe(), "You see a key to henhouse.");
		key.setup("outhouse", 13, "Mr Taxman");
		assertEquals(key.describe(), "You see a key to Mr Taxman's property, outhouse.");
	}

	/**
	 * Tests for matches.
	 */
	@Test
	public void testMatches() {
		final HouseKey key = (HouseKey) SingletonRepository.getEntityManager().getItem("house key");

		// only match when both the id and lock number match
		key.setup("henhouse", 42, null);
		assertTrue(key.matches("henhouse", 42));
		assertFalse(key.matches("outhouse", 42));
		assertFalse(key.matches("henhouse", 41));
		assertFalse(key.matches("outhouse", 41));

		// owner should not make a difference
		key.setup("henhouse", 42, "test player");
		assertTrue(key.matches("henhouse", 42));
		assertFalse(key.matches("outhouse", 42));
		assertFalse(key.matches("henhouse", 41));
		assertFalse(key.matches("outhouse", 41));
	}

	/**
	 * Tests for image.
	 */
	@Test
	public void testImage() {
		final HouseKey key = (HouseKey) SingletonRepository.getEntityManager().getItem("house key");

		// The subclass is up to the implementation to decide, but
		// it should be same for all keys with the same id and lock number
		key.setup("henhouse", 42, null);
		String image = key.get("subclass");
		assertTrue(image.length() > 0);
		key.setup("henhouse", 42, "Mr Taxman");
		assertTrue(image.equals(key.get("subclass")));

		key.setup("henhouse", 13, null);
		image = key.get("subclass");
		assertTrue(image.length() > 0);
		key.setup("henhouse", 13, "Mr Taxman");
		assertTrue(image.equals(key.get("subclass")));

		key.setup("outhouse", 0, "newbie");
		image = key.get("subclass");
		assertTrue(image.length() > 0);
		key.setup("outhouse", 0, "Mr Taxman");
		assertTrue(image.equals(key.get("subclass")));
	}
}
