package games.stendhal.server.core.engine;

import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.entity.player.Player;

import java.util.LinkedList;
import java.util.List;

public class PlayerList {

	public PlayerList() {
		players = new LinkedList<Player>();
	}

	
	private List<Player> players;

	
	/**
	 * use <code>forAllPlayersExecute(Task task) </code> instead.
	 * 
	 * @return
	 */
	@Deprecated
	public List<Player> getPlayers() {
		return players;
	}

	
	/**
	 * Retrieve from this list a player specified by its name.
	 * @param name the unique name of a player
	 * @return the Player specified by the name or <code> null </code> if not found
	 */
	Player getOnlinePlayer(String name) {
		for (Player player : players) {
			if (player.getTitle().equals(name)) {
				return player;
			}
		}
		return null;
	}

	
	/**
	 * Sends a privateText to all players in the list.
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
	 * @param task the task to execute
	 */
	public void forAllPlayersExecute(Task<Player> task) {
		for (Player player : players) {
			task.execute(player);
		}

	}

	/**
	 * Calls the execute method of task for all player in this list that return true in filter.
	 * 
	 * @param task 	the task to execute. 
	 * @param filter the FilterCriteria to pass
	 */
	public void forFilteredPlayersExecute(Task<Player> task, FilterCriteria<Player> filter) {
		for (Player player : players) {
			if (filter.passes(player)) {
				task.execute(player);
			}
		}
	}

	/**
	 * The amount of currently  logged in players.
	 * @return
	 */
	public int size() {
		return players.size();
	}

	public void add(Player player) {
		players.add(player);
		
	}

	public boolean remove(Player player) {
		return players.remove(player);
		
	}

}
