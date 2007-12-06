package games.stendhal.server.actions.admin;

import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class TellAllAction extends AdministrationAction {
	
	public static void register() {
		CommandCenter.register("tellall", new TellAllAction(), 200);

	}

	@Override
	public void perform(Player player, RPAction action) {
		if (action.has("text")) {
			String message = "Administrator SHOUTS: " + action.get("text");
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"tellall", action.get("text"));

			StendhalRPAction.shout(message);
		}
	}

}
