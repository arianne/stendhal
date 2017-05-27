package games.stendhal.server.entity.npc.condition;


import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;
/**
 * Tests for PlayerHasItemEquippedInSlot
 *
 * @author madmetzger
 */
public class PlayerHasItemEquippedInSlotTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MockStendlRPWorld.reset();
	}

	/**
	 * Positive test
	 *
	 * @throws Exception
	 */
	@Test
	public void testSuccess() throws Exception {
		final Player player = PlayerTestHelper.createPlayer("bob");
		PlayerTestHelper.equipWithItemToSlot(player, "axe", "rhand");
		final PlayerHasItemEquippedInSlot condition = new PlayerHasItemEquippedInSlot("axe", "rhand");
		assertThat(condition.fire(player, null, null), is(Boolean.TRUE));
	}

	/**
	 * Test if condition returns false on empty player
	 *
	 * @throws Exception
	 */
	@Test
	public void testFail() throws Exception {
		final Player player = PlayerTestHelper.createPlayer("bob");
		final PlayerHasItemEquippedInSlot condition = new PlayerHasItemEquippedInSlot("axe", "rhand");
		assertThat(condition.fire(player, null, null), is(Boolean.FALSE));
	}

}
