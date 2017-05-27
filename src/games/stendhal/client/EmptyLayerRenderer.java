/***************************************************************************
 *                 (C) Copyright 2003-2014 - Faiumoni e.V.                 *
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

import games.stendhal.client.sprite.Tileset;

/**
 * A dummy layer renderer.
 */
public class EmptyLayerRenderer extends LayerRenderer {
	@Override
	public void draw(Graphics g, int x, int y, int w, int h) {
		// Draw nothing
	}

	@Override
	public void setTileset(Tileset tileset) {
		// Do nothing
	}
}
