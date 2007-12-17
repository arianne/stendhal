package games.stendhal.server.core.engine;

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
		for (Player player : getPlayers()) {
			if (player.getTitle().equals(name)) {
				return player;
			}
		}
		return null;
	}



	void tellAllOnlinePlayers( final String message) {
		for (Player player : getPlayers()) {
			player.sendPrivateText(message);
			player.notifyWorldAboutChanges();
		}
	}



	public  int size() {
		return players.size(); 
	}

}
