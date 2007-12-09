package games.stendhal.server.extension;

import static org.junit.Assert.assertTrue;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class MagicExtnTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		Log4J.init();
		MockStendhalRPRuleProcessor.get();
		MockStendlRPWorld.get();
		new MagicExtn();
	}

	@Ignore
	@Test
	public final void testMagic() {
		Player pl = PlayerTestHelper.createPlayer("player");

		RPAction action = new RPAction();
		action.put("type", "spell");
		action.put("target", "player");
		assertTrue(CommandCenter.execute(pl, action));

		assertTrue(true);
	}
}
