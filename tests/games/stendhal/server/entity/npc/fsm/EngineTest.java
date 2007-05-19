package games.stendhal.server.entity.npc.fsm;

import static org.junit.Assert.*;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatCondition;
import games.stendhal.server.entity.player.Player;

import marauroa.common.game.RPObject;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EngineTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test (expected=IllegalArgumentException.class)
	public void testEngine() {
		Engine en = new Engine(null);
	}

	@Test
	public void testGetFreeState() {
		Engine en = new Engine(new SpeakerNPC("bob"));
		assertEquals("creates an integer for a free state",1,en.getFreeState());
		assertEquals("creates the next integer for a free state",2,en.getFreeState());
		
	}

	@Test
	public void testAddSingleStringEmptyCondition() {
		
		Engine en = new Engine(new SpeakerNPC("bob"));
		int state;
		state=0;
		String triggers = "boo";
		
			int nextState = state +1;
			String reply = "huch";
			ChatAction action = new ChatAction(){

				@Override
				public void fire(Player player, String text, SpeakerNPC npc) {
					assertEquals("boo",text);
					
				}};
		en.add(state, triggers, null, nextState, reply, action);
		Player pete = new Player(new RPObject());
		en.step(pete, "boo");
		assertEquals(nextState, en.getCurrentState());
		
	
	}
	@Test
	public void testAddSingleStringValidCondition() {
		SpeakerNPC bob = new SpeakerNPC("bob");
	
		Engine en = new Engine(bob);
		int state;
		state=0;
		final String triggers = "boo";
		
				ChatCondition cc = new ChatCondition(){

					@Override
					public boolean fire(Player player, String text, SpeakerNPC npc) {
						
						assertEquals(triggers,text);
						return true;
					}}; 
		
			int nextState = state +1;
			String reply = "huch";
			ChatAction action = new ChatAction(){

				@Override
				public void fire(Player player, String text, SpeakerNPC npc) {
					
					assertEquals(triggers,text);
				}};
		en.add(state, triggers, cc, nextState, reply, action);
		Player pete = new Player(new RPObject());
		en.step(pete, triggers);
		assertEquals(nextState, en.getCurrentState());
		assertEquals(bob.get("text"), reply);
	}

	
}
