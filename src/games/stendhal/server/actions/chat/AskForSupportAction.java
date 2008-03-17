package games.stendhal.server.actions.chat;

import static games.stendhal.server.actions.WellKnownActionConstants.TEXT;

import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Jail;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * handles asking for /support.
 */
public class AskForSupportAction  implements ActionListener {
	// HashMap <players_name, last_message_time>
	private Map<String, Long> lastMsg = new HashMap<String, Long>();

	public void onAction(Player player, RPAction action) {
		if (action.has(TEXT)) {

			if ("".equals( action.get(TEXT).trim() )) {
				player.sendPrivateText("Usage /support <your message here>");
				return;
			}

			if (Jail.isInJail(player)) {
				// check if the player sent a support message before
				if (lastMsg.containsKey(player.getName())) {
					Long timeLastMsg = System.currentTimeMillis()
							- lastMsg.get(player.getName());

					// the player have to wait one minute since the last support
					// message was sent
					if (timeLastMsg < 60000) {
						player.sendPrivateText("We only allow inmates one support message per minute.");
						return;
					}
				}

				lastMsg.put(player.getName(), System.currentTimeMillis());
			}

			String message = action.get(TEXT)
					+ "\r\nPlease use #/supportanswer #" + player.getTitle()
					+ " to answer.";

			SingletonRepository.getRuleProcessor().addGameEvent(player.getName(),
					"support", action.get(TEXT));

			StendhalRPRuleProcessor.sendMessageToSupporters(player.getTitle(), message);

			player.sendPrivateText("You ask for support: "
					+ action.get(TEXT)
					+ "\nIt may take a little time until your question is answered.");
			player.notifyWorldAboutChanges();
		}
	}
}
