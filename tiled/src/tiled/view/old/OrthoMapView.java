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

package tiled.view.old;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingConstants;

import tiled.core.*;
import tiled.mapeditor.selection.SelectionLayer;

public class OrthoMapView extends MapView {
	private static final long serialVersionUID = -6457593181854273044L;

	public OrthoMapView(Map m) {
		super(m);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		Dimension tsize = getTileSize(zoom);

		if (orientation == SwingConstants.VERTICAL) {
			return (visibleRect.height / tsize.height) * tsize.height;
		} else {
			return (visibleRect.width / tsize.width) * tsize.width;
		}
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		Dimension tsize = getTileSize(zoom);
		if (orientation == SwingConstants.VERTICAL) {
			return tsize.height;
		} else {
			return tsize.width;
		}
	}

	public Dimension getPreferredSize() {
		Dimension tsize = getTileSize(zoom);
		int border = ((modeFlags & PF_GRIDMODE) != 0) ? 1 : 0;

		return new Dimension(myMap.getWidth() * tsize.width + border, myMap.getHeight() * tsize.height + border);
	}

	protected void paintLayer(Graphics2D g2d, TileLayer layer, double zoom) {
		// Determine tile size and offset
		Dimension tsize = getTileSize(zoom);
		if (tsize.width <= 0 || tsize.height <= 0) {
			return;
		}
		int toffset = (((modeFlags & PF_GRIDMODE) != 0) ? 1 : 0);

		// Determine area to draw from clipping rectangle
		Rectangle clipRect = g2d.getClipBounds();
		int startX = clipRect.x / tsize.width;
		int startY = clipRect.y / tsize.height;
		int endX = (clipRect.x + clipRect.width) / tsize.width + 1;
		int endY = (clipRect.y + clipRect.height) / tsize.height + 3;
		// (endY +2 for high tiles, could be done more properly)

		// Draw this map layer
		for (int y = startY, gy = startY * tsize.height + toffset; y < endY; y++, gy += tsize.height) {
			for (int x = startX, gx = startX * tsize.width + toffset; x < endX; x++, gx += tsize.width) {
				Tile tile = layer.getTileAt(x, y);

				if (tile != null && tile != myMap.getNullTile()) {
					if (layer instanceof SelectionLayer) {
						Polygon gridPoly = createGridPolygon(gx, gy, 0);
						g2d.fillPolygon(gridPoly);
						// paintEdge(g, layer, gx, gy);
					} else {
						tile.draw(g2d, gx, gy, zoom);
					}
				}
			}
		}
	}

	// protected void paintLayer(Graphics2D g, ObjectGroup layer, double zoom) {
	// Iterator itr = layer.getObjects();
	//
	// while (itr.hasNext()) {
	// MapObject mo = (MapObject) itr.next();
	// double ox = mo.getX()*zoom;
	// double oy = mo.getY()*zoom;
	//
	// g.setColor(Color.black);
	// g.fillOval((int)ox, (int)oy, (int)(10 * zoom), (int)(10 * zoom));
	// if (zoom > 0.0625) {
	// g.setColor(Color.white);
	// g.drawString(mo.getType(),(int)(ox - 12),(int)(oy - 5));
	// }
	// }
	// }

	protected void paintGrid(Graphics2D g2d, double zoom) {
		// Determine tile size
		Dimension tsize = getTileSize(zoom);
		if (tsize.width <= 0 || tsize.height <= 0) {
			return;
		}

		// Determine lines to draw from clipping rectangle
		Rectangle clipRect = g2d.getClipBounds();
		int startX = clipRect.x / tsize.width;
		int startY = clipRect.y / tsize.height;
		int endX = (clipRect.x + clipRect.width) / tsize.width + 1;
		int endY = (clipRect.y + clipRect.height) / tsize.height + 1;
		int p = startY * tsize.height;

		for (int y = startY; y < endY; y++) {
			g2d.drawLine(clipRect.x, p, clipRect.x + clipRect.width - 1, p);
			p += tsize.height;
		}
		p = startX * tsize.width;
		for (int x = startX; x < endX; x++) {
			g2d.drawLine(p, clipRect.y, p, clipRect.y + clipRect.height - 1);
			p += tsize.width;
		}
	}

	protected void paintCoordinates(Graphics2D g2d, double zoom) {
		Dimension tsize = getTileSize(zoom);
		if (tsize.width <= 0 || tsize.height <= 0) {
			return;
		}
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Determine tile size and offset
		int toffset = (((modeFlags & PF_GRIDMODE) != 0) ? 1 : 0);
		Font font = new Font("SansSerif", Font.PLAIN, tsize.height / 4);
		g2d.setFont(font);
		FontRenderContext fontRenderContext = g2d.getFontRenderContext();

		// Determine area to draw from clipping rectangle
		Rectangle clipRect = g2d.getClipBounds();
		int startX = clipRect.x / tsize.width;
		int startY = clipRect.y / tsize.height;
		int endX = (clipRect.x + clipRect.width) / tsize.width + 1;
		int endY = (clipRect.y + clipRect.height) / tsize.height + 1;

		// Draw the coordinates
		int gy = startY * tsize.height + toffset;
		for (int y = startY; y < endY; y++) {
			int gx = startX * tsize.width + toffset;
			for (int x = startX; x < endX; x++) {
				String coords = "(" + x + "," + y + ")";
				Rectangle2D textSize = font.getStringBounds(coords, fontRenderContext);

				int fx = gx + (int) ((tsize.width - textSize.getWidth()) / 2);
				int fy = gy + (int) ((tsize.height + textSize.getHeight()) / 2);

				g2d.drawString(coords, fx, fy);
				gx += tsize.width;
			}
			gy += tsize.height;
		}
	}

	public void repaintRegion(Rectangle region) {
		Dimension tsize = getTileSize(zoom);
		if (tsize.width <= 0 || tsize.height <= 0) {
			return;
		}
		int toffset = (((modeFlags & PF_GRIDMODE) != 0) ? 1 : 0);
		int maxExtraHeight = (int) ((myMap.getTileHeightMax() * zoom + toffset) - tsize.height);

		// Calculate the visible corners of the region
		int startX = region.x * tsize.width + toffset;
		int startY = region.y * tsize.height + toffset - maxExtraHeight;
		int endX = (region.x + region.width) * tsize.width;
		int endY = (region.y + region.height) * tsize.height;

		Rectangle dirty = new Rectangle(startX, startY, (endX - startX), (endY - startY));

		repaint(dirty);
	}

	public Point screenToTileCoords(int x, int y) {
		Dimension tsize = getTileSize(zoom);
		return new Point(x / tsize.width, y / tsize.height);
	}

	protected Dimension getTileSize(double zoom) {
		int grid = (((modeFlags & PF_GRIDMODE) != 0) ? 1 : 0);

		return new Dimension((int) (myMap.getTileWidth() * zoom) + grid, (int) (myMap.getTileHeight() * zoom) + grid);
	}

	protected Polygon createGridPolygon(int tx, int ty, int border) {
		Dimension tsize = getTileSize(zoom);

		Polygon poly = new Polygon();
		poly.addPoint(tx - border, ty - border);
		poly.addPoint(tx + tsize.width + border, ty - border);
		poly.addPoint(tx + tsize.width + border, ty + tsize.height + border);
		poly.addPoint(tx - border, ty + tsize.height + border);

		return poly;
	}

	public Point tileToScreenCoords(double x, double y) {
		Dimension tsize = getTileSize(zoom);
		return new Point((int) x * tsize.width, (int) y * tsize.height);
	}
}
