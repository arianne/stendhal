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
 * Other classes can register here to be notified when a player logs in.
 *
 * It is the responsibility of the LoginListener to determine which players are
 * of interest for it, and to store this information persistently.
 *
 * @author daniel
 */
public final class LoginNotifier {

	/** The singleton instance. */
	private static LoginNotifier instance;

	/**
	 * Holds a list of all registered LoginListeners.
	 */
	private final List<LoginListener> loginListeners;


	/**
	 * Returns the LoginNotifier instance.
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
	 * Hidden singleton constructor.
	 */
	private LoginNotifier() {
		loginListeners = new ArrayList<LoginListener>();
	}

	/**
	 * Adds a LoginListener.
	 *
	 * @param listener
	 *            LoginListener to add
	 */
	public void addListener(final LoginListener listener) {
		loginListeners.add(listener);
	}

	/**
	 * Removes a LoginListener.
	 *
	 * @param listener
	 *            LoginListener to remove
	 */
	public void removeListener(final LoginListener listener) {
		loginListeners.remove(listener);
	}

	/**
	 * This method is invoked by Player.create().
	 *
	 * @param player
	 *            the player who logged in
	 */
	public void onPlayerLoggedIn(final Player player) {
		for (final LoginListener listener : loginListeners) {
			listener.onLoggedIn(player);
		}
	}
}
