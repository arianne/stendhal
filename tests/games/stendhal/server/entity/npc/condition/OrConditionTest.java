package games.stendhal.server.entity.npc.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.ConversationParser;

import org.junit.Before;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class OrConditionTest {

	private AlwaysTrueCondition trueCondition;

	private ChatCondition falsecondition;

	@Before
	public void setUp() throws Exception {
		trueCondition = new AlwaysTrueCondition();
		falsecondition = new NotCondition(new AlwaysTrueCondition());

	}

	/**
	 * Tests for constructor.
	 */
	@Test
	public void testConstructor() throws Throwable {
		new OrCondition();
	}

	/**
	 * Tests for equals.
	 */
	@Test
	public void testEquals() throws Throwable {
		assertFalse(new OrCondition().equals(null));

		final OrCondition obj = new OrCondition();
		assertTrue(obj.equals(obj));
		assertTrue(new OrCondition().equals(new OrCondition()));
		assertTrue(new OrCondition((ChatCondition) null).equals(new OrCondition(
				(ChatCondition) null)));

		assertFalse(new OrCondition((ChatCondition) null).equals(new OrCondition()));
		assertFalse(new OrCondition().equals(new OrCondition(
				(ChatCondition) null)));
		assertFalse(new OrCondition((ChatCondition) null).equals(new OrCondition(
				falsecondition)));
		assertFalse(new OrCondition().equals(Integer.valueOf(100)));
		assertTrue(new OrCondition().equals(new OrCondition() {
			// this is an anonymous sub class
		}));
	}

	/**
	 * Tests for fire.
	 */
	@Test
	public void testFire() throws Throwable {

		assertFalse("empty OR is false", new OrCondition().fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("testOrConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		OrCondition orCondition = new OrCondition(trueCondition);
		assertTrue("OR with one Allwaystrue is true", orCondition.fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("testOrConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		orCondition = new OrCondition(trueCondition, falsecondition);
		assertTrue("OR with one true and on false is true", orCondition.fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("testOrConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		orCondition = new OrCondition(falsecondition, trueCondition);
		assertTrue("OR with one false and on true is true", orCondition.fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("testOrConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		orCondition = new OrCondition(new AdminCondition());

		assertFalse("OR with one false is false", orCondition.fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("testOrConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
	}

	/**
	 * Tests for hashCode.
	 */
	@Test
	public void testHashCode() throws Throwable {
		final OrCondition obj = new OrCondition();
		assertEquals(obj.hashCode(), obj.hashCode());
		assertEquals(new OrCondition().hashCode(), new OrCondition().hashCode());
		assertEquals(new OrCondition((ChatCondition) null).hashCode(),
				new OrCondition((ChatCondition) null).hashCode());

	}

	/**
	 * Tests for toString.
	 */
	@Test
	public void testToString() throws Throwable {
		assertEquals("or <[]>", new OrCondition().toString());

		assertEquals("or <[true]>", new OrCondition(trueCondition).toString());
		assertEquals("or <[true, not <true>]>", new OrCondition(trueCondition,
				falsecondition).toString());
		assertEquals("or <[not <true>]>",
				new OrCondition(falsecondition).toString());
	}

}
