package games.stendhal.server.events;

import games.stendhal.server.StendhalRPWorld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Other classes can register here to be notified at some time in the future.
 * 
 * @author hendrik, daniel
 */
public class TurnNotifier {

	/**
	 * Struct to store a pair of TurnListener and String.
	 */
	// TODO: get rid of this class after the message parameter is not used anymore
	// this class uses lots of memory
	public static class TurnEvent {

		public TurnListener turnListener;

		public String message;

		public TurnEvent(TurnListener turnListener, String message) {
			this.turnListener = turnListener;
			this.message = message;
		}

		public boolean equals(TurnEvent other) {
			return (turnListener == other.turnListener)
			        && (((message == null) && (other.message == null)) || message.equals(other.message));
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
	 * @param diff the number of turns to wait before notifying
	 * @param turnListener the object to notify
	 */
	// internally uses old code for compatibility; will be rewritten after 
	// the other method is not required by external classes anymore
	@SuppressWarnings("deprecation")
	public void notifyInTurns(int diff, TurnListener turnListener) {
		notifyInTurns(diff, turnListener, null);
	}

	/**
	 * Notifies the <i>turnListener</i> in <i>diff</i> turns.
	 * 
	 * @param diff the number of turns to wait before notifying
	 * @param turnListener the object to notify
	 * @param message an object to pass to the event handler
	 * @deprecated use notifyInTurns(int, TurnListener) with an anon inner class 
	 */
	@Deprecated
	public void notifyInTurns(int diff, TurnListener turnListener, String message) {
		notifyAtTurn(currentTurn + diff + 1, turnListener, message);
	}

	/**
	 * Notifies the <i>turnListener</i> in <i>sec</i> seconds.
	 * 
	 * @param sec the number of seconds to wait before notifying
	 * @param turnListener the object to notify
	 */
	// internally uses old code for compatibility; will be rewritten after 
	// the other method is not required by external classes anymore
	@SuppressWarnings("deprecation")
	public void notifyInSeconds(int sec, TurnListener turnListener) {
		notifyInSeconds(sec, turnListener, null);
	}

	/**
	 * Notifies the <i>turnListener</i> in <i>sec</i> seconds.
	 * 
	 * @param sec the number of seconds to wait before notifying
	 * @param turnListener the object to notify
	 * @param message an object to pass to the event handler
	 * @deprecated use notifyInSeconds(int, TurnListener) with an anon inner class 
	 */
	@Deprecated
	public void notifyInSeconds(int sec, TurnListener turnListener, String message) {
		notifyInTurns(StendhalRPWorld.get().getTurnsInSeconds(sec), turnListener, message);
	}

	/**
	 * Notifies the <i>turnListener</i> at turn number <i>turn</i>.
	 * 
	 * @param turn the number of the turn
	 * @param turnListener the object to notify
	 */
	// internally uses old code for compatibility; will be rewritten after 
	// the other method is not required by external classes anymore
	@SuppressWarnings("deprecation")
	public void notifyAtTurn(int turn, TurnListener turnListener) {
		notifyAtTurn(turn, turnListener, null);
	}

	/**
	 * Notifies the <i>turnListener</i> at turn number <i>turn</i>.
	 * 
	 * @param turn the number of the turn
	 * @param turnListener the object to notify
	 * @param message an object to pass to the event handler
	 * @deprecated use notifyAtTurn(int, TurnListener) with an anon inner class 
	 */
	@Deprecated
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
	 *
	 * @param turnListener
	 */
 	// internally uses old code for compatibility; will be rewritten after 
	// the other method is not required by external classes anymore
	@SuppressWarnings("deprecation")
	public void dontNotify(TurnListener turnListener) {
		dontNotify(turnListener, null);
	}

	/**
	 * Forgets all registered notification entries for the given TurnListener
	 * where the entry's message equals the given one.
	 *
	 * @param turnListener
	 * @param message
	 * @deprecated use dontNotify(TurnListener) with an anon inner class 
	 */
	@Deprecated
	public void dontNotify(TurnListener turnListener, String message) {
		// all events that are equal to this one should be forgotten.
		TurnEvent turnEvent = new TurnEvent(turnListener, message);
		for (Map.Entry<Integer, Set<TurnEvent>> mapEntry : register.entrySet()) {
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
	/**
	 * Finds out how many turns will pass until the given TurnListener
	 * will be notified with the given message.
	 *
	 * @param turnListener
	 * @return the number of remaining turns, or -1 if the given TurnListener
	 *         will not be notified with the given message.
	 */
	// internally uses old code for compatibility; will be rewritten after 
	// the other method is not required by external classes anymore
	@SuppressWarnings("deprecation")
	public int getRemainingTurns(TurnListener turnListener) {
		return  getRemainingTurns(turnListener, null);
	}
	/**
	 * Finds out how many turns will pass until the given TurnListener
	 * will be notified with the given message.
	 *
	 * @param turnListener
	 * @param message
	 * @return the number of remaining turns, or -1 if the given TurnListener
	 *         will not be notified with the given message.
	 * @deprecated use getRemainingTurns(TurnListener) with an anon inner class 
	 */
	@Deprecated
	public int getRemainingTurns(TurnListener turnListener, String message) {
		// all events match that are equal to this.
		TurnEvent turnEvent = new TurnEvent(turnListener, message);
		// the HashMap is unsorted, so we need to run through
		// all of it.
		List<Integer> matchingTurns = new ArrayList<Integer>();
		for (Map.Entry<Integer, Set<TurnEvent>> mapEntry : register.entrySet()) {
			Set<TurnEvent> set = mapEntry.getValue();
			for (TurnEvent currentEvent : set) {
				if (currentEvent.equals(turnEvent)) {
					matchingTurns.add(mapEntry.getKey());
				}
			}
		}
		if (matchingTurns.size() > 0) {
			Collections.sort(matchingTurns);
			return matchingTurns.get(0).intValue() - currentTurn;
		} else {
			return -1;
		}
	}

	/**
	 * Finds out how many seconds will pass until the given TurnListener
	 * will be notified with the given message.
	 *
	 * @param turnListener
	 * @return the number of remaining seconds, or -1 if the given TurnListener
	 *         will not be notified with the given message.
	 */
	// internally uses old code for compatibility; will be rewritten after 
	// the other method is not required by external classes anymore
	@SuppressWarnings("deprecation")
	public int getRemainingSeconds(TurnListener turnListener) {
		return getRemainingSeconds(turnListener, null);
	}

	/**
	 * Finds out how many seconds will pass until the given TurnListener
	 * will be notified with the given message.
	 *
	 * @param turnListener
	 * @param message
	 * @return the number of remaining seconds, or -1 if the given TurnListener
	 *         will not be notified with the given message.
	 * @deprecated use getRemainingSeconds(TurnListener) with an anon inner class 
	 */
	@Deprecated
	public int getRemainingSeconds(TurnListener turnListener, String message) {
		return (getRemainingTurns(turnListener, message) * StendhalRPWorld.MILLISECONDS_PER_TURN) / 1000;
	}

	/**
	 * Returns the list of events. Note this is only for debugging the TurnNotifier
	 *
	 * @return eventList
	 */
	public Map<Integer, Set<TurnEvent>> getEventListForDebugging() {
		 return register;
	}
	
	/**
	 * Returns the current turn. Note this is only for debugging TurnNotifier
	 *
	 * @return current turn
	 */
	public int getCurrentTurnForDebugging() {
		return currentTurn;
	}
}
