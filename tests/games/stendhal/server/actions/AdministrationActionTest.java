package games.stendhal.server.actions;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import games.stendhal.common.Direction;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.actions.admin.AlterAction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.RaidCreature;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Jail;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.server.game.db.DatabaseFactory;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;
import utilities.RPClass.ArrestWarrentTestHelper;
import utilities.RPClass.CorpseTestHelper;
import utilities.RPClass.CreatureTestHelper;

public class AdministrationActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		new DatabaseFactory().initializeDatabase();
		ArrestWarrentTestHelper.generateRPClasses();
		CreatureTestHelper.generateRPClasses();
		CorpseTestHelper.generateRPClasses();
		// load item classes including "dagger" from XML
		//DefaultEntityManager.getInstance();	
		AdministrationAction.registerActions();
		MockStendlRPWorld.get();
		MockStendhalRPRuleProcessor.get().clearPlayers();

		// create zones needed for correct jail functionality:
		StendhalRPZone jailzone = new StendhalRPZone("knast", 100, 100);
		MockStendlRPWorld.get().addRPZone(jailzone);
		Jail jail = new Jail();
		jail.configureZone(jailzone, null);
		MockStendlRPWorld.get().addRPZone(new StendhalRPZone("-3_semos_jail", 100, 100));
	}

	@After
	public void tearDown() throws Exception {
		// release all prisoners left
		SingletonRepository.getJail().release("player");
		SingletonRepository.getJail().release("hugo");
		SingletonRepository.getJail().release("bob");

		MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	@Test
	public final void testGetLevelForCommand() {
		assertEquals(-1, AdministrationAction.getLevelForCommand("unkown")
				.intValue());
		assertEquals(0, AdministrationAction.getLevelForCommand("adminlevel")
				.intValue());
		assertEquals(-1, AdministrationAction.getLevelForCommand("support")
				.intValue());
		assertEquals(50, AdministrationAction.getLevelForCommand(
				"supportanswer").intValue());
		assertEquals(200, AdministrationAction.getLevelForCommand("tellall")
				.intValue());
		assertEquals(300, AdministrationAction.getLevelForCommand("teleportto")
				.intValue());
		assertEquals(400, AdministrationAction.getLevelForCommand("teleport")
				.intValue());
		assertEquals(400, AdministrationAction.getLevelForCommand("jail")
				.intValue());
		assertEquals(200, AdministrationAction.getLevelForCommand("gag")
				.intValue());
		assertEquals(500, AdministrationAction.getLevelForCommand("invisible")
				.intValue());
		assertEquals(500, AdministrationAction.getLevelForCommand("ghostmode")
				.intValue());
		assertEquals(500, AdministrationAction.getLevelForCommand(
				"teleclickmode").intValue());
		assertEquals(600, AdministrationAction.getLevelForCommand("inspect")
				.intValue());
		assertEquals(700, AdministrationAction.getLevelForCommand("destroy")
				.intValue());
		assertEquals(800, AdministrationAction.getLevelForCommand("summon")
				.intValue());
		assertEquals(800, AdministrationAction.getLevelForCommand("summonat")
				.intValue());
		assertEquals(900, AdministrationAction.getLevelForCommand("alter")
				.intValue());
		assertEquals(900, AdministrationAction.getLevelForCommand(
				"altercreature").intValue());
		assertEquals(5000, AdministrationAction.getLevelForCommand("super")
				.intValue());
	}

	@Test
	public final void testIsPlayerAllowedToExecuteAdminCommand() {
		final Player pl = PlayerTestHelper.createPlayer("player");
		assertTrue(AdministrationAction.isPlayerAllowedToExecuteAdminCommand(
				pl, "", true));
		assertTrue(AdministrationAction.isPlayerAllowedToExecuteAdminCommand(
				pl, "adminlevel", true));
		pl.setAdminLevel(50);
		pl.clearEvents();
		assertEquals(true, AdministrationAction
				.isPlayerAllowedToExecuteAdminCommand(pl, "adminlevel", true));
		assertEquals(true,
				AdministrationAction.isPlayerAllowedToExecuteAdminCommand(pl,
						"supportanswer", true));
	}

	@Test
	public final void testTellAllAction() {
		final Player pl = PlayerTestHelper.createPlayer("dummy");
		MockStendhalRPRuleProcessor.get().addPlayer(pl);

		CommandCenter.execute(pl, new RPAction());
		assertEquals("Unknown command null", pl.events().get(0).get("text"));

		pl.clearEvents();
		pl.setAdminLevel(5000);
		final RPAction action = new RPAction();
		action.put("type", "tellall");
		action.put("text", "huhu");
		CommandCenter.execute(pl, action);
		assertEquals("Administrator SHOUTS: huhu", pl.events().get(0).get("text"));
	
	}

	@Test
	public final void testSupportAnswerAction() {
		final Player pl = PlayerTestHelper.createPlayer("player");
		final Player bob = PlayerTestHelper.createPlayer("bob");
		final Player anptherAdmin = PlayerTestHelper.createPlayer("anotheradmin");
		anptherAdmin.setAdminLevel(5000);
		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		MockStendhalRPRuleProcessor.get().addPlayer(bob);
		MockStendhalRPRuleProcessor.get().addPlayer(anptherAdmin);

		pl.setAdminLevel(5000);
		final RPAction action = new RPAction();
		action.put("type", "supportanswer");
		action.put("text", "huhu");
		action.put("target", "bob");
		CommandCenter.execute(pl, action);
		assertEquals("Support (player) tells you: huhu \nIf you wish to reply, use /support.", bob.events().get(0).get("text"));
		assertEquals("player answers bob's support question: huhu", anptherAdmin.events().get(0).get("text"));

		bob.clearEvents();
		pl.clearEvents();
		pl.setAdminLevel(0);
		assertEquals("0", pl.get("adminlevel"));
		CommandCenter.execute(pl, action);
		assertEquals(
				"Sorry, you need to be an admin to run \"supportanswer\".", pl
						.events().get(0).get("text"));
	}

	@Test
	public final void testTeleportActionToInvalidZone() {

		final Player pl = PlayerTestHelper.createPlayer("player");
		final Player bob = PlayerTestHelper.createPlayer("bob");
		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		MockStendhalRPRuleProcessor.get().addPlayer(bob);

		pl.setAdminLevel(5000);
		final RPAction action = new RPAction();
		action.put("type", "teleport");
		action.put("text", "huhu");
		action.put("target", "bob");
		action.put("zone", "non-existing-zone");
		action.put("x", "0");
		action.put("y", "0");

		assertTrue(action.has("target") && action.has("zone")
				&& action.has("x"));

		CommandCenter.execute(pl, action);
		// The list of existing zones depends on other tests, so we simply
		// ignore it here.
		assertTrue(pl
				.events().get(0).get("text")
				.startsWith(
						"Zone \"IRPZone.ID [id=non-existing-zone]\" not found. Similar zone names: ["));
	}

	@Test
	public final void testTeleportActionToValidZone() {

		final StendhalRPZone zoneTo = new StendhalRPZone("zoneTo");
		final Player pl = PlayerTestHelper.createPlayer("player");
		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		PlayerTestHelper.generatePlayerRPClasses();
		final Player bob = new Player(new RPObject()) {
			@Override
			public boolean teleport(final StendhalRPZone zone, final int x, final int y,
					final Direction dir, final Player teleporter) {
				assertEquals("zoneTo", zone.getName());
				setName("hugo");
				return true;

			}
		};
		bob.setName("bob");
		PlayerTestHelper.addEmptySlots(bob);

		MockStendhalRPRuleProcessor.get().addPlayer(bob);

		MockStendlRPWorld.get().addRPZone(zoneTo);
		pl.setAdminLevel(5000);
		final RPAction action = new RPAction();
		action.put("type", "teleport");
		action.put("text", "huhu");
		action.put("target", "bob");
		action.put("zone", "zoneTo");
		action.put("x", "0");
		action.put("y", "0");

		assertTrue(action.has("target") && action.has("zone")
				&& action.has("x"));
		assertThat(bob.getName(), not(is("hugo")));
		CommandCenter.execute(pl, action);
		assertEquals("name must have changed, if action was executed", "hugo", bob.getName());
	}

	@Test
	public final void testTeleportToActionPlayerNotThere() {
		final Player pl = PlayerTestHelper.createPlayer("player");
		pl.setAdminLevel(5000);
		final RPAction action = new RPAction();
		action.put("type", "teleportto");
		action.put("target", "blah");
		CommandCenter.execute(pl, action);
		assertEquals("Player \"blah\" not found", pl.events().get(0).get("text"));
	}

	@Test
	public final void testTeleportToActionPlayerThere() {

		final Player pl = PlayerTestHelper.createPlayer("blah");

		pl.setAdminLevel(5000);

		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(pl);
		final RPAction action = new RPAction();
		action.put("type", "teleportto");
		action.put("target", "blah");
		CommandCenter.execute(pl, action);
		assertEquals("Position [0,0] is occupied", pl.events().get(0).get("text"));
	}

	@Test
	public final void testOnAlterActionWrongAttribute() {
		final Player pl = PlayerTestHelper.createPlayer("bob");
		pl.setAdminLevel(5000);

		MockStendhalRPRuleProcessor.get().addPlayer(pl);

		final RPAction action = new RPAction();
		action.put("type", "alter");
		action.put("target", "bob");
		action.put("stat", "0");
		action.put("mode", "");
		action.put("value", 0);

		CommandCenter.execute(pl, action);
		assertEquals(
				"Attribute you are altering is not defined in RPClass(player)",
				pl.events().get(0).get("text"));
	}

	@Test
	public final void testOnAlterAction() {

		final Player pl = PlayerTestHelper.createPlayer("bob");
		pl.setAdminLevel(5000);

		MockStendhalRPRuleProcessor.get().addPlayer(pl);

		final RPAction action = new RPAction();
		action.put("type", "alter");
		action.put("target", "bob");
		action.put("stat", "name");
		action.put("mode", "");
		action.put("value", 0);

		CommandCenter.execute(pl, action);
		assertEquals("Sorry, name cannot be changed.", pl.events().get(0).get("text"));
		action.put("stat", "adminlevel");
		pl.clearEvents();
		CommandCenter.execute(pl, action);
		assertEquals(
				"Use #/adminlevel #<playername> #[<newlevel>] to display or change adminlevel.",
				pl.events().get(0).get("text"));
	}

	@Test
	public final void testOnAlterActionTitle() {
		final Player pl = PlayerTestHelper.createPlayer("bob");
		pl.setAdminLevel(5000);

		MockStendhalRPRuleProcessor.get().addPlayer(pl);

		final RPAction action = new RPAction();
		action.put("type", "alter");
		action.put("target", "bob");
		action.put("stat", "title");
		action.put("mode", "");
		action.put("value", 0);

		CommandCenter.execute(pl, action);
		assertEquals("The title attribute may not be changed directly.", pl
				.events().get(0).get("text"));
	}

	@Test
	public final void testOnAlterActionHP() {
		final AdministrationAction aa = new AlterAction();

		final Player pl = PlayerTestHelper.createPlayer("bob");
		pl.setAdminLevel(5000);
		pl.setBaseHP(100);
		pl.setHP(100);
		MockStendhalRPRuleProcessor.get().addPlayer(pl);

		final RPAction action = new RPAction();
		action.put("type", "alter");
		action.put("target", "bob");
		action.put("stat", "hp");
		action.put("mode", "");
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
		final Player pl = PlayerTestHelper.createPlayer("bob");
		pl.setAdminLevel(5000);
		pl.setBaseHP(100);
		pl.setHP(100);
		MockStendhalRPRuleProcessor.get().addPlayer(pl);

		final RPAction action = new RPAction();
		action.put("type", "alter");
		action.put("target", "bob");
		action.put("stat", "hp");
		action.put("mode", "sub");
		action.put("value", 90);
		assertEquals(100, pl.getHP());

		CommandCenter.execute(pl, action);
		assertEquals(10, pl.getHP());
		CommandCenter.execute(pl, action);
		assertEquals(10, pl.getHP());
	}

	@Test
	public final void testOnAlterActionHPadd() {

		final Player pl = PlayerTestHelper.createPlayer("bob");
		pl.setAdminLevel(5000);
		pl.setBaseHP(100);
		pl.setHP(10);
		MockStendhalRPRuleProcessor.get().addPlayer(pl);

		final RPAction action = new RPAction();
		action.put("type", "alter");
		action.put("target", "bob");
		action.put("stat", "hp");
		action.put("mode", "add");
		action.put("value", 80);
		assertEquals(10, pl.getHP());

		CommandCenter.execute(pl, action);
		assertEquals(90, pl.getHP());
		CommandCenter.execute(pl, action);
		assertEquals("set to max base_hp", 100, pl.getHP());
	}

	@Test
	public final void testAlterCreatureEntityNotFound() {
		final Player pl = PlayerTestHelper.createPlayer("hugo");

		MockStendhalRPRuleProcessor.get().addPlayer(pl);

		pl.setAdminLevel(5000);
		final RPAction action = new RPAction();
		action.put("type", "altercreature");
		action.put("target", "bob");
		action.put("text", "blabla");

		CommandCenter.execute(pl, action);
		assertEquals("Entity not found", pl.events().get(0).get("text"));
	}

	@Test
	public final void testSummonAlterCreature() {

		final Player pl = PlayerTestHelper.createPlayer("hugo");

		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		final StendhalRPZone zone = new StendhalRPZone("testzone") {
			@Override
			public synchronized boolean collides(final Entity entity, final double x,
					final double y) {

				return false;
			}
		};
		zone.add(pl);
		pl.setPosition(1, 1);
		pl.setAdminLevel(5000);
		RPAction action = new RPAction();
		action.put("type", "summon");
		action.put("creature", "rat");
		action.put("x", 0);
		action.put("y", 0);
		CommandCenter.execute(pl, action);
		assertEquals(1, pl.getID().getObjectID());
		final Creature rat = (Creature) zone.getEntityAt(0, 0);
		assertEquals("rat", rat.get("subclass"));

		action = new RPAction();
		action.put("type", "altercreature");
		action.put("target", "#2");
		// must be of type "name/atk/def/hp/xp",
		action.put("text", "newname/5/6/7/8");

		CommandCenter.execute(pl, action);

		assertEquals("name", "newname", rat.getName());
		assertEquals("atk", 5, rat.getATK());
		assertEquals("def", 6, rat.getDEF());
		assertEquals("hp", 7, rat.getHP());
		assertEquals("xp", 8, rat.getXP());
	}

	@Test
	public final void testInvisible() {
		final Player pl = PlayerTestHelper.createPlayer("hugo");
		pl.setAdminLevel(5000);
		final RPAction action = new RPAction();
		action.put("type", "invisible");
		assertFalse(pl.isInvisibleToCreatures());
		CommandCenter.execute(pl, action);
		assertTrue(pl.isInvisibleToCreatures());
		CommandCenter.execute(pl, action);
		assertFalse(pl.isInvisibleToCreatures());
	}

	@Test
	public final void testTeleclickmode() {

		final Player pl = PlayerTestHelper.createPlayer("hugo");
		pl.setAdminLevel(5000);
		final RPAction action = new RPAction();
		action.put("type", "teleclickmode");
		assertFalse(pl.isTeleclickEnabled());
		CommandCenter.execute(pl, action);
		assertTrue(pl.isTeleclickEnabled());
		CommandCenter.execute(pl, action);
		assertFalse(pl.isTeleclickEnabled());
	}

	@Test
	public final void testJail() {
		
		MockStendlRPWorld.get().addRPZone(new StendhalRPZone("-1_semos_jail", 100, 100));

		final Player pl = PlayerTestHelper.createPlayer("hugo");
		PlayerTestHelper.registerPlayer(pl, "-1_semos_jail");
		pl.setAdminLevel(5000);
		RPAction action = new RPAction();
		action.put("type", "jail");

		CommandCenter.execute(pl, action);

		assertEquals("Usage: /jail name minutes reason", pl.events().get(0).get("text"));
		pl.clearEvents();
		action = new RPAction();
		action.put("type", "jail");
		action.put("target", "player");
		action.put("reason", "whynot");
		action.put("minutes", 1);

		CommandCenter.execute(pl, action);
		assertEquals("You have jailed player for 1 minutes. Reason: whynot.", pl.events().get(0).get("text"));
		 
		assertEquals("JailKeeper asks for support to ADMIN: hugo jailed player for 1 minutes. Reason: whynot.", pl.events().get(1).get("text"));
		assertEquals("Player player is not online, but the arrest warrant has been recorded anyway.", pl.events().get(2).get("text"));
		pl.clearEvents();

		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		action = new RPAction();
		action.put("type", "jail");
		action.put("target", "hugo");
		action.put("reason", "whynot");
		action.put("minutes", "noNumber");

		CommandCenter.execute(pl, action);
		assertEquals("Usage: /jail name minutes reason", pl.events().get(0).get("text"));
		pl.clearEvents();

		action = new RPAction();
		action.put("type", "jail");
		action.put("target", "hugo");
		action.put("reason", "whynot");
		action.put("minutes", 1);

		assertTrue(CommandCenter.execute(pl, action));
		assertThat(pl.events().get(0).get("text"), startsWith("You have been jailed by hugo for 1 minutes. Reason: whynot."));
	}

	@Test
	public final void testGag() {
		final Player pl = PlayerTestHelper.createPlayer("hugo");
		pl.setAdminLevel(5000);
		RPAction action = new RPAction();
		action.put("type", "gag");

		CommandCenter.execute(pl, action);

		assertEquals("Usage: /gag name minutes reason", pl.events().get(0).get("text"));
		pl.clearEvents();
		action = new RPAction();
		action.put("type", "gag");
		action.put("target", "name");
		action.put("reason", "whynot");
		action.put("minutes", 1);

		CommandCenter.execute(pl, action);
		assertEquals("Player name not found", pl.events().get(0).get("text"));

		pl.clearEvents();

		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		action = new RPAction();
		action.put("type", "gag");
		action.put("target", "hugo");
		action.put("reason", "whynot");
		action.put("minutes", "noNumber");

		CommandCenter.execute(pl, action);
		assertEquals("Usage: /gag name minutes reason", pl.events().get(0).get("text"));
		pl.clearEvents();

		action = new RPAction();
		action.put("type", "gag");
		action.put("target", "hugo");
		action.put("reason", "whynot");
		action.put("minutes", 1);

		CommandCenter.execute(pl, action);
		assertTrue(pl.events().get(0).get("text").startsWith(
				"You have gagged hugo for 1 minutes. Reason: "));
	}

	@Test
	public final void testOnDestroyEntityNotFOund() {
		final Player pl = PlayerTestHelper.createPlayer("hugo");
		pl.setAdminLevel(5000);
		final RPAction action = new RPAction();
		action.put("type", "destroy");

		CommandCenter.execute(pl, action);
		assertEquals("Entity not found", pl.events().get(0).get("text"));
	}

	@Test
	public final void testOnDestroyPlayer() {
		final Player pl = PlayerTestHelper.createPlayer("hugo");
		pl.setAdminLevel(5000);
		pl.clearEvents();

		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		final RPAction action = new RPAction();
		action.put("type", "destroy");
		action.put("target", "hugo");

		CommandCenter.execute(pl, action);
		assertEquals("You can't remove players", pl.events().get(0).get("text"));
	}

	@Test
	public final void testOnDestroyNPC() {

		final Player pl = PlayerTestHelper.createPlayer("hugo");
		final SpeakerNPC npc = SpeakerNPCTestHelper.createSpeakerNPC("npcTest");
		final StendhalRPZone testzone = new StendhalRPZone("Testzone");
		testzone.add(npc);
		testzone.add(pl);

		assertEquals(1, npc.getID().getObjectID());
		pl.setAdminLevel(5000);
		pl.clearEvents();

		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		final RPAction action = new RPAction();
		action.put("type", "destroy");
		action.put("target", "#1");

		CommandCenter.execute(pl, action);
		assertEquals("You can't remove SpeakerNPCs", pl.events().get(0).get("text"));
	}

	@Test
	public final void testOnDestroyRat() {
		CreatureTestHelper.generateRPClasses();
		final Player pl = PlayerTestHelper.createPlayer("hugo");
		final Creature rat = new RaidCreature(SingletonRepository.getEntityManager().getCreature("rat"));
		final StendhalRPZone testzone = new StendhalRPZone("Testzone");
		testzone.add(rat);
		testzone.add(pl);

		assertEquals(1, rat.getID().getObjectID());
		pl.setAdminLevel(5000);
		pl.clearEvents();

		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		final RPAction action = new RPAction();
		action.put("type", "destroy");
		action.put("target", "#1");

		CommandCenter.execute(pl, action);
		assertEquals("Removed rat creature with ID #1", pl.events().get(0).get("text"));
	}

	@Test
	public final void testOnDestroyRatWithTargetID() {

		final Player pl = PlayerTestHelper.createPlayer("hugo");
		final Creature rat = new RaidCreature(SingletonRepository.getEntityManager().getCreature("rat"));
		final StendhalRPZone testzone = new StendhalRPZone("Testzone");
		testzone.add(rat);
		testzone.add(pl);

		assertEquals(1, rat.getID().getObjectID());
		pl.setAdminLevel(5000);
		pl.clearEvents();

		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		final RPAction action = new RPAction();
		action.put("type", "destroy");
		action.put("target", "#1");

		assertTrue(CommandCenter.execute(pl, action));
		assertEquals("Removed rat creature with ID #1", pl.events().get(0).get("text"));
	}

	@Test
	public final void testOnInspectRatWithTargetID() {
		final Player pl = PlayerTestHelper.createPlayer("hugo");
		final Creature rat = new RaidCreature(SingletonRepository.getEntityManager().getCreature("rat"));
		final StendhalRPZone testzone = new StendhalRPZone("Testzone");
		testzone.add(rat);
		testzone.add(pl);

		assertEquals(1, rat.getID().getObjectID());
		pl.setAdminLevel(5000);
		pl.clearEvents();

		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		final RPAction action = new RPAction();
		action.put("type", "inspect");
		action.put("target", "#1");

		assertTrue(CommandCenter.execute(pl, action));
		assertTrue(pl
				.events().get(0).get("text")
				.startsWith(
						"Inspected creature is called \"rat\" and has the following attributes:"));
	}

	@Test
	public final void testOnSummonAt() {
		final Player pl = PlayerTestHelper.createPlayer("hugo");
		pl.setAdminLevel(5000);
		pl.clearEvents();

		MockStendhalRPRuleProcessor.get().addPlayer(pl);
		final StendhalRPZone testzone = new StendhalRPZone("Testzone");
		testzone.add(pl);

		RPAction action = new RPAction();
		action.put("type", "summonat");
		action.put("target", "hugo");
		action.put("slot", "hugo");
		action.put("item", "hugo");

		CommandCenter.execute(pl, action);
		assertEquals("Player \"hugo\" does not have an RPSlot named \"hugo\".",
				pl.events().get(0).get("text"));
		pl.clearEvents();

		action = new RPAction();
		action.put("type", "summonat");
		action.put("target", "hugo");
		action.put("slot", "bag");
		action.put("item", "hugo");

		CommandCenter.execute(pl, action);
		assertEquals("hugo is not an item.", pl.events().get(0).get("text"));
		pl.clearEvents();

		action = new RPAction();
		action.put("type", "summonat");
		action.put("target", "hugo");
		action.put("slot", "bag");
		action.put("item", "dagger");
		assertFalse(pl.isEquipped("dagger"));
		CommandCenter.execute(pl, action);
		// If the following fails, chances are quite good, the "items.xml" configuration file could not be loaded.
		assertTrue(pl.events().isEmpty());
		assertTrue(pl.isEquipped("dagger"));
		pl.clearEvents();

		action = new RPAction();
		action.put("type", "summonat");
		action.put("target", "noone");
		action.put("slot", "bag");
		action.put("item", "dagger");

		CommandCenter.execute(pl, action);
		assertEquals("Player \"noone\" not found.", pl.events().get(0).get("text"));
		pl.clearEvents();
	}
}
