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
package games.stendhal.server.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.maps.MockStendlRPWorld;

public class BloodTest {

	@BeforeClass
	public static void setUp() throws Exception {
		MockStendlRPWorld.get();

	}

	/**
	 * Tests for describe.
	 */
	@Test
	public final void testDescribe() {
		final Blood bl = new Blood();
		assertEquals("You see a pool of blood.", bl.describe());
	}

	/**
	 * Tests for bloodStringInt.
	 */
	@Test
	public final void testBloodStringInt() {
		final Blood bl = new Blood("blabla", 1);
		assertEquals("blabla", bl.get("class"));
		assertEquals("1", bl.get("amount"));
	}

	/**
	 * Tests for onTurnReached.
	 */
	@Test
	public final void testOnTurnReached() {
		final StendhalRPZone zone = new StendhalRPZone("testzone");
		final Blood bl = new Blood();
		zone.add(bl);
		assertNotNull(zone.getBlood(0, 0));
		bl.onTurnReached(1);
		assertNull(zone.getBlood(0, 0));
	}

}
