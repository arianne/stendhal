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
import games.stendhal.client.events.PositionChangeListener;
import games.stendhal.client.gui.FormatTextParser;
import games.stendhal.client.gui.j2d.Text;
import games.stendhal.client.gui.j2d.entity.Entity2DView;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.j2d.entity.EntityViewFactory;
import games.stendhal.client.gui.wt.GroundContainer;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.client.sprite.ImageSprite;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.NotificationType;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.font.TextAttribute;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.text.AttributedString;
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
public class GameScreen implements PositionChangeListener, IGameScreen {

	
	/**
	 * Comparator used to sort entities to display.
	 */
	protected static final EntityViewComparator entityViewComparator = new EntityViewComparator();

	/**
	 * A scale factor for panning delta (to allow non-float precision).
	 */
	protected static final int PAN_SCALE = 8;
	
	/** the logger instance. */
	private static final Logger LOGGER = Logger.getLogger(GameScreen.class);

	private static final Sprite offlineIcon;

	/** the singleton instance. */
	private static IGameScreen screen;

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

	private final BufferStrategy strategy;

	private Graphics2D g;

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
	private final int sw;
	private final int sh;

	


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
	 * The pan speed.
	 */
	private int speed;

	static {
		offlineIcon = SpriteStore.get().getSprite("data/gui/offline.png");
	}



	/**
	 * Create a game screen.
	 *
	 * @param client
	 *            The client.
	 * @param canvas
	 *            The canvas to render in.
	 */
	public GameScreen(final StendhalClient client, final Canvas canvas) {
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

		/*
		 * Create the buffering strategy which will allow AWT to manage our
		 * accelerated graphics
		 */
		canvas.createBufferStrategy(2);
		strategy = canvas.getBufferStrategy();

		g = (Graphics2D) strategy.getDrawGraphics();
	}

	/**
	 * Set the default [singleton] screen.
	 *
	 * @param screen
	 *            The screen.
	 */
	public static void setDefaultScreen(final IGameScreen screen) {
		GameScreen.screen = screen;
	}

	/** @return the GameScreen object. */
	public static IGameScreen get() {
		return screen;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#getViewWidth()
	 */
	public double getViewWidth() {
		return sw / SIZE_UNIT_PIXELS;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#getViewHeight()
	 */
	public double getViewHeight() {
		return sh / SIZE_UNIT_PIXELS;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#nextFrame()
	 */
	public void nextFrame() {
		g.dispose();
		strategy.show();

		adjustView();

		g = (Graphics2D) strategy.getDrawGraphics();
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
		final EntityView view = createView(entity);

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
	protected void addEntityView(final EntityView view) {
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
	protected void removeEntityView(final EntityView view) {
		view.release(this);
		views.remove(view);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#expose()
	 */
	public Graphics2D expose() {
		return g;
	}

	/**
	 * Update the view position to center the target position.
	 */
	protected void adjustView() {
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
			final int dux = dvx / 40;
			final int duy = dvy / 40;

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
					if (dx == 0) {
						dx = -1;
					} else if (dx < dvx) {
						dx = dvx;
					}
				} else if (dvx > 0) {
					if (dx == 0) {
						dx = 1;
					} else if (dx > dvx) {
						dx = dvx;
					}
				}

				if (dvy < 0) {
					if (dy == 0) {
						dy = -1;
					} else if (dy < dvy) {
						dy = dvy;
					}
				} else if (dvy > 0) {
					if (dy == 0) {
						dy = 1;
					} else if (dy > dvy) {
						dy = dvy;
					}
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
	 *
	 */
	protected void calculateView() {
		int cvx = (x * SIZE_UNIT_PIXELS) + (SIZE_UNIT_PIXELS / 2) - (sw / 2);
		int cvy = (y * SIZE_UNIT_PIXELS) + (SIZE_UNIT_PIXELS / 2) - (sh / 2);

		/*
		 * Keep the world with-in the screen view
		 */
		if (cvx < 0) {
			cvx = 0;
		} else {
			final int max = (ww * SIZE_UNIT_PIXELS) - sw;

			if (cvx > max) {
				cvx = max;
			}
		}

		if (cvy < 0) {
			cvy = 0;
		} else {
			final int max = (wh * SIZE_UNIT_PIXELS) - sh;

			if (cvy > max) {
				cvy = max;
			}
		}

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

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#createView(games.stendhal.client.entity.Entity)
	 */
	public EntityView createView(final IEntity entity) {
		return  EntityViewFactory.get().create(entity);
	}

	/*
	 * Draw the screen.
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#draw()
	 */
	public void draw() {
		Collections.sort(views, entityViewComparator);

		/*
		 * Draw the GameLayers from bootom to top, relies on exact naming of the
		 * layers
		 */
		final String set = gameLayers.getAreaName();

		final int xTemp = (int) getViewX();
		final int yTemp = (int) getViewY();
		final int w = (int) getViewWidth();
		final int h = (int) getViewHeight();

		/*
		 * End of the world (map falls short of the view)?
		 */
		int px = convertWorldXToScreenView(Math.max(xTemp, 0));

		if (px > 0) {
			g.setColor(Color.black);
			g.fillRect(0, 0, px, sh);
		}

		px = convertWorldXToScreenView(Math.min(xTemp + w, ww));

		if (px < sw) {
			g.setColor(Color.black);
			g.fillRect(px, 0, sw - px, sh);
		}

		int py = convertWorldYToScreenView(Math.max(yTemp, 0));

		if (py > 0) {
			g.setColor(Color.black);
			g.fillRect(0, 0, sw, py);
		}

		py = convertWorldYToScreenView(Math.min(yTemp + h, wh));

		if (py < sh) {
			g.setColor(Color.black);
			g.fillRect(0, py, sw, sh - py);
		}

		/*
		 * Layers
		 */
		gameLayers.draw(this, set, "0_floor", xTemp, yTemp, w, h);
		gameLayers.draw(this, set, "1_terrain", xTemp, yTemp, w, h);
		gameLayers.draw(this, set, "2_object", xTemp, yTemp, w, h);
		drawEntities();
		gameLayers.draw(this, set, "3_roof", xTemp, yTemp, w, h);
		gameLayers.draw(this, set, "4_roof_add", xTemp, yTemp, w, h);
		drawTopEntities();
		drawText();

		/*
		 * Dialogs
		 */
		ground.draw(g, this);

		/*
		 * Offline
		 */
		if (offline && (blinkOffline > 0)) {
			offlineIcon.draw(g, 560, 420);
		}

		if (blinkOffline < -10) {
			blinkOffline = 20;
		} else {
			blinkOffline--;
		}
	}

	/**
	 * Draw the screen entities.
	 */
	protected void drawEntities() {
		final Graphics2D g2d = expose();

		for (final EntityView view : views) {
			view.draw(g2d, this);
		}
	}

	/**
	 * Draw the top portion screen entities (such as HP/title bars).
	 */
	protected void drawTopEntities() {
		final Graphics2D g2d = expose();

		for (final EntityView view : views) {
			view.drawTop(g2d, this);
		}
	}

	/**
	 * Draw the screen text bubbles.
	 */
	protected void drawText() {
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

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#getViewX()
	 */
	public double getViewX() {
		return (double) getScreenViewX() / SIZE_UNIT_PIXELS;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#getViewY()
	 */
	public double getViewY() {
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

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#addText(double, double,
	 *      java.lang.String, java.awt.Color, boolean)
	 */
	public void addText(final double x, final double y, final String text,
			final Color color, final boolean talking) {
		addText(convertWorldToScreen(x), convertWorldToScreen(y), text, color,
				talking);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#addText(int, int,
	 *      java.lang.String, games.stendhal.client.NotificationType, boolean)
	 */
	public void addText(final int sx, final int sy, final String text,
			final NotificationType type, final boolean talking) {
		addText(sx, sy, text, type.getColor(), talking);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#addText(int, int,
	 *      java.lang.String, java.awt.Color, boolean)
	 */
	public void addText(int sx, int sy, final String text, final Color color,
			final boolean isTalking) {
		final Sprite sprite = createTextBox(text, 240, color, Color.white, isTalking);

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
	 * @see games.stendhal.client.IGameScreen#clear()
	 */
	public void clear() {
		g.setColor(Color.black);
		g.fillRect(0, 0, getScreenViewWidth(), getScreenViewHeight());
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

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#convertWorldXToScreenView(double)
	 */
	public int convertWorldXToScreenView(final double wx) {
		return convertWorldToScreen(wx) - svx;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#convertWorldYToScreenView(double)
	 */
	public int convertWorldYToScreenView(final double wy) {
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
	 * @see games.stendhal.client.IGameScreen#convertWorldToScreenView(java.awt.geom.Rectangle2D)
	 */
	public Rectangle convertWorldToScreenView(final Rectangle2D wrect) {
		return convertWorldToScreenView(wrect.getX(), wrect.getY(),
				wrect.getWidth(), wrect.getHeight());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#convertWorldToScreenView(double,
	 *      double, double, double)
	 */
	public Rectangle convertWorldToScreenView(final double wx, final double wy,
			final double wwidth, final double wheight) {
		return new Rectangle(convertWorldXToScreenView(wx),
				convertWorldYToScreenView(wy),
				(int) (wwidth * SIZE_UNIT_PIXELS),
				(int) (wheight * SIZE_UNIT_PIXELS));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#isInScreen(java.awt.Rectangle)
	 */
	public boolean isInScreen(final Rectangle srect) {
		return isInScreen(srect.x, srect.y, srect.width, srect.height);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#isInScreen(int, int, int, int)
	 */
	public boolean isInScreen(final int sx, final int sy, final int swidth, final int sheight) {
		return (((sx >= -swidth) && (sx < sw)) && ((sy >= -sheight) && (sy < sh)));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#draw(games.stendhal.client.sprite.Sprite,
	 *      double, double)
	 */
	public void draw(final Sprite sprite, final double wx, final double wy) {
		final Point p = convertWorldToScreenView(wx, wy);

		if (sprite != null) {
			final int spritew = sprite.getWidth() + 2;
			final int spriteh = sprite.getHeight() + 2;

			if (((p.x >= -spritew) && (p.x < sw))
					&& ((p.y >= -spriteh) && (p.y < sh))) {
				sprite.draw(g, p.x, p.y);
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
		sprite.draw(g, sx, sy);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#createString(java.lang.String,
	 *      games.stendhal.client.NotificationType)
	 */
	public Sprite createString(final String text, final NotificationType type) {
		return createString(text, type.getColor());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#createString(java.lang.String,
	 *      java.awt.Color)
	 */
	public Sprite createString(final String text, final Color textColor) {
		final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		final Image image = gc.createCompatibleImage(g.getFontMetrics().stringWidth(
				text) + 2, 16, Transparency.BITMASK);
		final Graphics g2d = image.getGraphics();

		drawOutlineString(g2d, textColor, text, 1, 10);

		return new ImageSprite(image);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#drawOutlineString(java.awt.Graphics,
	 *      java.awt.Color, java.lang.String, int, int)
	 */
	public void drawOutlineString(final Graphics g, final Color textColor,
			final String text, final int x, final int y) {
		/*
		 * Use light gray as outline for colors < 25% bright. Luminance = 0.299R +
		 * 0.587G + 0.114B
		 */
		final int lum = ((textColor.getRed() * 299) + (textColor.getGreen() * 587) + (textColor.getBlue() * 114)) / 1000;

		Color outlineColor;
		if (lum >= 64) {
			outlineColor = Color.black;
		} else {
			outlineColor = Color.lightGray;
		}
		drawOutlineString(g, textColor, outlineColor, text, x, y);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#drawOutlineString(java.awt.Graphics,
	 *      java.awt.Color, java.awt.Color, java.lang.String, int, int)
	 */
	public void drawOutlineString(final Graphics g, final Color textColor,
			final Color outlineColor, final String text, final int x,
			final int y) {
		g.setColor(outlineColor);
		g.drawString(text, x - 1, y - 1);
		g.drawString(text, x - 1, y + 1);
		g.drawString(text, x + 1, y - 1);
		g.drawString(text, x + 1, y + 1);

		g.setColor(textColor);
		g.drawString(text, x, y);
	}

	private int positionStringOfSize(final String text, final int width) {
		final String[] words = text.split("\\s+");

		int i = 1;
		// Bugfix: Prevent NPE for empty text intensifly@gmx.com
		String textUnderWidth = "";
		if (words != null) {
			textUnderWidth = words[0];
		}

		while ((i < words.length)
				&& (g.getFontMetrics().stringWidth(
						textUnderWidth + " " + words[i]) < width)) {
			textUnderWidth = textUnderWidth + " " + words[i];
			i++;
		}

		if ((textUnderWidth.length() == 0) && (words.length > 1)) {
			textUnderWidth = words[1];
		}

		if (g.getFontMetrics().stringWidth(textUnderWidth) > width) {
			return (int) ((float) width
					/ (float) g.getFontMetrics().stringWidth(textUnderWidth) * textUnderWidth.length());
		}

		return textUnderWidth.length();
	}

	// Added support formatted text displaying #keywords in another color
	// intensifly@gmx.com
	// ToDo: optimize the alghorithm, it's a little long ;)

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#formatLine(java.lang.String,
	 *      java.awt.Font, java.awt.Color)
	 */
	public AttributedString formatLine(final String line,
				final Font fontNormal, final Color colorNormal) {
		final Font specialFont = fontNormal.deriveFont(Font.ITALIC);

		try {
			// recreate the string without the # characters
			final StringBuilder temp = new StringBuilder();
			FormatTextParser parser = new FormatTextParserExtension(temp);
			parser.format(line);

			// create the attribute string including formating
			final AttributedString aStyledText = new AttributedString(temp.toString());

			parser = new FormatTextParser() {
				private int s = 0;

				@Override
				public void normalText(final String tok) {
					aStyledText.addAttribute(TextAttribute.FONT, fontNormal, s, s
							+ tok.length() + 1);
					aStyledText.addAttribute(TextAttribute.FOREGROUND, colorNormal, s, s
							+ tok.length() + 1);
					s += tok.length() + 1;
				}

				@Override
				public void colorText(final String tok) {
					aStyledText.addAttribute(TextAttribute.FONT, specialFont, s, s
							+ tok.length() + 1);
					aStyledText.addAttribute(TextAttribute.FOREGROUND, Color.blue, s, s
							+ tok.length() + 1);
					s += tok.length() + 1;
				}
			};
			parser.format(line);

			return aStyledText;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#createTextBox(java.lang.String,
	 *      int, java.awt.Color, java.awt.Color, boolean)
	 */
	public Sprite createTextBox(String text, final int width, final Color textColor,
			final Color fillColor, final boolean isTalking) {
		final java.util.List<String> lines = new java.util.LinkedList<String>();

		int i = 0;
		// Added support for speech balloons. If drawn, they take 10 pixels from
		// the left. intensifly@gmx.com

		int delta = 0;

		if (fillColor != null) {
			delta = 10;
		}
		text = text.trim();
		while (text.length() > 0) {
			int pos = positionStringOfSize(text, width - delta);

			/*
			 * Hard line breaks
			 */
			final int nlpos = text.indexOf('\n', 1);
			if ((nlpos != -1) && (nlpos < pos)) {
				pos = nlpos;
			}

			lines.add(text.substring(0, pos).trim());
			text = text.substring(pos);
			i++;
		}

		final int numLines = lines.size();
		int lineLengthPixels = 0;

		for (final String line : lines) {
			final int lineWidth = g.getFontMetrics().stringWidth(line);
			if (lineWidth > lineLengthPixels) {
				lineLengthPixels = lineWidth;
			}
		}

		final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		final int imageWidth;
		if (lineLengthPixels + delta < width) {
			imageWidth = (lineLengthPixels + delta) + 4;
		} else {
			imageWidth = width + 4;
		}
		
		int imageHeight = 16 * numLines;

		// Workaround for X-Windows not supporting images of height 0 pixel.
		if (imageHeight == 0) {
			imageHeight = 1;
			LOGGER.warn("Created textbox for empty text");
		}

		final Image image = gc.createCompatibleImage(imageWidth, imageHeight,
				Transparency.BITMASK);

		final Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (fillColor != null) {
			final Composite xac = g2d.getComposite();
			final AlphaComposite ac = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.8f);
			g2d.setComposite(ac);
			g2d.setColor(fillColor);
			
			g2d.fillRoundRect(
					10,
					0,
					Math.min(width, lineLengthPixels) + 3,
					16 * numLines - 1, 4, 4);
			g2d.setColor(textColor);
			if (isTalking) {
				g2d.drawRoundRect(
						10,
						0,
						Math.min(width, lineLengthPixels) + 3,
						16 * numLines - 1, 4, 4);
			} else {
				final float[] dash = { 4, 2 };
				final BasicStroke newStroke = new BasicStroke(2,
						BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1, dash,
						0);
				final Stroke oldStroke = g2d.getStroke();
				g2d.setStroke(newStroke);
				g2d.drawRect(
						10,
						0,
						Math.min(width, lineLengthPixels) + 3,
						16 * numLines - 1);
				g2d.setStroke(oldStroke);
			}
			g2d.setComposite(xac);
			if (isTalking) {
				g2d.setColor(fillColor);
				final Polygon p = new Polygon();
				p.addPoint(10, 3);
				p.addPoint(0, 16);
				p.addPoint(11, 12);
				g2d.fillPolygon(p);
				g2d.setColor(textColor);
				p.addPoint(0, 16);
				g2d.drawPolygon(p);
			}
		}

		i = 0;
		for (final String line : lines) {
			final AttributedString aStyledText = formatLine(line, g2d.getFont(), textColor);

			if (fillColor == null) {
				g2d.setColor(Color.black);
				g2d.drawString(aStyledText.getIterator(), 1, 2 + i * 16 + 9);
				g2d.drawString(aStyledText.getIterator(), 1, 2 + i * 16 + 11);
				g2d.drawString(aStyledText.getIterator(), 3, 2 + i * 16 + 9);
				g2d.drawString(aStyledText.getIterator(), 3, 2 + i * 16 + 11);
			}

			g2d.setColor(textColor);

			g2d.drawString(aStyledText.getIterator(), 2 + delta,
					2 + i * 16 + 10);
			i++;
		}

		return new ImageSprite(image);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#convertWorldToScreen(double)
	 */
	public int convertWorldToScreen(final double w) {
		return (int) (w * SIZE_UNIT_PIXELS);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#convertScreenToWorld(int, int)
	 */
	public Point2D convertScreenToWorld(final int x, final int y) {
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

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#getScreenHeight()
	 */
	public int getScreenHeight() {
		return convertWorldToScreen(wh);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#getScreenWidth()
	 */
	public int getScreenWidth() {
		return convertWorldToScreen(ww);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#getScreenViewHeight()
	 */
	public int getScreenViewHeight() {
		return sh;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#getScreenViewWidth()
	 */
	public int getScreenViewWidth() {
		return sw;
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

	public static class EntityViewComparator implements
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
