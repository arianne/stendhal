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
import marauroa.common.game.RPAction;

/**
 * Add a player to the ignore list.
 */
class IgnoreAction implements SlashAction {

	/**
	 * Execute an ignore command.
	 *
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 *
	 * @return <code>true</code> if command was handled.
	 */
	@Override
	public boolean execute(final String[] params, final String remainder) {
		final RPAction action = new RPAction();

		action.put("type", "ignore");
		// because the max number of parameters is non zero, a String[2] is created when the command is parsed
		// but if player only typed /ignore then even the first entry is null
		if (params[0] == null) {
			action.put("list", "1");
		} else {
			action.put("target", params[0]);
			String duration = params[1];
			if (duration != null) {
				/*
				 * Ignore "forever" values
				 */
				if (!duration.equals("*") || !duration.equals("-")) {
				/*
				 * Validate it's a number
				 */
					try {
						Integer.parseInt(duration);
					} catch (final NumberFormatException ex) {
						return false;
					}

					action.put("duration", duration);
				}
			}

			if (remainder.length() != 0) {
				action.put("reason", remainder);
			}
		}
		ClientSingletonRepository.getClientFramework().send(action);
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
		return 0;
	}
}
