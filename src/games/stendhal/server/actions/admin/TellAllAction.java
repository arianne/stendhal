package games.stendhal.server.actions.admin;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class TellAllAction extends AdministrationAction {

	private static final String _TEXT = "text";
	private static final String _TELLALL = "tellall";

	public static void register() {
		CommandCenter.register(_TELLALL, new TellAllAction(), 200);

	}

	@Override
	public void perform(Player player, RPAction action) {
		if (action.has(_TEXT)) {
			String message = "Administrator SHOUTS: " + action.get(_TEXT);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					_TELLALL, action.get(_TEXT));

			StendhalRPRuleProcessor.get().tellAllPlayers(message);
		}
	}

}
