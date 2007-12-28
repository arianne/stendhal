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

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;

/**
 * Called when current the selection is canceled.
 * 
 * @author mtotz
 */
public class CancelSelectionAction extends AbstractAction {
	private static final long serialVersionUID = -6217788914300686640L;

	private MapEditor mapEditor;

	public CancelSelectionAction(MapEditor mapEditor) {
		super("None");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift A"));
		putValue(SHORT_DESCRIPTION, "Cancel selection");
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent e) {
		if (mapEditor.currentMap != null) {
			mapEditor.setSelectedTiles(new ArrayList<Point>());
		}
	}
}