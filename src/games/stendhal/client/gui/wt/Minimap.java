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

import games.stendhal.client.IGameScreen;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.Creature;
import games.stendhal.client.entity.DomesticAnimal;
import games.stendhal.client.entity.HousePortal;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.NPC;
import games.stendhal.client.entity.Player;
import games.stendhal.client.entity.Portal;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.entity.User;
import games.stendhal.client.entity.WalkBlocker;
import games.stendhal.client.events.PositionChangeListener;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.client.soundreview.SoundMaster;
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
	
	/** the logger instance. */
	private static final Logger LOGGER = Logger.getLogger(Minimap.class);
		
	/**
	 * The color of the background (palest grey).
	 */
	private static final Color COLOR_BACKGROUND = new Color(0.8f, 0.8f, 0.8f);

	/**
	 * The color of blocked areas (red).
	 */
	private static final Color COLOR_BLOCKED = new Color(1.0f, 0.0f, 0.0f);
	
	/**
	 * The color of protected areas (palest green).
	 */
	private static final Color COLOR_PROTECTION = new Color(202, 230, 202);
	
	/**
	 * The color of a general entity (pale green).
	 */
	private static final Color COLOR_ENTITY = new Color(200, 255, 200);

	/**
	 * The color of the "N" text (black).
	 */
	private static final Color COLOR_NORTH = new Color(0.0f, 0.0f, 0.0f);

	/**
	 * The colour of walk blockers (dark pink) .
	 */
    private static final Color COLOR_WALKBLOCKER = new Color(209, 144, 224);

	/** width of the minimap. */
	private static final int MINIMAP_WIDTH = 129;

	/** height of the minimap. */
	private static final int MINIMAP_HEIGHT = 129;

	/** minimum scale of the minimap. */
	private static final int MINIMAP_MINIMUM_SCALE = 2;

	/** Enable X-ray vision (aka Superman) minimap? */
	private static final boolean mininps = (System
			.getProperty("stendhal.superman") != null);

	/** scale of map. */
	private int scale;

	/** width of (scaled) minimap . */
	private int width;

	/** height of (scaled) minimap . */
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

	private final StendhalClient client;

	/**
	 * PATHFIND.
	 */

	private int nodo_actual;

	private final Pathfind pathfind;

	private CollisionDetection collisiondetection;

	

	/**
	 * Creates a new instance of Minimap.
	 * 
	 * @param client
	 * @param gameScreen
	 */
	public Minimap(final StendhalClient client, final IGameScreen gameScreen) {
		super("minimap", 0, 0, 100, 100, gameScreen);

		this.client = client;

		setTitleBar(true);
		setFrame(true);
		setMovable(true);
		setMinimizeable(true);
		if (System.getProperty("stendhal.transparency") != null) {
			
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
	 * @param pd  
	 *      	  The protection map.
	 * @param gc
	 *            A graphics configuration.
	 * @param zone
	 *            The zone name.
	 */
	public void update(final CollisionDetection cd, final CollisionDetection pd, final GraphicsConfiguration gc,
			final String zone) {
		
		setTitletext(User.get().getRPObject().getID().getZoneID());		
		
		// FOR PATHFINDING THING
		collisiondetection = cd;
		pathfind.clearPath();
		nodo_actual = 0;

		// calculate size and scale
		final int w = cd.getWidth();
		final int h = cd.getHeight();

		// calculate scale
		scale = MINIMAP_MINIMUM_SCALE;
		while ((w * (scale + 1) < MINIMAP_WIDTH)
				&& (h * (scale + 1) < MINIMAP_HEIGHT)) {
			scale++;
		}

		if (w * scale < MINIMAP_WIDTH) {
			width = w * scale;
		} else {
			width = MINIMAP_WIDTH;
		}
		if (h * scale < MINIMAP_HEIGHT) {
			height = h * scale;
		} else {
			height = MINIMAP_HEIGHT;
		}

		// create the image for the minimap
		image = gc.createCompatibleImage(w * scale, h * scale);

		final Graphics2D mapgrapics = image.createGraphics();

		mapgrapics.setColor(COLOR_BACKGROUND);
		mapgrapics.fillRect(0, 0, w * scale, h * scale);

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				if (!cd.walkable(x, y)) {
					mapgrapics.setColor(COLOR_BLOCKED);
					mapgrapics.fillRect(x * scale, y * scale, scale, scale);
				} else if (pd != null && !pd.walkable(x, y)) {
					// draw protection only if there is no collision to draw
					mapgrapics.setColor(COLOR_PROTECTION);
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

		final int w = image.getWidth();
		final int h = image.getHeight();

		final int xpos = (int) ((playerX * scale) + 0.5) - width / 2;
		final int ypos = (int) ((playerY * scale) + 0.5) - width / 2;

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
			pathfind.jumpToPathNode(nodo_actual);
			final int manhatan = (int) ((Math.abs(playerX - pathfind.nodeGetX()) + Math
					.abs(playerY - pathfind.nodeGetY())));

			if (manhatan < 6) {

				pathfind.pathJumpNode();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Pathfind: To waypoint: "
							+ pathfind.nodeGetX() + " " + pathfind.nodeGetY());
				}
				final RPAction action = new RPAction();
				action.put("type", "moveto");
				action.put("x", pathfind.nodeGetX());
				action.put("y", pathfind.nodeGetY());

				client.send(action);

				nodo_actual = pathfind.final_path_index;

				if (pathfind.isGoalReached()) {
					pathfind.clearPath();
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
	protected void drawContent(final Graphics2D g, final IGameScreen gameScreen) {
		super.drawContent(g, gameScreen);

		if (image == null) {
			return;
		}

		final Graphics vg = g.create();

		// draw minimap
		vg.translate(-panx, -pany);
		vg.drawImage(image, 0, 0, null);

		final int level = User.getPlayerLevel();

		// display a "N" to show north direction
		if (level < 10) {
			vg.setColor(COLOR_NORTH);
			vg.setFont(new Font("SansSerif", Font.PLAIN, 9));
			final FontMetrics metrics = vg.getFontMetrics();
			final Rectangle2D rect = metrics.getStringBounds("N", 0, 0, g);
			vg.drawString("N", panx + (width - (int) rect.getWidth()) / 2, pany
					+ (int) rect.getHeight());
		}

		// PATHFIND ---
		// pathfind.Reinice();
		// while (!pathfind.ReachedGoal()) {
		// pathfind.PathNextNode();
		// vg.fillRect(pathfind.NodeGetX() * scale, pathfind.NodeGetY() scale,
		// scale, scale);
		// }
		// pathfind.Reinice();
		//
		// while (!pathfind.ReachedGoal()) {
		// pathfind.PathJumpNode();
		// vg.setColor(Color.CYAN);
		// vg.fillRect(pathfind.NodeGetX() * scale, pathfind.NodeGetY() scale,
		// scale, scale);
		// }
		// pathfind.Reinice();

		// --------------------------------------
		// Draw on ground entities
		for (final IEntity entity : client.getGameObjects()) {
			if (!entity.isOnGround()) {
				continue;
			}

			if (entity instanceof Player) {
				final Player player = (Player) entity;

				if (!player.isGhostMode()) {
					drawPlayer(vg, player, Color.WHITE);
				} else if (User.isAdmin()) {
					drawPlayer(vg, player, Color.GRAY);
				}
			} else if (entity instanceof Portal) {
				final Portal portal = (Portal) entity;

				if (!portal.isHidden()) {
					drawEntity(vg, entity, Color.WHITE, Color.BLACK);
				}
			} else if (entity instanceof HousePortal) {
				drawEntity(vg, entity, Color.WHITE, Color.BLACK);
			} else if (entity instanceof WalkBlocker) {
				drawEntity(vg, entity, COLOR_WALKBLOCKER);
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
	private void drawUser(final Graphics vg) {
		final User user = User.get();

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
	protected void drawEntity(final Graphics g, final IEntity entity,
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
	protected void drawEntity(final Graphics g, final IEntity entity,
			final Color color, final Color borderColor) {
		final Rectangle2D area = entity.getArea();

		final int x = (int) ((area.getX() * scale) + 0.5);
		final int y = (int) ((area.getY() * scale) + 0.5);
		final int widthTemp = ((int) area.getWidth()) * scale;
		final int heightTemp = ((int) area.getHeight()) * scale;

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
		drawCross(g, (int) ((player.getX() * scale) + 0.5), (int) ((player
				.getY() * scale) + 0.5), color);
	}

	/**
	 * Draws a cross at the given position.
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param color
	 */
	private void drawCross(final Graphics g, int x, int y, final Color color) {
		final int scale_2 = scale / 2;

		final int size = scale_2 + 2;

		x += scale_2;
		y += scale_2;

		g.setColor(color);
		g.drawLine(x - size, y, x + size, y);
		g.drawLine(x, y + size, x, y - size);
	}

	@Override
	public synchronized boolean onMouseDoubleClick(final Point p) {

		if (client.getPlayer().has("teleclickmode")) { 
			// teleclickmode is enabled.
			final RPAction action = new RPAction();
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
			final int width2;
			if (width < 192) {
				width2 = width;
			} else {
				width2 = 192;
			}
			final int height2;
			if (height < 192) {
				height2 = height;
			} else {
				height2 = 192;
			}
			final Rectangle search_area = new Rectangle((panx) / scale, (pany)
					/ scale, width2 / scale, height2 / scale);

			final long computation_time = System.currentTimeMillis();

			if (pathfind.newPath(collisiondetection, (int) playerX,
					(int) playerY, (p.x + panx - getClientX()) / scale, ((p.y
							+ pany - getClientY()) / scale) - 1, search_area)) {

				pathfind.pathJumpNode();
				nodo_actual = pathfind.final_path_index;

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Pathfind: Found, size: "
							+ pathfind.final_path_index);
					LOGGER.debug("Pathfind: First waypoint: "
							+ pathfind.nodeGetX() + "," + pathfind.nodeGetY());
				}

				final RPAction action = new RPAction();
				action.put("type", "moveto");
				action.put("x", pathfind.nodeGetX());
				action.put("y", pathfind.nodeGetY());

				client.send(action);

			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Pathfind: unreacheable.");
				}
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Pathfind: calculation time: "
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

	@Override
	protected void playOpenSound() {
		SoundMaster.play("click-4.wav");
	}
}
