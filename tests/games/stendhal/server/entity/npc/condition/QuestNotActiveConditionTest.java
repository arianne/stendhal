package games.stendhal.server.entity.npc.condition;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import games.stendhal.server.entity.npc.parser.ConversationParser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class QuestNotActiveConditionTest {
	@BeforeClass
	public static void setUpClass() throws Exception {
		MockStendlRPWorld.get();
		Log4J.init();
	}

	/**
	 * Tests for fire.
	 */
	@Test
	public final void testFire() {
		assertThat(new QuestNotActiveCondition("questname").fire(
				PlayerTestHelper.createPlayer("player"),
				ConversationParser.parse("QuestNotActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()),
				is(true));
		final Player bob = PlayerTestHelper.createPlayer("player");

		bob.setQuest("questname", "");
		assertThat(new QuestNotActiveCondition("questname").fire(bob,
				ConversationParser.parse("QuestNotActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()),
				is(false));

		bob.setQuest("questname", null);
		assertThat(new QuestNotActiveCondition("questname").fire(bob,
				ConversationParser.parse("QuestNotActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()),
				is(true));

		bob.setQuest("questname", "done");
		assertThat(new QuestNotActiveCondition("questname").fire(bob,
				ConversationParser.parse("QuestNotActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()),
				is(true));
		
		bob.setQuest("questname", "rejected");
		assertThat(new QuestNotActiveCondition("questname").fire(bob,
				ConversationParser.parse("QuestNotActiveConditionTest"),
				SpeakerNPCTestHelper.createSpeakerNPC()),
				is(true));

	}

	/**
	 * Tests for questNotActiveCondition.
	 */
	@Test
	public final void testQuestNotActiveCondition() {
		new QuestNotActiveCondition("questname");
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public final void testToString() {
		assertThat(new QuestNotActiveCondition("questname").toString(), is("QuestNotActive <questname>"));
	}

	/**
	 * Tests for equals.
	 */
	@Test
	public void testEquals() throws Throwable {
		assertThat(new QuestNotActiveCondition("questname"), not(equalTo(null)));

		final QuestNotActiveCondition obj = new QuestNotActiveCondition("questname");
		assertThat(obj, equalTo(obj));
		assertThat(new QuestNotActiveCondition("questname"), 
				equalTo(new QuestNotActiveCondition("questname")));

		assertThat(new QuestNotActiveCondition(null), 
				equalTo(new QuestNotActiveCondition(null)));

		assertThat(new QuestNotActiveCondition("questname"),
				not(equalTo(new Object())));

		assertThat(new QuestNotActiveCondition(null),
				not(equalTo(new QuestNotActiveCondition(
				"questname"))));
		assertThat(new QuestNotActiveCondition("questname"),
				not(equalTo(new QuestNotActiveCondition(
				null))));
		assertThat(new QuestNotActiveCondition("questname"),
				not(equalTo(new QuestNotActiveCondition(
				"questname2"))));

		assertThat(new QuestNotActiveCondition("questname"),
				equalTo((QuestNotActiveCondition) new QuestNotActiveCondition("questname") { 
					//sub classing
			}));
	}

	/**
	 * Tests for hashCode.
	 */
	@Test
	public void testHashCode() throws Exception {
		final QuestNotActiveCondition obj = new QuestNotActiveCondition("questname");
		assertThat(obj.hashCode(), equalTo(obj.hashCode()));
		assertThat(new QuestNotActiveCondition("questname").hashCode(),
				equalTo(new QuestNotActiveCondition("questname").hashCode()));
		assertThat(new QuestNotActiveCondition(null).hashCode(),
				equalTo(new QuestNotActiveCondition(null).hashCode()));

	}

}
