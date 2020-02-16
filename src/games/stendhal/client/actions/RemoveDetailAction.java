/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
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

import static games.stendhal.common.constants.Actions.REMOVEDETAIL;

import games.stendhal.client.ClientSingletonRepository;
import marauroa.common.game.RPAction;

/**
 * Action that players can execute to manually remove the detail outfit layer.
 */
public class RemoveDetailAction implements SlashAction {

	@Override
	public boolean execute(final String[] params, final String remainder) {
		final RPAction action = new RPAction();
		action.put("type", REMOVEDETAIL);
		ClientSingletonRepository.getClientFramework().send(action);
		return true;
	}

	@Override
	public int getMaximumParameters() {
		return 0;
	}

	@Override
	public int getMinimumParameters() {
		return 0;
	}
}
