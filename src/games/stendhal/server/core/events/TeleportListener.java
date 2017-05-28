/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2011 - Faiumoni e. V.                   *
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
 * Implementing classes can be notified that a player teleported.
 *
 * @author hendrik
 */
public interface TeleportListener {

	/**
	 * This method is called when a player teleports
	 *
	 * @param player the player teleporting
	 * @param playerAction true, if the player actively teleported; false for all teleports
	 */
	void onTeleport(Player player, boolean playerAction);
}
