package games.stendhal.server.entity.npc.fsm;

import static org.junit.Assert.assertEquals;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatCondition;
import games.stendhal.server.entity.player.Player;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class EngineTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PlayerTestHelper.generatePlayerRPClasses();
		PlayerTestHelper.generateNPCRPClasses();
	}


	@Test(expected = IllegalArgumentException.class)
	public void testEngine() {
		new Engine(null);
	}

	@Test
	public void testGetFreeState() {
		Engine en = new Engine(new SpeakerNPC("bob"));
		assertEquals("creates an integer for a free state", 1, en
				.getFreeState());
		assertEquals("creates the next integer for a free state", 2, en
				.getFreeState());
	}

	@Test
	public void testAddSingleStringEmptyCondition() {

		Engine en = new Engine(new SpeakerNPC("bob"));
		int state;
		state = 0;
		String triggers = "boo";

		int nextState = state + 1;
		String reply = "huch";
		ChatAction action = new ChatAction() {

			@Override
			public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
				assertEquals("boo", sentence.getVerb());
			}
		};
		en.add(state, triggers, null, nextState, reply, action);
		Player pete = PlayerTestHelper.createPlayer();
		en.step(pete, "boo");
		assertEquals(nextState, en.getCurrentState());

	}

	@Test
	public void testAddSingleStringValidCondition() {
		SpeakerNPC bob = new SpeakerNPC("bob");

		Engine en = new Engine(bob);
		int state;
		state = 0;
		final String triggers = "boo";

		ChatCondition cc = new ChatCondition() {

			@Override
			public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
				assertEquals(triggers, sentence.getVerb());
				return true;
			}
		};

		int nextState = state + 1;
		String reply = "huch";
		ChatAction action = new ChatAction() {

			@Override
			public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
				assertEquals(triggers, sentence.getVerb());
			}
		};
		en.add(state, triggers, cc, nextState, reply, action);
		Player pete = PlayerTestHelper.createPlayer();
		en.step(pete, triggers);
		assertEquals(nextState, en.getCurrentState());
		assertEquals(bob.get("text"), reply);
	}

}
