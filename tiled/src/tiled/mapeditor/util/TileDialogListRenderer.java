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

package tiled.mapeditor.util;

import java.awt.Component;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.DefaultListCellRenderer;

import tiled.core.*;

public class TileDialogListRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = -3416846019183710768L;
	private double zoom = 1;

	public TileDialogListRenderer() {
		setOpaque(true);
	}

	public TileDialogListRenderer(double zoom) {
		this();
		this.zoom = zoom;
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		Tile tile = (Tile) value;

		if (tile != null) {
			Image scaledImage = tile.getScaledImage(zoom);
			if (scaledImage != null) {
				setIcon(new ImageIcon(scaledImage));
			}
			setText("Tile " + tile.getId());
		} else {
			setIcon(null);
			setText("No tile?");
		}

		return this;
	}
}
