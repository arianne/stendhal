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
 * Moves the current layer up.
 * 
 * @author mtotz
 */
public class MoveLayerUpAction extends AbstractAction {
	private static final long serialVersionUID = -6847888144371163825L;

	private MapEditor mapEditor;

	public MoveLayerUpAction(MapEditor mapEditor) {
		super("Move Layer Up");
		putValue(SHORT_DESCRIPTION, "Move layer up one in layer stack");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("shift PAGE_UP"));
		putValue(SMALL_ICON, MapEditor.loadIcon("resources/gnome-up.png"));
		this.mapEditor = mapEditor;
	}

	public void actionPerformed(ActionEvent e) {
		if (mapEditor.currentLayer >= 0) {
			try {
				mapEditor.currentMap.swapLayerUp(mapEditor.currentLayer);
				mapEditor.setCurrentLayer(mapEditor.currentLayer + 1);
			} catch (Exception ex) {
				System.out.println(ex.toString());
			}
		}
	}
}
