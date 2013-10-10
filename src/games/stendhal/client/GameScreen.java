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

import games.stendhal.client.entity.Corpse;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Item;
import games.stendhal.client.gui.DropTarget;
import games.stendhal.client.gui.GroundContainer;
import games.stendhal.client.gui.j2d.AchievementBoxFactory;
import games.stendhal.client.gui.j2d.RemovableSprite;
import games.stendhal.client.gui.j2d.entity.Entity2DView;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.spellcasting.SpellCastingGroundContainerMouseState;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.VolatileImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * The game screen. This manages and renders the visual elements of the game.
 */
public class GameScreen extends JComponent implements IGameScreen, DropTarget,
	GameObjects.GameObjectListener, StendhalClient.ZoneChangeListener {
	/**
	 * serial version uid.
	 */
	private static final long serialVersionUID = -4070406295913030925L;

	private static final Logger logger = Logger.getLogger(GameScreen.class);
	
	/** Map KeyEvents to a number, i.e. to determine position in spells slot based on pressed key **/
	private static final Map<Integer, Integer> keyEventMapping = new HashMap<Integer, Integer>();
	static {
		keyEventMapping.put(KeyEvent.VK_1, Integer.valueOf(1));
		keyEventMapping.put(KeyEvent.VK_2, Integer.valueOf(2));
		keyEventMapping.put(KeyEvent.VK_3, Integer.valueOf(3));
		keyEventMapping.put(KeyEvent.VK_4, Integer.valueOf(4));
		keyEventMapping.put(KeyEvent.VK_5, Integer.valueOf(5));
		keyEventMapping.put(KeyEvent.VK_6, Integer.valueOf(6));
		keyEventMapping.put(KeyEvent.VK_7, Integer.valueOf(7));
		keyEventMapping.put(KeyEvent.VK_8, Integer.valueOf(8));
		keyEventMapping.put(KeyEvent.VK_9, Integer.valueOf(9));
		keyEventMapping.put(KeyEvent.VK_0, Integer.valueOf(10));
	}

	/**
	 * A scale factor for panning delta (to allow non-float precision).
	 */
	private static final int PAN_SCALE = 8;
	/**
	 * Speed factor for centering the screen. Smaller is faster,
	 * and keeps the player closer to the center of the screen when walking.
	 */
	private static final int PAN_INERTIA = 15;
	/**
	 * Space at the right and bottom of the screen next to the off line
	 * indicator icon.
	 */
	private static final int OFFLINE_MARGIN = 10;

	private static final Sprite offlineIcon;

	/** the singleton instance. */
	private static GameScreen screen;

	/**
	 * Static game layers.
	 */
	private final StaticGameLayers gameLayers;
	
	/** Entity views container. */
	private final EntityViewManager viewManager = new EntityViewManager();

	/** Actual width of the world in world units. */
	private int ww;
	/** Actual height of the world in world units. */
	private int wh;

	/**
	 * The ground layer.
	 */
	private final GroundContainer ground;

	/**
	 * The text bubbles.
	 */
	private final List<RemovableSprite> texts;
	
	/**
	 * Text boxes that are anchored to the screen coordinates.
	 */
	private final List<RemovableSprite> staticSprites;

	private boolean offline;

	/**
	 * Off line indicator counter. 
	 */
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
	
	/**
	 * Scaling factor of the screen.
	 */
	private double scale = 1.0;
	/**
	 * Flag for telling if the screen should be scaled if it's not of the
	 * default size.
	 */
	private boolean useScaling = true;
	/**
	 * A flag for telling if the screen is being actually scaled. Used for
	 * detecting if the ground layers will need triple buffering.
	 */
	private boolean useTripleBuffer;
	/**
	 * Buffer for drawing the ground layers when the screen is scaled.
	 */
	private VolatileImage buffer;

	static {
		offlineIcon = SpriteStore.get().getSprite("data/gui/offline.png");
	}

	private AchievementBoxFactory achievementBoxFactory;

	/**
	 * Create a game screen.
	 *
	 * @param client
	 *            The client.
	 */
	public GameScreen(final StendhalClient client) {
		setSize(stendhal.getScreenSize());
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				onResized();
			}
		});
		
		gameLayers = client.getStaticGameLayers();

		sw = getWidth();
		sh = getHeight();

		x = 0;
		y = 0;
		svx = -sw / 2;
		svy = -sh / 2;
		dvx = 0;
		dvy = 0;

		speed = 0;

		// Drawing is done in EDT
		texts = Collections.synchronizedList(new LinkedList<RemovableSprite>());
		staticSprites = Collections.synchronizedList(new LinkedList<RemovableSprite>());

		// create ground
		ground = new GroundContainer(client, this, this);

		// register native event handler
		addMouseListener(ground);
		addMouseWheelListener(ground);
		addMouseMotionListener(ground);
		/*
		 * Ignore OS level repaint requests to help systems that create too
		 * many of those. In game DnD is done within AWT so that is not
		 * affected.
		 */
		setIgnoreRepaint(true);
		client.getGameObjects().addGameObjectListener(this);
	}
	
	/**
	 * The canvas can be resized using a split pane, or with window size changes
	 * if screen scaling is used. This is for adjusting the internal parameters
	 * for the change. 
	 */
	private void onResized() {
		Dimension screenSize = stendhal.getScreenSize();
		sw = getWidth();
		sh = getHeight();
		if (useScaling) {
			double xScale = sw / screenSize.getWidth();
			double yScale = sh / screenSize.getHeight();
			// Scale by the dimension that needs more scaling
			scale = Math.max(xScale, yScale);
			if (Math.abs(scale - 1.0) > 0.0001) {
				useTripleBuffer = true;
			} else {
				useTripleBuffer = false;
				buffer = null;
			}
		} else {
			sw = Math.min(sw, screenSize.width);
			sh = Math.min(sh, screenSize.height);
			useTripleBuffer = false;
			buffer = null;
		}
		// Reset the view so that the player is in the center
		calculateView(x, y);
		center();
	}
	
	/**
	 * Set whether the screen should be drawn scaled, or in native resolution.
	 *  
	 * @param useScaling if <code>true</code> the screen will scale the view
	 * 	will be scaled to fit the screen size, otherwise it will be drawn using
	 * 	the native resolution.
	 */
	public void setUseScaling(boolean useScaling) {
		this.useScaling = useScaling;
		if (!useScaling) {
			scale = 1.0;
			useTripleBuffer = false;
			buffer = null;
		} else {
			onResized();
		}
	}
	
	/**
	 * Check if the screen uses scaling. Note that if the native resolution
	 * is in use, the screen size <b>must not</b> be allowed to grow larger than
	 * <code>standhal.getScreenSize()</code>.
	 * 
	 * @return <code>true</code> if the graphics are scaled to the screen size,
	 * 	<code>false</code> if the native resolution is used
	 */
	public boolean isScaled() {
		return useScaling;
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
		return (int) Math.ceil(sw / (SIZE_UNIT_PIXELS * scale));
	}

	/** @return screen height in world units .*/
	private int getViewHeight() {
		return (int) Math.ceil(sh / (SIZE_UNIT_PIXELS * scale));
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#nextFrame()
	 */
	@Override
	public void nextFrame() {
		adjustView();
	}
	
	/**
	 * Get the achievement box factory.
	 * 
	 * @return factory
	 */
	private AchievementBoxFactory getAchievementFactory() {
		if (achievementBoxFactory == null) {
			achievementBoxFactory = new AchievementBoxFactory();
		}
		return achievementBoxFactory;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#addEntity(games.stendhal.client.entity.Entity)
	 */
	@Override
	public void addEntity(final IEntity entity) {
		EntityView<IEntity> view = viewManager.addEntity(entity);
		if (view != null) {
			if (view instanceof Entity2DView) {
				final Entity2DView<?> inspectable = (Entity2DView<?>) view;
				
				inspectable.setInspector(ground);
			}
			if (entity.isUser()) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						center();
					}
				});
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#removeEntity(games.stendhal.client.entity.Entity)
	 */
	@Override
	public void removeEntity(final IEntity entity) {
		viewManager.removeEntity(entity);
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

		final int sx = convertWorldXToScaledScreen(x) - getScreenViewX() + SIZE_UNIT_PIXELS / 2;
		final int sy = convertWorldYToScaledScreen(y) - getScreenViewY() + SIZE_UNIT_PIXELS / 2;

		if ((sx < 0) || (sx >= sw) || (sy < -SIZE_UNIT_PIXELS) || (sy > sh)) {
			/*
			 * If off screen, just center
			 */
			center();
		} else {
			calculatePanningSpeed();

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
	 * Calculate the target speed for moving the view position. The farther
	 * away, the faster.
	 */
	private void calculatePanningSpeed() {
		final int dux = dvx / PAN_INERTIA;
		final int duy = dvy / PAN_INERTIA;

		final int tspeed = ((dux * dux) + (duy * duy)) * PAN_SCALE;

		if (speed > tspeed) {
			speed = (2 * speed + tspeed) / 3;

			/*
			 * Don't stall
			 */
			if ((dvx != 0) || (dvy != 0)) {
				speed = Math.max(speed, 1);
			}
		} else if (speed < tspeed) {
			speed += 2;
		}
	}

	/**
	 * Updates the target position of the view center.
	 * Prefer top left if the map is smaller than the screen.
	 * 
	 * @param x preferred x of center, if the map is large enough
	 * @param y preferred y of center, if the map is large enough
	 */
	private void calculateView(int x, int y) {
		// Coordinates for a screen centered on player
		int cvx = (int) ((x * SIZE_UNIT_PIXELS) + (SIZE_UNIT_PIXELS / 2) - (sw / 2) / scale);
		int cvy = (int) ((y * SIZE_UNIT_PIXELS) + (SIZE_UNIT_PIXELS / 2) - (sh / 2) / scale);

		/*
		 * Keep the world within the screen view
		 */
		final int maxX = (int) (ww * SIZE_UNIT_PIXELS - sw / scale);
		cvx = Math.min(cvx, maxX);
		cvx = Math.max(cvx, 0);
		
		final int maxY = (int) (wh * SIZE_UNIT_PIXELS - sh / scale);
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
	@Override
	public void center() {
		svx += dvx;
		svy += dvy;

		dvx = 0;
		dvy = 0;

		speed = 0;
	}
	
	@Override
	public void paintImmediately(int x, int y, int w, int h) {
		/*
		 * Try to keep the old screen while the user is switching maps.
		 * 
		 * NOTE: Relies on the repaint() requests to eventually come to this,
		 * so if swing internals change some time in the future, a new solution
		 * may be needed.
		 */
		if (StendhalClient.get().tryAcquireDrawingSemaphore()) {
			try {
				super.paintImmediately(x, y, w, h);
			} finally {
				StendhalClient.get().releaseDrawingSemaphore();
			}
		}
	}
	
	@Override
	public void paintComponent(final Graphics g) {
		if (StendhalClient.get().isInTransfer()) {
			/*
			 * A hack to prevent proper drawing during zone change when the draw
			 * request comes from paintChildren() of the parent. Those are not
			 * caught by the paintImmediately() wrapper. Prevents entity view
			 * images from being initialized before zone coloring is ready.
			 */
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
			return;
		}
		
		Graphics2D g2d = (Graphics2D) g;
		
		Graphics2D graphics = (Graphics2D) g2d.create();
		if (graphics.getClipBounds() == null) {
			graphics.setClip(0, 0, getWidth(), getHeight());
		}
		int xAdjust = -getScreenViewX();
		int yAdjust = -getScreenViewY();

		if (useTripleBuffer) {
			/*
			 * Do the scaling in one pass to avoid artifacts at tile borders.
			 */
			graphics.scale(scale, scale);
			graphics.translate(xAdjust, yAdjust);
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			int width = stendhal.getScreenSize().width;
			int height = stendhal.getScreenSize().height;
			do {
				GraphicsConfiguration gc = getGraphicsConfiguration();
				if ((buffer == null) || (buffer.validate(gc) == VolatileImage.IMAGE_INCOMPATIBLE)) {
					buffer = createVolatileImage(width, height);
				}
				Graphics2D gr = buffer.createGraphics();
				gr.setClip(0, 0, width, height);
				renderScene(gr, xAdjust, yAdjust);
				graphics.drawImage(buffer, -xAdjust, -yAdjust, null);
				gr.dispose();
			} while (buffer.contentsLost());
		} else {
			renderScene(graphics, xAdjust, yAdjust);
		}
		
		// Don't scale text to keep it readable
		drawText(g2d);

		paintOffLineIfNeeded(g2d);
		graphics.dispose();
	}
	
	/**
	 * Render the scalable parts of the screen.
	 * 
	 * @param g graphics
	 * @param xAdjust x coordinate offset
	 * @param yAdjust y coordinate offset
	 */
	private void renderScene(Graphics2D g, int xAdjust, int yAdjust) {
		// Adjust the graphics object so that the drawn objects do not need to
		// know about converting the position to screen
		g.translate(xAdjust, yAdjust);
		
		// Restrict the drawn area by the clip bounds. Smaller than gamescreen
		// draw requests can come for example from dragging items
		int startTileX = Math.max(0, (int) getViewX());
		int startTileY = Math.max(0, (int) getViewY());
		
		Rectangle clip = g.getClipBounds();
		startTileX = Math.max(startTileX, clip.x / IGameScreen.SIZE_UNIT_PIXELS);
		startTileY = Math.max(startTileY, clip.y / IGameScreen.SIZE_UNIT_PIXELS);
		int layerWidth = getViewWidth();
		int layerHeight = getViewHeight();
		// +2 is needed to ensure the drawn area is covered by the tiles
		layerWidth = Math.min(layerWidth, clip.width / IGameScreen.SIZE_UNIT_PIXELS) + 2;
		layerHeight = Math.min(layerHeight, clip.height / IGameScreen.SIZE_UNIT_PIXELS) + 2;

		drawEndOfTheWorld(g, xAdjust, yAdjust);
		viewManager.prepareViews(clip);
		
		final String set = gameLayers.getAreaName();
		gameLayers.drawLayers(g, set, "floor_bundle", startTileX,
				startTileY, layerWidth, layerHeight, "blend_ground", "0_floor",
				"1_terrain", "2_object");
		
		viewManager.draw(g);

		gameLayers.drawLayers(g, set, "roof_bundle", startTileX,
				startTileY, layerWidth, layerHeight, "blend_roof", "3_roof",
				"4_roof_add");
		
		// Draw the top portion screen entities (such as HP/title bars).
		viewManager.drawTop(g);
	}
	
	/**
	 * Fill with black the areas outside the map.
	 * 
	 * @param g graphics
	 * @param xAdjust x position of the screen
	 * @param yAdjust y position of the screen
	 */
	private void drawEndOfTheWorld(Graphics g, int xAdjust, int yAdjust) {
		// End of the world (map falls short of the view)?
		if (xAdjust > 0) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, xAdjust, sh);
		}

		if (yAdjust > 0) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, sw, yAdjust);
		}

		int tmpY = yAdjust + convertWorldToPixelUnits(wh);
		if (tmpY < sh) {
			g.setColor(Color.BLACK);
			g.fillRect(svx, tmpY, sw, sh);
		}

		int tmpX = yAdjust + convertWorldToPixelUnits(ww);
		if (tmpX < sw) {
			g.setColor(Color.BLACK);
			g.fillRect(tmpX, svy, sw, sh);
		}
	}
	
	/**
	 * Draw the offline indicator, blinking, if the client is offline.
	 * 
	 * @param g graphics
	 */
	private void paintOffLineIfNeeded(Graphics g) {
		// Offline
		if (offline) {
			if (blinkOffline > 0) {
				offlineIcon.draw(g, getWidth() - offlineIcon.getWidth() - OFFLINE_MARGIN,
						getHeight() - offlineIcon.getHeight() - OFFLINE_MARGIN);
			}

			// Show for 20 screen draws, hide for 12
			if (blinkOffline < -10) {
				blinkOffline = 20;
			} else {
				blinkOffline--;
			}
		}
	}

	/**
	 * Draw the screen text bubbles.
	 * 
	 * @param g2d destination graphics
	 */
	private void drawText(final Graphics2D g2d) {
		/*
		 * Text objects know their original placement relative to the screen,
		 * not to the map. Pass them a shifted coordinate system. 
		 */
		g2d.translate(-getScreenViewX(), -getScreenViewY());
		
		synchronized (texts) {
			Iterator<RemovableSprite> it = texts.iterator();
			while (it.hasNext()) {
				RemovableSprite text = it.next();
				if (!text.shouldBeRemoved()) {
					text.draw(g2d);
				} else {
					it.remove();
				}
			}
		}
		
		// Restore the coordinates
		g2d.translate(getScreenViewX(), getScreenViewY());
		// These are anchored to the screen, so they can use the usual proper
		// coordinates.
		synchronized (staticSprites) {
			Iterator<RemovableSprite> it = staticSprites.iterator();
			while (it.hasNext()) {
				RemovableSprite text = it.next();
				if (!text.shouldBeRemoved()) {
					text.draw(g2d);
				} else {
					it.remove();
				}
			}
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
	@Override
	public void setMaxWorldSize(final double width, final double height) {
		ww = (int) width;
		wh = (int) height;
		calculateView(x, y);
		center();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#setOffline(boolean)
	 */
	@Override
	public void setOffline(final boolean offline) {
		this.offline = offline;
	}
	
	/**
	 * Adds a text bubble at a give position.
	 * 
	 * @param sprite 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param textLength length of the text in characters
	 */
	public void addTextBox(Sprite sprite, double x, double y, int textLength) {
		int sx = convertWorldXToScaledScreen(x);
		int sy = convertWorldYToScaledScreen(y);
		// Point alignment: left, bottom
		sy -= sprite.getHeight();

		sx = keepSpriteOnMapX(sprite, sx);
		sy = keepSpriteOnMapY(sprite, sy);
		
		/*
		 * Adjust the position of boxes placed at the same point to make it
		 * clear for the player there are more than one.
		 */
		boolean found = true;
		int tries = 0;

		while (found) {
			found = false;

			synchronized (texts) {
				for (final RemovableSprite item : texts) {
					if ((item.getX() == sx) && (item.getY() == sy)) {
						found = true;
						sy += SIZE_UNIT_PIXELS / 2;
						sy = keepSpriteOnMapY(sprite, sy);
						break;
					}
				}
			}
			
			tries++;
			// give up, if no location found in a reasonable amount of tries
			if (tries > 20) {
				break;
			}
		}

		texts.add(new RemovableSprite(sprite, sx, sy, Math.max(
				RemovableSprite.STANDARD_PERSISTENCE_TIME, textLength
						* RemovableSprite.STANDARD_PERSISTENCE_TIME / 50)));
	}

	/**
	 * Try to keep a sprite on the map. Adjust the Y coordinate.
	 * 
	 * @param sprite sprite to keep on the map
	 * @param sy suggested Y coordinate on screen
	 * @return new Y coordinate
	 */
	private int keepSpriteOnMapY(Sprite sprite, int sy) {
		sy = Math.max(sy, 0);
		/*
		 * Allow placing beyond the map, but only if the area is on the screen.
		 * Do not try to adjust the coordinates if the world size is not known
		 * yet (as in immediately after a zone change)
		 */
		if (wh != 0) {
			sy = Math.min(sy, Math.max(getHeight() + svy,
					convertWorldYToScaledScreen(wh)) - sprite.getHeight());
		}
		return sy;
	}

	/**
	 * Try to keep a sprite on the map. Adjust the X coordinate.
	 * 
	 * @param sprite sprite to keep on the map
	 * @param sx suggested X coordinate on screen
	 * @return new X coordinate
	 */
	private int keepSpriteOnMapX(Sprite sprite, int sx) {
		sx = Math.max(sx, 0);
		/*
		 * Allow placing beyond the map, but only if the area is on the screen.
		 * Do not try to adjust the coordinates if the world size is not known
		 * yet (as in immediately after a zone change)
		 */
		if (ww != 0) {
			sx = Math.min(sx, Math.max(getWidth() + svx, convertWorldXToScaledScreen(ww)) - sprite.getWidth());
		}
		return sx;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#removeText(games.stendhal.client.gui.j2d.Text)
	 */
	@Override
	public void removeText(final RemovableSprite entity) {
		texts.remove(entity);
		staticSprites.remove(entity);
	}

	/**
	 * Remove all map objects.
	 */
	private void removeAllObjects() {
		logger.debug("CLEANING screen object list");
		texts.clear();
		// staticSprites contents are not zone specific, so don't clear those
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#clearTexts()
	 */
	@Override
	public void clearTexts() {
		texts.clear();
		staticSprites.clear();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#getEntityViewAt(double, double)
	 */
	@Override
	public EntityView<?> getEntityViewAt(final double x, final double y) {
		final int sx = convertWorldToPixelUnits(x);
		final int sy = convertWorldToPixelUnits(y);
		return viewManager.getEntityViewAt(x, y, sx, sy);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#getMovableEntityViewAt(double,
	 *      double)
	 */
	@Override
	public EntityView<?> getMovableEntityViewAt(final double x, final double y) {
		final int sx = convertWorldToPixelUnits(x);
		final int sy = convertWorldToPixelUnits(y);
		return viewManager.getMovableEntityViewAt(x, y, sx, sy);
	}

	/*
	 * (non-Javadoc)
	 * @see games.stendhal.client.IGameScreen#getTextAt(int, int)
	 */
	@Override
	public RemovableSprite getTextAt(final int x, final int y) {
		// staticTexts are drawn on top of the others; those in the end of the
		// lists are above preceding texts
		synchronized (staticSprites) {
			final ListIterator<RemovableSprite> it = staticSprites.listIterator(staticSprites.size());

			while (it.hasPrevious()) {
				final RemovableSprite text = it.previous();

				if (text.getArea().contains(x, y)) {
					return text;
				}
			}
		}
		// map pixel coordinates
		final int tx = x + svx;
		final int ty = y + svy;
		synchronized (texts) {
			final ListIterator<RemovableSprite> it = texts.listIterator(texts.size());

			while (it.hasPrevious()) {
				final RemovableSprite text = it.previous();

				if (text.getArea().contains(tx, ty)) {
					return text;
				}
			}
		}

		return null;
	}
	
	/**
	 * Convert a world x coordinate to <em>raw</em> (native resolution)
	 * screen x coordinate.
	 * 
	 * @param x world x coordinate
	 * @return pixel x coordinate on the screen
	 */
	private int convertWorldXToScaledScreen(double x) {
		return (int) (convertWorldToPixelUnits(x - svx / (double) SIZE_UNIT_PIXELS) * scale) + svx;
	}
	
	/**
	 * Convert a world y coordinate to <em>raw</em> (native resolution)
	 * screen y coordinate.
	 * 
	 * @param y world y coordinate
	 * @return pixel y coordinate on the screen
	 */
	private int convertWorldYToScaledScreen(double y) {
		return (int) (convertWorldToPixelUnits(y - svy / (double) SIZE_UNIT_PIXELS) * scale) + svy;
	}

	/**
	 * Convert a world unit value to pixel units.
	 *
	 * @param w World value.
	 *
	 * @return A screen value (in pixels).
	 */
	private int convertWorldToPixelUnits(final double w) {
		return (int) (w * SIZE_UNIT_PIXELS);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#convertScreenViewToWorld(java.awt.Point)
	 */
	@Override
	public Point2D convertScreenViewToWorld(final Point p) {
		return convertScreenViewToWorld(p.x, p.y);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see games.stendhal.client.IGameScreen#convertScreenViewToWorld(int, int)
	 */
	@Override
	public Point2D convertScreenViewToWorld(final int x, final int y) {
		return new Point.Double(((x / scale) + getScreenViewX()) / SIZE_UNIT_PIXELS,
				((y / scale) + getScreenViewY()) / SIZE_UNIT_PIXELS);
	}

	/**
	 * Get the view X screen coordinate.
	 *
	 * @return The X coordinate of the left side.
	 */
	private int getScreenViewX() {
		return svx;
	}

	/**
	 * Get the view Y screen coordinate.
	 *
	 * @return The Y coordinate of the left side.
	 */
	private int getScreenViewY() {
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
	@Override
	public void positionChanged(final double x, final double y) {
		final int ix = (int) x;
		final int iy = (int) y;

		/*
		 * Save CPU cycles
		 */
		if ((ix != this.x) || (iy != this.y)) {
			this.x = ix;
			this.y = iy;

			calculateView(ix, iy);
		}
	}
	
	@Override
	public void dropEntity(IEntity entity, int amount, Point point) {
		// Just pass it to the ground container
		ground.dropEntity(entity, amount, point);
	}

	/**
	 * Draw a box for a reached achievement with given title, description and
	 * category.
	 * 
	 * @param title title of the achievement
	 * @param description achievement description
	 * @param category achievement category
	 */
	public void addAchievementBox(String title, String description,
			String category) {
		final Sprite sprite = getAchievementFactory().createAchievementBox(title, description, category);
		
		/*
		 * Keep the achievements a bit longer on the screen. They do not leave
		 * a line to the chat log, so we give the player a bit more time to
		 * admire her prowess.
		 */
		addStaticSprite(sprite, 2 * RemovableSprite.STANDARD_PERSISTENCE_TIME, 0);
	}
	
	/**
	 * Add a text box bound to the bottom of the screen, with a timeout
	 * dependent on the text length.
	 * 
	 * @param sprite text box sprite
	 * @param textLength text length in characters
	 * @param priority importance of the message to keep it above others
	 */
	public void addStaticText(Sprite sprite, int textLength, int priority) {
		addStaticSprite(sprite,
				Math.max(RemovableSprite.STANDARD_PERSISTENCE_TIME,
				textLength * RemovableSprite.STANDARD_PERSISTENCE_TIME / 50),
				priority);
	}
	
	/**
	 * Add a sprite anchored to the screen bottom.
	 * 
	 * @param sprite sprite
	 * @param persistTime time to stay on the screen before being automatically
	 * 	removed
	 *  @param priority importance of the message to keep it above others
	 */
	private void addStaticSprite(Sprite sprite, long persistTime, int priority) {
		int x = (getWidth() - sprite.getWidth()) / 2;
		int y = getHeight() - sprite.getHeight();
		RemovableSprite msg = new RemovableSprite(sprite, x, y, persistTime);
		msg.setPriority(priority);
		staticSprites.add(msg);
		Collections.sort(staticSprites);
	}
	
	@Override
	public void onZoneUpdate() {
		viewManager.resetViews();
	}

	@Override
	public void onZoneChange() {
		removeAllObjects();
	}

	/**
	 * Switch to spell casting triggered by a key event.
	 * 
	 * @param e triggering key event
	 */
	public void switchToSpellCasting(KeyEvent e) {
		RPObject spell = findSpell(e);
		// only if a spell was found switch to spell casting
		// i.e. Ctrl + 9 was issued, but player only has 8 spell would return null
		if (spell != null) {
			switchToSpellCastingState(spell);
		}
	}

	/**
	 * Switch to spell casting with an already chosen spell.
	 * 
	 * @param spell the chosen spell
	 */
	public void switchToSpellCastingState(RPObject spell) {
		SpellCastingGroundContainerMouseState newState = new SpellCastingGroundContainerMouseState(this.ground);
		this.ground.setNewMouseHandlerState(newState);
		newState.setSpell(spell);
	}
	
	/**
	 * Find spell corresponding to a key.
	 * 
	 * @param e key
	 * @return spell, or <code>null</code> if the key does not match any spell
	 */
	private RPObject findSpell(KeyEvent e) {
		RPObject player = StendhalClient.get().getPlayer();
		Integer position = keyEventMapping.get(e.getKeyCode());
		RPSlot slot = player.getSlot("spells");
		Integer counter = Integer.valueOf(1);
		for (RPObject spell : slot) {
			if (counter.equals(position)) {
				return spell;
			}
			counter = Integer.valueOf(counter.intValue() + 1);
		}
		return null;
	}

	@Override
	public boolean canAccept(IEntity entity) {
		if ((entity instanceof Item) || (entity instanceof Corpse)) {
			return true;
		}
		return false;
	}
}
