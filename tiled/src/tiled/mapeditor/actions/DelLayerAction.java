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

import tiled.mapeditor.MapEditor;

/**
 * Deletes the current layer.
 * 
 * @author mtotz
 */
public class DelLayerAction extends AbstractAction {
	private static final long serialVersionUID = -6854510334788892219L;

	private MapEditor mapEditor;

	public DelLayerAction(MapEditor mapEditor) {
		super("Delete Layer");
		putValue(SHORT_DESCRIPTION, "Delete current layer");
		putValue(SMALL_ICON, MapEditor.loadIcon("resources/gnome-delete.png"));
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent e) {
		if (mapEditor.currentLayer >= 0) {
			mapEditor.currentMap.removeLayer(mapEditor.currentLayer);
			mapEditor.setCurrentLayer(mapEditor.currentLayer < 0 ? 0 : mapEditor.currentLayer);
		}
	}
}
