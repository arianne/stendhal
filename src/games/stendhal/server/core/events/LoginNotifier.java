package games.stendhal.server.core.events;

import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Other classes can register here to be notified when a player logs in.
 * 
 * It is the responsibility of the LoginListener to determine which players are
 * of interest for it, and to store this information persistently.
 * 
 * @author daniel
 */
public class LoginNotifier {

	/** The Singleton instance * */
	private static LoginNotifier instance;

	/**
	 * This Map maps each player name to the set of all events that will take
	 * place when that player logs in. Players for whom no event should take
	 * place needn't be registered here.
	 */
	private List<LoginListener> listeners;

	// singleton
	private LoginNotifier() {
		listeners = new ArrayList<LoginListener>();
	}

	/**
	 * Return the LoginNotifier instance.
	 * 
	 * @return LoginNotifier the Singleton instance
	 */
	public static LoginNotifier get() {
		if (instance == null) {
			instance = new LoginNotifier();
		}
		return instance;
	}

	/**
	 * adds a LoginListener
	 * 
	 * @param listener
	 *            LoginListener
	 */
	public void addListener(LoginListener listener) {
		listeners.add(listener);
	}

	/**
	 * removes a LoginListener
	 * 
	 * @param listener
	 *            LoginListener
	 */
	public void removeListener(LoginListener listener) {
		listeners.remove(listener);
	}

	/**
	 * This method is invoked by Player.create().
	 * 
	 * @param player
	 *            the player who logged in
	 */
	public void onPlayerLoggedIn(Player player) {
		for (LoginListener listener : listeners) {
			listener.onLoggedIn(player);
		}
	}
}
