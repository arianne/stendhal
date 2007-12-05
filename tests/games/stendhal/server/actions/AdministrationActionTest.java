package games.stendhal.server.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerHelper;
import utilities.PlayerTestHelper;

public class AdministrationActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AdministrationAction.register();
		MockStendlRPWorld.get();
		MockStendhalRPRuleProcessor.get().getPlayers().clear();
		Log4J.init();
	}

	@After
	public void tearDown() throws Exception {
		MockStendhalRPRuleProcessor.get().getPlayers().clear();
	}

	@Test
	public final void testRegisterCommandLevel() {
	}

	@Test
	public final void testGetLevelForCommand() {
		assertEquals(-1, AdministrationAction.getLevelForCommand(""));
		assertEquals(0, AdministrationAction.getLevelForCommand("adminlevel"));
		assertEquals(100, AdministrationAction.getLevelForCommand("support"));
		assertEquals(50, AdministrationAction
				.getLevelForCommand("supportanswer"));
		assertEquals(200, AdministrationAction.getLevelForCommand("tellall"));
		assertEquals(300, AdministrationAction.getLevelForCommand("teleportto"));
		assertEquals(400, AdministrationAction.getLevelForCommand("teleport"));
		assertEquals(400, AdministrationAction.getLevelForCommand("jail"));
		assertEquals(400, AdministrationAction.getLevelForCommand("gag"));
		assertEquals(500, AdministrationAction.getLevelForCommand("invisible"));
		assertEquals(500, AdministrationAction.getLevelForCommand("ghostmode"));
		assertEquals(500, AdministrationAction
				.getLevelForCommand("teleclickmode"));
		assertEquals(600, AdministrationAction.getLevelForCommand("inspect"));
		assertEquals(700, AdministrationAction.getLevelForCommand("destroy"));
		assertEquals(800, AdministrationAction.getLevelForCommand("summon"));
		assertEquals(800, AdministrationAction.getLevelForCommand("summonat"));
		assertEquals(900, AdministrationAction.getLevelForCommand("alter"));
		assertEquals(900, AdministrationAction
				.getLevelForCommand("altercreature"));
		assertEquals(5000, AdministrationAction.getLevelForCommand("super"));
	}

	@Test
	public final void testIsPlayerAllowedToExecuteAdminCommand() {
		Player pl = PlayerTestHelper.createPlayer();
		assertFalse(AdministrationAction.isPlayerAllowedToExecuteAdminCommand(
				pl, "", true));
		// assertEquals("Sorry, command \"\" is unknown.",
		// pl.get("private_text"));
		assertTrue(AdministrationAction.isPlayerAllowedToExecuteAdminCommand(
				pl, "adminlevel", true));
		pl.clearEvents();

		assertEquals(false, AdministrationAction
				.isPlayerAllowedToExecuteAdminCommand(pl, "support", true));
		assertEquals("Sorry, you need to be an admin to run \"support\".", pl
				.get("private_text"));

		pl.put("adminlevel", 50);
		pl.clearEvents();
		assertEquals(true, AdministrationAction
				.isPlayerAllowedToExecuteAdminCommand(pl, "adminlevel", true));
		assertEquals(false, AdministrationAction
				.isPlayerAllowedToExecuteAdminCommand(pl, "support", true));
		assertEquals(
				"Your admin level is only 50, but a level of 100 is required to run \"support\".",
				pl.get("private_text"));
		assertEquals(true,
				AdministrationAction.isPlayerAllowedToExecuteAdminCommand(pl,
						"supportanswer", true));
	}

	@Test
	public final void testTellAllAction() {

		AdministrationAction aa = new AdministrationAction();

		Player pl = PlayerTestHelper.createPlayer();
		// bad bad
		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);
		aa.onAction(pl, new RPAction());
		// assertEquals("Sorry, command \"null\" is unknown.", pl
		// .get("private_text"));

		pl.clearEvents();
		pl.put("adminlevel", 5000);
		RPAction action = new RPAction();
		action.put("type", "tellall");
		action.put("text", "huhu");
		aa.onAction(pl, action);
		assertEquals("Administrator SHOUTS: huhu", pl.get("private_text"));
	}

	@Test
	public final void testSupportAnswerAction() {

		AdministrationAction aa = new AdministrationAction();

		Player pl = PlayerTestHelper.createPlayer();
		Player bob = PlayerTestHelper.createPlayer("bob");
		// bad bad
		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);
		MockStendhalRPRuleProcessor.get().getPlayers().add(bob);

		pl.put("adminlevel", 5000);
		RPAction action = new RPAction();
		action.put("type", "supportanswer");
		action.put("text", "huhu");
		action.put("target", "bob");
		aa.onAction(pl, action);
		assertEquals("Support (player) tells you: huhu", bob
				.get("private_text"));
	}

	@Test
	public final void testTeleportActionToInvalidZone() {

		AdministrationAction aa = new AdministrationAction();

		Player pl = PlayerTestHelper.createPlayer();
		Player bob = PlayerTestHelper.createPlayer("bob");
		// bad bad
		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);
		MockStendhalRPRuleProcessor.get().getPlayers().add(bob);

		pl.put("adminlevel", 5000);
		RPAction action = new RPAction();
		action.put("type", "teleport");
		action.put("text", "huhu");
		action.put("target", "bob");
		action.put("zone", "non-existing-zone");
		action.put("x", "0");
		action.put("y", "0");

		assertTrue(action.has("target") && action.has("zone")
				&& action.has("x"));

		aa.onAction(pl, action);

		// The list of existing zones depends on other tests, so we simply
		// ignore it here.
		assertTrue(pl
				.get("private_text")
				.startsWith(
						"Zone \"IRPZone.ID [id=non-existing-zone]\" not found. Valid zones: ["));
	}

	@Test
	public final void testTeleportActionToValidZone() {

		AdministrationAction aa = new AdministrationAction();
		StendhalRPZone zoneTo = new StendhalRPZone("zoneTo");
		Player pl = PlayerTestHelper.createPlayer();
		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);
		PlayerHelper.generatePlayerRPClasses();
		Player bob = new Player(new RPObject()) {
			@Override
			public boolean teleport(StendhalRPZone zone, int x, int y,
					Direction dir, Player teleporter) {
				assertEquals("zoneTo", zone.getName());
				// added hack to have something to verify
				setName("hugo");
				return true;

			}
		};
		bob.setName("bob");
		PlayerHelper.addEmptySlots(bob);

		MockStendhalRPRuleProcessor.get().getPlayers().add(bob);

		MockStendlRPWorld.get().addRPZone(zoneTo);
		pl.put("adminlevel", 5000);
		RPAction action = new RPAction();
		action.put("type", "teleport");
		action.put("text", "huhu");
		action.put("target", "bob");
		action.put("zone", "zoneTo");
		action.put("x", "0");
		action.put("y", "0");

		assertTrue(action.has("target") && action.has("zone")
				&& action.has("x"));

		aa.onAction(pl, action);
		assertEquals("hugo", bob.getName());
	}

	@Test
	public final void testTeleportToActionPlayerNotThere() {

		AdministrationAction aa = new AdministrationAction();
		Player pl = PlayerTestHelper.createPlayer();
		pl.put("adminlevel", 5000);
		RPAction action = new RPAction();
		action.put("type", "teleportto");
		action.put("target", "blah");
		aa.onAction(pl, action);
		assertEquals("Player \"blah\" not found", pl.get("private_text"));

	}

	@Test
	public final void testTeleportToActionPlayerThere() {

		AdministrationAction aa = new AdministrationAction();
		Player pl = PlayerTestHelper.createPlayer();
		pl.setName("blah");

		pl.put("adminlevel", 5000);

		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);
		StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(pl);
		RPAction action = new RPAction();
		action.put("type", "teleportto");
		action.put("target", "blah");
		aa.onAction(pl, action);
		assertEquals("Position [0,0] is occupied", pl.get("private_text"));
	}

	@Test
	public final void testAdminLevelActionPlayerNotFound() {

		AdministrationAction aa = new AdministrationAction();

		Player pl = PlayerTestHelper.createPlayer();
		pl.put("adminlevel", 5000);
		RPAction action = new RPAction();
		action.put("type", "adminlevel");
		action.put("target", "bob");
		aa.onAction(pl, action);
		assertEquals("Player \"bob\" not found", pl.get("private_text"));
	}

	@Test
	public final void testAdminLevelActionPlayerFound() {

		AdministrationAction aa = new AdministrationAction();

		Player pl = PlayerTestHelper.createPlayer("bob");
		pl.put("adminlevel", 5000);

		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);

		RPAction action = new RPAction();
		action.put("type", "adminlevel");
		action.put("target", "bob");
		aa.onAction(pl, action);
		assertEquals("bob has adminlevel 5000", pl.get("private_text"));
	}

	@Test
	public final void testAdminLevelActionPlayerFoundNoInteger() {

		AdministrationAction aa = new AdministrationAction();

		Player pl = PlayerTestHelper.createPlayer("bob");
		pl.put("adminlevel", 5000);

		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);

		RPAction action = new RPAction();
		action.put("type", "adminlevel");
		action.put("target", "bob");
		action.put("newlevel", "1.3");
		aa.onAction(pl, action);
		assertEquals("The new adminlevel needs to be an Integer", pl
				.get("private_text"));
	}

	@Test
	public final void testAdminLevelAction0() {

		AdministrationAction aa = new AdministrationAction();
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
		aa.onAction(pl, action);
		assertEquals("Changed adminlevel of bob from 0 to 0.", pl
				.get("private_text"));
		assertEquals("player changed your adminlevel from 0 to 0.", bob
				.get("private_text"));
	}

	@Test
	public final void testAdminLevelActioncasterNotSuper() {

		AdministrationAction aa = new AdministrationAction();

		Player pl = PlayerTestHelper.createPlayer("bob");
		pl.put("adminlevel", 4999);

		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);

		RPAction action = new RPAction();
		action.put("type", "adminlevel");
		action.put("target", "bob");
		action.put("newlevel", "0");
		aa.onAction(pl, action);
		assertEquals(
				"Sorry, but you need an adminlevel of 5000 to change adminlevel.",
				pl.get("private_text"));
	}

	@Test
	public final void testOnAlterActionWrongAttribute() {

		AdministrationAction aa = new AdministrationAction();

		Player pl = PlayerTestHelper.createPlayer("bob");
		pl.put("adminlevel", 5000);

		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);

		RPAction action = new RPAction();
		action.put("type", "alter");
		action.put("target", "bob");
		action.put("stat", "0");
		action.put("mode", "0");
		action.put("value", 0);

		aa.onAction(pl, action);
		assertEquals(
				"Attribute you are altering is not defined in RPClass(player)",
				pl.get("private_text"));
	}

	@Test
	public final void testOnAlterAction() {

		AdministrationAction aa = new AdministrationAction();

		Player pl = PlayerTestHelper.createPlayer("bob");
		pl.put("adminlevel", 5000);

		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);

		RPAction action = new RPAction();
		action.put("type", "alter");
		action.put("target", "bob");
		action.put("stat", "name");
		action.put("mode", "0");
		action.put("value", 0);

		aa.onAction(pl, action);
		assertEquals("Sorry, name cannot be changed.", pl.get("private_text"));
		action.put("stat", "adminlevel");
		pl.clearEvents();
		aa.onAction(pl, action);
		assertEquals(
				"Use #/adminlevel #<playername> #[<newlevel>] to display or change adminlevel.",
				pl.get("private_text"));
	}

	@Test
	public final void testOnAlterActionTitle() {

		AdministrationAction aa = new AdministrationAction();

		Player pl = PlayerTestHelper.createPlayer("bob");
		pl.put("adminlevel", 5000);

		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);

		RPAction action = new RPAction();
		action.put("type", "alter");
		action.put("target", "bob");
		action.put("stat", "title");
		action.put("mode", "0");
		action.put("value", 0);

		aa.onAction(pl, action);
		assertEquals("The title attribute may not be changed directly.", pl
				.get("private_text"));
	}

	@Test
	public final void testOnAlterActionHP() {

		AdministrationAction aa = new AdministrationAction();

		Player pl = PlayerTestHelper.createPlayer("bob");
		pl.put("adminlevel", 5000);
		pl.put("base_hp", 100);
		pl.setHP(100);
		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);

		RPAction action = new RPAction();
		action.put("type", "alter");
		action.put("target", "bob");
		action.put("stat", "hp");
		action.put("mode", "0");
		action.put("value", 0);
		assertEquals(100, pl.getHP());

		aa.onAction(pl, action);
		assertEquals("may not change HP to 0 ", 100, pl.getHP());

		action.put("value", 120);
		aa.onAction(pl, action);
		assertEquals("may  not change HP over base_hp", 100, pl.getHP());

		action.put("value", 90);
		aa.onAction(pl, action);
		assertEquals("may  change HP to 90 ", 90, pl.getHP());

		action.put("value", 90);
		action.put("mode", "sub");
		assertEquals("may  change HP to 90 ", 90, pl.getHP());
	}

	@Test
	public final void testOnAlterActionHPsub() {

		AdministrationAction aa = new AdministrationAction();

		Player pl = PlayerTestHelper.createPlayer("bob");
		pl.put("adminlevel", 5000);
		pl.put("base_hp", 100);
		pl.setHP(100);
		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);

		RPAction action = new RPAction();
		action.put("type", "alter");
		action.put("target", "bob");
		action.put("stat", "hp");
		action.put("mode", "sub");
		action.put("value", 90);
		assertEquals(100, pl.getHP());

		aa.onAction(pl, action);
		assertEquals(10, pl.getHP());
		aa.onAction(pl, action);
		assertEquals(-80, pl.getHP());
	}

	@Test
	public final void testOnAlterActionHPadd() {

		AdministrationAction aa = new AdministrationAction();

		Player pl = PlayerTestHelper.createPlayer("bob");
		pl.put("adminlevel", 5000);
		pl.put("base_hp", 100);
		pl.setHP(10);
		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);

		RPAction action = new RPAction();
		action.put("type", "alter");
		action.put("target", "bob");
		action.put("stat", "hp");
		action.put("mode", "add");
		action.put("value", 80);
		assertEquals(10, pl.getHP());

		aa.onAction(pl, action);
		assertEquals(90, pl.getHP());
		aa.onAction(pl, action);
		assertEquals(90, pl.getHP());
	}

	@Test
	public final void testAlterCreatureEntityNotFound() {

		AdministrationAction aa = new AdministrationAction();

		Player pl = PlayerTestHelper.createPlayer("hugo");

		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);

		pl.put("adminlevel", 5000);
		RPAction action = new RPAction();
		action.put("type", "altercreature");
		action.put("target", "bob");
		action.put("text", "blabla");

		aa.onAction(pl, action);
		assertEquals("Entity not found", pl.get("private_text"));
	}

	@Test
	public final void testSummonAlterCreature() {

		AdministrationAction aa = new AdministrationAction();

		Player pl = PlayerTestHelper.createPlayer("hugo");

		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);
		StendhalRPZone zone = new StendhalRPZone("testzone") {
			@Override
			public synchronized boolean collides(Entity entity, double x,
					double y) {

				return false;
			}
		};
		zone.add(pl);
		pl.setPosition(1, 1);
		pl.put("adminlevel", 5000);
		RPAction action = new RPAction();
		action.put("type", "summon");
		action.put("creature", "rat");
		action.put("x", 0);
		action.put("y", 0);
		aa.onAction(pl, action);
		assertEquals(1, pl.getID().getObjectID());
		Creature rat = (Creature) zone.getEntityAt(0, 0);
		assertEquals("rat", rat.get("subclass"));

		action = new RPAction();
		action.put("type", "altercreature");
		action.put("target", "#2");
		// must be of type "name/atk/def/hp/xp",
		action.put("text", "name/5/6/7/8");

		aa.onAction(pl, action);

		assertEquals("name", "name", rat.getName());
		assertEquals("atk", 5, rat.getATK());
		assertEquals("def", 6, rat.getDEF());
		assertEquals("hp", 7, rat.getHP());
		assertEquals("xp", 8, rat.getXP());

	}

	@Test
	public final void testInvisible() {
		AdministrationAction aa = new AdministrationAction();

		Player pl = PlayerTestHelper.createPlayer("hugo");
		pl.put("adminlevel", 5000);
		RPAction action = new RPAction();
		action.put("type", "invisible");
		assertFalse(pl.isInvisible());
		aa.onAction(pl, action);
		assertTrue(pl.isInvisible());
		aa.onAction(pl, action);
		assertFalse(pl.isInvisible());

	}

	@Test
	public final void testGhostmode() {
		AdministrationAction aa = new AdministrationAction();

		Player pl = PlayerTestHelper.createPlayer("hugo");
		Player bob = PlayerTestHelper.createPlayer("bob");
		pl.put("adminlevel", 5000);
		MockStendhalRPRuleProcessor.get().getPlayers().add(pl);
		MockStendhalRPRuleProcessor.get().getPlayers().add(bob);
		bob.setKeyedSlot("!buddy", "_" + pl.getName(), "1");

		RPAction action = new RPAction();

		action.put("type", "ghostmode");
		assertFalse(pl.isInvisible());
		assertFalse(pl.isGhost());

		aa.onAction(pl, action);
		assertTrue(pl.isInvisible());
		assertTrue(pl.isGhost());

		assertEquals(null, bob.get("online"));

		assertEquals("hugo", bob.get("offline"));
		bob.remove("offline");
		bob.clearEvents();
		aa.onAction(pl, action);
		assertFalse(pl.isInvisible());
		assertFalse(pl.isGhost());
		assertEquals(null, bob.get("offline"));
		assertEquals("hugo", bob.get("online"));

	}

	@Test
	public final void testTeleclickmode() {
		AdministrationAction aa = new AdministrationAction();

		Player pl = PlayerTestHelper.createPlayer("hugo");
		pl.put("adminlevel", 5000);
		RPAction action = new RPAction();
		action.put("type", "teleclickmode");
		assertFalse(pl.isTeleclickEnabled());
		aa.onAction(pl, action);
		assertTrue(pl.isTeleclickEnabled());
		aa.onAction(pl, action);
		assertFalse(pl.isTeleclickEnabled());

	}

}
