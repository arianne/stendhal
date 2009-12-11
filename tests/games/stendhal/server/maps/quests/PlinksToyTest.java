package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.Arrays;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.semos.plains.LittleBoyNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;
import utilities.RPClass.PassiveEntityRespawnPointTestHelper;

public class PlinksToyTest {
	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		PassiveEntityRespawnPointTestHelper.generateRPClasses();
	}
	
	@Before
	public void setUp() {
		MockStendlRPWorld.get().addRPZone(new StendhalRPZone("0_semos_plains_n"));

		npc = new SpeakerNPC("Plink");
		SingletonRepository.getNPCList().add(npc);
		final LittleBoyNPC npcConf = new LittleBoyNPC();
		npcConf.createDialog(npc);

		final PlinksToy quest = new PlinksToy();
		quest.addToWorld();
		en = npc.getEngine();

		player = PlayerTestHelper.createPlayer("player");
	}

	@Test
	public void testQuest() {
		en.step(player, "hi");
		assertEquals("*cries* There were wolves in the #park! *sniff* I ran away, but I dropped my #teddy! Please will you get it for me? *sniff* Please?", getReply(npc));
		en.step(player, "park!");
		assertEquals("My parents told me not to go to the park by myself, but I got lost when I was playing... Please don't tell them! Can you bring my #teddy back?", getReply(npc));
		en.step(player, "yes");
		assertEquals("*sniff* Thanks a lot! *smile*", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("*cries* There were wolves in the #park! *sniff* I ran away, but I dropped my #teddy! Please will you get it for me? *sniff* Please?", getReply(npc));
		en.step(player, "teddy");
		assertEquals("Teddy is my favourite toy! Please will you bring him back?", getReply(npc));
		en.step(player, "yes");
		assertEquals("*sniff* Thanks a lot! *smile*", getReply(npc));

		// -----------------------------------------------
		
		final Item teddy = ItemTestHelper.createItem("teddy");
		teddy.setEquipableSlots(Arrays.asList("bag"));
		player.equipToInventoryOnly(teddy);
		assertTrue(player.isEquipped("teddy"));

		System.out.println(player.getSlot("!quests"));
		System.out.println(player.getSlot("lhand"));
		System.out.println(player.getSlot("rhand"));

		en.step(player, "hi");
		// [21:25] player earns 10 experience points.
		assertEquals("You found him! *hugs teddy* Thank you, thank you! *smile*", getReply(npc));

		assertFalse(player.isEquipped("teddy"));

		en.step(player, "help");
		assertEquals("Be careful out east, there are wolves about!", getReply(npc));
		en.step(player, "job");
		assertEquals("I play all day.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}
}
