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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.RaidCreature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import utilities.PlayerTestHelper;

public class SummonActionTest {

	private StendhalRPZone zone;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		SummonAction.register();
		MockStendlRPWorld.get();
		MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	@Before
	public void setUP() {
		zone = new StendhalRPZone("testzone") {
			@Override
			public synchronized boolean collides(final Entity entity,
					final double x, final double y) {

				return false;
			}
		};
	}

	@After
	public void teardown() {
		MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	/**
	 * Tests for summonRat.
	 */
	@Test
	public final void testSummonRat() {
		final Player pl = PlayerTestHelper.createPlayer("hugo");

		MockStendhalRPRuleProcessor.get().addPlayer(pl);

		zone.add(pl);
		pl.setPosition(1, 1);
		pl.put("adminlevel", 5000);
		final RPAction action = new RPAction();
		action.put("type", "summon");
		action.put("creature", "rat");
		action.put("x", 0);
		action.put("y", 0);
		CommandCenter.execute(pl, action);
		assertEquals(1, pl.getID().getObjectID());
		final Creature rat = (Creature) zone.getEntityAt(0, 0);
		assertEquals("rat", rat.get("subclass"));
		assertTrue("RaidCreature", rat instanceof RaidCreature);
	}

	/**
	 * Tests for summonDagger.
	 */
	@Test
	public final void testSummonDagger() {

		final Player pl = PlayerTestHelper.createPlayer("hugo");

		MockStendhalRPRuleProcessor.get().addPlayer(pl);

		zone.add(pl);
		pl.setPosition(1, 1);
		pl.put("adminlevel", 5000);
		final RPAction action = new RPAction();
		action.put("type", "summon");
		action.put("creature", "dagger");
		action.put("x", 0);
		action.put("y", 0);
		CommandCenter.execute(pl, action);
		assertEquals(1, pl.getID().getObjectID());
		final Item item = (Item) zone.getEntityAt(0, 0);
		assertEquals("dagger", item.get("subclass"));
	}

	/**
	 * Tests for summonUnKnown.
	 */
	@Test
	public final void testSummonUnKnown() {
		final Player pl = PlayerTestHelper.createPlayer("hugo");

		MockStendhalRPRuleProcessor.get().addPlayer(pl);

		zone.add(pl);
		pl.setPosition(1, 1);
		pl.put("adminlevel", 5000);
		final RPAction action = new RPAction();
		action.put("type", "summon");
		action.put("creature", "unknown");
		action.put("x", 0);
		action.put("y", 0);
		CommandCenter.execute(pl, action);
		assertEquals(1, pl.getID().getObjectID());
		assertNull(zone.getEntityAt(0, 0));
	}

	@Test
	public final void testSummonFishingRod() {
		final Player pl = PlayerTestHelper.createPlayer("hugo");

		MockStendhalRPRuleProcessor.get().addPlayer(pl);

		zone.add(pl);
		pl.setPosition(1, 1);
		pl.put("adminlevel", 5000);
		final RPAction action = new RPAction();
		action.put("type", "summon");
		action.put("creature", "fishing rod");
		action.put("x", 0);
		action.put("y", 0);
		CommandCenter.execute(pl, action);
		assertEquals(1, pl.getID().getObjectID());
		final Item item = (Item) zone.getEntityAt(0, 0);
		assertEquals("fishing_rod", item.get("subclass"));
	}

	/**
	 * Tests for avoidNFE.
	 */
	@Test
	public final void testAvoidNFE() {
		final Player pl = PlayerTestHelper.createPlayer("hugo");

		MockStendhalRPRuleProcessor.get().addPlayer(pl);

		zone.add(pl);
		pl.setPosition(1, 1);
		pl.put("adminlevel", 5000);
		final RPAction action = new RPAction();
		action.put("type", "summon");
		action.put("creature", "unknown");
		action.put("x", "bag");
		action.put("y", "perch");
		CommandCenter.execute(pl, action);
		assertEquals(1, pl.getID().getObjectID());
		assertNull(zone.getEntityAt(0, 0));
	}
}
