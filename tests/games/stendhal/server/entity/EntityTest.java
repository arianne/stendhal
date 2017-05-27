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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;

public class EntityTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MockStendlRPWorld.reset();
	}

	/**
	 * Tests for nextTo.
	 */
	@Test
	public void testnextTo() {
		PlayerTestHelper.generatePlayerRPClasses();
		final Entity en = new Entity() { };
		final Player pl = PlayerTestHelper.createPlayer("player");

		en.setPosition(2, 2);

		pl.setPosition(1, 1);
		assertEquals(1, pl.getX());
		assertEquals(1, pl.getY());

		assertTrue(en.nextTo(pl));

		pl.setPosition(0, 0);
		assertFalse("Player at (0,0) is NOT next to (2,2)",
				en.nextTo(pl, 0.25));
		assertFalse("Player at (0,0) is NOT next to (2,2) with distance 0.5",
				en.nextTo(pl, 0.5));
		assertFalse("Player at (0,0) is NOT next to (2,2) with distance 0.75",
				en.nextTo(pl, 0.75));
		assertTrue("Player at (0,0) is next to (2,2) with distance 1.01",
				en.nextTo(pl, 1.01));

		pl.setPosition(2, 1);
		assertTrue(en.nextTo(pl, 0.25));

		pl.setPosition(3, 1);
		assertTrue(en.nextTo(pl, 0.25));

		pl.setPosition(1, 0);
		assertFalse(en.nextTo(pl, 0.25));

		pl.setPosition(2, 0);
		assertFalse(en.nextTo(pl, 0.25));

		pl.setPosition(3, 0);
		assertFalse(en.nextTo(pl, 0.25));
		assertTrue(en.nextTo(pl, 1.01));

		pl.setPosition(1, 2);
		assertTrue(en.nextTo(pl, 0.25));

		pl.setPosition(2, 2);
		assertTrue(en.nextTo(pl, 0.25));

		pl.setPosition(3, 2);
		assertTrue(en.nextTo(pl, 0.25));

		pl.setPosition(4, 2);
		assertFalse(en.nextTo(pl));
		assertTrue(en.nextTo(pl, 1.01));
	}

	/**
	 * Tests for squaredDistanceonebyone.
	 */
	@Test
	public void testSquaredDistanceonebyone() {
		final Entity en = new Entity() {
		};

		en.setPosition(4, 4);
		assertThat("same position", en.squaredDistance(4, 4), is(0.0));

		assertThat("next to", en.squaredDistance(4, 3), is(0.0));
		assertThat("next to", en.squaredDistance(4, 5), is(0.0));
		assertThat("next to", en.squaredDistance(3, 3), is(0.0));
		assertThat("next to", en.squaredDistance(3, 4), is(0.0));
		assertThat("next to", en.squaredDistance(3, 5), is(0.0));
		assertThat("next to", en.squaredDistance(5, 3), is(0.0));
		assertThat("next to", en.squaredDistance(5, 4), is(0.0));
		assertThat("next to", en.squaredDistance(5, 5), is(0.0));

		assertThat("one tile between", en.squaredDistance(2, 2), is(2.0));
		assertThat("one tile between", en.squaredDistance(3, 2), is(1.0));
		assertThat("one tile between", en.squaredDistance(4, 2), is(1.0));
		assertThat("one tile between", en.squaredDistance(5, 2), is(1.0));
		assertThat("one tile between", en.squaredDistance(6, 2), is(2.0));

		assertThat("one tile between", en.squaredDistance(2, 3), is(1.0));
		assertThat("one tile between", en.squaredDistance(2, 4), is(1.0));
		assertThat("one tile between", en.squaredDistance(2, 5), is(1.0));

		assertThat("one tile between", en.squaredDistance(6, 3), is(1.0));
		assertThat("one tile between", en.squaredDistance(6, 4), is(1.0));
		assertThat("one tile between", en.squaredDistance(6, 5), is(1.0));

		assertThat("one tile between", en.squaredDistance(2, 6), is(2.0));
		assertThat("one tile between", en.squaredDistance(3, 6), is(1.0));
		assertThat("one tile between", en.squaredDistance(4, 6), is(1.0));
		assertThat("one tile between", en.squaredDistance(5, 6), is(1.0));
		assertThat("one tile between", en.squaredDistance(6, 6), is(2.0));
	}
	/**
	 * Tests for squaredDistanceonebytwo.
	 */
	@Test
	public void testSquaredDistanceonebytwo() {
		final Entity en = new Entity() { };
		en.setPosition(4, 4);
		en.setSize(2, 1);

		assertThat("same position", en.squaredDistance(4, 4), is(0.0));

		assertThat("next to", en.squaredDistance(4, 3), is(0.0));
		assertThat("next to", en.squaredDistance(4, 5), is(0.0));
		assertThat("next to", en.squaredDistance(3, 3), is(0.0));
		assertThat("next to", en.squaredDistance(3, 4), is(0.0));
		assertThat("next to", en.squaredDistance(3, 5), is(0.0));
		assertThat("next to", en.squaredDistance(5, 3), is(0.0));
		assertThat("next to", en.squaredDistance(5, 4), is(0.0));
		assertThat("next to", en.squaredDistance(5, 5), is(0.0));

		assertThat("one tile between", en.squaredDistance(2, 2), is(2.0));
		assertThat("one tile between", en.squaredDistance(3, 2), is(1.0));
		assertThat("one tile between", en.squaredDistance(4, 2), is(1.0));
		assertThat("one tile between", en.squaredDistance(5, 2), is(1.0));
		assertThat("one tile between", en.squaredDistance(6, 2), is(1.0));
		assertThat("one tile between", en.squaredDistance(7, 2), is(2.0));

		assertThat("one tile between", en.squaredDistance(2, 3), is(1.0));
		assertThat("one tile between", en.squaredDistance(2, 4), is(1.0));
		assertThat("one tile between", en.squaredDistance(2, 5), is(1.0));

		assertThat("one tile between", en.squaredDistance(7, 3), is(1.0));
		assertThat("one tile between", en.squaredDistance(7, 4), is(1.0));
		assertThat("one tile between", en.squaredDistance(7, 5), is(1.0));

		assertThat("one tile between", en.squaredDistance(2, 6), is(2.0));
		assertThat("one tile between", en.squaredDistance(3, 6), is(1.0));
		assertThat("one tile between", en.squaredDistance(4, 6), is(1.0));
		assertThat("one tile between", en.squaredDistance(5, 6), is(1.0));
		assertThat("one tile between", en.squaredDistance(6, 6), is(1.0));
		assertThat("one tile between", en.squaredDistance(7, 6), is(2.0));

	}
	/**
	 * Tests for squaredDistanceelevenbytwelve.
	 */
	@Test
	public void testSquaredDistanceelevenbytwelve() {
		final Entity en = new Entity() { };
		en.setPosition(4, 4);
		en.setSize(11, 12);

		assertThat("same position", en.squaredDistance(4, 4), is(0.0));

		assertThat("next to", en.squaredDistance(4, 3), is(0.0));
		assertThat("next to", en.squaredDistance(4, 5), is(0.0));
		assertThat("next to", en.squaredDistance(3, 3), is(0.0));
		assertThat("next to", en.squaredDistance(3, 4), is(0.0));
		assertThat("next to", en.squaredDistance(3, 5), is(0.0));
		assertThat("next to", en.squaredDistance(5, 3), is(0.0));
		assertThat("next to", en.squaredDistance(5, 4), is(0.0));
		assertThat("next to", en.squaredDistance(5, 5), is(0.0));

		assertThat("one tile between", en.squaredDistance(2, 2), is(2.0));
		assertThat("one tile between", en.squaredDistance(3, 2), is(1.0));
		assertThat("one tile between", en.squaredDistance(4, 2), is(1.0));
		assertThat("one tile between", en.squaredDistance(5, 2), is(1.0));
		assertThat("one tile between", en.squaredDistance(6, 2), is(1.0));
		assertThat("one tile between", en.squaredDistance(7, 2), is(1.0));
		assertThat("one tile between", en.squaredDistance(8, 2), is(1.0));
		assertThat("one tile between", en.squaredDistance(9, 2), is(1.0));
		assertThat("one tile between", en.squaredDistance(10, 2), is(1.0));
		assertThat("one tile between", en.squaredDistance(11, 2), is(1.0));
		assertThat("one tile between", en.squaredDistance(12, 2), is(1.0));


		assertThat("one tile between", en.squaredDistance(1, 1), is(8.0));
		assertThat("one tile between", en.squaredDistance(2, 1), is(5.0));
		assertThat("one tile between", en.squaredDistance(3, 1), is(4.0));
		assertThat("one tile between", en.squaredDistance(4, 1), is(4.0));
		assertThat("one tile between", en.squaredDistance(5, 1), is(4.0));
		assertThat("one tile between", en.squaredDistance(6, 1), is(4.0));
		assertThat("one tile between", en.squaredDistance(7, 1), is(4.0));
		assertThat("one tile between", en.squaredDistance(8, 1), is(4.0));
		assertThat("one tile between", en.squaredDistance(9, 1), is(4.0));
		assertThat("one tile between", en.squaredDistance(10, 1), is(4.0));
		assertThat("one tile between", en.squaredDistance(11, 1), is(4.0));


		assertThat("one tile between", en.squaredDistance(2, 3), is(1.0));
		assertThat("one tile between", en.squaredDistance(2, 4), is(1.0));
		assertThat("one tile between", en.squaredDistance(2, 5), is(1.0));


	}
}
