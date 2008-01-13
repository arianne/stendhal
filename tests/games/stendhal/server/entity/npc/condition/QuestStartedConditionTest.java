package games.stendhal.server.entity.npc.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.parser.ConversationParser;
import games.stendhal.server.entity.player.Player;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class QuestStartedConditionTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testFire() {
		assertFalse(new QuestStartedCondition("questname").fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
		Player bob = PlayerTestHelper.createPlayer("player");

		bob.setQuest("questname", "done");
		assertTrue(new QuestStartedCondition("questname").fire(bob,
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest("questname", null);
		assertFalse(new QuestStartedCondition("questname").fire(bob,
				ConversationParser.parse("testAdminConditionText"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

	}

	@Test
	public final void testQuestNotStartedCondition() {
		new QuestStartedCondition("questname");
	}

	@Test
	public final void testToString() {
		assertEquals("QuestStarted <questname>", new QuestStartedCondition(
				"questname").toString());
	}

	@Test
	public void testEquals() throws Throwable {
		assertFalse(new QuestStartedCondition("questname").equals(null));

		QuestStartedCondition obj = new QuestStartedCondition("questname");
		assertTrue(obj.equals(obj));
		assertTrue(new QuestStartedCondition("questname").equals(new QuestStartedCondition(
		"questname")));
		assertTrue(new QuestStartedCondition(null).equals(new QuestStartedCondition(
		null)));

		assertFalse(new QuestStartedCondition("questname").equals(new Object()));


		assertFalse(new QuestStartedCondition(null).equals(new QuestStartedCondition(
		"questname")));
		assertFalse(new QuestStartedCondition("questname").equals(new QuestStartedCondition(
		null)));


	}


	@Test
	public void testHashcode() throws Throwable {
		QuestStartedCondition obj = new QuestStartedCondition("questname");
		assertTrue(obj.equals(obj));
assertEquals(obj.hashCode(), obj.hashCode());


		assertTrue(new QuestStartedCondition("questname").equals(new QuestStartedCondition(
				"questname")));
		assertEquals(new QuestStartedCondition("questname").hashCode(), new QuestStartedCondition("questname").hashCode());
		assertEquals(new QuestStartedCondition(null).hashCode(), new QuestStartedCondition(null).hashCode());
	}
}
