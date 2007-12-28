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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import tiled.core.Map;
import tiled.core.TileSet;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.util.TileSelectionEvent;
import tiled.mapeditor.util.TileSelectionListener;

/**
 * Shows one tab for each Tileset.
 * 
 * @author Matthias Totz <mtotz@users.sourceforge.net>
 */
public class TilesetChooserTabbedPane extends TilesetChooser implements TileSelectionListener {
	private static final long serialVersionUID = -2997343048462435218L;
	/** list of the tilesets for the current map. */
	private List<TilePalettePanel> tilePanels;

	private JTabbedPane pane;

	/** */
	public TilesetChooserTabbedPane(MapEditor mapEditor) {
		super(mapEditor);
		pane = new JTabbedPane();
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		add(pane);
	}

	/** sets the tilesets to display. */
	public void setTilesets(List<TileSet> tilesets) {
		// recreate the panes
		recreateUI(tilesets);
		addTileSelectionListener(this);
	}

	/** creates the panels for the tilesets. */
	private void recreateUI(List<TileSet> tilesets) {
		// remove all tabs
		pane.removeAll();
		tilePanels = new ArrayList<TilePalettePanel>();

		if (tilesets != null) {
			// add one tab for each tileset
			for (TileSet tileset : tilesets) {
				if (tileset != null) {
					TilePalettePanel tilePanel = new TilePalettePanel(mapEditor, tileset);
					tilePanels.add(tilePanel);
					JScrollPane paletteScrollPane = new JScrollPane(tilePanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
							JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
					pane.addTab(tileset.getName(), paletteScrollPane);
				}
			}
			Dimension minSize = getMinimumSize();
			minSize.height = 0;
			pane.setMinimumSize(minSize);
		}
	}

	/**
	 * Adds tile selection listener. The listener will be notified when the user
	 * selects a tile.
	 */
	public void addTileSelectionListener(TileSelectionListener l) {
		for (TilePalettePanel panel : tilePanels) {
			panel.addTileSelectionListener(l);
		}
	}

	/**
	 * Removes tile selection listener.
	 */
	public void removeTileSelectionlistener(TileSelectionListener l) {
		for (TilePalettePanel panel : tilePanels) {
			panel.removeTileSelectionListener(l);
		}
	}

	/** informs the editor of the new tile. */
	public void tileSelected(TileSelectionEvent e) {
		mapEditor.setCurrentTiles(e.getTiles());
	}

	/** sets the tiles panes to the the ones from this map. */
	public void setMap(Map currentMap) {
		if (currentMap == null) {
			pane.removeAll();
		} else {
			setTilesets(currentMap.getTilesets());
		}
	}
}
