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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import tiled.view.MapView;

/**
 * This is the minimap panel. TODO: There are some ugly visual artefacts when
 * scrolling the minimap. This is due to lazy repaints/dirty rectangle handling
 * 
 * @author mtotz
 */
public class MiniMapViewer extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -1243207988158851225L;

	public static final int MAX_HEIGHT = 150;

	private MapView mapView;
	private JScrollPane mapScrollPane;

	/** last viewpoint in the map editing panel. */
	private Point lastViewPoint;
	/** need this to prevent recursive painting. */
	private boolean paintingInProgress;

	public MiniMapViewer() {
		setSize(MAX_HEIGHT, MAX_HEIGHT);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void setView(MapView view) {
		mapView = view;
		revalidate();
	}

	@Override
	public Dimension getPreferredSize() {
		if (mapView == null) {
			return new Dimension(100, 100);
		}
		Image image = mapView.getMinimap();
		if (image == null) {
			return new Dimension(100, 100);
		}
		return new Dimension(image.getWidth(null), image.getHeight(null));
	}

	public void setMainPanel(JScrollPane main) {
		mapScrollPane = main;
	}

	/** clears the background. */
	private void clearBackground(Graphics g) {
		Rectangle clip = g.getClipBounds();
		g.setColor(Color.BLACK);
		g.fillRect(clip.x, clip.y, clip.width, clip.height);
	}

	@Override
	public void paintComponent(Graphics g) {
		if (paintingInProgress) {
			// recursive painting caused by scrollRectToVisible(..)
			return;
		}
		paintingInProgress = true;
		clearBackground(g);
		if (mapView == null || mapView.getMinimap() == null) {
			paintingInProgress = false;
			return;
		}

		((Graphics2D) g).drawImage(mapView.getMinimap(), 0, 0, null);

		if (mapScrollPane != null) {
			g.setColor(Color.yellow);
			Point viewPoint = mapScrollPane.getViewport().getViewPosition();
			Dimension viewSize = mapScrollPane.getViewport().getExtentSize();

			double scale = mapView.getMinimapScale() / mapView.getScale();

			if (viewPoint != null && viewSize != null) {
				Rectangle rect = new Rectangle((int) ((viewPoint.x - 1) * scale), (int) ((viewPoint.y - 1) * scale),
						(int) ((viewSize.width - 1) * scale), (int) ((viewSize.height - 1) * scale));

				// only update scrolling when the main viewport has changed
				g.drawRect(rect.x, rect.y, rect.width, rect.height);
				if (!viewPoint.equals(lastViewPoint)) {
					scrollRectToVisible(rect);
					repaint();
				}
				lastViewPoint = viewPoint;
			}
		}
		paintingInProgress = false;
	}

	/** scrolls the viewport of the map edit panel to this point. */
	private void scrollMainViewport(Point p) {
		if (p == null) {
			return;
		}

		if (mapView != null && mapScrollPane != null) {
			double scale = mapView.getScale() / mapView.getMinimapScale();

			p.x *= scale;
			p.y *= scale;

			Dimension size = mapScrollPane.getViewport().getExtentSize();
			Point p2 = new Point(p.x - (size.width / 2), p.y - (size.height / 2));

			mapScrollPane.getViewport().setViewPosition(p2);
			repaint();
		}

	}

	public void mouseClicked(MouseEvent e) {
		scrollMainViewport(e.getPoint());

	}

	public void mouseDragged(MouseEvent e) {
		scrollMainViewport(e.getPoint());
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}
}
