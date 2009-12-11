package games.stendhal.server.maps.semos.bakery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import marauroa.common.game.RPObject.ID;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class ChefNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "testzone";

	private static final String QUEST = "leander_make_sandwiches";

	private SpeakerNPC npc;
	private Engine en;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		setupZone(ZONE_NAME);
	}

	public ChefNPCTest() {
		super(ZONE_NAME, "chef");
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		npc = new SpeakerNPC("chef");
		final ChefNPC cnpc = new ChefNPC();

		en = npc.getEngine();
		cnpc.createDialog(npc);
	}

	@Override
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
				getReply(npc));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		assertEquals("Bye.", getReply(npc));
	}

	@Test
	public void testHiAndMakeNoStuff() {
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.",
				getReply(npc));
		en.step(player, "make");
		assertTrue(npc.isTalking());
		assertEquals(
				"I can only make 1 sandwich if you bring me 1 #'loaf of bread', 1 #'piece of ham', and 2 #'pieces of cheese'.",
				getReply(npc));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		assertEquals("Bye.", getReply(npc));
	}

	@Test
	public void testHiAndMakeWithStuffSingle() {
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.",
				getReply(npc));
		final StackableItem cheese = new StackableItem("cheese", "", "", null);
		cheese.setQuantity(2);
		cheese.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(cheese);
		final StackableItem bread = new StackableItem("bread", "", "", null);
		bread.setQuantity(1);
		bread.setID(new ID(1, ZONE_NAME));
		player.getSlot("bag").add(bread);
		final StackableItem ham = new StackableItem("ham", "", "", null);
		ham.setID(new ID(3, ZONE_NAME));
		player.getSlot("bag").add(ham);
		assertEquals(2, player.getNumberOfEquipped("cheese"));
		assertEquals(1, player.getNumberOfEquipped("bread"));
		assertEquals(1, player.getNumberOfEquipped("ham"));

		en.step(player, "make");
		assertTrue(npc.isTalking());
		assertEquals(
				"I need you to fetch me 1 #'loaf of bread', 1 #'piece of ham', and 2 #'pieces of cheese' for this job. Do you have it?",
				getReply(npc));
		en.step(player, "yes");
		final String[] questStatus = player.getQuest(QUEST).split(";");
		final String[] expected = { "1", "sandwich", "" };
		assertEquals("amount", expected[0], questStatus[0]); 
		assertEquals("item", expected[1], questStatus[1]); 

		assertTrue(npc.isTalking());
		assertEquals(
				"OK, I will make 1 sandwich for you, but that will take some time. Please come back in 3 minutes.",
				getReply(npc));
		assertEquals(0, player.getNumberOfEquipped("cheese"));
		assertEquals(0, player.getNumberOfEquipped("bread"));
		assertEquals(0, player.getNumberOfEquipped("ham"));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		player.setQuest(QUEST, "1;;0");

		en.step(player, "hi");
		assertEquals(
				"Welcome back! I'm done with your order. Here you have 1 sandwich.",
				getReply(npc));
		assertEquals(1, player.getNumberOfEquipped("sandwich"));
	}

	@Test
	public void testHiAndMakeWithStuffMultiple() {
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.",
				getReply(npc));
		final StackableItem cheese = new StackableItem("cheese", "", "", null);
		cheese.setQuantity(4);
		cheese.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(cheese);
		final StackableItem bread = new StackableItem("bread", "", "", null);
		bread.setQuantity(2);
		bread.setID(new ID(1, ZONE_NAME));
		player.getSlot("bag").add(bread);
		final StackableItem ham = new StackableItem("ham", "", "", null);
		ham.setQuantity(2);
		ham.setID(new ID(3, ZONE_NAME));
		player.getSlot("bag").add(ham);
		assertEquals(4, player.getNumberOfEquipped("cheese"));
		assertEquals(2, player.getNumberOfEquipped("bread"));
		assertEquals(2, player.getNumberOfEquipped("ham"));

		en.step(player, "make 2 sandwiches");
		assertTrue(npc.isTalking());
		assertEquals(
				"I need you to fetch me 2 #'loaves of bread', 2 #'pieces of ham', and 4 #'pieces of cheese' for this job. Do you have it?",
				getReply(npc));
		en.step(player, "yes");
		final String[] questStatus = player.getQuest(QUEST).split(";");
		final String[] expected = { "2", "sandwich", "" };
		assertEquals("amount", expected[0], questStatus[0]);
		assertEquals("item", expected[1], questStatus[1]); 

		assertTrue(npc.isTalking());
		assertEquals(
				"OK, I will make 2 sandwiches for you, but that will take some time. Please come back in 6 minutes.",
				getReply(npc));
		assertEquals(0, player.getNumberOfEquipped("cheese"));
		assertEquals(0, player.getNumberOfEquipped("bread"));
		assertEquals(0, player.getNumberOfEquipped("ham"));
		en.step(player, "bye");
		assertFalse(npc.isTalking());
		player.setQuest(QUEST, "2;;0");

		en.step(player, "hi");
		assertEquals(
				"Welcome back! I'm done with your order. Here you have 2 sandwiches.",
				getReply(npc));
		assertEquals(2, player.getNumberOfEquipped("sandwich"));
	}

	@Test
	public void testMultipleWithoutName() {
		en.step(player, "hi");
		assertTrue(npc.isTalking());
		assertEquals(
				"Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.",
				getReply(npc));
		final StackableItem cheese = new StackableItem("cheese", "", "", null);
		cheese.setQuantity(6);
		cheese.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(cheese);
		final StackableItem bread = new StackableItem("bread", "", "", null);
		bread.setQuantity(3);
		bread.setID(new ID(1, ZONE_NAME));
		player.getSlot("bag").add(bread);
		final StackableItem ham = new StackableItem("ham", "", "", null);
		ham.setQuantity(10);
		ham.setID(new ID(3, ZONE_NAME));
		player.getSlot("bag").add(ham);
		assertEquals(6, player.getNumberOfEquipped("cheese"));
		assertEquals(3, player.getNumberOfEquipped("bread"));
		assertEquals(10, player.getNumberOfEquipped("ham"));

		en.step(player, "make 3");
		assertTrue(npc.isTalking());
		assertEquals(
				"I need you to fetch me 3 #'loaves of bread', 3 #'pieces of ham', and 6 #'pieces of cheese' for this job. Do you have it?",
				getReply(npc));
		en.step(player, "yes");
		final String[] questStatus = player.getQuest(QUEST).split(";");
		final String[] expected = { "3", "sandwich", "" };
		assertEquals("amount", expected[0], questStatus[0]);
		assertEquals("item", expected[1], questStatus[1]); 

		assertTrue(npc.isTalking());
		assertEquals(
				"OK, I will make 3 sandwiches for you, but that will take some time. Please come back in 9 minutes.",
				getReply(npc));
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
				getReply(npc));
		assertEquals(3, player.getNumberOfEquipped("sandwich"));
	}

}
