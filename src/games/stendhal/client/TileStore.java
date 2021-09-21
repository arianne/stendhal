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

import java.awt.Color;
import java.awt.Composite;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.client.sprite.DataLoader;
import games.stendhal.client.sprite.FlippedSprite;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.client.sprite.SpriteTileset;
import games.stendhal.client.sprite.Tileset;
import games.stendhal.client.sprite.TilesetAnimationMap;
import games.stendhal.client.sprite.TilesetGroupAnimationMap;
import games.stendhal.common.tiled.TileSetDefinition;
import marauroa.common.net.InputSerializer;

/** It is class to get tiles from the tileset. */
class TileStore implements Tileset {
	/** Tiled reserves 3 highest bits for tile flipping. */
	private static final int TILE_FLIP_MASK = 0xE0000000;
	/**
	 * Tiled reserves 3 highest bits for tile flipping. Mask for getting the
	 * actual tile number.
	 */
	private static final int TILE_ID_MASK = 0xFFFFFFFF ^ TILE_FLIP_MASK;
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(TileStore.class);

	/**
	 * The base directory for tileset resources.
	 */
	private static final String baseFolder = getResourceBase();

	/**
	 * The tileset animation map.
	 */
	private static final TilesetGroupAnimationMap animationMap = createAnimationMap();

	/**
	 * A cache of loaded tilesets.
	 */
	private static final MemoryCache<String, Tileset> tilesetsLoaded = new MemoryCache<String, Tileset>();

	/**
	 * The sprite store.
	 */
	private final SpriteStore store;

	/**
	 * The tile sprites.
	 */
	private final ArrayList<Sprite> tiles;
	/** Tilesets waiting to be added to the store. */
	private final List<TileSetDefinition> tilesets = new ArrayList<TileSetDefinition>();
	/**
	 * <code>true</code>, if the store has been successfully validated,
	 *	otherwise <code>false</code>.
	 */
	private boolean validated;

	/**
	 * Get the animation map.
	 *
	 * @return animation map
	 */
	public static TilesetGroupAnimationMap getAnimationMap() {
		return animationMap;
	}

	/**
	 * Create a tile store.
	 */
	public TileStore() {
		this(SpriteStore.get());
	}

	/**
	 * Create a tile store with a specific sprite store.
	 *
	 * @param store
	 *            A sprite store.
	 */
	private TileStore(final SpriteStore store) {
		this.store = store;

		tiles = new ArrayList<Sprite>();
		tiles.add(store.getEmptySprite());
	}

	//
	// TileStore
	//

	/**
	 * Add a tileset.
	 *
	 * @param tsdef
	 *            The tileset definition.
	 * @param color Color for modifying the tileset image, or <code>null</code>
	 * @param blend Blend mode for applying the adjustment color, or
	 * 	<code>null</code>
	 */
	private void add(final TileSetDefinition tsdef, Color color, Composite blend) {
		String ref = tsdef.getSource();
		final int baseindex = tsdef.getFirstGid();

		/*
		 * Strip off leading path info TODO: Remove this earlier in the stage
		 * (server side?)
		 */
		if (ref.startsWith("../../")) {
			ref = ref.substring(6);
		}

		/*
		 * Make sure we are the right size
		 */
		final int mapsize = tiles.size();

		if (mapsize > baseindex) {
			logger.debug("Tileset base index mismatch (" + mapsize + " > "
					+ baseindex + "): " + ref);
			for (int i = baseindex; i < mapsize; i++) {
				tiles.remove(baseindex);
			}
		} else if (mapsize < baseindex) {
			logger.debug("Tileset base index mismatch (" + mapsize + " < "
					+ baseindex + "): " + ref);
			/*
			 * Pad missing entries
			 */
			for (int i = mapsize; i < baseindex; i++) {
				tiles.add(null);
			}
		}

		String realRef;
		if ((color != null) && (blend != null)) {
			realRef = store.createModifiedRef(ref, color, blend);
		} else {
			realRef = ref;
		}
		Tileset tileset = tilesetsLoaded.get(realRef);

		if (tileset == null) {
			tileset = new SpriteTileset(store, baseFolder + ref, color, blend);
			tilesetsLoaded.put(realRef, tileset);
		}

		final int size = tileset.getSize();

		tiles.ensureCapacity(baseindex + size);

		for (int i = 0; i < size; i++) {
			tiles.add(tileset.getSprite(i));
		}

		/*
		 * Override the animated tiles (if any)
		 */
		final TilesetAnimationMap tsam = animationMap.get(ref);

		if (tsam != null) {
			for (int i = 0; i < size; i++) {
				final Sprite sprite = tsam.getSprite(tileset, i);

				if (sprite != null) {
					tiles.set(baseindex + i, sprite);
				}
			}
		}
	}

	/**
	 * Add tilesets. The store will require validating afterwards.
	 *
	 * @param in
	 *            The object stream.
	 * 	<code>null</code>
	 *
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	void addTilesets(final InputSerializer in) throws IOException,
			ClassNotFoundException {
		final int amount = in.readInt();

		for (int i = 0; i < amount; i++) {
			final TileSetDefinition tileset = (TileSetDefinition) in.readObject(new TileSetDefinition(null, null, -1));
			tilesets.add(tileset);
		}
	}
	/**
	 * Try finishing the tile store withan adjustment color and blend mode for
	 * the tilesets.
	 *
	 * @param color Color for adjusting the tileset, or <code>null</code>
	 * @param blend blend mode for applying the adjustment color, or
	 * @return <code>true</code> if the store has been validated successfully,
	 * 	<code>false</code> otherwise
	 */
	boolean validate(final Color color, final Composite blend) {
		if (!validated) {
			if (!tilesets.isEmpty()) {
				for (TileSetDefinition def : tilesets) {
					add(def, color, blend);
				}
				tilesets.clear();
				validated = true;
				return true;
			}
			return false;
		}
		return true;
	}

	/**
	 * Create the tileset animation map.
	 *
	 * @return A tileset animation map.
	 */
	private static TilesetGroupAnimationMap createAnimationMap() {
		final TilesetGroupAnimationMap map = new TilesetGroupAnimationMap();

		final URL url = DataLoader.getResource(baseFolder + "tileset/animation.seq");

		if (url != null) {
			try {
				final InputStream in = url.openStream();

				try {
					map.load(in);
				} finally {
					in.close();
				}
			} catch (final IOException ex) {
				logger.error("Error loading tileset animation map", ex);
			}
		}

		return map;
	}

	/**
	 * Get the base directory for tileset resources.
	 *
	 * Hack: Read the tileset directly from tiled/tileset if started from an
	 * IDE.
	 * @return the / separated url to the resource
	 */
	private static String getResourceBase() {
		String path = "data/";

		if (DataLoader.getResource("tiled/tileset/README") != null) {
			logger.debug("Developing mode, loading tileset from tiled/tileset instead of data/tileset");
			path = "tiled/";
		}

		return path;
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
		return tiles.size();
	}

	/**
	 * Get the sprite for an index tile of the tileset.
	 *
	 * @param index
	 *            The index with-in the tileset.
	 *
	 * @return A sprite, or <code>null</code> if no mapped sprite.
	 */
	@Override
	public Sprite getSprite(int index) {
		int flip = index & TILE_FLIP_MASK;
		index &= TILE_ID_MASK;
		if (index >= tiles.size()) {
			logger.error("Accessing unassigned sprite at: " + index);
			return store.getEmptySprite();
		}

		Sprite sprite = tiles.get(index);

		if (sprite == null) {
			logger.error("Accessing unassigned sprite at: " + index);
			return store.getEmptySprite();
		}

		if (flip != 0) {
			sprite = new FlippedSprite(sprite, flip);
		}

		return sprite;
	}
}
