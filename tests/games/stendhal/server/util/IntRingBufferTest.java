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
package games.stendhal.server.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * tests for IntRingBuffer
 *
 * @author hendrik
 */
public class IntRingBufferTest {

	/**
	 * tests for isEmpty()
	 */
	@Test
	public void testIsEmpty() {
		IntRingBuffer buffer = new IntRingBuffer(2);
		assertTrue(buffer.isEmpty());
		buffer.add(1);
		assertFalse(buffer.isEmpty());
		buffer.add(2);
		buffer.removeOldest();
		assertFalse(buffer.isEmpty());
		buffer.add(3);
		assertFalse(buffer.isEmpty());
		buffer.removeOldest();
		assertFalse(buffer.isEmpty());
		buffer.removeOldest();
		assertTrue(buffer.isEmpty());
	}

	/**
	 * tests for isFull()
	 */
	@Test
	public void testIsFull() {
		IntRingBuffer buffer = new IntRingBuffer(2);
		assertFalse(buffer.isFull());
		buffer.add(1);
		assertFalse(buffer.isFull());
		buffer.add(2);
		buffer.removeOldest();
		assertFalse(buffer.isFull());
		buffer.add(3);
		assertTrue(buffer.isFull());
		buffer.removeOldest();
		assertFalse(buffer.isFull());
		buffer.removeOldest();
		assertFalse(buffer.isFull());
	}

	/**
	 * tests for add()
	 */
	@Test
	public void testAdd() {
		IntRingBuffer buffer = new IntRingBuffer(2);
		assertTrue(buffer.add(1));
		assertTrue(buffer.add(2));
		assertFalse(buffer.add(3));
		buffer.removeOldest();
		assertTrue(buffer.add(4));
	}

	/**
	 * Tests for removeOldest()
	 */
	@Test
	public void testRemoveOldest() {
		IntRingBuffer buffer = new IntRingBuffer(2);
		assertFalse(buffer.removeOldest());
		buffer.add(1);
		assertTrue(buffer.removeOldest());
		assertFalse(buffer.removeOldest());
		buffer.add(2);
		buffer.add(3);
		assertTrue(buffer.removeOldest());
		assertTrue(buffer.removeOldest());
		assertFalse(buffer.removeOldest());
	}

	/**
	 * tests for removeSmaller
	 */
	@Test
	public void testRemoveSmaller() {
		IntRingBuffer buffer = new IntRingBuffer(2);
		buffer.removeSmaller(100);
		assertTrue(buffer.isEmpty());

		buffer.add(5);
		buffer.removeSmaller(1);
		assertFalse(buffer.isEmpty());

		buffer.removeSmaller(100);
		assertTrue(buffer.isEmpty());

		buffer.add(5);
		buffer.add(10);
		buffer.removeSmaller(6);
		assertFalse(buffer.isEmpty());

		buffer.add(20);
		buffer.removeSmaller(100);
		assertTrue(buffer.isEmpty());
	}
}
