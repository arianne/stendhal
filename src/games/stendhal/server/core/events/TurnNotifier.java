/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;

/**
 * Other classes can register here to be notified at some time in the future.
 *
 * @author hendrik, daniel
 */
public final class TurnNotifier {

	private static Logger logger = Logger.getLogger(TurnNotifier.class);

	/** The singleton instance. */
	private static TurnNotifier instance;

	private int currentTurn = -1;

	/**
	 * This Map maps each turn to the set of all events that will take place at
	 * this turn. Turns at which no event should take place needn't be
	 * registered here.
	 */
	private final Map<Integer, Set<TurnListener>> register = new HashMap<Integer, Set<TurnListener>>();

	/** Used for multi-threading synchronization. * */
	private final Object sync = new Object();


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
	 * Hidden singleton constructor.
	 */
	private TurnNotifier() {
		// singleton
	}

	/**
	 * This method is invoked by StendhalRPRuleProcessor.endTurn().
	 *
	 * @param currentTurn
	 *            currentTurn
	 */

	public void logic(final int currentTurn) {
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
			final StringBuilder os = new StringBuilder();
			os.append("register: " + register.size() + "\n");
			int setSize;
			if (set != null) {
				setSize = set.size();
			} else {
				setSize = 0;
			}
			os.append("set: " + setSize + "\n");
			logger.info(os);
		}

		if (set != null) {
			for (final TurnListener turnListener : set) {

				try {
					turnListener.onTurnReached(currentTurn);
				} catch (final RuntimeException e) {
					logger.error("Exception in " + turnListener, e);
				}
			}
		}
	}

	/**
	 * Notifies the <i>turnListener</i> in <i>diff</i> turns.
	 *
	 * @param diff
	 *            the number of turns to wait before notifying
	 * @param turnListener
	 *            the object to notify
	 */

	public void notifyInTurns(final int diff, final TurnListener turnListener) {
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
	public void notifyInSeconds(final int sec, final TurnListener turnListener) {
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

	public void notifyAtTurn(final int turn, final TurnListener turnListener) {
		if (turnListener == null) {
			logger.error("Trying to notify null-object", new Throwable());
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.info("Notify at " + turn + " by " + turnListener);
			final StringBuilder st = new StringBuilder();

			for (final StackTraceElement e : Thread.currentThread().getStackTrace()) {
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
			final Integer turnInt = Integer.valueOf(turn);
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

	public void dontNotify(final TurnListener turnListener) {
		// all events that are equal to this one should be forgotten.
		// TurnEvent turnEvent = new TurnEvent(turnListener);
		for (final Map.Entry<Integer, Set<TurnListener>> mapEntry : register.entrySet()) {
			final Set<TurnListener> set = mapEntry.getValue();
			// We don't remove directly, but first store in this
			// set. This is to avoid ConcurrentModificationExceptions.
			final Set<TurnListener> toBeRemoved = new HashSet<TurnListener>();
			if (set.contains(turnListener)) {
					toBeRemoved.add(turnListener);
			}
			for (final TurnListener event : toBeRemoved) {
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

	public int getRemainingTurns(final TurnListener turnListener) {
		// all events match that are equal to this.
		// TurnEvent turnEvent = new TurnEvent(turnListener);
		// the HashMap is unsorted, so we need to run through
		// all of it.
		final List<Integer> matchingTurns = new ArrayList<Integer>();
		for (final Map.Entry<Integer, Set<TurnListener>> mapEntry : register.entrySet()) {
			final Set<TurnListener> set = mapEntry.getValue();
			for (final TurnListener currentEvent : set) {
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

	public int getRemainingSeconds(final TurnListener turnListener) {

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
