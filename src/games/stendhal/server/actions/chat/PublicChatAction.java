package games.stendhal.server.actions.chat;

import static games.stendhal.server.actions.WellKnownActionConstants.TEXT;
import marauroa.common.game.RPAction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.GagManager;
import games.stendhal.server.entity.player.Player;

/**
 * handles public said text .
 */
public class PublicChatAction implements ActionListener {

	public void onAction(Player player, RPAction action) {
		if (GagManager.checkIsGaggedAndInformPlayer(player)) {
			return;
		}

		if (action.has(TEXT)) {
			String text = action.get(TEXT);
			player.put("text", text);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "chat",
					null, Integer.toString(text.length()),
					text.substring(0, Math.min(text.length(), 1000)));

			player.notifyWorldAboutChanges();
			StendhalRPRuleProcessor.get().removePlayerText(player);
		}
	}

}
