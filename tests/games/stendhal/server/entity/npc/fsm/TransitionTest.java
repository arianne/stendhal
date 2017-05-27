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
package games.stendhal.server.entity.npc.fsm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.condition.AlwaysTrueCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class TransitionTest {

	private static final ConversationStates idle_0 = ConversationStates.IDLE;
	private static final ConversationStates someconst = ConversationStates.INFORMATION_9;

	@BeforeClass
	public static void setUp() throws Exception {
		MockStendlRPWorld.get();
	}


	/**
	 * Tests for transition.
	 */
	@Test
	public final void testTransition() {
		new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")), null, false, idle_0, null, null);
	}

	/**
	 * Tests for matches.
	 */
	@Test
	public final void testMatches() {
		final Transition t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")), null, false, idle_0, null, null);
		assertTrue(t.matches(someconst, ConversationParser.parse("trigger")));
		assertFalse(t.matches(idle_0, ConversationParser.parse("trigger")));
		assertFalse(t.matches(idle_0, ConversationParser.parse(null)));
		assertFalse(t.matches(someconst, ConversationParser.parse(null)));
	}

	/**
	 * Tests for matchesNormalized.
	 */
	@Test
	public final void testMatchesNormalized() {
		final Transition t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")), null, false, idle_0, null, null);
		assertTrue(t.matchesNormalized(someconst, ConversationParser.parse("trigger")));
		assertFalse(t.matchesNormalized(idle_0, ConversationParser.parse("trigger")));
		assertFalse(t.matchesNormalized(idle_0, ConversationParser.parse(null)));
		assertFalse(t.matchesNormalized(someconst, ConversationParser.parse(null)));
	}

	/**
	 * Tests for matchesSimilar.
	 */
	@Test
	public final void testMatchesSimilar() {
		final Transition t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")), null, false, idle_0, null, null);
		assertTrue(t.matchesSimilar(someconst, ConversationParser.parse("triggerx")));
		assertFalse(t.matchesSimilar(someconst, ConversationParser.parse("xxxtriggerxxx")));
		assertFalse(t.matchesSimilar(idle_0, ConversationParser.parse("triggerx")));
		assertFalse(t.matchesSimilar(someconst, ConversationParser.parse(null)));
	}

	/**
	 * Tests for isAbsoluteJump.
	 */
	@Test
	public final void testIsAbsoluteJump() {
		Transition t = new Transition(ConversationStates.ANY,
				Arrays.asList(ConversationParser.createTriggerExpression("trigger")),
				null, false, idle_0, null, null);
		assertTrue(t.matchesWild(ConversationParser.parse("trigger")));

		t = new Transition(ConversationStates.ANY, Arrays.asList(ConversationParser.createTriggerExpression("TRiggER")), null, false, idle_0, null, null);
		assertFalse(t.matchesWild(ConversationParser.parse("trigger")));

		t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("Trigger")),
				null, false, idle_0, null, null);
		assertFalse(t.matchesWild(ConversationParser.parse("trigger")));

		t = new Transition(ConversationStates.ANY,
				Arrays.asList(ConversationParser.createTriggerExpression("trigger")),
				null, false, idle_0, null, null);
		assertTrue(t.matchesWildNormalized(ConversationParser.parse("trigger")));

		t = new Transition(ConversationStates.ANY, Arrays.asList(ConversationParser.createTriggerExpression("TRiggER")), null, false, idle_0, null, null);
		assertTrue(t.matchesWildNormalized(ConversationParser.parse("trigger")));

		t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("Trigger")),
				null, false, idle_0, null, null);
		assertFalse(t.matchesWildNormalized(ConversationParser.parse("trigger")));
	}

	/**
	 * Tests for isConditionFulfilled.
	 */
	@Test
	public final void testIsConditionFulfilled() {
		Transition t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")), null, false, idle_0, null, null);
		assertTrue(t.isConditionFulfilled(PlayerTestHelper.createPlayer("player"),
				null, SpeakerNPCTestHelper.createSpeakerNPC()));
		t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")),
				new AlwaysTrueCondition(), false, idle_0, null, null);
		assertTrue(t.isConditionFulfilled(PlayerTestHelper.createPlayer("player"),
				null, SpeakerNPCTestHelper.createSpeakerNPC()));
		t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")),
				new NotCondition(new AlwaysTrueCondition()), false, idle_0, null, null);
		assertFalse(t.isConditionFulfilled(PlayerTestHelper.createPlayer("player"),
				null, SpeakerNPCTestHelper.createSpeakerNPC()));
	}

	/**
	 * Tests for getAction.
	 */
	@Test
	public final void testGetAction() {
		Transition t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")),
										null, false, idle_0, null, null);
		assertNull(t.getAction());
		final PostTransitionAction postTransitionAction = new PostTransitionAction() {

			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser entity) {
				// do nothing
			}
		};
		t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")),
							null, false, idle_0, null, postTransitionAction);
		assertEquals(postTransitionAction, t.getAction());
	}

	/**
	 * Tests for getCondition.
	 */
	@Test
	public final void testGetCondition() {
		Transition t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")),
										null, false, idle_0, null, null);
		assertNull(t.getCondition());
		final ChatCondition cond = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				return false;
			}
		};
		t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")),
							cond, false, idle_0, null, null);
		assertEquals(cond, t.getCondition());
	}

	/**
	 * Tests for getNextState.
	 */
	@Test
	public final void testGetNextState() {
		Transition t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")),
										null, false, idle_0, null, null);
		assertEquals(idle_0, t.getNextState());
		t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")),
							null, false, ConversationStates.ATTENDING, null, null);
		assertEquals(ConversationStates.ATTENDING, t.getNextState());
	}

	/**
	 * Tests for getSetReply.
	 */
	@Test
	public final void testGetSetReply() {
		Transition t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")),
										null, false, idle_0, null, null);
		assertNull(t.getReply());
		t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")), null, false, idle_0, "output", null);
		assertEquals("output", t.getReply());
		t.setReply("blabla");
		assertEquals("blabla", t.getReply());
	}

	/**
	 * Tests for getState.
	 */
	@Test
	public final void testGetState() {
		final Transition t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")), null, false, idle_0, null, null);
		assertEquals(someconst, t.getState());
	}

	/**
	 * Tests for getTrigger.
	 */
	@Test
	public final void testGetTrigger() {
		final Transition t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")), null, false, idle_0, null, null);
		assertEquals("trigger", t.getTriggers().iterator().next().getNormalized());
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public final void testToString() {
		Transition t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")), null, false, idle_0, null, null);
		assertEquals("[INFORMATION_9,trigger,IDLE,null,\"\"]", t.toString());
		t = new Transition(someconst, Arrays.asList(ConversationParser.createTriggerExpression("trigger")), null, false, idle_0, null, null, "trigger");
		assertEquals("[INFORMATION_9,trigger,IDLE,null,\"trigger\"]", t.toString());
	}

}
