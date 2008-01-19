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

package tiled.mapeditor.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import tiled.core.Map;
import tiled.core.StatefulTile;
import tiled.core.Tile;
import tiled.core.TileGroup;
import tiled.core.TileLayer;
import tiled.mapeditor.MapEditor;

/**
 * Creates a new single layer brush from the current selection.
 * 
 * @author mtotz
 */
public class CreateSingleLayerBrushAction extends AbstractAction {
	private static final long serialVersionUID = 8534893227991603958L;

	private MapEditor mapEditor;

	public CreateSingleLayerBrushAction(MapEditor mapEditor) {
		super("");
		putValue(SHORT_DESCRIPTION, "Creates a new singlelayer brush");
		putValue(SMALL_ICON, MapEditor.loadIcon("resources/plus.png"));
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent e) {
		List<Point> selectedList = new ArrayList<Point>(mapEditor.getSelectedTiles());

		List<StatefulTile> brushList = new ArrayList<StatefulTile>();
		int layer = mapEditor.currentLayer;
		Map map = mapEditor.getCurrentMap();

		TileLayer tileLayer = (TileLayer) map.getLayer(layer);

		int minx = map.getWidth();
		int miny = map.getHeight();

		// copy tiles
		for (Point p : selectedList) {
			Tile tile = tileLayer.getTileAt(p.x, p.y);
			if (tile != null) {
				brushList.add(new StatefulTile(p, layer, tile));

				if (p.x < minx) {
					minx = p.x;
				}
				if (p.y < miny) {
					miny = p.y;
				}
			}
		}

		if (brushList.size() > 0) {
			// normalize brush
			for (StatefulTile tile : brushList) {
				tile.p.x -= minx;
				tile.p.y -= miny;
			}

			String s = (String) JOptionPane.showInputDialog(mapEditor.appFrame, "Enter a name for the brush:",
					"Brush Name", JOptionPane.PLAIN_MESSAGE, null, null, "name");

			TileGroup group = new TileGroup(map, brushList, s == null ? "unnamed" : s);

			map.addUserBrush(group);
			mapEditor.toolBar.repaint();
		}

	}

}
