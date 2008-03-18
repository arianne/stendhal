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

package tiled.mapeditor.builder;

import java.awt.Point;
import java.awt.Rectangle;

import tiled.core.Map;
import tiled.mapeditor.brush.Brush;

/**
 * This builder simply places the current brush at the building location.
 * 
 * @author mtotz
 */
public class SimpleBuilder extends AbstractBuilder {
	/**
	 * Creates a new builder. The map/brush/startLayer must be set before using
	 * the builder
	 */
	public SimpleBuilder() {
		super();
	}

	/** creates a new builder. */
	public SimpleBuilder(Map map, Brush brush, int startLayer) {
		super(map, brush, startLayer);
	}

	/** draws the brush. */
	private Rectangle draw(Point tile, boolean ignoreBrushSize) {
		Rectangle brushSize = brush.getBounds();

		if (brushSize.width == 0 || brushSize.height == 0) {
			return null;
		}

		if (lastPoint == null) {
			lastPoint = new Point(tile);
		}

		int dx = tile.x - lastPoint.x;
		int dy = tile.y - lastPoint.y;

		// do not override the last brush
		if (dx % brushSize.width == 0 && dy % brushSize.height == 0 || ignoreBrushSize) {
			Rectangle modified = brush.commitPaint(map, tile.x, tile.y, startLayer);
			return modified;
		}
		return null;
	}

	/** starts the builder. simply commits the brush to the given tile */
	@Override
	public Rectangle startBuilder(Point tile) {
		start(map.getLayer(startLayer));
		Rectangle modified = draw(tile, true);
		started = true;
		return modified;
	}

	/** commits the brush to the given tile. */
	@Override
	public Rectangle moveBuilder(Point tile) {
		if (!tile.equals(lastPoint)) {
			return draw(tile, false);
		}
		return null;
	}

	/** finished the builder. the last brush commit. */
	@Override
	public Rectangle finishBuilder(Point tile) {
		if (!started) {
			return new Rectangle();
		}

		Rectangle modified = null;

		if (tile != null) {
			if (!tile.equals(lastPoint)) {
				modified = draw(tile, false);
			}
		}
		started = false;
		updateLastPoint(null);

		finish();

		return modified;
	}

	/** returns the brush's bounds. */
	@Override
	public Rectangle getBounds() {
		return brush.getBounds();
	}

}
