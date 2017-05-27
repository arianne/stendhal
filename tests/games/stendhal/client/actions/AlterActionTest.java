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

public class AlterActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		StendhalClient.resetClient();
	}

	/**
	 * Tests for execute.
	 */
	@Test
	public void testExecute() {
		new MockStendhalClient() {
			@Override
			public void send(final RPAction action) {
				assertEquals("alter", action.get("type"));
				assertEquals("schnick", action.get("target"));
				assertEquals("schnack", action.get("stat"));
				assertEquals("schnuck", action.get("mode"));
				assertEquals("blabla", action.get("value"));
			}
		};
		final AlterAction action = new AlterAction();
		assertFalse(action.execute(null, null));
		assertFalse(action.execute(new String[] { "schnick" }, null));
		assertFalse(action.execute(new String[] { "schnick", "schnick" }, null));
		assertFalse(action.execute(new String[] { "schnick", "schnick", "schnick" }, null));

		assertTrue(action.execute(new String[] { "schnick", "schnack", "schnuck" }, "blabla"));
	}

	/**
	 * Tests for getMaximumParameters.
	 */
	@Test
	public void testGetMaximumParameters() {
		final AlterAction action = new AlterAction();
		assertThat(action.getMaximumParameters(), is(3));
	}

	/**
	 * Tests for getMinimumParameters.
	 */
	@Test
	public void testGetMinimumParameters() {
		final AlterAction action = new AlterAction();
		assertThat(action.getMinimumParameters(), is(3));
	}

}
