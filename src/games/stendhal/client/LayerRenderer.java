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

/**
 * This is a helper base class to render a layer
 */
public abstract class LayerRenderer {

	protected int width;

	protected int height;

	public LayerRenderer() {
		width = height = 0;
	}

	/** Returns the widht in world units */
	public int getWidth() {
		return width;
	}

	/** Returns the height in world units */
	public int getHeight() {
		return height;
	}

	/**
	 * Render the data to screen.
	 */

	public abstract void draw(GameScreen screen);

}