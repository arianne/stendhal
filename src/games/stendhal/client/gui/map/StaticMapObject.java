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

import games.stendhal.client.entity.IEntity;

abstract class StaticMapObject extends MapObject {
	StaticMapObject(final IEntity entity) {
		super(entity);
	}

	/**
	 * Draw the entity
	 *
	 * @param g Graphics context
	 * @param scale Scaling factor
	 * @param color Drawing Color
	 * @param outline Outline color, or <code>null</code> if no outline
	 */
	void draw(final Graphics g, final int scale, final Color color, final Color outline) {
		final int rx = worldToCanvas(x, scale);
		final int ry = worldToCanvas(y, scale);
		final int rwidth = width * scale;
		final int rheight = height * scale;

		g.setColor(color);
		g.fillRect(rx, ry, rwidth, rheight);

		if (outline != null) {
			g.setColor(outline);
			g.drawRect(rx, ry, rwidth - 1, rheight - 1);
		}
	}
}
