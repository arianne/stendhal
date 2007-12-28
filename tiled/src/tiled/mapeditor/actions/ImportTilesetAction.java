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

package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import tiled.core.TileSet;
import tiled.io.MapHelper;
import tiled.io.MapReader;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.util.TiledFileFilter;

/**
 * Imports a new tileset.
 * 
 * @author mtotz
 */
public class ImportTilesetAction extends AbstractAction {
	private static final long serialVersionUID = 1123982825353327389L;

	private MapEditor mapEditor;

	public ImportTilesetAction(MapEditor mapEditor) {
		super("Import Tileset...");
		putValue(SHORT_DESCRIPTION, "Import an external tileset");
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent event) {
		if (mapEditor.currentMap != null) {
			JFileChooser ch = new JFileChooser(mapEditor.currentMap.getFilename());
			MapReader[] readers = mapEditor.pluginLoader.getReaders();
			for (int i = 0; i < readers.length; i++) {
				try {
					ch.addChoosableFileFilter(new TiledFileFilter(readers[i].getFilter(), readers[i].getName()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			ch.addChoosableFileFilter(new TiledFileFilter(TiledFileFilter.FILTER_TSX));

			int ret = ch.showOpenDialog(mapEditor.appFrame);
			if (ret == JFileChooser.APPROVE_OPTION) {
				String filename = ch.getSelectedFile().getAbsolutePath();
				try {
					TileSet set = MapHelper.loadTileset(filename);
					mapEditor.currentMap.addTileset(set);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
