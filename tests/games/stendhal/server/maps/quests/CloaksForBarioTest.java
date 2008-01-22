package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ados.outside.CloaksCollectorNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.ItemTestHelper;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class CloaksForBarioTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		ZoneConfigurator zoneConf = new CloaksCollectorNPC();
		zoneConf.configureZone(new StendhalRPZone("admin_test"), null);
		npc = SingletonRepository.getNPCList().get("Bario");

		AbstractQuest quest = new CloaksForBario();
		quest.addToWorld();
		en = npc.getEngine();

		player = PlayerTestHelper.createPlayer("player");
	}

	@Test
	public void testQuest() {
		en.step(player, "hi");
		assertEquals("Hey! How did you get down here? You did what? Huh. Well, I'm Bario. I don't suppose you could do a #task for me?", npc.get("text"));
		en.step(player, "task");
		assertEquals("I don't dare go upstairs anymore because I stole a beer barrel from the dwarves. But it is so cold down here... Can you help me?", npc.get("text"));
		en.step(player, "no");
		assertEquals("Oh dear... I'm going to be in trouble...", npc.get("text"));
		en.step(player, "task");
		assertEquals("I don't dare go upstairs anymore because I stole a beer barrel from the dwarves. But it is so cold down here... Can you help me?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("I need some blue elven cloaks if I'm to survive the winter. Bring me ten of them, and I will give you a reward.", npc.get("text"));
		en.step(player, "ok");
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));

		// -----------------------------------------------
		equipCloaks(4);
		
		en.step(player, "hi");
		assertEquals("Hi again! I still need 10 blue elven cloaks. Do you have any for me?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Thank you very much! Do you have another one? I still need 9 cloaks.", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Thank you very much! Do you have another one? I still need 8 cloaks.", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Thank you very much! Do you have another one? I still need 7 cloaks.", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Thank you very much! Do you have another one? I still need 6 cloaks.", npc.get("text"));
		en.step(player, "no");
		assertEquals("Too bad.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));
		en.step(player, "hi");
		assertEquals("Hi again! I still need 6 blue elven cloaks. Do you have any for me?", npc.get("text"));
		en.step(player, "yes");
		// was lying
		assertEquals("Really? I don't see any...", npc.get("text"));
		en.step(player, "no");
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));

		// -----------------------------------------------
		equipCloaks(6);

		en.step(player, "hi");
		assertEquals("Hi again! I still need 6 blue elven cloaks. Do you have any for me?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Thank you very much! Do you have another one? I still need 5 cloaks.", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Thank you very much! Do you have another one? I still need 4 cloaks.", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Thank you very much! Do you have another one? I still need 3 cloaks.", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Thank you very much! Do you have another one? I still need 2 cloaks.", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Thank you very much! Do you have another one? I still need 1 cloak.", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Thank you very much! Now I have enough cloaks to survive the winter. Here, take this golden shield as a reward.", npc.get("text"));
		// [23:48] superkym earns 1500 experience points. 
		en.step(player, "task");
		assertEquals("I don't have anything for you to do, really.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Welcome! Thanks again for those cloaks.", npc.get("text"));
		en.step(player, "task");
		assertEquals("I don't have anything for you to do, really.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Bye.", npc.get("text"));
	}

	private void equipCloaks(int quantity) {
		for (int i = 0; i < quantity; i++) {
			Item item = ItemTestHelper.createItem("blue elf cloak");
			player.getSlot("bag").add(item);
		}
	}
}
