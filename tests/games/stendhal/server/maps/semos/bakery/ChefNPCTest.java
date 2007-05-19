package games.stendhal.server.maps.semos.bakery;

import static org.junit.Assert.*;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.EntitySlot;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.codehaus.groovy.runtime.NewInstanceMetaMethod;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ChefNPCTest {

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

	@Test
	public void testHiAndBye() {
		SpeakerNPC npc = new SpeakerNPC("chef");
		ChefNPC  cnpc = new ChefNPC();
		
		Engine en = npc.getEngine();
		cnpc.createDialog(npc);
		Player player = new Player(new RPObject());
		
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals("Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.", npc.get("text"));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		assertEquals("Bye.", npc.get("text"));
		
	}
	@Test
	public void testHiAndMake() {
		SpeakerNPC npc = new SpeakerNPC("chef");
		ChefNPC  cnpc = new ChefNPC();
		
		Engine en = npc.getEngine();
		cnpc.createDialog(npc);
		
		Player player = new Player(new RPObject());
		player.addSlot(new RPSlot("bag"));
		player.addSlot(new EntitySlot("lhand"));
		player.addSlot(new EntitySlot("rhand"));
		player.addSlot(new EntitySlot("armor"));
		player.addSlot(new EntitySlot("head"));
		player.addSlot(new EntitySlot("legs"));
		player.addSlot(new EntitySlot("feet"));
		player.addSlot(new EntitySlot("finger"));
		player.addSlot(new EntitySlot("cloak"));
		player.addSlot(new EntitySlot("keyring"));
		
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals("Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.", npc.get("text"));
		en.step(player, "make");
		assertTrue(npc.isTalking());
		assertEquals("I can only make 1 sandwich if you bring me 2 #cheese, 1 #bread, and 1 #ham.", npc.get("text"));
		
	}
}
