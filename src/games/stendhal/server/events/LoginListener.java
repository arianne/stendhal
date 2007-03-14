package games.stendhal.server.events;

/**
 * Implementing classes can be notified that a certain player has
 * logged in.
 * 
 * After registering at the LoginNotifier, the LoginNotifier will wait
 * until the specified user has logged in, and notify the
 * LoginListener.
 * 
 * A string can be passed to the LoginNotifier while registering; this
 * string will then be passed back to the LoginListener when the specified
 * player has logged in. Using this string, a LoginListener can
 * register itself multiple times at the LoginNotifier. 
 *
 * @author daniel
 */
public interface LoginListener {

	/**
	 * This method is called when the turn number is reached
	 *
	 * @param player the player who has logged in
	 * @param message the string that was used 
	 */
	public void onLoggedIn(String playerName, String message);
}
