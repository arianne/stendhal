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
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class QuestNotActiveConditionTest {
	@BeforeClass
	public static void setUpClass() throws Exception {
		MockStendlRPWorld.get();
		Log4J.init();
	}

	/**
	 * Tests for fire.
	 */
	@Test
	public final void testFire() {
		assertThat(new QuestNotActiveCondition("questname").fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("QuestNotActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()),
				is(true));
		final Player bob = PlayerTestHelper.createPlayer("player");

		bob.setQuest("questname", "");
		assertThat(new QuestNotActiveCondition("questname").fire(bob,
				ConversationParser.parse("QuestNotActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()),
				is(false));

		bob.setQuest("questname", null);
		assertThat(new QuestNotActiveCondition("questname").fire(bob,
				ConversationParser.parse("QuestNotActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()),
				is(true));

		bob.setQuest("questname", "done");
		assertThat(new QuestNotActiveCondition("questname").fire(bob,
				ConversationParser.parse("QuestNotActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()),
				is(true));

		bob.setQuest("questname", "rejected");
		assertThat(new QuestNotActiveCondition("questname").fire(bob,
				ConversationParser.parse("QuestNotActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()),
				is(true));

	}

	/**
	 * Tests for questNotActiveCondition.
	 */
	@Test
	public final void testQuestNotActiveCondition() {
		new QuestNotActiveCondition("questname");
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public final void testToString() {
		assertThat(new QuestNotActiveCondition("questname").toString(), is("QuestNotActive <questname>"));
	}

	/**
	 * Tests for equals.
	 */
	@Test
	public void testEquals() {
		assertThat(new QuestNotActiveCondition("questname"), not(equalTo(null)));

		final QuestNotActiveCondition obj = new QuestNotActiveCondition("questname");
		assertThat(obj, equalTo(obj));
		assertThat(new QuestNotActiveCondition("questname"),
				equalTo(new QuestNotActiveCondition("questname")));

		assertThat(new QuestNotActiveCondition("questname"),
				not(equalTo(new Object())));

		assertThat(new QuestNotActiveCondition("questname"),
				not(equalTo(new QuestNotActiveCondition(
				"questname2"))));

		assertThat(new QuestNotActiveCondition("questname"),
				equalTo((QuestNotActiveCondition) new QuestNotActiveCondition("questname") {
					//sub classing
			}));
	}

	/**
	 * Tests for hashCode.
	 */
	@Test
	public void testHashCode() {
		final QuestNotActiveCondition obj = new QuestNotActiveCondition("questname");
		assertThat(obj.hashCode(), equalTo(obj.hashCode()));
		assertThat(new QuestNotActiveCondition("questname").hashCode(),
				equalTo(new QuestNotActiveCondition("questname").hashCode()));

	}

}
