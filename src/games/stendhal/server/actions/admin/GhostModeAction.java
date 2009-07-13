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

public class GhostModeAction extends AdministrationAction {

	public static void register() {
		CommandCenter.register(GHOSTMODE, new GhostModeAction(), 500);

	}

	@Override
	public void perform(final Player player, final RPAction action) {

		if (player.isGhost()) {
			player.setGhost(false);
			new GameEvent(player.getName(), GHOSTMODE, "off").raise();

		} else {
			/*
			 * When we enter ghostmode we want our player to be also invisible.
			 */
			player.setInvisible(true);
			new GameEvent(player.getName(), INVISIBLE, "on").raise();

			player.setGhost(true);
			new GameEvent(player.getName(), GHOSTMODE, "on").raise();
	
			
		}
		/* Notify database that the player is in Ghost mode */
		DAORegister.get().get(StendhalWebsiteDAO.class).setOnlineStatus(player, !player.isGhost());
		
		/* Notify players about admin going into ghost mode. */
		StendhalRPRuleProcessor.get().notifyOnlineStatus(!player.isGhost(), player.getName());
		
		player.notifyWorldAboutChanges();
	}

}
