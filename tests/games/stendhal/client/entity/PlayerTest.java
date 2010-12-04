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
package games.stendhal.client.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.client.MockClientUI;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;

import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class PlayerTest {

	/**
	 * Initialize client UI mockup.
	 *
	 * @throws Exception
	 */
	@BeforeClass
	public static void buildWorld() throws Exception {
		new MockClientUI();
	}

	@After
	public void tearDown() throws Exception {
		MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	/**
	 * Tests for getHearingArea.
	 */
	@Test
	public final void testGetHearingArea() {
		final RPObject rpo = new RPObject();
		rpo.put("type", "player");
		rpo.put("outfit", 0);
		final User pl = new User();
		pl.initialize(rpo);

		final Rectangle2D rect = pl.getHearingArea();
		assertEquals(new Rectangle2D.Double(-20.0, -20.0, 40, 40), rect);
	}
	
	/**
	 * Tests for isBadBoy.
	 */
	@Test
	public final void testIsBadBoy() {
		Player george = new Player();
		assertFalse(george.isBadBoy());
		
		RPObject player = new RPObject();
		player.put("x", 1);
		player.put("y", 1);
		
		RPObject changes = new RPObject();
		george.onChangedAdded(player, changes);
		assertFalse(george.isBadBoy());
		
		changes.put("last_player_kill_time", 1);
		george.onChangedAdded(player, changes);
		assertTrue(george.isBadBoy());
		
	}
		
	/**
	 * Tests for amnesty.
	 */
	@Test
	public final void testAmnesty() {
		Player george = new Player();
		assertFalse(george.isBadBoy());
		
		RPObject player = new RPObject();
		player.put("x", 1);
		player.put("y", 1);
		
		RPObject changes = new RPObject();
		changes.put("last_player_kill_time", 1);
		george.onChangedAdded(player, changes);
		assertTrue(george.isBadBoy());

		george.onChangedRemoved(player, changes);
		assertFalse(george.isBadBoy());
	}
	
}
