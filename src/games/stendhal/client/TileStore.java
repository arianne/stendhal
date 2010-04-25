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

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.client.sprite.SpriteTileset;
import games.stendhal.client.sprite.Tileset;
import games.stendhal.client.sprite.TilesetAnimationMap;
import games.stendhal.client.sprite.TilesetGroupAnimationMap;
import games.stendhal.tools.tiled.TileSetDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import marauroa.common.net.InputSerializer;

import org.apache.log4j.Logger;

/** It is class to get tiles from the tileset. */
public class TileStore implements Tileset {
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
	private static final Map<String, Tileset> tilesetsLoaded = new HashMap<String, Tileset>();

	/**
	 * The sprite store.
	 */
	private SpriteStore store;

	/**
	 * The tile sprites.
	 */
	private ArrayList<Sprite> tiles;

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
	public TileStore(final SpriteStore store) {
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
	 */
	private void add(final TileSetDefinition tsdef) {
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
			logger.error("Tileset base index mismatch (" + mapsize + " > "
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

		Tileset tileset = tilesetsLoaded.get(ref);

		if (tileset == null) {
			tileset = new SpriteTileset(store, baseFolder + ref);
			tilesetsLoaded.put(ref, tileset);
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
	 * Add tilesets.
	 * 
	 * @param in
	 *            The object stream.
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void addTilesets(final InputSerializer in) throws IOException,
			ClassNotFoundException {
		final int amount = in.readInt();

		for (int i = 0; i < amount; i++) {
			final TileSetDefinition tileset = (TileSetDefinition) in.readObject(new TileSetDefinition(
					null, -1));
			add(tileset);
		}
	}

	/**
	 * Create the tileset animation map.
	 * 
	 * @return A tileset animation map.
	 */
	protected static TilesetGroupAnimationMap createAnimationMap() {
		final TilesetGroupAnimationMap map = new TilesetGroupAnimationMap();

		final URL url = TileStore.class.getClassLoader().getResource(baseFolder + "tileset/animation.seq");

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

		if (TileStore.class.getClassLoader().getResource("tiled/tileset/README") != null) {
			logger.warn("Developing mode, loading tileset from tiled/tileset instead of data/tileset");
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
	public Sprite getSprite(final int index) {
		if (index >= tiles.size()) {
			logger.error("Accessing unassigned sprite at: " + index);
			return store.getEmptySprite();
		}

		final Sprite sprite = tiles.get(index);

		if (sprite == null) {
			logger.error("Accessing unassigned sprite at: " + index);
			return store.getEmptySprite();
		}

		return sprite;
	}
}
