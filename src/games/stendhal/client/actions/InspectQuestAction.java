/***************************************************************************
 *                     Copyright © 2020-2023 - Arianne                     *
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

import static games.stendhal.common.constants.Actions.INSPECTQUEST;

import games.stendhal.client.ClientSingletonRepository;
import marauroa.common.game.RPAction;

public class InspectQuestAction implements SlashAction {

	@Override
	public boolean execute(final String[] params, final String remainder) {
		final RPAction action = new RPAction();

		action.put("type", INSPECTQUEST);
		action.put("target", params[0]);
		// FIXME: Why does param[1] result as null when single parameter given?
		//        Clue may be in `games.stendhal.client.scripting.SlashActionParser.parse`.
		if (params.length > 1 && params[1] != null) {
			action.put("quest_slot", params[1]);
		}

		ClientSingletonRepository.getClientFramework().send(action);

		return true;
	}

	@Override
	public int getMaximumParameters() {
		return 2;
	}

	@Override
	public int getMinimumParameters() {
		return 1;
	}
}
