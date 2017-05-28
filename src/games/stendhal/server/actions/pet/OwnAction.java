/***************************************************************************
 *                   (C) Copyright 2003-2016 - Marauroa                    *
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

import java.util.List;

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
import marauroa.common.game.RPAction;

/**
 * Owns an domestic animal like a sheep.
 */
public class OwnAction implements ActionListener {

	/**
	 * registers the action in the command center.
	 */
	public static void register() {
		CommandCenter.register(OWN, new OwnAction());
	}

	/**
	 * processes the requested action.
	 *
	 * @param player the caller of the action
	 * @param action the action to be performed
	 */
	@Override
	public void onAction(final Player player, final RPAction action) {
		if (!action.has(TARGET)) {
			return;
		}

		final Entity entity = EntityHelper.entityFromTargetName(action.get(TARGET), player);
		if (entity != null) {
			if (!checkEntityIsDomesticAnimal(player, entity)) {
				return;
			}

			DomesticAnimal animal = (DomesticAnimal) entity;
			if (!checkNotOwned(player, animal)) {
				return;
			}

			if (!checkEntityIsReachable(player, animal)) {
				return;
			}

			// all checks have been okay, so lets own it
			own(player, animal);
		}

		player.notifyWorldAboutChanges();
	}

	/**
	 * checks whether the entity is a domestic animal.
	 *
	 * @param player player to complain to
	 * @param entity entity to check
	 * @return true if it is a domestic animal
	 */
	private boolean checkEntityIsDomesticAnimal(final Player player, final Entity entity) {
		// Make sure the entity is valid (hacked client?)
		if (!(entity instanceof DomesticAnimal)) {
			player.sendPrivateText("Maybe you should stick to owning domestic animals.");
			return false;
		}
		return true;
	}

	/**
	 * checks whether this animal is already owned.
	 *
	 * @param player player to complain to
	 * @param animal animal to check
	 * @return true if the animal is unowned.
	 */
	private boolean checkNotOwned(final Player player, DomesticAnimal animal) {
		final Player owner = animal.getOwner();
		if (owner != null) {
			player.sendPrivateText("This animal is already owned by " + owner.getTitle());
			return false;
		}
		return true;
	}

	/**
	 * checks whether this entity is reachable (whether a path exists)
	 *
	 * @param player player to complain to
	 * @param entity entity to check
	 * @return true if the entity is reachable.
	 */
	private boolean checkEntityIsReachable(final Player player, final Entity entity) {
		final List<Node> path = Path.searchPath(player, player.getX(),
				player.getY(), entity.getArea(), 7);

		if (path.isEmpty() && !entity.nextTo(player)) {
			// The animal is too far away
			player.sendPrivateText("That " + entity.getTitle() + " is too far away.");
			return false;
		}
		return true;
	}

	/**
	 * owns an domestic animal
	 *
	 * @param player new owner
	 * @param animal animal to be owned
	 */
	private void own(final Player player, final DomesticAnimal animal) {
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
		new GameEvent(player.getName(), "own", animal.getRPClass().getName(), animal.getTitle()).raise();
	}
}
