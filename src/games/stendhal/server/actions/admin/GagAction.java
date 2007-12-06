package games.stendhal.server.actions.admin;

import games.stendhal.server.GagManager;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class GagAction extends AdministrationAction {
	public static void register(){
		CommandCenter.register("gag", new GagAction(), 400);

	}

	@Override
	public void perform(Player player, RPAction action) {
	
		if (action.has("target") && action.has("minutes")) {
			String target = action.get("target");
			String reason = "";
			if (action.has("reason")) {
				reason = action.get("reason");
			}
			try {
				int minutes = action.getInt("minutes");
				StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
						"gag", target, Integer.toString(minutes), reason);
				GagManager.get().gag(target, player, minutes, reason);
			} catch (NumberFormatException e) {
				player.sendPrivateText("Usage: /gag name minutes reason");
			}
		} else {
			player.sendPrivateText("Usage: /gag name minutes reason");
		}
	}

}
