package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.semos.plains.LittleBoyNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.ItemTestHelper;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class PlinksToyTest {
	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}
	
	@Before
	public void setUp() {
		MockStendlRPWorld.get().addRPZone(new StendhalRPZone("0_semos_plains_n"));

		npc = new SpeakerNPC("Plink");
		NPCList.get().add(npc);
		LittleBoyNPC npcConf = new LittleBoyNPC();
		npcConf.createDialog(npc);

		PlinksToy quest = new PlinksToy();
		quest.addToWorld();
		en = npc.getEngine();
		
		player = PlayerTestHelper.createPlayer();
	}

	@Test
	public void testQuest() {
		en.step(player, "hi");
		assertEquals("*cries* There were wolves in the #park! *sniff* I ran away, but I dropped my #teddy! Please will you get it for me? *sniff* Please?", npc.get("text"));
		en.step(player, "park!");
		assertEquals("My parents told me not to go to the park by myself, but I got lost when I was playing... Please don't tell them! Can you bring my #teddy back?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("*sniff* Thanks a lot! *smile*", npc.get("text"));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("*cries* There were wolves in the #park! *sniff* I ran away, but I dropped my #teddy! Please will you get it for me? *sniff* Please?", npc.get("text"));
		en.step(player, "teddy");
		assertEquals("Teddy is my favourite toy! Please will you bring him back?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("*sniff* Thanks a lot! *smile*", npc.get("text"));

		// -----------------------------------------------
		
		Item teddy = ItemTestHelper.createItem("teddy");
		// TODO: why does this not work? player.equip(teddy);
		player.getSlot("bag").add(teddy);
		assertTrue(player.isEquipped("teddy"));

		System.out.println(player.getSlot("!quests"));
		System.out.println(player.getSlot("lhand"));
		System.out.println(player.getSlot("rhand"));

		en.step(player, "hi");
		// [21:25] player earns 10 experience points.
		assertEquals("You found him! *hugs teddy* Thank you, thank you! *smile*", npc.get("text"));

		assertFalse(player.isEquipped("teddy"));

		en.step(player, "help");
		assertEquals("Be careful out east, there are wolves about!", npc.get("text"));
		en.step(player, "job");
		assertEquals("I play all day.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));
	}
}