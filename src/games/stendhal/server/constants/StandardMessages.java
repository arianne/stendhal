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
	 * Processes a message.
	 *
	 * @param target
	 *   Player receiving message or {@code null}.
	 * @param _type
	 *   Message type or {@code null} for default type.
	 * @param msg
	 *   Message text.
	 * @return
	 *   Message text.
	 */
	private static String process(final Player target, final NotificationType _type,
			final String msg) {
		if (target != null) {
			if (_type != null) {
				target.sendPrivateText(_type, msg);
			} else {
				target.sendPrivateText(msg);
			}
		}
		return msg;
	}

	/**
	 * Processes a message.
	 *
	 * @param target
	 *   Player receiving message or {@code null}.
	 * @param msg
	 *   Message text.
	 * @return
	 *   Message text.
	 */
	private static String process(final Player target, final String msg) {
		return process(target, null, msg);
	}

	/**
	 * Processes an error type message.
	 *
	 * @param target
	 *   Player receiving message or {@code null}.
	 * @param msg
	 *   Message text.
	 * @return
	 *   Message text
	 */
	private static String processError(final Player target, final String msg) {
		return process(target, NotificationType.ERROR, msg);
	}

	/**
	 * Specified player was not found online.
	 *
	 * @param target
	 *   Player receiving message or {@code null}.
	 * @param subject
	 *   Name of the subject player.
	 * @return
	 *   Message text.
	 */
	public static String playerNotOnline(final Player target, final String subject) {
		return process(target, "No player named \"" + subject + "\" is currently logged in.");
	}

	/**
	 * Script or command executed with too few parameters.
	 *
	 * @param target
	 *   Player receiving message or {@code null}.
	 * @return
	 *   Message text.
	 */
	public static String missingParameter(final Player target) {
		return processError(target, "Missing parameter.");
	}

	/**
	 * Script or command executed with too few parameters.
	 *
	 * @param target
	 *   Player receiving message or {@code null}.
	 * @param cmd
	 *   Name of command that was to be executed.
	 * @return
	 *   Message text.
	 */
	public static String missingParameter(final Player target, final String cmd) {
		return processError(target, "Missing parameter for command \"" + cmd + "\".");
	}

	/**
	 * Command parameter requires an additional value parameter.
	 *
	 * @param target
	 *   Player receiving message or {@code null}.
	 * @param param
	 *   Name of parameter.
	 * @return
	 *   Message text.
	 */
	public static String missingParamValue(final Player target, final String param) {
		return processError(target, "Missing value for parameter \"" + param + "\".");
	}

	/**
	 * Script or command executed with too many parameters.
	 *
	 * @param target
	 *   Player receiving message or {@code null}.
	 * @return
	 *   Message text.
	 */
	public static String excessParameter(final Player target) {
		return processError(target, "Too many parameters.");
	}

	/**
	 * Script or command executed with too many parameters.
	 *
	 * @param target
	 *   Player receiving message or {@code null}.
	 * @param cmd
	 *   Name of command that was to be executed.
	 * @return
	 *   Message text.
	 */
	public static String excessParameter(final Player target, final String cmd) {
		return processError(target, "Too many parameters for command \"" + cmd + "\".");
	}

	/**
	 * Script or command executed with unacceptable parameter.
	 *
	 * @param target
	 *   Player receiving message or {@code null}.
	 * @param cmd
	 *   Name of command that was to be executed.
	 * @return
	 *   Message text.
	 */
	public static String unknownCommand(final Player target, final String cmd) {
		return processError(target, "Unknown command: " + cmd);
	}

	/**
	 * Script or command executed with unacceptable parameter.
	 *
	 * @param target
	 *   Player receiving message or {@code null}.
	 * @param param
	 *   Name of parameter.
	 * @return
	 *   Message text.
	 */
	public static String unknownParameter(final Player target, final String param) {
		return processError(target, "Unknown parameter: " + param);
	}

	/**
	 * Script or command executed with unacceptable parameter type.
	 *
	 * @param target
	 *   Player receiving message or {@code null}.
	 * @param param
	 *   Name of parameter.
	 * @return
	 *   Message text.
	 */
	public static String paramMustBeNumber(final Player target, final String param) {
		return processError(target, param + " must be a number.");
	}

	/**
	 * Script or command executed with unacceptable parameter type.
	 *
	 * @param target
	 *   Player receiving message or {@code null}.
	 * @return
	 *   Message text.
	 */
	public static String paramMustBeNumber(final Player target) {
		return paramMustBeNumber(target, "Parameter");
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
