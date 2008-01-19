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
 * Saves the map.
 * 
 * @author mtotz
 */
public class SaveMapAction extends AbstractAction {
	private static final long serialVersionUID = -5617105173971065571L;

	private boolean withDialog;
	private MapEditor mapEditor;

	/**
	 * the action will popup a filechooser dialog when <i>withDialog</i> is
	 * true (Save as...) .
	 */
	public SaveMapAction(MapEditor mapEditor, boolean withDialog) {
		super("Save" + (withDialog ? " as..." : ""));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control " + (withDialog ? "shift " : "") + "S"));
		putValue(SHORT_DESCRIPTION, "Saves this map");
		this.mapEditor = mapEditor;
		this.withDialog = withDialog;
	}

	/** constructor for the action manager. */
	public SaveMapAction(MapEditor mapEditor, Object[] params) {
		// note: no checks. the only parameter should be a boolean :)
		this(mapEditor, ((Boolean) params[0]).booleanValue());
	}

	public void actionPerformed(ActionEvent e) {
		mapEditor.saveMap(withDialog);
	}

}
