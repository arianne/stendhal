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

import static games.stendhal.common.constants.Actions.ALTERCREATURE;
import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TEXT;
import static games.stendhal.common.constants.Actions.TYPE;

import games.stendhal.client.ClientSingletonRepository;
import marauroa.common.game.RPAction;

/**
 * Alter an entity's attributes.
 */
class AlterCreatureAction implements SlashAction {

	/**
	 * Alters an entity's attributes.
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
		if ((params == null) || (params.length < getMinimumParameters())) {
			return false;
		}
		final RPAction alter = new RPAction();

		alter.put(TYPE, ALTERCREATURE);
		alter.put(TARGET, params[0]);
		alter.put(TEXT, params[1]);
		ClientSingletonRepository.getClientFramework().send(alter);

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMaximumParameters() {
		return 2;
	}

	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMinimumParameters() {
		return 2;
	}
}
