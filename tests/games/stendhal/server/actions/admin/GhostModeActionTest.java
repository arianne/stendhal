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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPAction;
import marauroa.server.game.db.DatabaseFactory;
import utilities.PlayerTestHelper;

public class GhostModeActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new DatabaseFactory().initializeDatabase();
		MockStendlRPWorld.get();
	}

	/**
	 * Tests for ghostmode.
	 */
	@Test
	public final void testGhostmode() {
		final Player hugo = PlayerTestHelper.createPlayer("hugo");
		hugo.put("adminlevel", 5000);

		final Player bob = PlayerTestHelper.createPlayer("bob21233");
		bob.put("buddies", hugo.getName(), true);

		final Player jack = PlayerTestHelper.createPlayer("jack");

		MockStendhalRPRuleProcessor.get().addPlayer(hugo);
		MockStendhalRPRuleProcessor.get().addPlayer(bob);
		MockStendhalRPRuleProcessor.get().addPlayer(jack);

		final RPAction action = new RPAction();

		action.put("type", "ghostmode");
		assertFalse(hugo.isInvisibleToCreatures());
		assertFalse(hugo.isGhost());

		CommandCenter.execute(hugo, action);

		assertTrue(hugo.isInvisibleToCreatures());
		assertTrue(hugo.isGhost());

		assertEquals(null, bob.get("online"));
		assertEquals("hugo", bob.get("offline"));

		assertEquals(null, jack.get("online"));
		assertEquals(null, jack.get("offline"));

		bob.remove("offline");
		bob.clearEvents();
		CommandCenter.execute(hugo, action);

		assertTrue(hugo.isInvisibleToCreatures());
		assertFalse(hugo.isGhost());

		assertEquals("hugo", bob.get("online"));
		assertEquals(null, bob.get("offline"));

		assertEquals(null, jack.get("online"));
		assertEquals(null, jack.get("offline"));
	}


}
