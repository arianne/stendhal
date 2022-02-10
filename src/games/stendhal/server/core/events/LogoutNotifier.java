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
import java.util.List;

import games.stendhal.server.entity.player.Player;

/**
 * Other classes can register here to be notified when a player logs out.
 *
 * It is the responsibility of the LogoutListener to determine which players are
 * of interest for it, and to store this information persistently.
 *
 * @author markus
 */
public final class LogoutNotifier {

	/** The singleton instance. */
	private static LogoutNotifier instance;

	/**
	 * Holds a list of all registered LoginListeners.
	 */
	private final List<LogoutListener> logoutListeners;


	/**
	 * Returns the LogoutNotifier instance.
	 *
	 * @return LogoutNotifier the Singleton instance
	 */
	public static LogoutNotifier get() {
		if (instance == null) {
			instance = new LogoutNotifier();
		}

		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private LogoutNotifier() {
		logoutListeners = new ArrayList<>();
	}

	/**
	 * Adds a LogoutListener.
	 *
	 * @param listener
	 *            LogoutListener to add
	 */
	public void addListener(final LogoutListener listener) {
		logoutListeners.add(listener);
	}

	/**
	 * Removes a LogoutListener.
	 *
	 * @param listener
	 *            LogoutListener to remove
	 */
	public void removeListener(final LogoutListener listener) {
		logoutListeners.remove(listener);
	}

	/**
	 * This method is invoked by TODO
	 *
	 * @param player
	 *            the player who logged out
	 */
	public void onPlayerLoggedOut(final Player player) {
		for (final LogoutListener listener : logoutListeners) {
			listener.onLoggedOut(player);
		}
	}
}
