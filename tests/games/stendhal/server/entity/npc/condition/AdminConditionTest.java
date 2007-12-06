package games.stendhal.server.entity.npc.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.ConversationParser;

import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class AdminConditionTest {

	@Test
	public void testConstructor() throws Throwable {
		AdminCondition adminCondition = new AdminCondition();
		assertEquals("adminCondition.hashCode()", 5629,
				adminCondition.hashCode());
	}

	@Test
	public void testConstructor1() throws Throwable {
		AdminCondition adminCondition = new AdminCondition(100);
		assertEquals("adminCondition.hashCode()", 729,
				adminCondition.hashCode());
	}

	@Test
	public void testEquals() throws Throwable {
		AdminCondition obj = new AdminCondition(100);
		assertTrue(obj.equals(obj));
		assertTrue(new AdminCondition().equals(new AdminCondition()));
		assertFalse(new AdminCondition(100).equals(new AdminCondition(1000)));
		assertFalse(new AdminCondition(100).equals("testString"));
		assertFalse(new AdminCondition(100).equals(null));
		assertTrue("subclass is equal",
				new AdminCondition(100).equals(new AdminCondition(100) {
				}));
	}

	@Test
	public void testFire() throws Throwable {
		assertTrue(new AdminCondition(0).fire(PlayerTestHelper.createPlayer(),
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
		assertFalse(new AdminCondition().fire(PlayerTestHelper.createPlayer(),
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

	}

	@Test
	public void testHashCode() throws Throwable {
		assertEquals("result", 629, new AdminCondition(0).hashCode());
		assertEquals("result", 729, new AdminCondition(100).hashCode());
	}

	@Test
	public void testToString() throws Throwable {
		assertEquals("result", "admin <100>",
				new AdminCondition(100).toString());
	}

	@Test(expected = NullPointerException.class)
	public void testFireThrowsNullPointerException() throws Throwable {
		new AdminCondition(100).fire(null, ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC());
	}
}
