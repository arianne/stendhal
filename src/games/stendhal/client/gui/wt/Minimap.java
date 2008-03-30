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

import games.stendhal.client.GameObjects;
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
import games.stendhal.client.gui.ClientPanel;
import games.stendhal.client.gui.MouseHandlerAdapter;
import games.stendhal.common.CollisionDetection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

/**
 * The minimap.
 * 
 * @author mtotz
 */

@SuppressWarnings("serial")
public final class Minimap extends ClientPanel implements PositionChangeListener {

	/**
	 * The color of the background.
	 */
	private static final Color COLOR_BACKGROUND = new Color(0.8f, 0.8f, 0.8f);

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

	/** size of (scaled) minimap .*/
	private Dimension size = new Dimension();

	/**
	 * The view offset.
	 */
	private Point pan = new Point();

	/**
	 * The player coordinate.
	 */
	private Point2D.Double playerPos = new Point2D.Double();

	/** minimap image. */
	private BufferedImage image;

	private StendhalClient client;

	/**
	 * PATHFIND.
	 */

	private Pathfind pathfind = new Pathfind();

	private int currentNode;

	private CollisionDetection collisiondetection;

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(Minimap.class);

	/** Creates a new instance of Minimap. */
	public Minimap(StendhalClient client) {
		// Show nothing until there is map data
		super("Minimap", 100, 0);

		setLocation(4/*StendhalUI.get().getWidth() - 200*/, 220);

		this.client = client;

//		if (System.getProperty("stendhal.transparency") != null) {
//			setTransparency(0.5f);
//		}

		addMouseListener(new MyMouseHandlerAdapter());
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
		setTitle(zone);

		// FOR PATHFINDING THING
		collisiondetection = cd;
		pathfind.ClearPath();
		currentNode = 0;

		// calculate size and scale
		int w = cd.getWidth();
		int h = cd.getHeight();

		// calculate scale
		scale = MINIMAP_MINIMUM_SCALE;
		while ((w * (scale + 1) < MINIMAP_WIDTH)
				&& (h * (scale + 1) < MINIMAP_HEIGHT)) {
			++scale;
		}

		// calculate size of map
		size.width = (w * scale < MINIMAP_WIDTH) ? w * scale : MINIMAP_WIDTH;
		size.height = (h * scale < MINIMAP_HEIGHT) ? h * scale : MINIMAP_HEIGHT;

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
		setClientSize(size.width, size.height + 2);

		updateView();
	}

	/**
	 * Update the view pan. This should be done when the map size or player
	 * position changes.
	 */
	private void updateView() {
		pan = new Point(0, 0);

		if (image == null) {
			return;
		}

		int w = image.getWidth();
		int h = image.getHeight();

		int xpos = (int) ((playerPos.x * scale) + 0.5) - size.width / 2;
		int ypos = (int) ((playerPos.y * scale) + 0.5) - size.width / 2;

		if (w > size.width) {
			// need to pan width
			if ((xpos + size.width) > w) {
				// x is at the screen border
				pan.x = w - size.width;
			} else if (xpos > 0) {
				pan.x = xpos;
			}
		}

		if (h > size.height) {
			// need to pan height
			if ((ypos + size.height) > h) {
				// y is at the screen border
				pan.y = h - size.height;
			} else if (ypos > 0) {
				pan.y = ypos;
			}
		}
	}

	public void update_pathfind() {
		if (currentNode != 0) {
			pathfind.PathJumpToNode(currentNode);
			int manhatan = (int) ((Math.abs(playerPos.x - pathfind.NodeGetX()) + Math.abs(playerPos.y - pathfind.NodeGetY())));

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

				currentNode = pathfind.final_path_index;

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
    public void paint(Graphics g) {
		super.paint(g);

		if (image == null) {
			return;
		}

		Graphics vg = g.create();
		Rectangle clnt = getClientRect();

		vg.clipRect(clnt.x, clnt.y, clnt.width, clnt.height);

		// draw minimap
		vg.translate(-pan.x, -pan.y);
		vg.drawImage(image, clnt.x, clnt.y, null);

		int level = User.getPlayerLevel();

		// take into account the client area shift
		vg.translate(clnt.x, clnt.y);

		// display a "N" to show north direction for beginners
		if (level < 10) {
    		vg.setColor(COLOR_NORTH);
    		vg.setFont(new Font("SansSerif", Font.PLAIN, 9));
    		FontMetrics metrics = vg.getFontMetrics();
    		Rectangle2D rect = metrics.getStringBounds("N", 0, 0, g);
    		vg.drawString("N",
    						pan.x + (size.width - (int) rect.getWidth()) / 2,
    						pan.y + (int) rect.getHeight());
		}

//		pathfind.Reinice();
//		while (!pathfind.ReachedGoal()) {
//			pathfind.PathNextNode();
//			vg.fillRect(pathfind.NodeGetX() * scale, pathfind.NodeGetY() *scale, scale, scale);
//		}
//		pathfind.Reinice();
//
//		 while (!pathfind.ReachedGoal()) {
//			 pathfind.PathJumpNode();
//			 vg.setColor(Color.CYAN);
//			 vg.fillRect(pathfind.NodeGetX() * scale, pathfind.NodeGetY() *scale, scale, scale);
//		}
//		pathfind.Reinice();

		// --------------------------------------
		// Draw on ground entities
		GameObjects gameObjects = client.getGameObjects();

		//TODO remove the need for synchronization by changing getGameObjects() to return a copy of the game object list

		synchronized (gameObjects) {
    		for (Entity entity : gameObjects) {
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
	protected void drawEntity(final Graphics g, final Entity entity, final Color color) {
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
	protected void drawEntity(final Graphics g, final Entity entity, final Color color, final Color borderColor) {
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
	protected void drawPlayer(final Graphics g, final Player player, final Color color) {
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
		playerPos = new Point2D.Double(x, y);

		updateView();
	}


	class MyMouseHandlerAdapter extends MouseHandlerAdapter {

		@Override
    	public void onLeftClick(MouseEvent e) {
    		Point p = e.getPoint();
    		Point clnt = getClientPos();

    		// If teleclickmode is enabled.
    		if (client.getPlayer().has("teleclickmode")) {
    			RPAction action = new RPAction();
    			action.put("type", "moveto");
    			action.put("x", (p.x + pan.x - clnt.x) / scale);
    			action.put("y", (p.y + pan.y - clnt.y) / scale - 1);

    			client.send(action);
    		} else {
    			/*
    			 * Move the player to the coordinates using Pathfinding
    			 */

    			// check if destination is walkable.
    			if (!collisiondetection.walkable((p.x + pan.x - clnt.x)
    					/ scale, ((p.y + pan.y - clnt.y) / scale) - 1)) {
    				return;
    			}

    			currentNode = 0;

    			int width2 = size.width < 192 ? size.width : 192;
    			int height2 = size.height < 192 ? size.height : 192;

    			Rectangle search_area = new Rectangle(
    					pan.x / scale, pan.y / scale,
    					width2 / scale, height2 / scale);

    			long computation_time = System.currentTimeMillis();

    			if (pathfind.NewPath(collisiondetection, (int) playerPos.x, (int) playerPos.y,
    						(p.x + pan.x - clnt.x) / scale,
    						((p.y + pan.y - clnt.y) / scale) - 1, search_area)) {
    				pathfind.PathJumpNode();
    				currentNode = pathfind.final_path_index;

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

    				if (currentNode == 0)// We arrived at our destination.
    				{
    					pathfind.ClearPath();
    				}
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
		}
	}

}
