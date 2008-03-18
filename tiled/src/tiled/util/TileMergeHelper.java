/*
 *  Tiled Map Editor, (c) 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 *  
 *  modified for Stendhal, an Arianne powered RPG 
 *  (http://arianne.sf.net)
 *
 *  Matthias Totz &lt;mtotz@users.sourceforge.net&gt;
 */

package tiled.util;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.core.TileSet;

/**
 * Some helper methods for merging tile images.
 */
public class TileMergeHelper {
	private Map myMap;
	private TileSet myTs;
	private TileLayer mergedLayer;
	private List<Cell> cells;

	public TileMergeHelper(Map map) {
		myMap = map;
		cells = new ArrayList<Cell>();
		myTs = new TileSet();
		myTs.setName("Merged Set");
	}

	public TileLayer merge(int start, int len, boolean all) {
		int w = myMap.getBounds().width;
		int h = myMap.getBounds().height;
		mergedLayer = new TileLayer(w, h);

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				mergedLayer.setTileAt(j, i, createCell(j, i, start, len, all));
			}
		}

		return mergedLayer;
	}

	public TileSet getSet() {
		return myTs;
	}

	public Tile createCell(int tx, int ty, int start, int len, boolean all) {
		Cell c = new Cell(myMap, tx, ty, start, len, all);
		Iterator<Cell> itr = cells.iterator();
		Tile tile;

		while (itr.hasNext()) {
			Cell check = itr.next();
			if (check.equals(c)) {
				return check.getTile();
			}
		}

		cells.add(c);

		tile = new Tile();
		c.setTile(tile);

		// GENERATE MERGED TILE IMAGE
		GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		Image tileImg = config.createCompatibleImage(c.getWidth(), c.getHeight());
		c.render(tileImg.getGraphics());
		tile.setImage(tileImg);

		myTs.addTile(tile);

		return tile;
	}

	private static class Cell {
		private List<Tile> sandwich;
		private Tile myTile;

		public Cell(Map map, int posx, int posy, int start, int len, boolean all) {
			sandwich = new ArrayList<Tile>();
			for (int i = 0; i < len; i++) {
				MapLayer ml = (MapLayer) map.getLayer(start + i);
				if (ml instanceof TileLayer) {
					TileLayer l = (TileLayer) ml;
					if (l != null && (l.isVisible() || all)) {
						sandwich.add(l.getTileAt(posx, posy));
					} else {
						sandwich.add(null);
					}
				}
			}
		}

		public void setTile(Tile t) {
			myTile = t;
		}

		public Tile getTile() {
			return myTile;
		}

		public void render(Graphics g) {
			Iterator<Tile> itr = sandwich.iterator();
			while (itr.hasNext()) {
				Tile t = itr.next();
				if (t != null) {
					t.draw(g, 0, 0, 1.0f);
				}
			}
		}

		public boolean equals(Cell c) {
			Iterator<Tile> me = sandwich.iterator();
			Iterator<Tile> them = c.sandwich.iterator();
			while (me.hasNext()) {
				Tile m = me.next();
				Tile t = them.next();
				if ((m != null && t != null) && !m.equals(t)) {
					return false;
				} else if (m != null && t != null && t != m) {
					return false;
				}
			}
			return true;
		}

		public int getWidth() {
			int width = 0;
			Iterator<Tile> itr = sandwich.iterator();
			while (itr.hasNext()) {
				Tile t = itr.next();
				if (t != null) {
					int w = t.getWidth();
					if (w > width) {
						width = w;
					}
				}
			}
			return width;
		}

		public int getHeight() {
			int height = 0;
			Iterator<Tile> itr = sandwich.iterator();
			while (itr.hasNext()) {
				Tile t = itr.next();
				if (t != null) {
					int h = t.getHeight();
					if (h > height) {
						height = h;
					}
				}
			}
			return height;
		}
	}
}
