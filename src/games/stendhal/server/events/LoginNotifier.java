package games.stendhal.server.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Other classes can register here to be notified when a certain
 * player logs in.
 * 
 * Attention: LoginEvents aren't stored persistently, i.e. if the server
 * is restarted, all LoginEvents are lost. Thus, don't use this for anything
 * critical. 
 * 
 * @author daniel
 */
public class LoginNotifier {
	
	/**
	 * Struct to store a pair of LoginListener and String.
	 */
	protected static class LoginEvent {
		public LoginListener loginListener;
		
		public String message; 

		public String playerName; 
		
		public LoginEvent(LoginListener loginListener, String playerName, String message) {
			this.loginListener = loginListener;
			this.message = message;
			this.playerName = playerName;
		}
		
		public boolean equals(LoginEvent other) {
			return (loginListener == other.loginListener)
					&& (((message == null) && (other.message == null))
							|| message.equals(other.message));
		}
	}
	
	private static Logger logger = Logger.getLogger(LoginNotifier.class);
	
	/** The Singleton instance **/
	private static LoginNotifier instance = null;
	
	/**
	 * This Map maps each player name to the set of all events that will
	 * take place when that player logs in.
	 * Players for whom no event should take place needn't be registered here.
	 */
	private Map<String, Set<LoginEvent>> register = new HashMap<String, Set<LoginEvent>>();
	
	/** Used for multi-threading synchronization. **/
	private final Object sync = new Object();

	private LoginNotifier() {
		// singleton
	}

	/**
	 * Return the TurnNotifier instance.
	 *
	 * @return TurnNotifier the Singleton instance
	 */
	public static LoginNotifier get() {
		if (instance == null) {
			instance = new LoginNotifier();
		}
		return instance;
	}

	/**
	 * This method is invoked by Player.create().
	 *
	 * @param playerName the name of the player who logged in
	 */
	public void onPlayerLoggedIn(String playerName) {

		// get and remove the set for this turn
		Set<LoginEvent> set = null;
		synchronized (sync) {
			set = register.remove(playerName);
		}

		if (set != null) {
			for (LoginEvent event : set) {
				LoginListener loginListener = event.loginListener;
				String message = event.message;
				try {
					loginListener.onLoggedIn(playerName, message);
				} catch (RuntimeException e) {
					logger.error(e, e);
				}				
			}
		}
	}

	/**
	 * Notifies the <i>turnListener</i> in <i>diff</i> turns.
	 * 
	 * @param diff the number of turns to wait
	 * @param turnListener the object to notify
	 * @param message an object to pass to the event handler
	 */

	/**
	 * Notifies the <i>turnListener</i> at turn number <i>turn</i>.
	 * 
	 * @param turn the number of the turn
	 * @param turnListener the object to notify
	 * @param message an object to pass to the event handler
	 */
	public void notifyOnLogin(String playerName, LoginListener loginListener, String message) {
		synchronized (sync) {
			// do we have other events for this turn?
			Set<LoginEvent> set = register.get(playerName);
			if (set == null) {
				set = new HashSet<LoginEvent>();
				register.put(playerName, set);
			}
			// add it to the list
			set.add(new LoginEvent(loginListener, playerName, message));
		}
	}
	
	/**
	 * Forgets all registered notification entries for the given LoginListener
	 * where the entry's message equals the given one. 
	 * @param loginListener
	 * @param message
	 */
	public void dontNotify(LoginListener loginListener, String playerName, String message) {
		// all events that are equal to this one should be forgotten.
		LoginEvent loginEvent = new LoginEvent(loginListener, playerName, message);
		for (Map.Entry<String, Set<LoginEvent>> mapEntry: register.entrySet()) {
			Set<LoginEvent> set = mapEntry.getValue();
			// We don't remove directly, but first store in this
			// set. This is to avoid ConcurrentModificationExceptions. 
			Set<LoginEvent> toBeRemoved = new HashSet<LoginEvent>();
			for (LoginEvent currentEvent : set) {
				if (currentEvent.equals(loginEvent)) {
					toBeRemoved.add(currentEvent);
				}
			}
			for (LoginEvent event : toBeRemoved) {
				set.remove(event);
			}
		}
	}
}
