package games.stendhal.server.actions.admin;

import static org.junit.Assert.*;
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

public class AdminLevelActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		MockStendhalRPRuleProcessor.get().getPlayers().clear();
		Log4J.init();
		AdminLevelAction.register();
	}
	@After
	public void tearDown() throws Exception {
		MockStendhalRPRuleProcessor.get().getPlayers().clear();
	}

	@Test
	public final void testAdminLevelAction0() {
		Player pl = PlayerTestHelper.createPlayer();
		Player bob = PlayerTestHelper.createPlayer("bob");
		// bad bad
		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);
		MockStendhalRPRuleProcessor.get().getPlayers().add(bob);
	
		pl.put("adminlevel", 5000);
	
		RPAction action = new RPAction();
		action.put("type", "adminlevel");
		action.put("target", "bob");
		action.put("newlevel", "0");
		CommandCenter.execute(pl, action);
		assertEquals("Changed adminlevel of bob from 0 to 0.", pl
				.getPrivateText());
		assertEquals("player changed your adminlevel from 0 to 0.", bob
				.getPrivateText());
	}

	@Test
	public final void testAdminLevelActioncasterNotSuper() {
		Player pl = PlayerTestHelper.createPlayer("bob");
		pl.put("adminlevel", 4999);
	
		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);
	
		RPAction action = new RPAction();
		action.put("type", "adminlevel");
		action.put("target", "bob");
		action.put("newlevel", "0");
		CommandCenter.execute(pl, action);
		assertEquals(
				"Sorry, but you need an adminlevel of 5000 to change adminlevel.",
				pl.getPrivateText());
	}

	@Test
	public final void testAdminLevelActionOverSuper() {
		Player pl = PlayerTestHelper.createPlayer();
		Player bob = PlayerTestHelper.createPlayer("bob");
		// bad bad
		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);
		MockStendhalRPRuleProcessor.get().getPlayers().add(bob);
	
		pl.put("adminlevel", 5000);
	
		RPAction action = new RPAction();
		action.put("type", "adminlevel");
		action.put("target", "bob");
		action.put("newlevel", "5001");
		CommandCenter.execute(pl, action);
		assertEquals("Changed adminlevel of bob from 0 to 5000.", pl
				.getPrivateText());
		assertEquals(5000, pl.getAdminLevel());
		assertEquals(5000, bob.getAdminLevel());
		assertEquals("player changed your adminlevel from 0 to 5000.", bob
				.getPrivateText());
	}

	@Test
	public final void testAdminLevelActionPlayerFound() {
	
	
	
		Player pl = PlayerTestHelper.createPlayer("bob");
		pl.put("adminlevel", 5000);
	
		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);
	
		RPAction action = new RPAction();
		action.put("type", "adminlevel");
		action.put("target", "bob");
		CommandCenter.execute(pl, action);
		assertEquals("bob has adminlevel 5000", pl.getPrivateText());
	}

	@Test
	public final void testAdminLevelActionPlayerFoundNoInteger() {
		Player pl = PlayerTestHelper.createPlayer("bob");
		pl.put("adminlevel", 5000);
	
		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);
	
		RPAction action = new RPAction();
		action.put("type", "adminlevel");
		action.put("target", "bob");
		action.put("newlevel", "1.3");
		CommandCenter.execute(pl, action);
		assertEquals("The new adminlevel needs to be an Integer", pl
				.getPrivateText());
	}

	@Test
	public final void testAdminLevelActionPlayerNotFound() {
	
	
		Player pl = PlayerTestHelper.createPlayer();
		pl.put("adminlevel", 5000);
		RPAction action = new RPAction();
		action.put("type", "adminlevel");
		action.put("target", "bob");
		CommandCenter.execute(pl, action);
	
		assertEquals("Player \"bob\" not found", pl.getPrivateText());
	}

}
