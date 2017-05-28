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
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.parser.ConversationParser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class QuestCompletedConditionTest {
	@BeforeClass
	public static void setUpClass() throws Exception {
		Log4J.init();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MockStendlRPWorld.reset();
	}

	/**
	 * Tests for fire.
	 */
	@Test
	public final void testFire() {
		assertFalse(new QuestCompletedCondition("questname").fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("testQuestCompletedCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
		final Player bob = PlayerTestHelper.createPlayer("player");

		bob.setQuest("questname", "");
		assertFalse(new QuestCompletedCondition("questname").fire(bob,
				ConversationParser.parse("testQuestCompletedCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest("questname", null);
		assertFalse(new QuestCompletedCondition("questname").fire(bob,
				ConversationParser.parse("testQuestCompletedCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest("questname", "done");
		assertTrue(new QuestCompletedCondition("questname").fire(bob,
				ConversationParser.parse("testQuestCompletedCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

	}

	/**
	 * Tests for questCompletedCondition.
	 */
	@Test
	public final void testQuestCompletedCondition() {
		new QuestCompletedCondition("questname");
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public final void testToString() {
		assertEquals("QuestCompleted <questname>", new QuestCompletedCondition(
				"questname").toString());
	}

	/**
	 * Tests for equals.
	 */
	@Test
	public void testEquals() {
		assertTrue(new QuestCompletedCondition("questname").equals(new QuestCompletedCondition(
				"questname")));

		assertFalse(new QuestCompletedCondition("questname").equals(null));

		final QuestCompletedCondition obj = new QuestCompletedCondition("questname");
		assertTrue(obj.equals(obj));

		assertFalse(new QuestCompletedCondition("questname").equals(new Object()));

		assertTrue(new QuestCompletedCondition("questname").equals(new QuestCompletedCondition(
				"questname") {
			// this is an anonymous sub class
		}));
	}

	/**
	 * Tests for hashCode.
	 */
	@Test
	public void testHashCode() {
		assertEquals(new QuestCompletedCondition("questname").hashCode(),
				new QuestCompletedCondition("questname").hashCode());
	}

}
