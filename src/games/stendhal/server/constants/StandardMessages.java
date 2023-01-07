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

import games.stendhal.common.NotificationType;
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

	/**
	 * Script or command executed with wrong number of parameters.
	 */
	public static void missingParameter(final Player target) {
		target.sendPrivateText(NotificationType.ERROR, "Missing parameter.");
	}

	/**
	 * Script or command executed with unacceptable parameter
	 */
	public static void unknownCommand(final Player target, final String cmd) {
		target.sendPrivateText(NotificationType.ERROR, "Unknown command: " + cmd);
	}

	/**
	 * Script or command executed with unacceptable parameter.
	 */
	public static void unknownParameter(final Player target, final String param) {
		target.sendPrivateText(NotificationType.ERROR, "Unknown parameter: " + param);
	}

	public static void paramMustBeNumber(final Player target, final String param) {
		target.sendPrivateText(NotificationType.ERROR, param + " must be a number.");
	}

	public static void paramMustBeNumber(final Player target) {
		paramMustBeNumber(target, "Parameter");
	}
}
