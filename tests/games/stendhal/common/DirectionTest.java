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
package games.stendhal.common;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.util.BitSet;

import org.junit.Test;

public class DirectionTest {

	/**
	 * Tests for build.
	 */
	@Test
	public final void testBuild() {
		assertSame(Direction.STOP, Direction.build(0));
		assertSame(Direction.UP, Direction.build(1));
		assertSame(Direction.RIGHT, Direction.build(2));
		assertSame(Direction.DOWN, Direction.build(3));
		assertSame(Direction.LEFT, Direction.build(4));
	}

	/**
	 * Tests for getdx.
	 */
	@Test
	public final void testGetdx() {
		assertEquals(0, Direction.STOP.getdx());
		assertEquals(0, Direction.UP.getdx());
		assertEquals(0, Direction.DOWN.getdx());

		assertEquals(1, Direction.RIGHT.getdx());
		assertEquals(-1, Direction.LEFT.getdx());
	}

	/**
	 * Tests for getdy.
	 */
	@Test
	public final void testGetdy() {
		assertEquals(0, Direction.STOP.getdy());
		assertEquals(0, Direction.RIGHT.getdy());
		assertEquals(0, Direction.LEFT.getdy());

		assertEquals(-1, Direction.UP.getdy());
		assertEquals(1, Direction.DOWN.getdy());
	}

	/**
	 * Tests for get.
	 */
	@Test
	public final void testGet() {
		assertEquals(0, Direction.STOP.get());
		assertEquals(1, Direction.UP.get());
		assertEquals(2, Direction.RIGHT.get());
		assertEquals(3, Direction.DOWN.get());
		assertEquals(4, Direction.LEFT.get());
	}

	/**
	 * Tests for oppositeDirection.
	 */
	@Test
	public final void testOppositeDirection() {
		assertEquals(Direction.UP, Direction.DOWN.oppositeDirection());
		assertEquals(Direction.DOWN, Direction.UP.oppositeDirection());
		assertEquals(Direction.LEFT, Direction.RIGHT.oppositeDirection());
		assertEquals(Direction.RIGHT, Direction.LEFT.oppositeDirection());
		assertEquals(Direction.STOP, Direction.STOP.oppositeDirection());
		assertEquals(Direction.UP, Direction.UP.oppositeDirection()
				.oppositeDirection());
	}
	/**
	 * Tests for nextDirection.
	 */
	@Test
	public final void testNextDirection() {
		assertEquals(Direction.LEFT, Direction.STOP.nextDirection());
		assertEquals(Direction.LEFT, Direction.DOWN.nextDirection());
		assertEquals(Direction.UP, Direction.LEFT.nextDirection());
		assertEquals(Direction.RIGHT, Direction.UP.nextDirection());
		assertEquals(Direction.DOWN, Direction.RIGHT.nextDirection());
	}

	/**
	 * Tests for rand.
	 */
	@Test(timeout = 10000)
	public final void testRand() {

		BitSet gotcha = new BitSet(5);
		gotcha.set(0);
		assertThat(gotcha.cardinality(), is(1));
		gotcha.set(2);
		assertThat(gotcha.cardinality(), is(2));
		gotcha.set(1);
		assertThat(gotcha.cardinality(), is(3));
		gotcha.set(3);
		assertThat(gotcha.cardinality(), is(4));
		gotcha.set(4);
		assertThat(gotcha.cardinality(), is(5));

		gotcha = new BitSet(5);
		int val;
		while (gotcha.cardinality() < 4) {
			val = Direction.rand().get();
			gotcha.set(val);
		}

		assertFalse(gotcha.get(0));
	}



}
