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
import static org.junit.Assert.assertTrue;
import games.stendhal.client.MockClientUI;
import games.stendhal.client.MockStendhalClient;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.scripting.ChatLineParser;
import marauroa.common.game.RPAction;

import org.junit.After;
import org.junit.Test;

/**
 * Test the SummonAtAction class.
 *
 * @author Martin Fuchs
 */
public class SummonAtActionTest {

	@After
	public void tearDown() throws Exception {
		StendhalClient.resetClient();
	}

	@Test
	public void testInvalidAmount() {
		// create client
		new MockStendhalClient() {
			@Override
			public void send(final RPAction action) {
				assertEquals("summonat", action.get("type"));
				assertEquals("player", action.get("target"));
				assertEquals("bag", action.get("slot"));
				assertEquals(1, action.getInt("amount"));
				assertEquals("5x", action.get("item"));
			}
		};

		final SummonAtAction action = new SummonAtAction();

		// issue "/summonat bag 5x money"
		assertTrue(action.execute(new String[]{"player", "bag", "5x"}, "money"));
	}

	/**
	 * Tests for execute.
	 */
	@Test
	public void testExecute() {
		// create client UI
		final MockClientUI clientUI = new MockClientUI();

		// create client
		new MockStendhalClient() {
			@Override
			public void send(final RPAction action) {
				assertEquals("summonat", action.get("type"));
				assertEquals("player", action.get("target"));
				assertEquals("bag", action.get("slot"));
				assertEquals(5, action.getInt("amount"));
				assertEquals("money", action.get("item"));
			}
		};

		// issue "/summonat bag 5 money"
		final SummonAtAction action = new SummonAtAction();
		assertTrue(action.execute(new String[]{"player", "bag", "5"}, "money"));
		assertEquals("", clientUI.getEventBuffer());
	}

	/**
	 * Tests for spaceHandling.
	 */
	@Test
	public void testSpaceHandling() {
		// create client UI
		final MockClientUI clientUI = new MockClientUI();

		// create client
		new MockStendhalClient() {
			@Override
			public void send(final RPAction action) {
				assertEquals("summonat", action.get("type"));
				assertEquals("player", action.get("target"));
				assertEquals("bag", action.get("slot"));
				assertEquals(1, action.getInt("amount"));
				assertEquals("silver sword", action.get("item"));
			}
		};

		// issue "/summonat bag silver sword"
		final SummonAtAction action = new SummonAtAction();
		assertTrue(action.execute(new String[]{"player", "bag", "silver"}, "sword"));
		assertEquals("", clientUI.getEventBuffer());
	}

	/**
	 * Tests for getMaximumParameters.
	 */
	@Test
	public void testGetMaximumParameters() {
		final SummonAtAction action = new SummonAtAction();
		assertEquals(3, action.getMaximumParameters());
	}

	/**
	 * Tests for getMinimumParameters.
	 */
	@Test
	public void testGetMinimumParameters() {
		final SummonAtAction action = new SummonAtAction();
		assertEquals(3, action.getMinimumParameters());
	}

	/**
	 * Tests for fromChatline.
	 */
	@Test
	public void testFromChatline() throws Exception {
		// create client UI
		@SuppressWarnings("unused")
		final MockClientUI clientUI = new MockClientUI();

		// create client
		new MockStendhalClient() {
			@Override
			public void send(final RPAction action) {
				assertEquals("summonat", action.get("type"));
				assertEquals("memem", action.get("target"));
				assertEquals("bag", action.get("slot"));
				assertEquals(3, action.getInt("amount"));
				assertEquals("greater potion", action.get("item"));
			}
		};
		SlashActionRepository.register();
		ChatLineParser.parseAndHandle("/summonat memem bag 3 greater potion");
	}
	
}
