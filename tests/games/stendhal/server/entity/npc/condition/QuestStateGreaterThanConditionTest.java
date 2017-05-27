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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;

/**
 * Tests for the {@link QuestStateGreaterThanCondition}
 *
 * @author madmetzger
 */
public class QuestStateGreaterThanConditionTest {

	private static Player player;

	@BeforeClass
	public static void setUpBeforeClass() {
		player = PlayerTestHelper.createPlayer("testplayer");
		player.setQuest("testquest", "done;5");
	}

	@Test
	public void testNotOftenEnoughFinished() {
		QuestStateGreaterThanCondition c = new QuestStateGreaterThanCondition("testquest", 1, 10);
		assertFalse(c.fire(player, null, null));
	}

	@Test
	public void testExactlyMatched() {
		QuestStateGreaterThanCondition c = new QuestStateGreaterThanCondition("testquest", 1, 5);
		assertFalse(c.fire(player, null, null));
	}

	@Test
	public void testMoreThanNeeded() {
		QuestStateGreaterThanCondition c = new QuestStateGreaterThanCondition("testquest", 1, 3);
		assertTrue(c.fire(player, null, null));
	}

}
