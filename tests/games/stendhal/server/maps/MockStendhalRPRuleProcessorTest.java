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
package games.stendhal.server.maps;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;

public class MockStendhalRPRuleProcessorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PlayerTestHelper.removeAllPlayers();
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void teardownAfterClass() throws Exception {

		MockStendlRPWorld.reset();
	}
	/**
	 * Tests for getTurn.
	 */
	@Test
	public void testGetTurn() {
		assertThat(MockStendhalRPRuleProcessor.get().getTurn(), is(0));
	}

	/**
	 * Tests for get.
	 */
	@Test
	public void testGet() {
		assertSame(MockStendhalRPRuleProcessor.get(),
				MockStendhalRPRuleProcessor.get());
	}

	/**
	 * Tests for addPlayer.
	 */
	@Test
	public void testAddPlayer() {
		final MockStendhalRPRuleProcessor processor = MockStendhalRPRuleProcessor.get();
		assertThat(StendhalRPRuleProcessor.getAmountOfOnlinePlayers(), is(0));

		final Player bob = PlayerTestHelper.createPlayer("bob");
		processor.addPlayer(bob);
		assertThat(StendhalRPRuleProcessor.getAmountOfOnlinePlayers(), is(1));
		assertSame(bob, processor.getPlayer("bob"));
		final Player bob2 = PlayerTestHelper.createPlayer("bob");
		processor.addPlayer(bob2);
		assertThat(StendhalRPRuleProcessor.getAmountOfOnlinePlayers(), is(1));
		assertSame(bob2, processor.getPlayer("bob"));
		assertNotSame(bob, processor.getPlayer("bob"));
	}

}
