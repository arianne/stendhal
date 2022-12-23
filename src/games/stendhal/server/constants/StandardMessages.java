/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.constants;

import games.stendhal.server.entity.player.Player;


public abstract class StandardMessages {

	/**
	 * Sends a message to target notifying that the specified player was
	 * not found online.
	 *
	 * @param target
	 *     Player being notified.
	 * @param subject
	 *     Name of the subject player.
	 */
	public static void playerNotOnline(final Player target, final String subject) {
		target.sendPrivateText("No player named \"" + subject + "\" is currently logged in.");
	}
}
