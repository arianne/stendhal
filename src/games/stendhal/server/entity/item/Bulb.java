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

import games.stendhal.server.actions.PlantBulbAction;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.RPEntity;

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
		
		final PlantBulbAction plantAction = new PlantBulbAction();
		plantAction.setUser(user);
		plantAction.setBulb(this);
		return plantAction.execute();
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
