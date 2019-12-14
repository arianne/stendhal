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
package games.stendhal.server.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import utilities.PlayerTestHelper;

public class OutfitActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * Tests for onWrongAction.
	 */
	@Test
	public void testOnWrongAction() {
		final OutfitAction oa = new OutfitAction();
		final Player player = PlayerTestHelper.createPlayer("player");
		final RPAction action = new RPAction();
		oa.onAction(player, action);
		assertTrue("no exception thrown", true);
	}

	/**
	 * Tests for onActionWrongValue.
	 */
	/*
	 * FIXME: setting an entity's outfit can be done with a string
	 *        so perhaps this test is not required any longer?
	@Test(expected = NumberFormatException.class)
	public void testOnActionWrongValue() {
		final OutfitAction oa = new OutfitAction();
		final Player player = PlayerTestHelper.createPlayer("player");
		final RPAction action = new RPAction();
		action.put("value", "schnick");
		oa.onAction(player, action);
	}
	*/

	/**
	 * Tests for onAction.
	 */
	@Test
	public void testOnAction() {
		final OutfitAction oa = new OutfitAction();
		final Player player = PlayerTestHelper.createPlayer("player");
		final RPAction action = new RPAction();

		// FIXME: why does created player not have "outfit_ext"
		//assertNotNull(player.get("outfit_ext"));
		action.put("value", 1);
		oa.onAction(player, action);
		assertTrue(player.has("outfit_ext"));
		assertEquals(Integer.valueOf(0), player.getOutfit().getLayer("body"));

		action.put("value", 51515151);
		oa.onAction(player, action);
		assertTrue(player.has("outfit_ext"));
		assertEquals("invalid player outfit", Integer.valueOf(0), player.getOutfit().getLayer("body"));
	}

	/**
	 * Tests colors
	 */
	@Test
	public void testColors() {
		final OutfitAction oa = new OutfitAction();
		final Player player = PlayerTestHelper.createPlayer("player");
		final RPAction action = new RPAction();

		// FIXME: why does created player not have "outfit_ext"
		//assertNotNull(player.get("outfit_ext"));
		action.put("value", 1);
		action.put("hair", 0xfeed);
		action.put("dress", 0xf00d);
		// valid color attribute, but not settable by the player
		action.put("detail", 0xbadf00d);
		oa.onAction(player, action);
		assertTrue(player.has("outfit_ext"));
		assertEquals(Integer.valueOf(0), player.getOutfit().getLayer("body"));

		action.put("value", 51515151);
		oa.onAction(player, action);
		assertTrue(player.has("outfit_ext"));
		assertEquals("invalid player outfit", Integer.valueOf(0), player.getOutfit().getLayer("body"));
		assertEquals("invalid hair color", 0xfeed, player.getInt("outfit_colors", "hair"));
		assertEquals("invalid dress color", 0xf00d, player.getInt("outfit_colors", "dress"));
		assertFalse("invalid attribute", player.has("outfit_colors", "detail"));
	}
}
