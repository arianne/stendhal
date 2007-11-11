package games.stendhal.server.entity.npc.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatCondition;
import games.stendhal.server.entity.player.Player;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class NotConditionTest {

	private final class AlwaysFalseCondition extends ChatCondition {
		@Override
		public boolean fire(Player player, String text, SpeakerNPC engine) {
			return false;
		}

		@Override
		public String toString() {
			return "false";
		}
	}

	AllwaysTrueCondition trueCondition;
	ChatCondition falsecondition;

	@Before
	public void setUp() throws Exception {
		trueCondition = new AllwaysTrueCondition();
		falsecondition = new AlwaysFalseCondition();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void selftest() throws Exception {
		assertTrue("empty And is true", trueCondition.fire(
				PlayerTestHelper.createPlayer(), "testAndConditionText",
				SpeakerNPCTestHelper.createSpeakerNPC()));
		assertFalse("empty And is true", falsecondition.fire(
				PlayerTestHelper.createPlayer(), "testAndConditionText",
				SpeakerNPCTestHelper.createSpeakerNPC()));


	}
	@Test
	public final void testHashCode() {
		assertEquals(32,new NotCondition(trueCondition).hashCode());
	// hmmm hmm 	assertEquals(14613049,new NotCondition(falsecondition).hashCode());
		assertFalse((new NotCondition(trueCondition)).equals(new NotCondition(falsecondition)));
	}

	@Test
	public final void testFire() {
		assertFalse(new NotCondition(trueCondition).fire(PlayerTestHelper.createPlayer(), "notconditiontest",SpeakerNPCTestHelper.createSpeakerNPC()));
		assertTrue(new NotCondition(falsecondition).fire(PlayerTestHelper.createPlayer(), "notconditiontest",SpeakerNPCTestHelper.createSpeakerNPC()));
	}

	@Test
	public final void testNotCondition() {
		new NotCondition(trueCondition);
	}

	@Test
	public final void testToString() {
		assertEquals("not <true>",new NotCondition(trueCondition).toString());
		assertEquals("not <false>",new NotCondition(falsecondition).toString());
	}

	@Test
	public void testEquals() throws Throwable {

		assertFalse(new NotCondition(trueCondition).equals(null));

		NotCondition obj = new NotCondition(trueCondition);
		assertTrue(obj.equals(obj));

		assertFalse(new NotCondition(trueCondition).equals(new Integer(100)));

		assertTrue(new NotCondition(trueCondition).equals(new NotCondition(trueCondition)));
		assertFalse(new NotCondition(trueCondition).equals(new NotCondition(trueCondition) {
		}));
	}

}
