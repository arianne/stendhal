package games.stendhal.server.core.engine;

import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.entity.player.Player;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerList {

	public PlayerList() {
		players = new ConcurrentHashMap<String, Player>();
	}

	private Map<String, Player> players;

	/**
	 * Retrieve from this list a player specified by its name.
	 * 
	 * @param name
	 *            the unique name of a player
	 * @return the Player specified by the name or <code> null </code> if not
	 *         found
	 */
	Player getOnlinePlayer(String name) {
		return players.get(name.toLowerCase());
	}

	/**
	 * Sends a privateText to all players in the list.
	 * 
	 * @param message
	 */
	void tellAllOnlinePlayers(final String message) {
		forAllPlayersExecute(new Task<Player>() {
			public void execute(Player player) {
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
	public void forAllPlayersExecute(Task<Player> task) {
		Iterator<Map.Entry<String, Player>> it = players.entrySet().iterator();
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
	public void forFilteredPlayersExecute(Task<Player> task, FilterCriteria<Player> filter) {
		Iterator<Map.Entry<String, Player>> it = players.entrySet().iterator();

		while (it.hasNext()) {

			Player player = it.next().getValue();

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

	public void add(Player player) {
		String playerName = player.getName();

		if (playerName != null) {
			players.put(playerName.toLowerCase(), player);
		} else {
			throw new IllegalArgumentException("can't add player without name");
		}
	}

	public boolean remove(Player player) {
		String playerName = player.getName();

		if (playerName != null) {
			return players.remove(playerName.toLowerCase()) != null;
		} else {
			throw new IllegalArgumentException("can't remove player without name:");
		}
	}

}
