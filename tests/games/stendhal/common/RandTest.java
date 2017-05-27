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

import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RandTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for randUniform.
	 */
	@Test
	public void testRandUniform() {
		assertEquals(0, Rand.randUniform(0, 0));
	}

	/**
	 * Tests for randUniform2.
	 */
	@Test
	public void testRandUniform2() {
		for (int i = 0; i < 10; i++) {
			assertThat(Rand.randUniform(-1, 0), is(in(Arrays.asList(0, -1))));
			assertThat(Rand.randUniform(0, -1), is(in(Arrays.asList(0, -1))));
			assertThat(Rand.randUniform(1, -1), is(in(Arrays.asList(1, 0, -1))));
			assertThat(Rand.randUniform(-1, 1), is(in(Arrays.asList(1, 0, -1))));
			assertThat(Rand.randUniform(100, 102), is(in(Arrays.asList(100, 101, 102))));
		}
	}


}
