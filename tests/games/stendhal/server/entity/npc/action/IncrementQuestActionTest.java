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
package games.stendhal.server.entity.npc.action;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.server.game.db.DatabaseFactory;
import utilities.PlayerTestHelper;

/**
 * Tests for IncrementQuestAction
 */
public class IncrementQuestActionTest {

	private static String questSlot = "test_slot";

	@BeforeClass
	public static void beforeClass() {
		Log4J.init();
		MockStendlRPWorld.get();
		new DatabaseFactory().initializeDatabase();
	}

	/**
	 * Test incrementing a plain number quest slot. The quest has a previous
	 * value.
	 */
	@Test
	public void testIncrement() {
		Player player = PlayerTestHelper.createPlayer("bob");
		player.setQuest(questSlot, "1");
		assertEquals("1", player.getQuest(questSlot));
		IncrementQuestAction action = new IncrementQuestAction(questSlot,1);
		action.fire(player, null, null);
		assertEquals("2", player.getQuest(questSlot));
	}

	/**
	 * Test incrementing a quest state part. The quest has a previous value.
	 */
	@Test
	public void testIncrementIndex() {
		Player player = PlayerTestHelper.createPlayer("bob");
		player.setQuest(questSlot, "test;10");
		assertEquals("test;10", player.getQuest(questSlot));
		IncrementQuestAction action = new IncrementQuestAction(questSlot,1,5);
		action.fire(player, null, null);
		assertEquals("test;15", player.getQuest(questSlot));
	}

	/**
	 * Test incrementing a quest state that has no previous value
	 */
	@Test
	public void testIncrementInitial() {
		Player player = PlayerTestHelper.createPlayer("bob");
		assertEquals(null, player.getQuest(questSlot));
		IncrementQuestAction action = new IncrementQuestAction(questSlot,1);
		action.fire(player, null, null);
		assertEquals("1", player.getQuest(questSlot));
	}

	/**
	 * Test incrementing a quest state part, when there's no previous value
	 */
	@Test
	public void testIncrementIndexInitial() {
		Player player = PlayerTestHelper.createPlayer("bob");
		assertEquals(null, player.getQuest(questSlot));
		IncrementQuestAction action = new IncrementQuestAction(questSlot, 1, 42);
		action.fire(player, null, null);
		assertEquals(";42", player.getQuest(questSlot));
	}
}
