package games.stendhal.server.actions;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import games.stendhal.common.constants.Actions;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.Cat;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPAction;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.RPClass.CatTestHelper;
import utilities.RPClass.PetTestHelper;
import utilities.RPClass.SheepTestHelper;

public class PlayersQueryTest {

	private boolean whoWasExecuted;
	private boolean whereWasExecuted;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@After
	public void tearDown() throws Exception {

		MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	/**
	 * Tests for onAction.
	 */
	@Test
	public void testOnAction() {
		final ActionListener al = new PlayersQuery() {
			@Override
			public void onWhere(final Player player, final RPAction action) {
				whereWasExecuted = true;
			}

			@Override
			public void onWho(final Player player, final RPAction action) {
				whoWasExecuted = true;
			}
		};
		final RPAction action = new RPAction();
		action.put(Actions.TYPE, "who");
		final Player player = PlayerTestHelper.createPlayer("player");
		assertFalse(whoWasExecuted);
		al.onAction(player, action);
		assertTrue(whoWasExecuted);

		action.put(Actions.TYPE, "where");

		assertFalse(whereWasExecuted);
		al.onAction(player, action);
		assertTrue(whereWasExecuted);
	}

	/**
	 * Tests for onWho.
	 */
	@Test
	public void testOnWho() {
		final PlayersQuery pq = new PlayersQuery();
		final RPAction action = new RPAction();
		action.put(Actions.TYPE, "who");
		final Player player = PlayerTestHelper.createPlayer("player");
		pq.onWho(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("0 Players online: "));
		player.clearEvents();
		MockStendhalRPRuleProcessor.get().addPlayer(player);
		pq.onWho(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("1 Players online: player(0) "));
		player.clearEvents();

		player.setAdminLevel(AdministrationAction.getLevelForCommand("ghostmode") - 1);
		player.setGhost(true);
		pq.onWho(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("0 Players online: "));
		player.clearEvents();

		player.setAdminLevel(AdministrationAction.getLevelForCommand("ghostmode"));
		player.setGhost(true);
		pq.onWho(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("1 Players online: player(!0) "));
		player.clearEvents();
		
		player.setAdminLevel(AdministrationAction.getLevelForCommand("ghostmode") + 1);
		player.setGhost(true);
		pq.onWho(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("1 Players online: player(!0) "));
	}

	/**
	 * Tests for onWhereNoTarget.
	 */
	@Test
	public void testOnWhereNoTarget() {
		final PlayersQuery pq = new PlayersQuery();
		final RPAction action = new RPAction();
		action.put(Actions.TYPE, "where");
		final Player player = PlayerTestHelper.createPlayer("player");
		MockStendhalRPRuleProcessor.get().addPlayer(player);

		pq.onWhere(player, action);
		assertTrue(player.events().isEmpty());
	}

	/**
	 * Tests for onWhereNotThere.
	 */
	@Test
	public void testOnWhereNotThere() {
		final PlayersQuery pq = new PlayersQuery();
		final RPAction action = new RPAction();
		action.put(Actions.TYPE, "where");
		action.put(Actions.TARGET, "NotThere");

		final Player player = PlayerTestHelper.createPlayer("player");
		MockStendhalRPRuleProcessor.get().addPlayer(player);

		pq.onWhere(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("No player or pet named \"NotThere\" is currently logged in."));
	}

	/**
	 * Tests for onWhere.
	 */
	@Test
	public void testOnWhere() {
		final PlayersQuery pq = new PlayersQuery();
		final RPAction action = new RPAction();
		action.put(Actions.TYPE, "where");
		action.put(Actions.TARGET, "bob");

		final Player player = PlayerTestHelper.createPlayer("bob");
		final StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(player);
		MockStendhalRPRuleProcessor.get().addPlayer(player);
		pq.onWhere(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("bob is in zone at (0,0)"));
		player.clearEvents();
		
		
		final Player ghosted = PlayerTestHelper.createPlayer("ghosted");
		zone.add(ghosted);
		MockStendhalRPRuleProcessor.get().addPlayer(ghosted);
		action.put(Actions.TARGET, ghosted.getName());
		pq.onWhere(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("ghosted is in zone at (0,0)"));
		player.clearEvents();
		
		ghosted.setGhost(true);
		pq.onWhere(player, action);
		
		assertThat(player.events().get(0).get("text"), equalTo("No player or pet named \"ghosted\" is currently logged in."));
	}

	/**
	 * Tests for onWhereSheep.
	 */
	@Test
	public void testOnWhereSheep() {
		final PlayersQuery pq = new PlayersQuery();
		final RPAction action = new RPAction();
		action.put(Actions.TYPE, "where");
		action.put(Actions.TARGET, "sheep");

		final Player player = PlayerTestHelper.createPlayer("player");
		MockStendhalRPRuleProcessor.get().addPlayer(player);

		pq.onWhere(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("No player or pet named \"sheep\" is currently logged in."));
	}

	/**
	 * Tests for onWherePetSheep.
	 */
	@Test
	public void testOnWherePetSheep() {
		SheepTestHelper.generateRPClasses();
		PetTestHelper.generateRPClasses();
		CatTestHelper.generateRPClasses();
		final PlayersQuery pq = new PlayersQuery();
		final RPAction action = new RPAction();
		action.put(Actions.TYPE, "where");
		action.put(Actions.TARGET, "pet");

		Player player = PlayerTestHelper.createPlayer("player");

		pq.onWhere(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("No player or pet named \"pet\" is currently logged in."));
	
		
		final Pet testPet = new Cat();
		
		final Sheep testSheep = new Sheep();

		player = PlayerTestHelper.createPlayer("player");
		
		StendhalRPZone stendhalRPZone = new StendhalRPZone("zone");
		MockStendlRPWorld.get().addRPZone(stendhalRPZone);
		stendhalRPZone.add(player);
		
		stendhalRPZone.add(testSheep);
		stendhalRPZone.add(testPet);
		player.setPet(testPet);

		pq.onWhere(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("Your cat is at (0,0)"));
		player.clearEvents();

		player.setSheep(testSheep);

		action.put(Actions.TARGET, "sheep");

		pq.onWhere(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("Your sheep is at (0,0)"));
	}

}
