package games.stendhal.server.extension;

import static org.junit.Assert.assertTrue;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

/**
 * JUnit Tests for MagicExtn.
 * @author Martin Fuchs
 */
public class MagicExtnTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		Log4J.init();
		MockStendhalRPRuleProcessor.get();
		MockStendlRPWorld.get();
		new MagicExtn();
	}

	@After
	public void tearDown() {
		PlayerTestHelper.removePlayer("player");
	}

	/**
	 * Tests for magic.
	 */
	@Test
	public final void testMagic() {
		final Player pl = PlayerTestHelper.createPlayer("player");

		final RPAction action = new RPAction();
		action.put("type", "spell");
		action.put("target", "player");
		assertTrue(CommandCenter.execute(pl, action));

		assertTrue(true);
	}
}
