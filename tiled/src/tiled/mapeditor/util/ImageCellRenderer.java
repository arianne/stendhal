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

public class ImageCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 8131656400219422438L;

	public ImageCellRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		Image img = (Image) value;
		if (img != null) {
			setIcon(new ImageIcon(img));
		} else {
			setIcon(null);
		}
		setText(null);

		return this;
	}
}
