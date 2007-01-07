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
import games.stendhal.server.entity.player.Player;
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
		StendhalRPRuleProcessor.register("ignore", buddy);
		StendhalRPRuleProcessor.register("removebuddy", buddy);
	}

	@Override
	public void onAction(Player player, RPAction action) {
		if (action.get("type").equals("addbuddy")) {
			onAddBuddy(player, action);
		} else if (action.get("type").equals("removebuddy")){
			onRemoveBuddy(player, action);
		} else if (action.get("type").equals("ignore")) {
			onAddIgnore(player, action);
		}
	}
	
	private void addToSpecialSlot(Player player, RPAction action, String slotName, int status) {
		if (action.has("target")) {
			String who = action.get("target");
			RPSlot slot = player.getSlot(slotName);

			RPObject listBuddies = null;

			if (slot.size() > 0) {
				listBuddies = slot.iterator().next();
			} else {
				listBuddies = new RPObject();
				slot.assignValidID(listBuddies);
				slot.add(listBuddies);
			}

			listBuddies.put("_" + who, status);
			System.out.println(listBuddies);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "buddy", "add", slotName, who);
		}
	}

	private void onAddBuddy(Player player, RPAction action) {
		String who = action.get("target");
		int online = 0;
		if (StendhalRPRuleProcessor.get().getPlayer(who) != null) {
			online = 1;
		}
		addToSpecialSlot(player, action, "!buddy", online);
	}
	
	private void onAddIgnore(Player player, RPAction action) {
		String who = action.get("target");
		addToSpecialSlot(player, action, "!ignore", 0);
		player.sendPrivateText(who + " was added to your ignore list.");
	}

	private void onRemoveBuddy(Player player, RPAction action) {
		Log4J.startMethod(logger, "removeBuddy");

		if (action.has("target")) {
			String who = action.get("target");

			for (String slotName : new String[] { "!buddy", "!ignore"}) {
				RPSlot slot = player.getSlot(slotName);

				RPObject listBuddies = null;

				if (slot.size() > 0) {
					listBuddies = slot.getFirst();

					if (listBuddies.has("_" + who)) {
						listBuddies.remove("_" + who);
						StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "buddy", "remove", slotName, who);
					}
				}
			}
		}

		Log4J.finishMethod(logger, "removeBuddy");
	}
}
