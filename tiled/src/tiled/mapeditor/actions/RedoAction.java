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
 * Redoes the last undo.
 * 
 * @author mtotz
 */
public class RedoAction extends AbstractAction {
	private static final long serialVersionUID = 2467790103953607697L;

	private MapEditor mapEditor;

	public RedoAction(MapEditor mapEditor) {
		super("Redo");
		putValue(SHORT_DESCRIPTION, "Redo one action");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Y"));
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent evt) {
		mapEditor.undoStack.redo();
		mapEditor.updateHistory();
		mapEditor.mapEditPanel.repaint();
	}
}