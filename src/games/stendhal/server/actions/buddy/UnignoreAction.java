package games.stendhal.server.actions.buddy;

import marauroa.common.game.RPAction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;
import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;

class UnignoreAction implements ActionListener{

	

	public void onAction(Player player, RPAction action) {
		if (action.has(TARGET)) {
			String who = action.get(TARGET);

			if (player.removeIgnore(who)) {
				player.sendPrivateText(who
						+ " was removed from your ignore list.");
			}
		}
		
	}

}
