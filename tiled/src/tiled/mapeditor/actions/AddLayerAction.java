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
 * Called when a new layer is added to the map.
 * 
 * @author mtotz
 */
public class AddLayerAction extends AbstractAction {
	private static final long serialVersionUID = -9136823833761490842L;

	private MapEditor mapEditor;

	public AddLayerAction(MapEditor mapEditor) {
		super("Add Layer");
		putValue(SHORT_DESCRIPTION, "Add a Layer");
		putValue(SMALL_ICON, MapEditor.loadIcon("resources/gnome-new.png"));
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent e) {
		mapEditor.currentMap.addLayer();
		mapEditor.setCurrentLayer(mapEditor.currentMap.getTotalLayers() - 1);
	}

}
