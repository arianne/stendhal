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
package games.stendhal.client.actions;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.client.MockStendhalClient;
import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;

public class AdminLevelActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		StendhalClient.resetClient();
	}

	/**
	 * Tests for executeOneParam.
	 */
	@Test
	public void testExecuteOneParam() {
		new MockStendhalClient() {
			@Override
			public void send(final RPAction action) {
				assertEquals("adminlevel", action.get("type"));
				assertEquals("schnick", action.get("target"));
			}
		};
		final AdminLevelAction action = new AdminLevelAction();
		assertFalse(action.execute(null, null));
		assertTrue(action.execute(new String[] { "schnick" }, null));
	}

	/**
	 * Tests for executeSecondParamNull.
	 */
	@Test
	public void testExecuteSecondParamNull() {

		new MockStendhalClient() {
			@Override
			public void send(final RPAction action) {
				assertEquals("adminlevel", action.get("type"));
				assertEquals("schnick", action.get("target"));
				assertFalse(action.has("newlevel"));
			}
		};
		final AdminLevelAction action = new AdminLevelAction();
		assertFalse(action.execute(null, null));
		assertTrue(action.execute(new String[] { "schnick", null }, null));
	}

	/**
	 * Tests for executeSecondParamValid.
	 */
	@Test
	public void testExecuteSecondParamValid() {

		new MockStendhalClient() {
			@Override
			public void send(final RPAction action) {
				assertEquals("adminlevel", action.get("type"));
				assertEquals("schnick", action.get("target"));
				assertEquals("100", action.get("newlevel"));
			}
		};
		final AdminLevelAction action = new AdminLevelAction();
		assertFalse(action.execute(null, null));
		assertTrue(action.execute(new String[] { "schnick", "100" }, null));
	}

	/**
	 * Tests for getMaximumParameters.
	 */
	@Test
	public void testGetMaximumParameters() {
		final AdminLevelAction action = new AdminLevelAction();
		assertThat(action.getMaximumParameters(), is(2));
	}

	/**
	 * Tests for getMinimumParameters.
	 */
	@Test
	public void testGetMinimumParameters() {
		final AdminLevelAction action = new AdminLevelAction();
		assertThat(action.getMinimumParameters(), is(1));
	}

}
