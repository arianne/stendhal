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
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.Tileset;
import games.stendhal.common.tiled.LayerDefinition;

/**
 * This is a helper class to render coherent tiles based on the tileset. This
 * should be replaced by independent tiles as soon as possible .
 */
class TileRenderer extends LayerRenderer {
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(TileRenderer.class);
	/** Tileset used for the map data */
	protected Tileset tileset;
	/** Raw map data. Indices of tiles in the tileset. */
	protected int[] map;
	/** The map data converted to tile references */
	protected Sprite[] spriteMap;

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
		this.tileset = tileset;
	}

	/**
	 * Initialize the sprite map from the tileset and the map data.
	 *
	 * @return true if the map is ready to be used, false otherwise.
	 */
	private boolean initSpriteMap() {
		if (spriteMap == null) {
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
				return false;
			}
		}
		return true;
	}

	@Override
	public void draw(Graphics g, int x, int y, final int w, final int h) {
		if (!initSpriteMap()) {
			return;
		}

		final int endX = Math.min(x + w, getWidth());
		final int endY= Math.min(y + h, getHeight());

		int sy = y * IGameScreen.SIZE_UNIT_PIXELS;
		for (int j = y; j < endY; j++) {
			int mapidx = (j * width) + x;
			int sx = x * IGameScreen.SIZE_UNIT_PIXELS;

			for (int i = x; i < endX; i++) {
				spriteMap[mapidx].draw(g, sx, sy);
				mapidx++;
				sx += IGameScreen.SIZE_UNIT_PIXELS;
			}
			sy += IGameScreen.SIZE_UNIT_PIXELS;
		}
	}
}
