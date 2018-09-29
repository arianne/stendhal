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
package games.stendhal.client.gui.j2d.entity;

import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;

/**
 * The 2D view of a fly over area.
 */
public class FlyOverArea2DView extends InvisibleEntity2DView<IEntity> {
	/**
	 * Drawn just under walk blocker.
	 *
	 * @see games.stendhal.client.gui.j2d.entity.InvisibleEntity2DView.getZIndex()
	 * @return the drawing index
	 */
	@Override
	public int getZIndex() {
		return 2999;
	}

	/**
	 * Use normal cursor for these areas.
	 */
	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.NORMAL;
	}
}
