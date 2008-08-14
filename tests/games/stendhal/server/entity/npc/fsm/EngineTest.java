package games.stendhal.server.entity.npc.fsm;

import static org.junit.Assert.assertEquals;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
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
		final Engine en = new Engine(new SpeakerNPC("bob"));
		assertEquals("creates an integer for a free state", 1, en
				.getFreeState());
		assertEquals("creates the next integer for a free state", 2, en
				.getFreeState());
	}

	@Test
	public void testAddSingleStringEmptyCondition() {
		final Engine en = new Engine(new SpeakerNPC("bob"));
		int state;
		state = 0;
		final String triggers = "boo";

		final int nextState = state + 1;
		final String reply = "huch";
		final ChatAction action = new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
				assertEquals("boo", sentence.getTriggerExpression().getNormalized());
			}
		};
		en.add(state, triggers, null, nextState, reply, action);
		final Player pete = PlayerTestHelper.createPlayer("player");
		en.step(pete, "boo");
		assertEquals(nextState, en.getCurrentState());
	}

	@Test
	public void testAddSingleStringValidCondition() {
		final SpeakerNPC bob = new SpeakerNPC("bob");

		final Engine en = new Engine(bob);
		int state;
		state = 0;
		final String triggers = "boo";

		final ChatCondition cc = new ChatCondition() {
			public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
				assertEquals(triggers, sentence.getTriggerExpression().getNormalized());
				return true;
			}
		};

		final int nextState = state + 1;
		final String reply = "huch";
		final ChatAction action = new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
				assertEquals(triggers, sentence.getTriggerExpression().getNormalized());
			}
		};
		en.add(state, triggers, cc, nextState, reply, action);
		final Player pete = PlayerTestHelper.createPlayer("player");
		en.step(pete, triggers);
		assertEquals(nextState, en.getCurrentState());
		assertEquals(bob.get("text"), reply);
	}

}
