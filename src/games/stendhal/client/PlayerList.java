package games.stendhal.client;

import java.util.HashSet;
import java.util.Set;

public class PlayerList {
	
	private Set<String> namesList = new HashSet<String>();

	public Set<String> getNamesList() {
		return namesList;
	}
	
	public void removePlayer(String player) {
		namesList.remove(player);
	}
	
	public void addPlayer(String player) {
		namesList.add(player);
	}

	public boolean contains(String player) {
		return namesList.contains(player);
	}
}
