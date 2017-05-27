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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.parser.ConversationParser;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Log4J;
import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class QuestActiveConditionTest {
	@BeforeClass
	public static void setUpClass() throws Exception {
		Log4J.init();
	}

	/**
	 * Tests for fire.
	 */
	@Test
	public final void testFire() {
		assertThat(new QuestActiveCondition("questname").fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("QuestActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()),
				is(false));
		final Player bob = PlayerTestHelper.createPlayer("player");

		bob.setQuest("questname", "");
		assertThat(new QuestActiveCondition("questname").fire(bob,
				ConversationParser.parse("QuestActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()),
				is(true));

		bob.setQuest("questname", null);
		assertThat(new QuestActiveCondition("questname").fire(bob,
				ConversationParser.parse("QuestActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()),
				is(false));

		bob.setQuest("questname", "done");
		assertThat(new QuestActiveCondition("questname").fire(bob,
				ConversationParser.parse("QuestActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()),
				is(false));

		bob.setQuest("questname", "rejected");
		assertThat(new QuestActiveCondition("questname").fire(bob,
				ConversationParser.parse("QuestActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()),
				is(false));

	}

	/**
	 * Tests for questActiveCondition.
	 */
	@Test
	public final void testQuestActiveCondition() {
		new QuestActiveCondition("questname");
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public final void testToString() {
		assertThat(new QuestActiveCondition("questname").toString(), is("QuestActive <questname>"));
	}

	/**
	 * Tests for equals.
	 */
	@Test
	public void testEquals() {
		assertThat(new QuestActiveCondition("questname"), not(equalTo(null)));

		final QuestActiveCondition obj = new QuestActiveCondition("questname");
		assertThat(obj, equalTo(obj));
		assertThat(new QuestActiveCondition("questname"),
				equalTo(new QuestActiveCondition("questname")));

		assertThat(new QuestActiveCondition("questname"),
				not(equalTo(new Object())));

		assertThat(new QuestActiveCondition("questname"),
				not(equalTo(new QuestActiveCondition(
				"questname2"))));

		assertThat(new QuestActiveCondition("questname"),
				equalTo((QuestActiveCondition) new QuestActiveCondition("questname") {
					//sub classing
			}));
	}

	/**
	 * Tests for hashCode.
	 */
	@Test
	public void testHashCode() {
		final QuestActiveCondition obj = new QuestActiveCondition("questname");
		assertThat(obj.hashCode(), equalTo(obj.hashCode()));
		assertThat(new QuestActiveCondition("questname").hashCode(),
				equalTo(new QuestActiveCondition("questname").hashCode()));
	}

}
