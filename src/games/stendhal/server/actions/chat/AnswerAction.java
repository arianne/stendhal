package games.stendhal.server.actions.chat;

import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;
import static games.stendhal.server.actions.WellKnownActionConstants.TEXT;
import static games.stendhal.server.actions.WellKnownActionConstants.TYPE;
import marauroa.common.game.RPAction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;

/**
 * handles /tell-action (/msg-action). 
 */
public class AnswerAction implements ActionListener {

	public void onAction(final Player player, final RPAction action) {
		if (action.has(TEXT)) {
			if (player.getLastPrivateChatter() != null) {
				// convert the action to a /tell action
				action.put(TYPE, "tell");
				action.put(TARGET, player.getLastPrivateChatter());
				new TellAction().onAction(player, action);
			} else {
				player.sendPrivateText("Nobody has talked privately to you.");
			}
		}
	}

}
