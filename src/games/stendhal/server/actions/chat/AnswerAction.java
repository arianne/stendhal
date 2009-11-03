package games.stendhal.server.actions.chat;

import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TEXT;
import static games.stendhal.common.constants.Actions.TYPE;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * handles /tell-action (/msg-action). 
 */
class AnswerAction implements ActionListener {

	public void onAction(final Player player, final RPAction action) {
		if (!player.getChatBucket().checkAndAdd()) {
			return;
		}

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
