package games.stendhal.server.entity.npc.fsm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatCondition;
import games.stendhal.server.entity.npc.condition.AllwaysTrueCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.player.Player;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;

public class TransitionTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testTransition() {
		new Transition(-2, "trigger",null,0, null,null);
	}

	@Test
	public final void testIsAbsoluteJump() {
		Transition t = new Transition(ConversationStates.ANY, "trigger",null,0, null,null);
		assertTrue(t.matchesWild("trigger"));

		t = new Transition(ConversationStates.ANY, "TRiggER",null,0, null,null);
		assertTrue(t.matchesWild("trigger"));

		t = new Transition(-2, "Trigger",null,0, null,null);
		assertFalse(t.matchesWild("trigger"));
	}

	@Test
	public final void testMatches() {
		Transition t = new Transition(-2, "trigger",null,0, null,null);
		assertTrue(t.matches(-2, "trigger"));
		assertFalse(t.matches(0, "trigger"));
		assertFalse(t.matches(0, null));
		assertFalse(t.matches(-2, null));

	}

	@Test
	public final void testMatchesBeginning() {
		Transition t = new Transition(-2, "trigger",null,0, null,null);
		assertTrue(t.matchesBeginning(-2, "triggerstartisok"));
		assertFalse(t.matchesBeginning(-2, "Nottriggerstartisok"));
		assertFalse(t.matchesBeginning(0, "triggerstartisok"));
		//assertFalse(t.matchesBeginning(-2, null)); TODO : throws NPE
	}

	@Test
	public final void testIsConditionFulfilled() {
		Transition t = new Transition(-2, "trigger",null,0, null,null);
		assertTrue(t.isConditionFulfilled(PlayerTestHelper.createPlayer(), null,SpeakerNPCTestHelper.createSpeakerNPC() ));
		t = new Transition(-2, "trigger",new AllwaysTrueCondition(),0, null,null);
		assertTrue(t.isConditionFulfilled(PlayerTestHelper.createPlayer(), null,SpeakerNPCTestHelper.createSpeakerNPC() ));
		t = new Transition(-2, "trigger",new NotCondition(new AllwaysTrueCondition()),0, null,null);
		assertFalse(t.isConditionFulfilled(PlayerTestHelper.createPlayer(), null,SpeakerNPCTestHelper.createSpeakerNPC() ));


	}

	@Test
	public final void testGetAction() {
		Transition t = new Transition(-2, "trigger",null,0, null,null);
		assertNull(t.getAction());
		PostTransitionAction postTransitionAction = new PostTransitionAction(){

			public void fire(Player player, String text, SpeakerNPC engine) {
				//do nothing

			}};
		t = new Transition(-2, "trigger",null,0, null,postTransitionAction);
		assertEquals(postTransitionAction,t.getAction());


	}

	@Test
	public final void testGetCondition() {
		Transition t = new Transition(-2, "trigger",null,0, null,null);
		assertNull(t.getCondition());
		ChatCondition cond = new ChatCondition(){

			@Override
			public boolean fire(Player player, String text, SpeakerNPC npc) {
				return false;
			}};
			t = new Transition(-2, "trigger",cond,0, null,null);
			assertEquals(cond ,t.getCondition());
	}

	@Test
	public final void testGetNextState() {
		Transition t = new Transition(-2, "trigger",null,0, null,null);
		assertEquals(0,t.getNextState());
		t = new Transition(-2, "trigger",null,1, null,null);
		assertEquals(1,t.getNextState());
	}

	@Test
	public final void testGetSetReply() {
		Transition t = new Transition(-2, "trigger",null,0, null,null);
		assertNull(t.getReply());
		t = new Transition(-2, "trigger",null,0, "output",null);
		assertEquals("output",t.getReply());
		t.setReply("blabla");
		assertEquals("blabla",t.getReply());
	}



	@Test
	public final void testGetState() {
		Transition t = new Transition(-2, "trigger",null,0, null,null);
		assertEquals(-2,t.getState());
	}

	@Test
	public final void testGetTrigger() {
		Transition t = new Transition(-2, "trigger",null,0, null,null);
		assertEquals("trigger",t.getTrigger());
	}

	@Test
	public final void testToString() {
		Transition t = new Transition(-2, "trigger",null,0, null,null);
		assertEquals("[-2,trigger,0,null]",t.toString());
	}

}
