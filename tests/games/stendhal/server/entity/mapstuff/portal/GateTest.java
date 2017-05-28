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
package games.stendhal.server.entity.mapstuff.portal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.npc.condition.AlwaysFalseCondition;
import games.stendhal.server.entity.npc.condition.AlwaysTrueCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import utilities.PlayerTestHelper;

/**
 * Tests for Gate
 */
public class GateTest {
	@BeforeClass
	public static void setupBeforeClass() {
		MockStendlRPWorld.get();
		if (!RPClass.hasRPClass("gate")) {
			Gate.generateGateRPClass();
		}

	}

	/**
	 * Tests for openCloseGate.
	 */
	@Test
	public void testOpenCloseGate() {
		final Gate gate = new Gate();
		gate.open();
		assertTrue(gate.isOpen());
		gate.close();
		assertFalse(gate.isOpen());
	}

	/**
	 * Tests that closing fails if there's something on the way.
	 */
	@Test
	public void testCloseGateBlocked() {
		final Gate gate = new Gate();
		gate.open();
		StendhalRPZone zone = new StendhalRPZone("room", 5, 5);
		gate.setPosition(3, 3);
		zone.add(gate);
		assertTrue("Sanity check", gate.isOpen());
		final Creature creature = SingletonRepository.getEntityManager().getCreature("rat");
		creature.setPosition(3, 3);
		zone.add(creature);
		System.err.println("RESISTANCE: " + creature.getResistance());
		gate.close();
		assertTrue("Rat in the way", gate.isOpen());
		// A "ghostmode" rat
		creature.setResistance(0);
		gate.close();
		assertFalse("Ghost in the way", gate.isOpen());
	}

	/**
	 * Tests for closeOpenGate.
	 */
	@Test
	public void testCloseOpenGate() {
		final Gate gate = new Gate();
		gate.close();
		assertFalse(gate.isOpen());
		gate.open();
		assertTrue(gate.isOpen());
	}

	/**
	 * Tests for useGateNotNextTo.
	 */
	@Test
	public void testUseGateNotNextTo() {
		final Gate gate = new Gate();
		gate.setPosition(5, 5);
		assertFalse(gate.isOpen());
		final RPEntity user = new RPEntity() {

			@Override
			protected void dropItemsOn(final Corpse corpse) {

			}

			@Override
			public void logic() {

			}
		};
		assertFalse(gate.nextTo(user));
		assertFalse(gate.isOpen());
		gate.onUsed(user);
		assertFalse(gate.isOpen());
		gate.open();
		gate.onUsed(user);
		assertTrue(gate.isOpen());
	}

	/**
	 * Tests for useGateNextTo.
	 */
	@Test
	public void testUseGateNextTo() {
		final Gate gate = new Gate();

		final Player user = new Player(new RPObject()) {

			@Override
			protected void dropItemsOn(final Corpse corpse) {

			}

			@Override
			public void logic() {

			}
		};
		assertTrue(gate.nextTo(user));

		assertFalse(gate.isOpen());
		gate.onUsed(user);
		assertTrue(gate.isOpen());

		gate.open();
		gate.onUsed(user);
		assertFalse(gate.isOpen());
	}

	/**
	 * Tests for isObstacle.
	 */
	@Test
	public void testIsObstacle() {
		final Gate gate = new Gate();

		final RPEntity user = new RPEntity() {

			@Override
			protected void dropItemsOn(final Corpse corpse) {

			}

			@Override
			public void logic() {

			}
		};
		assertFalse(gate.isOpen());
		assertTrue(gate.isObstacle(user));

		gate.open();
		assertTrue(gate.isOpen());
		assertFalse(gate.isObstacle(user));
	}

	/**
	 * Test player opening and closing a gate he's allowed to use.
	 */
	@Test
	public void testOpenCloseAllowed() {
		final Gate gate = new Gate("v", "image", new AlwaysTrueCondition());
		Player user = PlayerTestHelper.createPlayer("Gate keeper");
		assertFalse(gate.isOpen());
		assertTrue(gate.onUsed(user));
		assertTrue(gate.isOpen());
		assertTrue(gate.onUsed(user));
		assertFalse(gate.isOpen());
	}

	/**
	 * Test player opening and closing a gate he's not allowed to use.
	 */
	@Test
	public void testOpenCloseDenied() {
		final Gate gate = new Gate("v", "image", new AlwaysFalseCondition());
		Player user = PlayerTestHelper.createPlayer("Gate keeper");
		assertFalse(gate.isOpen());
		assertFalse(gate.onUsed(user));
		assertEquals(null, PlayerTestHelper.getPrivateReply(user));
		assertFalse(gate.isOpen());
		gate.open();
		assertTrue(gate.isOpen());
		assertFalse(gate.onUsed(user));
		assertTrue(gate.isOpen());
		assertEquals(null, PlayerTestHelper.getPrivateReply(user));

		// Repeat the same sequence, but with a deny message.
		gate.close();
		gate.setRefuseMessage("lorem ipsum");
		assertFalse(gate.isOpen());
		assertFalse(gate.onUsed(user));
		assertEquals("lorem ipsum", PlayerTestHelper.getPrivateReply(user));
		assertFalse(gate.isOpen());
		gate.open();
		assertTrue(gate.isOpen());
		assertFalse(gate.onUsed(user));
		assertTrue(gate.isOpen());
		assertEquals("lorem ipsum", PlayerTestHelper.getPrivateReply(user));
	}
}
