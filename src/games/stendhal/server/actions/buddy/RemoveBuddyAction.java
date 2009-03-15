package games.stendhal.server.actions.buddy;

import static games.stendhal.common.constants.Actions.TARGET;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

class RemoveBuddyAction implements ActionListener {

	public void onAction(final Player player, final RPAction action) {
		if (action.has(TARGET)) {
			final String who = action.get(TARGET);

			player.setKeyedSlot("!buddy", "_" + who, null);

			new GameEvent(player.getName(), "buddy", "remove", who).raise();

			// TEMP! Supreceeded by /unignore
			player.removeIgnore(who);
		}
	}

}
