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
package tiled.mapeditor.widget;

import java.util.List;

import javax.swing.JPanel;

import tiled.core.Map;
import tiled.core.TileSet;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.util.TileSelectionEvent;
import tiled.mapeditor.util.TileSelectionListener;

/**
 * Base Class of a tileset chooser.
 * 
 * @author mtotz
 */
public abstract class TilesetChooser extends JPanel implements TileSelectionListener {
	private static final long serialVersionUID = 1L;
	protected MapEditor mapEditor;

	/**
	 * 
	 */
	public TilesetChooser(MapEditor mapEditor) {
		this.mapEditor = mapEditor;
	}

	/** informs the editor of the new tile. */
	public void tileSelected(TileSelectionEvent e) {
		mapEditor.setCurrentTiles(e.getTiles());
	}

	/** sets the tilesets to display. */
	public abstract void setTilesets(List<TileSet> tilesets);

	/** sets the tiles panes to the the ones from this map. */
	public abstract void setMap(Map currentMap);

}
