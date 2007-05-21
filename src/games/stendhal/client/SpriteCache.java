/*
 * @(#) src/games/stendhal/client/SpriteCache.java
 *
 * $Id$
 */

package games.stendhal.client;

//
//

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * A cache of keyed sprites.
 */
public class SpriteCache {
	/**
	 * The singleton.
	 */
	private static final SpriteCache	sharedInstance	= new SpriteCache();

	/**
	 * The sprite map.
	 */
	protected Map<Object,Reference<Sprite>>	sprites;


	/**
	 * Create a sprite cache.
	 */
	public SpriteCache() {
		sprites = new HashMap<Object,Reference<Sprite>>();
	}


	//
	// SpriteCache
	//

	/**
	 * Add a sprite to the cache. This will use a sprite's getReference()
	 * value as the cache key.
	 *
	 * @param	sprite		The sprite to add.
	 *
	 * @see-also	Sprite#getReference()
	 */
	public void add(Sprite sprite) {
		add(sprite.getReference(), sprite);
	}


	/**
	 * Add a sprite to the cache.
	 *
	 * @param	key		The cache key.
	 * @param	sprite		The sprite to add.
	 */
	public void add(Object key, Sprite sprite) {
		if(key != null) {
			sprites.put(key, new SoftReference<Sprite>(sprite));
		}
	}


	/**
	 * Get the shared instance.
	 *
	 * @return	The shared [singleton] instance.
	 */
	public static SpriteCache get() {
		return sharedInstance;
	}


	/**
	 * Get a cached sprite.
	 *
	 * @param	key		The cache key.
	 *
	 * @return	A sprite, or <code>null</code> if not found.
	 */
	public Sprite get(Object key) {
		if(key == null) {
			return null;
		}

		Reference<Sprite> ref = sprites.get(key);

		if(ref == null) {
			return null;
		}

		Sprite sprite = ref.get();

		if(sprite == null) {
			sprites.remove(key);
		}

		return sprite;
	}
}
