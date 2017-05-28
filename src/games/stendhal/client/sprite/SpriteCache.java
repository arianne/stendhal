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


import org.apache.log4j.Logger;

import games.stendhal.client.MemoryCache;

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
	private MemoryCache<Object, Sprite> sprites;


	/**
	 * Create a sprite cache.
	 */
	public SpriteCache() {
		sprites = new MemoryCache<Object, Sprite>();
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
	void add(final Sprite sprite) {
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
			sprites.put(key, sprite);
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
		return sprites.get(key);
	}
}
