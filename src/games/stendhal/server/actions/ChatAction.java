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

import games.stendhal.server.StendhalPlayerDatabase;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.Player;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.game.JDBCPlayerDatabase;
import marauroa.server.game.Transaction;

import org.apache.log4j.Logger;

/**
 * Processes /chat, /tell (/msg) and /support
 */
public class ChatAction extends ActionListener {
	private static final Logger logger = Log4J.getLogger(ChatAction.class);

	/**
	 * Registers actions
	 */
	public static void register() {
		ChatAction chat = new ChatAction();
		StendhalRPRuleProcessor.register("chat", chat);
		StendhalRPRuleProcessor.register("tell", chat);
		StendhalRPRuleProcessor.register("support", chat);

		// start the logcleaner
		LogCleaner logCleaner = new LogCleaner();
		logCleaner.start();
	}

	@Override
	public void onAction(Player player, RPAction action) {
		if (action.get("type").equals("chat")) {
			onChat(player, action);
		} else if (action.get("type").equals("tell")) {
			onTell(player, action);
		} else {
			onSupport(player, action);
		}
	}

	private void onChat(Player player, RPAction action) {
		Log4J.startMethod(logger, "chat");
		if (action.has("text")) {
			player.put("text", action.get("text"));
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "chat", null, action.get("text"));
			player.notifyWorldAboutChanges();
			StendhalRPRuleProcessor.get().removePlayerText(player);
		}
		Log4J.finishMethod(logger, "chat");
	}

	private void onTell(Player player, RPAction action) {
		if (action.has("target") && action.has("text")) {
			String message = player.getName() + " tells you: "+ action.get("text");

			// find the target player
			String receiverName = action.get("target");
			Player receiver = StendhalRPRuleProcessor.get().getPlayer(receiverName);
			if (receiver == null) {
				player.sendPrivateText("No player named \"" + action.get("target") + "\" is currently active.");
				player.notifyWorldAboutChanges();
				return;
			}
			
			// check ignore list
			boolean ok = true;
			RPSlot slot = receiver.getSlot("!ignore");
			RPObject listBuddies = null;
			if (slot.size() > 0) {
				listBuddies = slot.getFirst();
				System.out.println(listBuddies);
				if (listBuddies.has("_" + player.getName())) {
					ok = false;
				}
			}
			if (!ok) {
				player.sendPrivateText("Message not accepted for delivery. This person is ignoring you.");
				player.notifyWorldAboutChanges();
				return;
			}

			// transmit the message
			receiver.sendPrivateText(message);
			player.sendPrivateText("You tell " + receiver.getName() + ": " + action.get("text"));
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "chat", receiverName, action.get("text"));
			receiver.notifyWorldAboutChanges();
			player.notifyWorldAboutChanges();
			return;
		}
	}

	private void onSupport(Player player, RPAction action) {
		Log4J.startMethod(logger, "support");

		if (action.has("text")) {
			String message = player.getName() + " asks for support to ADMIN: "
					+ action.get("text") + "\r\nPlease use #/supportanswer #" + player.getName() + " to answer.";

			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "support", action.get("text"));

			boolean found = false;
			for (Player p : StendhalRPRuleProcessor.get().getPlayers()) {
				if (p.getAdminLevel() >= AdministrationAction.REQUIRED_ADMIN_LEVEL_FOR_SUPPORT) {
					p.sendPrivateText(message);
					p.notifyWorldAboutChanges();
					if (!p.getName().equals("postman")) {
						found = true;
					}
				}
			}

			if (found) {
				player.sendPrivateText("You ask for support: " + action.get("text"));
			} else {
				player.sendPrivateText("Sorry, your support request cannot be processed at the moment, because no administrators are currently active. Please try again in a short while, or visit #irc://irc.freenode.net/#arianne");
			}
			player.notifyWorldAboutChanges();
		}

		Log4J.finishMethod(logger, "tell");
	}

	/**
	 * Deletes the chatlog after a short delay. Note this
	 * runs inside a thread outside the normal turn based processing
	 * because the SQL command may take more then 100ms on MySQL. 
	 */
	protected static class LogCleaner extends Thread {
		public LogCleaner() {
			super("ChatLogCleaner");
			super.setDaemon(true);
			super.setPriority(Thread.MIN_PRIORITY);
		}

		@Override
		public void run() {
			while (true) {
				try {
					StendhalPlayerDatabase database = (StendhalPlayerDatabase) StendhalPlayerDatabase.getDatabase();
					Transaction transaction = database.getTransaction();
					database.cleanChatLog(transaction);
					transaction.commit();
					Thread.sleep(3600 * 1000);
				} catch (Exception e) {
					logger.error(e, e);
				}
			}
		}
	}
}
