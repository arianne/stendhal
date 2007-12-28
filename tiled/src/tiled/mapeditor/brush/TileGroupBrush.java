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
 *  Matthias Totz <mtotz@users.sourceforge.net>
 */

package tiled.mapeditor.brush;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import tiled.core.MapLayer;
import tiled.core.MultilayerPlane;
import tiled.core.StatefulTile;
import tiled.core.TileGroup;
import tiled.core.TileLayer;

/**
 * This brush encapsulates a tilegroup. It can draw to one or multiple layers.
 * 
 * @author mtotz
 */
public class TileGroupBrush extends AbstractBrush {
	private TileGroup tileGroup;
	private MapLayer[] cachedLayerIndices;

	/** creates a new brush based on the given tilegroup. */
	public TileGroupBrush(TileGroup tileGroup) {
		this.tileGroup = tileGroup;
	}

	/** returns the bounds of the brush in tile coordinates.*/
	public Rectangle getBounds() {
		return new Rectangle(0, 0, tileGroup.getWidth(), tileGroup.getHeight());
	}

	/**
	 * draws the brush. This ignores the initLayer and uses the layer
	 * information from the TileGroup
	 */
	public Rectangle commitPaint(MultilayerPlane mp, int x, int y, int initLayer) {
		// for all maplayers...
		for (MapLayer mapLayer : mp.getLayerList()) {
			// ...when they are TileLayers and not locked...
			if (mapLayer instanceof TileLayer && !mapLayer.isLocked()) {
				TileLayer tileLayer = (TileLayer) mapLayer;
				List<StatefulTile> groupLayer = tileGroup.getTileLayer(tileLayer.getName());
				// ...commit all tiles from for the layer
				if (groupLayer != null) {
					for (StatefulTile statefulTile : groupLayer) {
						tileLayer.setTileAt(x + statefulTile.p.x, y + statefulTile.p.y, statefulTile.tile);
					}
				}
			}
		}
		Rectangle rect = getBounds();
		rect.translate(x, y);
		return rect;
	}

	/** not implemented. */
	public void paint(Graphics g, int x, int y) {
	}

	/** not implemented. */
	public boolean equals(Brush b) {
		return false;
	}

	public String toString() {
		return "[" + this.getClass().getName() + ":" + tileGroup + "]";
	}

	/** returns the affected layers. */
	public MapLayer[] getAffectedLayers() {
		if (cachedLayerIndices == null) {
			List<TileLayer> layers = new ArrayList<TileLayer>(tileGroup.getTileLayers().keySet());
			cachedLayerIndices = layers.toArray(new MapLayer[layers.size()]);
		}

		return cachedLayerIndices;
	}

	public String getName() {
		return "TileGroup Brush " + tileGroup.getName();
	}

	/** returns the used tiles. */
	public List<StatefulTile> getTiles() {
		List<StatefulTile> tileList = new ArrayList<StatefulTile>();

		for (List<StatefulTile> tile : tileGroup.getTileLayers().values()) {
			tileList.addAll(tile);
		}

		return tileList;
	}

}
