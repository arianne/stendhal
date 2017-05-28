/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import games.stendhal.common.NotificationType;
import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.entity.player.Player;

/**
 * Manages the list of online players.
 *
 * @author durkham
 */
public class PlayerList {

	/**
	 * Creates a new PlayerList.
	 */
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
	 * @param notificationType type of the notification
	 * @param message message to tell all online players
	 */
	void tellAllOnlinePlayers(final NotificationType notificationType, final String message) {
		forAllPlayersExecute(new Task<Player>() {

			@Override
			public void execute(final Player player) {
				player.sendPrivateText(notificationType, message);
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
		for(Player player : players.values()) {
			task.execute(player);
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
		for(Player player : players.values()) {
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

	/**
	 * adds a player
	 *
	 * @param player player
	 */
	public void add(final Player player) {
		final String playerName = player.getName();

		if (playerName != null) {
			players.put(playerName.toLowerCase(), player);
		} else {
			throw new IllegalArgumentException("can't add player without name");
		}
	}

	/**
	 * removes a player
	 *
	 * @param player player
	 * @return true, if the player was in the list
	 */
	public boolean remove(final Player player) {
		final String playerName = player.getName();

		if (playerName != null) {
			return players.remove(playerName.toLowerCase()) != null;
		} else {
			throw new IllegalArgumentException("can't remove player without name");
		}
	}

	/**
	 * gets a mutable list of all players
	 *
	 * @return list of all players
	 */
	public Collection<Player> getAllPlayers() {
		return players.values();
	}

}
