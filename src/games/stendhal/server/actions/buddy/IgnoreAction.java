package games.stendhal.server.actions.buddy;

import static games.stendhal.common.constants.Actions.DURATION;
import static games.stendhal.common.constants.Actions.REASON;
import static games.stendhal.common.constants.Actions.TARGET;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

class IgnoreAction implements ActionListener {



	public void onAction(final Player player, final RPAction action) {
		int duration;
		String reason;

		if (action.has(TARGET)) {
			final String who = action.get(TARGET);

			if (action.has(DURATION)) {
				duration = action.getInt(DURATION);
			} else {
				duration = 0;
			}

			if (action.has(REASON)) {
				reason = action.get(REASON);
			} else {
				reason = null;
			}

			if (player.addIgnore(who, duration, reason)) {
				player.sendPrivateText(who + " was added to your ignore list.");
			}
		}

	}

}
