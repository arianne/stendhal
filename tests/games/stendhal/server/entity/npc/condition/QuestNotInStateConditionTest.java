package games.stendhal.server.entity.npc.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Log4J;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class QuestNotInStateConditionTest {
	private static final String QUESTNAME = "questname";

	@BeforeClass
	public static void setUpClass() throws Exception {
		Log4J.init();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testFire() {
		String validState = "valid";
		assertTrue(new QuestNotInStateCondition(QUESTNAME, validState).fire(
				PlayerTestHelper.createPlayer(), "QuestNotInStateConditionTest",
				SpeakerNPCTestHelper.createSpeakerNPC()));
		Player bob = PlayerTestHelper.createPlayer();

		bob.setQuest(QUESTNAME, "valid");
		assertFalse(new QuestNotInStateCondition(QUESTNAME, validState).fire(bob,
				"QuestNotInStateConditionTest",
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, "");
		assertTrue(new QuestNotInStateCondition(QUESTNAME, validState).fire(bob,
				"QuestNotInStateConditionTest",
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, null);
		assertTrue(new QuestNotInStateCondition(QUESTNAME, validState).fire(bob,
				"QuestNotInStateConditionTest",
				SpeakerNPCTestHelper.createSpeakerNPC()));

		bob.setQuest(QUESTNAME, "done");
		assertTrue(new QuestNotInStateCondition(QUESTNAME, validState).fire(bob,
				"QuestNotInStateConditionTest",
				SpeakerNPCTestHelper.createSpeakerNPC()));

	}

	@Test
	public final void testQuestNotInStateCondition() {
		new QuestNotInStateCondition(QUESTNAME, "");
	}

	@Test
	public final void testToString() {
		assertEquals("QuestNotInState <questname,testToString>", new QuestNotInStateCondition(
				QUESTNAME, "testToString").toString());
	}

	@Test
	public void testEquals() throws Throwable {
		String state = "state";
		assertFalse(new QuestNotInStateCondition(QUESTNAME, state).equals(null));

		QuestNotInStateCondition obj = new QuestNotInStateCondition(QUESTNAME, state);
		assertTrue(obj.equals(obj));

		assertFalse(new QuestNotInStateCondition(QUESTNAME, state).equals(new Object()));

		assertTrue(new QuestNotInStateCondition(QUESTNAME, state).equals(new QuestNotInStateCondition(
				QUESTNAME, state)));
		assertFalse(new QuestNotInStateCondition(QUESTNAME, state).equals(new QuestNotInStateCondition(
				QUESTNAME, state) {
		}));
	}

}
