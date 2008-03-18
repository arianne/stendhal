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

package tiled.mapeditor.util;

import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.core.TileSet;

public class TilesetTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 9164983301574753663L;

	private Map map;
	// private String[] columnNames = { "Tileset name", "Usage count" };
	private String[] columnNames = { "Tileset name" };

	public TilesetTableModel(Map map) {
		this.map = map;
	}

	public void setMap(Map map) {
		this.map = map;
		fireTableDataChanged();
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	public int getRowCount() {
		if (map != null) {
			return map.getTilesets().size();
		} else {
			return 0;
		}
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public Object getValueAt(int row, int col) {
		List<TileSet> tilesets = map.getTilesets();
		if (row >= 0 && row < tilesets.size()) {
			TileSet tileset = tilesets.get(row);
			if (col == 0) {
				return tileset.getName();
			} else {
				return "" + checkSetUsage(tileset);
			}
		} else {
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		if (col == 0) {
			return true;
		}
		return false;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		List<TileSet> tilesets = map.getTilesets();
		if (row >= 0 && row < tilesets.size()) {
			TileSet tileset = tilesets.get(row);
			if (col == 0) {
				tileset.setName(value.toString());
			}
			fireTableCellUpdated(row, col);
		}
	}

	private int checkSetUsage(TileSet s) {
		int used = 0;
		Iterator tileIterator = s.iterator();

		while (tileIterator.hasNext()) {
			Tile tile = (Tile) tileIterator.next();
			Iterator itr = map.iterator();

			while (itr.hasNext()) {
				MapLayer ml = (MapLayer) itr.next();

				if (ml instanceof TileLayer) {
					if (((TileLayer) ml).isUsed(tile)) {
						used++;
						break;
					}
				}
			}
		}

		return used;
	}
}
