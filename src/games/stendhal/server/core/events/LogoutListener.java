/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
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
 * Implementing classes can be notified that a player has logged out.
 *
 * After registering at the LoginNotifier, the LoginNotifier will notify it
 * about each player who logs out.
 *
 * It is the responsibility of the LogoutListener to determine which players are
 * of interest for it, and to store this information persistently.
 *
 * @author markus
 */
public interface LogoutListener {

	/**
	 * Called when a player has logged out.
	 *
	 * @param player The player having logged out.
	 */
	void onLoggedOut(Player player);
}
