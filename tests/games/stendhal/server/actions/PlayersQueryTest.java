package games.stendhal.server.actions;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.PrivateTextMockingTestPlayer;
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

	@Test
	public void testOnAction() {
		ActionListener al = new PlayersQuery() {
			@Override
			public void onWhere(Player player, RPAction action) {
				whereWasExecuted = true;
			}

			@Override
			public void onWho(Player player, RPAction action) {
				whoWasExecuted = true;
			}
		};
		RPAction action = new RPAction();
		action.put(WellKnownActionConstants.TYPE, "who");
		PrivateTextMockingTestPlayer player = PlayerTestHelper.createPrivateTextMockingTestPlayer("player");
		assertFalse(whoWasExecuted);
		al.onAction(player, action);
		assertTrue(whoWasExecuted);

		action.put(WellKnownActionConstants.TYPE, "where");

		assertFalse(whereWasExecuted);
		al.onAction(player, action);
		assertTrue(whereWasExecuted);

	}

	@Test
	public void testOnWho() {
		PlayersQuery pq = new PlayersQuery();
		RPAction action = new RPAction();
		action.put(WellKnownActionConstants.TYPE, "who");
		PrivateTextMockingTestPlayer player = PlayerTestHelper.createPrivateTextMockingTestPlayer("player");
		pq.onWho(player, action);
		assertThat(player.getPrivateTextString(), equalTo("0 Players online: "));
		player.resetPrivateTextString();
		MockStendhalRPRuleProcessor.get().addPlayer(player);
		pq.onWho(player, action);
		assertThat(player.getPrivateTextString(), equalTo("1 Players online: player(0) "));
		player.resetPrivateTextString();

		player.setAdminLevel(AdministrationAction.getLevelForCommand("ghostmode") - 1);
		player.setGhost(true);
		pq.onWho(player, action);
		assertThat(player.getPrivateTextString(), equalTo("0 Players online: "));
		player.resetPrivateTextString();

		player.setAdminLevel(AdministrationAction.getLevelForCommand("ghostmode"));
		player.setGhost(true);
		pq.onWho(player, action);
		assertThat(player.getPrivateTextString(), equalTo("1 Players online: player(!0) "));
		player.resetPrivateTextString();
		
		player.setAdminLevel(AdministrationAction.getLevelForCommand("ghostmode") + 1);
		player.setGhost(true);
		pq.onWho(player, action);
		assertThat(player.getPrivateTextString(), equalTo("1 Players online: player(!0) "));
	}

	@Test
	public void testOnWhereNoTarget() {
		PlayersQuery pq = new PlayersQuery();
		RPAction action = new RPAction();
		action.put(WellKnownActionConstants.TYPE, "where");
		PrivateTextMockingTestPlayer player = PlayerTestHelper.createPrivateTextMockingTestPlayer("player");
		MockStendhalRPRuleProcessor.get().addPlayer(player);

		pq.onWhere(player, action);
		assertEquals("", player.getPrivateTextString());
	}

	@Test
	public void testOnWhereNotThere() {
		PlayersQuery pq = new PlayersQuery();
		RPAction action = new RPAction();
		action.put(WellKnownActionConstants.TYPE, "where");
		action.put(WellKnownActionConstants.TARGET, "NotThere");

		PrivateTextMockingTestPlayer player = PlayerTestHelper.createPrivateTextMockingTestPlayer("player");
		MockStendhalRPRuleProcessor.get().addPlayer(player);

		pq.onWhere(player, action);
		assertThat(player.getPrivateTextString(), equalTo("No player named \"NotThere\" is currently logged in."));
	}

	@Test
	public void testOnWhere() {
		PlayersQuery pq = new PlayersQuery();
		RPAction action = new RPAction();
		action.put(WellKnownActionConstants.TYPE, "where");
		action.put(WellKnownActionConstants.TARGET, "bob");

		PrivateTextMockingTestPlayer player = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(player);
		MockStendhalRPRuleProcessor.get().addPlayer(player);

		pq.onWhere(player, action);
		assertThat(player.getPrivateTextString(), equalTo("bob is in zone at (0,0)"));

	}

	@Test
	public void testOnWhereSheep() {
		PlayersQuery pq = new PlayersQuery();
		RPAction action = new RPAction();
		action.put(WellKnownActionConstants.TYPE, "where");
		action.put(WellKnownActionConstants.TARGET, "sheep");

		PrivateTextMockingTestPlayer player = PlayerTestHelper.createPrivateTextMockingTestPlayer("player");
		MockStendhalRPRuleProcessor.get().addPlayer(player);

		pq.onWhere(player, action);
		assertThat(player.getPrivateTextString(), equalTo("You currently have no sheep."));
	}

	@Test
	public void testOnWherePetSheep() {
		SheepTestHelper.generateRPClasses();
		PlayersQuery pq = new PlayersQuery();
		RPAction action = new RPAction();
		action.put(WellKnownActionConstants.TYPE, "where");
		action.put(WellKnownActionConstants.TARGET, "pet");

		PrivateTextMockingTestPlayer player = PlayerTestHelper.createPrivateTextMockingTestPlayer("player");

		pq.onWhere(player, action);
		assertThat(player.getPrivateTextString(), equalTo("You currently have no pet."));
		final Pet testPet = new Pet() {
			@Override
			public ID getID() {

				return new ID(new RPObject() {
					@Override
					public int getInt(String attribute) {

						return 1;
					}
				});
			}
		};
		final Sheep testSheep = new Sheep() {
			@Override
			public ID getID() {

				return new ID(new RPObject() {
					@Override
					public int getInt(String attribute) {

						return 1;
					}
				});
			}
		};
		player = new PrivateTextMockingTestPlayer(new RPObject(), "player") {
			@Override
			public Sheep getSheep() {
				return testSheep;
			}

			@Override
			public Pet getPet() {
				return testPet;
			}
		};

		player.setPet(testPet);

		MockStendhalRPRuleProcessor.get().addPlayer(player);

		pq.onWhere(player, action);
		assertThat(player.getPrivateTextString(), equalTo("Your pet is at (0,0)"));
		player.resetPrivateTextString();

		player.setSheep(testSheep);

		action.put(WellKnownActionConstants.TARGET, "sheep");

		pq.onWhere(player, action);
		assertThat(player.getPrivateTextString(), equalTo("Your sheep is at (0,0)"));
	}

}
