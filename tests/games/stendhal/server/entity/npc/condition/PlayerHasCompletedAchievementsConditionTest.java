package games.stendhal.server.entity.npc.condition;


import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import utilities.PlayerTestHelper;

/**
 * Test for {@link PlayerHasCompletedAchievementsCondition}
 * 
 * @author madmetzger
 *
 */
public class PlayerHasCompletedAchievementsConditionTest {
	
	private Player player;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		MockStendlRPWorld.get();
	}
	
	public static void tearDownAfterClass() {
		MockStendlRPWorld.reset();
	}
	
	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("achiever");
		player.initReachedAchievements();
	}

	@Test
	public void testFire() throws Exception {
		PlayerHasCompletedAchievementsCondition condition = new PlayerHasCompletedAchievementsCondition("identifier");
		assertThat(condition.fire(player, null, null), is(false));
		player.addReachedAchievement("identifier1");
		assertThat(condition.fire(player, null, null), is(false));
		player.addReachedAchievement("identifier");
		assertThat(condition.fire(player, null, null), is(true));
	}
	
}
