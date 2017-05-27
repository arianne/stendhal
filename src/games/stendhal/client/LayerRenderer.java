/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client;

import java.awt.Graphics;

//
//

import games.stendhal.client.sprite.Tileset;

/**
 * This is a helper base class to render a layer.
 */
public abstract class LayerRenderer {

	protected int width;

	protected int height;

	public LayerRenderer() {
		width = 0;
		height = 0;
	}

	/** @return the width in world units */
	public int getWidth() {
		return width;
	}

	/** @return the height in world units */
	public int getHeight() {
		return height;
	}

	/**
	 * Render the layer.
	 *
	 * @param g The graphics to draw to
	 * @param x starting x coordinate in world units
	 * @param y starting y coordinate in world units
	 * @param w width in world units
	 * @param h height in world units
	 */
	public abstract void draw(Graphics g, int x, int y, int w, int h);

	/**
	 * Set the tiles used for rendering.
	 *
	 * @param tileset tile set
	 */
	public abstract void setTileset(Tileset tileset);
}
