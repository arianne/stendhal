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
package games.stendhal.server.entity.npc.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;

public class NakedConditionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MockStendlRPWorld.reset();
	}

	/**
	 * Tests for fire.
	 */
	@Test
	public final void testFire() {
		final Player bob = PlayerTestHelper.createPlayer("player");
		bob.setOutfit(0, 0, 0, null, 0, null, 0, null, 0);
		assertTrue(bob.getOutfit().isNaked());
		assertTrue(new NakedCondition().fire(bob, null, null));
		bob.setOutfit(0, 1, 0, null, 0, null, 0, null, 100);
		assertFalse("finally dressed", bob.getOutfit().isNaked());
		assertFalse("should be false when dressed", new NakedCondition().fire(
				bob, null, null));
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public final void testToString() {
		assertEquals("naked?", new NakedCondition().toString());
	}

	/**
	 * Tests for equals.
	 */
	@Test
	public void testEquals() {

		assertFalse(new NakedCondition().equals(null));

		final NakedCondition obj = new NakedCondition();
		assertTrue(obj.equals(obj));
		assertTrue(new NakedCondition().equals(new NakedCondition()));

	}

	/**
	 * Tests for hashCode.
	 */
	@Test
	public final void testHashCode() {
		final NakedCondition obj = new NakedCondition();
		assertEquals(obj.hashCode(), obj.hashCode());
		assertEquals(new NakedCondition().hashCode(),
				new NakedCondition().hashCode());

	}
}
