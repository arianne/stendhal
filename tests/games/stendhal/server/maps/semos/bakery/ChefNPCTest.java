package games.stendhal.server.maps.semos.bakery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import marauroa.common.game.RPObject.ID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.ZonePlayerAndNPCTest;

public class ChefNPCTest extends ZonePlayerAndNPCTest {

	private static final String ZONE_NAME = "testzone";

	private static final String QUEST = "leander_make_sandwiches";

	private SpeakerNPC npc;
	private Engine en;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ZonePlayerAndNPCTest.setUpBeforeClass();

		setupZone(ZONE_NAME);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public ChefNPCTest() {
		super(ZONE_NAME, "chef");
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();

		npc = new SpeakerNPC("chef");
		ChefNPC cnpc = new ChefNPC();

		en = npc.getEngine();
		cnpc.createDialog(npc);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();

		player.removeQuest(QUEST);
	}

	@Test
	public void testHiAndBye() {
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.",
				npc.get("text"));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		assertEquals("Bye.", npc.get("text"));
	}

	@Test
	public void testHiAndMakeNoStuff() {
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.",
				npc.get("text"));
		en.step(player, "make");
		assertTrue(npc.isTalking());
		assertEquals(
				"I can only make 1 sandwich if you bring me 1 loaf of #bread, 1 piece of #ham, and 2 pieces of #cheese.",
				npc.get("text"));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		assertEquals("Bye.", npc.get("text"));
	}

	@Test
	public void testHiAndMakeWithStuffSingle() {
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.",
				npc.get("text"));
		StackableItem cheese = new StackableItem("cheese", "", "", null);
		cheese.setQuantity(2);
		cheese.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(cheese);
		StackableItem bread = new StackableItem("bread", "", "", null);
		bread.setQuantity(1);
		bread.setID(new ID(1, ZONE_NAME));
		player.getSlot("bag").add(bread);
		StackableItem ham = new StackableItem("ham", "", "", null);
		ham.setID(new ID(3, ZONE_NAME));
		player.getSlot("bag").add(ham);
		assertEquals(2, player.getNumberOfEquipped("cheese"));
		assertEquals(1, player.getNumberOfEquipped("bread"));
		assertEquals(1, player.getNumberOfEquipped("ham"));

		en.step(player, "make");
		assertTrue(npc.isTalking());
		assertEquals(
				"I need you to fetch me 1 loaf of #bread, 1 piece of #ham, and 2 pieces of #cheese for this job. Do you have it?",
				npc.get("text"));
		en.step(player, "yes");
		String[] questStatus = player.getQuest(QUEST).split(";");
		String[] expected = { "1", "sandwich", "" };
		assertEquals("amount", expected[0], questStatus[0]); 
		assertEquals("item", expected[1], questStatus[1]); 

		assertTrue(npc.isTalking());
		assertEquals(
				"OK, I will make 1 sandwich for you, but that will take some time. Please come back in 3 minutes.",
				npc.get("text"));
		assertEquals(0, player.getNumberOfEquipped("cheese"));
		assertEquals(0, player.getNumberOfEquipped("bread"));
		assertEquals(0, player.getNumberOfEquipped("ham"));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		player.setQuest(QUEST, "1;;0");

		en.step(player, "hi");
		assertEquals(
				"Welcome back! I'm done with your order. Here you have 1 sandwich.",
				npc.get("text"));
		assertEquals(1, player.getNumberOfEquipped("sandwich"));
	}

	@Test
	public void testHiAndMakeWithStuffMultiple() {
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.",
				npc.get("text"));
		StackableItem cheese = new StackableItem("cheese", "", "", null);
		cheese.setQuantity(4);
		cheese.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(cheese);
		StackableItem bread = new StackableItem("bread", "", "", null);
		bread.setQuantity(2);
		bread.setID(new ID(1, ZONE_NAME));
		player.getSlot("bag").add(bread);
		StackableItem ham = new StackableItem("ham", "", "", null);
		ham.setQuantity(2);
		ham.setID(new ID(3, ZONE_NAME));
		player.getSlot("bag").add(ham);
		assertEquals(4, player.getNumberOfEquipped("cheese"));
		assertEquals(2, player.getNumberOfEquipped("bread"));
		assertEquals(2, player.getNumberOfEquipped("ham"));

		en.step(player, "make 2 sandwiches");
		assertTrue(npc.isTalking());
		assertEquals(
				"I need you to fetch me 2 loaves of #bread, 2 pieces of #ham, and 4 pieces of #cheese for this job. Do you have it?",
				npc.get("text"));
		en.step(player, "yes");
		String[] questStatus = player.getQuest(QUEST).split(";");
		String[] expected = { "2", "sandwich", "" };
		assertEquals("amount", expected[0], questStatus[0]);
		assertEquals("item", expected[1], questStatus[1]); 

		assertTrue(npc.isTalking());
		assertEquals(
				"OK, I will make 2 sandwich for you, but that will take some time. Please come back in 6 minutes.",
				npc.get("text"));
		assertEquals(0, player.getNumberOfEquipped("cheese"));
		assertEquals(0, player.getNumberOfEquipped("bread"));
		assertEquals(0, player.getNumberOfEquipped("ham"));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		player.setQuest(QUEST, "2;;0");

		en.step(player, "hi");
		assertEquals(
				"Welcome back! I'm done with your order. Here you have 2 sandwiches.",
				npc.get("text"));
		assertEquals(2, player.getNumberOfEquipped("sandwich"));
	}

	@Test
	public void testMultipleWithoutName() {
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.",
				npc.get("text"));
		StackableItem cheese = new StackableItem("cheese", "", "", null);
		cheese.setQuantity(6);
		cheese.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(cheese);
		StackableItem bread = new StackableItem("bread", "", "", null);
		bread.setQuantity(3);
		bread.setID(new ID(1, ZONE_NAME));
		player.getSlot("bag").add(bread);
		StackableItem ham = new StackableItem("ham", "", "", null);
		ham.setQuantity(10);
		ham.setID(new ID(3, ZONE_NAME));
		player.getSlot("bag").add(ham);
		assertEquals(6, player.getNumberOfEquipped("cheese"));
		assertEquals(3, player.getNumberOfEquipped("bread"));
		assertEquals(10, player.getNumberOfEquipped("ham"));

		en.step(player, "make 3");
		assertTrue(npc.isTalking());
		assertEquals(
				"I need you to fetch me 3 loaves of #bread, 3 pieces of #ham, and 6 pieces of #cheese for this job. Do you have it?",
				npc.get("text"));
		en.step(player, "yes");
		String[] questStatus = player.getQuest(QUEST).split(";");
		String[] expected = { "3", "sandwich", "" };
		assertEquals("amount", expected[0], questStatus[0]);
		assertEquals("item", expected[1], questStatus[1]); 

		assertTrue(npc.isTalking());
		assertEquals(
				"OK, I will make 3 sandwich for you, but that will take some time. Please come back in 9 minutes.",
				npc.get("text"));
		assertEquals(0, player.getNumberOfEquipped("cheese"));
		assertEquals(0, player.getNumberOfEquipped("bread"));
		// 10 - 3 -> 7
		assertEquals(7, player.getNumberOfEquipped("ham"));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		player.setQuest(QUEST, "3;;0");

		en.step(player, "hi");
		assertEquals(
				"Welcome back! I'm done with your order. Here you have 3 sandwiches.",
				npc.get("text"));
		assertEquals(3, player.getNumberOfEquipped("sandwich"));
	}

}
