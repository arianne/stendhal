package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.bank.CustomerAdvisorNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.ItemTestHelper;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class ArmorForDagobertTest {
	private Player player;
	private SpeakerNPC npc;
	private Engine en;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}
	@Before
	public void setUp() {
		npc = new SpeakerNPC("Dagobert");
		NPCList.get().add(npc);
		SpeakerNPCFactory npcConf = new CustomerAdvisorNPC();
		npcConf.createDialog(npc);

		AbstractQuest quest = new ArmorForDagobert();
		quest.addToWorld();
		en = npc.getEngine();
		
		player = PlayerTestHelper.createPlayer("player");
	}

	@Test
	public void testQuest() {
		en.step(player, "hi");
		assertEquals("Welcome to the bank of Semos! Do you need #help on your personal chest?", npc.get("text"));
		en.step(player, "no");
		en.step(player, "task");
		assertEquals("I'm so afraid of being robbed. I don't have any protection. Do you think you can help me?", npc.get("text"));
		en.step(player, "no");
		assertEquals("Well, then I guess I'll just duck and cover.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("It was a pleasure to serve you.", npc.get("text"));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Welcome to the bank of Semos! Do you need #help on your personal chest?", npc.get("text"));
		en.step(player, "task");
		assertEquals("I'm so afraid of being robbed. I don't have any protection. Do you think you can help me?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Once I had a nice #leather_cuirass, but it was destroyed during the last robbery. If you find a new one, I'll give you a reward.", npc.get("text"));
		en.step(player, "leather");
		assertEquals("A leather_cuirass is the traditional cyclops armor. Some cyclopes are living in the dungeon deep under the city.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("It was a pleasure to serve you.", npc.get("text"));

		// -----------------------------------------------
		Item item = ItemTestHelper.createItem("leather_cuirass");
		player.getSlot("bag").add(item);

		en.step(player, "hi");
		assertEquals("Excuse me, please! I have noticed the leather_cuirass you're carrying. Is it for me?", npc.get("text"));
		en.step(player, "no");
		assertEquals("Well then, I hope you find another one which you can give to me before I get robbed again.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("It was a pleasure to serve you.", npc.get("text"));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Excuse me, please! I have noticed the leather_cuirass you're carrying. Is it for me?", npc.get("text"));
		// put it out of bag onto ground, then say yes.
		player.drop("leather_cuirass");
		assertFalse(player.isEquipped("leather_cuirass"));
		npc.remove("text");
		en.step(player, "yes");
		// he doesn't do anything.
		assertFalse(npc.has("text"));
		en.step(player, "bye");
		assertEquals("It was a pleasure to serve you.", npc.get("text"));

		// -----------------------------------------------

		player.getSlot("bag").add(item);
		en.step(player, "hi");
		assertEquals("Excuse me, please! I have noticed the leather_cuirass you're carrying. Is it for me?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Oh, I am so thankful! Here is some gold I found ... ehm ... somewhere.", npc.get("text"));
		// [23:08] rosie earns 50 experience points. 
		en.step(player, "task");
		assertEquals("Thank you very much for the armor, but I don't have any other task for you.", npc.get("text"));
	}
}
