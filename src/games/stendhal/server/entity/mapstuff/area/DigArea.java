/***************************************************************************
 *                   (C) Copyright 2003-2021 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.area;

import games.stendhal.server.entity.player.Player;


public class DigArea extends ToolUseArea {

	/**
	 * Called when player attempts an action on the area
	 *
	 * @param user
	 *      Player that is digging
	 * @param tool_name
	 *      String name of tool being used
	 * @return
	 *      Player can dig
	 */
	@Override
	public boolean use(final Player user, final String tool_name) {
		// make sure player used a shovel
		if (tool_name.equals("shovel")) {
			return onUsed(user);
		}

		return false;
	}
}
