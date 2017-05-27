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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import games.stendhal.common.Line.Action;

public class LineTest {

	/**
	 * Tests for line.
	 */
	@Test
	public void testLine() {
		final String expected = "10,10;11,10;12,10;13,11;14,11;15,12;16,12;17,13;18,13;19,14;20,14;21,14;22,15;23,15;24,16;25,16;26,17;27,17;28,18;29,18;30,18;31,19;32,19;33,20;34,20;35,21;36,21;37,22;38,22;39,22;40,23;41,23;42,24;43,24;44,25;45,25;46,26;47,26;48,26;49,27;50,27;51,28;52,28;53,29;54,29;55,30;56,30;57,30;58,31;59,31;60,32;61,32;62,33;63,33;64,34;65,34;66,34;67,35;68,35;69,36;70,36;71,37;72,37;73,38;74,38;75,38;76,39;77,39;78,40;79,40;80,41;81,41;82,42;83,42;84,42;85,43;86,43;87,44;88,44;89,45;90,45;91,46;92,46;93,46;94,47;95,47;96,48;97,48;98,49;99,49;100,50;";
		final StringBuilder sb = new StringBuilder();
		Line.renderLine(10, 10, 100, 50, new Action() {

			@Override
			public void fire(final int x, final int y) {
				sb.append(x + "," + y + ";");
			}
		});

		assertEquals("Current path finding. Note if you improve the " + "pathfinder, you need to adjust this test.",
				expected, sb.toString());
	}

	/**
	 * Tests for line2_2_10_7.
	 */
	@Test
	public void testLine2_2_10_7() {
		final String expected = "2,5;3,5;4,5;5,5;6,6;7,6;8,6;9,6;10,7;";
		final StringBuilder sb = new StringBuilder();

		Line.renderLine(2, 5, 10, 7, new Action() {

			@Override
			public void fire(final int x, final int y) {
				sb.append(x + "," + y + ";");
			}
		});
		assertEquals("Current path finding. Note if you improve the " + "pathfinder, you need to adjust this test.",
				expected, sb.toString());
	}

	/**
	 * Tests for line10_7_2_2.
	 */
	@Test
	public void testLine10_7_2_2() {
		final String expected = "10,7;9,7;8,6;7,6;6,5;5,4;4,4;3,3;2,2;";
		final StringBuilder sb = new StringBuilder();

		Line.renderLine(10, 7, 2, 2, new Action() {

			@Override
			public void fire(final int x, final int y) {
				sb.append(x + "," + y + ";");
			}
		});

		assertEquals("Current path finding. Note if you improve the " + "pathfinder, you need to adjust this test.",
				expected, sb.toString());

	}

	/**
	 * Tests for line1_1_10_10.
	 */
	@Test
	public void testLine1_1_10_10() {

		final String expected = "1,1;2,2;3,3;4,4;5,5;6,6;7,7;8,8;9,9;10,10;";
		final StringBuilder sb = new StringBuilder();
		Line.renderLine(1, 1, 10, 10, new Action() {

			@Override
			public void fire(final int x, final int y) {
				sb.append(x + "," + y + ";");
			}
		});

		assertEquals("Current path finding. Note if you improve the " + "pathfinder, you need to adjust this test.",
				expected, sb.toString());

	}

	/**
	 * Tests for line1_0_10_0.
	 */
	@Test
	public void testLine1_0_10_0() {

		final String expected = "1,0;2,0;3,0;4,0;5,0;6,0;7,0;8,0;9,0;10,0;";
		final StringBuilder sb = new StringBuilder();
		Line.renderLine(1, 0, 10, 0, new Action() {

			@Override
			public void fire(final int x, final int y) {
				sb.append(x + "," + y + ";");
			}
		});

		assertEquals("Current path finding. Note if you improve the " + "pathfinder, you need to adjust this test.",
				expected, sb.toString());

	}
}
