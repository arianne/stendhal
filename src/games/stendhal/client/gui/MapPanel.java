package games.stendhal.client.gui;

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
import games.stendhal.common.CollisionDetection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import marauroa.common.game.RPAction;

public class MapPanel extends JPanel implements PositionChangeListener {
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
	 * The color of other players (white).
	 */
	private static final Color COLOR_PLAYER = Color.white;
	/**
	 * The color of ghostmode players, if visible (white).
	 */
	private static final Color COLOR_GHOST = Color.gray;
	/**
	 * The colour of walk blockers (dark pink) .
	 */
    private static final Color COLOR_WALKBLOCKER = new Color(209, 144, 224);
    /**
	 * The color of a general entity (pale green).
	 */
	private static final Color COLOR_ENTITY = new Color(200, 255, 200);
	
	
	/** width of the minimap. */
	private static final int WIDTH = 128;
	/** height of the minimap. */
	private static final int HEIGHT = 128;
	/** height of the title */
	private static final int TITLE_HEIGHT = 15;
	/** Minimum scale of the map; the minimum size of one tile in pixels */
	private static final int MINIMUM_SCALE = 2;
	
	private StendhalClient client;
	/**
	 * The player X coordinate.
	 */
	private double playerX;
	/**
	 * The player Y coordinate.
	 */
	private double playerY;
	private int xOffset;
	private int yOffset;

	private static final boolean supermanMode = (System.getProperty("stendhal.superman") != null);
	
	private int width;
	private int height;
	private int scale;
	
	private Image mapImage; 
	
	/** Name of the map */
	private String title = "map";
	
	public MapPanel(final StendhalClient client) {
		this.client = client;
		// black area outside the map
		setBackground(Color.black);
		updateSize(new Dimension(WIDTH, HEIGHT + TITLE_HEIGHT));
		
		// handle double clicks for moving.
		// should we do single clicks instead?
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					onDoubleClick(e.getPoint());
				}
			}
		});
	}
	
	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		drawTitle(g);
		// The rest of the things should be drawn inside the actual map area
		g.setClip(0, 0, width, height);
		// also choose the origin so that we can simply draw to the 
		// normal coordinates
		g.translate(-xOffset, -yOffset);
		
		drawMap(g);
		
		drawEntities(g);
		drawUser(g);
		
		g.dispose();
	}
	
	/**
	 * Set the dimensions of the component 
	 * @param dim 
	 */
	private void updateSize(final Dimension dim) {
		/*
		 * For some reason this:
		 * 		setMaximumSize(dim);
		 * causes this component to become unshrinkable
		 * in java 1.6. In 1.5 it would work as expected.
		 * Using the following as workaround as it seems
		 * to work in both.  
		 */
		setMaximumSize(new Dimension(0, dim.height));
		setPreferredSize(dim);
		// the user may have hidden the component partly or entirely
		setSize(getWidth(), dim.height);
				                                
		revalidate();
	}
	
	/**
	 * Draw the map backgound
	 * 
	 * @param g The graphics context
	 */
	private void drawMap(final Graphics g) {
		g.drawImage(mapImage, 0, 0, null);
	}

	/**
	 * Draw the entities on the map 
	 * @param g The graphics context
	 */
	private void drawEntities(final Graphics g) {
		for (final IEntity entity : client.getGameObjects()) {
			if (!entity.isOnGround()) {
				continue;
			}
			if (entity instanceof Player) {
				final Player player = (Player) entity;

				if (!player.isGhostMode()) {
					drawPlayer(g, player, COLOR_PLAYER);
				} else if (User.isAdmin()) {
					drawPlayer(g, player, COLOR_GHOST);
				}
			} else if (entity instanceof Portal) {
				final Portal portal = (Portal) entity;

				if (!portal.isHidden()) {
					drawEntity(g, entity, Color.WHITE, Color.BLACK);
				}
			} else if (entity instanceof HousePortal) {
				drawEntity(g, entity, Color.WHITE, Color.BLACK); 
			} else if (entity instanceof WalkBlocker) {
				drawEntity(g, entity, COLOR_WALKBLOCKER);
			} else if (supermanMode && User.isAdmin()) {
				if (entity instanceof RPEntity) {
					drawRPEntity(g, (RPEntity) entity);
				} else {
				    drawEntity(g, entity, COLOR_ENTITY);
				}
			}
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
	private void drawPlayer(final Graphics g, final Player player,
			final Color color) {
		drawCross(g, (int) ((player.getX() * scale) + 0.5), (int) ((player
				.getY() * scale) + 0.5), color);
	}
	
	/**
	 * Draws the User.
	 * 
	 * @param g graphics context
	 */
	private void drawUser(final Graphics g) {
		final User user = User.get();

		if (user != null) {
			drawPlayer(g, user, Color.BLUE);
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
	private void drawEntity(final Graphics g, final IEntity entity,
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
	private void drawEntity(final Graphics g, final IEntity entity,
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
	 * Draws an RPEntity on the map.
	 * 
	 * @param g
	 *            Graphics
	 * @param entity
	 *            The entity to be drawn
	 */
	private void drawRPEntity(final Graphics g, final RPEntity entity) {
		if (entity instanceof DomesticAnimal) {
			drawEntity(g, entity, Color.ORANGE);
		} else if (entity instanceof Creature) {
			drawEntity(g, entity, Color.YELLOW);
		} else if (entity instanceof NPC) {
			drawEntity(g, entity, Color.BLUE);
		}
	}
	
	/**
	 * Draws a cross at the given position.
	 * 
	 * @param g The graphics context
	 * @param x x coordinate of the center
	 * @param y y coordinate of the center
	 * @param color the draw color
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
	
	/**
	 * Draw the map title 
	 * @param g The graphics context
	 */
	private void drawTitle(final Graphics g) {
		final Rectangle bounds = g.getClipBounds();
		
		// draw only if drawing the title area is requested
		if (bounds.y + bounds.height > height) {
			final Rectangle2D rect = g.getFontMetrics().getStringBounds(title, g);
			g.setColor(Color.white);
			g.drawString(title, Math.max(0, (width - (int) rect.getWidth()) / 2), height + TITLE_HEIGHT - 3);
		}
	}
	
	/**
	 * The player's position changed.
	 * 
	 * @param x
	 *            The X coordinate (in world units).
	 * @param y
	 *            The Y coordinate (in world units).
	 */
	public void positionChanged(final double x, final double y) {
		/*
		 * The client gets occasionally spurious events.
		 * Suppress repainting unless the position actually changed
		 */
		if ((playerX != x) || (playerY != y)) {
			playerX = x;
			playerY = y;

			updateView();
	
		}
	}
	
	/**
	 * Redraw the map area. To be called from the game loop.
	 */
	public void refresh() {
		repaint(0, 0, width, height);	
	}
	
	/**
	 * Update the view pan. This should be done when the map size or player
	 * position changes.
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
		title = zone;

		// calculate the size and scale of the map
		final int mapWidth = cd.getWidth();
		final int mapHeight = cd.getHeight();
		scale = Math.max(MINIMUM_SCALE, Math.min(HEIGHT / mapHeight, WIDTH / mapWidth));
		width = Math.min(WIDTH, mapWidth * scale);
		height = Math.min(HEIGHT, mapHeight * scale);
		
		// create the map image, and fill it with the wanted details
		mapImage = this.getGraphicsConfiguration().createCompatibleImage(mapWidth * scale, mapHeight * scale);
		final Graphics g = mapImage.getGraphics();
		g.setColor(COLOR_BACKGROUND);
		g.fillRect(0, 0, mapWidth * scale, mapHeight * scale);
		
		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				if (!cd.walkable(x, y)) {
					g.setColor(COLOR_BLOCKED);
					g.fillRect(x * scale, y * scale, scale, scale);
				} else if (pd != null && !pd.walkable(x, y)) {
					// draw protection only if there is no collision to draw
					g.setColor(COLOR_PROTECTION);
					g.fillRect(x * scale, y * scale, scale, scale);
				}
			}
		}
		g.dispose();
		
		updateSize(new Dimension(WIDTH, height + TITLE_HEIGHT));
		updateView();
		
		repaint();
	}
	
	/**
	 * Tell the player to move to point p
	 * @param p the point
	 */
	private void onDoubleClick(final Point p) {
		// Ignore clicks to the title area 
		if (p.y <= height) {
			final RPAction action = new RPAction();
			action.put("type", "moveto");
			action.put("x", (p.x + xOffset) / scale);
			action.put("y", (p.y + yOffset) / scale);

			client.send(action);
		}
	}
}
