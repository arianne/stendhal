/***************************************************************************
 *                   (C) Copyright 2011 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.query;

import static games.stendhal.common.constants.Actions.LANGUAGE;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Processes a language action.
 */
public class LanguageAction implements ActionListener {

	/**
	 * registers the action
	 */
	public static void register() {
		CommandCenter.register(LANGUAGE, new LanguageAction());
	}

	/**
	 * processes the requested action.
	 *
	 * @param player the caller of the action
	 * @param action the action to be performed
	 */
	@Override
	public void onAction(final Player player, final RPAction action) {
		player.setLanguage(action.get("target"));
	}
}
