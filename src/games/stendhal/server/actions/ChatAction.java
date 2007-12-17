/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions;

import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;
import static games.stendhal.server.actions.WellKnownActionConstants.TEXT;
import static games.stendhal.server.actions.WellKnownActionConstants.TYPE;
import games.stendhal.common.Grammar;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.GagManager;
import games.stendhal.server.entity.player.Jail;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 * Processes /chat, /tell (/msg) and /support.
 */
public class ChatAction implements ActionListener {

	private static final String _SUPPORT = "support";
	private static final String _TELL = "tell";
	private static final String _CHAT = "chat";
	private static final String _ANSWER = "answer";
	// HashMap <players_name, last_message_time>
	private Map<String, Long> lastMsg = new HashMap<String, Long>();

	/**
	 * Registers AnswerAction ChatAction TellAction and SupportAction.
	 */
	public static void register() {
		ChatAction chat = new ChatAction();
		CommandCenter.register(_ANSWER, chat);
		CommandCenter.register(_CHAT, chat);
		CommandCenter.register(_TELL, chat);
		CommandCenter.register(_SUPPORT, chat);
	}

	public void onAction(final Player player, final RPAction action) {

		if (GagManager.isGagged(player)) {
			long timeRemaining = GagManager.get().getTimeRemaining(player);
			player.sendPrivateText("You are gagged, it will expire in "
					+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L)));
			return;
		}

		if (action.get(TYPE).equals(_CHAT)) {
			onChat(player, action);
		} else if (action.get(TYPE).equals(_TELL)) {
			onTell(player, action);
		} else if (action.get(TYPE).equals(_ANSWER)) {
			onAnswer(player, action);
		} else {
			onSupport(player, action);
		}
	}

	private void onChat(Player player, RPAction action) {

		if (action.has(TEXT)) {
			String text = action.get(TEXT);
			player.put("text", text);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), _CHAT,
					null, Integer.toString(text.length()),
					text.substring(0, Math.min(text.length(), 1000)));

			player.notifyWorldAboutChanges();
			StendhalRPRuleProcessor.get().removePlayerText(player);
		}

	}

	private void onAnswer(Player player, RPAction action) {
		if (action.has(TEXT)) {
			if (player.getLastPrivateChatter() != null) {
				// convert the action to a /tell action
				action.put(TYPE, _TELL);
				action.put(TARGET, player.getLastPrivateChatter());
				onTell(player, action);
			} else {
				player.sendPrivateText("Nobody has talked privately to you.");
			}
		}

	}

	private void onTell(Player player, RPAction action) {
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

			if (receiverName.equals("postman")) {
				// HACK: Don't risk breaking postman messages
				message = senderName + " tells you: " + text;
			} else if (senderName.equals(receiverName)) {
				message = "You mutter to yourself: " + text;
			} else {
				message = senderName + " tells " + receiverName + ": " + text;
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
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), _CHAT,
					receiverName, Integer.toString(text.length()),
					text.substring(0, Math.min(text.length(), 1000)));
			receiver.notifyWorldAboutChanges();
			player.notifyWorldAboutChanges();
			return;
		}
	}

	private void onSupport(final Player player, final RPAction action) {

		if (action.has(TEXT)) {

			if (action.get(TEXT).trim().equals("")) {
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

			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					_SUPPORT, action.get(TEXT));

			StendhalRPRuleProcessor.sendMessageToSupporters(player.getTitle(), message);

			player.sendPrivateText("You ask for support: "
					+ action.get(TEXT)
					+ "\nIt may take a little time until your question is answered.");
			player.notifyWorldAboutChanges();
		}

	}

	/**
	 * Deletes the chatlog after a short delay. Note this runs inside a thread
	 * outside the normal turn based processing because the SQL command may take
	 * more then 100ms on MySQL.
	 */

}
