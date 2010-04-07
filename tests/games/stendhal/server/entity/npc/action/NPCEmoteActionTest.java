package games.stendhal.server.entity.npc.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.ConversationParser;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class NPCEmoteActionTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}
	
	/**
	 * Tests for constructor.
	 */
	@Test
	public void testConstructor() throws Throwable {
		final NPCEmoteAction npcEmoteAction = new NPCEmoteAction("");
		assertEquals("npcEmoteAction.hashCode()", 629,
				npcEmoteAction.hashCode());
	}
	
	/**
	 * Tests for equals.
	 */
	@Test
	public void testEquals() throws Throwable {
		final NPCEmoteAction obj = new NPCEmoteAction("hugs");
		assertTrue(obj.equals(obj));
		assertTrue(new NPCEmoteAction("hugs").equals(new NPCEmoteAction("hugs")));
		assertFalse(new NPCEmoteAction("hugs").equals(new NPCEmoteAction("kill")));
		assertFalse(new NPCEmoteAction("hugs").equals("testString"));
		assertFalse(new NPCEmoteAction("hugs").equals(null));
		assertTrue("subclass is equal",
				new NPCEmoteAction("hugs").equals(new NPCEmoteAction("hugs") {
					// this is an anonymous sub class
				}));
	}	
	
	/**
	 * Tests for fire.
	 */
	@Ignore
	public void testFire() throws Throwable {
		final SpeakerNPC npc = SpeakerNPCTestHelper.createSpeakerNPC();
		npc.setName("TestNPC");
		new NPCEmoteAction("hugs").fire(PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("!me hugs TestNPC"),
				npc);
		new NPCEmoteAction("hugs").fire(PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("!me killing TestNPC"),
				npc);
		new NPCEmoteAction("hugs").fire(PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("I killing TestNPC"),
				npc);
		new NPCEmoteAction("hugs").fire(PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("!me hugs Monogenes"),
				npc);
		new NPCEmoteAction("hugs").fire(PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("!me hugs "),
				npc);
	}

	/**
	 * Tests for hashCode.
	 */
	@Test
	public void testHashCode() throws Throwable {
		assertEquals("result", 3214638, new NPCEmoteAction("hugs").hashCode());
		assertEquals("result", 3292627, new NPCEmoteAction("kill").hashCode());
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public void testToString() throws Throwable {
		assertEquals("result", "NPCEmoteAction",
				new NPCEmoteAction("hugs").toString());
	}

	/**
	 * Tests for fireThrowsNullPointerException.
	 */
	@Test(expected = NullPointerException.class)
	public void testFireThrowsNullPointerException() throws Throwable {
		new NPCEmoteAction("hugs").fire(null, ConversationParser.parse("!me hugs TestNPC"),
				null);
	}
}
