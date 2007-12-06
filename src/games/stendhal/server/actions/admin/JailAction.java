package games.stendhal.server.actions.admin;

import games.stendhal.server.Jail;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class JailAction extends AdministrationAction {
	public static void register() {
		CommandCenter.register("jail", new JailAction(), 400);

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
						"jail", target, Integer.toString(minutes), reason);
				Jail.get().imprison(target, player, minutes, reason);
			} catch (NumberFormatException e) {
				player.sendPrivateText("Usage: /jail name minutes reason");
			}
		} else {
			player.sendPrivateText("Usage: /jail name minutes reason");
		}

	}

}
