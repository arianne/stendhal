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

package tiled.mapeditor.widget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import tiled.core.TileGroup;
import tiled.mapeditor.MapEditor;

/**
 * @author mtotz
 * 
 */
public class TileGroupButton extends JToggleButton {
	private static final long serialVersionUID = -5257961848226829114L;
	/** width of the tile group. */
	private int width;
	/** width of the tile group. */
	private int height;
	/** the tilegroup. */
	private TileGroup tileGroup;

	/** */
	public TileGroupButton(MapEditor mapEditor, Action action, TileGroup tileGroup) {
		super(action);
		setBorderPainted(false);
		BufferedImage image = mapEditor.mapView.drawTileGroup(tileGroup);

		width = image.getWidth();
		height = image.getHeight();

		Graphics2D g = image.createGraphics();
		g.setColor(Color.WHITE);
		g.drawRect(0, 0, width - 1, height - 1);
		g.drawRect(1, 1, width - 3, height - 3);

		setIcon(new ImageIcon(image));
		setText(null);
		this.tileGroup = tileGroup;
	}

	/** returns the tilegroup for this button. */
	public TileGroup getTileGroup() {
		return tileGroup;
	}

	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
}
