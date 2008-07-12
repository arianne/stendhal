package games.stendhal.server.actions.buddy;

import marauroa.common.game.RPAction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;
import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;

class UnignoreAction implements ActionListener {

	public void onAction(final Player player, final RPAction action) {
		if (action.has(TARGET)) {
			final String who = action.get(TARGET);

			if (player.removeIgnore(who)) {
				player.sendPrivateText(who
						+ " was removed from your ignore list.");
			}
		}

	}

}
