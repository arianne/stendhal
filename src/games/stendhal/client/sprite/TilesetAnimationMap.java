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
	 * The default frame delay (in ms).
	 */
	public static final int DEFAULT_DELAY = 500;

	/**
	 * The map of tileset animations.
	 */
	protected Map<Integer, Mapping> animations;

	/**
	 * Create a tileset animation map.
	 */
	public TilesetAnimationMap() {
		animations = new HashMap<Integer, Mapping>();
	}

	//
	// TilesetAnimationMap
	//

	/**
	 * Add a mapping of a tile index to animation frame indexes.
	 * 
	 * <strong>NOTE: The array of frame indexes/delays passed is not copied, and
	 * should not be altered after this is called.</strong>
	 * 
	 * @param index
	 *            The tile index to map.
	 * @param frameIndexes
	 *            The indexes of frame tiles.
	 * @param frameDelays
	 *            The frame delays (in ms).
	 */
	public void add(final int index, final int[] frameIndexes,
			final int[] frameDelays) {
		animations.put(new Integer(index), new Mapping(frameIndexes,
				frameDelays));
	}

	/**
	 * Add mappings of a tile indexes to animation frame indexes. For each
	 * frame, a mapping will be created with the remaining indexes as it's
	 * frames (in order, starting with it's index).
	 * 
	 * @param frameIndexes
	 *            The indexes of frame tiles.
	 * @param frameDelays
	 *            The frame delays (in ms).
	 * 
	 * @throws IllegalArgumentException
	 *             If the number of indexes and delays don't match.
	 */
	public void add(final int[] frameIndexes, final int[] frameDelays) {
		if (frameIndexes.length != frameDelays.length) {
			throw new IllegalArgumentException(
					"Mismatched number of frame indexes and delays");
		}

		for (int i = 0; i < frameIndexes.length; i++) {
			int[] frames = new int[frameIndexes.length];
			int[] delays = new int[frameIndexes.length];
			int tidx = i;

			for (int fidx = 0; fidx < frameIndexes.length; fidx++) {
				frames[fidx] = frameIndexes[tidx];
				delays[fidx] = frameDelays[tidx];

				if (++tidx >= frameIndexes.length) {
					tidx = 0;
				}
			}

			add(frameIndexes[i], frames, delays);
		}
	}

	/**
	 * Get the animated sprite for an indexed tile of a tileset.
	 * 
	 * @param tileset
	 *            The tileset to load from.
	 * @param index
	 *            The index with-in the tileset.
	 * 
	 * @return A sprite, or <code>null</code> if no mapped sprite.
	 */
	public Sprite getSprite(final Tileset tileset, final int index) {
		Mapping mapping = animations.get(new Integer(index));

		if (mapping == null) {
			return null;
		}

		int[] frameIndexes = mapping.getIndexes();
		int[] frameDelays = mapping.getDelays();

		Sprite[] frames = new Sprite[frameIndexes.length];

		for (int i = 0; i < frameIndexes.length; i++) {
			frames[i] = tileset.getSprite(frameIndexes[i]);
		}

		return new AnimatedSprite(frames, frameDelays, true, null);
	}

	//
	//

	/**
	 * A frame indexes/delays mapping entry for an animated tile.
	 */
	protected static class Mapping {
		/**
		 * The frame indexes.
		 */
		protected int[] indexes;

		/**
		 * The frame delays.
		 */
		protected int[] delays;

		public Mapping(final int[] indexes, final int[] delays) {
			this.indexes = indexes;
			this.delays = delays;
		}

		//
		// Mapping
		//

		public int[] getDelays() {
			return delays;
		}

		public int[] getIndexes() {
			return indexes;
		}
	}
}
