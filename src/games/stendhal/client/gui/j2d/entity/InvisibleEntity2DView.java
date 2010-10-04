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
package games.stendhal.client.gui.j2d.entity;


import games.stendhal.client.gui.styled.cursor.StendhalCursor;

import java.awt.Graphics2D;

/**
 * The 2D view of an invisible entity.
 */
class InvisibleEntity2DView extends Entity2DView {

	//
	// Entity2DView
	//

	@Override
	protected void buildRepresentation() {
	}

	/**
	 * Draw the entity (NOT!).
	 * 
	 * @param g2d
	 *            The graphics to drawn on.
	 */
	@Override
	public void draw(final Graphics2D g2d) {
		applyChanges();
	}

	/**
	 * Determines on top of which other entities this entity should be drawn.
	 * Entities with a high Z index will be drawn on top of ones with a lower Z
	 * index.
	 * 
	 * Also, players can only interact with the topmost entity.
	 * 
	 * @return The drawing index.
	 */
	@Override
	public int getZIndex() {
		return 5000;
	}

	/**
	 * gets the mouse cursor image to use for this entity
	 *
	 * @return StendhalCursor
	 */
	@Override
	public StendhalCursor getCursor() {
		return null;
	}
}
