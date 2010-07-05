package games.stendhal.server.actions;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import games.stendhal.common.constants.Actions;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPAction;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class WhoActionTest {


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
		final WhoAction pq = new WhoAction();
		final RPAction action = new RPAction();
		action.put(Actions.TYPE, "who");
		final Player player = PlayerTestHelper.createPlayer("player");
		pq.onAction(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("0 Players online: "));
		player.clearEvents();
		MockStendhalRPRuleProcessor.get().addPlayer(player);
		pq.onAction(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("1 Players online: player(0) "));
		player.clearEvents();

		player.setAdminLevel(AdministrationAction.getLevelForCommand("ghostmode") - 1);
		player.setGhost(true);
		pq.onAction(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("0 Players online: "));
		player.clearEvents();

		player.setAdminLevel(AdministrationAction.getLevelForCommand("ghostmode"));
		player.setGhost(true);
		pq.onAction(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("1 Players online: player(!0) "));
		player.clearEvents();
		
		player.setAdminLevel(AdministrationAction.getLevelForCommand("ghostmode") + 1);
		player.setGhost(true);
		pq.onAction(player, action);
		assertThat(player.events().get(0).get("text"), equalTo("1 Players online: player(!0) "));
	}

}
