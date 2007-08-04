package games.stendhal.server.actions;

import static org.junit.Assert.*;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;


import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AdministrationActionTest {

	@Before
	public void setUp() throws Exception {
		MockStendlRPWorld.get();
		MockStendhalRPRuleProcessor.get();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testRegister() {
		AdministrationAction.register();
	}

	@Test
	public final void testRegisterCommandLevel() {

	}

	@Test
	public final void testGetLevelForCommand() {
		assertEquals(-1, AdministrationAction.getLevelForCommand(""));
		assertEquals(0, AdministrationAction.getLevelForCommand("adminlevel"));
		assertEquals(100, AdministrationAction.getLevelForCommand("support"));
		assertEquals(50, AdministrationAction.getLevelForCommand("supportanswer"));
		assertEquals(200, AdministrationAction.getLevelForCommand("tellall"));
		assertEquals(300, AdministrationAction.getLevelForCommand("teleportto"));
		assertEquals(400, AdministrationAction.getLevelForCommand("teleport"));
		assertEquals(400, AdministrationAction.getLevelForCommand("jail"));
		assertEquals(400, AdministrationAction.getLevelForCommand("gag"));
		assertEquals(500, AdministrationAction.getLevelForCommand("invisible"));
		assertEquals(500, AdministrationAction.getLevelForCommand("ghostmode"));
		assertEquals(500, AdministrationAction.getLevelForCommand("teleclickmode"));
		assertEquals(600, AdministrationAction.getLevelForCommand("inspect"));
		assertEquals(700, AdministrationAction.getLevelForCommand("destroy"));
		assertEquals(800, AdministrationAction.getLevelForCommand("summon"));
		assertEquals(800, AdministrationAction.getLevelForCommand("summonat"));
		assertEquals(900, AdministrationAction.getLevelForCommand("alter"));
		assertEquals(900, AdministrationAction.getLevelForCommand("altercreature"));
		assertEquals(5000, AdministrationAction.getLevelForCommand("super"));

	}

	@Test
	public final void testIsPlayerAllowedToExecuteAdminCommand() {
		Player pl = new Player(new RPObject());
		assertFalse(AdministrationAction.isPlayerAllowedToExecuteAdminCommand(pl,"",true));
		assertEquals("Sorry, command \"\" is unknown.", pl.get("private_text"));
		assertTrue(AdministrationAction.isPlayerAllowedToExecuteAdminCommand(pl,"adminlevel",true));
		pl.remove("private_text");

		assertEquals(false, AdministrationAction.isPlayerAllowedToExecuteAdminCommand(pl,"support",true));
		assertEquals("Sorry, you need to be an admin to run \"support\".", pl.get("private_text"));

		pl.put("adminlevel",50);
		pl.remove("private_text");
		assertEquals(true, AdministrationAction.isPlayerAllowedToExecuteAdminCommand(pl,"adminlevel",true));
		assertEquals(false, AdministrationAction.isPlayerAllowedToExecuteAdminCommand(pl,"support",true));
		assertEquals("Your admin level is only 50, but a level of 100 is required to run \"support\".", pl.get("private_text"));
		assertEquals(true, AdministrationAction.isPlayerAllowedToExecuteAdminCommand(pl,"supportanswer",true));
	}

	@Ignore
	@Test  //TODO this is a testing flaw fails because pl is not added to players in ruleprocessor
	public final void testOnAction() {
		AdministrationAction aa = new AdministrationAction();
		Player pl = new Player(new RPObject());
		aa.onAction(pl,new RPAction());
		assertEquals("Sorry, command \"null\" is unknown.", pl.get("private_text"));
		pl = new Player(new RPObject());

		pl.put("adminlevel", 5000);
		RPAction action = new RPAction();
		action.put("type","tellall");
		aa.onAction(pl,action);
		action.put("text", "blabla");
		aa.onAction(pl,action);

		assertEquals("Sorry, command \"null\" is unknown.", pl.get("private_text"));
	}

}
