package games.stendhal.server.actions.chat;

import static games.stendhal.common.constants.Actions.TEXT;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.GagManager;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Handles emote actions.
 * 
 * @author raignarok
 */
public class EmoteAction implements ActionListener {

	public void onAction(final Player player, final RPAction action) {
		if (!player.getChatBucket().checkAndAdd()) {
			return;
		}

		if (GagManager.checkIsGaggedAndInformPlayer(player)) {
			return;
		}

		if (action.has(TEXT)) {
			//emote actions are treated as normal chat actions
			//on the client side, !me is replaced with the name
			final String text = "!me " + action.get(TEXT);
			player.put("text", text);
 
			new GameEvent(player.getName(), "chat", null, Integer.toString(text.length()), text.substring(0, Math.min(text.length(), 1000))).raise();

			player.notifyWorldAboutChanges();
			SingletonRepository.getRuleProcessor().removePlayerText(player);
		}
	}

}
