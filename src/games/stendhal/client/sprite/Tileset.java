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

/**
 * A tileset.
 */
public interface Tileset {
	/**
	 * Get the number of tiles.
	 *
	 * @return The number of tiles.
	 */
	int getSize();

	/**
	 * Get the sprite for an index tile of a tileset.
	 *
	 * @param index
	 *            The index with-in the tileset.
	 *
	 * @return A sprite, or <code>null</code> if no mapped sprite.
	 */
	Sprite getSprite(final int index);
}
