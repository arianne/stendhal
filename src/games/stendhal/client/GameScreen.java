/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client;

import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.j2d.Text;
import games.stendhal.client.gui.j2d.TextBoxFactory;
import games.stendhal.client.gui.j2d.entity.Entity2DView;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.j2d.entity.EntityViewFactory;
import games.stendhal.client.gui.wt.GroundContainer;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.client.listener.PositionChangeListener;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.NotificationType;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * The game screen. This manages and renders the visual elements of the game.
 */
/*
 * In principle swing has a nice builtin double buffering. In practice it 
 * can not be used for anything but trivial cases, so using Canvas instead
 */
public class GameScreen implements PositionChangeListener, IGameScreen {
	/**
	 * Comparator used to sort entities to display.
	 */
	protected static final EntityViewComparator entityViewComparator = new EntityViewComparator();

	/**
	 * A scale factor for panning delta (to allow non-float precision).
	 */
	protected static final int PAN_SCALE = 8;
	/**
	 * Speed factor for centering the screen. Smaller is faster,
	 * and keeps the player closer to the center of the screen when walking.
	 */
	private static final int PAN_INERTIA = 15;
	
	/** the logger instance. */
	private static final Logger LOGGER = Logger.getLogger(GameScreen.class);

	private static final Sprite offlineIcon;

	/** the singleton instance. */
	private static GameScreen screen;

	private Canvas canvas;
	private BufferStrategy buffer;
	/**
	 * Static game layers.
	 */
	protected StaticGameLayers gameLayers;
	
	/**
	 * The entity views.
	 */
	protected List<EntityView> views;

	/**
	 * The entity to view map.
	 */
	protected Map<IEntity, EntityView> entities;

	/** Actual size of the world in world units. */
	protected int ww;
	protected int wh;

	/**
	 * The ground layer.
	 */
	private final GroundContainer ground;

	

	/**
	 * The text bubbles.
	 */
	private final LinkedList<Text> texts;

	/**
	 * The text bubbles to remove.
	 */
	private final List<Text> textsToRemove;

	
	
	private boolean offline;

	private int blinkOffline;

	/**
	 * The targeted center of view X coordinate (truncated).
	 */
	private int x;

	/**
	 * The targeted center of view Y coordinate (truncated).
	 */
	private int y;

	/** Actual size of the screen in pixels. */
	private int sw;
	private int sh;

	private long lastDrawYield = 0;
	


	/**
	 * The difference between current and target screen view X.
	 */
	private int dvx;

	/**
	 * The difference between current and target screen view Y.
	 */
	private int dvy;

	/**
	 * The current screen view X.
	 */
	private int svx;

	/**
	 * The current screen view Y.
	 */
	private int svy;

	/**
	 * Current panning speed.
	 */
	private int speed;

	static {
		offlineIcon = SpriteStore.get().getSprite("data/gui/offline.png");
	}

	private TextBoxFactory textBoxFactory;
	
	/**
	 * The canvas can be resized using a split pane. This is 
	 * for adjusting the internal parameters for the change. 
	 */
	private class CanvasResizeListener implements ComponentListener {
		public void componentHidden(ComponentEvent e) {	}

		public void componentMoved(ComponentEvent e) { 	}

		public void componentResized(ComponentEvent e) {
			Dimension size = canvas.getSize(); 

			sw = Math.min(canvas.getWidth(), stendhal.screenSize.width);
			sh = Math.min(canvas.getHeight(), stendhal.screenSize.height);
			// Reset the view so that the player is in the center
			calculateView();
			center();
		}

		public void componentShown(ComponentEvent e) { 	}
	}

	/**
	 * Create a game screen.
	 *
	 * @param client
	 *            The client.
	 */
	public GameScreen(final StendhalClient client) {
		canvas = new Canvas();
		canvas.setIgnoreRepaint(true);
		
		canvas.setSize(stendhal.screenSize);
		canvas.addComponentListener(new CanvasResizeListener());
		
		gameLayers = client.getStaticGameLayers();

		sw = canvas.getWidth();
		sh = canvas.getHeight();

		x = 0;
		y = 0;
		svx = sw / -2;
		svy = sh / -2;
		dvx = 0;
		dvy = 0;

		speed = 0;

		texts = new LinkedList<Text>();
		textsToRemove = new LinkedList<Text>();
		views = new LinkedList<EntityView>();
		entities = new HashMap<IEntity, EntityView>();

		// create ground
		ground = new GroundContainer(client, this, sw, sh);

		// register native event handler
		canvas.addMouseListener(ground);
		canvas.addMouseMotionListener(ground);
	}
	
	/**
	 * Get the canvas component
	 * 
	 * @return the canvas
	 */
	public Component getComponent() {
		return canvas;
	}

	/**
	 * Set the default [singleton] screen.
	 *
	 * @param screen
	 *            The screen.
	 */
	
	public static void setDefaultScreen(final GameScreen screen) {
		GameScreen.screen = screen;
	}

	/** @return the GameScreen object. */
	public static GameScreen get() {
		return screen;
	}

	/** @return screen width in world units. */
	private int getViewWidth() {
		return sw / SIZE_UNIT_PIXELS;
	}

	/** @return screen height in world units .*/
	private int getViewHeight() {
		return sh / SIZE_UNIT_PIXELS;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#nextFrame()
	 */
	public void nextFrame() {
		adjustView();
	}
	
	private TextBoxFactory getTextFactory() {
		if (textBoxFactory == null) {
			textBoxFactory = new TextBoxFactory((Graphics2D) getGraphics());
		}
		
		return textBoxFactory;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#addDialog(games.stendhal.client.gui.wt.core.WtPanel)
	 */
	public void addDialog(final WtPanel panel) {
		ground.addChild(panel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#addEntity(games.stendhal.client.entity.Entity)
	 */
	public void addEntity(final IEntity entity) {
		final EntityView view = EntityViewFactory.create(entity);

		if (view != null) {
			entities.put(entity, view);
			addEntityView(view);
		}
	}

	/**
	 * Add an entity view.
	 *
	 * @param view
	 *            A view.
	 */
	private void addEntityView(final EntityView view) {
		views.add(view);
		if (view instanceof Entity2DView) {
			final Entity2DView inspectable = (Entity2DView) view;
			
			inspectable.setInspector(ground);
		}	
	}
	

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#removeEntity(games.stendhal.client.entity.Entity)
	 */
	public void removeEntity(final IEntity entity) {
		final EntityView view = entities.remove(entity);

		if (view != null) {
			removeEntityView(view);
		}
	}

	/**
	 * Remove an entity view.
	 *
	 * @param view
	 *            A view.
	 */
	private void removeEntityView(final EntityView view) {
		view.release(this);
		views.remove(view);
	}

	/**
	 * Update the view position to center the target position.
	 */
	private void adjustView() {
		/*
		 * Already centered?
		 */
		if ((dvx == 0) && (dvy == 0)) {
			return;
		}

		final int sx = convertWorldXToScreenView(x) + (SIZE_UNIT_PIXELS / 2);
		final int sy = convertWorldYToScreenView(y) + (SIZE_UNIT_PIXELS / 2);

		if ((sx < 0) || (sx >= sw) || (sy < -SIZE_UNIT_PIXELS) || (sy > sh)) {
			/*
			 * If off screen, just center
			 */
			center();
		} else {
			/*
			 * Calculate the target speed. The farther away, the faster.
			 */
			final int dux = dvx / PAN_INERTIA;
			final int duy = dvy / PAN_INERTIA;

			final int tspeed = ((dux * dux) + (duy * duy)) * PAN_SCALE;

			if (speed > tspeed) {
				speed = (speed + speed + tspeed) / 3;

				/*
				 * Don't stall
				 */
				if ((dvx != 0) || (dvy != 0)) {
					speed = Math.max(speed, 1);
				}
			} else if (speed < tspeed) {
				speed += 2;
			}

			/*
			 * Moving?
			 */
			if (speed != 0) {
				/*
				 * Not a^2 + b^2 = c^2, but good enough
				 */
				final int scalediv = (Math.abs(dvx) + Math.abs(dvy)) * PAN_SCALE;

				int dx = speed * dvx / scalediv;
				int dy = speed * dvy / scalediv;

				/*
				 * Don't overshoot. Don't stall.
				 */
				if (dvx < 0) {
					dx = Math.max(Math.min(-1, dx), dvx);
				} else if (dvx > 0) {
					dx = Math.min(Math.max(1, dx), dvx);
				}

				if (dvy < 0) {
					dy = Math.max(Math.min(-1, dy), dvy);
				} else if (dvy > 0) {
					dy = Math.min(Math.max(1, dy), dvy);
				}

				/*
				 * Adjust view
				 */
				svx += dx;
				dvx -= dx;

				svy += dy;
				dvy -= dy;
			}
		}
	}

	/**
	 * Updates the view position to center the target position.
	 * Prefer top left if the map is smaller than the screen.
	 */
	private void calculateView() {
		// Coordinates for a screen centered on player
		int cvx = (x * SIZE_UNIT_PIXELS) + (SIZE_UNIT_PIXELS / 2) - (sw / 2);
		int cvy = (y * SIZE_UNIT_PIXELS) + (SIZE_UNIT_PIXELS / 2) - (sh / 2);

		/*
		 * Keep the world within the screen view
		 */
		final int maxX = ww * SIZE_UNIT_PIXELS - sw;
		cvx = Math.min(cvx, maxX);
		cvx = Math.max(cvx, 0);
		
		final int maxY = wh * SIZE_UNIT_PIXELS - sh;
		cvy = Math.min(cvy, maxY);
		cvy = Math.max(cvy, 0);
		
		// Differences from center
		dvx = cvx - svx;
		dvy = cvy - svy;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#center()
	 */
	public void center() {
		svx += dvx;
		svy += dvy;

		dvx = 0;
		dvy = 0;

		speed = 0;
	}	

	public void draw() {
		/*
		 * Don't try drawing until we are ready. A workaround until
		 * the canvas has been done properly in swing.
		 */
		if (canvas.isDisplayable()) {
			final Graphics g = getGraphics();
			draw((Graphics2D) g);

			buffer.show();
			g.dispose();
		}
	}
	
	public Graphics getGraphics() {
		/*
		 *  swing does not want to give a valid GraphicsConfiguration until the
		 *  window has been drawn, so this can not be done in the constructor.
		 */
		if (buffer == null) {
			canvas.createBufferStrategy(2);
			buffer = canvas.getBufferStrategy(); 
		}
		
		return buffer.getDrawGraphics();
	}
	
	private void draw(final Graphics2D g2d) {
		Collections.sort(views, entityViewComparator);

		/*
		 * Draw the GameLayers from bootom to top, relies on exact naming of the
		 * layers
		 */
		final String set = gameLayers.getAreaName();
		
		// An adjusted graphics object so that the drawn objects do not need to
		// know about converting the position to screen
		Graphics2D graphics = (Graphics2D) g2d.create();
		if (graphics.getClipBounds() == null) {
			graphics.setClip(0, 0, Math.min(canvas.getWidth(), stendhal.screenSize.width),
					Math.min(canvas.getHeight(), stendhal.screenSize.height));
		}

		int xAdjust = -getScreenViewX();
		int yAdjust = -getScreenViewY();
		graphics.translate(xAdjust, yAdjust);
		/*
		 * End of the world (map falls short of the view)?
		 */
		if (xAdjust > 0) {
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, 0, xAdjust, sh);
		}
		
		if (yAdjust > 0) {
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, 0, sw, yAdjust);
		}
		
		int tmpY = yAdjust + convertWorldToScreen(wh);
		if (tmpY < sh) {
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, tmpY, sw, sh);
		}
		
		int tmpX = yAdjust + convertWorldToScreen(ww);
		if (tmpX < sw) {
			g2d.setColor(Color.BLACK);
			g2d.fillRect(tmpX, 0, sw, sh);
		}
		
		int layerWidth = getViewWidth() + 2;
		int layerHeight = getViewHeight() + 2;
		
		final int xTemp = Math.max(0, (int) getViewX());
		final int yTemp = Math.max(0, (int) getViewY());
		
		gameLayers.draw(graphics, set, "0_floor", xTemp, yTemp, layerWidth, layerHeight);
		gameLayers.draw(graphics, set, "1_terrain", xTemp, yTemp, layerWidth, layerHeight);
		gameLayers.draw(graphics, set, "2_object", xTemp, yTemp, layerWidth, layerHeight);
		
		drawEntities(graphics);

		gameLayers.draw(graphics, set, "3_roof", xTemp, yTemp, layerWidth, layerHeight);
		gameLayers.draw(graphics, set, "4_roof_add", xTemp, yTemp, layerWidth, layerHeight);
		
		drawTopEntities(graphics);
		
		drawText(g2d);

		/*
		 * Dialogs
		 */
		ground.draw(g2d, this);

		/*
		 * Offline
		 */
		if (offline && (blinkOffline > 0)) {
			offlineIcon.draw(g2d, 560, 420);
		}

		if (blinkOffline < -10) {
			blinkOffline = 20;
		} else {
			blinkOffline--;
		}
		graphics.dispose();

		// On Ubuntu 9.10 with Sub Java 1.6.0_15 the client does not react to typed letters
		// in the chat line without the Thread.yield. The problem does neither occure on OpenJDK
		// nor on Ubuntu 9.04
		long now = System.currentTimeMillis();
		if (now - lastDrawYield > 200) {
			lastDrawYield = now;
			Thread.yield();
		}
	}

	/**
	 * Draw the screen entities.
	 */
	private void drawEntities(final Graphics2D g) {
		for (final EntityView view : views) {
			view.draw(g);
		}
	}

	/**
	 * Draw the top portion screen entities (such as HP/title bars).
	 */
	private void drawTopEntities(final Graphics2D g) {
		for (final EntityView view : views) {
			view.drawTop(g);
		}
	}

	/**
	 * Draw the screen text bubbles.
	 */
	private void drawText(final Graphics2D g2d) {
		texts.removeAll(textsToRemove);
		textsToRemove.clear();

		try {
			for (final Text text : texts) {
				text.draw(this);
			}
		} catch (final ConcurrentModificationException e) {
			LOGGER.error("cannot draw text", e);
		}
	}

	/**
	 * Get the view X world coordinate.
	 *
	 * @return The X coordinate of the left side.
	 */
	private double getViewX() {
		return (double) getScreenViewX() / SIZE_UNIT_PIXELS;
	}

	/**
	 * Get the view Y world coordinate.
	 *
	 * @return The Y coordinate of the left side.
	 */
	private double getViewY() {
		return (double) getScreenViewY() / SIZE_UNIT_PIXELS;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#setMaxWorldSize(double, double)
	 */
	public void setMaxWorldSize(final double width, final double height) {
		ww = (int) width;
		wh = (int) height;

		calculateView();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#setOffline(boolean)
	 */
	public void setOffline(final boolean offline) {
		this.offline = offline;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#addText(double, double,
	 *      java.lang.String, games.stendhal.client.NotificationType, boolean)
	 */
	public void addText(final double x, final double y, final String text, final NotificationType type,
			final boolean isTalking) {
		addText(x, y, text, type.getColor(), isTalking);
	}

	/**
	 * Adds a text bubble at a give position of the specified Color.
	 * 
	 * @param x The screen X coordinate.
	 * @param y The screen Y coordinate.
	 * @param text The textual content
	 * @param color The color in which the text shall be shown 
	 * @param isTalking Is it a talking text bubble
	 * @see games.stendhal.common.NotificationType
	 * 
	 */
	private void addText(final double x, final double y, final String text,
			final Color color, final boolean talking) {
		addText(convertWorldToScreen(x), convertWorldToScreen(y), text, color,
				talking);
	}

	/**
	 * Add a text bubble.
	 *
	 * @param sx
	 *            The screen X coordinate.
	 * @param sy
	 *            The screen Y coordinate.
	 * @param text
	 *            The text.
	 * @param color
	 *            The text color.
	 * @param isTalking
	 *            Is it is a talking text bubble.
	 */
	private void addText(int sx, int sy, final String text, final Color color,
			final boolean isTalking) {
		final Sprite sprite = getTextFactory().createTextBox(text, 240, color, Color.white, isTalking);

		if (isTalking) {
			// Point alignment: left, bottom
			sy -= sprite.getHeight();
		} else {
			// Point alignment: left-right centered, bottom
			sx -= (sprite.getWidth() / 2);
			sy -= sprite.getHeight();
		}

		/*
		 * Try to keep the text on screen. This could mess up the "talk" origin
		 * positioning.
		 */
		if (sx < 0) {
			sx = 0;
		} else {
			final int max = getScreenWidth() - sprite.getWidth();

			if (sx > max) {
				sx = max;
			}
		}

		if (sy < 0) {
			sy = 0;
		} else {
			final int max = getScreenHeight() - sprite.getHeight();

			if (sy > max) {
				sy = max;
			}
		}

		boolean found = true;

		while (found) {
			found = false;

			for (final Text item : texts) {
				if ((item.getX() == sx) && (item.getY() == sy)) {
					found = true;
					sy += (SIZE_UNIT_PIXELS / 2);
					break;
				}
			}
		}

		texts.add(new Text(sprite, sx, sy, Math.max(
				Text.STANDARD_PERSISTENCE_TIME, text.length()
						* Text.STANDARD_PERSISTENCE_TIME / 50)));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#removeText(games.stendhal.client.gui.j2d.Text)
	 */
	public void removeText(final Text entity) {
		textsToRemove.add(entity);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#removeAll()
	 */
	public void removeAll() {
		views.clear();
		texts.clear();
		textsToRemove.clear();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#clearTexts()
	 */
	public void clearTexts() {
		for (final Text text : texts) {
			textsToRemove.add(text);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#getEntityViewAt(double, double)
	 */
	public EntityView getEntityViewAt(final double x, final double y) {
		ListIterator<EntityView> it;

		/*
		 * Try the physical entity areas first
		 */
		it = views.listIterator(views.size());

		while (it.hasPrevious()) {
			final EntityView view = it.previous();

			if (view.getEntity().getArea().contains(x, y)) {
				return view;
			}
		}

		/*
		 * Now the visual entity areas
		 */
		final int sx = convertWorldToScreen(x);
		final int sy = convertWorldToScreen(y);

		it = views.listIterator(views.size());

		while (it.hasPrevious()) {
			final EntityView view = it.previous();

			if (view.getArea().contains(sx, sy)) {
				return view;
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#getMovableEntityViewAt(double,
	 *      double)
	 */
	public EntityView getMovableEntityViewAt(final double x, final double y) {
		ListIterator<EntityView> it;

		/*
		 * Try the physical entity areas first
		 */
		it = views.listIterator(views.size());

		while (it.hasPrevious()) {
			final EntityView view = it.previous();

			if (view.isMovable()) {
				if (view.getEntity().getArea().contains(x, y)) {
					return view;
				}
			}
		}

		/*
		 * Now the visual entity areas
		 */
		final int sx = convertWorldToScreen(x);
		final int sy = convertWorldToScreen(y);

		it = views.listIterator(views.size());

		while (it.hasPrevious()) {
			final EntityView view = it.previous();

			if (view.isMovable()) {
				if (view.getArea().contains(sx, sy)) {
					return view;
				}
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#getTextAt(double, double)
	 */
	public Text getTextAt(final double x, final double y) {
		final ListIterator<Text> it = texts.listIterator(texts.size());

		final int sx = convertWorldToScreen(x);
		final int sy = convertWorldToScreen(y);

		while (it.hasPrevious()) {
			final Text text = it.previous();

			if (text.getArea().contains(sx, sy)) {
				return text;
			}
		}

		return null;
	}

	/**
	 * Convert world X coordinate to screen view coordinate.
	 *
	 * @param wx
	 *            World X coordinate.
	 *
	 * @return Screen X coordinate (in integer value).
	 */
	private int convertWorldXToScreenView(final double wx) {
		return convertWorldToScreen(wx) - svx;
	}

	/**
	 * Convert world Y coordinate to screen view coordinate.
	 *
	 * @param wy
	 *            World Y coordinate.
	 *
	 * @return Screen Y coordinate (in integer value).
	 */
	private int convertWorldYToScreenView(final double wy) {
		return convertWorldToScreen(wy) - svy;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#convertWorldToScreenView(double,
	 *      double)
	 */
	public Point convertWorldToScreenView(final double wx, final double wy) {
		return new Point(convertWorldXToScreenView(wx),
				convertWorldYToScreenView(wy));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#draw(games.stendhal.client.sprite.Sprite,
	 *      int, int)
	 */
	public void draw(final Sprite sprite, final double wx, final double wy) {
		final Point p = convertWorldToScreenView(wx, wy);

		if (sprite != null) {
			final int spritew = sprite.getWidth() + 2;
			final int spriteh = sprite.getHeight() + 2;

			if (((p.x >= -spritew) && (p.x < sw))
					&& ((p.y >= -spriteh) && (p.y < sh))) {
				sprite.draw(getGraphics(), p.x, p.y);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#drawInScreen(games.stendhal.client.sprite.Sprite,
	 *      int, int)
	 */
	public void drawInScreen(final Sprite sprite, final int sx, final int sy) {
		sprite.draw(getGraphics(), sx, sy);
	}

	/**
	 * Convert a world unit value to a screen unit value.
	 *
	 * @param w World value.
	 *
	 * @return A screen value (in pixels).
	 */
	private int convertWorldToScreen(final double w) {
		return (int) (w * SIZE_UNIT_PIXELS);
	}

	/**
	 * Convert screen coordinates to world coordinates.
	 *
	 * @param x
	 *            The virtual screen X coordinate.
	 * @param y
	 *            The virtual screen Y coordinate.
	 *
	 * @return World coordinates.
	 */
	private Point2D convertScreenToWorld(final int x, final int y) {
		return new Point.Double((double) x / SIZE_UNIT_PIXELS, (double) y
				/ SIZE_UNIT_PIXELS);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#convertScreenViewToWorld(java.awt.Point)
	 */
	public Point2D convertScreenViewToWorld(final Point p) {
		return convertScreenViewToWorld(p.x, p.y);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#convertScreenViewToWorld(int, int)
	 */
	public Point2D convertScreenViewToWorld(final int x, final int y) {
		return convertScreenToWorld(x + getScreenViewX(), y + getScreenViewY());
	}

	/**
	 * Get the full screen height in pixels.
	 *
	 * @return The height.
	 */
	private int getScreenHeight() {
		return convertWorldToScreen(wh);
	}

	/**
	 * Get the full screen width in pixels.
	 *
	 * @return The width.
	 */
	private int getScreenWidth() {
		return convertWorldToScreen(ww);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#getScreenViewX()
	 */
	public int getScreenViewX() {
		return svx;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#getScreenViewY()
	 */
	public int getScreenViewY() {
		return svy;
	}

	//
	// PositionChangeListener
	//

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#positionChanged(double, double)
	 */
	public void positionChanged(final double x, final double y) {
		final int ix = (int) x;
		final int iy = (int) y;

		/*
		 * Save CPU cycles
		 */
		if ((ix != this.x) || (iy != this.y)) {
			this.x = ix;
			this.y = iy;

			calculateView();
		}
	}

	//
	//

	private static class EntityViewComparator implements
			Comparator<EntityView> {
		//
		// Comparator
		//

		public int compare(final EntityView view1, final EntityView view2) {
			int rv;

			rv = view1.getZIndex() - view2.getZIndex();

			if (rv == 0) {
				final Rectangle area1 = view1.getArea();
				final Rectangle area2 = view2.getArea();

				rv = (area1.y + area1.height) - (area2.y + area2.height);

				if (rv == 0) {
					rv = area1.x - area2.x;
					/*
					 * Quick workaround to stack items in the same order they
					 * were added.
					 *
					 * TODO: stack items in the same order they were added on server side. 
					 */
					if (rv == 0) {
						rv = view1.getEntity().getID().getObjectID()
								- view2.getEntity().getID().getObjectID();
					}
				}
			}

			return rv;
		}
	}
}
