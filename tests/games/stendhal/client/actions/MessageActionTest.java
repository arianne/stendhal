/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
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

public class MessageActionTest {
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
				assertEquals("tell", action.get("type"));
			}
		};

		final MessageAction action = new MessageAction();
		String[] params = {"lastplayertell"};
		assertFalse(action.execute(params, ""));
		final MessageAction action_2 = new MessageAction();
		assertTrue(action_2.execute(params, "remainder"));
	}

	/**
	 * Tests for getMaximumParameters.
	 */
	@Test
	public void testGetMaximumParameters() {
		final MessageAction action = new MessageAction();
		assertThat(action.getMaximumParameters(), is(1));
	}

	/**
	 * Tests for getMinimumParameters.
	 */
	@Test
	public void testGetMinimumParameters() {
		final MessageAction action = new MessageAction();
		assertThat(action.getMinimumParameters(), is(1));
	}

	/*
	 * Tests for getLastPlayerTell
	 */
	@Test
	public void testGetLastPlayerTell() {
		final MessageAction action = new MessageAction();
		String[] params = {"lastplayertell"};
		action.execute(params, "");
		assertEquals(params[0], action.getLastPlayerTell());
	}
}
