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
package games.stendhal.server.entity.npc.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.parser.ConversationParser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;

public class LevelLessThanConditionTest {

	private Player level100Player;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void teardownAfterClass() throws Exception {

		MockStendlRPWorld.reset();
	}
	@Before
	public void setUp() throws Exception {
		level100Player = PlayerTestHelper.createPlayer("player");
		level100Player.setLevel(100);
	}

	/**
	 * Tests for hashCode.
	 */
	@Test
	public final void testHashCode() {
		assertEquals(new LevelLessThanCondition(101).hashCode(),
				new LevelLessThanCondition(101).hashCode());

	}

	/**
	 * Tests for fire.
	 */
	@Test
	public final void testFire() {
		assertFalse(new LevelLessThanCondition(99).fire(level100Player,
				ConversationParser.parse("lessthan"), null));
		assertFalse(new LevelLessThanCondition(100).fire(level100Player,
				ConversationParser.parse("lessthan"), null));
		assertTrue(new LevelLessThanCondition(101).fire(level100Player,
				ConversationParser.parse("lessthan"), null));
	}

	/**
	 * Tests for levelLessThanCondition.
	 */
	@Test
	public final void testLevelLessThanCondition() {
		new LevelLessThanCondition(0);

	}

	/**
	 * Tests for toString.
	 */
	@Test
	public final void testToString() {
		assertEquals("level < 0 ", new LevelLessThanCondition(0).toString());
	}

	/**
	 * Tests for equalsObject.
	 */
	@Test
	public final void testEqualsObject() {
		assertEquals(new LevelLessThanCondition(101),
				new LevelLessThanCondition(101));
		assertFalse((new LevelLessThanCondition(101)).equals(new LevelLessThanCondition(
				102)));
		assertFalse((new LevelLessThanCondition(102)).equals(new LevelLessThanCondition(
				101)));

	}

}
