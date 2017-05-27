/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.pet;

import static games.stendhal.common.constants.Actions.FORSAKE;
import static games.stendhal.common.constants.Actions.PET;
import static games.stendhal.common.constants.Actions.SHEEP;
import static games.stendhal.common.constants.Actions.SPECIES;

import org.apache.log4j.Logger;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * release a pet into the wilderness
 */
public class ForsakeAction implements ActionListener {

	private static final String DB_ID = "#db_id";
	private static final Logger logger = Logger.getLogger(ForsakeAction.class);

	/**
	 * registers an action
	 */
	public static void register() {
		CommandCenter.register(FORSAKE, new ForsakeAction());
	}

	@Override
	public void onAction(final Player player, final RPAction action) {

		if (action.has(SPECIES)) {
			final String species = action.get(SPECIES);

			if (species.equals(SHEEP)) {
				final Sheep sheep = player.getSheep();

				if (sheep != null) {
					player.removeSheep(sheep);

					// HACK: Avoid a problem on database
					if (sheep.has(DB_ID)) {
						sheep.remove(DB_ID);
					}
					new GameEvent(player.getName(), "leave", Integer.toString(sheep.getWeight())).raise();
				} else {
					logger.error("sheep not found in disown action: " + action.toString());
				}
			} else if (species.equals(PET)) {
				final Pet pet = player.getPet();

				if (pet != null) {
					player.removePet(pet);

					// HACK: Avoid a problem on database
					if (pet.has(DB_ID)) {
						pet.remove(DB_ID);
					}
					new GameEvent(player.getName(), "leave", species, Integer.toString(pet.getWeight())).raise();
				} else {
					logger.error("pet not found in disown action: " + action.toString());
				}
			}
		}
	}
}
