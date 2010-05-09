package games.stendhal.server.entity.npc.action;

import static org.junit.Assert.assertEquals;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.server.game.db.DatabaseFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class IncrementQuestActionTest {
	
	private static String questSlot = "test_slot";
	
	@BeforeClass
	public static void beforeClass() {
		Log4J.init();
		MockStendlRPWorld.get();
		new DatabaseFactory().initializeDatabase();
	}

	@Test
	public void testIncrement() {
		Player player = PlayerTestHelper.createPlayer("bob");
		player.setQuest(questSlot, "1");
		assertEquals("1", player.getQuest(questSlot));
		IncrementQuestAction action = new IncrementQuestAction(questSlot,1);
		action.fire(player, null, null);
		assertEquals("2", player.getQuest(questSlot));
	}
	
	@Test
	public void testIncrementIndex() {
		Player player = PlayerTestHelper.createPlayer("bob");
		player.setQuest(questSlot, "test;10");
		assertEquals("test;10", player.getQuest(questSlot));
		IncrementQuestAction action = new IncrementQuestAction(questSlot,1,5);
		action.fire(player, null, null);
		assertEquals("test;15", player.getQuest(questSlot));
	}

}
