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

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.DomesticAnimal;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.pathfinder.Node;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.util.EntityHelper;

import java.util.List;

import marauroa.common.game.RPAction;

public class OwnAction implements ActionListener {

	public static void register() {
		CommandCenter.register("own", new OwnAction());
	}

	public void onAction(Player player, RPAction action) {
		if (!action.has("target")) {
			return;
		}

		 // evaluate the target parameter
		StendhalRPZone zone = player.getZone();
		RPEntity entity = EntityHelper.entityFromTargetName(action.get("target"), zone);

		if (entity != null) {
			// Make sure the entity is valid (hacked client?)
			// TODO: Allow "Own" on client for all RPEntity's just for some
			// humor?
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
			int target = action.getInt("target");

    		// TODO: BUG: This features is potentially abusable right now. Consider
    		// removing it...
    		if (target == -1) {
    			// Disown
    			if (action.has("species")) {
    				String species = action.get("species");
    
    				if (species.equals("sheep")) {
    					Sheep sheep = player.getSheep();
    					player.removeSheep(sheep);
    
    					// HACK: Avoid a problem on database
    					if (sheep.has("#db_id")) {
    						sheep.remove("#db_id");
    					}
    				} else if (species.equals("pet")) {
    					Pet pet = player.getPet();
    					player.removePet(pet);
    
    					// HACK: Avoid a problem on database
    					if (pet.has("#db_id")) {
    						pet.remove("#db_id");
    					}
    				}
    			}
    		}
		}

		player.notifyWorldAboutChanges();
	}
}
