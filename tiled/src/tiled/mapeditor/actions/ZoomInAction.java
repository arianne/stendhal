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
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

/**
 * Increases the map zoom.
 * 
 * @author mtotz
 */
public class ZoomInAction extends AbstractAction {
	private static final long serialVersionUID = -5253002744432344462L;

	private MapEditor mapEditor;

	public ZoomInAction(MapEditor mapEditor) {
		super("Zoom In");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control EQUALS"));
		putValue(SHORT_DESCRIPTION, "Zoom in one level");
		putValue(SMALL_ICON, MapEditor.loadIcon("resources/gnome-zoom-in.png"));
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent evt) {
		if (mapEditor.currentMap != null) {
			mapEditor.mapView.zoomIn();
			mapEditor.statusBar.setZoom(mapEditor.mapView.getScale());
			mapEditor.mapEditPanel.notifyZoom();
		}
	}
}