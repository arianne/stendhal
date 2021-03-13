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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

import games.stendhal.client.MockStendhalClient;
import games.stendhal.client.StendhalClient;
import games.stendhal.common.constants.Actions;
import marauroa.common.game.RPAction;

public class RemoveDetailActionTest {

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
				/*for (final String attrib : action) {
					assertEquals("type", attrib);
					assertEquals("teleport", (action.get(attrib)));
				}*/
				assertEquals(Actions.REMOVEDETAIL, action.get("type"));
			}
		};
		final RemoveDetailAction action = new RemoveDetailAction();
		String[] param = {};
		assertTrue(action.execute(param, "reason"));
	}

	/**
	 * Tests for getMaximumParameters().
	 */
	@Test
	public void testGetMaximumParameters() {
		final RemoveDetailAction action = new RemoveDetailAction();
		assertThat(action.getMaximumParameters(), is(0));
	}

	/**
	 * Tests for getMinimumParameters.
	 */
	@Test
	public void testGetMinimumParameters() {
		final RemoveDetailAction action = new RemoveDetailAction();
		assertThat(action.getMinimumParameters(), is(0));
	}

}
