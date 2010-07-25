package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.player.Player;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import utilities.PlayerTestHelper;

/**
 * Tests for the {@link FinishedQuestGreaterOrEqualThanCondition}
 * 
 * @author madmetzger
 */
public class FinishedQuestGreaterOrEqualThanConditionTest {
	
	private static Player player;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		player = PlayerTestHelper.createPlayer("testplayer");
		player.setQuest("testquest", "done;5");
	}
	
	@Test
	public void testNotOftenEnoughFinished() {
		FinishedQuestGreaterOrEqualThanCondition c = new FinishedQuestGreaterOrEqualThanCondition("testquest", 10, 1);
		assertFalse(c.fire(player, null, null));
	}
	
	@Test
	public void testExactlyMatched() {
		FinishedQuestGreaterOrEqualThanCondition c = new FinishedQuestGreaterOrEqualThanCondition("testquest", 5, 1);
		assertTrue(c.fire(player, null, null));
	}
	
	@Test
	public void testMoreThanNeeded() {
		FinishedQuestGreaterOrEqualThanCondition c = new FinishedQuestGreaterOrEqualThanCondition("testquest", 3, 1);
		assertTrue(c.fire(player, null, null));
	}

}
