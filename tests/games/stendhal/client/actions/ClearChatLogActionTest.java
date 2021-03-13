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
import org.junit.Ignore;
import org.junit.Test;

import games.stendhal.client.StendhalClient;

public class ClearChatLogActionTest {
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
	@Ignore
	public void testExecute() {

		final ClearChatLogAction action = new ClearChatLogAction();
		String[] params = {"target", "killtype", "count"};
		assertTrue(action.execute(params, "remainder"));
	}

	/**
	 * Tests for getMaximumParameters.
	 */
	@Test
	public void testGetMaximumParameters() {
		final ClearChatLogAction action = new ClearChatLogAction();
		assertThat(action.getMaximumParameters(), is(0));
	}

	/**
	 * Tests for getMinimumParameters.
	 */
	@Test
	public void testGetMinimumParameters() {
		final ClearChatLogAction action = new ClearChatLogAction();
		assertThat(action.getMinimumParameters(), is(0));
	}
}
