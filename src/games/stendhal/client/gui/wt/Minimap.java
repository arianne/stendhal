/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
/*
 * Minimap.java
 * Created on 16. Oktober 2005, 13:34
 */
package games.stendhal.client.gui.wt;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.Creature;
import games.stendhal.client.entity.DomesticAnimal;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.NPC;
import games.stendhal.client.entity.Player;
import games.stendhal.client.entity.Portal;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.entity.User;
import games.stendhal.client.events.PositionChangeListener;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.common.CollisionDetection;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

/**
 * The minimap.
 * 
 * @author mtotz
 */

public class Minimap extends WtPanel implements PositionChangeListener {
	/**
	 * The color of the background.
	 */
	private static Color COLOR_BACKGROUND = new Color(0.8f, 0.8f, 0.8f);

	/**
	 * The color of blocked areas.
	 */
	private static final Color COLOR_BLOCKED = new Color(1.0f, 0.0f, 0.0f);

	/**
	 * The color of a general entity.
	 */
	private static final Color COLOR_ENTITY = new Color(200, 255, 200);

	/**
	 * The color of the "N" text.
	 */
	private static final Color COLOR_NORTH = new Color(0.0f, 0.0f, 0.0f);

	/** width of the minimap. */
	private static final int MINIMAP_WIDTH = 129;

	/** height of the minimap. */
	private static final int MINIMAP_HEIGHT = 129;

	/** minimum scale of the minimap. */
	private static final int MINIMAP_MINIMUM_SCALE = 2;

	/** Enable X-ray vision (aka Superman) minimap? */
	private static final boolean mininps = (System.getProperty("stendhal.superman") != null);

	/** scale of map. */
	private int scale;

	/** width of (scaled) minimap .*/
	private int width;

	/** height of (scaled) minimap .*/
	private int height;

	/**
	 * The view X offset.
	 */
	private int panx;

	/**
	 * The view Y offset.
	 */
	private int pany;

	/**
	 * The player X coordinate.
	 */
	private double playerX;

	/**
	 * The player Y coordinate.
	 */
	private double playerY;

	/** minimap image. */
	private BufferedImage image;

	private StendhalClient client;

	/**
	 * PATHFIND.
	 */

	private int nodo_actual;

	private Pathfind pathfind;

	private CollisionDetection collisiondetection;

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(Minimap.class);

	/** Creates a new instance of Minimap. */
	public Minimap(StendhalClient client) {
		super("minimap", 0, 0, 100, 100);

		this.client = client;

		setTitleBar(true);
		setFrame(true);
		setMovable(true);
		setMinimizeable(true);
		if (System.getProperty("stendhal.transparency") != null) {
			COLOR_BACKGROUND = new Color(0.8f, 0.8f, 0.8f);
			setTransparency(0.5f);
		}

		// INIT PATHFIND
		pathfind = new Pathfind();

		// Show nothing until there is map data
		resizeToFitClientArea(100, 0);
	}

	/**
	 * Update the map with new data.
	 * 
	 * @param cd
	 *            The collision map.
	 * @param gc
	 *            A graphics configuration.
	 * @param zone
	 *            The zone name.
	 */
	public void update(CollisionDetection cd, GraphicsConfiguration gc, String zone) {
		setTitletext(zone);

		// FOR PATHFINDING THING
		collisiondetection = cd;
		pathfind.ClearPath();
		nodo_actual = 0;

		// calculate size and scale
		int w = cd.getWidth();
		int h = cd.getHeight();

		// calculate scale
		scale = MINIMAP_MINIMUM_SCALE;
		while ((w * (scale + 1) < MINIMAP_WIDTH)
				&& (h * (scale + 1) < MINIMAP_HEIGHT)) {
			scale++;
		}

		// calculate size of map
		width = (w * scale < MINIMAP_WIDTH) ? w * scale : MINIMAP_WIDTH;
		height = (h * scale < MINIMAP_HEIGHT) ? h * scale : MINIMAP_HEIGHT;

		// create the image for the minimap
		image = gc.createCompatibleImage(w * scale, h * scale);

		Graphics2D mapgrapics = image.createGraphics();

		mapgrapics.setColor(COLOR_BACKGROUND);
		mapgrapics.fillRect(0, 0, w * scale, h * scale);

		mapgrapics.setColor(COLOR_BLOCKED);

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				if (!cd.walkable(x, y)) {
					mapgrapics.fillRect(x * scale, y * scale, scale, scale);
				}
			}
		}

		mapgrapics.dispose();

		// now resize the panel to match the size of the map
		resizeToFitClientArea(width, height + 2);

		updateView();
	}

	/** we're using the window manager. */
	@Override
	protected boolean useWindowManager() {
		return true;
	}

	/**
	 * Update the view pan. This should be done when the map size or player
	 * position changes.
	 */
	private void updateView() {
		panx = 0;
		pany = 0;

		if (image == null) {
			return;
		}

		int w = image.getWidth();
		int h = image.getHeight();

		int xpos = (int) ((playerX * scale) + 0.5) - width / 2;
		int ypos = (int) ((playerY * scale) + 0.5) - width / 2;

		if (w > width) {
			// need to pan width
			if ((xpos + width) > w) {
				// x is at the screen border
				panx = w - width;
			} else if (xpos > 0) {
				panx = xpos;
			}
		}

		if (h > height) {
			// need to pan height
			if ((ypos + height) > h) {
				// y is at the screen border
				pany = h - height;
			} else if (ypos > 0) {
				pany = ypos;
			}
		}
	}

	public void update_pathfind() {
		if (nodo_actual != 0) {
			pathfind.PathJumpToNode(nodo_actual);
			int manhatan = (int) ((Math.abs(playerX - pathfind.NodeGetX()) + Math.abs(playerY
					- pathfind.NodeGetY())));

			if (manhatan < 6) {

				pathfind.PathJumpNode();

				if (logger.isDebugEnabled()) {
					logger.debug("Pathfind: To waypoint: "
							+ pathfind.NodeGetX() + " " + pathfind.NodeGetY());
				}
				RPAction action = new RPAction();
				action.put("type", "moveto");
				action.put("x", pathfind.NodeGetX());
				action.put("y", pathfind.NodeGetY());

				client.send(action);

				nodo_actual = pathfind.final_path_index;

				if (pathfind.ReachedGoal()) {
					pathfind.ClearPath();
				}
			}

		}

	}

	/**
	 * Draws the minimap.
	 * 
	 * @param g
	 *            graphics object for the game main window
	 */
	@Override
	protected void drawContent(Graphics2D g) {
		super.drawContent(g);

		if (image == null) {
			return;
		}

		Graphics vg = g.create();

		// draw minimap
		vg.translate(-panx, -pany);
		vg.drawImage(image, 0, 0, null);

		int level = User.getPlayerLevel();

		// display a "N" to show north direction
		if (level < 10) {
    		vg.setColor(COLOR_NORTH);
    		vg.setFont(new Font("SansSerif", Font.PLAIN, 9));
    		FontMetrics metrics = vg.getFontMetrics();
    		Rectangle2D rect = metrics.getStringBounds("N", 0, 0, g);
    		vg.drawString("N", panx + (width - (int) rect.getWidth()) / 2, pany + (int) rect.getHeight());
		}

//		PATHFIND ---
//		pathfind.Reinice();
//		while (!pathfind.ReachedGoal()) {
//			pathfind.PathNextNode();
//			vg.fillRect(pathfind.NodeGetX() * scale, pathfind.NodeGetY() scale, scale, scale);
//		}
//		pathfind.Reinice();
//
//		 while (!pathfind.ReachedGoal()) {
//			 pathfind.PathJumpNode();
//			 vg.setColor(Color.CYAN);
//			 vg.fillRect(pathfind.NodeGetX() * scale, pathfind.NodeGetY() scale, scale, scale);
//		}
//		pathfind.Reinice();

		// --------------------------------------
		// Draw on ground entities
		for (Entity entity : client.getGameObjects()) {
			if (!entity.isOnGround()) {
				continue;
			}

			if (entity instanceof Player) {
				Player player = (Player) entity;

				if (!player.isGhostMode()) {
					drawPlayer(vg, player, Color.WHITE);
				} else if (User.isAdmin()) {
					drawPlayer(vg, player, Color.GRAY);
				}
			} else if (entity instanceof Portal) {
				Portal portal = (Portal) entity;

				if (!portal.isHidden()) {
					drawEntity(vg, entity, Color.WHITE, Color.BLACK);
				}
			} else if (mininps && User.isAdmin()) {
				// Enabled with -Dstendhal.superman=x.

				if (entity instanceof RPEntity) {
					drawRPEntity(vg, (RPEntity) entity);
				} else {
					drawEntity(vg, entity, COLOR_ENTITY);
				}
			}
		}

		drawUser(vg);

		vg.dispose();
	}

	/**
	 * Draws the User.
	 * 
	 * @param vg
	 *            graphics context
	 */
	private void drawUser(Graphics vg) {
		User user = User.get();

		if (user != null) {
			drawPlayer(vg, user, Color.BLUE);
		}
	}

	/**
	 * Draws an RPEntity on the map.
	 * 
	 * @param g
	 *            Graphics
	 * @param entity
	 *            The entity to be drawn
	 */
	protected void drawRPEntity(final Graphics g, final RPEntity entity) {
		if (entity instanceof DomesticAnimal) {
			drawEntity(g, entity, Color.ORANGE);
		} else if (entity instanceof Creature) {
			drawEntity(g, entity, Color.YELLOW);
		} else if (entity instanceof NPC) {
			drawEntity(g, entity, Color.BLUE);
		}
	}

	/**
	 * Draw an entity on the map as a colored rectangle.
	 * 
	 * @param g
	 *            graphics
	 * @param entity
	 *            the Entity to be drawn
	 * @param color
	 *            the Color to be used
	 */
	protected void drawEntity(final Graphics g, final Entity entity,
			final Color color) {
		drawEntity(g, entity, color, null);
	}

	/**
	 * Draw an entity on the map as a colored rectangle, with an optional border
	 * (for non 1x1 entities).
	 * 
	 * @param g
	 *            The graphics context.
	 * @param entity
	 *            The Entity to be drawn.
	 * @param color
	 *            The color to draw.
	 * @param borderColor
	 *            The (optional) border color.
	 */
	protected void drawEntity(final Graphics g, final Entity entity,
			final Color color, final Color borderColor) {
		Rectangle2D area = entity.getArea();

		int x = (int) ((area.getX() * scale) + 0.5);
		int y = (int) ((area.getY() * scale) + 0.5);
		int widthTemp = ((int) area.getWidth()) * scale;
		int heightTemp = ((int) area.getHeight()) * scale;

		g.setColor(color);
		g.fillRect(x, y, widthTemp, heightTemp);

		if (borderColor != null) {
			g.setColor(borderColor);
			g.drawRect(x, y, widthTemp - 1, heightTemp - 1);
		}
	}

	/**
	 * Draw a player entity.
	 * 
	 * @param g
	 *            The graphics context.
	 * @param player
	 *            The player to be drawn.
	 * @param color
	 *            The color to draw with.
	 */
	protected void drawPlayer(final Graphics g, final Player player,
			final Color color) {
		drawCross(g, (int) ((player.getX() * scale) + 0.5),
				(int) ((player.getY() * scale) + 0.5), color);
	}

	/** Draws a cross at the given position. */
	private void drawCross(Graphics g, int x, int y, Color color) {
		int scale_2 = scale / 2;

		int size = scale_2 + 2;

		x += scale_2;
		y += scale_2;

		g.setColor(color);
		g.drawLine(x - size, y, x + size, y);
		g.drawLine(x, y + size, x, y - size);
	}

	@Override
	public synchronized boolean onMouseDoubleClick(Point p) {
		// TODO: Check that titlebar height is calculated correctly.
		// The p.y seems higher than it should after adjustment.

		// If teleclickmode is disabled.
		if (client.getPlayer().has("teleclickmode")) { // If teleclickmode is enabled.
			RPAction action = new RPAction();
			action.put("type", "moveto");
			action.put("x", (p.x + panx - getClientX()) / scale);
			action.put("y", ((p.y + pany - getClientY()) / scale) - 1);

			client.send(action);
		} else {
			/*
			 * Move the player to the coordinates using Pathfinding
			 */

			// check if destination is walkeable.
			if (!collisiondetection.walkable((p.x + panx - getClientX())
					/ scale, ((p.y + pany - getClientY()) / scale) - 1)) {
				return true;
			}

			nodo_actual = 0;

			// Rectangle(int x, int y, int width, int height)
			int width2 = width < 192 ? width : 192;
			int height2 = height < 192 ? height : 192;
			Rectangle search_area = new Rectangle((panx) / scale, (pany)
					/ scale, width2 / scale, height2 / scale);

			long computation_time = System.currentTimeMillis();

			if (pathfind.NewPath(collisiondetection, (int) playerX,
					(int) playerY, (p.x + panx - getClientX()) / scale, ((p.y
							+ pany - getClientY()) / scale) - 1, search_area)) {

				pathfind.PathJumpNode();
				nodo_actual = pathfind.final_path_index;

				if (logger.isDebugEnabled()) {
					logger.debug("Pathfind: Found, size: "
							+ pathfind.final_path_index);
					logger.debug("Pathfind: First waypoint: "
							+ pathfind.NodeGetX() + "," + pathfind.NodeGetY());
				}

				RPAction action = new RPAction();
				action.put("type", "moveto");
				action.put("x", pathfind.NodeGetX());
				action.put("y", pathfind.NodeGetY());

				client.send(action);

			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Pathfind: unreacheable.");
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Pathfind: calculation time: "
						+ (System.currentTimeMillis() - computation_time)
						+ "ms");
			}
		}
		return true;
	}

	//
	// PositionChangeListener
	//

	/**
	 * The user position changed.
	 * 
	 * @param x
	 *            The X coordinate (in world units).
	 * @param y
	 *            The Y coordinate (in world units).
	 */
	public void positionChanged(final double x, final double y) {
		playerX = x;
		playerY = y;

		updateView();
	}
}
