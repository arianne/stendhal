package games.stendhal.server.actions.buddy;

import marauroa.common.game.RPAction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;

class RemoveBuddyAction implements ActionListener {

	public void onAction(final Player player, final RPAction action) {
		if (action.has(TARGET)) {
			final String who = action.get(TARGET);

			player.setKeyedSlot("!buddy", "_" + who, null);

			SingletonRepository.getRuleProcessor().addGameEvent(player.getName(),
					"buddy", "remove", who);

			// TEMP! Supreceeded by /unignore
			player.removeIgnore(who);
		}
	}

}
