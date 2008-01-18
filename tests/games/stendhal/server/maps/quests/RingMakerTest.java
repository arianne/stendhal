package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.defaultruleset.DefaultEntityManager;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.fado.weaponshop.RingSmithNPC;


import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.ItemTestHelper;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class RingMakerTest {

	private static final String QUEST_SLOT = "fix_emerald_ring";
	
	private static SpeakerNPC npc;
	private static Engine en;
	private Player player;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		StendhalRPZone zone = new StendhalRPZone("admin_test");
		RingSmithNPC ognir = new RingSmithNPC();
		ognir.configureZone(zone, null);

		AbstractQuest quest = new RingMaker();
		quest.addToWorld();
		npc = NPCList.get().get("Ognir");
		en = npc.getEngine();
	}

	@AfterClass
	public static void tearDownAftereClass() throws Exception {
		 npc = null;
		 en = null;
	}

	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
		en.setCurrentState(ConversationStates.IDLE);
	}
	
	@Test
	public void testHiandBye() throws Exception {
		en.step(player, "hi");
		assertEquals("Hi! Can I #help you?", npc.get("text"));
		assertTrue(en.step(player, "bye"));
		assertEquals("Bye, my friend.", npc.get("text"));	
		
	}
	
	
	@Test
	public void testOrderEmeraldRingWithoutEnoughmoney() {
		
		// **at ringsmith**
		npc = NPCList.get().get("Ognir");
		en = npc.getEngine();
		assertTrue(en.step(player, "hi"));
		assertEquals("Hi! Can I #help you?", npc.get("text"));
		assertTrue(en.step(player, "help"));
		assertEquals("I am an expert on #wedding rings and #emerald rings, sometimes called the ring of #life.", npc.get("text"));
		assertTrue(en.step(player, "emerald"));
		assertEquals("It is difficult to get the ring of life. Do a favour for a powerful elf in Nal'wor and you may receive one as a reward.", npc.get("text"));
		assertTrue(en.step(player, "bye"));
		// -----------------------------------------------
		Item item = DefaultEntityManager.getInstance().getItem("emerald_ring");
		player.getSlot("bag").add(item);
		assertTrue(en.step(player, "hi"));
		assertEquals("Hi! Can I #help you?", npc.get("text"));
		assertTrue(en.step(player, "help"));
		assertEquals("I am an expert on #wedding rings and #emerald rings, sometimes called the ring of #life.", npc.get("text"));
		assertTrue(en.step(player, "emerald"));
		assertEquals("I see you already have an emerald ring. If it gets broken, you can come to me to fix it.", npc.get("text"));

	// break the ring don't give them money and make them lie that they have it
		item.put("amount", 0);
		assertTrue(en.step(player, "emerald"));
		assertEquals("What a pity, your emerald ring is broken. I can fix it, for a #price.", npc.get("text"));
		assertTrue(en.step(player, "price"));
		assertEquals("The charge for my service is 80000 money, and I need 2 gold bars and 1 emerald to fix the ring. Do you want to pay now?", npc.get("text"));
		assertTrue(en.step(player, "yes"));
		assertEquals("Come back when you have the money, the gem and the gold. Goodbye.", npc.get("text"));
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		
	}
	
	@Test
	public void testOrderEmeraldRingDeny() {
		
		// -----------------------------------------------
		// this time say no they don't want to pay yet
		npc = NPCList.get().get("Ognir");
		en = npc.getEngine();
		Item item = DefaultEntityManager.getInstance().getItem("emerald_ring");
		item.put("amount", 0);
		player.getSlot("bag").add(item);
		assertTrue(en.step(player, "hi"));
		assertEquals("Hi! Can I #help you?", npc.get("text"));
		assertTrue(en.step(player, "help"));
		assertEquals("I am an expert on #wedding rings and #emerald rings, sometimes called the ring of #life.", npc.get("text"));
		assertTrue(en.step(player, "emerald"));
		assertEquals("What a pity, your emerald ring is broken. I can fix it, for a #price.", npc.get("text"));
		assertTrue(en.step(player, "price"));
		assertEquals("The charge for my service is 80000 money, and I need 2 gold bars and 1 emerald to fix the ring. Do you want to pay now?", npc.get("text"));
		assertTrue(en.step(player, "no"));
		assertEquals("No problem, just come back when you have the money, the emerald, and the gold.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye, my friend.", npc.get("text"));
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
	}

	@Test
	public void testOrderEmeraldRing() {
		
		Item item = DefaultEntityManager.getInstance().getItem("emerald_ring");
		item.put("amount", 0);
		player.getSlot("bag").add(item);
		
		Item money = ItemTestHelper.createItem("money", 80000);
		player.getSlot("bag").add(money);
		Item goldbar = ItemTestHelper.createItem("gold bar", 2);
		player.getSlot("bag").add(goldbar);
		Item emerald = ItemTestHelper.createItem("emerald", 1);
		player.getSlot("bag").add(emerald);

		assertTrue(en.step(player, "hi"));
		assertEquals("Hi! Can I #help you?", npc.get("text"));
		assertTrue(en.step(player, "help"));
		assertEquals("I am an expert on #wedding rings and #emerald rings, sometimes called the ring of #life.", npc.get("text"));
		assertTrue(en.step(player, "emerald"));
		assertEquals("What a pity, your emerald ring is broken. I can fix it, for a #price.", npc.get("text"));
		assertTrue(en.step(player, "price"));
		assertEquals("The charge for my service is 80000 money, and I need 2 gold bars and 1 emerald to fix the ring. Do you want to pay now?", npc.get("text"));		
		assertTrue(en.step(player, "yes"));
		assertEquals("Okay, that's all I need to fix the ring. Come back in 10 minutes and it will be ready. Bye for now.", npc.get("text"));

		assertTrue(player.getQuest(QUEST_SLOT).startsWith("forging"));
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
	}

	@Test
	public void testFetchOrderedEmeraldRing() {
		
		
		player.setQuest("fix_emerald_ring", "forging;" + Long.MAX_VALUE);
		
		en.step(player, "hi");
		assertEquals("Hi! Can I #help you?", npc.get("text"));
		en.step(player, "help");
		assertEquals("I am an expert on #wedding rings and #emerald rings, sometimes called the ring of #life.", npc.get("text"));
		en.step(player, "emerald");
		assertTrue(npc.get("text").startsWith("I haven't finished fixing your ring of life. Please check back"));
		en.step(player, "bye");

		// Jump relatively forward in time (by pushing the past events to the beginning of time
		
		assertTrue(player.getQuest("fix_emerald_ring").startsWith("forging;"));
		player.setQuest("fix_emerald_ring", "forging;1");
		
		en.step(player, "hi");
		assertEquals("Hi! Can I #help you?", npc.get("text"));
		en.step(player, "help");
		assertEquals("I am an expert on #wedding rings and #emerald rings, sometimes called the ring of #life.", npc.get("text"));
		int oldXP = player.getXP();
		en.step(player, "emerald");
		assertEquals("I'm pleased to say, your ring of life is fixed! It's good as new now.", npc.get("text"));
		assertEquals("player earns 500 experience points.", oldXP + 500, player.getXP());
		en.step(player, "bye");
		assertEquals("Bye, my friend.", npc.get("text"));

	}

}
