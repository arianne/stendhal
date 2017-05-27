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

/**
 * Implementing classes can be notified that a player has logged in.
 *
 * After registering at the LoginNotifier, the LoginNotifier will notify it
 * about each player who logs in.
 *
 * It is the responsibility of the LoginListener to determine which players are
 * of interest for it, and to store this information persistently.
 *
 * @author daniel
 */
public interface LoginListener {

	/**
	 * Is called after a player logged into the game.
	 *
	 * @param player
	 *            the player who has logged in
	 */
	void onLoggedIn(Player player);
}
