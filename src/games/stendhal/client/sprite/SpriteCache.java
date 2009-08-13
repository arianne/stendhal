/*
 * @(#) src/games/stendhal/client/sprite/SpriteCache.java
 *
 * $Id$
 */

package games.stendhal.client.sprite;

//
//

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * A cache of keyed sprites.
 */
public class SpriteCache {
	/**
	 * The logger instance.
	 */
	private static final Logger LOGGER = Logger.getLogger(SpriteCache.class);

	/**
	 * The singleton.
	 */
	private static final SpriteCache sharedInstance = new SpriteCache();

	/**
	 * The sprite map.
	 */
	protected Map<Object, Reference<Sprite>> sprites;

	/**
	 * Create a sprite cache.
	 */
	public SpriteCache() {
		sprites = new HashMap<Object, Reference<Sprite>>();
	}

	//
	// SpriteCache
	//

	/**
	 * Add a sprite to the cache. This will use a sprite's getReference() value
	 * as the cache key.
	 * 
	 * @param sprite
	 *            The sprite to add.
	 * 
	 * @see Sprite#getReference()
	 */
	public void add(final Sprite sprite) {
		add(sprite.getReference(), sprite);
	}

	/**
	 * Add a sprite to the cache.
	 * 
	 * @param key
	 *            The cache key.
	 * @param sprite
	 *            The sprite to add.
	 */
	public void add(final Object key, final Sprite sprite) {
		if (key != null) {
			sprites.put(key, new SoftReference<Sprite>(sprite));
			LOGGER.debug("SpriteCache - add: " + key);
		}
	}

	/**
	 * Get the shared instance.
	 * 
	 * @return The shared [singleton] instance.
	 */
	public static SpriteCache get() {
		return sharedInstance;
	}

	/**
	 * Get a cached sprite.
	 * 
	 * @param key
	 *            The cache key.
	 * 
	 * @return A sprite, or <code>null</code> if not found.
	 */
	public Sprite get(final Object key) {
		if (key == null) {
			return null;
		}

		final Reference<Sprite> ref = sprites.get(key);

		if (ref == null) {
			LOGGER.debug("SpriteCache - miss: " + key);
			return null;
		}

		final Sprite sprite = ref.get();

		if (sprite == null) {
			LOGGER.debug("SpriteCache - GC'd miss: " + key);
			sprites.remove(key);
		} else {
			LOGGER.debug("SpriteCache - hit: " + key);
		}

		return sprite;
	}
}
