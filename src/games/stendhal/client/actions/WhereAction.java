/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
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

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.StendhalClient;
import games.stendhal.common.StringHelper;
import marauroa.common.game.RPAction;

/**
 * Query for player position.
 */
class WhereAction implements SlashAction {

	/**
	 * Execute a chat command.
	 *
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 *
	 * @return <code>true</code> if command was handled.
	 */
	@Override
	public boolean execute(final String[] params, String remainder) {
		final RPAction where = new RPAction();

		// use executing character if name parameter not supplied
		if (remainder.length() == 0) {
			remainder = StendhalClient.get().getCharacter();
		}

		where.put("type", "where");
		where.put("target", StringHelper.unquote(remainder));

		ClientSingletonRepository.getClientFramework().send(where);

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMaximumParameters() {
		return 0;
	}

	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMinimumParameters() {
		return 0;
	}
}
