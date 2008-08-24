package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.goldsmith.GoldsmithNPC;
import games.stendhal.server.maps.semos.blacksmith.BlacksmithNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

public class HungryJoshuaTest {


	//private static String questSlot = "jailedbarb";
	
	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();
		
		final StendhalRPZone zone = new StendhalRPZone("admin_test");

		new GoldsmithNPC().configureZone(zone, null);
		
		SpeakerNPC npc = new SpeakerNPC("Xoderos");
		SingletonRepository.getNPCList().add(npc);
		final SpeakerNPCFactory npcConf = new BlacksmithNPC();
		npcConf.createDialog(npc);

				
		final AbstractQuest quest = new HungryJoshua();
		quest.addToWorld();

	}
	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
	}

	@Test
	public void testQuest() {
		
		npc = SingletonRepository.getNPCList().get("Xoderos");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Greetings. I am sorry to tell you that, because of the war, I am not allowed to sell you any weapons. However, I can #cast iron for you. I can also #offer you tools.", npc.get("text"));
		en.step(player, "task");
		assertEquals("I'm worried about my brother who lives in Ados. I need someone to take some #food to him.", npc.get("text"));
		en.step(player, "food");
		assertEquals("I think five sandwiches would be enough. My brother is called #Joshua. Can you help?", npc.get("text"));
		en.step(player, "joshua");
		assertEquals("He's the goldsmith in Ados. They're so short of supplies. Will you help?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Thank you. Please tell him #food or #sandwich so he knows you're not just a customer.", npc.get("text"));
		en.step(player, "food");
		assertEquals("#Joshua will be getting hungry! Please hurry!", npc.get("text"));
		en.step(player, "joshua");
		assertEquals("My brother, the goldsmith in Ados.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Greetings. I am sorry to tell you that, because of the war, I am not allowed to sell you any weapons. However, I can #cast iron for you. I can also #offer you tools.", npc.get("text"));
		en.step(player, "task");
		assertEquals("Please don't forget the five #sandwiches for #Joshua!", npc.get("text"));
		en.step(player, "sandwiches");
		assertEquals("#Joshua will be getting hungry! Please hurry!", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));

		// -----------------------------------------------


		// -----------------------------------------------


		// -----------------------------------------------
		npc = SingletonRepository.getNPCList().get("Joshua");
		en = npc.getEngine();
		
		Item item = ItemTestHelper.createItem("sandwich", 5);
		player.getSlot("bag").add(item);
		
		en.step(player, "hi");
		assertEquals("Hi! I'm the local goldsmith. If you require me to #cast you a #'gold bar' just tell me!", npc.get("text"));
		en.step(player, "food");
		assertEquals("Oh great! Did my brother Xoderos send you with those sandwiches?", npc.get("text"));
		en.step(player, "yes");
		// [07:28] kymara earns 150 experience points.
		assertEquals("Thank you! Please let Xoderos know that I am fine. Say my name, Joshua, so he knows that you saw me. He will probably give you something in return.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye", npc.get("text"));

		// -----------------------------------------------
		npc = SingletonRepository.getNPCList().get("Xoderos");
		en = npc.getEngine();
		
		en.step(player, "hi");
		assertEquals("Greetings. I am sorry to tell you that, because of the war, I am not allowed to sell you any weapons. However, I can #cast iron for you. I can also #offer you tools.", npc.get("text"));
		en.step(player, "task");
		assertEquals("I do hope #Joshua is well ....", npc.get("text"));
		en.step(player, "food");
		assertEquals("I wish you could confirm for me that #Joshua is fine ...", npc.get("text"));
		en.step(player, "joshua");
		// [07:29] kymara earns 50 experience points.
		assertEquals("I'm glad Joshua is well. Now, what can I do for you? I know, I'll fix that broken key ring that you're carrying ... there, it should work now!", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));
	}
}