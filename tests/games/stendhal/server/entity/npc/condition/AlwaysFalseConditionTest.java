/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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
import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class AlwaysFalseConditionTest {
	/**
	 * Tests for equals.
	 */
	@Test
	public void testEquals() {
		assertFalse(new AlwaysFalseCondition().equals(null));

		final AlwaysFalseCondition obj = new AlwaysFalseCondition();
		assertTrue(obj.equals(obj));

		assertFalse(new AlwaysFalseCondition().equals(Integer.valueOf(100)));

		assertTrue(new AlwaysFalseCondition().equals(new AlwaysFalseCondition()));
		assertTrue(new AlwaysFalseCondition().equals(new AlwaysFalseCondition() {
		}));
	}

	/**
	 * Tests for fire.
	 */
	@Test
	public void testFire() {
		assertFalse(new AlwaysFalseCondition().fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("testAllwaysFalseConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
	}

	/**
	 * Tests for hashCode.
	 */
	@Test
	public void testHashCode() {
		AlwaysFalseCondition obj = new AlwaysFalseCondition();
		assertEquals(obj.hashCode(), obj.hashCode());
		assertEquals(obj.hashCode(), new AlwaysFalseCondition().hashCode());
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public void testToString() {
		assertEquals("false", new AlwaysFalseCondition().toString());
	}
}
