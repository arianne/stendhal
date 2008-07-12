package games.stendhal.server.actions.buddy;

import marauroa.common.game.RPAction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;

public class GrumpyAction implements ActionListener {
	private static final String _REASON = "reason";

	/**
	 * Handle a Grumpy action.
	 * 
	 * @param player
	 *            The player.
	 * @param action
	 *            The action.
	 */
	public void onAction(final Player player, final RPAction action) {
		if (action.has(_REASON)) {
			player.setGrumpyMessage(action.get(_REASON));
		} else {
			player.setGrumpyMessage(null);
		}
		player.notifyWorldAboutChanges();

	}

}
