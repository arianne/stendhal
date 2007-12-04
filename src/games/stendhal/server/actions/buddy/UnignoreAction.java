package games.stendhal.server.actions.buddy;

import marauroa.common.game.RPAction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;

class UnignoreAction implements ActionListener{

	public void onAction(Player player, RPAction action) {
		if (action.has("target")) {
			String who = action.get("target");

			if (player.removeIgnore(who)) {
				player.sendPrivateText(who
						+ " was removed from your ignore list.");
			}
		}
		
	}

}
