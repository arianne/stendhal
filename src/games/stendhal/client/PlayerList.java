package games.stendhal.client;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Manages a list of player names
 * 
 * @author madmetzger
 */
public class PlayerList {
	
	private static final Logger logger = Logger.getLogger(PlayerList.class);
	
	private Set<String> namesList = new HashSet<String>();

	public Set<String> getNamesList() {
		return namesList;
	}
	
	public void removePlayer(String player) {
		logger.debug("Player "+player+" removed.");
		namesList.remove(player);
		logger.debug("Currently in list after remove: "+namesList);
	}
	
	public void addPlayer(String player) {
		logger.debug("Player "+player+" added.");
		namesList.add(player);
		logger.debug("Currently in list after add: "+namesList);
	}

	public boolean contains(String player) {
		return namesList.contains(player);
	}
}
