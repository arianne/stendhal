package games.stendhal.server.events;

import games.stendhal.server.entity.RPEntity;

/**
 * Implementing classes will be called back when a player uses them. 
 */
public interface UseListener {
	public void onUsed(RPEntity user);
}
