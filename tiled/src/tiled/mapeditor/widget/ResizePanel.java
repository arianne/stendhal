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
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import tiled.core.Map;
import tiled.view.old.MapView;

/**
 * A special widget designed as an aid for resizing the map. Based on a similar
 * widget used by the GIMP when resizing the image.
 */
public class ResizePanel extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -155382559935793032L;
	private MapView inner;
	private Map currentMap;
	private Dimension oldDim;
	private int offsetX, offsetY;
	private Point startPress;
	private double zoom;

	public ResizePanel() {
		super();
		setLayout(new OverlayLayout(this));
		setBorder(BorderFactory.createLoweredBevelBorder());
	}

	public ResizePanel(Map map) {
		this();
		inner = MapView.createViewforMap(map);
		inner.addMouseListener(this);
		inner.addMouseMotionListener(this);
		add(inner);
		zoom = 0.1;
		inner.setZoom(zoom);
		currentMap = map;
		Dimension old = inner.getPreferredSize();
		// TODO: get smaller dimension, zoom based on that...

		oldDim = old;
		setSize(old);
	}

	public ResizePanel(Dimension size, Map map) {
		this(map);
		oldDim = size;
		setSize(size);
	}

	public void moveMap(int x, int y) {
		// snap!
		inner.setLocation((int) (x * (currentMap.getTileWidth() * zoom)),
				(int) (y * (currentMap.getTileHeight() * zoom)));
	}

	public void setNewDimensions(Dimension n) {
	}

	@Override
	public Dimension getPreferredSize() {
		return oldDim;
	}

	public double getZoom() {
		return zoom;
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		int newOffsetX = offsetX + (e.getX() - startPress.x);
		int newOffsetY = offsetY + (e.getY() - startPress.y);

		newOffsetX /= (currentMap.getTileWidth() * zoom);
		newOffsetY /= (currentMap.getTileHeight() * zoom);

		if (newOffsetX != offsetX) {
			firePropertyChange("offsetX", offsetX, newOffsetX);
			offsetX = newOffsetX;
		}

		if (newOffsetY != offsetY) {
			firePropertyChange("offsetY", offsetY, newOffsetY);
			offsetY = newOffsetY;
		}
	}

	public void mousePressed(MouseEvent e) {
		startPress = e.getPoint();
	}

	public void mouseReleased(MouseEvent e) {
		startPress = null;
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}
}
