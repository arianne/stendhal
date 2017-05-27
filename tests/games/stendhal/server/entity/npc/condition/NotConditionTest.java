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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class NotConditionTest {

	private static final class AlwaysFalseCondition implements ChatCondition {
		@Override
		public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
			return false;
		}

		@Override
		public String toString() {
			return "false";
		}
	}

	private AlwaysTrueCondition trueCondition;

	private ChatCondition falsecondition;

	@BeforeClass
	public static void setupClass() {
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownClass() {
		MockStendlRPWorld.get();
	}

	@Before
	public void setUp() throws Exception {
		trueCondition = new AlwaysTrueCondition();
		falsecondition = new AlwaysFalseCondition();
	}

	@Test
	public void selftest() throws Exception {
		assertTrue("true",
				trueCondition.fire(PlayerTestHelper.createPlayer("player"),
						ConversationParser.parse("testNotConditionText"),
						SpeakerNPCTestHelper.createSpeakerNPC()));
		assertFalse("false", falsecondition.fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("testNotConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

	}

	/**
	 * Tests for hashCode.
	 */
	@Test
	public final void testHashCode() {
		final NotCondition obj = new NotCondition(trueCondition);
		assertEquals(obj.hashCode(), obj.hashCode());
		assertEquals(new NotCondition(trueCondition).hashCode(),
				new NotCondition(trueCondition).hashCode());

	}

	/**
	 * Tests for fire.
	 */
	@Test
	public final void testFire() {
		assertFalse(new NotCondition(trueCondition).fire(
				PlayerTestHelper.createPlayer("player"), ConversationParser.parse("notconditiontest"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
		assertTrue(new NotCondition(falsecondition).fire(
				PlayerTestHelper.createPlayer("player"), ConversationParser.parse("notconditiontest"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
	}

	/**
	 * Tests for notCondition.
	 */
	@Test
	public final void testNotCondition() {
		new NotCondition(trueCondition);
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public final void testToString() {
		assertEquals("NOT <true>", new NotCondition(trueCondition).toString());
		assertEquals("NOT <false>", new NotCondition(falsecondition).toString());
	}

	/**
	 * Tests for equals.
	 */
	@Test
	public void testEquals() {

		assertFalse(new NotCondition(trueCondition).equals(null));

		final NotCondition obj = new NotCondition(trueCondition);
		assertTrue(obj.equals(obj));
		assertTrue(new NotCondition(trueCondition).equals(new NotCondition(
				trueCondition)));
		assertFalse(new NotCondition(trueCondition).equals(Integer.valueOf(100)));
		assertTrue(new NotCondition(trueCondition).equals(new NotCondition(
				trueCondition) {
			// this is an anonymous sub class
		}));
	}

}
