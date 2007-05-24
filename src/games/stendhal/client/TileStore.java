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

import games.stendhal.tools.tiled.TileSetDefinition;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import marauroa.common.Logger;

import marauroa.common.Log4J;
import marauroa.common.net.InputSerializer;

/** It is class to get tiles from the tileset */
public class TileStore extends SpriteStore {
	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(TileStore.class);

	private static class RangeFilename {
		private static String BASE_FOLDER="data";

		// Hack: Read the tileset directly from tiled/tileset if started from an IDE.
		static {
			if (SpriteStore.get().getResourceURL("tiled/tileset/README") != null) {
				logger.warn("Developing mode, loading tileset from tiled/tileset instead of data/tileset");
				BASE_FOLDER = "tiled";
			}
		}

		int amount;

		String filename;

		private Vector<Sprite> tileset;
		boolean loaded;
		
		RangeFilename(String filename) {
			this.amount = 0;
			this.filename = filename;
			this.tileset = new Vector<Sprite>();
			this.loaded = false;
		}

		boolean isInRange(int i) {
			if ((i >= 0) && (i < amount)) {
				return true;
			}

			return false;
		}

		String getFilename() {
			return filename;
		}

		public boolean isLoaded() {
			return loaded;
		}

		@Override
		public String toString() {
			return BASE_FOLDER+filename + "[" + 0 + "," + amount + "]";
		}
		
		public void map(int gid, Vector<Sprite> globaltileset) {
			logger.debug("Loading "+filename+": "+(gid)+" to "+(gid+amount));

			/*
			 * If needed increase vector size.
			 */
			if(gid+amount>=globaltileset.size()) {
				globaltileset.setSize(gid+amount);
			}
			
			for(int i=0;i<amount;i++) {
				globaltileset.set(gid+i,tileset.get(i));
			}			
		}

		public void load() {
			SpriteStore sprites=SpriteStore.get();
			filename=filename.replace("../../", "/");
			
			Sprite tiles = sprites.getSprite(BASE_FOLDER+filename);

//			GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
//			        .getDefaultConfiguration();

			int idx = 0;

			/*
			 * Set the correct size for the vector.
			 */
			tileset.setSize((tiles.getHeight() / GameScreen.SIZE_UNIT_PIXELS)*(tiles.getWidth() / GameScreen.SIZE_UNIT_PIXELS));

			for (int j = 0; j < tiles.getHeight() / GameScreen.SIZE_UNIT_PIXELS; j++) {
				for (int i = 0; i < tiles.getWidth() / GameScreen.SIZE_UNIT_PIXELS; i++) {
					amount++;

					Sprite tile = sprites.getTile(tiles, i * GameScreen.SIZE_UNIT_PIXELS, j * GameScreen.SIZE_UNIT_PIXELS, GameScreen.SIZE_UNIT_PIXELS, GameScreen.SIZE_UNIT_PIXELS);

					tileset.set(idx++, tile);
				}
			}

			sprites.free(BASE_FOLDER+filename);

			loaded = true;
		}
	}

	private Vector<Sprite> zoneTileset;
	static private Map<String, RangeFilename> tilesetsLoaded=new HashMap<String, RangeFilename>();

	public TileStore() {
		super();
		zoneTileset = new Vector<Sprite>();
	}
	
	public void addTilesets(InputSerializer in) throws IOException, ClassNotFoundException {
		int amount=in.readInt();
		
		for(int i=0;i<amount;i++) {
			TileSetDefinition tileset=(TileSetDefinition) in.readObject(new TileSetDefinition(null, -1));
			RangeFilename range=add(tileset.getSource());
			/*
			 * We copy the sprites to the actual zone tileset.
			 */
			range.map(tileset.getFirstGid(), zoneTileset);			
		}
	}

	private RangeFilename add(String ref) {
		RangeFilename range=tilesetsLoaded.get(ref);
		if(range==null) {
			range=new RangeFilename(ref);
			range.load();		
			tilesetsLoaded.put(ref, range);
		}
		
		return range;
	}

	public Sprite getTile(int i) {
		Sprite sprite = zoneTileset.get(i);
		return sprite;
	}
}
