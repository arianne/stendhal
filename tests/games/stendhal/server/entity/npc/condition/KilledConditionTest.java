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

import java.util.Arrays;

import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;

public class KilledConditionTest {

	/**
	 * Tests for hashCode.
	 */
	@Test
	public final void testHashCode() {

		assertEquals(new KilledCondition("rat").hashCode(),
				new KilledCondition("rat").hashCode());
		assertEquals("i would expect this equal", new KilledCondition("rat",
				"mouse").hashCode(),
				new KilledCondition("mouse", "rat").hashCode());

	}

	/**
	 * Tests for fire.
	 */
	@Test
	public final void testFire() {
		KilledCondition kc = new KilledCondition();
		assertTrue(kc.fire(null, null, null));
		Player bob = PlayerTestHelper.createPlayer("player");

		assertTrue("bob has killed all of none", kc.fire(bob, null, null));
		kc = new KilledCondition("rat");
		assertFalse(kc.fire(bob, null, null));
		bob.setSoloKill("rat");
		assertTrue("bob killed a rat ", kc.fire(bob, null, null));

		bob = PlayerTestHelper.createPlayer("player");
		new KilledCondition(Arrays.asList("rat"));
		assertFalse(kc.fire(bob, null, null));
		bob.setSoloKill("rat");
		assertTrue("bob killed a rat ", kc.fire(bob, null, null));
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public final void testToString() {
		final KilledCondition kc = new KilledCondition("rat");
		assertEquals("KilledCondition <[rat]>", kc.toString());
	}

	/**
	 * Tests for equalsObject.
	 */
	@Test
	public final void testEqualsObject() {
		assertEquals(new KilledCondition("rat"), new KilledCondition("rat"));
		assertEquals("i would expect this equal", new KilledCondition("rat",
				"mouse"), new KilledCondition("mouse", "rat"));
	}

}
