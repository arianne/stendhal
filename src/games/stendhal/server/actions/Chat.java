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

public class Chat extends ActionListener {
	private static final Logger logger = Log4J.getLogger(Chat.class);

	public static void register() {
		Chat chat = new Chat();
		StendhalRPRuleProcessor.register("chat", chat);
		StendhalRPRuleProcessor.register("tell", chat);
		StendhalRPRuleProcessor.register("support", chat);
	}

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
			for (Player p : rules.getPlayers()) {
				if (p.getName().equals(action.get("target"))) {
					p.setPrivateText(message);
					player.setPrivateText("You tell " + p.getName() + ": "
							+ action.get("text"));
					world.modify(p);
					world.modify(player);

					rules.removePlayerText(player);
					rules.removePlayerText(p);
					return;
				}
			}

			player.setPrivateText(action.get("target")
					+ " is not currently logged.");
			rules.removePlayerText(player);
		}

		Log4J.finishMethod(logger, "tell");
	}

	private void onSupport(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "support");

		if (action.has("text")) {
			String message = player.getName() + " ask for support to ADMIN: "
					+ action.get("text");

			rules.addGameEvent(player.getName(), "support", action.get("text"));

			for (Player p : rules.getPlayers()) {
				if (p.isAdmin()) {
					p.setPrivateText(message);
					world.modify(p);
					rules.removePlayerText(p);
				}
			}

			player.setPrivateText("You ask for support: " + action.get("text"));
			rules.removePlayerText(player);
			world.modify(player);
		}

		Log4J.finishMethod(logger, "tell");
	}
}
