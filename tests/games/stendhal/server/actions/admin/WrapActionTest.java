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
package games.stendhal.server.actions.admin;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.item.Present;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPAction;
import utilities.PlayerTestHelper;

public class WrapActionTest {

	@BeforeClass
	public static void setUpBeforeclass() throws Exception {
		MockStendlRPWorld.get();
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for onActionnotAtplayer.
	 */
	@Test
	public void testOnActionnotAtplayer() {
		final WrapAction wrap = new WrapAction();
		final Player player = PlayerTestHelper.createPlayer("bob");
		final RPAction action = new RPAction();
		action.put("type", "wrap");
		action.put("args", "");
		wrap.onAction(player, action);
		assertThat(player.events().get(0).get("text"), is("You don't have any null"));
		player.clearEvents();

		action.put("args", "blabla");
		wrap.onAction(player, action);
		assertThat(player.events().get(0).get("text"), is("You don't have any null blabla"));

		player.clearEvents();

		action.put("target", "what");
		action.put("args", "blabla");
		wrap.onAction(player, action);
		assertThat(player.events().get(0).get("text"), is("You don't have any what blabla"));
	}

	/**
	 * Tests for onActionPotion.
	 */
	@Test
	public void testOnActionPotion() {
		final WrapAction wrap = new WrapAction();
		final Player player = PlayerTestHelper.createPlayer("bob");

		PlayerTestHelper.equipWithItem(player, "potion");

		final RPAction action = new RPAction();
		action.put("type", "wrap");
		action.put("target", "potion");
		wrap.onAction(player, action);
		assertTrue(player.isEquipped("present"));
		final Present present = (Present) player.getFirstEquipped("present");
		assertNotNull(present);
		assertThat(present.getInfoString(), is("potion"));
		present.onUsed(player);
		assertTrue(player.isEquipped("potion"));
	}

	/**
	 * Tests for onActionGreaterPotion.
	 */
	@Test
	public void testOnActionGreaterPotion() {

		final WrapAction wrap = new WrapAction();
		final Player player = PlayerTestHelper.createPlayer("bob");

		PlayerTestHelper.equipWithItem(player, "greater potion");

		final RPAction action = new RPAction();
		action.put("type", "wrap");
		action.put("target", "greater");
		action.put("args", "potion");
		wrap.onAction(player, action);
		assertTrue(player.isEquipped("present"));
		final Present present = (Present) player.getFirstEquipped("present");
		assertNotNull(present);
		assertThat(present.getInfoString(), is("greater potion"));
		present.onUsed(player);
		assertTrue(player.isEquipped("greater potion"));
	}

	/**
	 * Tests for onActionMithrilshield.
	 */
	@Test
	public void testOnActionMithrilshield() {
		final WrapAction wrap = new WrapAction();
		final Player player = PlayerTestHelper.createPlayer("bob");

		PlayerTestHelper.equipWithItem(player, "mithril shield");

		final RPAction action = new RPAction();
		action.put("type", "wrap");
		action.put("target", "mithril");
		action.put("args", "shield");
		wrap.onAction(player, action);
		assertTrue(player.isEquipped("present"));
		final Present present = (Present) player.getFirstEquipped("present");
		assertNotNull(present);
		assertThat(present.getInfoString(), is("mithril shield"));
		present.onUsed(player);
		assertTrue(player.isEquipped("mithril shield"));
	}

}
