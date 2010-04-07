package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.GHOSTMODE;
import static games.stendhal.common.constants.Actions.INVISIBLE;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.db.StendhalWebsiteDAO;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.server.game.db.DAORegister;

/**
 * changes the ghostmode flag of admins
 */
public class GhostModeAction extends AdministrationAction {

	public static void register() {
		CommandCenter.register(GHOSTMODE, new GhostModeAction(), 500);

	}

	@Override
	public void perform(final Player player, final RPAction action) {

		if (alreadyInRequestedMode(player, action)) {
			return;
		}

		if (player.isGhost()) {
			deactivateGhost(player);
		} else {
			activateGhostmode(player);
		}

		/* Notify database that the player is in Ghost mode */
		DAORegister.get().get(StendhalWebsiteDAO.class).setOnlineStatus(player, !player.isGhost());
		
		/* Notify players about admin going into ghost mode. */
		StendhalRPRuleProcessor.get().notifyOnlineStatus(!player.isGhost(), player);
		
		player.notifyWorldAboutChanges();
	}

	/**
	 * is the player already in teh requested mode?
	 *
	 * @param player Player
	 * @param action request
	 * @return true, if the requested and the current mode match, false otherwise
	 */
	private boolean alreadyInRequestedMode(final Player player, final RPAction action) {
		if (!action.has("mode")) {
			return false;
		}

		boolean requestedMode = Boolean.parseBoolean(action.get("mode"));
		return (requestedMode == player.isGhost());
	}

	/**
	 * deactivates ghostmode
	 *
	 * @param player the admin
	 */
	private void deactivateGhost(final Player player) {
		player.setGhost(false);
		new GameEvent(player.getName(), GHOSTMODE, "off").raise();
	}


	/**
	 * activtes ghostmode and makes the player invisible to monsters.
	 *
	 * @param player the admin
	 */
	private void activateGhostmode(final Player player) {
		player.setInvisible(true);
		new GameEvent(player.getName(), INVISIBLE, "on").raise();

		player.setGhost(true);
		new GameEvent(player.getName(), GHOSTMODE, "on").raise();
	}

}
