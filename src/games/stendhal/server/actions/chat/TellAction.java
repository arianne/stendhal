package games.stendhal.server.actions.chat;

import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;
import static games.stendhal.server.actions.WellKnownActionConstants.TEXT;
import games.stendhal.common.Grammar;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.GagManager;
import games.stendhal.server.entity.player.Jail;
import games.stendhal.server.entity.player.Player;

import java.util.StringTokenizer;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 * handles /tell-action (/msg-action). 
 */
public class TellAction implements ActionListener {

	// TODO: split this method into small parts
	public void onAction(Player player, RPAction action) {
		if (GagManager.checkIsGaggedAndInformPlayer(player)) {
			return;
		}

		String away;
		String reply;
		String grumpy;

		// TODO: find a cleaner way to implement it
		if (Jail.isInJail(player)) {
			player.sendPrivateText("The strong anti telepathy aura prevents you from getting through. Use /support <text> to contact an admin!");
			return;
		}

		if (action.has(TARGET) && action.has(TEXT)) {
			String text = action.get(TEXT).trim();
			String message;

			// find the target player
			String senderName = player.getTitle();
			String receiverName = action.get(TARGET);

			Player receiver = StendhalRPRuleProcessor.get().getPlayer(
					receiverName);
			/*
			 * If the receiver is not logged or if it is a ghost and you don't
			 * have the level to see ghosts...
			 */
			if ((receiver == null)
					|| (receiver.isGhost() && (player.getAdminLevel() < AdministrationAction.getLevelForCommand("ghostmode")))) {
				player.sendPrivateText("No player named \""
						+ action.get(TARGET) + "\" is currently active.");
				player.notifyWorldAboutChanges();
				return;
			}

			if (senderName.equals(receiverName)) {
				message = "You mutter to yourself: " + text;
			} else {
				message = senderName + " tells you: " + text;
			}

			// HACK: extract sender from postman messages
			StringTokenizer st = new StringTokenizer(text, " ");
			if (senderName.equals("postman") && (st.countTokens() > 2)) {
				String temp = st.nextToken();
				String command = st.nextToken();
				if (command.equals("asked")) {
					senderName = temp;
				}
			}

			// check ignore list
			reply = receiver.getIgnore(senderName);
			if (reply != null) {
				// sender is on ignore list
				// HACK: do not notify postman
				if (!senderName.equals("postman")) {
					if (reply.length() == 0) {
						player.sendPrivateText(Grammar.suffix_s(receiverName)
								+ " mind is not attuned to yours, so you cannot reach them.");
					} else {
						player.sendPrivateText(receiverName
								+ " is ignoring you: " + reply);
					}

					player.notifyWorldAboutChanges();
				}
				return;
			}

			// check grumpiness
			grumpy = receiver.getGrumpyMessage();
			if (grumpy != null && receiver.getSlot("!buddy").size() > 0) {
				RPObject buddies = receiver.getSlot("!buddy").iterator().next();
				boolean senderFound = false;
				for (String buddyName : buddies) {
					// TODO: as in Player.java, remove '_' prefix if ID is made
					// completely virtual
					if (buddyName.charAt(0) == '_') {
						buddyName = buddyName.substring(1);
					}
					if (buddyName.equals(senderName)) {
						senderFound = true;
						break;
					}
				}
				if (!senderFound) {
					// sender is not a buddy
					// HACK: do not notify postman
					if (!senderName.equals("postman")) {
						if (grumpy.length() == 0) {
							player.sendPrivateText(receiverName
									+ " has a closed mind, and is seeking solitude from all but close friends");
						} else {
							player.sendPrivateText(receiverName
									+ " is seeking solitude from all but close friends: "
									+ grumpy);
						}
						player.notifyWorldAboutChanges();
					}
					return;
				}
			}
			// transmit the message
			receiver.sendPrivateText(message);

			if (!senderName.equals(receiverName)) {
				player.sendPrivateText("You tell " + receiverName + ": " + text);
			}

			/*
			 * Handle /away messages
			 */
			away = receiver.getAwayMessage();
			if (away != null) {
				if (receiver.isAwayNotifyNeeded(senderName)) {
					/*
					 * Send away response
					 */
					player.sendPrivateText(receiverName + " is away: " + away);

					player.notifyWorldAboutChanges();
				}
			}

			receiver.setLastPrivateChatter(senderName);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "chat",
					receiverName, Integer.toString(text.length()),
					text.substring(0, Math.min(text.length(), 1000)));
			receiver.notifyWorldAboutChanges();
			player.notifyWorldAboutChanges();
			return;
		}
	}

}
