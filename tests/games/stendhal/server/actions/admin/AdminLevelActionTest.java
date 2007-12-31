package games.stendhal.server.actions.admin;

import static org.junit.Assert.assertEquals;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.PrivateTextMockingTestPlayer;

public class AdminLevelActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		MockStendhalRPRuleProcessor.get().clearPlayers();
		Log4J.init();
		AdminLevelAction.register();
	}
	@After
	public void tearDown() throws Exception {
		MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	@Test
	public final void testAdminLevelAction0() {
		PrivateTextMockingTestPlayer pl = PlayerTestHelper.createPrivateTextMockingTestPlayer("player");
		PrivateTextMockingTestPlayer bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		MockStendhalRPRuleProcessor.get().addPlayer(bob);
	
		pl.put("adminlevel", 5000);
	
		RPAction action = new RPAction();
		action.put("type", "adminlevel");
		action.put("target", "bob");
		action.put("newlevel", "0");
		CommandCenter.execute(pl, action);
		assertEquals("Changed adminlevel of bob from 0 to 0.", pl
				.getPrivateTextString());
		assertEquals("player changed your adminlevel from 0 to 0.", bob
				.getPrivateTextString());
	}

	@Test
	public final void testAdminLevelActioncasterNotSuper() {
		PrivateTextMockingTestPlayer pl = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		pl.put("adminlevel", 4999);
	
		MockStendhalRPRuleProcessor.get().addPlayer(pl);
	
		RPAction action = new RPAction();
		action.put("type", "adminlevel");
		action.put("target", "bob");
		action.put("newlevel", "0");
		CommandCenter.execute(pl, action);
		assertEquals(
				"Sorry, but you need an adminlevel of 5000 to change adminlevel.",
				pl.getPrivateTextString());
	}

	@Test
	public final void testAdminLevelActionOverSuper() {
		PrivateTextMockingTestPlayer pl = PlayerTestHelper.createPrivateTextMockingTestPlayer("player");
		PrivateTextMockingTestPlayer bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		// bad bad
		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		MockStendhalRPRuleProcessor.get().addPlayer(bob);
	
		pl.put("adminlevel", 5000);
	
		RPAction action = new RPAction();
		action.put("type", "adminlevel");
		action.put("target", "bob");
		action.put("newlevel", "5001");
		CommandCenter.execute(pl, action);
		assertEquals("Changed adminlevel of bob from 0 to 5000.", pl
				.getPrivateTextString());
		assertEquals(5000, pl.getAdminLevel());
		assertEquals(5000, bob.getAdminLevel());
		assertEquals("player changed your adminlevel from 0 to 5000.", bob
				.getPrivateTextString());
	}

	@Test
	public final void testAdminLevelActionPlayerFound() {
		PrivateTextMockingTestPlayer pl = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		pl.put("adminlevel", 5000);
	
		MockStendhalRPRuleProcessor.get().addPlayer(pl);
	
		RPAction action = new RPAction();
		action.put("type", "adminlevel");
		action.put("target", "bob");
		CommandCenter.execute(pl, action);
		assertEquals("bob has adminlevel 5000", pl.getPrivateTextString());
	}

	@Test
	public final void testAdminLevelActionPlayerFoundNoInteger() {
		PrivateTextMockingTestPlayer pl = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		pl.put("adminlevel", 5000);
	
		MockStendhalRPRuleProcessor.get().addPlayer(pl);
	
		RPAction action = new RPAction();
		action.put("type", "adminlevel");
		action.put("target", "bob");
		action.put("newlevel", "1.3");
		CommandCenter.execute(pl, action);
		assertEquals("The new adminlevel needs to be an Integer", pl
				.getPrivateTextString());
	}

	@Test
	public final void testAdminLevelActionPlayerNotFound() {
		PrivateTextMockingTestPlayer pl = PlayerTestHelper.createPrivateTextMockingTestPlayer("player");
		pl.put("adminlevel", 5000);
		RPAction action = new RPAction();
		action.put("type", "adminlevel");
		action.put("target", "bob");
		CommandCenter.execute(pl, action);
	
		assertEquals("Player \"bob\" not found", pl.getPrivateTextString());
	}

}
