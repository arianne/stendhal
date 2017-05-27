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
package games.stendhal.server.script;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;

public class ListRaidsTest {

	@BeforeClass
	public static void setUpBeforeClass() {
		MockStendlRPWorld.get();
	}

	@After
	public void tearDown() {
		MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	/**
	 * Tests for name.
	 */
	@Test
	public void testname() {
		ListRaids script = new ListRaids();
		Player player = PlayerTestHelper.createPlayer("george");
		script.execute(player, null);
		assertThat(player.events().get(0).toString(), containsString("ZombieRaid"));
	}
}
