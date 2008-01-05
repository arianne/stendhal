package games.stendhal.server.core.events;

import games.stendhal.server.entity.RPEntity;

/**
 * Implementing classes will be called back when a player uses them.
 */
public interface UseListener {

	/**
	 * Invoked when the object is used.
	 * 
	 * @param user
	 *            the RPEntity who uses the object
	 */
	boolean onUsed(RPEntity user);
}
