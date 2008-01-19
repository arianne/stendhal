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
import javax.swing.JCheckBoxMenuItem;

import tiled.mapeditor.MapEditor;

/**
 * Action for switching between the treelike and tabbed tileset chooser.
 * 
 * @author mtotz
 */
public class TreeTilesetChooserAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private MapEditor mapEditor;

	public TreeTilesetChooserAction(MapEditor mapEditor) {
		super("Show as tree");
		putValue(SHORT_DESCRIPTION, "Shows the Tilesets as tree");
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent e) {
		JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
		if (item != null) {
			mapEditor.toggleTreeTilesetChooser(item.isSelected());
		}
	}

}
