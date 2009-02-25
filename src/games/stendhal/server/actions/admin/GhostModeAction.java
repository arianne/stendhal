package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.GHOSTMODE;
import static games.stendhal.common.constants.Actions.INVISIBLE;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalPlayerDatabase;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class GhostModeAction extends AdministrationAction {

	public static void register() {
		CommandCenter.register(GHOSTMODE, new GhostModeAction(), 500);

	}

	@Override
	public void perform(final Player player, final RPAction action) {

		if (player.isGhost()) {
			player.setGhost(false);
			SingletonRepository.getRuleProcessor().addGameEvent(player.getName(), GHOSTMODE, "off");

		} else {
			/*
			 * When we enter ghostmode we want our player to be also invisible.
			 */
			player.setInvisible(true);
			SingletonRepository.getRuleProcessor().addGameEvent(player.getName(), INVISIBLE, "on");

			player.setGhost(true);
			SingletonRepository.getRuleProcessor().addGameEvent(player.getName(), GHOSTMODE, "on");
	
			
		}
		/* Notify database that the player is in Ghost mode */
		final StendhalPlayerDatabase database = (StendhalPlayerDatabase) StendhalPlayerDatabase.getDatabase();		
		database.setOnlineStatus(player, !player.isGhost());
		
		/* Notify players about admin going into ghost mode. */
		StendhalRPRuleProcessor.notifyOnlineStatus(!player.isGhost(), player.getName());
		
		player.notifyWorldAboutChanges();
	}

}
