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

import static games.stendhal.common.constants.Actions.ALTERKILL;

import java.util.LinkedList;
import java.util.List;

import games.stendhal.client.ClientSingletonRepository;
import marauroa.common.game.RPAction;

/**
 * Changes solo or shared kill count of specified creature for player.
 */
public class AlterKillAction implements SlashAction {

	@Override
	public boolean execute(final String[] params, String remainder) {
		final RPAction action = new RPAction();

		final String target = params[0];
		final String killtype = params[1];
		final String count = params[2];
		String creature = null;

		if (remainder != null) {
			final List<String> words = new LinkedList<>();
			// trim whitespace
			for (final String w: remainder.split(" ")) {
				if (!w.equals("")) {
					words.add(w);
				}
			}

			if (!words.isEmpty()) {
				creature = String.join(" ", words);
			}
		}

		action.put("type", ALTERKILL);
		action.put("target", target);
		action.put("killtype", killtype);
		action.put("count", count);
		if (creature != null) {
			action.put("creature", creature);
		}

		ClientSingletonRepository.getClientFramework().send(action);

		return true;
	}

	@Override
	public int getMaximumParameters() {
		return 3;
	}

	@Override
	public int getMinimumParameters() {
		return 3;
	}
}
