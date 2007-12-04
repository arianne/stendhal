package games.stendhal.server.actions.buddy;

import marauroa.common.game.RPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;

class RemoveBuddyAction implements ActionListener {

	public void onAction(Player player, RPAction action) {
		if (action.has("target")) {
			String who = action.get("target");

			player.setKeyedSlot("!buddy", "_" + who, null);

			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"buddy", "remove", who);

			// TEMP! Supreceeded by /unignore
			player.removeIgnore(who);
		}
	}

}
