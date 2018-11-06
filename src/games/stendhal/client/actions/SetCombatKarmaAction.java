/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
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

import static games.stendhal.common.Constants.KARMA_SETTINGS;
import static games.stendhal.common.constants.Actions.TYPE;
import static games.stendhal.common.constants.General.COMBAT_KARMA;

import org.apache.log4j.Logger;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.common.NotificationType;
import marauroa.common.game.RPAction;

/**
 * Sets entity's combat karma attribute to determine when karma is used on combat.
 */
public class SetCombatKarmaAction implements SlashAction {

	/* logger instance */
	final static Logger logger = Logger.getLogger(SetCombatKarmaAction.class);

	@Override
	public boolean execute(final String[] params, final String remainder) {
		final boolean sent = sendAction(params[0]);

		if (sent) {
			// update GUI to reflect new setting when used as a slash action
			WtWindowManager.getInstance().setProperty("combat.karma", params[0]);
		}

		return sent;
	}

	/**
	 * Notifies the server to update "combat.karma" attribute.
	 *
	 * @param value
	 * 		New value of attribute.
	 */
	public boolean sendAction(final String value) {
		// server version compatibility
		if (!StendhalClient.serverVersionAtLeast("1.28.5")) {
			ClientSingletonRepository.getUserInterface().addEventLine(new EventLine("",
					"The server version does not support setting combat karma mode.", NotificationType.SERVER));
			return false;
		}

		if (!KARMA_SETTINGS.contains(value)) {
			final String errMsg = "Invalid value. Must be one of " + KARMA_SETTINGS.toString();
			ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(errMsg, NotificationType.ERROR));

			logger.warn("Cannot set battle karma to \"" + value + "\"");
			return false;
		}

		final RPAction action = new RPAction();
		action.put(TYPE, COMBAT_KARMA);
		action.put(COMBAT_KARMA, value);

		ClientSingletonRepository.getClientFramework().send(action);

		return true;
	}

	@Override
	public int getMaximumParameters() {
		return 1;
	}

	@Override
	public int getMinimumParameters() {
		return 1;
	}

}
