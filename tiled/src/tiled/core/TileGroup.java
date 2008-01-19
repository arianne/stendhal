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

package tiled.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This is a multilayer group of single tiles. Each tile has an exact position
 * and a destination layer. The size of the group is arbitrary.
 * 
 * Note that the TileGroup is immutable.
 * 
 * @author mtotz
 */
public class TileGroup {
	/** name of the group .*/
	private String name;
	/** assigns layers to the tiles. */
	private java.util.Map<TileLayer, List<StatefulTile>> tileLayers;
	// dimensions of the group
	private int x;
	private int y;
	private int width;
	private int height;

	/** default constructor is private. */
	private TileGroup() {
		this.name = "";
		tileLayers = new HashMap<TileLayer, List<StatefulTile>>();
	}

	/**
	 * creates the tile group.
	 * 
	 * @param map
	 *            the map
	 * @param tiles
	 *            list of stateful tiles
	 * @param name
	 *            optional name of this group
	 */
	public TileGroup(Map map, List<StatefulTile> tiles, String name) {
		this.name = (name == null) ? "" : name;
		tileLayers = new HashMap<TileLayer, List<StatefulTile>>();

		x = Integer.MAX_VALUE;
		y = Integer.MAX_VALUE;
		int maxx = 0;
		int maxy = 0;

		// sort tiles
		for (StatefulTile tile : tiles) {
			MapLayer layer = map.getLayer(tile.layer);
			if (layer != null && layer instanceof TileLayer) {
				getTileLayerSafe((TileLayer) layer).add(tile);

				if (tile.p.x < x) {
					x = tile.p.x;
				}
				if (tile.p.y < y) {
					y = tile.p.y;
				}

				if (tile.p.x > maxx) {
					maxx = tile.p.x;
				}
				if (tile.p.y > maxy) {
					maxy = tile.p.y;
				}
			}
		}
		width = (maxx - x) + 1;
		height = (maxy - y) + 1;
	}

	/**
	 * returns the tilelist for a specific layer (creates one if is does not.
	 * exists yet)
	 */
	private List<StatefulTile> getTileLayerSafe(TileLayer layer) {
		List<StatefulTile> ret = tileLayers.get(layer);
		if (ret == null) {
			ret = new ArrayList<StatefulTile>();
			tileLayers.put(layer, ret);
		}
		return ret;
	}

	/**
	 * returns the tilelist for a specific layer or null if there are no tiles
	 * for the layer in this group.
	 */
	public List<StatefulTile> getTileLayer(TileLayer layer) {
		return tileLayers.get(layer);
	}

	/**
	 * returns the tilelist for a specific layer or null if there are no tiles
	 * for the layer in this group.
	 */
	public List<StatefulTile> getTileLayer(String layerName) {
		for (TileLayer layer : tileLayers.keySet()) {
			if (layer.getName().equals(layerName)) {
				return tileLayers.get(layer);
			}
		}
		return null;
	}

	/**
	 * returns a map with all layers/tilelists of this group.
	 */
	public java.util.Map<TileLayer, List<StatefulTile>> getTileLayers() {
		return Collections.unmodifiableMap(tileLayers);
	}

	/**
	 * Checks if this tile group is still valid with the given map (are all
	 * layers are present?).
	 */
	public boolean isValid(Map map) {
		List<MapLayer> layerList = map.getLayerList();
		for (TileLayer layer : tileLayers.keySet()) {
			if (!layerList.contains(layer)) {
				return false;
			}
		}
		return true;
	}

	/** Returns a new normalized TileGroup which starts at (0,0). */
	public TileGroup normalize() {
		TileGroup tileGroup = new TileGroup();
		tileGroup.name = name;
		tileGroup.width = width;
		tileGroup.height = height;
		tileGroup.x = 0;
		tileGroup.y = 0;

		// copy all layers and normalize the tiles
		for (TileLayer layer : tileLayers.keySet()) {
			List<StatefulTile> tileList = tileGroup.getTileLayerSafe(layer);
			for (StatefulTile tile : getTileLayer(layer)) {
				StatefulTile newTile = new StatefulTile(tile.p, tile.layer, tile.tile);
				newTile.p.translate(-x, -y);
				tileList.add(newTile);
			}
		}

		return tileGroup;
	}

	/** returns the name of the group. */
	public String getName() {
		return name;
	}

	/** sets the name if the tilegroup .*/
	public void setName(String name) {
		this.name = name;
	}

	/** retuns the width in tiles. */
	public int getWidth() {
		return width;
	}

	/** retuns the height in tiles .*/
	public int getHeight() {
		return height;
	}

	/** returns the minimal x position (tile coordinate space) .*/
	public int getX() {
		return x;
	}

	/** returns the minimal y position (tile coordinate space). */
	public int getY() {
		return y;
	}
}
