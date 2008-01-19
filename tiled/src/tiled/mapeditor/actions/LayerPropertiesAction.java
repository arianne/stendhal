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

import tiled.core.MapLayer;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.dialog.PropertiesDialog;

/**
 * Sets the properties for the current layer.
 * 
 * @author mtotz
 */
public class LayerPropertiesAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private MapEditor mapEditor;

	public LayerPropertiesAction(MapEditor mapEditor) {
		super("Layer Properties");
		putValue(SHORT_DESCRIPTION, "Current layer properties");
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent e) {
		MapLayer layer = mapEditor.getCurrentLayer();
		PropertiesDialog lpd = new PropertiesDialog(mapEditor.appFrame, layer.getProperties());
		lpd.setTitle(layer.getName() + " Properties");
		lpd.getProps();
	}
}
