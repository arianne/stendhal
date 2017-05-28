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

import org.junit.Test;

import games.stendhal.client.MockClientUI;
import games.stendhal.client.MockStendhalClient;
import games.stendhal.client.entity.User;
import games.stendhal.common.Constants;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObject.ID;

/**
 * Test the DropAction class.
 *
 * @author Martin Fuchs
 */
public class DropActionTest {
	private static final String ZONE_NAME = "Testzone";
	private static final int USER_ID = 1001;
	private static final int MONEY_ID = 1234;
	private static final int SILVER_SWORD_ID = 1235;

	/**
	 * Create and initialize a User object.
	 * @return a playerPRObject
	 */
	private static RPObject createPlayer() {
		final RPObject rpo = new RPObject();

		rpo.put("type", "player");
		rpo.put("name", "player");
		rpo.setID(new ID(USER_ID, ZONE_NAME));

		final User pl = new User();
		pl.initialize(rpo);

		for (final String slotName : Constants.CARRYING_SLOTS) {
			rpo.addSlot(slotName);
		}

		return rpo;
	}

	private static RPObject createItem(final String itemName, final int id, final int amount) {
		final RPObject rpo = new RPObject();
		rpo.put("type", "item");
		rpo.put("name", itemName);
		rpo.put("quantity", amount);
		rpo.setID(new ID(id, ZONE_NAME));

		return rpo;
	}

	/**
	 * Tests for noMoney.
	 */
	@Test
	public void testNoMoney() {
		final MockClientUI clientUI = new MockClientUI();
		final DropAction action = new DropAction();

		createPlayer();

		// issue "/drop money"
		assertTrue(action.execute(new String[]{"money"}, ""));
		assertEquals("You don't have any money", clientUI.getEventBuffer());
	}

	/**
	 * Tests for invalidAmount.
	 */
	@Test
	public void testInvalidAmount() {
		final MockClientUI clientUI = new MockClientUI();
		final DropAction action = new DropAction();

		createPlayer();

		// issue "/drop 85x money"
		assertTrue(action.execute(new String[]{"85x"}, "money"));
		assertEquals("Invalid quantity: 85x", clientUI.getEventBuffer());
	}

	/**
	 * Tests for dropSingle.
	 */
	@Test
	public void testDropSingle() {
		// create client UI
		final MockClientUI clientUI = new MockClientUI();

		// create client
		new MockStendhalClient() {
			@Override
			public void send(final RPAction action) {
				client = null;
				assertEquals("drop", action.get("type"));
				assertEquals(USER_ID, action.getInt("baseobject"));
				assertEquals(0, action.getInt("x"));
				assertEquals(0, action.getInt("y"));
				assertEquals("bag", action.get("baseslot"));
				assertEquals(1, action.getInt("quantity"));
				assertEquals(MONEY_ID, action.getInt("baseitem"));
			}
		};

		// create a player and give him some money
		final RPObject player = createPlayer();
		player.getSlot("bag").addPreservingId(createItem("money", MONEY_ID, 100));

		// issue "/drop money"
		final DropAction action = new DropAction();
		assertTrue(action.execute(new String[]{"money"}, ""));
		assertEquals("", clientUI.getEventBuffer());
	}

	/**
	 * Tests for dropMultiple.
	 */
	@Test
	public void testDropMultiple() {
		// create client UI
		final MockClientUI clientUI = new MockClientUI();

		// create client
		new MockStendhalClient() {
			@Override
			public void send(final RPAction action) {
				client = null;
				assertEquals("drop", action.get("type"));
				assertEquals(USER_ID, action.getInt("baseobject"));
				assertEquals(0, action.getInt("x"));
				assertEquals(0, action.getInt("y"));
				assertEquals("bag", action.get("baseslot"));
				assertEquals(50, action.getInt("quantity"));
				assertEquals(MONEY_ID, action.getInt("baseitem"));
			}
		};

		// create a player and give him some money
		final RPObject player = createPlayer();
		player.getSlot("bag").addPreservingId(createItem("money", MONEY_ID, 100));

		// issue "/drop 50 money"
		final DropAction action = new DropAction();
		assertTrue(action.execute(new String[]{"50"}, "money"));
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
				client = null;
				assertEquals("drop", action.get("type"));
				assertEquals(USER_ID, action.getInt("baseobject"));
				assertEquals(0, action.getInt("x"));
				assertEquals(0, action.getInt("y"));
				assertEquals("bag", action.get("baseslot"));
				assertEquals(1, action.getInt("quantity"));
				assertEquals(SILVER_SWORD_ID, action.getInt("baseitem"));
			}
		};

		// create a player and give him some money
		final RPObject player = createPlayer();
		player.getSlot("bag").addPreservingId(createItem("silver sword", SILVER_SWORD_ID, 1));

		// issue "/drop money"
		final DropAction action = new DropAction();
		assertTrue(action.execute(new String[]{"silver"}, "sword"));
		assertEquals("", clientUI.getEventBuffer());
	}

	/**
	 * Tests for getMaximumParameters.
	 */
	@Test
	public void testGetMaximumParameters() {
		final DropAction action = new DropAction();
		assertThat(action.getMaximumParameters(), is(1));
	}

	/**
	 * Tests for getMinimumParameters.
	 */
	@Test
	public void testGetMinimumParameters() {
		final DropAction action = new DropAction();
		assertThat(action.getMinimumParameters(), is(1));
	}

}
