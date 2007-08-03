/*
 * @(#) src/games/stendhal/client/sprite/TilesetAnimationMap.java
 *
 * $Id$
 */

package games.stendhal.client.sprite;

//
//

import java.util.HashMap;
import java.util.Map;

/**
 * A tileset animation map.
 */
public class TilesetAnimationMap {
	/**
	 * The map of tileset animations.
	 */
	protected Map<Integer, int []>	animations;


	/**
	 * Create a tileset animation map.
	 */
	public TilesetAnimationMap() {
		animations = new HashMap<Integer, int []>();
	}


	//
	// TilesetAnimationMap
	//

	/**
	 * Add a mapping of a tile index to animation frame indexes.
	 *
	 * <strong>NOTE: The array of frame indexes passed is not copied,
	 * and should not be altered after this is called.</strong>
	 *
	 * @param	index		The tile index to map.
	 * @param	frameIndexes	The indexes of frame tiles.
	 */
	public void add(final int index, final int [] frameIndexes) {
		animations.put(new Integer(index), frameIndexes);
	}


	/**
	 * Add mappings of a tile indexes to animation frame indexes.
	 * For each frame, a mapping will be created with the remaining
	 * indexes as it's frames (in order, starting with it's index).
	 *
	 * @param	index		The tile index to map.
	 * @param	frameIndexes	The indexes of frame tiles.
	 */
	public void add(final int [] frameIndexes) {
		for(int i = 0; i < frameIndexes.length; i++) {
			int [] frames = new int[frameIndexes.length];
			int tidx = i;

			for(int fidx = 0; fidx < frameIndexes.length; fidx++) {
				frames[fidx] = frameIndexes[tidx];

				if(++tidx >= frameIndexes.length) {
					tidx = 0;
				}
			}

			add(frameIndexes[i], frames);
		}
	}


	/**
	 * Get the animated sprite for an indexed tile of a tileset.
	 *
	 * @param	tileset		The tileset to load from.
	 * @param	index		The index with-in the tileset.
	 *
	 * @return	A sprite, or <code>null</code> if no mapped sprite.
	 */
	public Sprite getSprite(final Tileset tileset, final int index) {
		int [] frameIndexes = animations.get(new Integer(index));

		if(frameIndexes == null) {
			return null;
		}

		Sprite [] frames = new Sprite[frameIndexes.length];

		for(int i = 0; i < frameIndexes.length; i++) {
			frames[i] = tileset.getSprite(frameIndexes[i]);
		}

		return new AnimatedSprite(frames, 500L);
	}
}
