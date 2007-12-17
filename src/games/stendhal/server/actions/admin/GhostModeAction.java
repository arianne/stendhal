package games.stendhal.server.actions.admin;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class GhostModeAction extends AdministrationAction {
	private static final String _INVISIBLE = "invisible";
	private static final String _GHOSTMODE = "ghostmode";

	public static void register() {
		CommandCenter.register(_GHOSTMODE, new GhostModeAction(), 500);

	}

	@Override
	public void perform(Player player, RPAction action) {

		if (player.isGhost()) {
			player.setGhost(false);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), _GHOSTMODE, "off");

			player.setInvisible(false);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), _INVISIBLE, "off");
	
		} else {
			/*
			 * When we enter ghostmode we want our player to be also invisible.
			 */
			player.setInvisible(true);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), _INVISIBLE, "on");

			player.setGhost(true);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), _GHOSTMODE, "on");
	
			
		}
		StendhalRPRuleProcessor.notifyOnlineStatus(!player.isGhost(), player.getName());

		player.notifyWorldAboutChanges();
	}

}
