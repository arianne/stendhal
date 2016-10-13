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
package games.stendhal.client.gui.textformat;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TallyMarksTest {

	@Test
	public void testNegative() {
		assertEquals("0", new TallyMarks(-1).toString());
	}

	@Test
	public void testZero() {
		assertEquals("0", new TallyMarks(0).toString());
	}

	@Test
	public void testOne() {
		assertEquals("1", new TallyMarks(1).toString());
	}

	@Test
	public void testFour() {
		assertEquals("4", new TallyMarks(4).toString());
	}

	@Test
	public void testFive() {
		assertEquals("5", new TallyMarks(5).toString());
	}

	@Test
	public void testSix() {
		assertEquals("51", new TallyMarks(6).toString());
	}

	@Test
	public void testTen() {
		assertEquals("55", new TallyMarks(10).toString());
	}

	@Test
	public void testTwelve() {
		assertEquals("552", new TallyMarks(12).toString());
	}
}
