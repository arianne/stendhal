/***************************************************************************
 *                   (C) Copyright 2003-2015 - Arianne                     *
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

import static games.stendhal.common.constants.Actions.MODE;
import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TYPE;
import static games.stendhal.common.constants.Actions.WALK;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.common.StringHelper;
import marauroa.common.game.RPAction;

/**
 * Stops player's movement.
 *
 * @author
 *         AntumDeluge
 */
public class AutoWalkStopAction implements SlashAction {
	/**
	 * Execute a chat command.
	 *
	 * @param params
	 *        The formal parameters.
	 * @param remainder
	 *        Line content after parameters.
	 * @return
	 *         <code>true</code> if command was handled.
	 */
	@Override
	public boolean execute(String[] params, String remainder) {
		final RPAction stop = new RPAction();

		stop.put(TYPE, WALK);
		stop.put(TARGET, StringHelper.unquote(remainder));
		stop.put(MODE, "stop");

		ClientSingletonRepository.getClientFramework().send(stop);

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return
	 *         Parameter count.
	 */
	@Override
	public int getMaximumParameters() {
		return 0;
	}

	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return
	 *         Parameter count.
	 */
	@Override
	public int getMinimumParameters() {
		return 0;
	}
}
