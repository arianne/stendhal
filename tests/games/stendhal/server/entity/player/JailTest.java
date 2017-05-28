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

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import utilities.PlayerTestHelper;
import utilities.RPClass.ArrestWarrentTestHelper;

public class JailTest {
	@BeforeClass
	public static void setUpClass() {
		Log4J.init();
		ArrestWarrentTestHelper.generateRPClasses();
		MockStendhalRPRuleProcessor.get().clearPlayers();
		StendhalRPZone jailZone = new StendhalRPZone("test_jail", 100, 100);
		MockStendlRPWorld.get().addRPZone(jailZone);
		new Jail().configureZone(jailZone, null);
		MockStendlRPWorld.get().addRPZone(new StendhalRPZone("-3_semos_jail", 100, 100));
	}

	@After
	public void tearDown() {
		// release bob from jail in case he is still imprisoned
		SingletonRepository.getJail().release("bob");

		//TODO remove arrest warrant in any case - also if bob was not online when arresting him

		MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	/**
	 * Tests for criminalNotInworld.
	 */
	@Test
	public final void testCriminalNotInworld() {
		final Player policeman = PlayerTestHelper.createPlayer("police officer");
		PlayerTestHelper.createPlayer("bob");
		SingletonRepository.getJail().imprison("bob", policeman, 1, "test");
		assertEquals("You have jailed bob for 1 minute. Reason: test.", policeman.events().get(0).get("text"));

		assertEquals("Player bob is not online, but the arrest warrant has been recorded anyway.", policeman.events().get(1).get("text"));

	}

	/**
	 * Tests for criminalimprison.
	 */
	@Test
	public final void testCriminalimprison() {
		final Player policeman = PlayerTestHelper.createPlayer("police officer");
		final Player bob = PlayerTestHelper.createPlayer("bob");
		MockStendhalRPRuleProcessor.get().addPlayer(bob);
		PlayerTestHelper.registerPlayer(bob, "-3_semos_jail");

		SingletonRepository.getJail().imprison(bob.getName(), policeman, 1, "test");
		assertTrue(Jail.isInJail(bob));
		assertEquals("You have jailed bob for 1 minute. Reason: test.",
				policeman.events().get(0).get("text"));
		SingletonRepository.getJail().release(bob);
		assertFalse(Jail.isInJail(bob));
	}


	/**
	 * Tests for repeatedJailing.
	 */
	@Test
	public final void testrepeatedJailing() {

		final Player bob = PlayerTestHelper.createPlayer("bob");
		final StendhalRPZone zone = new StendhalRPZone("knast", 100, 100);
				Jail.jailzone = zone;
		MockStendhalRPRuleProcessor.get().addPlayer(bob);
		SingletonRepository.getJail().release("bob");

		SingletonRepository.getJail().imprison("bob", bob, 1, "test");
			assertTrue(Jail.isInJail(bob));

		assertEquals("bob: 1 Minutes because: test\n", SingletonRepository.getJail().listJailed());
		SingletonRepository.getJail().imprison("bob", bob, 1, "test2");
		assertEquals("bob: 1 Minutes because: test2\n", SingletonRepository.getJail().listJailed());

	}
	/**
	 * Tests for isInJail.
	 */
	@Test
	public final void testIsInJail() {
		StendhalRPZone jail = new StendhalRPZone("testknast");
		jail.collisionMap.init(64, 64);
		Jail jailcnf = new Jail();
		jailcnf.configureZone(jail, null);


		final Player bob = PlayerTestHelper.createPlayer("bob");
		jail.add(bob);

		assertFalse(Jail.isInJail(bob));
		bob.setPosition(1, 1);

		assertTrue(Jail.isInJail(bob));
	}
}
