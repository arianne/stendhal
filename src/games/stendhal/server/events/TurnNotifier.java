package games.stendhal.server.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Other classes can register here to be notified at some time in the future.
 * 
 * 
 * 
 * @author hendrik
 * @author daniel
 */
public class TurnNotifier {
	
	/**
	 * Struct to store a pair of TurnListener and String.
	 */
	protected static class TurnEvent {
		public TurnListener turnListener;
		
		public String message; 
		
		public TurnEvent(TurnListener turnListener, String message) {
			this.turnListener = turnListener;
			this.message = message;
		}
		
		public boolean equals(TurnEvent other) {
			return turnListener == other.turnListener
					&& ((message == null && other.message == null)
							|| message.equals(other.message));
		}
	}
	
	private static Logger logger = Logger.getLogger(TurnNotifier.class);
	
	/** The Singleton instance **/
	private static TurnNotifier instance = null;
	
	private int currentTurn = -1;
	
	/**
	 * This Map maps each turn to the set of all events that will take place
	 * at this turn.
	 * Turns at which no event should take place needn't be registered here.
	 */
	private Map<Integer, Set<TurnEvent>> register = new HashMap<Integer, Set<TurnEvent>>();
	
	/** Used for multi-threading synchronization. **/
	private final Object sync = new Object();

	private TurnNotifier() {
		// singleton
	}

	/**
	 * Return the TurnNotifier instance.
	 *
	 * @return TurnNotifier the Singleton instance
	 */
	public static TurnNotifier get() {
		if (instance == null) {
			instance = new TurnNotifier();
		}
		return instance;
	}

	/**
	 * This method is invoked by StendhalRPRuleProcessor.endTurn().
	 *
	 * @param currentTurn currentTurn
	 */
	public void logic(int currentTurn) {
		// Note: It is OK to only synchronize the remove part
		//       because notifyAtTurn will not allow registrations
		//       for the current turn. So it is important to
		//       adjust currentTurn before the loop.

		this.currentTurn = currentTurn;

		// get and remove the set for this turn
		Set<TurnEvent> set = null;
		synchronized (sync) {
			set = register.remove(new Integer(currentTurn));
		}

		if (set != null) {
			for (TurnEvent event : set) {
				TurnListener turnListener = event.turnListener;
				String message = event.message;
				try {
					turnListener.onTurnReached(currentTurn, message);
				} catch (RuntimeException e) {
					logger.error(e, e);
				}				
			}
		}
	}

	/**
	 * Return the number of the next turn
	 *
	 * @return number of the next turn
	 */
	public int getNumberOfNextTurn() {
		return this.currentTurn + 1;
	}
	
	/**
	 * Notifies the <i>turnListener</i> in <i>diff</i> turns.
	 * 
	 * @param diff the number of turns to wait
	 * @param turnListener the object to notify
	 * @param message an object to pass to the event handler
	 */
	public void notifyInTurns(int diff, TurnListener turnListener, String message) {
		notifyAtTurn(currentTurn + diff + 1, turnListener, message);
	}

	/**
	 * Notifies the <i>turnListener</i> at turn number <i>turn</i>.
	 * 
	 * @param turn the number of the turn
	 * @param turnListener the object to notify
	 * @param message an object to pass to the event handler
	 */
	public void notifyAtTurn(int turn, TurnListener turnListener, String message) {
		if (turn <= currentTurn) {
			logger.error("requested turn " + turn + " is in the past. Current turn is " + currentTurn, new Throwable());
			return;
		}
		synchronized (sync) {
			// do we have other events for this turn?
			Integer turnInt = new Integer(turn);
			Set<TurnEvent> set = register.get(turnInt);
			if (set == null) {
				set = new HashSet<TurnEvent>();
				register.put(turnInt, set);
			}
			// add it to the list
			set.add(new TurnEvent(turnListener, message));
		}
	}
	
	/**
	 * Forgets all registered notification entries for the given TurnListener
	 * where the entry's message equals the given one. 
	 * @param turnListener
	 * @param message
	 */
	public void dontNotify(TurnListener turnListener, String message) {
		// all events that are equal to this one should be forgotten.
		TurnEvent turnEvent = new TurnEvent(turnListener, message);
		for (Map.Entry<Integer, Set<TurnEvent>> mapEntry: register.entrySet()) {
			Set<TurnEvent> set = mapEntry.getValue();
			// We don't remove directly, but first store in this
			// set. This is to avoid ConcurrentModificationExceptions. 
			Set<TurnEvent> toBeRemoved = new HashSet<TurnEvent>();
			for (TurnEvent currentEvent : set) {
				if (currentEvent.equals(turnEvent)) {
					toBeRemoved.add(currentEvent);
				}
			}
			for (TurnEvent event : toBeRemoved) {
				set.remove(event);
			}
		}
	}
}
