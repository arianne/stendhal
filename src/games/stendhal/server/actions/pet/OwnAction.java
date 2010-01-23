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
package games.stendhal.server.actions.pet;

import static games.stendhal.common.constants.Actions.OWN;
import static games.stendhal.common.constants.Actions.TARGET;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
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

public class OwnAction implements ActionListener {


	public static void register() {
		CommandCenter.register(OWN, new OwnAction());
	}

	public void onAction(final Player player, final RPAction action) {
		if (!action.has(TARGET)) {
			return;
		}

		// evaluate the target parameter
		final Entity entity = EntityHelper.entityFromTargetName(action.get(TARGET), player);

		if (entity != null) {
			// Make sure the entity is valid (hacked client?)
			if (!(entity instanceof DomesticAnimal)) {
				player.sendPrivateText("Maybe you should stick to owning domestic animals.");
				return;
			}

			final DomesticAnimal animal = (DomesticAnimal) entity;
			final Player owner = animal.getOwner();

			if (owner != null) {
				player.sendPrivateText("This animal is already owned by "
						+ owner.getTitle());
			} else {
				final List<Node> path = Path.searchPath(player, player.getX(),
						player.getY(), animal.getArea(), 7);

				if (path.isEmpty() && !animal.nextTo(player)) {
					// The animal is too far away
					player.sendPrivateText("That " + animal.getTitle()
							+ " is too far away.");
				} else {
					if (animal instanceof Sheep) {
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
					new GameEvent(player.getName(), "own", animal.getRPClass().getName(), animal.getName()).raise();
				}
			}
		} 

		player.notifyWorldAboutChanges();
	}
}
