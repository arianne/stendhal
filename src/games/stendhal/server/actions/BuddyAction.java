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

import org.apache.log4j.Logger;

public class BuddyAction extends ActionListener {
	private static final Logger logger = Log4J.getLogger(BuddyAction.class);

	public static void register() {
		BuddyAction buddy = new BuddyAction();
		StendhalRPRuleProcessor.register("addbuddy", buddy);
		StendhalRPRuleProcessor.register("removebuddy", buddy);
	}

	@Override
	public void onAction(Player player, RPAction action) {
		if (action.get("type").equals("addbuddy")) {
			onAddBuddy(player, action);
		} else {
			onRemoveBuddy(player, action);
		}
	}

	private void onAddBuddy(Player player, RPAction action) {
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
			if (StendhalRPRuleProcessor.get().getPlayer(who) != null) {
				online = 1;
			}
			listBuddies.put("_" + who, online);
		}

		Log4J.finishMethod(logger, "addBuddy");
	}

	private void onRemoveBuddy(Player player, RPAction action) {
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
