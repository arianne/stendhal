/*
 * @(#) src/games/stendhal/client/sprite/Tileset.java
 *
 * $Id$
 */

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
