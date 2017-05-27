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

import java.awt.Rectangle;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.util.Area;
import utilities.PlayerTestHelper;

public class PlayerInAreaConditionTest {

	@Before
	public void setUp() throws Exception {
		MockStendlRPWorld.get();
	}

	/**
	 * Tests for hashCode.
	 */
	@SuppressWarnings("serial")
	@Test
	public final void testHashCode() {
		final Area ar = new Area(new StendhalRPZone("test"), new Rectangle() {
			// this is an anonymous sub class
		});

		assertEquals((new PlayerInAreaCondition(ar)).hashCode(),
				new PlayerInAreaCondition(ar).hashCode());
	}

	/**
	 * Tests for fire.
	 */
	@SuppressWarnings("serial")
	@Test
	public final void testFire() {
		final StendhalRPZone zone = new StendhalRPZone("test");
		final Area ar = new Area(zone, new Rectangle(-2, -2, 4, 4) {
			// this is an anonymous sub class
		});
		final PlayerInAreaCondition cond = new PlayerInAreaCondition(ar);
		final Player player = PlayerTestHelper.createPlayer("player");
		assertFalse(cond.fire(player, null, null));
		zone.add(player);
		assertTrue(ar.contains(player));
		assertTrue(cond.fire(player, null, null));

	}

	/**
	 * Tests for fireNPE.
	 */
	@Test(expected = NullPointerException.class)
	public void testFireNPE() {
		final PlayerInAreaCondition cond = new PlayerInAreaCondition(null);
		final Player player = PlayerTestHelper.createPlayer("player");
		assertFalse(cond.fire(player, null, null));
	}

	/**
	 * Tests for toString.
	 */
	@SuppressWarnings("serial")
	@Test
	public final void testToString() {
		final Area ar = new Area(new StendhalRPZone("test"), new Rectangle() {
			// this is an anonymous sub class
		});
		assertEquals("player in <" + ar.toString() + ">",
				new PlayerInAreaCondition(ar).toString());
	}

	/**
	 * Tests for equalsObject.
	 */
	@SuppressWarnings("serial")
	@Test
	public final void testEqualsObject() {
		final Area ar = new Area(new StendhalRPZone("test"), new Rectangle() {
			// this is an anonymous sub class
		});
		final Area ar2 = new Area(new StendhalRPZone("test2"), new Rectangle() {
			// this is an anonymous sub class
		});
		assertTrue((new PlayerInAreaCondition(ar)).equals(new PlayerInAreaCondition(ar)));
		assertFalse((new PlayerInAreaCondition(ar)).equals(new PlayerInAreaCondition(ar2)));

		assertTrue(new PlayerInAreaCondition(ar).equals(new PlayerInAreaCondition(ar) {
			// this is an anonymous sub class
		}));
	}

}
