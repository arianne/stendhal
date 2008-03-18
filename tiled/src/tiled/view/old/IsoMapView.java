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

import tiled.core.Map;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.mapeditor.selection.SelectionLayer;

public class IsoMapView extends MapView {
	private static final long serialVersionUID = -3682315460749405130L;

	public IsoMapView(Map m) {
		super(m);
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		Dimension tsize = getTileSize(zoom);
		if (orientation == SwingConstants.VERTICAL) {
			return (visibleRect.height / tsize.height) * tsize.height;
		} else {
			return (visibleRect.width / tsize.width) * tsize.width;
		}
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		Dimension tsize = getTileSize(zoom);
		if (orientation == SwingConstants.VERTICAL) {
			return tsize.height;
		} else {
			return tsize.width;
		}
	}

	@Override
	protected void paintLayer(Graphics2D g2d, TileLayer layer, double zoom) {
		// Turn anti alias on for selection drawing
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Rectangle clipRect = g2d.getClipBounds();
		Dimension tileSize = getTileSize(zoom);
		int tileStepY = (tileSize.height / 2 == 0) ? 1 : tileSize.height / 2;

		Point rowItr = screenToTileCoords(clipRect.x, clipRect.y);
		rowItr.x--;
		Point drawLoc = tileToScreenCoords(rowItr.x, rowItr.y);
		drawLoc.x -= tileSize.width / 2;

		// Determine area to draw from clipping rectangle
		int columns = clipRect.width / tileSize.width + 3;
		int rows = (clipRect.height + (int) (myMap.getTileHeightMax() * zoom)) / tileStepY + 4;

		// Draw this map layer
		for (int y = 0; y < rows; y++) {
			Point columnItr = new Point(rowItr);

			for (int x = 0; x < columns; x++) {
				Tile tile = layer.getTileAt(columnItr.x, columnItr.y);

				if (tile != null) {
					if (layer instanceof SelectionLayer) {
						Polygon gridPoly = createGridPolygon(drawLoc.x, drawLoc.y, 0);
						g2d.fillPolygon(gridPoly);
						// paintEdge(g2d, layer, drawLoc.x, drawLoc.y);
					} else {
						tile.draw(g2d, drawLoc.x, drawLoc.y, zoom);
					}
				}

				// Advance to the next tile
				columnItr.x++;
				columnItr.y--;
				drawLoc.x += tileSize.width;
			}

			// Advance to the next row
			if ((y & 1) > 0) {
				rowItr.x++;
				drawLoc.x += tileSize.width / 2;
			} else {
				rowItr.y++;
				drawLoc.x -= tileSize.width / 2;
			}
			drawLoc.x -= columns * tileSize.width;
			drawLoc.y += tileStepY;
		}
	}

	// protected void paintLayer(Graphics2D g2d, ObjectGroup og, double zoom) {
	// // TODO: Implement objectgroup painting for IsoMapView
	// }

	@Override
	protected void paintGrid(Graphics2D g2d, double zoom) {
		Dimension tileSize = getTileSize(zoom);
		Rectangle clipRect = g2d.getClipBounds();

		clipRect.x -= tileSize.width / 2;
		clipRect.width += tileSize.width;
		clipRect.height += tileSize.height / 2;

		int startX = Math.max(0, screenToTileCoords(clipRect.x, clipRect.y).x);
		int startY = Math.max(0, screenToTileCoords(clipRect.x + clipRect.width, clipRect.y).y);
		int endX = Math.min(myMap.getWidth(), screenToTileCoords(clipRect.x + clipRect.width, clipRect.y
				+ clipRect.height).x);
		int endY = Math.min(myMap.getHeight(), screenToTileCoords(clipRect.x, clipRect.y + clipRect.height).y);

		for (int y = startY; y <= endY; y++) {
			Point start = tileToScreenCoords(startX, y);
			Point end = tileToScreenCoords(endX, y);
			g2d.drawLine(start.x, start.y, end.x, end.y);
		}
		for (int x = startX; x <= endX; x++) {
			Point start = tileToScreenCoords(x, startY);
			Point end = tileToScreenCoords(x, endY);
			g2d.drawLine(start.x, start.y, end.x, end.y);
		}
	}

	@Override
	protected void paintCoordinates(Graphics2D g2d, double zoom) {
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		Rectangle clipRect = g2d.getClipBounds();
		Dimension tileSize = getTileSize(zoom);
		int tileStepY = (tileSize.height / 2 == 0) ? 1 : tileSize.height / 2;
		Font font = new Font("SansSerif", Font.PLAIN, tileSize.height / 4);
		g2d.setFont(font);
		FontRenderContext fontRenderContext = g2d.getFontRenderContext();

		Point rowItr = screenToTileCoords(clipRect.x, clipRect.y);
		rowItr.x--;
		Point drawLoc = tileToScreenCoords(rowItr.x, rowItr.y);
		drawLoc.y += tileSize.height / 2;

		// Determine area to draw from clipping rectangle
		int columns = clipRect.width / tileSize.width + 3;
		int rows = clipRect.height / tileStepY + 4;

		// Draw the coordinates
		for (int y = 0; y < rows; y++) {
			Point columnItr = new Point(rowItr);

			for (int x = 0; x < columns; x++) {
				if (myMap.contains(columnItr.x, columnItr.y)) {
					String coords = "(" + columnItr.x + "," + columnItr.y + ")";
					Rectangle2D textSize = font.getStringBounds(coords, fontRenderContext);

					int fx = drawLoc.x - (int) (textSize.getWidth() / 2);
					int fy = drawLoc.y + (int) (textSize.getHeight() / 2);

					g2d.drawString(coords, fx, fy);
				}

				// Advance to the next tile
				columnItr.x++;
				columnItr.y--;
				drawLoc.x += tileSize.width;
			}

			// Advance to the next row
			if ((y & 1) > 0) {
				rowItr.x++;
				drawLoc.x += tileSize.width / 2;
			} else {
				rowItr.y++;
				drawLoc.x -= tileSize.width / 2;
			}
			drawLoc.x -= columns * tileSize.width;
			drawLoc.y += tileStepY;
		}
	}

	@Override
	public void repaintRegion(Rectangle region) {
		Dimension tileSize = getTileSize(zoom);
		int maxExtraHeight = (int) (myMap.getTileHeightMax() * zoom) - tileSize.height;

		int mapX1 = region.x;
		int mapY1 = region.y;
		int mapX2 = mapX1 + region.width;
		int mapY2 = mapY1 + region.height;

		int x1 = tileToScreenCoords(mapX1, mapY2).x;
		int y1 = tileToScreenCoords(mapX1, mapY1).y - maxExtraHeight;
		int x2 = tileToScreenCoords(mapX2, mapY1).x;
		int y2 = tileToScreenCoords(mapX2, mapY2).y;

		repaint(new Rectangle(x1, y1, x2 - x1, y2 - y1));
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension tileSize = getTileSize(zoom);
		int border = ((modeFlags & PF_GRIDMODE) != 0) ? 1 : 0;
		int mapSides = myMap.getHeight() + myMap.getWidth();

		return new Dimension((mapSides * tileSize.width) / 2 + border, (mapSides * tileSize.height) / 2 + border);
	}

	/**
	 * Returns the coordinates of the tile at the given screen coordinates.
	 */
	@Override
	public Point screenToTileCoords(int x, int y) {
		Dimension tileSize = getTileSize(zoom);
		double r = getTileRatio();

		// Translate origin to top-center
		x -= myMap.getHeight() * (tileSize.width / 2);
		int mx = y + (int) (x / r);
		int my = y - (int) (x / r);

		// Calculate map coords and divide by tile size (tiles assumed to
		// be square in normal projection)
		return new Point(((mx < 0) ? mx - tileSize.height : mx) / tileSize.height, ((my < 0) ? my - tileSize.height
				: my)
				/ tileSize.height);
	}

	@Override
	protected Polygon createGridPolygon(int tx, int ty, int border) {
		Dimension tileSize = getTileSize(zoom);
		tileSize.width -= border * 2;
		tileSize.height -= border * 2;

		Polygon poly = new Polygon();
		poly.addPoint((tx + tileSize.width / 2 + border), ty + border);
		poly.addPoint((tx + tileSize.width), ty + tileSize.height / 2 + border);
		poly.addPoint((tx + tileSize.width / 2 + border), ty + tileSize.height + border);
		poly.addPoint((tx + border), ty + tileSize.height / 2 + border);
		return poly;
	}

	protected Dimension getTileSize(double zoom) {
		return new Dimension((int) (myMap.getTileWidth() * zoom), (int) (myMap.getTileHeight() * zoom));
	}

	protected double getTileRatio() {
		return (double) myMap.getTileWidth() / (double) myMap.getTileHeight();
	}

	/**
	 * Returns the location on the screen of the top corner of a tile.
	 */
	@Override
	public Point tileToScreenCoords(double x, double y) {
		Dimension tileSize = getTileSize(zoom);
		int originX = (myMap.getHeight() * tileSize.width) / 2;
		return new Point((int) ((x - y) * tileSize.width / 2) + originX, (int) ((x + y) * tileSize.height / 2));
	}
}
