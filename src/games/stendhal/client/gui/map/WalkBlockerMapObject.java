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

class WalkBlockerMapObject extends StaticMapObject {
	/**
	 * The colour of walk blockers (dark pink) .
	 */
    private static final Color COLOR = new Color(209, 144, 224);

	WalkBlockerMapObject(final IEntity entity) {
		super(entity);
	}

	@Override
	public void draw(final Graphics g, final int scale) {
		draw(g, scale, COLOR, null);
	}
}
