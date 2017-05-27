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

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.parser.ConversationParser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class QuestStartedConditionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		MockStendlRPWorld.get();
	}

	/**
	 * Tests for fire.
	 */
	@Test
	public final void testFire() {
		assertFalse(new QuestStartedCondition("questname").fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
		final Player bob = PlayerTestHelper.createPlayer("player");

		bob.setQuest("questname", "done");
		assertTrue(new QuestStartedCondition("questname").fire(bob,
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest("questname", null);
		assertFalse(new QuestStartedCondition("questname").fire(bob,
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest("questname", "rejected");
		assertFalse(new QuestStartedCondition("questname").fire(bob,
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

	}

	/**
	 * Tests for questNotStartedCondition.
	 */
	@Test
	public final void testQuestNotStartedCondition() {
		new QuestStartedCondition("questname");
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public final void testToString() {
		assertEquals("QuestStarted <questname>", new QuestStartedCondition(
				"questname").toString());
	}

	/**
	 * Tests for equals.
	 */
	@Test
	public void testEquals() {
		assertFalse(new QuestStartedCondition("questname").equals(null));

		final QuestStartedCondition obj = new QuestStartedCondition("questname");
		assertTrue(obj.equals(obj));
		assertTrue(new QuestStartedCondition("questname").equals(new QuestStartedCondition("questname")));

		assertFalse(new QuestStartedCondition("questname").equals(new Object()));
	}


	/**
	 * Tests for hashcode.
	 */
	@Test
	public void testHashcode() {
		final QuestStartedCondition obj = new QuestStartedCondition("questname");
		assertTrue(obj.equals(obj));
		assertEquals(obj.hashCode(), obj.hashCode());

		assertTrue(new QuestStartedCondition("questname").equals(new QuestStartedCondition("questname")));
		assertEquals(new QuestStartedCondition("questname").hashCode(), new QuestStartedCondition("questname").hashCode());
	}
}
