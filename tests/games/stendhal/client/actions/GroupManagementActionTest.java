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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.client.MockStendhalClient;
import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;

public class GroupManagementActionTest {
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
			}
		};

		final GroupManagementAction action = new GroupManagementAction(new GroupMessageAction());
		String[] params = {"target", "killtype", "count"};
		assertTrue(action.execute(params, "remainder"));

		final GroupManagementAction action_2 = new GroupManagementAction(new GroupMessageAction());
		String[] params_2 = {"message", "killtype", "count"};
		assertTrue(action_2.execute(params_2, "remainder"));
	}

	/**
	 * Tests for getMaximumParameters.
	 */
	@Test
	public void testGetMaximumParameters() {
		final GroupManagementAction action = new GroupManagementAction(new GroupMessageAction());
		assertThat(action.getMaximumParameters(), is(1));
	}

	/**
	 * Tests for getMinimumParameters.
	 */
	@Test
	public void testGetMinimumParameters() {
		final GroupManagementAction action = new GroupManagementAction(new GroupMessageAction());
		assertThat(action.getMinimumParameters(), is(1));
	}
}
