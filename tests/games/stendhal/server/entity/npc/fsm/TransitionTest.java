package games.stendhal.server.entity.npc.fsm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.AlwaysTrueCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.parser.ConversationParser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class TransitionTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testTransition() {
		new Transition(-2, ConversationParser.createTriggerExpression("trigger"), null, 0, null, null);
	}

	@Test
	public final void testMatches() {
		final Transition t = new Transition(-2, ConversationParser.createTriggerExpression("trigger"), null, 0, null, null);
		assertTrue(t.matches(-2, ConversationParser.parse("trigger")));
		assertFalse(t.matches(0, ConversationParser.parse("trigger")));
		assertFalse(t.matches(0, ConversationParser.parse(null)));
		assertFalse(t.matches(-2, ConversationParser.parse(null)));
	}

	@Test
	public final void testMatchesNormalized() {
		final Transition t = new Transition(-2, ConversationParser.createTriggerExpression("trigger"), null, 0, null, null);
		assertTrue(t.matchesNormalized(-2, ConversationParser.parse("trigger")));
		assertFalse(t.matchesNormalized(0, ConversationParser.parse("trigger")));
		assertFalse(t.matchesNormalized(0, ConversationParser.parse(null)));
		assertFalse(t.matchesNormalized(-2, ConversationParser.parse(null)));
	}

	@Test
	public final void testMatchesSimilar() {
		final Transition t = new Transition(-2, ConversationParser.createTriggerExpression("trigger"), null, 0, null, null);
		assertTrue(t.matchesSimilar(-2, ConversationParser.parse("triggerx")));
		assertFalse(t.matchesSimilar(-2, ConversationParser.parse("xxxtriggerxxx")));
		assertFalse(t.matchesSimilar(0, ConversationParser.parse("triggerx")));
		assertFalse(t.matchesSimilar(-2, ConversationParser.parse(null)));
	}

	@Test
	public final void testIsAbsoluteJump() {
		Transition t = new Transition(ConversationStates.ANY,
				ConversationParser.createTriggerExpression("trigger"),
				null, 0, null, null);
		assertTrue(t.matchesWild(ConversationParser.parse("trigger")));

		t = new Transition(ConversationStates.ANY, ConversationParser.createTriggerExpression("TRiggER"), null, 0, null, null);
		assertFalse(t.matchesWild(ConversationParser.parse("trigger")));

		t = new Transition(-2, ConversationParser.createTriggerExpression("Trigger"),
				null, 0, null, null);
		assertFalse(t.matchesWild(ConversationParser.parse("trigger")));

		t = new Transition(ConversationStates.ANY,
				ConversationParser.createTriggerExpression("trigger"),
				null, 0, null, null);
		assertTrue(t.matchesWildNormalized(ConversationParser.parse("trigger")));

		t = new Transition(ConversationStates.ANY, ConversationParser.createTriggerExpression("TRiggER"), null, 0, null, null);
		assertTrue(t.matchesWildNormalized(ConversationParser.parse("trigger")));

		t = new Transition(-2, ConversationParser.createTriggerExpression("Trigger"),
				null, 0, null, null);
		assertFalse(t.matchesWildNormalized(ConversationParser.parse("trigger")));
	}

	@Test
	public final void testIsConditionFulfilled() {
		Transition t = new Transition(-2, ConversationParser.createTriggerExpression("trigger"), null, 0, null, null);
		assertTrue(t.isConditionFulfilled(PlayerTestHelper.createPlayer("player"),
				null, SpeakerNPCTestHelper.createSpeakerNPC()));
		t = new Transition(-2, ConversationParser.createTriggerExpression("trigger"),
				new AlwaysTrueCondition(), 0, null, null);
		assertTrue(t.isConditionFulfilled(PlayerTestHelper.createPlayer("player"),
				null, SpeakerNPCTestHelper.createSpeakerNPC()));
		t = new Transition(-2, ConversationParser.createTriggerExpression("trigger"),
				new NotCondition(new AlwaysTrueCondition()), 0, null, null);
		assertFalse(t.isConditionFulfilled(PlayerTestHelper.createPlayer("player"),
				null, SpeakerNPCTestHelper.createSpeakerNPC()));
	}

	@Test
	public final void testGetAction() {
		Transition t = new Transition(-2, ConversationParser.createTriggerExpression("trigger"),
										null, 0, null, null);
		assertNull(t.getAction());
		final PostTransitionAction postTransitionAction = new PostTransitionAction() {

			public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
				// do nothing
			}
		};
		t = new Transition(-2, ConversationParser.createTriggerExpression("trigger"),
							null, 0, null, postTransitionAction);
		assertEquals(postTransitionAction, t.getAction());
	}

	@Test
	public final void testGetCondition() {
		Transition t = new Transition(-2, ConversationParser.createTriggerExpression("trigger"),
										null, 0, null, null);
		assertNull(t.getCondition());
		final ChatCondition cond = new ChatCondition() {
			public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
				return false;
			}
		};
		t = new Transition(-2, ConversationParser.createTriggerExpression("trigger"),
							cond, 0, null, null);
		assertEquals(cond, t.getCondition());
	}

	@Test
	public final void testGetNextState() {
		Transition t = new Transition(-2, ConversationParser.createTriggerExpression("trigger"),
										null, 0, null, null);
		assertEquals(0, t.getNextState());
		t = new Transition(-2, ConversationParser.createTriggerExpression("trigger"),
							null, 1, null, null);
		assertEquals(1, t.getNextState());
	}

	@Test
	public final void testGetSetReply() {
		Transition t = new Transition(-2, ConversationParser.createTriggerExpression("trigger"),
										null, 0, null, null);
		assertNull(t.getReply());
		t = new Transition(-2, ConversationParser.createTriggerExpression("trigger"), null, 0, "output", null);
		assertEquals("output", t.getReply());
		t.setReply("blabla");
		assertEquals("blabla", t.getReply());
	}

	@Test
	public final void testGetState() {
		final Transition t = new Transition(-2, ConversationParser.createTriggerExpression("trigger"), null, 0, null, null);
		assertEquals(-2, t.getState());
	}

	@Test
	public final void testGetTrigger() {
		final Transition t = new Transition(-2, ConversationParser.createTriggerExpression("trigger"), null, 0, null, null);
		assertEquals("trigger", t.getTrigger().getNormalized());
	}

	@Test
	public final void testToString() {
		final Transition t = new Transition(-2, ConversationParser.createTriggerExpression("trigger"), null, 0, null, null);
		assertEquals("[-2,trigger,0,null]", t.toString());
	}

}
