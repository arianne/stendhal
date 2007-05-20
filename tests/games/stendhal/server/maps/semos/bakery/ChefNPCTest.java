package games.stendhal.server.maps.semos.bakery;

import static org.junit.Assert.*;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.EntitySlot;

import marauroa.common.Log4J;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ChefNPCTest {
	private Engine en;
	private Player player;
	private SpeakerNPC npc;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		npc = new SpeakerNPC("chef");
		ChefNPC  cnpc = new ChefNPC();
		
		en = npc.getEngine();
		cnpc.createDialog(npc);
		
		 player = new Player(new RPObject());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHiAndBye() {

		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals("Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.", npc.get("text"));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		assertEquals("Bye.", npc.get("text"));
		
	}
	@Test
	public void testHiAndMakeNoStuff() {
		
		addslots(player);
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals("Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.", npc.get("text"));
		en.step(player, "make");
		assertTrue(npc.isTalking());
		assertEquals("I can only make 1 sandwich if you bring me 2 #cheese, 1 #bread, and 1 #ham.", npc.get("text"));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		assertEquals("Bye.", npc.get("text"));
	}
	@Test
	public void testHiAndMakeWithStuff() {
		
		addslots(player);
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals("Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.", npc.get("text"));
		StackableItem cheese =  new StackableItem("cheese",null,null,null);
		cheese.setQuantity(2);
		player.getSlot("bag").add(cheese);
		StackableItem bread =  new StackableItem("bread",null,null,null);
		player.getSlot("bag").add(bread );
		player.getSlot("bag").add( new Item("ham",null,null,null));
		assertEquals(2,player.getNumberOfEquipped("cheese"));
		assertEquals(1,player.getNumberOfEquipped("bread"));
		assertEquals(2,player.getNumberOfEquipped("ham"));
		en.step(player, "make");
		assertTrue(npc.isTalking());
		assertEquals("I can only make 1 sandwich if you bring me 2 #cheese, 1 #bread, and 1 #ham.", npc.get("text"));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		assertEquals("Bye.", npc.get("text"));
	}

	private void addslots(Player player) {
		player.addSlot(new RPSlot("bag"));
		player.getSlot("bag").setCapacity(20);
		player.addSlot(new EntitySlot("lhand"));
		player.addSlot(new EntitySlot("rhand"));
		player.addSlot(new EntitySlot("armor"));
		player.addSlot(new EntitySlot("head"));
		player.addSlot(new EntitySlot("legs"));
		player.addSlot(new EntitySlot("feet"));
		player.addSlot(new EntitySlot("finger"));
		player.addSlot(new EntitySlot("cloak"));
		player.addSlot(new EntitySlot("keyring"));
	}
}
