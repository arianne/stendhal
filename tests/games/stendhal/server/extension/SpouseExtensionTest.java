package games.stendhal.server.extension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.PrivateTextMockingTestPlayer;

/**
 * JUnit Tests for SpouseExtension.
 * 
 * @author Martin Fuchs
 */
public class SpouseExtensionTest {

	private static final String ZONE_NAME = "testzone";

	@BeforeClass
	public static final void setUpClass() throws Exception {
		Log4J.init();
		assertTrue(MockStendhalRPRuleProcessor.get() instanceof MockStendhalRPRuleProcessor);
		MockStendlRPWorld.get();
		new SpouseExtension();
	}

	@Before
	public final void setup() {
		StendhalRPZone zone = new StendhalRPZone(ZONE_NAME);
		MockStendlRPWorld.get().addRPZone(zone);

		Player pl1 = PlayerTestHelper.createPlayer("player1");
		PlayerTestHelper.registerPlayer(pl1, zone);

		Player pl2 = PlayerTestHelper.createPlayer("player2");
		PlayerTestHelper.registerPlayer(pl2, zone);
	}

	@After
	public final void tearDown() {
		PlayerTestHelper.removePlayer("player2", ZONE_NAME);
		PlayerTestHelper.removePlayer("player1", ZONE_NAME);
		PlayerTestHelper.removeZone(ZONE_NAME);
	}

	@Test
	public final void testMagic() {
		StendhalRPWorld world = MockStendlRPWorld.get();
		StendhalRPZone zone = world.getZone(ZONE_NAME);

		PrivateTextMockingTestPlayer admin = PlayerTestHelper.createPrivateTextMockingTestPlayer("admin");
		admin.setAdminLevel(400);
		PlayerTestHelper.registerPlayer(admin, zone);

		RPAction action = new RPAction();
		action.put("type", "marry");
		assertTrue(CommandCenter.execute(admin, action));
		assertEquals("Usage: #/marry #<player1> #<player2>",
				admin.getPrivateTextString());
		admin.resetPrivateTextString();

		action = new RPAction();
		action.put("type", "marry");
		action.put("target", "player1");
		action.put("args", "player2");
		assertTrue(CommandCenter.execute(admin, action));
		assertEquals(
				"You have successfully married \"player1\" and \"player2\".",
				admin.getPrivateTextString());
		admin.resetPrivateTextString();

		assertTrue(CommandCenter.execute(admin, action));
		assertEquals(
				"player1 is already married to player2. player2 is already married to player1.",
				admin.getPrivateTextString());
		admin.resetPrivateTextString();
	}
}
