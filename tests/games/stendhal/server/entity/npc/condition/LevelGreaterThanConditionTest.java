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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.parser.ConversationParser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import utilities.PlayerTestHelper;

public class LevelGreaterThanConditionTest {
	private Player level100Player;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		MockStendlRPWorld.get();

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
		assertEquals(new LevelGreaterThanCondition(101).hashCode(),
				new LevelGreaterThanCondition(101).hashCode());

	}

	/**
	 * Tests for fire.
	 */
	@Test
	public final void testFire() {
		assertTrue(new LevelGreaterThanCondition(99).fire(level100Player,
				ConversationParser.parse("greaterthan"), null));
		assertFalse(new LevelGreaterThanCondition(100).fire(level100Player,
				ConversationParser.parse("greaterthan"), null));
		assertFalse(new LevelGreaterThanCondition(101).fire(level100Player,
				ConversationParser.parse("greaterthan"), null));
	}

	/**
	 * Tests for levelGreaterThanCondition.
	 */
	@Test
	public final void testLevelGreaterThanCondition() {
		new LevelGreaterThanCondition(0);

	}

	/**
	 * Tests for toString.
	 */
	@Test
	public final void testToString() {
		assertEquals("level > 0 ", new LevelGreaterThanCondition(0).toString());
	}

	/**
	 * Tests for equalsObject.
	 */
	@Test
	public final void testEqualsObject() {
		assertEquals(new LevelGreaterThanCondition(101),
				new LevelGreaterThanCondition(101));
		assertFalse((new LevelGreaterThanCondition(101)).equals(new LevelGreaterThanCondition(
				102)));
		assertFalse((new LevelGreaterThanCondition(102)).equals(new LevelGreaterThanCondition(
				101)));
	}

}
