/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions;

import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.Path;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.DomesticAnimal;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;

import java.util.List;

import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

public class OwnAction implements ActionListener {

	private static final Logger logger = Logger.getLogger(OwnAction.class);

	private static final String _SPECIES = "species";
	private static final String _OWN = "own";

	public static void register() {
		CommandCenter.register(_OWN, new OwnAction());
	}

	public void onAction(Player player, RPAction action) {
		if (!action.has(TARGET)) {
			return;
		}

		// evaluate the target parameter
		Entity entity = EntityHelper.entityFromTargetName(action.get(TARGET), player);

		if (entity != null) {
			// Make sure the entity is valid (hacked client?)
			if (!(entity instanceof DomesticAnimal)) {
				player.sendPrivateText("Maybe you should stick to owning domestic animals.");
				return;
			}

			DomesticAnimal animal = (DomesticAnimal) entity;
			Player owner = animal.getOwner();

			if (owner != null) {
				player.sendPrivateText("This animal is already owned by "
						+ owner.getTitle());
			} else {
				List<Node> path = Path.searchPath(player, player.getX(),
						player.getY(), animal.getArea(), 7);

				if (path.isEmpty()) {
					// The animal is too far away
					player.sendPrivateText("That " + animal.getTitle()
							+ " is too far away.");
				} else if (animal instanceof Sheep) {
					if (player.getSheep() != null) {
						player.sendPrivateText("You already own a sheep.");
					} else {
						player.setSheep((Sheep) animal);
					}
				} else if (animal instanceof Pet) {
					if (player.getPet() != null) {
						player.sendPrivateText("You already own a pet.");
					} else {
						player.setPet((Pet) animal);
					}
				}
			}
		} else {
			String targetString = action.get(TARGET);

			// TODO: BUG: This features is potentially abusable right now.
			// Consider removing it...
			if (targetString != null && targetString.equals("-1")) {
				// Disown
				if (action.has(_SPECIES)) {
					String species = action.get(_SPECIES);

					if (species.equals("sheep")) {
						Sheep sheep = player.getSheep();

						if (sheep != null) {
							player.removeSheep(sheep);

    						// HACK: Avoid a problem on database
    						if (sheep.has("#db_id")) {
    							sheep.remove("#db_id");
    						}
    					} else {
    						logger.error("sheep not found in disown action: " + action.toString());
    					}
					} else if (species.equals("pet")) {
						Pet pet = player.getPet();

						if (pet != null) {
							player.removePet(pet);

    						// HACK: Avoid a problem on database
    						if (pet.has("#db_id")) {
    							pet.remove("#db_id");
    						}
						} else {
							logger.error("pet not found in disown action: " + action.toString());
						}
					}
				}
			}
		}

		player.notifyWorldAboutChanges();
	}
}
