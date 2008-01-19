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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import tiled.mapeditor.MapEditor;
import tiled.mapeditor.dialog.TilesetManager;

/**
 * Opens the tileset manager.
 * 
 * @author mtotz
 */
public class TilesetManagerAction extends AbstractAction {
	private static final long serialVersionUID = -4785910554628085157L;

	private MapEditor mapEditor;

	public TilesetManagerAction(MapEditor mapEditor) {
		super("Tileset Manager");
		putValue(SHORT_DESCRIPTION, "Open the tileset manager");
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent e) {
		if (mapEditor.currentMap != null) {
			TilesetManager manager = new TilesetManager(mapEditor.appFrame, mapEditor.currentMap);
			manager.setVisible(true);
		}
	}

}
