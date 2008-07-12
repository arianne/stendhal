package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.amazon.hut.PrincessNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

public class AmazonPrincessTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		final ZoneConfigurator zoneConf = new PrincessNPC();
		zoneConf.configureZone(new StendhalRPZone("admin_test"), null);
		npc = SingletonRepository.getNPCList().get("Princess Esclara");
		en = npc.getEngine();

		final AbstractQuest quest = new AmazonPrincess();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("player");
	}

	@Test
	public void testQuest() {
		en.step(player, "hi");
		assertEquals("Huh, what are you doing here?", npc.get("text"));
		en.step(player, "help");
		assertEquals("Beware of my sisters on the island, they do not like strangers.", npc.get("text"));
		en.step(player, "task");
		assertEquals("I'm looking for a drink, should be an exotic one. Can you bring me one?", npc.get("text"));
		en.step(player, "yes");
		assertEquals("Thank you! If you have found some, say #drink to me so I know you have it. I'll be sure to give you a nice reward.", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Goodbye, and beware of the barbarians.", npc.get("text"));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Huh, what are you doing here?", npc.get("text"));
		en.step(player, "task");
		assertEquals("I like these exotic drinks, I forget the name of my favourite one.", npc.get("text"));
		en.step(player, "help");
		assertEquals("Beware of my sisters on the island, they do not like strangers.", npc.get("text"));
		en.step(player, "pinacolada");
		assertEquals("You don't have any drink I like yet. Go, and you better get an exotic one!", npc.get("text"));
		en.step(player, "exotic drink");
		en.step(player, "pinacolad");
		assertEquals("You don't have any drink I like yet. Go, and you better get an exotic one!", npc.get("text"));
		en.step(player, "help");
		assertEquals("Beware of my sisters on the island, they do not like strangers.", npc.get("text"));
		en.step(player, "favor");
		assertEquals("I like these exotic drinks, I forget the name of my favourite one.", npc.get("text"));
		en.step(player, "pinacolada");
		assertEquals("You don't have any drink I like yet. Go, and you better get an exotic one!", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Goodbye, and beware of the barbarians.", npc.get("text"));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Huh, what are you doing here?", npc.get("text"));
		en.step(player, "help");
		assertEquals("Beware of my sisters on the island, they do not like strangers.", npc.get("text"));
		en.step(player, "quest");
		assertEquals("I like these exotic drinks, I forget the name of my favourite one.", npc.get("text"));
		en.step(player, "done");
		en.step(player, "drink");
		assertEquals("You don't have any drink I like yet. Go, and you better get an exotic one!", npc.get("text"));
		en.step(player, "bye");
		assertEquals("Goodbye, and beware of the barbarians.", npc.get("text"));

		// -----------------------------------------------
		final Item item = ItemTestHelper.createItem("pina colada");
		player.getSlot("bag").add(item);

		en.step(player, "hi");
		assertEquals("Huh, what are you doing here?", npc.get("text"));
		en.step(player, "pina colada");
		assertTrue(npc.get("text").startsWith("Thank you!! Take these "));
		assertTrue(player.isEquipped("fish pie"));
		en.step(player, "bye");
		assertEquals("Goodbye, and beware of the barbarians.", npc.get("text"));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Huh, what are you doing here?", npc.get("text"));
		en.step(player, "task");
		assertTrue(npc.get("text").startsWith("I'm sure I'll be too drunk to have another for at least"));
		en.step(player, "bye");
		assertEquals("Goodbye, and beware of the barbarians.", npc.get("text"));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Huh, what are you doing here?", npc.get("text"));
		en.step(player, "pina colada");
		assertEquals("Sometime you could do me a #favour ...", npc.get("text"));
		en.step(player, "favour");
		assertTrue(npc.get("text").startsWith("I'm sure I'll be too drunk to have another for at least "));
		en.step(player, "bye");
		assertEquals("Goodbye, and beware of the barbarians.", npc.get("text"));
	}
}
