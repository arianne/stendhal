/* $Id$ */
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
package games.stendhal.server.actions;

import games.stendhal.server.core.events.TurnNotifier;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Seed;
import games.stendhal.server.entity.mapstuff.spawner.FlowerGrower;

/**
 * Called when a player uses a server.entity.item.Seed
 * When a seed is planted, a FlowerGrower is created 
 * FlowerGrowers will only flourish on fertile ground, but the Seed and the PlantAction don't know this
 */
public class PlantAction {

	private RPEntity user;
	private Seed seed;

	public void setUser(final RPEntity user) {
		this.user = user;
	}

	public void setSeed(final Seed seed) {
		this.seed = seed;
	}

	public boolean execute() {
		if ((seed == null) || (user == null)) {
			return false;
		} else if (!seed.isContained()) {
			// the seed is on the ground, but not next to the player
			if (!seed.nextTo(user)) {
				user.sendPrivateText("The seed is too far away");
				return false;
			}
			
			// the infostring of the seed stores what it should grow
			final String infostring = seed.getInfoString();
			FlowerGrower flowerGrower;
			// choose the default flower grower if there is none set
			if (infostring == null) {
				flowerGrower = new FlowerGrower();
			} else {
				flowerGrower = new FlowerGrower(seed.getInfoString());
			}
			user.getZone().add(flowerGrower);
			// add the FlowerGrower where the seed was on the ground
			flowerGrower.setPosition(seed.getX(), seed.getY());
			// The first stage of growth happens almost immediately        
			TurnNotifier.get().notifyInTurns(3, flowerGrower);
			// remove the seed now that it is planted
			seed.removeOne();
			return true;
		}
		// the seed was 'contained' in a slot and so it cannot be planted
		user.sendPrivateText("You have to put the seed on the ground to plant it, silly!");
		return false;

	}

}
