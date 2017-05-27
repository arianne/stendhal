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
package games.stendhal.server.entity.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;

public class GagManagerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		MockStendhalRPRuleProcessor.get().clearPlayers();
		PlayerTestHelper.generatePlayerRPClasses();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	  MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	/**
	 * Tests for gagAbsentPlayer.
	 */
	@Test
	public final void testGagAbsentPlayer() {
		final Player policeman = PlayerTestHelper.createPlayer("player");
		final Player bob = PlayerTestHelper.createPlayer("bob");
		SingletonRepository.getGagManager().gag("bob", policeman, 1, "test");
		assertEquals("Player bob not found", policeman.events().get(0).get("text"));
		assertFalse(GagManager.isGagged(bob));
	}

	/**
	 * Tests for gagPlayer.
	 */
	@Test
	public final void testGagPlayer() {
		final Player policeman = PlayerTestHelper.createPlayer("player");
		final Player bob = PlayerTestHelper.createPlayer("bob");
		SingletonRepository.getGagManager().gag(bob, policeman, 1, "test", bob.getName());
		assertEquals("You have gagged bob for 1 minutes. Reason: test.",
				policeman.events().get(0).get("text"));
		assertTrue(GagManager.isGagged(bob));
		SingletonRepository.getGagManager().release(bob);
		assertFalse(GagManager.isGagged(bob));
	}

	/**
	 * Tests for negativ.
	 */
	@Test
	public final void testnegativ() {
		final Player policeman = PlayerTestHelper.createPlayer("player");
		final Player bob = PlayerTestHelper.createPlayer("bob");
		assertTrue(policeman.events().isEmpty());
		SingletonRepository.getGagManager().gag(bob, policeman, -1, "test", bob.getName());
		assertEquals("Infinity (negative numbers) is not supported.", policeman
				.events().get(0).get("text"));
		assertFalse(GagManager.isGagged(bob));
	}

	/**
	 * Tests for onLoggedIn.
	 */
	@Test
	public final void testOnLoggedIn() {
		final Player policeman = PlayerTestHelper.createPlayer("player");
		final Player bob = PlayerTestHelper.createPlayer("bob");

		SingletonRepository.getGagManager().gag(bob, policeman, 1, "test", bob.getName());
		assertEquals("You have gagged bob for 1 minutes. Reason: test.",
				policeman.events().get(0).get("text"));
		assertTrue(GagManager.isGagged(bob));
		SingletonRepository.getGagManager().onLoggedIn(bob);
		assertTrue(GagManager.isGagged(bob));
		bob.setQuest("gag", "0");
		SingletonRepository.getGagManager().onLoggedIn(bob);
		assertFalse(GagManager.isGagged(bob));
	}

	/**
	 * Tests for onLoggedInAfterExpiry.
	 */
	@Test
	public final void testOnLoggedInAfterExpiry() {
		final Player bob = PlayerTestHelper.createPlayer("bob");

		bob.setQuest("gag", "" + (System.currentTimeMillis() - 5));
		assertTrue(GagManager.isGagged(bob));
		SingletonRepository.getGagManager().onLoggedIn(bob);
		assertFalse(GagManager.isGagged(bob));
	}

	/**
	 * Tests for getTimeremaining.
	 */
	@Test
	public final void testgetTimeremaining() {
		final Player bob = PlayerTestHelper.createPlayer("player");
		assertEquals(0L, SingletonRepository.getGagManager().getTimeRemaining(bob));
		bob.setQuest("gag", "" + (System.currentTimeMillis() - 1000));
		assertTrue(SingletonRepository.getGagManager().getTimeRemaining(bob) <= -1000);
	}
}
