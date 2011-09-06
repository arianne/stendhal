/***************************************************************************
 *                    (C) Copyright 2011 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.player;

import marauroa.common.game.RPObject;

/**
 * handling of used actions on players.
 * 
 * @author hendrik
 */
public interface PlayerUseListener {

	/**
	 * handles use actions
	 *
	 * @param used the player on which "use" was called
	 * @param user the player doing the use
	 * @return true, if the event is successful, false otherwise
	 */
	public boolean onUsed(Player used, RPObject user);
}
