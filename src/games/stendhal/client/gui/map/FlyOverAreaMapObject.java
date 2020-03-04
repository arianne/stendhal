/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
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

public class FlyOverAreaMapObject extends StaticMapObject {
	/**
	 * The colour of fly over areas (orange).
	 */
	private static final Color COLOR = new Color(212, 158, 72);

	FlyOverAreaMapObject(IEntity entity) {
		super(entity);
	}

	@Override
	void draw(Graphics g, int scale) {
		draw(g, scale, COLOR, null);
	}
}
