/***************************************************************************
 *                   (C) Copyright 2013 - Faiumoni e. V.                   *
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

import java.awt.Graphics;

import games.stendhal.client.entity.IEntity;

/**
 * representation of a wall entity on the map
 *
 * @author hendrik
 */
class WallMapObject extends StaticMapObject {

	/**
	 * a wall map object
	 *
	 * @param entity Entity
	 */
	WallMapObject(final IEntity entity) {
		super(entity);
	}

	@Override
	public void draw(final Graphics g, final int scale) {
		draw(g, scale, MapPanel.COLOR_BLOCKED, null);
	}
}
