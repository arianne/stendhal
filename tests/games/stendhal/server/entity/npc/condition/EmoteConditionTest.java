package games.stendhal.server.entity.npc.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.ConversationParser;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class EmoteConditionTest extends PlayerTestHelper {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}
	
	/**
	 * Tests for constructor.
	 */
	@Test
	public void testConstructor() throws Throwable {
		final EmoteCondition emoteCondition = new EmoteCondition("");
		assertEquals("emoteCondition.hashCode()", 629,
				emoteCondition.hashCode());
	}

	/**
	 * Tests for equals.
	 */
	@Test
	public void testEquals() throws Throwable {
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
	public void testFire() throws Throwable {
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
	public void testHashCode() throws Throwable {
		assertEquals("result", 3214638, new EmoteCondition("hugs").hashCode());
		assertEquals("result", 3292627, new EmoteCondition("kill").hashCode());
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public void testToString() throws Throwable {
		assertEquals("result", "EmoteCondition",
				new EmoteCondition("hugs").toString());
	}

	/**
	 * Tests for fireThrowsNullPointerException.
	 */
	@Test(expected = NullPointerException.class)
	public void testFireThrowsNullPointerException() throws Throwable {
		new EmoteCondition("hugs").fire(null, ConversationParser.parse("!me hugs TestNPC"),
				null);
	}

}
