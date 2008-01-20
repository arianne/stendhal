package games.stendhal.server.entity.npc.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.parser.ConversationParser;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Log4J;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class QuestActiveConditionTest {
	@BeforeClass
	public static void setUpClass() throws Exception {
		Log4J.init();
	}

	@Test
	public final void testFire() {
		assertFalse(new QuestActiveCondition("questname").fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("QuestActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
		Player bob = PlayerTestHelper.createPlayer("player");

		bob.setQuest("questname", "");
		assertTrue(new QuestActiveCondition("questname").fire(bob,
				ConversationParser.parse("QuestActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest("questname", null);
		assertFalse(new QuestActiveCondition("questname").fire(bob,
				ConversationParser.parse("QuestActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest("questname", "done");
		assertFalse(new QuestActiveCondition("questname").fire(bob,
				ConversationParser.parse("QuestActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()));
		
		bob.setQuest("questname", "rejected");
		assertFalse(new QuestActiveCondition("questname").fire(bob,
				ConversationParser.parse("QuestActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()));

	}

	@Test
	public final void testQuestActiveCondition() {
		new QuestActiveCondition("questname");
	}

	@Test
	public final void testToString() {
		assertEquals("QuestActive <questname>", new QuestActiveCondition(
				"questname").toString());
	}

	@Test
	public void testEquals() throws Throwable {
		assertFalse(new QuestActiveCondition("questname").equals(null));

		QuestActiveCondition obj = new QuestActiveCondition("questname");
		assertTrue(obj.equals(obj));
		assertTrue(new QuestActiveCondition("questname").equals(new QuestActiveCondition(
				"questname")));
		assertTrue(new QuestActiveCondition(null).equals(new QuestActiveCondition(
				null)));

		assertFalse(new QuestActiveCondition("questname").equals(new Object()));

		assertFalse(new QuestActiveCondition(null).equals(new QuestActiveCondition(
				"questname")));
		assertFalse(new QuestActiveCondition("questname").equals(new QuestActiveCondition(
				null)));
		assertFalse(new QuestActiveCondition("questname").equals(new QuestActiveCondition(
				"questname2")));
		assertTrue(new QuestActiveCondition("questname").equals(new QuestActiveCondition(
				"questname") { 
			//sub classing
		}));
	}

	@Test
	public void testHashCode() throws Exception {
		QuestActiveCondition obj = new QuestActiveCondition("questname");
		assertEquals(obj.hashCode(), obj.hashCode());
		assertEquals(new QuestActiveCondition("questname").hashCode(),
				new QuestActiveCondition("questname").hashCode());
		assertEquals(new QuestActiveCondition(null).hashCode(),
				new QuestActiveCondition(null).hashCode());

	}

}
