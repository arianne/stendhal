package games.stendhal.server.actions.buddy;

import static games.stendhal.common.constants.Actions.REASON;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class GrumpyAction implements ActionListener {

	/**
	 * Handle a Grumpy action.
	 * 
	 * @param player
	 *            The player.
	 * @param action
	 *            The action.
	 */
	public void onAction(final Player player, final RPAction action) {
		if (action.has(REASON)) {
			player.setGrumpyMessage(action.get(REASON));
		} else {
			player.setGrumpyMessage(null);
		}
		player.notifyWorldAboutChanges();

	}

}
