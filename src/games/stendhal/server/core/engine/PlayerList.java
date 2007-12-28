package games.stendhal.server.core.engine;

import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.entity.player.Player;

import java.util.LinkedList;
import java.util.List;

public class PlayerList {

	public PlayerList() {
		players = new LinkedList<Player>();
	}

	/**
	 * A list of all players who are currently logged in.
	 */
	private List<Player> players;

	public List<Player> getPlayers() {
		return players;
	}

	Player getOnlinePlayer(String name) {
		for (Player player : players) {
			if (player.getTitle().equals(name)) {
				return player;
			}
		}
		return null;
	}

	void tellAllOnlinePlayers(final String message) {
		forAllPlayersExecute(new Task<Player>() {
			public void execute(Player player) {
				player.sendPrivateText(message);
				player.notifyWorldAboutChanges();

			}
		});
	}

	public void forAllPlayersExecute(Task<Player> task) {
		for (Player player : players) {
			task.execute(player);
		}

	}

	public void forFilteredPlayersExecute(Task<Player> task, FilterCriteria<Player> filter) {
		for (Player player : players) {
			if (filter.passes(player)) {
				task.execute(player);
			}
		}
	}

	public int size() {
		return players.size();
	}

	void add(Player player) {
		players.add(player);
		
	}

	public boolean remove(Player player) {
		return players.remove(player);
		
	}

}
