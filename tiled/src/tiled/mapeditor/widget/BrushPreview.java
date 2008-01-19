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

package tiled.mapeditor.widget;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.JPanel;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.StatefulTile;
import tiled.core.TileLayer;
import tiled.mapeditor.brush.Brush;
import tiled.view.Orthogonal;

/**
 * Shows a preview of the currently selected Brush.
 * 
 * @author mtotz
 */
public class BrushPreview extends JPanel {
	private static final long serialVersionUID = 1L;
	private Brush brush;

	/** sets the new brush. */
	public void updateBrush(Brush brush) {
		this.brush = brush;
		repaint();
	}

	protected void paintComponent(Graphics g) {
		if (brush == null) {
			return;
		}

		List<StatefulTile> tiles = brush.getTiles();
		if (tiles.size() == 0) {
			return;
		}

		// check if we're using the 'delete' tile
		if (tiles.get(0).tile == null) {
			return;
		}

		Rectangle bounds = brush.getBounds();
		if (bounds.width == 0 || bounds.height == 0) {
			return;
		}

		Map newMap = new Map(bounds.width, bounds.height);
		MapLayer[] layers = brush.getAffectedLayers();
		if (layers.length > 0) {
			for (MapLayer layer : layers) {
				TileLayer tileLayer = new TileLayer(bounds.width, bounds.height);
				tileLayer.setName(layer.getName());
				tileLayer.setOpacity(layer.getOpacity());
				newMap.addLayer(tileLayer);
			}
		} else {
			newMap.addLayer();
		}

		int width = tiles.get(0).tile.getWidth();
		int height = tiles.get(0).tile.getHeight();

		Dimension dim = getSize();

		double scalex = (height != 0) ? (1.0 * dim.height / (height * (bounds.height + 1))) : 1.0;
		double scaley = (width != 0) ? (1.0 * dim.width / (width * (bounds.width + 1))) : 1.0;

		double scale = scalex < scaley ? scalex : scaley;

		if (scale < 0.1 || scale > 1.0) {
			scale = 1.0;
		}

		newMap.setTileHeight(height);
		newMap.setTileWidth(width);
		brush.commitPaint(newMap, 0, 0, 0);

		Orthogonal orthogonal = new Orthogonal();
		orthogonal.setMap(newMap);
		orthogonal.setScale(scale);
		orthogonal.draw(g);
	}
}
