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
package games.stendhal.server.entity.item;

import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.mapstuff.spawner.FlowerBulbGrower;

import java.util.Map;

/**
 * A bulb can be planted. 
 * The plant action defines the behaviour (e.g. only plantable on fertile ground).
 * The infostring stores what it will grow.
 */
public class Bulb extends StackableItem implements UseListener {

	public Bulb(final Bulb item) {
		super(item);
	}

	/**
	 * Creates a new bulb
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public Bulb(final String name, final String clazz, final String subclass, final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	public boolean onUsed(final RPEntity user) {
		if (!this.isContained()) {
			// the bulb is on the ground, but not next to the player
			if (!this.nextTo(user)) {
				user.sendPrivateText("The bulb is too far away");
				return false;
			}
			// the infostring of the bulb stores what it should grow
			final String infostring = this.getInfoString();
			FlowerBulbGrower flowerGrower;
			// choose the default flower grower if there is none set
			if (infostring == null) {
				flowerGrower = new FlowerBulbGrower();
			} else {
				flowerGrower = new FlowerBulbGrower(this.getInfoString());
			}
			user.getZone().add(flowerGrower);
			// add the FlowerBulbGrower where the bulb was on the ground
			flowerGrower.setPosition(this.getX(), this.getY());
			// The first stage of growth happens almost immediately        
			TurnNotifier.get().notifyInTurns(3, flowerGrower);
			// remove the bulb now that it is planted
			this.removeOne();
			return true;
		}
		// the bulb was 'contained' in a slot and so it cannot be planted
		user.sendPrivateText("Alas, This bulb will not thrive in your pockets! Try putting it on fertile ground to plant it.");
		return false;
	}


	@Override
	public String describe() {
		final String flowerName = getInfoString();

		if (flowerName != null) {
			return "You see a " + flowerName + " bulb. It can be planted anywhere, but it will only thrive on fertile ground.";
		} else {
            //can this actually ever happen?
			return "You see a bulb. It can be planted anywhere, but it will only thrive on fertile ground.";
		}
	}
	
}
