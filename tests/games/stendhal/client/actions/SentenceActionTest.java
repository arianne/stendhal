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
import org.junit.Test;

import games.stendhal.client.MockStendhalClient;
import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;

public class SentenceActionTest {

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
				assertEquals(action.get("type"), "sentence");
			}
		};
		final SentenceAction action = new SentenceAction();
		assertFalse(action.execute(null, ""));
		String[] param = {null, "30"};
		assertTrue(action.execute(param, "30"));
		param[0] = "notnull";
		assertTrue(action.execute(param, "30"));
	}

	/**
	 * Tests for getMaximumParameters().
	 */
	@Test
	public void testGetMaximumParameters() {
		final SentenceAction action = new SentenceAction();
		assertThat(action.getMaximumParameters(), is(0));
	}

	/**
	 * Tests for getMinimumParameters.
	 */
	@Test
	public void testGetMinimumParameters() {
		final SentenceAction action = new SentenceAction();
		assertThat(action.getMinimumParameters(), is(0));
	}

}
