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
 * Copies the current selection to the clipboard.
 * 
 * @author mtotz
 */
public class CopyAction extends AbstractAction {
	private static final long serialVersionUID = -7093838522430390018L;

	private MapEditor mapEditor;

	public CopyAction(MapEditor mapEditor) {
		super("Copy");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
		putValue(SHORT_DESCRIPTION, "Copy");
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent evt) {
	}
}