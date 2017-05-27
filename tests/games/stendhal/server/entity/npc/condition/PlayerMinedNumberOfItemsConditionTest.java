package games.stendhal.server.entity.npc.condition;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;
/**
 * Tests for PlayerMinedNumberOfItemsCondition
 *
 * @author madmetzger
 *
 */
public class PlayerMinedNumberOfItemsConditionTest {

	@Before
	public void setUp() throws Exception {
		MockStendlRPWorld.get();
	}

	@After
	public void tearDown() throws Exception {
		MockStendlRPWorld.reset();
	}

	@Test
	public void testEqualsHashCode() {
		PlayerMinedNumberOfItemsCondition actual = new PlayerMinedNumberOfItemsCondition(1, "gold");
		assertThat(actual.toString(), is("player has mined <1 of [gold]>"));
		assertThat(actual, is(actual));
		assertThat(actual.hashCode(), is(actual.hashCode()));
		assertThat(actual, is(new PlayerMinedNumberOfItemsCondition(1, "gold")));
		assertThat(actual.hashCode(), is(new PlayerMinedNumberOfItemsCondition(1, "gold").hashCode()));
		assertThat(actual, not(is(new PlayerMinedNumberOfItemsCondition(1, "flour"))));
	}

	@Test
	public void testFire() {
		PlayerMinedNumberOfItemsCondition condition = new PlayerMinedNumberOfItemsCondition(5, "gold");
		Player player = PlayerTestHelper.createPlayer("miner");
		assertThat(condition.fire(player, null, null), is(false));
		player.incMinedForItem("iron", 12);
		assertThat(condition.fire(player, null, null), is(false));
		player.incMinedForItem("gold", 4);
		assertThat(condition.fire(player, null, null), is(false));
		player.incMinedForItem("gold", 2);
		assertThat(condition.fire(player, null, null), is(true));
	}

}
