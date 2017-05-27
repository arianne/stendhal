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
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class EmoteConditionTest extends PlayerTestHelper {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	/**
	 * Tests for equals.
	 */
	@Test
	public void testEquals() {
		final EmoteCondition obj = new EmoteCondition("hugs");
		assertTrue(obj.equals(obj));
		assertTrue(new EmoteCondition("hugs").equals(new EmoteCondition("hugs")));
		assertFalse(new EmoteCondition("hugs").equals(new EmoteCondition("kill")));
		assertFalse(new EmoteCondition("hugs").equals("testString"));
		assertFalse(new EmoteCondition("hugs").equals(null));
		assertTrue("subclass is equal",
				new EmoteCondition("hugs").equals(new EmoteCondition("hugs") {
					// this is an anonymous sub class
				}));
	}

	/**
	 * Tests for fire.
	 */
	@Test
	public void testFire() {
		final SpeakerNPC npc = SpeakerNPCTestHelper.createSpeakerNPC();
		npc.setName("TestNPC");
		assertTrue(new EmoteCondition("hugs").fire(createPlayer("player"),
				ConversationParser.parse("!me hugs TestNPC"),
				npc));
		assertFalse(new EmoteCondition("hugs").fire(createPlayer("player"),
				ConversationParser.parse("!me killing TestNPC"),
				npc));
		assertFalse(new EmoteCondition("hugs").fire(createPlayer("player"),
				ConversationParser.parse("I killing TestNPC"),
				npc));
		assertFalse(new EmoteCondition("hugs").fire(createPlayer("player"),
				ConversationParser.parse("!me hugs Monogenes"),
				npc));
		assertFalse(new EmoteCondition("hugs").fire(createPlayer("player"),
				ConversationParser.parse("!me hugs "),
				npc));
	}

	/**
	 * Tests for hashCode.
	 */
	@Test
	public void testHashCode() {
		assertEquals("result", new EmoteCondition("hugs").hashCode(), new EmoteCondition("hugs").hashCode());
		assertEquals("result", new EmoteCondition("kill").hashCode(), new EmoteCondition("kill").hashCode());
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public void testToString() {
		assertEquals("result", "EmoteCondition",
				new EmoteCondition("hugs").toString());
	}

	/**
	 * Tests for fireThrowsNullPointerException.
	 */
	@Test(expected = NullPointerException.class)
	public void testFireThrowsNullPointerException() {
		new EmoteCondition("hugs").fire(null, ConversationParser.parse("!me hugs TestNPC"),
				null);
	}

}
