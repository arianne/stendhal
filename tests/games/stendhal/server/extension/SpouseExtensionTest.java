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
package games.stendhal.server.extension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

/**
 * JUnit Tests for SpouseExtension.
 * 
 * @author Martin Fuchs
 */
public class SpouseExtensionTest {

	private static final String ZONE_NAME = "testzone";

	@BeforeClass
	public static final void setUpClass() throws Exception {
		Log4J.init();
		MockStendhalRPRuleProcessor.get();

		MockStendlRPWorld.get();
		new SpouseExtension();
	}

	@Before
	public final void setup() {
		final StendhalRPZone zone = new StendhalRPZone(ZONE_NAME);
		MockStendlRPWorld.get().addRPZone(zone);

		final Player pl1 = PlayerTestHelper.createPlayer("player1");
		PlayerTestHelper.registerPlayer(pl1, zone);

		final Player pl2 = PlayerTestHelper.createPlayer("player2");
		PlayerTestHelper.registerPlayer(pl2, zone);
	}

	@After
	public final void tearDown() {
		PlayerTestHelper.removePlayer("player2", ZONE_NAME);
		PlayerTestHelper.removePlayer("player1", ZONE_NAME);
		
	}

	/**
	 * Tests for magic.
	 */
	@Test
	public final void testMagic() {
		final StendhalRPWorld world = MockStendlRPWorld.get();
		final StendhalRPZone zone = world.getZone(ZONE_NAME);

		final Player admin = PlayerTestHelper.createPlayer("admin");
		admin.setAdminLevel(400);
		PlayerTestHelper.registerPlayer(admin, zone);

		RPAction action = new RPAction();
		action.put("type", "marry");
		assertTrue(CommandCenter.execute(admin, action));
		assertEquals("Usage: #/marry #<player1> #<player2>",
				admin.events().get(0).get("text"));
		admin.clearEvents();

		action = new RPAction();
		action.put("type", "marry");
		action.put("target", "player1");
		action.put("args", "player2");
		assertTrue(CommandCenter.execute(admin, action));
		assertEquals(
				"You have successfully married \"player1\" and \"player2\".",
				admin.events().get(0).get("text"));
		admin.clearEvents();

		assertTrue(CommandCenter.execute(admin, action));
		assertEquals(
				"player1 is already married to player2. player2 is already married to player1.",
				admin.events().get(0).get("text"));
		admin.clearEvents();
	}
}
