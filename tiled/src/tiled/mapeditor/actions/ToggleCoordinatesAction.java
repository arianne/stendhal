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
 */

package tiled.mapeditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JOptionPane;

import tiled.mapeditor.MapEditor;

/**
 * Toggles viewing coordinates.
 *
 * @author Martin Fuchs
 */
public class ToggleCoordinatesAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private MapEditor mapEditor;

	public ToggleCoordinatesAction(MapEditor mapEditor) {
		super("Show Coordinates");
	//	putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
		putValue(SHORT_DESCRIPTION, "Toggle coordinates");
		putValue(LONG_DESCRIPTION, "Toggle tile coordinates");
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent e) {
		JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
		if (item != null) {
			//TODO
			JOptionPane.showMessageDialog(mapEditor.getAppFrame(), "Displaying coordinates is not yet implemented.");
		}
	}

}
