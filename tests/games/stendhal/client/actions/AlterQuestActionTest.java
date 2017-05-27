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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

import games.stendhal.client.MockStendhalClient;
import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;

public class AlterQuestActionTest {

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
				assertEquals("alterquest", action.get("type"));
				assertEquals("schnick", action.get("target"));
				assertEquals("schnack", action.get("name"));
				assertEquals("schnuck", action.get("state"));

			}
		};
		final AlterQuestAction action = new AlterQuestAction();
		assertFalse(action.execute(null, null));
		assertFalse(action.execute(new String[] { "schnick" }, null));
		assertTrue(action.execute(new String[] { "schnick", "schnack", "schnuck" }, null));

		new MockStendhalClient() {
			@Override
			public void send(final RPAction action) {
				assertEquals("alterquest", action.get("type"));
				assertEquals("schnick", action.get("target"));
				assertEquals("schnick", action.get("name"));
				assertEquals(null, action.get("state"));

			}
		};

		assertTrue(action.execute(new String[] { "schnick", "schnick" }, null));

	}
}
