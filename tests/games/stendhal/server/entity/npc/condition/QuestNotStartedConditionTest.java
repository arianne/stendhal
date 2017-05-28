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

import org.junit.Test;

import games.stendhal.common.parser.ConversationParser;
import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class QuestNotStartedConditionTest {

	/**
	 * Tests for fire.
	 */
	@Test
	public final void testFire() {
		assertTrue(new QuestNotStartedCondition("questname").fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
		final Player bob = PlayerTestHelper.createPlayer("player");

		bob.setQuest("questname", "");
		assertFalse(new QuestNotStartedCondition("questname").fire(bob,
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest("questname", null);
		assertTrue(new QuestNotStartedCondition("questname").fire(bob,
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest("questname", "rejected");
		assertTrue(new QuestNotStartedCondition("questname").fire(bob,
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
	}

	/**
	 * Tests for questNotStartedCondition.
	 */
	@Test
	public final void testQuestNotStartedCondition() {
		new QuestNotStartedCondition("questname");
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public final void testToString() {
		assertEquals("QuestNotStarted <questname>",
				new QuestNotStartedCondition("questname").toString());
	}

	/**
	 * Tests for equals.
	 */
	@Test
	public void testEquals() {
		assertFalse(new QuestNotStartedCondition("questname").equals(null));

		final QuestNotStartedCondition obj = new QuestNotStartedCondition("questname");
		assertTrue(obj.equals(obj));

		assertTrue(new QuestNotStartedCondition("questname").equals(new QuestNotStartedCondition(
				"questname")));

		assertFalse(new QuestNotStartedCondition("questname").equals(new Object()));

		assertTrue(new QuestNotStartedCondition("questname").equals(new QuestNotStartedCondition(
				"questname") {
			// this is an anonymous sub class
		}));

	}

	/**
	 * Tests for hashCode.
	 */
	@Test
	public void testHashCode() {
		final QuestNotStartedCondition obj = new QuestNotStartedCondition("questname");
		assertTrue(obj.equals(obj));
		assertEquals(obj.hashCode(), obj.hashCode());
		assertTrue(new QuestNotStartedCondition("questname").equals(new QuestNotStartedCondition(
				"questname")));
		assertEquals(new QuestNotStartedCondition("questname").hashCode(),
				new QuestNotStartedCondition("questname").hashCode());
	}
}
