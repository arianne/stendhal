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
 * Pastes the clipboard to the current layer/mouse position.
 * 
 * @author mtotz
 */
public class PasteAction extends AbstractAction {
	private static final long serialVersionUID = -9094834729794547379L;

	private MapEditor mapEditor;

	public PasteAction(MapEditor mapEditor) {
		super("Paste");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
		putValue(SHORT_DESCRIPTION, "Paste");
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent evt) {
		if (mapEditor.currentMap != null && mapEditor.clipboardLayer != null) {
			// List<MapLayer> layersBefore =
			// mapEditor.currentMap.getLayerList();
			// MapLayer ml =
			// mapEditor.createLayerCopy(mapEditor.clipboardLayer);
			// ml.setName("Layer " + mapEditor.currentMap.getTotalLayers());
			// mapEditor.currentMap.addLayer(ml);
			// mapEditor.undoSupport.postEdit(new
			// MapLayerStateEdit(mapEditor.currentMap,
			// layersBefore,mapEditor.currentMap.getLayerList(), "Paste
			// Selection"));
		}
	}
}