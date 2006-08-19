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

import games.stendhal.common.Debug;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/** It is class to get tiles from the tileset */
public class TileStore extends SpriteStore {
	private class RangeFilename {
		int base;

		int amount;

		String filename;

		boolean loaded;

		RangeFilename(int base, int amount, String filename) {
			this.base = base;
			this.amount = amount;
			this.filename = filename;
			this.loaded = false;
		}

		boolean isInRange(int i) {
			if (i >= base && i < base + amount) {
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

		public String toString() {
			return filename + "[" + base + "," + (base + amount) + "]";
		}

		public void load() {
			SpriteStore sprites;
			sprites = get();
			Sprite tiles = sprites.getSprite(filename);

			GraphicsConfiguration gc = GraphicsEnvironment
					.getLocalGraphicsEnvironment().getDefaultScreenDevice()
					.getDefaultConfiguration();

			for (int j = 0; j < tiles.getHeight() / GameScreen.SIZE_UNIT_PIXELS; j++) {
				for (int i = 0; i < tiles.getWidth()
						/ GameScreen.SIZE_UNIT_PIXELS; i++) {
					Image image = gc.createCompatibleImage(
							GameScreen.SIZE_UNIT_PIXELS,
							GameScreen.SIZE_UNIT_PIXELS, Transparency.BITMASK);
					Graphics2D g = (Graphics2D) image.getGraphics();

					// Bugfixs: parameters width and height added, see comment
					// in Sprite.java
					// tiles.draw(g,0,0,i*GameScreen.SIZE_UNIT_PIXELS,j*GameScreen.SIZE_UNIT_PIXELS);
					// intensifly @ gmx.com, April 20th, 2006

					tiles.draw(g, 0, 0, i * GameScreen.SIZE_UNIT_PIXELS, j
							* GameScreen.SIZE_UNIT_PIXELS,
							GameScreen.SIZE_UNIT_PIXELS,
							GameScreen.SIZE_UNIT_PIXELS);

					// create a sprite, add it the cache then return it
					tileset.set(base + i + j * tiles.getWidth()
							/ GameScreen.SIZE_UNIT_PIXELS, new Sprite(image));
				}
			}

			sprites.free(filename);

			loaded = true;
		}
	}

	private List<RangeFilename> rangesTiles;

	private Vector<Sprite> tileset;

	private static TileStore singleton;

	public static TileStore get() {
		if (singleton == null) {
			singleton = new TileStore();
		}

		return singleton;
	}

	private TileStore() {
		super();
		tileset = new Vector<Sprite>();
		rangesTiles = new LinkedList<RangeFilename>();
	}

	public void add(String ref, int amount) {
		int base = tileset.size();
		tileset.setSize(tileset.size() + amount);

		if (Debug.VERY_FAST_CLIENT_START) {
			rangesTiles.add(new RangeFilename(base, amount, ref));
		} else {
			new RangeFilename(base, amount, ref).load();
		}
	}
	
	private Object locker=new Object();

	public void preload() {
		Thread loader=new Thread() {
			public void run() {
				for(RangeFilename range: rangesTiles) {	
					synchronized(locker) {
						if(!range.isLoaded())  {
							range.load();
						}
					}
				}
			}
		};
		
		loader.start();
	}

	public Sprite getTile(int i) {
		Sprite sprite = tileset.get(i);

		if (Debug.VERY_FAST_CLIENT_START && sprite == null) {
			synchronized(locker) {
				for (RangeFilename range : rangesTiles) {
					if (range.isInRange(i)) {
						StendhalClient.get().addEventLine("Loading tileset " + range.getFilename(),	Color.pink);
						range.load();
						
						sprite = tileset.get(i);
						break;
					}
				}
			}
		}

		return sprite;
	}
}