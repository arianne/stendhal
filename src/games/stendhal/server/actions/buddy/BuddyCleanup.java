/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2010-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package games.stendhal.server.actions.buddy;

import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.dbcommand.CheckCharactersExistingCommand;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnListenerDecorator;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import marauroa.server.db.command.DBCommand;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.db.command.ResultHandle;

/**
 * Clean up the buddy list from obsolete and duplicate entries
 * using case insensitive matching.
 */
public class BuddyCleanup implements TurnListener {

	private Player player;
	private ResultHandle handle = new ResultHandle();

	public BuddyCleanup(Player player) {
		this.player = player;
	}

	public void cleanup() {
		Map<String, String> buddies = player.getMap("buddies");

		if (buddies != null) {
			Set<String> lowerBuddies = new HashSet<String>();
			List<String> duplicates = new ArrayList<String>();

			// collect all buddy names in lower case and detect duplicates
			for(String name : buddies.keySet()) {
				if (!lowerBuddies.add(name.toLowerCase())) {
					duplicates.add(name);
				}
			}

			// remove duplicate buddy names
			for(String duplicateName : duplicates) {
				player.removeBuddy(duplicateName);
				new GameEvent(player.getName(), "buddy", "remove", duplicateName).raise();
			}

			// invoke the check for valid character names
			DBCommand command = new CheckCharactersExistingCommand(player, player.getBuddies());
			DBCommandQueue.get().enqueueAndAwaitResult(command, handle);
			TurnNotifier.get().notifyInTurns(0, new TurnListenerDecorator(this));
		}
	}

	public void onTurnReached(int currentTurn) {
		CheckCharactersExistingCommand checkCommand = DBCommandQueue.get().getOneResult(CheckCharactersExistingCommand.class, handle);

		if (checkCommand == null) {
			TurnNotifier.get().notifyInTurns(0, new TurnListenerDecorator(this));
			return;
		}

		Set<String> queriedNames = checkCommand.getQueriedNames();
		Set<String> validNames = checkCommand.getValidNames();

		// compute the difference between the two name sets
		Set<String> oldNames = new HashSet<String>();
		oldNames.addAll(queriedNames);
		for(String name : validNames) {
			oldNames.remove(name);
		}

		Set<String> newNames = new HashSet<String>();
		newNames.addAll(validNames);
		for(String name : queriedNames) {
			newNames.remove(name);
		}

		// remove invalid buddy names
		for(String invalidName : oldNames) {
			if (player.removeBuddy(invalidName)) {
				new GameEvent(player.getName(), "buddy", "remove", invalidName).raise();
			}
		}

		// add renamed buddy entries
		for(String newName : newNames) {
			final Player buddy = SingletonRepository.getRuleProcessor().getPlayer(newName);

			if (player.addBuddy(newName, buddy!=null && !buddy.isGhost())) {
				new GameEvent(player.getName(), "buddy", "add", newName).raise();
			}
		}
	}
}
