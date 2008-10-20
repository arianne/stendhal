package games.stendhal.server.entity.npc.fsm;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Log4J;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class EngineTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
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
	public void testaddBothActionsNull() throws Exception {
		final Engine en = new Engine(new SpeakerNPC("bob"));
		assertTrue(en.getTransitions().isEmpty());
		en.add(0, null, null, null, 0, null, null);
		assertThat(en.getTransitions().size(), is(1));
		en.add(0, null, null, null, 0, null, null);
		assertThat(en.getTransitions().size(), is(1));
	}
	
	@Test
	public void testaddExistingActionNull() throws Exception {
		final Engine en = new Engine(new SpeakerNPC("bob"));
		
		en.add(0, null, null, null, 0, null, null);
		assertThat(en.getTransitions().size(), is(1));
		en.add(0, null, null, null, 0, null, new ChatAction() {

			public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
				
			}
		});
		assertThat(en.getTransitions().size(), is(2));
		
	}
	
	@Test
	public void testaddnewNullAction() throws Exception {
		final Engine en = new Engine(new SpeakerNPC("bob"));
		
		
		en.add(0, null, null, null, 0, null, new ChatAction() {

			public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
				
			}
		});
		assertThat(en.getTransitions().size(), is(1));
		en.add(0, null, null, null, 0, null, null);
		
		assertThat(en.getTransitions().size(), is(2));
		
	}
	
	@Test
	public void testaddSameAction() throws Exception {
		final Engine en = new Engine(new SpeakerNPC("bob"));
		ChatAction chatAction = new ChatAction() {

			public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
				
			}
		};
		en.add(0, null, null, null, 0, null, chatAction);
		assertThat(en.getTransitions().size(), is(1));
		
		en.add(0, null, null, null, 0, null, chatAction);
		assertThat(en.getTransitions().size(), is(1));
		
	}
	
	@Test
	public void testaddNotSameAction() throws Exception {
		final Engine en = new Engine(new SpeakerNPC("bob"));
		ChatAction chatAction1 = new ChatAction() {

			public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
				
			}
		};
		ChatAction chatAction2 = new ChatAction() {

			public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
				
			}
		};
		en.add(0, null, null, null, 0, null, chatAction1);
		assertThat(en.getTransitions().size(), is(1));
		
		en.add(0, null, null, null, 0, null, chatAction2);
		assertThat(en.getTransitions().size(), is(2));
		
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
