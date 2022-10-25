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

import games.stendhal.client.entity.Creature;

class EnemiesMapObject extends MovingMapObject {
	private static final Color COLOR_CREATURE= new Color(150, 0, 85);

	private Creature creature;
	private Color drawColor;

	EnemiesMapObject(final Creature creature) {
		super(creature);
		this.creature = creature;
		drawColor = COLOR_CREATURE;
	}
	
	/**
	 * Draws a player using given color.
	 *
	 * @param g The graphics context
	 * @param scale Scaling factor
	 * @param color The draw color
	 */
	@Override
	void draw(final Graphics g, final int scale,  final Color color) {
		int mapX = worldToCanvas(x, scale);
		int mapY = worldToCanvas(y, scale);
		final int size = scale;

		mapX += scale;
		mapY += scale;

		g.setColor(drawColor);
		g.drawLine(mapX - size, mapY, mapX + size, mapY);
		g.drawLine(mapX, mapY - size, mapX, mapY + size);
	}
}
