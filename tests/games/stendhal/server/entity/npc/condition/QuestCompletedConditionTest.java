package games.stendhal.server.entity.npc.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.parser.ConversationParser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class QuestCompletedConditionTest {
	@BeforeClass
	public static void setUpClass() throws Exception {
		Log4J.init();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MockStendlRPWorld.reset();
	}

	@Test
	public final void testFire() {
		assertFalse(new QuestCompletedCondition("questname").fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("testQuestCompletedCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
		final Player bob = PlayerTestHelper.createPlayer("player");

		bob.setQuest("questname", "");
		assertFalse(new QuestCompletedCondition("questname").fire(bob,
				ConversationParser.parse("testQuestCompletedCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest("questname", null);
		assertFalse(new QuestCompletedCondition("questname").fire(bob,
				ConversationParser.parse("testQuestCompletedCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest("questname", "done");
		assertTrue(new QuestCompletedCondition("questname").fire(bob,
				ConversationParser.parse("testQuestCompletedCondition"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

	}

	@Test
	public final void testQuestCompletedCondition() {
		new QuestCompletedCondition("questname");
	}

	@Test
	public final void testToString() {
		assertEquals("QuestCompleted <questname>", new QuestCompletedCondition(
				"questname").toString());
	}

	@Test
	public void testEquals() throws Throwable {
		assertTrue(new QuestCompletedCondition("questname").equals(new QuestCompletedCondition(
				"questname")));
		assertTrue(new QuestCompletedCondition(null).equals(new QuestCompletedCondition(
				null)));

		assertFalse(new QuestCompletedCondition(null).equals(new QuestCompletedCondition(
				"questname")));
		assertFalse(new QuestCompletedCondition("questname").equals(new QuestCompletedCondition(
				null)));
		assertFalse(new QuestCompletedCondition("questname").equals(null));

		final QuestCompletedCondition obj = new QuestCompletedCondition("questname");
		assertTrue(obj.equals(obj));

		assertFalse(new QuestCompletedCondition("questname").equals(new Object()));

		assertTrue(new QuestCompletedCondition("questname").equals(new QuestCompletedCondition(
				"questname") {
			// this is an anonymous sub class
		}));
	}

	@Test
	public void testHashCode() throws Exception {
		assertEquals(new QuestCompletedCondition("questname").hashCode(),
				new QuestCompletedCondition("questname").hashCode());
		assertEquals(new QuestCompletedCondition(null).hashCode(),
				new QuestCompletedCondition(null).hashCode());
	}

}
