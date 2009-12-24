package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
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
		public void createDialog(final SpeakerNPC npc) {
			super.createDialog(npc);
		}

	}

	private static SpeakerNPC hayunn;

	private static BeerForHayunn bfh;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		
		MockStendhalRPRuleProcessor.get();

		MockStendlRPWorld.reset();
		MockStendlRPWorld.get();
		hayunn = new SpeakerNPC("Hayunn Naratha");
		SingletonRepository.getNPCList().add(hayunn);

		bfh = new BeerForHayunn();

		bfh.addToWorld();
	}

	@Before
	public void setup() {
		PlayerTestHelper.removeAllPlayers();
	}

	@Test
	public void quest() {
	
		final Player player = PlayerTestHelper.createPlayer("player");

		(new MockRetiredAdventurer()).createDialog(hayunn);
		final Engine en = hayunn.getEngine();
		en.step(player, "hi");
		// we assume the player has already completed the meet hayunn quest
		// so that we know which of the greetings he will use
		player.setQuest("meet_hayunn", "done");
		assertTrue(player.isQuestCompleted("meet_hayunn"));
		assertTrue(hayunn.isTalking());
		assertEquals(
				"Hi. I bet you've been sent here to learn about adventuring from me. First, lets see what you're made of. Go and kill a rat outside, you should be able to find one easily. Do you want to learn how to attack it, before you go?",
				getReply(hayunn));
		en.step(player, "quest");
		assertEquals(
				"My mouth is dry, but I can't be seen to abandon this teaching room! Could you bring me some #beer from the #tavern?",
				getReply(hayunn));
		en.step(player, "yes");
		assertTrue(player.hasQuest("beer_hayunn"));
		en.step(player, "bye");
		assertFalse(hayunn.isTalking());
		assertEquals("start", player.getQuest("beer_hayunn"));
		final StackableItem beer = new StackableItem("beer", "", "", null);
		beer.setQuantity(1);
		beer.setID(new ID(2, "testzone"));
		player.getSlot("bag").add(beer);
		assertEquals(1, player.getNumberOfEquipped("beer"));
		en.step(player, "hi");
		en.step(player, "yes");
		assertEquals("done", player.getQuest("beer_hayunn"));
		en.step(player, "bye");
		// reject
		final Player player2 = PlayerTestHelper.createPlayer("player");

		en.step(player2, "hi");
		player2.setQuest("meet_hayunn", "done");
		assertTrue(player2.isQuestCompleted("meet_hayunn"));
		assertTrue(hayunn.isTalking());
		assertEquals(
			"Hi. I bet you've been sent here to learn about adventuring from me. First, lets see what you're made of. Go and kill a rat outside, you should be able to find one easily. Do you want to learn how to attack it, before you go?",
				getReply(hayunn));
		en.step(player2, "quest");
		assertEquals(
				"My mouth is dry, but I can't be seen to abandon this teaching room! Could you bring me some #beer from the #tavern?",
				getReply(hayunn));
		en.step(player2, "no");
		assertTrue(player2.hasQuest("beer_hayunn"));
		assertEquals("rejected", player2.getQuest("beer_hayunn"));
		en.step(player2, "bye");
	}

	/**
	 * Tests for getHistory.
	 */
	@Test
	public void testgetHistory() {
		final Player player = PlayerTestHelper.createPlayer("bob");
		assertTrue(bfh.getHistory(player).isEmpty());
		player.setQuest("beer_hayunn", "");
		final List<String> history = new LinkedList<String>();
		history.add("FIRST_CHAT");
		assertEquals(history, bfh.getHistory(player));
		
		player.setQuest("beer_hayunn", "rejected");
		history.add("QUEST_REJECTED");
		assertEquals(history, bfh.getHistory(player));
	
		player.setQuest("beer_hayunn", "start");
		history.remove("QUEST_REJECTED");
		history.add("QUEST_ACCEPTED");
		assertEquals(history, bfh.getHistory(player));

		player.equipToInventoryOnly(SingletonRepository.getEntityManager().getItem("beer"));
		history.add("FOUND_ITEM");
		assertEquals(history, bfh.getHistory(player));
		player.setQuest("beer_hayunn", "done");
		history.add("DONE");
		assertEquals(history, bfh.getHistory(player));

	}



}
