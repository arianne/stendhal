package games.stendhal.server.actions.buddy;

import marauroa.common.game.RPAction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;

class IgnoreAction implements ActionListener {

	public void onAction(Player player, RPAction action) {
		int duration;
		String reason;

		if (action.has("target")) {
			String who = action.get("target");

			if (action.has("duration")) {
				duration = action.getInt("duration");
			} else {
				duration = 0;
			}

			if (action.has("reason")) {
				reason = action.get("reason");
			} else {
				reason = null;
			}

			if (player.addIgnore(who, duration, reason)) {
				player.sendPrivateText(who + " was added to your ignore list.");
			}
		}

	}

}
