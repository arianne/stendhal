/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import games.stendhal.client.StendhalClient;
import games.stendhal.common.CollisionDetection;
import marauroa.common.game.RPAction;

class MapPanel extends JComponent {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -6471592733173102868L;

	/**
	 * The color of the background (palest grey).
	 */
	private static final Color COLOR_BACKGROUND = new Color(0.8f, 0.8f, 0.8f);
	/**
	 * The color of blocked areas (red).
	 */
	public static final Color COLOR_BLOCKED = new Color(1.0f, 0.0f, 0.0f);
	/**
	 * The color of protected areas (palest green).
	 */
	private static final Color COLOR_PROTECTION = new Color(202, 230, 202);
	/**
	 * The color of other players (white).
	 */

	/** width of the minimap. */
	private static final int MAP_WIDTH = 128;
	/** height of the minimap. */
	private static final int MAP_HEIGHT = 128;
	/** Minimum scale of the map; the minimum size of one tile in pixels */
	private static final int MINIMUM_SCALE = 2;

	private final StendhalClient client;
	private final MapPanelController controller;

	/**
	 * The player X coordinate.
	 */
	private double playerX;
	/**
	 * The player Y coordinate.
	 */
	private double playerY;
	/** X offset of the background image */
	private int xOffset;
	/** Y offset of the background image */
	private int yOffset;

	/**
	 * Maximum width of visible part of the map image. This should be accessed
	 * only in the event dispatch thread.
	 */
	private int width;
	/**
	 * Maximum height of visible part of the map image. This should be accessed
	 * only in the event dispatch thread.
	 */
	private int height;
	/**
	 * Scaling of the map image. Amount of pixels used for each map tile in each
	 * dimension. This should be accessed only in the event dispatch thread.
	 */
	private int scale;

	/**
	 * Map background. This should be accessed only in the event dispatch
	 * thread.
	 */
	private Image mapImage;

	/**
	 * Create a new MapPanel.
	 *
	 * @param controller
	 * @param client
	 */
	MapPanel(final MapPanelController controller, final StendhalClient client) {
		this.client = client;
		this.controller = controller;
		// black area outside the map
		setBackground(Color.black);
		updateSize(new Dimension(MAP_WIDTH, MAP_HEIGHT));
		setOpaque(true);

		// handle clicks for moving.
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				movePlayer(e.getPoint(), e.getClickCount() > 1);
			}
		});
	}

	@Override
	public void paintComponent(final Graphics g) {
		// Set this first, so that any changes made during the drawing will
		// flag the map changed
		controller.setNeedsRefresh(false);

		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		// The rest of the things should be drawn inside the actual map area
		g.clipRect(0, 0, width, height);
		// also choose the origin so that we can simply draw to the
		// normal coordinates
		g.translate(-xOffset, -yOffset);

		drawMap(g);
		drawEntities(g);

		g.dispose();
	}

	/**
	 * Draw the entities on the map.
	 *
	 * @param g The graphics context
	 */
	private void drawEntities(final Graphics g) {
		for (final MapObject object : controller.mapObjects.values()) {
			object.draw(g, scale);
		}
	}

	/**
	 * Set the dimensions of the component. This must be called from the event
	 * dispatch thread.
	 *
	 * @param dim the new dimensions
	 */
	private void updateSize(final Dimension dim) {
		setMaximumSize(dim);
		setMinimumSize(new Dimension(0, dim.height));
		setPreferredSize(dim);
		// the user may have hidden the component partly or entirely
		setSize(getWidth(), dim.height);

		controller.setNeedsRefresh(true);
		revalidate();
	}

	/**
	 * Draw the map background. This must be called from the event dispatch
	 * thread.
	 *
	 * @param g The graphics context
	 */
	private void drawMap(final Graphics g) {
		g.drawImage(mapImage, 0, 0, null);
	}

	/**
	 * The player's position changed.
	 *
	 * @param x
	 *            The X coordinate (in world units).
	 * @param y
	 *            The Y coordinate (in world units).
	 */
	void positionChanged(final double x, final double y) {
		playerX = x;
		playerY = y;

		updateView();
	}

	@Override
	public void paintImmediately(int x, int y, int w, int h) {
		/*
		 * Try to keep the view screen while the user is switching maps.
		 *
		 * NOTE: Relies on the repaint() requests to eventually come to
		 * this, so if swing internals change some time in the future,
		 * a new solution may be needed.
		 */
		if (StendhalClient.get().tryAcquireDrawingSemaphore()) {
			try {
				super.paintImmediately(x, y, w, h);
			} finally {
				StendhalClient.get().releaseDrawingSemaphore();
			}
		}
	}

	/**
	 * Update the view pan. This should be done when the map size or player
	 * position changes. This must be called from the event dispatch thread.
	 */
	private void updateView() {
		xOffset = 0;
		yOffset = 0;

		if (mapImage == null) {
			return;
		}

		final int imageWidth = mapImage.getWidth(null);
		final int imageHeight = mapImage.getHeight(null);

		final int xpos = (int) ((playerX * scale) + 0.5) - width / 2;
		final int ypos = (int) ((playerY * scale) + 0.5) - width / 2;

		if (imageWidth > width) {
			// need to pan width
			if ((xpos + width) > imageWidth) {
				// x is at the screen border
				xOffset = imageWidth - width;
			} else if (xpos > 0) {
				xOffset = xpos;
			}
		}

		if (imageHeight > height) {
			// need to pan height
			if ((ypos + height) > imageHeight) {
				// y is at the screen border
				yOffset = imageHeight - height;
			} else if (ypos > 0) {
				yOffset = ypos;
			}
		}
	}

	/**
	 * Update the map with new data. This method can be called outside the
	 * event dispatch thread.
	 *
	 * @param cd
	 *            The collision map.
	 * @param pd
	 *      	  The protection map.
	 */
	void update(final CollisionDetection cd, final CollisionDetection pd) {
		// calculate the size and scale of the map
		final int mapWidth = cd.getWidth();
		final int mapHeight = cd.getHeight();
		final int scale = Math.max(MINIMUM_SCALE, Math.min(MAP_HEIGHT / mapHeight, MAP_WIDTH / mapWidth));
		final int width = Math.min(MAP_WIDTH, mapWidth * scale);
		final int height = Math.min(MAP_HEIGHT, mapHeight * scale);

		// this.getGraphicsConfiguration is not thread safe
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		// create the map image, and fill it with the wanted details
		final Image newMapImage  = gc.createCompatibleImage(mapWidth * scale, mapHeight * scale);
		final Graphics g = newMapImage.getGraphics();
		g.setColor(COLOR_BACKGROUND);
		g.fillRect(0, 0, mapWidth * scale, mapHeight * scale);

		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				if (cd.collides(x, y)) {
					g.setColor(COLOR_BLOCKED);
					g.fillRect(x * scale, y * scale, scale, scale);
				} else if (pd != null && pd.collides(x, y)) {
					// draw protection only if there is no collision to draw
					g.setColor(COLOR_PROTECTION);
					g.fillRect(x * scale, y * scale, scale, scale);
				}
			}
		}
		g.dispose();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Swap the image only after the new one is ready
				mapImage = newMapImage;
				// Update the other data
				MapPanel.this.scale = scale;
				MapPanel.this.width = width;
				MapPanel.this.height = height;
				updateSize(new Dimension(MAP_WIDTH, height));
				updateView();
			}
		});
		repaint();
	}

	/**
	 * Tell the player to move to point p
	 *
	 * @param p the point
	 * @param doubleClick <code>true</code> if the movement was requested with
	 * 	a double click, <code>false</code> otherwise
	 */
	private void movePlayer(final Point p, boolean doubleClick) {
		// Ignore clicks to the title area
		if (p.y <= height) {
			final RPAction action = new RPAction();
			action.put("type", "moveto");
			action.put("x", (p.x + xOffset) / scale);
			action.put("y", (p.y + yOffset) / scale);
			if (doubleClick) {
				action.put("double_click", "");
			}
			client.send(action);
		}
	}
}
