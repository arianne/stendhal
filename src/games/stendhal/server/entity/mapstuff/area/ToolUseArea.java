/***************************************************************************
 *                     (C) Copyright 2021 - Stendhal                       *
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

import org.apache.log4j.Logger;

import games.stendhal.server.entity.player.Player;


public class ToolUseArea extends AreaEntity {

	/** logger instance. */
	private static final Logger logger = Logger.getLogger(ToolUseArea.class);


    /**
     * Called when player attempts an action on the area
     *
     * @param user
     *      Player acting on area
     * @param tool_name
     *      String name of tool being used
     * @return
     *      True if area was used correctly
     */
    public boolean use(final Player user, final String tool_name) {
		/* overriding classes should check for proper tool being used */

        return onUsed(user);
    }

	/**
	 * Action(s) to execute when conditions for using area are met
	 *
	 * @param user
	 *      Player acting on area
	 */
	protected boolean onUsed(final Player user) {
		// override with implementing classes
		logger.warn("ToolUseArea.onUsed called without overriding");

		return false;
	}
}
