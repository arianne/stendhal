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

public class QuestSmallerThanConditionTest {
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
		final Player bob = PlayerTestHelper.createPlayer("player");
		final int value = 2009;

		assertFalse(new QuestSmallerThanCondition(QUESTNAME, value).fire(
				bob,
				ConversationParser.parse("testQuestSmallerThanCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, "invalid");
		assertFalse(new QuestSmallerThanCondition(QUESTNAME, value).fire(bob,
				ConversationParser.parse("testQuestSmallerThanCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, "");
		assertFalse(new QuestSmallerThanCondition(QUESTNAME, value).fire(bob,
				ConversationParser.parse("testQuestSmallerThanCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, "2010");
		assertFalse(new QuestSmallerThanCondition(QUESTNAME, value).fire(bob,
				ConversationParser.parse("testQuestSmallerThanCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, "2009");
		assertFalse(new QuestSmallerThanCondition(QUESTNAME, value).fire(bob,
				ConversationParser.parse("testQuestSmallerThanCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, "2008");
		assertTrue(new QuestSmallerThanCondition(QUESTNAME, value).fire(bob,
				ConversationParser.parse("testQuestSmallerThanCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

	}

	/**
	 * Tests for toString.
	 */
	@Test
	public final void testToString() {
		assertEquals("QuestSmallerThan <questname[-1] = 2009>",
				new QuestSmallerThanCondition(QUESTNAME, 2009).toString());
	}

	/**
	 * Tests for equals.
	 */
	@Test
	public void testEquals() {
		final int value = 2009;
		assertFalse(new QuestSmallerThanCondition(QUESTNAME, value).equals(null));

		final QuestSmallerThanCondition obj = new QuestSmallerThanCondition(QUESTNAME, value);
		assertTrue(obj.equals(obj));

		assertTrue(new QuestSmallerThanCondition(QUESTNAME, value).equals(new QuestSmallerThanCondition(QUESTNAME, value)));

		assertFalse(new QuestSmallerThanCondition(QUESTNAME, value).equals(new Object()));

		assertFalse(new QuestSmallerThanCondition(QUESTNAME, 2008).equals(new QuestSmallerThanCondition(QUESTNAME, value)));
		assertFalse(new QuestSmallerThanCondition(QUESTNAME, value).equals(new QuestSmallerThanCondition(QUESTNAME, value + 2)));

		assertTrue(new QuestSmallerThanCondition(QUESTNAME, value).equals(new QuestSmallerThanCondition(QUESTNAME, value) {
			// this is an anonymous sub class
		}));
	}

	/**
	 * Tests for hashCode.
	 */
	@Test
	public void testHashCode() {

		final QuestSmallerThanCondition obj = new QuestSmallerThanCondition(QUESTNAME, 2009);
		assertEquals(obj.hashCode(), obj.hashCode());

		assertEquals(
				new QuestSmallerThanCondition("questname", 2009).hashCode(),
				new QuestSmallerThanCondition("questname", 2009).hashCode());
	}

}
