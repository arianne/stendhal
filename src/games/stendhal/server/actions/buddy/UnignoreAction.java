package games.stendhal.server.actions.buddy;

import static games.stendhal.common.constants.Actions.TARGET;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

class UnignoreAction implements ActionListener {

	public void onAction(final Player player, final RPAction action) {
		if (action.has(TARGET)) {
			final String who = action.get(TARGET);
			if (player.getIgnore(who) == null) {
				player.sendPrivateText(who
						+ " was not being ignored by you.");
			} else if (player.removeIgnore(who)) {
				player.sendPrivateText(who
						+ " was removed from your ignore list.");
			}
		}

	}

}
