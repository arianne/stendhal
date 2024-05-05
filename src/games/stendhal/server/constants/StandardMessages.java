/***************************************************************************
 *                 Copyright © 2003-2024 - Faiumoni e. V.                  *
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
	 * Script or command executed with too few parameters.
	 */
	public static void missingParameter(final Player target) {
		target.sendPrivateText(NotificationType.ERROR, "Missing parameter.");
	}

	/**
	 * Script or command executed with too few parameters.
	 */
	public static void missingParameter(final Player target, final String command) {
		target.sendPrivateText(NotificationType.ERROR, "Missing parameter for command \"" + command
				+ "\".");
	}

	/**
	 * Script or command executed with too many parameters.
	 */
	public static void excessParameter(final Player target) {
		target.sendPrivateText(NotificationType.ERROR, "Too many parameters.");
	}

	/**
	 * Script or command executed with too many parameters.
	 */
	public static void excessParameter(final Player target, final String command) {
		target.sendPrivateText(NotificationType.ERROR, "Too many parameters for command \"" + command
				+ "\".");
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


	/**
	 * Messages admin and player when admin makes a change to player's quest state.
	 *
	 * @param admin
	 *   Admin making change.
	 * @param player
	 *   Player being update.
	 * @param questName
	 *   Quest ID/name.
	 * @param oldState
	 *   Quest state before change.
	 * @param newState
	 *   Quest state after change.
	 */
	public static void changedQuestState(final Player admin, final Player player,
			final String questName, final String oldState, final String newState) {
		player.sendPrivateText(NotificationType.SUPPORT, "Admin " + admin.getTitle()
				+ " changed your state of the quest '" + questName + "' from '" + oldState + "' to '"
				+ newState + "'");
		admin.sendPrivateText("Changed the state of quest '" + questName + "' from '" + oldState
				+ "' to '" + newState + "'");
	}
}
