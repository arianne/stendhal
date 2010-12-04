/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.query;

import static games.stendhal.common.constants.Actions.PROGRESS_STATUS;
import games.stendhal.common.NotificationType;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ProgressStatusEvent;

import java.util.Arrays;
import java.util.List;

import marauroa.common.game.RPAction;

/**
 * queries the server about the progress.
 *
 * @author hendrik
 */
public class ProgressStatusQueryAction implements ActionListener {
	

	/**
	 * registers this action.
	 */
	public static void register() {
		CommandCenter.register(PROGRESS_STATUS, new ProgressStatusQueryAction());
	}

	/**
	 * processes the requested action.
	 * 
	 * @param player the caller of the action
	 * @param action the action to be performed
	 */
	public void onAction(final Player player, final RPAction action) {
		if (!action.has("progress_type")) {
			sendProgressTypes(player);
		} else if (!action.has("item")) {
			sendItemList(player, action.get("progress_type"));
		} else {
			sendDetails(player, action.get("progress_type"), action.get("item"));
		}

		player.notifyWorldAboutChanges();
	}

	/**
	 * sends a list of supported progress types
	 *
	 * @param player Player to sent the event to
	 */
	private void sendProgressTypes(Player player) {
		List<String> list = Arrays.asList("Open Quests", "Completed Quests"); //, "Producing");
		player.addEvent(new ProgressStatusEvent(list));
	}

	/**
	 * sends a list of items in the specified progress types
	 *
	 * @param player Player to sent the event to
	 * @param progressType the type of progress the player is interested in
	 */
	private void sendItemList(Player player, String progressType) {
		if (progressType.equals("Open Quests")) {
			player.addEvent(new ProgressStatusEvent(progressType, 
				SingletonRepository.getStendhalQuestSystem().getOpenQuests(player)));
		} else if (progressType.equals("Completed Quests")) {
			player.addEvent(new ProgressStatusEvent(progressType, 
				SingletonRepository.getStendhalQuestSystem().getCompletedQuests(player)));
		}
	}

	/**
	 * sends details about a selected item
	 *
	 * @param player Player to sent the event to
	 * @param progressType the type of progress the player is interested in
	 * @param item name of item to get details about
	 */
	private void sendDetails(Player player, String progressType, String item) {
		player.sendPrivateText(NotificationType.ERROR, "Not implemented, yet.");
	}

}
