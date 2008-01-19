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

import java.awt.*;

import javax.swing.SwingConstants;

import tiled.core.*;
import tiled.mapeditor.selection.SelectionLayer;

/**
 * A View for displaying Hex based maps. The Hexs are layed out horizontally
 * (i.e. the pointy sides are on the sides and the flat sides are on the
 * bottom).
 * 
 * <pre>
 *       ___
 * e.g. /   \
 *      \---/
 * </pre>
 * 
 * Even numbered columns are staggered downwards by half a hex.
 * 
 * <pre>
 * e.g.
 *     1,0     3,0
 * 0,0     2,0     4,0
 *     1,1     3,1
 * 0,1     2,1     4,1
 * </pre>
 * 
 * <p>
 * The icon width (as returned by Map.getTileWidth()) refers to the total width
 * of a hex (i.e from the left most corner to the right most corner). The actual
 * distance between two adjacent hexes is equal to 3/4 of this figure.
 * 
 * <p>
 * The icon height (as returned by Map.getTileHeight()) refers to the total
 * height of a hex (i.e. from the bottom edge to the top edge). This is equal to
 * the distance between two adjacent hexes (in the same column)
 */
public class HexMapView extends MapView {
	private static final long serialVersionUID = 1644191239700013106L;

	private static double HEX_SLOPE = Math.tan(Math.toRadians(60));

	public HexMapView(Map m) {
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
		int wbhc = (int) getWidthBetweenHexCentres();

		return new Dimension((int) (myMap.getWidth() * wbhc + border + wbhc), myMap.getHeight() * tsize.height + border
				+ tsize.height / 2);
	}

	protected void paintLayer(Graphics2D g2d, TileLayer layer, double zoom) {
		// Determine area to draw from clipping rectangle
		Dimension tsize = getTileSize(zoom);
		int toffset = (((modeFlags & PF_GRIDMODE) != 0) ? 1 : 0);

		Rectangle clipRect = g2d.getClipBounds();
		int startX = (int) clipRect.x / tsize.width;
		int startY = (int) clipRect.y / tsize.height - 1;
		int endX = (int) (((clipRect.x + clipRect.width) / tsize.width) * 1.5) + 2;
		int endY = (int) ((clipRect.y + clipRect.height) / tsize.height) * 3 + 2;

		int gy = (int) (startY * tsize.height + toffset);
		for (int y = startY; y < endY; y++) {
			Polygon gridPoly = createGridPolygon(0, y, 1);
			gridPoly.translate(0, -(int) (tsize.getHeight() / 2));
			int gx = (int) (startX * tsize.width + toffset);
			for (int x = startX; x < endX; x++) {
				Tile t = layer.getTileAt(x, y);

				if (t != null && t != myMap.getNullTile()) {
					if (layer.getClass() == SelectionLayer.class) {
						g2d.fillPolygon(gridPoly);
					} else {
						t.draw(g2d, gx, (int) (gy + (tsize.getHeight() / 2) * (1 - x % 2)), zoom);
					}
				}
				if (x % 2 == 0) {
					gridPoly.translate((int) (tsize.getWidth() * .75), -(int) (tsize.getHeight() / 2));
				} else {
					gridPoly.translate((int) (tsize.getWidth() * .75), (int) (tsize.getHeight() / 2));
				}
				gx += tsize.getWidth() * .75;
			}
			gy += tsize.getHeight();
		}
	}

	// protected void paintLayer(Graphics2D g2d, ObjectGroup og, double zoom) {
	// }

	/**
	 * Returns the distance between the centres of two horizontally adjacent
	 * Hexes.
	 */
	private double getWidthBetweenHexCentres() {
		return myMap.getTileWidth() * 3 / 4;
	}

	private Dimension getTileSize(double zoom) {
		return new Dimension((int) (myMap.getTileWidth() * zoom), (int) (myMap.getTileHeight() * zoom));
	}

	protected void paintGrid(Graphics2D g2d, double zoom) {
		g2d.setColor(Color.black);
		Dimension tileSize = getTileSize(zoom);
		// Determine area to draw from clipping rectangle
		Rectangle clipRect = g2d.getClipBounds();
		Point topLeft = screenToTileCoords((int) clipRect.getMinX(), (int) clipRect.getMinY());
		Point bottomRight = screenToTileCoords((int) clipRect.getMaxX(), (int) clipRect.getMaxY());
		int startX = (int) topLeft.getX();
		int startY = (int) topLeft.getY();
		int endX = (int) (bottomRight.getX() * 1.5) + 1;
		int endY = (int) bottomRight.getY() * 2 + 1;
		int dy = 0;
		Polygon grid;

		for (int y = startY; y < endY; y++, dy += tileSize.getHeight()) {
			grid = createGridPolygon(0, 0, 1);
			grid.translate(0, dy);
			for (int x = startX; x < endX; x++) {
				grid.translate(0, (int) ((tileSize.getHeight() / 2) * (1 - x % 2)));
				g2d.drawPolygon(grid);
				grid.translate((int) (tileSize.getWidth() * .75), 0);
				grid.translate(0, -(int) ((tileSize.getHeight() / 2) * (1 - x % 2)));
			}
		}
	}

	protected void paintCoordinates(Graphics2D g2d, double zoom) {
		// TODO: Implement paintCoordinates for HexMapView
	}

	public Point screenToTileCoords(int screenX, int screenY) {
		// An algorithm copied from the net years ago
		// Note the C style short variable names :-)
		int x = (int) (screenX / zoom);
		int y = (int) (screenY / zoom);
		double hexWidth = getWidthBetweenHexCentres();
		double hexHeight = myMap.getTileHeight();

		double tw = hexWidth * 2 / 3;
		double cw = hexWidth / 3;

		int adjustyhexes = 10;

		// Note: We adjust my & mx so they are always positive.
		// The algorithm returns incorrect values for negative my
		// The value adjustyhexes is arbitrary.
		// my is only ever negative for offboard hexes at the top of the map
		// We adjust it back further down
		int my = (int) (y + hexHeight * adjustyhexes);
		int mx = (int) (x + cw + hexWidth * adjustyhexes);
		int tx = (int) (mx / hexWidth);
		int rx = (int) (mx % hexWidth);

		if (tx % 2 == 1) {
			my += hexHeight / 2;
		}

		int ty = (int) (my / hexHeight);
		int ry = (int) (my % hexHeight);

		if (rx > tw) {
			double newX = rx - tw;
			double height = (cw - newX) * HEX_SLOPE;
			if (ry < hexHeight / 2 - height) {
				tx++;
				if (tx % 2 == 0) {
					ty--;
				}
			}
			if (ry > hexHeight / 2 + height) {
				tx++;
				if (tx % 2 == 1) {
					ty++;
				}
			}
		}

		// Adjust back (see above)
		ty -= adjustyhexes;
		tx -= adjustyhexes;

		Point result = new Point(tx, ty);

		return result;
	}

	// TODO: remove me
	// /**
	// * Get the point at the top left corner of the bounding rectangle of this
	// * hex.
	// */
	// private Point2D getTopLeftCornerOfHex(int x, int y, double zoom) {
	// Dimension tileSize = getTileSize(zoom);
	// Point2D centre = tileToScreenCoords(x, y);
	// double leftX = centre.getX() - tileSize.getWidth() / 2;
	// double topY = centre.getY() - tileSize.getHeight() / 2;
	// return new Point2D.Double(leftX, topY);
	// }

	private double getTileHeight() {
		return myMap.getTileHeight();
	}

	public void repaintRegion(Rectangle region) {
		super.repaintRegion(region);
		// This code should work. I've disabled it because of general problems
		// with the view refresh.
		// Point2D topLeft=getTopLeftCornerOfHex((int) region.getMinX(),(int)
		// region.getMinY(),zoom);
		// Point2D bottomRight=getTopLeftCornerOfHex((int)
		// region.getMaxX(),(int) region.getMaxY(),zoom);
		//
		// Dimension tileSize=getTileSize(zoom);
		// int width=(int)
		// (bottomRight.getX()-topLeft.getX()+tileSize.getWidth());
		// int height=(int)
		// (bottomRight.getY()-topLeft.getY()+tileSize.getHeight());
		//
		// Rectangle dirty=new Rectangle((int) topLeft.getX(),(int)
		// topLeft.getY(),width,height);
		//
		// repaint(dirty);
	}

	protected Polygon createGridPolygon(int tx, int ty, int border) {
		double centrex, centrey;
		Dimension tileSize = getTileSize(zoom);
		Polygon poly = new Polygon();

		centrex = tx * tileSize.getWidth() + tileSize.getWidth() / 2;
		centrey = ty * tileSize.getHeight() + tileSize.getHeight() / 2;

		// Go round the sides clockwise
		poly.addPoint((int) (centrex - tileSize.getWidth() / 2), (int) centrey);
		poly.addPoint((int) (centrex - tileSize.getWidth() / 4), (int) (centrey - tileSize.getHeight() / 2));
		poly.addPoint((int) (centrex + tileSize.getWidth() / 4), (int) (centrey - tileSize.getHeight() / 2));
		poly.addPoint((int) (centrex + tileSize.getWidth() / 2), (int) centrey);
		poly.addPoint((int) (centrex + tileSize.getWidth() / 4), (int) (centrey + tileSize.getHeight() / 2));
		poly.addPoint((int) (centrex - tileSize.getWidth() / 4), (int) (centrey + tileSize.getHeight() / 2));

		return poly;
	}

	/**
	 * Returns the location on screen for the given tile.
	 * 
	 * @return The point at the centre of the Hex.
	 */
	public Point tileToScreenCoords(double x, double y) {
		double xx = getWidthBetweenHexCentres() * x;
		double yy = getTileHeight() * y;
		if (x % 2 == 0) {
			yy += getTileHeight() / 2;
		}
		return new Point((int) (xx * zoom), (int) (yy * zoom));
	}
}
