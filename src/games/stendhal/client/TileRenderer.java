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

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.Tileset;
import games.stendhal.tools.tiled.LayerDefinition;

import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * This is a helper class to render coherent tiles based on the tileset. This
 * should be replaced by independent tiles as soon as possible .
 */
public class TileRenderer extends LayerRenderer {
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(TileRenderer.class);

	private int[] map;

	private Sprite[] spriteMap;

	public TileRenderer() {
		map = null;
		spriteMap = null;
	}

	/**
	 * Sets the data that will be rendered.
	 * @param in the stream to read from
	 * @throws IOException 
	 * 
	 * @throws ClassNotFoundException
	 */
	public void setMapData(final InputStream in) throws IOException,
			ClassNotFoundException {
		final LayerDefinition layer = LayerDefinition.decode(in);
		width = layer.getWidth();
		height = layer.getHeight();

		logger.debug("Layer(" + layer.getName() + "): " + width + "x" + height);

		map = layer.expose();
	}

	/**
	 * Set the tileset.
	 * 
	 * @param tileset
	 *            The tileset.
	 */
	@Override
	public void setTileset(final Tileset tileset) {
		if (tileset != null) {
			/*
			 * Cache sprites
			 */
			spriteMap = new Sprite[map.length];

			int i = spriteMap.length;

			while (i-- != 0) {
				spriteMap[i] = tileset.getSprite(map[i]);
			}
		} else {
			spriteMap = null;
		}
	}

	/**
	 * Render the layer to screen. We assume that game screen will clip.
	 * 
	 * @param screen
	 *            The screen to draw on.
	 * @param x
	 *            The view X world coordinate.
	 * @param y
	 *            The view Y world coordinate.
	 * @param w
	 *            The view world width.
	 * @param h
	 *            The view world height.
	 */
	@Override
	public void draw(final IGameScreen screen, int x, int y, final int w, final int h) {
		if (spriteMap == null) {
			return;
		}

		final int x2 = Math.min(x + w + 1, getWidth());
		final int y2 = Math.min(y + h + 1, getHeight());

		if (x > 0) {
			x--;
		} else {
			x = 0;
		}

		if (y > 0) {
			y--;
		} else {
			y = 0;
		}

		final Point p = screen.convertWorldToScreenView(x, y);

		int sy = p.y;

		for (int j = y; j < y2; j++) {
			int mapidx = (j * width) + x;
			int sx = p.x;

			for (int i = x; i < x2; i++) {
				spriteMap[mapidx].draw(screen.getGraphics(), sx, sy);
				mapidx++;
				sx += IGameScreen.SIZE_UNIT_PIXELS;
			}

			sy += IGameScreen.SIZE_UNIT_PIXELS;
		}
	}
}
