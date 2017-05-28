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
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class QuestInStateConditionTest {
	private static final String QUESTNAME = "questname";

	@BeforeClass
	public static void setUpClass() throws Exception {
		Log4J.init();
		MockStendlRPWorld.get();
		MockStendhalRPRuleProcessor.get();
	}

	/**
	 * Tests for fire.
	 */
	@Test
	public final void testFire() {
		final String validState = "valid";
		assertFalse(new QuestInStateCondition(QUESTNAME, validState).fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("testQuestInStateCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
		final Player bob = PlayerTestHelper.createPlayer("player");

		bob.setQuest(QUESTNAME, "valid");
		assertTrue(new QuestInStateCondition(QUESTNAME, validState).fire(bob,
				ConversationParser.parse("testQuestInStateCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, "");
		assertFalse(new QuestInStateCondition(QUESTNAME, validState).fire(bob,
				ConversationParser.parse("testQuestInStateCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, null);
		assertFalse(new QuestInStateCondition(QUESTNAME, validState).fire(bob,
				ConversationParser.parse("testQuestInStateCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, "done");
		assertFalse(new QuestInStateCondition(QUESTNAME, validState).fire(bob,
				ConversationParser.parse("testQuestInStateCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

	}

	/**
	 * Tests for questInStateCondition.
	 */
	@Test
	public final void testQuestInStateCondition() {
		new QuestInStateCondition(QUESTNAME, "");
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public final void testToString() {
		assertEquals("QuestInState <questname[-1] = testToString>",
				new QuestInStateCondition(QUESTNAME, "testToString").toString());
	}

	/**
	 * Tests for equals.
	 */
	@Test
	public void testEquals() {
		final String state = "state";
		assertFalse(new QuestInStateCondition(QUESTNAME, state).equals(null));

		final QuestInStateCondition obj = new QuestInStateCondition(QUESTNAME, state);
		assertTrue(obj.equals(obj));

		assertTrue(new QuestInStateCondition(QUESTNAME, state).equals(new QuestInStateCondition(
				QUESTNAME, state)));

		assertFalse(new QuestInStateCondition(QUESTNAME, state).equals(new Object()));

		assertFalse(new QuestInStateCondition(QUESTNAME, state).equals(new QuestInStateCondition(
				QUESTNAME, state + "2")));

		assertTrue(new QuestInStateCondition(QUESTNAME, state).equals(new QuestInStateCondition(
				QUESTNAME, state) {
			// this is an anonymous sub class
		}));
	}

	/**
	 * Tests for hashCode.
	 */
	@Test
	public void testHashCode() {
		final QuestInStateCondition obj = new QuestInStateCondition(QUESTNAME, "state");
		assertEquals(obj.hashCode(), obj.hashCode());

		assertEquals(
				new QuestInStateCondition("questname", "state").hashCode(),
				new QuestInStateCondition("questname", "state").hashCode());
	}

}
