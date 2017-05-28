/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions;

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * processes actions sent by the client.
 */
public interface ActionListener {

	/**
	 * processes the requested action.
	 *
	 * @param player the caller of the action
	 * @param action the action to be performed
	 */
	void onAction(final Player player, final RPAction action);

}
