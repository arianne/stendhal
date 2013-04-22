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
import games.stendhal.common.constants.Actions;
import marauroa.common.game.RPAction;

/**
 * Request the travel (quest and producer) status.
 */
class TravelLogAction implements SlashAction {
	@Override
	public boolean execute(String[] params, String remainder) {
		final RPAction action = new RPAction();
		action.put("type", Actions.PROGRESS_STATUS);

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
