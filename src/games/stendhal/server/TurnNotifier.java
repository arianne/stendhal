package games.stendhal.server;

import games.stendhal.server.events.TurnEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Other classes can register here to be notified sometime in the future.
 *
 * @author hendrik
 */
public class TurnNotifier {
	private static Logger logger = Logger.getLogger(TurnNotifier.class);
	private static TurnNotifier turnNotifier = null;
	private int currentTurn = -1;
	private Map<Integer, Set<TurnEvent>> register = new HashMap<Integer, Set<TurnEvent>>();
	private final Object sync = new Object();

	private TurnNotifier() {
		// signleton
	}

	/**
	 * Return the TurnNotifier
	 *
	 * @return TurnNotifier
	 */
	public static TurnNotifier get() {
		if (turnNotifier == null) {
			turnNotifier = new TurnNotifier();
		}
		return turnNotifier;
	}

	/**
	 * This method is invoked by endTurn()
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
			for (TurnEvent turnEvent : set) {
				turnEvent.onTurnReached(currentTurn);
			}
		}
	}

	/**
	 * Notifies the class <i>turnEvent</i> in <i>diff</i> turns.
	 * 
	 * @param turnEvent the class to notify
	 * @param diff the number of turns to wait
	 */
	public void notifyInTurns(TurnEvent turnEvent, int diff) {
		notifyAtTurn(turnEvent, currentTurn + diff + 1);
	}

	/**
	 * Notifies the class <i>turnEvent</i> at <i>turn</i> turns.
	 * 
	 * @param turnEvent the class to notify
	 * @param turn the number of the turn
	 */
	public void notifyAtTurn(TurnEvent turnEvent, int turn) {
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
			set.add(turnEvent);
		}
	}
}
