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
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;

public class AntidoteEaterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MockStendlRPWorld.reset();
	}

	/**
	 * Tests for hashCode.
	 */
	@Test
	public void testHashCode() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		AntidoteEater eater1 = new AntidoteEater(bob);
		AntidoteEater eater2 = new AntidoteEater(bob);
		assertTrue(eater1.equals(eater2));
		assertTrue(eater2.equals(eater1));
		assertEquals(eater1.hashCode(), eater2.hashCode());
	}

}
