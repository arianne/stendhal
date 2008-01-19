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

package tiled.mapeditor.widget;

import java.awt.*;

import javax.swing.JPanel;

public class ImageViewPanel extends JPanel {

	private static final long serialVersionUID = 8789559203794768370L;
	private Image image;

	public ImageViewPanel() {
		super();
	}

	public ImageViewPanel(Image i) {
		this();
		image = i;
	}

	public Dimension getPreferredSize() {
		return new Dimension(150, 150);
	}

	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
}
