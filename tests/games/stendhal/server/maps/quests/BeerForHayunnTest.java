package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.rule.defaultruleset.DefaultEntityManager;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.semos.guardhouse.RetiredAdventurerNPC;

import java.util.LinkedList;
import java.util.List;

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
		Player player = PlayerTestHelper.createPlayer("player");

		(new MockRetiredAdventurer()).createDialog(hayunn);
		Engine en = hayunn.getEngine();
		en.step(player, "hi");
		assertTrue(player.isQuestCompleted("meet_hayunn"));
		assertTrue(hayunn.isTalking());
		assertEquals(
				"Hi again, how can I #help you this time?",
				hayunn.get("text"));
		en.step(player, "quest");
		assertEquals(
				"My mouth is dry, but I can't be seen to abandon this teaching room! Could you bring me some #beer from the #tavern?",
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
		Player player2 = PlayerTestHelper.createPlayer("player");

		en.step(player2, "hi");
		assertTrue(player2.isQuestCompleted("meet_hayunn"));
		assertTrue(hayunn.isTalking());
		assertEquals(
				"Hi again, how can I #help you this time?",
				hayunn.get("text"));
		en.step(player2, "quest");
		assertEquals(
				"My mouth is dry, but I can't be seen to abandon this teaching room! Could you bring me some #beer from the #tavern?",
				hayunn.get("text"));
		en.step(player2, "no");
		assertTrue(player2.hasQuest("beer_hayunn"));
		assertEquals("rejected", player2.getQuest("beer_hayunn"));
		en.step(player2, "bye");
	}
	@Test
	public void testgetHistory() {
		Player player = PlayerTestHelper.createPlayer("bob");
		assertTrue(bfh.getHistory(player).isEmpty());
		player.setQuest("beer_hayunn", "");
		List<String> history = new LinkedList<String>();
		history.add("FIRST_CHAT");
		assertEquals(history, bfh.getHistory(player));
		
		player.setQuest("beer_hayunn", "rejected");
		history.add("QUEST_REJECTED");
		assertEquals(history, bfh.getHistory(player));
	
		player.setQuest("beer_hayunn", "start");
		history.remove("QUEST_REJECTED");
		history.add("QUEST_ACCEPTED");
		assertEquals(history, bfh.getHistory(player));

		player.equip(DefaultEntityManager.getInstance().getItem("beer"));
		history.add("FOUND_ITEM");
		assertEquals(history, bfh.getHistory(player));
		player.setQuest("beer_hayunn", "done");
		history.add("DONE");
		assertEquals(history, bfh.getHistory(player));

	}

	@Test
	public void testinit() {
		BeerForHayunn quest = new BeerForHayunn();
		quest.init("bla");
		assertEquals("bla", quest.getName());
	}
	

}
