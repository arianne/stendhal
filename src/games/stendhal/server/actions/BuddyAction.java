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
import marauroa.common.game.RPAction;

public class BuddyAction implements ActionListener {

	public static void register() {
		BuddyAction buddy = new BuddyAction();
		StendhalRPRuleProcessor.register("addbuddy", buddy);
		StendhalRPRuleProcessor.register("grumpy", buddy);
		StendhalRPRuleProcessor.register("ignore", buddy);
		StendhalRPRuleProcessor.register("removebuddy", buddy);
		StendhalRPRuleProcessor.register("unignore", buddy);
	}

	public void onAction(Player player, RPAction action) {
		if (action.get("type").equals("addbuddy")) {
			onAddBuddy(player, action);
		} else if (action.get("type").equals("removebuddy")) {
			onRemoveBuddy(player, action);
		} else if (action.get("type").equals("ignore")) {
			onIgnore(player, action);
		} else if (action.get("type").equals("unignore")) {
			onUnignore(player, action);
		} else if (action.get("type").equals("grumpy")) {
			onGrumpy(player, action);
		}

	}

	private void onAddBuddy(Player player, RPAction action) {
		String who = action.get("target");
		String online = "0";
		Player buddy = StendhalRPRuleProcessor.get().getPlayer(who);
		if (buddy != null && !buddy.isGhost()) {
			online = "1";
		}
		player.setKeyedSlot("!buddy", "_" + who, online);

		StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "buddy",
				"add", who);
	}

	private void onIgnore(Player player, RPAction action) {
		int duration;
		String reason;

		if (action.has("target")) {
			String who = action.get("target");

			if (action.has("duration")) {
				duration = action.getInt("duration");
			} else {
				duration = 0;
			}

			if (action.has("reason")) {
				reason = action.get("reason");
			} else {
				reason = null;
			}

			if (player.addIgnore(who, duration, reason)) {
				player.sendPrivateText(who + " was added to your ignore list.");
			}
		}

	}

	private void onUnignore(Player player, RPAction action) {

		if (action.has("target")) {
			String who = action.get("target");

			if (player.removeIgnore(who)) {
				player.sendPrivateText(who
						+ " was removed from your ignore list.");
			}
		}

	}

	private void onRemoveBuddy(Player player, RPAction action) {

		if (action.has("target")) {
			String who = action.get("target");

			player.setKeyedSlot("!buddy", "_" + who, null);

			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"buddy", "remove", who);

			// TEMP! Supreceeded by /unignore
			player.removeIgnore(who);
		}

	}

	/**
	 * Handle a Grumpy action.
	 *
	 * @param	player		The player.
	 * @param	action		The action.
	 */
	protected void onGrumpy(Player player, RPAction action) {
		if (action.has("reason")) {
			player.setGrumpyMessage(action.get("reason"));
		} else {
			player.setGrumpyMessage(null);	
		}
		player.notifyWorldAboutChanges();
	}
}
