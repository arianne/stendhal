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


public class TimePassedConditionTest {

	private static final String QUEST_SLOT = "questname";
	private static final int delay = 10;

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

		// make a brand new player. never done quest so enough 'time' has passed
		assertTrue(new TimePassedCondition(QUEST_SLOT, delay).fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("testQuestInStateCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		final Player bob = PlayerTestHelper.createPlayer("player");

		bob.setQuest(QUEST_SLOT, System.currentTimeMillis() + "");

		// no, ten minutes has not passed since right now
		assertFalse(new TimePassedCondition(QUEST_SLOT, delay).fire(
				bob,
				ConversationParser.parse("testQuestInStateCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUEST_SLOT, "spam;" + System.currentTimeMillis());

		// no, ten minutes has not passed since right now, 1st argument
		assertFalse(new TimePassedCondition(QUEST_SLOT, 1, delay).fire(
				bob,
				ConversationParser.parse("testQuestInStateCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUEST_SLOT,  1 + "");
		// yes, ten minutes has passed since really ancient ago time in unix history,
		// testing version with no argument
		assertTrue(new TimePassedCondition(QUEST_SLOT, delay).fire(
				bob,
				ConversationParser.parse("testQuestInStateCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUEST_SLOT,  "spam;" + 1);
		// yes, ten minutes has passed since really ancient ago time in unix history,
		// testing version with argument
		assertTrue(new TimePassedCondition(QUEST_SLOT, 1, delay).fire(
				bob,
				ConversationParser.parse("testQuestInStateCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));


		bob.setQuest(QUEST_SLOT,  "spam");
		// the condition expects a TIME at space '1' i.e. spam;TIME but it doesn't get one
		// so this is an 'old' quest state so yes time has passed
		assertTrue(new TimePassedCondition(QUEST_SLOT, 1, delay).fire(
				bob,
				ConversationParser.parse("testQuestInStateCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		// the condition expects a Long TIME in the quest slot but it got a string word,
		// so this is an 'old' quest state so yes time has passed
		assertTrue(new TimePassedCondition(QUEST_SLOT, delay).fire(
				bob,
				ConversationParser.parse("testQuestInStateCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUEST_SLOT,  "spam;spam");
		// the condition expects a Long TIME at space '1' i.e. spam;TIME but it doesn't get one
		// it gets a string it can't parse
		// so this is an 'old' quest state so yes time has passed
		assertTrue(new TimePassedCondition(QUEST_SLOT, 1, delay).fire(
				bob,
				ConversationParser.parse("testQuestInStateCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUEST_SLOT,  System.currentTimeMillis() + "");
		// what happens if check with 0 delay?
		// it should pass, when you think about it logically. does it?
		assertTrue(new TimePassedCondition(QUEST_SLOT, 0).fire(
				bob,
				ConversationParser.parse("testQuestInStateCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

	}

	/**
	 * Test for TimePassedCondition with an argument for where the timestamp is
	 */
	@Test
	public void testTimePassedConditionArgument() {
		new TimePassedCondition(QUEST_SLOT, 1, delay);
	}

	/**
	 * Test for TimePassedCondition with no argument for where the timestamp is
	 * (i.e. no ; in the state)
	 */
	@Test
	public void testTimePassedCondition() {
		new TimePassedCondition(QUEST_SLOT, delay);
	}

	@Test
	public void testToString() {
		assertEquals("10 minutes passed since last doing quest questname?",
				new TimePassedCondition(QUEST_SLOT, 1, delay).toString());

	}

}
