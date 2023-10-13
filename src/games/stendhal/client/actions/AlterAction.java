/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
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

import static games.stendhal.common.constants.Actions.ALTER;
import static games.stendhal.common.constants.Actions.MODE;
import static games.stendhal.common.constants.Actions.STAT;
import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TYPE;
import static games.stendhal.common.constants.Actions.VALUE;

import games.stendhal.client.ClientSingletonRepository;
import marauroa.common.game.RPAction;

/**
 * Alter an entity's attributes.
 */
class AlterAction implements SlashAction {

	/**
	 * Executes a chat command.
	 *
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 *
	 * @return <code>true</code> if was handled.
	 */
	@Override
	public boolean execute(final String[] params, final String remainder) {
		if (hasInvalidArguments(params, remainder)) {
			return false;
		}
		final RPAction alter = new RPAction();

		alter.put(TYPE, ALTER);
		alter.put(TARGET, params[0]);
		alter.put(STAT, params[1]);
		alter.put(MODE, params[2]);
		alter.put(VALUE, remainder);
		ClientSingletonRepository.getClientFramework().send(alter);

		return true;
	}

	/**
	 * Checks whether the arguments passed are valid for execution.
	 *
	 * @param params to be evaluated
	 * @param remainder to be evaluated
	 * @return true if <code>params</code>.length too short or remainder is <code>null</code>
	 */
	private boolean hasInvalidArguments(final String[] params, final String remainder) {
		return (params == null) || (remainder == null) || (params.length < getMinimumParameters());
	}

	/**
	 * Gets the maximum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMaximumParameters() {
		return 3;
	}

	/**
	 * Gets the minimum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMinimumParameters() {
		return 3;
	}
}
