package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.semos.plains.LittleBoyNPC;



import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.QuestHelper;
public class PlinkToysTest {
	private Player player = null; // TODO
	private SpeakerNPC npc = null; // TODO
	private Engine en = null; // TODO

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
	}

	@Test
	public void testQuest() {
		en.step(player, "hi");
		assertEquals("*cries* There were wolves in the park! *sniff* I ran away, but I dropped my teddy! Please will you get it for me? *sniff* Please?", npc.get("text"));
		en.step(player, "park!");
		assertEquals("My parents told me not to go to the park by myself, but I got lost when I was playing... Please don't tell them! Can you bring my teddy back?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("*sniff* Thanks a lot! *smile*", npc.get("text"));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("*cries* There were wolves in the park! *sniff* I ran away, but I dropped my teddy! Please will you get it for me? *sniff* Please?", npc.get("text"));
		en.step(player, "teddy");
		assertEquals("Teddy is my favourite toy! Please will you bring him back?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("*sniff* Thanks a lot! *smile*", npc.get("text"));

		// -----------------------------------------------

		en.step(player, "hi");
		// [21:25] hendrikus earns 10 experience points.
		assertEquals("You found him! *hugs teddy* Thank you, thank you! *smile*", npc.get("text"));
		en.step(player, "help");
		assertEquals("Be careful out east, there are wolves about!", npc.get("text"));
		en.step(player, "job");
		assertEquals("I play all day.", npc.get("text"));
		en.step(player, "quest");
		en.step(player, "toy");
		en.step(player, "teddy");
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));
	}
}