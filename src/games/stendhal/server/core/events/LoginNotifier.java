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
public final class LoginNotifier {

	/** The Singleton instance. */
	private static final LoginNotifier instance = new LoginNotifier();

	/**
	 * Holds a list of all registered listeners.
	 */
	private final List<LoginListener> listeners;

	// singleton
	private LoginNotifier() {
		listeners = new ArrayList<LoginListener>();
	}

	/**
	 * Returns the LoginNotifier instance.
	 * 
	 * @return LoginNotifier the Singleton instance
	 */
	public static LoginNotifier get() {
		return instance;
	}

	/**
	 * Adds a LoginListener.
	 * 
	 * @param listener
	 *            LoginListener to add
	 */
	public void addListener(final LoginListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a LoginListener.
	 * 
	 * @param listener
	 *            LoginListener to remove
	 */
	public void removeListener(final LoginListener listener) {
		listeners.remove(listener);
	}

	/**
	 * This method is invoked by Player.create().
	 * 
	 * @param player
	 *            the player who logged in
	 */
	public void onPlayerLoggedIn(final Player player) {
		for (final LoginListener listener : listeners) {
			listener.onLoggedIn(player);
		}
	}
}
