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

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.Player;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.server.game.RPWorld;

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
	}

	@Override
	public void onAction(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		if (action.get("type").equals("chat")) {
			onChat(world, rules, player, action);
		} else if (action.get("type").equals("tell")) {
			onTell(world, rules, player, action);
		} else {
			onSupport(world, rules, player, action);
		}
	}

	private void onChat(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "chat");
		if (action.has("text")) {
			player.put("text", action.get("text"));
			world.modify(player);
			rules.removePlayerText(player);
		}
		Log4J.finishMethod(logger, "chat");
	}

	private void onTell(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "tell");

		if (action.has("target") && action.has("text")) {
			String message = player.getName() + " tells you: "
					+ action.get("text");
			Player receiver = rules.getPlayer(action.get("target"));
			if (receiver != null) {
				receiver.sendPrivateText(message);
				player.sendPrivateText("You tell " + receiver.getName() + ": "
						+ action.get("text"));
				world.modify(receiver);
				world.modify(player);
				return;
			}
			player.sendPrivateText(action.get("target")
					+ " is not currently logged.");
		}

		Log4J.finishMethod(logger, "tell");
	}

	private void onSupport(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "support");

		if (action.has("text")) {
			String message = player.getName() + " asks for support to ADMIN: "
					+ action.get("text") + "\r\nPlease use /supportanswer " + player.getName() + " to answer.";

			rules.addGameEvent(player.getName(), "support", action.get("text"));

			boolean found = false;
			for (Player p : rules.getPlayers()) {
				if (p.getAdminLevel() >= AdministrationAction.REQUIRED_ADMIN_LEVEL_FOR_SUPPORT) {
					p.sendPrivateText(message);
					world.modify(p);
					if (!p.getName().equals("postman")) {
						found = true;
					}
				}
			}

			if (found) {
				player.sendPrivateText("You ask for support: " + action.get("text"));
			} else {
				player.sendPrivateText("Sorry, your support request cannot be processed at the moment because no supporter is in game right now.");
			}
			world.modify(player);
		}

		Log4J.finishMethod(logger, "tell");
	}
}
