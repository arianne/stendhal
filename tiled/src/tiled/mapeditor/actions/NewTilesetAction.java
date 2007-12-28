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

import tiled.core.TileSet;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.dialog.NewTilesetDialog;

/**
 * Creates a new Tileset.
 * 
 * @author mtotz
 */
public class NewTilesetAction extends AbstractAction {
	private static final long serialVersionUID = -8888155680402257585L;

	private MapEditor mapEditor;

	public NewTilesetAction(MapEditor mapEditor) {
		super("New Tileset...");
		putValue(SHORT_DESCRIPTION, "Add a new internal tileset");
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent e) {
		if (mapEditor.currentMap != null) {
			NewTilesetDialog dialog = new NewTilesetDialog(mapEditor.appFrame, mapEditor.currentMap);
			TileSet newSet = dialog.create();
			if (newSet != null) {
				mapEditor.currentMap.addTileset(newSet);
			}
		}
	}
}
