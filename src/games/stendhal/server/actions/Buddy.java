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
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.game.RPWorld;

import org.apache.log4j.Logger;

public class Buddy extends ActionListener {
	private static final Logger logger = Log4J.getLogger(Buddy.class);

	public static void register() {
		Buddy buddy = new Buddy();
		StendhalRPRuleProcessor.register("addbuddy", buddy);
		StendhalRPRuleProcessor.register("removebuddy", buddy);
	}

	@Override
	public void onAction(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		if (action.get("type").equals("addbuddy")) {
			onAddBuddy(world, rules, player, action);
		} else {
			onRemoveBuddy(world, rules, player, action);
		}
	}

	private void onAddBuddy(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "addBuddy");

		if (action.has("target")) {
			String who = action.get("target");
			RPSlot slot = player.getSlot("!buddy");

			RPObject listBuddies = null;

			if (slot.size() > 0) {
				listBuddies = slot.iterator().next();
			} else {
				listBuddies = new RPObject();
				slot.assignValidID(listBuddies);
				slot.add(listBuddies);
			}

			int online = 0;

			for (Player p : rules.getPlayers()) {
				if (p.getName().equals(who)) {
					online = 1;
					break;
				}
			}

			listBuddies.put("_" + who, online);
		}

		Log4J.finishMethod(logger, "addBuddy");
	}

	private void onRemoveBuddy(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "removeBuddy");

		if (action.has("target")) {
			String who = "_" + action.get("target");
			RPSlot slot = player.getSlot("!buddy");

			RPObject listBuddies = null;

			if (slot.size() > 0) {
				listBuddies = slot.getFirst();

				if (listBuddies.has(who)) {
					listBuddies.remove(who);
				}
			}
		}

		Log4J.finishMethod(logger, "removeBuddy");
	}
}
