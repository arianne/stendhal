package games.stendhal.server.actions.admin;

import static org.junit.Assert.*;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;

import marauroa.common.game.RPAction;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class GhostModeActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
	}

	@Test
	public final void testGhostmode() {
		Player hugo = PlayerTestHelper.createPlayer("hugo");
		Player bob = PlayerTestHelper.createPlayer("bob");
		hugo.put("adminlevel", 5000);
		MockStendhalRPRuleProcessor.get().addPlayer(hugo);
		MockStendhalRPRuleProcessor.get().addPlayer(bob);
		bob.setKeyedSlot("!buddy", "_" + hugo.getName(), "1");

		RPAction action = new RPAction();

		action.put("type", "ghostmode");
		assertFalse(hugo.isInvisible());
		assertFalse(hugo.isGhost());

		CommandCenter.execute(hugo, action);

		assertTrue(hugo.isInvisible());
		assertTrue(hugo.isGhost());

		assertEquals(null, bob.get("online"));

		assertEquals("hugo", bob.get("offline"));
		bob.remove("offline");
		bob.clearEvents();
		CommandCenter.execute(hugo, action);

		assertFalse(hugo.isInvisible());
		assertFalse(hugo.isGhost());
		assertEquals(null, bob.get("offline"));
		assertEquals("hugo", bob.get("online"));
	}


}
