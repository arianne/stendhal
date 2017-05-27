package games.stendhal.server.entity.npc.condition;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;

/**
 * Tests for the PlayerManaGreaterThanCondition
 *
 * @author madmetzger
 */
public class PlayerManaGreaterThanConditionTest {

	@Before
	public void setUp() {
		MockStendlRPWorld.get();
	}

	@After
	public void tearDown() {
		MockStendlRPWorld.reset();
	}

	@Test
	public void testFireSuccessful() {
		Player player = PlayerTestHelper.createPlayer("mana-enough");
		player.setBaseMana(1000);
		player.setMana(1);
		assertThat(new PlayerManaGreaterThanCondition(0).fire(player, null, null), is(Boolean.TRUE));
		player.setMana(1000);
		assertThat(new PlayerManaGreaterThanCondition(5).fire(player, null, null), is(Boolean.TRUE));
	}

	@Test
	public void testFireFailure() {
		Player player = PlayerTestHelper.createPlayer("mana-too-less");
		player.setBaseMana(1000);
		player.setMana(0);
		assertThat(new PlayerManaGreaterThanCondition(0).fire(player, null, null), is(Boolean.FALSE));
		player.setMana(4);
		assertThat(new PlayerManaGreaterThanCondition(5).fire(player, null, null), is(Boolean.FALSE));
	}

}
