package games.stendhal.server.actions.buddy;

import marauroa.common.game.RPAction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;

public class GrumpyAction implements ActionListener {
	/**
	 * Handle a Grumpy action.
	 *
	 * @param	player		The player.
	 * @param	action		The action.
	 */
	public void onAction(Player player, RPAction action) {
		if (action.has("reason")) {
			player.setGrumpyMessage(action.get("reason"));
		} else {
			player.setGrumpyMessage(null);	
		}
		player.notifyWorldAboutChanges();

	}

}
