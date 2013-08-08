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
package games.stendhal.server.entity.status;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.status.PoisonAttacker;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class PoisonAttackerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for poisoner.
	 */
	@Test
	public void testPoisoner() {
		final String poisontype = "greater poison";
		final ConsumableItem poison = (ConsumableItem) SingletonRepository.getEntityManager().getItem(poisontype);

		final PoisonAttacker poisoner = new PoisonAttacker(100, poison);
		final Player victim = PlayerTestHelper.createPlayer("bob");
		poisoner.attack(victim);
		assertTrue(victim.isPoisoned());
	}

	/**
	 * Tests for poisonerProbabilityZero.
	 */
	@Test
	public void testPoisonerProbabilityZero() {
		final String poisontype = "greater poison";
		final ConsumableItem poison = (ConsumableItem) SingletonRepository.getEntityManager().getItem(poisontype);

		final PoisonAttacker poisoner = new PoisonAttacker(0, poison);
		final Player victim = PlayerTestHelper.createPlayer("bob");
		poisoner.attack(victim);
		assertFalse(victim.isPoisoned());
	}
}
