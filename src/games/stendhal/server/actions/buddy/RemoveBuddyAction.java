package games.stendhal.server.actions.buddy;

import marauroa.common.game.RPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;
import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;
class RemoveBuddyAction implements ActionListener {

	

	public void onAction(Player player, RPAction action) {
		if (action.has(TARGET)) {
			String who = action.get(TARGET);

			player.setKeyedSlot("!buddy", "_" + who, null);

			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"buddy", "remove", who);

			// TEMP! Supreceeded by /unignore
			player.removeIgnore(who);
		}
	}

}
