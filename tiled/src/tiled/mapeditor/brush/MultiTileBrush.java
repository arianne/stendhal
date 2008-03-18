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

package tiled.mapeditor.brush;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import tiled.core.MapLayer;
import tiled.core.MultilayerPlane;
import tiled.core.StatefulTile;
import tiled.core.TileLayer;

/**
 * Brush with a custom tile pattern.
 * 
 * @author Matthias Totz &lt;mtotz@users.sourceforge.net&gt;
 */
public class MultiTileBrush extends AbstractBrush {
	private Rectangle cachedBounds;

	public MultiTileBrush() {
	}

	public MultiTileBrush(MultiTileBrush otherBrush) {
		setTiles(otherBrush.selectedTiles);
	}

	/** returns the bounds of the brush in tile coordinates. */
	@Override
	public Rectangle getBounds() {
		if (cachedBounds == null) {
			cachedBounds = recalculateBounds();
		}
		return cachedBounds;
	}

	/** calculates the bounds. */
	private Rectangle recalculateBounds() {
		Point p1 = null;
		Point p2 = null;

		for (StatefulTile tile : selectedTiles) {
			if (p1 == null) {
				p1 = new Point(tile.p);
				p2 = new Point(p1);
			} else {
				if (tile.p.x < p1.x) {
					p1.x = tile.p.x;
				}

				if (tile.p.y < p1.y) {
					p1.y = tile.p.y;
				}

				if (tile.p.x > p2.x) {
					p2.x = tile.p.x;
				}

				if (tile.p.y > p2.y) {
					p2.y = tile.p.y;
				}
			}
		}

		for (StatefulTile tile : selectedTiles) {
			tile.p.x -= p1.x;
			tile.p.y -= p1.y;
		}

		return (p1 == null) ? new Rectangle() : new Rectangle(0, 0, p2.x - p1.x + 1, p2.y - p1.y + 1);
	}

	/** Sets the currently selected Tiles. */
	@Override
	public void setTiles(List<StatefulTile> selectedTiles) {
		super.setTiles(selectedTiles);
		cachedBounds = null;
	}

	/** draws the brush. */
	public Rectangle commitPaint(MultilayerPlane mp, int x, int y, int initLayer) {
		TileLayer tileLayer = (TileLayer) mp.getLayer(initLayer);
		if (tileLayer != null) {
			for (StatefulTile tile : selectedTiles) {
				tileLayer.setTileAt(tile.p.x + x, tile.p.y + y, tile.tile);
			}
		}

		Rectangle rect = new Rectangle(getBounds());
		rect.x += x;
		rect.y += y;

		return rect;
	}

	/** not implemented. */
	public void paint(Graphics g, int x, int y) {
	}

	/** not implemented. */
	public boolean equals(Brush b) {
		return false;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("[MultiTileBrush: ");

		for (StatefulTile tile : selectedTiles) {
			buf.append(tile);
		}

		return buf.toString() + "]";
	}

	/** returns the affected layers. */
	public MapLayer[] getAffectedLayers() {
		return new MapLayer[0];
	}

	public String getName() {
		return "Selected Tiles Brush (" + getBounds().width + "x" + getBounds().height + ")";
	}
}
