package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.semos.city.RetiredAdventurerNPC;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject.ID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class BeerForHayunnTest {
	private class MockRetiredAdventurer extends RetiredAdventurerNPC {

		@Override
		public void createDialog(SpeakerNPC npc) {

			super.createDialog(npc);
		}

	}

	private static SpeakerNPC hayunn;

	private static BeerForHayunn bfh;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		assertTrue(MockStendhalRPRuleProcessor.get() instanceof MockStendhalRPRuleProcessor);
		MockStendlRPWorld.get();
		hayunn = new SpeakerNPC("Hayunn Naratha");
		NPCList.get().add(hayunn);

		bfh = new BeerForHayunn();

		bfh.addToWorld();
	}

	@Before
	public void setup() {

	}

	@Test
	public void quest() {
		Player player = PlayerTestHelper.createPlayer();

		(new MockRetiredAdventurer()).createDialog(hayunn);
		Engine en = hayunn.getEngine();
		en.step(player, "hi");
		assertTrue(hayunn.isTalking());
		assertEquals(
				"You've probably heard of me; Hayunn Naratha, a retired adventurer. Have you read my book? No? It's called \"Know How To Kill Creatures\". Maybe we could talk about adventuring, if you like?",
				hayunn.get("text"));
		en.step(player, "quest");
		assertEquals(
				"My mouth is dry, but I can't be seen to abandon my post! Could you bring me some #beer from the #tavern?",
				hayunn.get("text"));
		en.step(player, "yes");
		assertTrue(player.hasQuest("beer_hayunn"));
		en.step(player, "bye");
		assertFalse(hayunn.isTalking());
		assertEquals("start", player.getQuest("beer_hayunn"));
		StackableItem beer = new StackableItem("beer", "", "", null);
		beer.setQuantity(1);
		beer.setID(new ID(2, "testzone"));
		player.getSlot("bag").add(beer);
		assertEquals(1, player.getNumberOfEquipped("beer"));
		en.step(player, "hi");
		en.step(player, "yes");
		assertEquals("done", player.getQuest("beer_hayunn"));
		en.step(player, "bye");
		// reject
		Player player2 = PlayerTestHelper.createPlayer();

		en.step(player2, "hi");
		assertTrue(hayunn.isTalking());
		assertEquals(
				"You've probably heard of me; Hayunn Naratha, a retired adventurer. Have you read my book? No? It's called \"Know How To Kill Creatures\". Maybe we could talk about adventuring, if you like?",
				hayunn.get("text"));
		en.step(player2, "quest");
		assertEquals(
				"My mouth is dry, but I can't be seen to abandon my post! Could you bring me some #beer from the #tavern?",
				hayunn.get("text"));
		en.step(player2, "no");
		assertTrue(player2.hasQuest("beer_hayunn"));
		assertEquals("rejected", player2.getQuest("beer_hayunn"));
		en.step(player2, "bye");
	}

}
