package games.stendhal.server.entity.npc.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.parser.ConversationParser;
import games.stendhal.server.entity.player.Player;

import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class QuestNotStartedConditionTest {

	@Test
	public final void testFire() {
		assertTrue(new QuestNotStartedCondition("questname").fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
		final Player bob = PlayerTestHelper.createPlayer("player");

		bob.setQuest("questname", "");
		assertFalse(new QuestNotStartedCondition("questname").fire(bob,
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest("questname", null);
		assertTrue(new QuestNotStartedCondition("questname").fire(bob,
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
		
		bob.setQuest("questname", "rejected");
		assertTrue(new QuestNotStartedCondition("questname").fire(bob,
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
	}

	@Test
	public final void testQuestNotStartedCondition() {
		new QuestNotStartedCondition("questname");
	}

	@Test
	public final void testToString() {
		assertEquals("QuestNotStarted <questname>",
				new QuestNotStartedCondition("questname").toString());
	}

	@Test
	public void testEquals() throws Throwable {
		assertFalse(new QuestNotStartedCondition("questname").equals(null));

		final QuestNotStartedCondition obj = new QuestNotStartedCondition("questname");
		assertTrue(obj.equals(obj));

		assertTrue(new QuestNotStartedCondition("questname").equals(new QuestNotStartedCondition(
				"questname")));

		assertTrue(new QuestNotStartedCondition(null).equals(new QuestNotStartedCondition(
				null)));

		assertFalse(new QuestNotStartedCondition("questname").equals(new Object()));

		assertFalse(new QuestNotStartedCondition("questname").equals(new QuestNotStartedCondition(
				null)));
		assertFalse(new QuestNotStartedCondition(null).equals(new QuestNotStartedCondition(
				"questname")));

		assertTrue(new QuestNotStartedCondition("questname").equals(new QuestNotStartedCondition(
				"questname") {
			// this is an anonymous sub class
		}));
		
	}

	@Test
	public void testHashCode() throws Throwable {
		final QuestNotStartedCondition obj = new QuestNotStartedCondition("questname");
		assertTrue(obj.equals(obj));
		assertEquals(obj.hashCode(), obj.hashCode());
		assertTrue(new QuestNotStartedCondition("questname").equals(new QuestNotStartedCondition(
				"questname")));
		assertEquals(new QuestNotStartedCondition("questname").hashCode(),
				new QuestNotStartedCondition("questname").hashCode());

		assertTrue(new QuestNotStartedCondition(null).equals(new QuestNotStartedCondition(
				null)));

		assertEquals(new QuestNotStartedCondition(null).hashCode(),
				new QuestNotStartedCondition(null).hashCode());
	}
}
