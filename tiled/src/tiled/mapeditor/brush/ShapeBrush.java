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
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import tiled.core.MapLayer;
import tiled.core.MultilayerPlane;
import tiled.core.Tile;
import tiled.core.TileLayer;

public class ShapeBrush extends AbstractBrush {
	protected Shape shape;
	protected Rectangle bounds;

	public ShapeBrush() {
		super();
	}

	public ShapeBrush(Shape shape) {
		super();
		this.shape = shape;
	}

	public ShapeBrush(ShapeBrush sb) {
		super();
		shape = ((ShapeBrush) sb).shape;
	}

	/**
	 * Creates a circular brush.
	 * 
	 * @param diameter
	 *            the diameter of the circular region
	 */
	public static ShapeBrush makeCircleBrush(int diameter) {
		ShapeBrush brush = new ShapeBrush();
		brush.shape = new Ellipse2D.Double(-0.1, -0.1, diameter - 1, diameter - 1);
		brush.bounds = new Rectangle(0, 0, diameter, diameter);
		brush.resize(diameter, diameter, 0, 0);
		return brush;
	}

	/**
	 * Creates a rectangular brush.
	 * 
	 * @param r
	 *            a Rectangle to use as the shape of the brush
	 */
	public static ShapeBrush makeRectBrush(int width, int height) {
		ShapeBrush brush = new ShapeBrush();
		brush.shape = new Rectangle2D.Double(-0.1, -0.1, width, height);
		brush.bounds = new Rectangle(0, 0, width, height);
		brush.resize(width, height, 0, 0);
		return brush;
	}

	/**
	 * Paints the entire area of the brush with the set tile. This brush can
	 * affect several layers.
	 * 
	 * @see Brush#commitPaint(MultilayerPlane, int, int, int)
	 * @return a Rectangle of the bounds of the area that was modified
	 * @param mp
	 *            The multilayer plane that will be modified
	 * @param x
	 *            The x-coordinate where the click occurred.
	 * @param y
	 *            The y-coordinate where the click occurred.
	 */
	public Rectangle commitPaint(MultilayerPlane mp, int x, int y, int initLayer) {
		if (selectedTiles.size() == 0) {
			return new Rectangle(x, y, 0, 0);
		}

		Rectangle bounds = shape.getBounds();

		TileLayer tileLayer = (TileLayer) mp.getLayer(initLayer);
		Tile tile = selectedTiles.get(0).tile;

		for (int x1 = 0; x1 < bounds.width + 1; x1++) {
			for (int y1 = 0; y1 < bounds.height + 1; y1++) {
				if (shape.contains(x1, y1)) {
					tileLayer.setTileAt(x + x1, y + y1, tile);
				}
			}
		}

		// Return affected area
		return new Rectangle(x, y, bounds.width, bounds.height);
	}

	@Override
	public Rectangle getBounds() {
		return bounds;
	}

	public void paint(Graphics g, int x, int y) {
	}

	public boolean equals(Brush b) {
		if (b instanceof ShapeBrush) {
			return ((ShapeBrush) b).shape.equals(shape);
		}
		return false;
	}

	/** returns the affected layers. */
	public MapLayer[] getAffectedLayers() {
		return new MapLayer[0];
	}

	public String getName() {
		if (shape instanceof Ellipse2D) {
			return "Circle Brush (" + ((int) bounds.getBounds().getWidth()) + ")";
		}
		if (shape instanceof Rectangle2D) {
			return "Rect Brush (" + ((int) bounds.getWidth()) + "x" + ((int) bounds.getHeight()) + ")";
		}

		return "Shape Brush";
	}

}
