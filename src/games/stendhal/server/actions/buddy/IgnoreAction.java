package games.stendhal.server.actions.buddy;

import marauroa.common.game.RPAction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;
import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;

class IgnoreAction implements ActionListener {

	private static final String _REASON = "reason";
	// TODO: make this minutes
	private static final String _DURATION = "duration";

	public void onAction(final Player player, final RPAction action) {
		int duration;
		String reason;

		if (action.has(TARGET)) {
			final String who = action.get(TARGET);

			if (action.has(_DURATION)) {
				duration = action.getInt(_DURATION);
			} else {
				duration = 0;
			}

			if (action.has(_REASON)) {
				reason = action.get(_REASON);
			} else {
				reason = null;
			}

			if (player.addIgnore(who, duration, reason)) {
				player.sendPrivateText(who + " was added to your ignore list.");
			}
		}

	}

}
