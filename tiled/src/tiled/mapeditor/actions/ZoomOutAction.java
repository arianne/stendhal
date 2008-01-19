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
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

/**
 * Decreases the map zoom level.
 * 
 * @author mtotz
 */
public class ZoomOutAction extends AbstractAction {
	private static final long serialVersionUID = 4963537857700059134L;

	private MapEditor mapEditor;

	public ZoomOutAction(MapEditor mapEditor) {
		super("Zoom Out");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control MINUS"));
		putValue(SHORT_DESCRIPTION, "Zoom out one level");
		putValue(SMALL_ICON, MapEditor.loadIcon("resources/gnome-zoom-out.png"));
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent evt) {
		if (mapEditor.currentMap != null) {
			mapEditor.mapView.zoomOut();
			mapEditor.statusBar.setZoom(mapEditor.mapView.getScale());
			mapEditor.mapEditPanel.notifyZoom();
		}
	}
}