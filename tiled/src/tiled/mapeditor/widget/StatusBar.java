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

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import tiled.core.Map;

/**
 * The status bar.
 * 
 * @author mtotz
 */
public class StatusBar extends JPanel {
	private static final long serialVersionUID = -2827624703708560572L;

	/** shows the current zoom. */
	private JLabel zoomLabel;
	/** shows the current mouse position. */
	private JLabel tileCoordsLabel;

	/**
	 * 
	 */
	public StatusBar() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		zoomLabel = new JLabel("100%");
		zoomLabel.setPreferredSize(zoomLabel.getPreferredSize());
		tileCoordsLabel = new JLabel(" ", SwingConstants.CENTER);

		setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		JPanel largePart = new JPanel();

		add(largePart);
		add(tileCoordsLabel);
		add(Box.createRigidArea(new Dimension(20, 0)));
		add(zoomLabel);
	}

	/** removes all text from the labels. */
	public void clearLabels() {
		tileCoordsLabel.setPreferredSize(null);
		tileCoordsLabel.setText(" ");
		zoomLabel.setText(" ");
	}

	/** updates the labes to reflect the current map settings. */
	public void setMap(Map map) {
		tileCoordsLabel.setText("" + (map.getWidth() - 1) + ", " + (map.getHeight() - 1));
		tileCoordsLabel.setPreferredSize(null);
		Dimension size = tileCoordsLabel.getPreferredSize();
		tileCoordsLabel.setText(" ");
		tileCoordsLabel.setMinimumSize(size);
		tileCoordsLabel.setPreferredSize(size);
	}

	/** updates the zoom labels text. */
	public void setZoom(double zoom) {
		zoomLabel.setText("" + ((int) (zoom * 100)) + "%");
	}

}
