package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.fado.weaponshop.RingSmithNPC;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.ItemTestHelper;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class RingMakerTest {

	private static final String QUEST_SLOT = "fix_emerald_ring";
	private static Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		StendhalRPZone zone = new StendhalRPZone("admin_test");
		new RingSmithNPC().configureZone(zone, null);

		AbstractQuest quest = new RingMaker();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("player");

		zone.add(player);
		player.setPosition(52, 53);
		MockStendhalRPRuleProcessor.get().addPlayer(player);
	}

	@AfterClass
	public static void afterClass() {
		PlayerTestHelper.removePlayer(player);
	}

	@Test
	public void testOrderEmeraldRing() {

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
		Item item = ItemTestHelper.createItem("emerald_ring");
		player.getSlot("bag").add(item);
		assertTrue(en.step(player, "hi"));
		assertEquals("Hi! Can I #help you?", npc.get("text"));
		assertTrue(en.step(player, "help"));
		assertEquals("I am an expert on #wedding rings and #emerald rings, sometimes called the ring of #life.", npc.get("text"));
		assertTrue(en.step(player, "emerald"));
		assertEquals("I see you already have an emerald ring. If it gets broken, you can come to me to fix it.", npc.get("text"));

	// break the ring don't give them money and make them lie that they have it
		item.put("amount",0);
		assertTrue(en.step(player, "emerald"));
		assertEquals("What a pity, your emerald ring is broken. I can fix it, for a #price.", npc.get("text"));
		assertTrue(en.step(player, "price"));
		assertEquals("The charge for my service is 80000 money, and I need 2 gold bars and 1 emerald to fix the ring. Do you want to pay now?", npc.get("text"));
		assertTrue(en.step(player, "yes"));
		assertEquals("Come back when you have both the money, the gem and the gold. Goodbye", npc.get("text"));
		assertTrue(en.step(player, "bye"));

		// -----------------------------------------------
		// this time say no they don't want to pay yet
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

		// -----------------------------------------------
		// this time say yes and give them the stuff
		item = ItemTestHelper.createItem("money", 80000);
		player.getSlot("bag").add(item);
		item = ItemTestHelper.createItem("gold bar", 2);
		player.getSlot("bag").add(item);
		item = ItemTestHelper.createItem("emerald", 1);
		player.getSlot("bag").add(item);

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


		// -----------------------------------------------


	}

	@Test
	public void testFetchOrderedEmeraldRing() {
		npc = NPCList.get().get("Ognir");
		en = npc.getEngine();

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
		en.step(player, "emerald");
		assertEquals("I'm pleased to say, your ring of life is fixed! It's good as new now.", npc.get("text"));
		// player earns 500 experience points.
		en.step(player, "bye");
		assertEquals("Bye, my friend.", npc.get("text"));

	}

}
