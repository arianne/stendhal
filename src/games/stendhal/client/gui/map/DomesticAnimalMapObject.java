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
package games.stendhal.client.gui.map;

import java.awt.Color;
import java.awt.Graphics;

import games.stendhal.client.entity.DomesticAnimal;
import games.stendhal.client.entity.User;

class DomesticAnimalMapObject extends MovingMapObject {
	private static final Color COLOR_DOMESTIC_ANIMAL = new Color(255, 150, 0);

	private DomesticAnimal domesticanimal;
	private Color drawColor;

	DomesticAnimalMapObject(final DomesticAnimal domesticanimal) {
		super(domesticanimal);
		this.domesticanimal = domesticanimal;
		drawColor = COLOR_DOMESTIC_ANIMAL;
	}

	@Override
	void draw(final Graphics g, final int scale) {
		// we check this here rather than in the MapPanel so that any changes to the user are refreshed (e.g. disowning pet)
		User user = User.get();
		if ((user != null) && ((user.hasPet() && user.getPetID() == domesticanimal.getObjectID())
				|| (user.hasSheep() && user.getSheepID() == domesticanimal.getObjectID()))) {
			draw(g, scale, drawColor);
		}
	}
}
