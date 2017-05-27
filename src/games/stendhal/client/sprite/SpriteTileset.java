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
package games.stendhal.client.sprite;


import java.awt.Color;
import java.awt.Composite;

import games.stendhal.client.IGameScreen;

/**
 * A tileset that is created from a source sprite.
 */
public class SpriteTileset implements Tileset {
	/**
	 * The sprite tiles.
	 */
	private Sprite[] tiles;

	/**
	 * Create a tileset.
	 *
	 * @param store
	 *            A sprite store.
	 * @param filename
	 *            A sprite resource path.
	 * @param color Adjustment color for the tileset, or <code>null</code>
	 * @param blend Blend mode for applying the adjustment color, or
	 * 	<code>null</code>
	 */
	public SpriteTileset(final SpriteStore store, final String filename,
			final Color color, final Composite blend) {
		this(store, store.getModifiedSprite(filename, color, blend), IGameScreen.SIZE_UNIT_PIXELS);
	}

	/**
	 * Create a tileset.
	 *
	 * @param store
	 *            A sprite store.
	 * @param sprite
	 *            A source sprite.
	 * @param size
	 *            The tile size.
	 */
	public SpriteTileset(final SpriteStore store, final Sprite sprite,
			final int size) {
		if (sprite == null) {
			tiles = new Sprite[0];
		} else {
			tiles = extractTiles(store, sprite, size);
		}
	}

	//
	// SpriteTileset
	//

	/**
	 * Extract all the tiles from a source sprite in left-right, top-bottom scan
	 * order.
	 *
	 * @param store
	 *            A sprite store.
	 * @param sprite
	 *            The master sprite.
	 * @param size
	 *            The tile size.
	 *
	 * @return An array of sprites.
	 */
	private Sprite[] extractTiles(final SpriteStore store,
			final Sprite sprite, final int size) {
		final int rows = sprite.getHeight() / size;
		final int cols = sprite.getWidth() / size;

		final Sprite[] sprites = new Sprite[rows * cols];
		int idx = 0;

		int y = 0;

		for (int row = 0; row < rows; row++) {
			int x = 0;

			for (int col = 0; col < cols; col++) {
				sprites[idx++] = store.getTile(sprite, x, y, size, size);

				x += size;
			}

			y += size;
		}

		return sprites;
	}

	//
	// Tileset
	//

	/**
	 * Get the number of tiles.
	 *
	 * @return The number of tiles.
	 */
	@Override
	public int getSize() {
		return tiles.length;
	}

	/**
	 * Get the sprite for an index tile of a tileset.
	 *
	 * @param index
	 *            The index with-in the tileset.
	 *
	 * @return A sprite, or <code>null</code> if no mapped sprite.
	 */
	@Override
	public Sprite getSprite(final int index) {
		if (index < tiles.length) {
			return tiles[index];
		} else {
			return null;
		}
	}
}
