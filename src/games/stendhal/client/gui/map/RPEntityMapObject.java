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
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.NPC;

class RPEntityMapObject extends MovingMapObject {
	private static final Color COLOR_DOMESTIC_ANIMAL = new Color(255, 150, 0);
	private static final Color COLOR_CREATURE = Color.YELLOW;
	private static final Color COLOR_NPC = new Color(0, 150, 0);

	protected Color drawColor;

	RPEntityMapObject(final IEntity entity) {
		super(entity);
		if (entity instanceof NPC) {
			drawColor = COLOR_NPC;
		} else if (entity instanceof DomesticAnimal) {
			drawColor = COLOR_DOMESTIC_ANIMAL;
		} else {
			drawColor = COLOR_CREATURE;
		}
	}

	@Override
	void draw(final Graphics g, final int scale) {
		draw(g, scale, drawColor);
	}
}
