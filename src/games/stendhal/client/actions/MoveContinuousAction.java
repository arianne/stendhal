/***************************************************************************
 *                   (C) Copyright 2003-2017 - Arianne                     *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.actions;

import static games.stendhal.common.constants.Actions.MOVE_CONTINUOUS;
import static games.stendhal.common.constants.Actions.TYPE;

import javax.swing.JCheckBox;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.client.gui.settings.SettingsDialog;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.common.NotificationType;
import marauroa.common.game.RPAction;

/**
 * Allows a player to continue movement after teleport via portal
 * or after map change without the need to release and press
 * direction again.
 *
 * @author
 * 		AntumDeluge
 */
public class MoveContinuousAction implements SlashAction {

	/**
	 * Execute a command to toggle continuous movement.
	 *
	 * @param params
	 * 		The formal parameters.
	 * @param remainder
	 * 		Line content after parameters.
	 * @return
	 * 		<code>true</code> if command was handled.
	 */
	@Override
	public boolean execute(String[] params, String remainder) {
		WtWindowManager wm = WtWindowManager.getInstance();
		boolean enabled = Boolean.parseBoolean(wm.getProperty(MOVE_CONTINUOUS, "false"));
		wm.setProperty(MOVE_CONTINUOUS, Boolean.toString(!enabled));

		// Was not called from settings GUI so we need to update check box state.
		JCheckBox moveContinuousToggle = SettingsDialog.getMoveContinuousToggle();
		if (moveContinuousToggle != null) {
			moveContinuousToggle.setSelected(!enabled);
			return true;
		} else {
			return sendAction(!enabled);
		}
	}

	/**
	 * Sends the action to the server.
	 *
	 * @param enable
	 *		If <code>true</code>, continuous movement will be enabled.
	 * @param notify
	 *		If <code>true</code>, notifies player of state change.
	 * @return
	 *		<code>true</code>
	 */
	public boolean sendAction(final boolean enable, final boolean notify) {
		// Create action to be sent to server.
		final RPAction action = new RPAction();
		action.put(TYPE, MOVE_CONTINUOUS);

		if (enable) {
			action.put(MOVE_CONTINUOUS, "");
		}

		ClientSingletonRepository.getClientFramework().send(action);

		if (notify) {
			String msg;
			if (action.has(MOVE_CONTINUOUS)) {
				msg = "Continuous movement enabled.";
			} else {
				msg = "Continuous movement disabled.";
			}

			// FIXME: Notification should be sent from server MoveContinuousAction event.
			ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(msg, NotificationType.CLIENT));
		}

		return true;
	}

	/**
	 * Sends the action to the server.
	 *
	 * @param enable
	 *		If <code>true</code>, continuous movement will be enabled.
	 * @return
	 *		<code>true</code>
	 */
	public boolean sendAction(final boolean enable) {
		return sendAction(enable, true);
	}

	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return
	 * 		Parameter count.
	 */
	@Override
	public int getMaximumParameters() {
		return 0;
	}

	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return
	 * 		Parameter count.
	 */
	@Override
	public int getMinimumParameters() {
		return 0;
	}
}
