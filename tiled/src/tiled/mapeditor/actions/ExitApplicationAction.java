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
 * Exits the application.
 * 
 * @author mtotz
 */
public class ExitApplicationAction extends AbstractAction {
	private static final long serialVersionUID = -8126594339805319303L;

	private MapEditor mapEditor;

	public ExitApplicationAction(MapEditor mapEditor) {
		super("Exit");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Q"));
		putValue(SHORT_DESCRIPTION, "Exit the map editor");
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent e) {
		mapEditor.exit();
	}

}
