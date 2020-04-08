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
 * Tests for {@link PlayerProducedNumberOfItemsCondition}
 *
 * @author madmetzger
 */
public class PlayerProducedNumberOfItemsConditionTest {

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
		PlayerProducedNumberOfItemsCondition actual = new PlayerProducedNumberOfItemsCondition(1, "flour");
		assertThat(actual.toString(), is("player has produced <1 of [flour]>"));
		assertThat(actual, is(actual));
		assertThat(actual.hashCode(), is(actual.hashCode()));
		assertThat(actual, is(new PlayerProducedNumberOfItemsCondition(1, "flour")));
		assertThat(actual.hashCode(), is(new PlayerProducedNumberOfItemsCondition(1, "flour").hashCode()));
		assertThat(actual, not(is(new PlayerProducedNumberOfItemsCondition(1, "iron"))));
	}

	@Test
	public void testFire() {
		PlayerProducedNumberOfItemsCondition condition = new PlayerProducedNumberOfItemsCondition(5, "flour");
		Player player = PlayerTestHelper.createPlayer("producer");
		assertThat(condition.fire(player, null, null), is(false));
		player.incProducedForItem("iron", 12);
		assertThat(condition.fire(player, null, null), is(false));
		player.incProducedForItem("flour", 4);
		assertThat(condition.fire(player, null, null), is(false));
		player.incProducedForItem("flour", 2);
		assertThat(condition.fire(player, null, null), is(true));
	}

}
