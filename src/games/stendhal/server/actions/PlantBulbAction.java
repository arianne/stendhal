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
import games.stendhal.server.entity.item.Bulb;
import games.stendhal.server.entity.mapstuff.spawner.FlowerBulbGrower;

/**
 * Called when a player uses a server.entity.item.Bulb
 * When a bulb is planted, a FlowerBulbGrower is created 
 * FlowerBulbGrowers will only flourish on fertile ground, but the Bulb and the PlantBulbAction don't know this
 */
public class PlantBulbAction {

	private RPEntity user;
	private Bulb bulb;

	public void setUser(final RPEntity user) {
		this.user = user;
	}

	public void setBulb(final Bulb bulb) {
		this.bulb = bulb;
	}

	public boolean execute() {
		if ((bulb == null) || (user == null)) {
			return false;
		} else if (!bulb.isContained()) {
			// the bulb is on the ground, but not next to the player
			if (!bulb.nextTo(user)) {
				user.sendPrivateText("The bulb is too far away");
				return false;
			}
			// the infostring of the bulb stores what it should grow
			final String infostring = bulb.getInfoString();
			FlowerBulbGrower flowerGrower;
			// choose the default flower grower if there is none set
			if (infostring == null) {
				flowerGrower = new FlowerBulbGrower();
			} else {
				flowerGrower = new FlowerBulbGrower(bulb.getInfoString());
			}
			user.getZone().add(flowerGrower);
			// add the FlowerBulbGrower where the bulb was on the ground
			flowerGrower.setPosition(bulb.getX(), bulb.getY());
			// The first stage of growth happens almost immediately        
			TurnNotifier.get().notifyInTurns(3, flowerGrower);
			// remove the bulb now that it is planted
			bulb.removeOne();
			return true;
		}
		// the bulb was 'contained' in a slot and so it cannot be planted
		user.sendPrivateText("Alas, This bulb will will not thrive in your pockets! Try putting it on fertile ground to plant it.");
		return false;
	}
}
