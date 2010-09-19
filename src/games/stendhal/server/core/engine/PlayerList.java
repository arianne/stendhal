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
package games.stendhal.server.core.engine;

import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.entity.player.Player;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerList {

	public PlayerList() {
		players = new ConcurrentHashMap<String, Player>();
	}

	private final Map<String, Player> players;

	/**
	 * Retrieve from this list a player specified by its name.
	 * 
	 * @param name
	 *            the unique name of a player
	 * @return the Player specified by the name or <code> null </code> if not
	 *         found
	 */
	Player getOnlinePlayer(final String name) {
		if (name == null) {
			return null;
		}
		return players.get(name.toLowerCase());
	}

	/**
	 * Sends a privateText to all players in the list.
	 * 
	 * @param message
	 */
	void tellAllOnlinePlayers(final String message) {
		forAllPlayersExecute(new Task<Player>() {
			public void execute(final Player player) {
				player.sendPrivateText(message);
				player.notifyWorldAboutChanges();
			}
		});
	}

	/**
	 * Calls the execute method of task for each player in this List.
	 * 
	 * @param task
	 *            the task to execute
	 */
	public void forAllPlayersExecute(final Task<Player> task) {
		final Iterator<Map.Entry<String, Player>> it = players.entrySet().iterator();
		while (it.hasNext()) {
			task.execute(it.next().getValue());
		}
	}

	/**
	 * Calls the execute method of task for all player in this list that return
	 * true in filter.
	 * 
	 * @param task
	 *            the task to execute.
	 * @param filter
	 *            the FilterCriteria to pass
	 */
	public void forFilteredPlayersExecute(final Task<Player> task, final FilterCriteria<Player> filter) {
		final Iterator<Map.Entry<String, Player>> it = players.entrySet().iterator();

		while (it.hasNext()) {

			final Player player = it.next().getValue();

			if (filter.passes(player)) {
				task.execute(player);
			}
		}
	}

	/**
	 * The amount of currently logged in players.
	 * 
	 * @return the amount Player items in this list.
	 */
	public int size() {
		return players.size();
	}

	public void add(final Player player) {
		final String playerName = player.getName();

		if (playerName != null) {
			players.put(playerName.toLowerCase(), player);
		} else {
			throw new IllegalArgumentException("can't add player without name");
		}
	}

	public boolean remove(final Player player) {
		final String playerName = player.getName();

		if (playerName != null) {
			return players.remove(playerName.toLowerCase()) != null;
		} else {
			throw new IllegalArgumentException("can't remove player without name:");
		}
	}
	
	public Collection<Player> getAllPlayers() {
		return players.values();
	}

}
