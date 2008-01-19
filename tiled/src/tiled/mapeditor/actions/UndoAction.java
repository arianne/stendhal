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
 * Undoes the last action when possible.
 * 
 * @author mtotz
 */
public class UndoAction extends AbstractAction {
	private static final long serialVersionUID = -1129586889816507546L;

	private MapEditor mapEditor;

	public UndoAction(MapEditor mapEditor) {
		super("Undo");
		putValue(SHORT_DESCRIPTION, "Undo one action");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Z"));
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent evt) {
		mapEditor.undoStack.undo();
		mapEditor.updateHistory();
		mapEditor.mapEditPanel.repaint();
	}
}