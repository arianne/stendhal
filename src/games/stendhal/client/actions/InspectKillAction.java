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

import static games.stendhal.common.constants.Actions.INSPECTKILL;

import java.util.LinkedList;
import java.util.List;

import games.stendhal.client.ClientSingletonRepository;
import marauroa.common.game.RPAction;

/**
 * Checks kill counts of a player for a specified creature.
 */
public class InspectKillAction implements SlashAction {

	@Override
	public boolean execute(String[] params, final String remainder) {
		final RPAction action = new RPAction();

		final String target = params[0];
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

		action.put("type", INSPECTKILL);
		action.put("target", target);
		if (creature != null) {
			action.put("creature", creature);
		}

		ClientSingletonRepository.getClientFramework().send(action);

		return true;
	}

	@Override
	public int getMaximumParameters() {
		return 1;
	}

	@Override
	public int getMinimumParameters() {
		return 1;
	}
}
