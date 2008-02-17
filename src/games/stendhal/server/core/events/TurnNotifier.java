package games.stendhal.server.core.events;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;

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
public final class TurnNotifier {

	private static Logger logger = Logger.getLogger(TurnNotifier.class);

	/** The Singleton instance. */
	private static TurnNotifier instance;

	private int currentTurn = -1;

	/**
	 * This Map maps each turn to the set of all events that will take place at
	 * this turn. Turns at which no event should take place needn't be
	 * registered here.
	 */
	private Map<Integer, Set<TurnListener>> register = new HashMap<Integer, Set<TurnListener>>();

	/** Used for multi-threading synchronization. * */
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
	 * @param currentTurn
	 *            currentTurn
	 */

	public void logic(int currentTurn) {
		// Note: It is OK to only synchronise the remove part
		// because notifyAtTurn will not allow registrations
		// for the current turn. So it is important to
		// adjust currentTurn before the loop.

		this.currentTurn = currentTurn;

		// get and remove the set for this turn
		Set<TurnListener> set = null;
		synchronized (sync) {
			set = register.remove(Integer.valueOf(currentTurn));
		}

		if (logger.isDebugEnabled()) {
			StringBuffer os = new StringBuffer();
			os.append("register: " + register.size() + "\n");
			os.append("set: " + (set != null ? set.size() : 0) + "\n");
			logger.info(os);
		}

		if (set != null) {
			for (TurnListener event : set) {
				TurnListener turnListener = event;
				try {
					if (logger.isDebugEnabled()) {
						logger.info(turnListener);
					}

					turnListener.onTurnReached(currentTurn);
				} catch (RuntimeException e) {
					logger.error(e, e);
				}
			}
		}
	}

	/**
	 * Return the number of the next turn.
	 * 
	 * @return number of the next turn
	 */

	public int getNumberOfNextTurn() {
		return this.currentTurn + 1;
	}

	/**
	 * Notifies the <i>turnListener</i> in <i>diff</i> turns.
	 * 
	 * @param diff
	 *            the number of turns to wait before notifying
	 * @param turnListener
	 *            the object to notify
	 */

	public void notifyInTurns(int diff, TurnListener turnListener) {
		notifyAtTurn(currentTurn + diff + 1, turnListener);
	}

	/**
	 * Notifies the <i>turnListener</i> in <i>sec</i> seconds.
	 * 
	 * @param sec
	 *            the number of seconds to wait before notifying
	 * @param turnListener
	 *            the object to notify
	 */
	public void notifyInSeconds(int sec, TurnListener turnListener) {
		notifyInTurns(SingletonRepository.getRPWorld().getTurnsInSeconds(sec),
				turnListener);
	}

	/**
	 * Notifies the <i>turnListener</i> at turn number <i>turn</i>.
	 * 
	 * @param turn
	 *            the number of the turn
	 * @param turnListener
	 *            the object to notify
	 */

	public void notifyAtTurn(int turn, TurnListener turnListener) {
		if (logger.isDebugEnabled()) {
			logger.info("Notify at " + turn + " by " + turnListener);
			StringBuffer st = new StringBuffer();

			for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
				st.append(e);
				st.append("\n");
			}

			logger.info(st);
		}

		if (turn <= currentTurn) {
			logger.error("requested turn " + turn
					+ " is in the past. Current turn is " + currentTurn,
					new IllegalArgumentException("turn"));
			return;
		}

		synchronized (sync) {
			// do we have other events for this turn?
			Integer turnInt = Integer.valueOf(turn);
			Set<TurnListener> set = register.get(turnInt);
			if (set == null) {
				set = new HashSet<TurnListener>();
				register.put(turnInt, set);
			}
			// add it to the list
			set.add(turnListener);
		}
	}

	/**
	 * Forgets all registered notification entries for the given TurnListener
	 * where the entry's message equals the given one.
	 * 
	 * @param turnListener
	 */

	public void dontNotify(TurnListener turnListener) {
		// all events that are equal to this one should be forgotten.
		// TurnEvent turnEvent = new TurnEvent(turnListener);
		for (Map.Entry<Integer, Set<TurnListener>> mapEntry : register.entrySet()) {
			Set<TurnListener> set = mapEntry.getValue();
			// We don't remove directly, but first store in this
			// set. This is to avoid ConcurrentModificationExceptions.
			Set<TurnListener> toBeRemoved = new HashSet<TurnListener>();
			for (TurnListener currentEvent : set) {
				if (currentEvent.equals(turnListener)) {
					toBeRemoved.add(currentEvent);
				}
			}
			for (TurnListener event : toBeRemoved) {
				set.remove(event);
			}
		}
	}

	/**
	 * Finds out how many turns will pass until the given TurnListener will be
	 * notified with the given message.
	 * 
	 * @param turnListener
	 * @return the number of remaining turns, or -1 if the given TurnListener
	 *         will not be notified with the given message.
	 */

	public int getRemainingTurns(TurnListener turnListener) {
		// all events match that are equal to this.
		// TurnEvent turnEvent = new TurnEvent(turnListener);
		// the HashMap is unsorted, so we need to run through
		// all of it.
		List<Integer> matchingTurns = new ArrayList<Integer>();
		for (Map.Entry<Integer, Set<TurnListener>> mapEntry : register.entrySet()) {
			Set<TurnListener> set = mapEntry.getValue();
			for (TurnListener currentEvent : set) {
				if (currentEvent.equals(turnListener)) {
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
	 * Finds out how many seconds will pass until the given TurnListener will be
	 * notified with the given message.
	 * 
	 * @param turnListener
	 * @return the number of remaining seconds, or -1 if the given TurnListener
	 *         will not be notified with the given message.
	 */

	public int getRemainingSeconds(TurnListener turnListener) {

		return (getRemainingTurns(turnListener) * StendhalRPWorld.MILLISECONDS_PER_TURN) / 1000;
	}

	/**
	 * Returns the list of events. Note this is only for debugging the
	 * TurnNotifier
	 * 
	 * @return eventList
	 */
	public Map<Integer, Set<TurnListener>> getEventListForDebugging() {
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
