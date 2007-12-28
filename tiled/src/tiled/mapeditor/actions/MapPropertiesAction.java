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
import tiled.mapeditor.dialog.PropertiesDialog;

/**
 * Shows the map properties.
 * 
 * @author mtotz
 */
public class MapPropertiesAction extends AbstractAction {
	private static final long serialVersionUID = 248911712933902868L;

	private MapEditor mapEditor;

	public MapPropertiesAction(MapEditor mapEditor) {
		super("Properties");
		putValue(SHORT_DESCRIPTION, "Map properties");
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent e) {
		PropertiesDialog pd = new PropertiesDialog(mapEditor.appFrame, mapEditor.currentMap.getProperties());
		pd.setTitle("Map Properties");
		pd.getProps();
	}

}
