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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.font.TextAttribute;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.text.AttributedString;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import games.stendhal.client.entity.Entity2DView;
import games.stendhal.client.entity.Text;
import games.stendhal.client.gui.wt.core.WtBaseframe;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

/**
 * This class is an abstraction of the game screen, so that we can think of it
 * as a window to the world, we can move it, place it and draw object usings
 * World coordinates. This class is based on the singleton pattern.
 */
public class GameScreen {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(GameScreen.class);

	/** The width / height of one tile. */
	public final static int SIZE_UNIT_PIXELS = 32;

	/**
	 * A scale factor for panning delta (to allow non-float precision).
	 */
	protected static final int	PAN_SCALE	= 8;

	private BufferStrategy strategy;

	private Graphics2D g;

	/**
	 * Client.
	 */
	protected StendhalClient	client;

	/**
	 * The text bubbles.
	 */
	private LinkedList<Text> texts;

	/**
	 * The text bubbles to remove.
	 */
	private List<Text> textsToRemove;

	/**
	 * The entity views.
	 */
	protected List<Entity2DView>	views;

	private static Sprite offlineIcon;

	private boolean offline;

	private int blinkOffline;

	/**
	 * The targeted center of view X coordinate (truncated).
	 */
	private int	x;

	/**
	 * The targeted center of view Y coordinate (truncated).
	 */
	private int	y;

	/** Actual size of the screen in pixels */
	private int sw, sh;

	/** Actual size of the world in world units */
	protected int ww, wh;

	/** the singleton instance */
	private static GameScreen screen;

	/** the awt-component which this screen belongs to */
	private Component component;

	/**
	 * The difference between current and target screen view X.
	 */
	private int	dvx;

	/**
	 * The difference between current and target screen view Y.
	 */
	private int	dvy;

	/**
	 * The current screen view X.
	 */
	private int	svx;

	/**
	 * The current screen view Y.
	 */
	private int	svy;

	/**
	 * The pan speed.
	 */
	private int	speed;

	/**
	 * Whether the internal state is valid
	 */
	private boolean valid;


	static {
		offlineIcon = SpriteStore.get().getSprite("data/gui/offline.png");
	}


	/**
	 * Set the default [singleton] screen.
	 *
	 * @param	screen		The screen.
	 */
	public static void setDefaultScreen(GameScreen screen) {
		GameScreen.screen = screen;
	}

	/** Returns the GameScreen object */
	public static GameScreen get() {
		return screen;
	}

	/** sets the awt-component which this screen belongs to */
	public void setComponent(Component component) {
		this.component = component;
	}

	/** returns the awt-component which this screen belongs to */
	public Component getComponent() {
		return component;
	}

	/** Returns screen width in world units */
	public double getWidth() {
		return sw / SIZE_UNIT_PIXELS;
	}

	/** Returns screen height in world units */
	public double getHeight() {
		return sh / SIZE_UNIT_PIXELS;
	}

	/** Returns screen width in pixels */
	public int getWidthInPixels() {
		return sw;
	}

	/** Returns screen height in pixels */
	public int getHeightInPixels() {
		return sh;
	}

	public GameScreen(StendhalClient client, BufferStrategy strategy, int sw, int sh) {
		this.client = client;
		this.strategy = strategy;
		this.sw = sw;
		this.sh = sh;

		x = 0;
		y = 0;
		svx = sw / -2;
		svy = sh / -2;
		dvx = 0;
		dvy = 0;

		speed = 0;

		texts = new LinkedList<Text>();
		textsToRemove = new LinkedList<Text>();
		views = new LinkedList<Entity2DView>();

		g = (Graphics2D) strategy.getDrawGraphics();
	}


	public void invalidate() {
		valid = false;
	}


	/** Prepare screen for the next frame to be rendered and move it if needed */
	public void nextFrame() {
		Log4J.startMethod(logger, "nextFrame");

		g.dispose();
		strategy.show();

		adjustView();

		g = (Graphics2D) strategy.getDrawGraphics();

		Log4J.finishMethod(logger, "nextFrame");
	}


	/**
	 * Add an entity view.
	 *
	 * @param	view		A view.
	 */
	public void addEntityView(Entity2DView view) {
		views.add(view);
	}


	/**
	 * Remove an entity view.
	 *
	 * @param	view		A view.
	 */
	public void removeEntityView(Entity2DView view) {
		views.remove(view);
	}


	/**
	 * Returns the Graphics2D object in case you want to operate it directly.
	 * Ex. GUI
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
		if((dvx == 0) && (dvy == 0)) {
			return;
		}

		int sx = convertWorldXToScreen(x) + (SIZE_UNIT_PIXELS / 2);
		int sy = convertWorldYToScreen(y) + (SIZE_UNIT_PIXELS / 2);

		if((sx < 0) || (sx >= sw) || (sy < 0) || (sy > sh)) {
			/*
			 * If off screen, just center
			 */
			center();
		} else {
			/*
			 * Calculate the target speed.
			 * The farther away, the faster.
			 */
			int dux = dvx / 40;
			int duy = dvy / 40;

			int tspeed = ((dux * dux) + (duy * duy)) * PAN_SCALE;

			if(speed > tspeed) {
				speed = (speed + tspeed) / 2;

				/*
				 * Don't stall
				 */
				if((dvx != 0) || (dvy != 0)) {
					speed = Math.max(speed, 1);
				}
			} else if(speed < tspeed) {
				speed++;
			}

			/*
			 * Moving?
			 */
			if(speed != 0) {
				/*
				 * Not a^2 + b^2 = c^2, but good enough
				 */
				int scalediv = (Math.abs(dvx) + Math.abs(dvy)) * PAN_SCALE;

				int dx = speed * dvx / scalediv;
				int dy = speed * dvy / scalediv;

				/*
				 * Don't overshoot.
				 * Don't stall.
				 */
				if(dvx < 0) {
					if(dx == 0) {
						dx = -1;
					} else if(dx < dvx) {
						dx = dvx;
					}
				} else if(dvx > 0) {
					if(dx == 0) {
						dx = 1;
					} else if(dx > dvx) {
						dx = dvx;
					}
				}

				if(dvy < 0) {
					if(dy == 0) {
						dy = -1;
					} else if(dy < dvy) {
						dy = dvy;
					}
				} else if(dvy > 0) {
					if(dy == 0) {
						dy = 1;
					} else if(dy > dvy) {
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
	 * Update the view position to center the target position.
	 *
	 * @param	immediate	Center on the coodinates immediately.
	 */
	protected void calculateView() {
		int cvx = (x * SIZE_UNIT_PIXELS) + (SIZE_UNIT_PIXELS / 2) - (sw / 2);
		int cvy = (y * SIZE_UNIT_PIXELS) + (SIZE_UNIT_PIXELS / 2) - (sh / 2);

		/*
		 * Keep the world with-in the screen view
		 */
		if(cvx < 0) {
			cvx = 0;
			svx = 0;
		} else {
			int max = (ww * SIZE_UNIT_PIXELS) - sw;

			if(cvx > max) {
				cvx = max;
				svx = max;
			}
		}

		if(cvy < 0) {
			cvy = 0;
			svy = 0;
		} else {
			int max = (wh * SIZE_UNIT_PIXELS) - sh;

			if(cvy > max) {
				cvy = max;
				svy = max;
			}
		}

		dvx = cvx - svx;
		dvy = cvy - svy;
	}


	/**
	 * Center the view.
	 */
	public void center() {
		svx += dvx;
		svy += dvy;

		dvx = 0;
		dvy = 0;

		speed = 0;
	}


	/*
	 * Draw the screen.
	 */
	public void draw(WtBaseframe baseframe) {
		/*
		 * Draw the GameLayers from bootom to top, relies on exact
		 * naming of the layers
		 */
		StaticGameLayers gameLayers = client.getStaticGameLayers();
		String set = gameLayers.getRPZoneLayerSet();

		GameObjects gameObjects = client.getGameObjects();

		int x = (int) getViewX();
		int y = (int) getViewY();
		int w = (int) getWidth();
		int h = (int) getHeight();

		/*
		 * End of the world (map falls short of the view)?
		 */
		int px = convertWorldXToScreen(Math.max(x, 0));

		if(px > 0) {
			g.setColor(Color.black);
			g.fillRect(0, 0, px, sh);
		}

		px = convertWorldXToScreen(Math.min(x + w, ww));

		if(px < sw) {
			g.setColor(Color.black);
			g.fillRect(px, 0, sw - px, sh);
		}

		int py = convertWorldYToScreen(Math.max(y, 0));

		if(py > 0) {
			g.setColor(Color.black);
			g.fillRect(0, 0, sw, py);
		}

		py = convertWorldYToScreen(Math.min(y + h, wh));

		if(py < sh) {
			g.setColor(Color.black);
			g.fillRect(0, py, sw, sh - py);
		}

		/*
		 * Layers
		 */
		gameLayers.draw(this, set, "0_floor", x, y, w, h);
		gameLayers.draw(this, set, "1_terrain", x, y, w, h);
		gameLayers.draw(this, set, "2_object", x, y, w, h);
		drawEntities();
		gameLayers.draw(this, set, "3_roof", x, y, w, h);
		gameLayers.draw(this, set, "4_roof_add", x, y, w, h);
		gameObjects.drawHPbar(this);
		drawText();

		/*
		 * Dialogs
		 */
		baseframe.draw(g);

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


	protected void drawEntities() {
// SOON:
//		Collections.sort(views, new EntityViewComparator());
//
//		for (Entity2DView view : views) {
//			view.draw(this);
//		}

		GameObjects gameObjects = client.getGameObjects();
		gameObjects.draw(this);
	}


	protected void drawText() {
		texts.removeAll(textsToRemove);
		textsToRemove.clear();

		try {
			for (Text entity : texts) {
				entity.draw(this);
			}
		} catch (ConcurrentModificationException e) {
			logger.error("cannot draw text", e);
		}
	}


	/**
	 * Get the view X world coordinate.
	 *
	 * @return	The X coordinate of the left side.
	 */
	public double getViewX() {
		return (double) svx / SIZE_UNIT_PIXELS;
	}

	/**
	 * Get the view Y world coordinate.
	 *
	 * @return	The Y coordinate of the left side.
	 */
	public double getViewY() {
		return (double) svy / SIZE_UNIT_PIXELS;
	}

	/**
	 * Set the target coordinates that the screen centers on.
	 *
	 * @param	x		The world X coordinate.
	 * @param	y		The world Y coordinate.
	 */
	public void place(double x, double y) {
		int ix = (int) x;
		int iy = (int) y;

		/*
		 * Save CPU cycles
		 */
		if((ix != this.x) || (iy != this.y)) {
			this.x = ix;
			this.y = iy;

			calculateView();
		}
	}

	/**
	 * Sets the world size.
	 *
	 * @param	width		The world width.
	 * @param	height		The height width.
	 */
	public void setMaxWorldSize(double width, double height) {
		ww = (int) width;
		wh = (int) height;

		calculateView();
	}

	/**
	 * Set the offline indication state.
	 *
	 * @param	offline		<code>true</code> if offline.
	 */
	public void setOffline(boolean offline) {
		this.offline = offline;
	}


	/**
	 * Add a text bubble.
	 *
	 *
	 *
	 */
	public void addText(double x, double y, String text, Color color, boolean isTalking) {
		boolean found = true;

		while (found == true) {
			found = false;
			for (Text item : texts) {
				if ((item.getX() == x) && (item.getY() == y)) {
					found = true;
					y += 0.5;
					break;
				}
			}
		}

		Text entity = new Text(text, x, y, color, isTalking);
		texts.add(entity);
	}


	/**
	 * Add a text bubble.
	 *
	 *
	 *
	 */
	public void addText(double x, double y, Sprite sprite, long persistTime) {
		Text entity = new Text(sprite, x, y, persistTime);
		texts.add(entity);
	}


	/**
	 * Remove a text bubble.
	 */
	public void removeText(Text entity) {
		textsToRemove.add(entity);
	}


	/**
	 * Clear the screen.
	 */
	public void clear() {
		Log4J.startMethod(logger, "clear");

		texts.clear();
		textsToRemove.clear();

		g.setColor(Color.black);
		g.fillRect(0, 0, getWidthInPixels(), getHeightInPixels());

		Log4J.finishMethod(logger, "clear");
	}

	/**
	 * Removes all the text entities.
	 */
	public void clearTexts() {
		Log4J.startMethod(logger, "clearText");

		for (Iterator it = texts.iterator(); it.hasNext();) {
			textsToRemove.add((Text) it.next());
		}

		Log4J.finishMethod(logger, "clearText");
	}


	public Entity2DView getEntityViewAt(double x, double y) {
		ListIterator<Entity2DView> it = views.listIterator(views.size());

		while (it.hasPrevious()) {
			Entity2DView view = it.previous();

			if (view.getDrawnArea().contains(x, y)) {
				return view;
			}
		}

		return null;
	}


	/**
	 * Get the text bubble at specific coordinates.
	 *
	 *
	 *
	 */
	public Text getTextAt(double x, double y) {
		ListIterator<Text> it = texts.listIterator(texts.size());

		while (it.hasPrevious()) {
			Text entity = it.previous();

			if (entity.getDrawedArea().contains(x, y)) {
				return entity;
			}
		}

		return null;
	}


	/** Translate to world coordinates the given screen coordinate */
	public Point2D translate(Point2D point) {
		double tx = (point.getX() + svx) / SIZE_UNIT_PIXELS;
		double ty = (point.getY() + svy) / SIZE_UNIT_PIXELS;
		return new Point.Double(tx, ty);
	}

	/** Translate to screen coordinates the given world coordinate */
	public Point2D invtranslate(Point2D point) {
		return convertWorldToScreen(point.getX(), point.getY());
	}


	/**
	 * Convert world X coordinate to screen coordinate.
	 *
	 * @param	wx		World X coordinate.
	 *
	 * @return	Screen X coordinate (in integer value).
	 */
	public int convertWorldXToScreen(double wx) {
		return (int) (wx * SIZE_UNIT_PIXELS) - svx;
	}


	/**
	 * Convert world Y coordinate to screen coordinate.
	 *
	 * @param	wy		World Y coordinate.
	 *
	 * @return	Screen Y coordinate (in integer value).
	 */
	public int convertWorldYToScreen(double wy) {
		return (int) (wy * SIZE_UNIT_PIXELS) - svy;
	}


	/**
	 * Convert world coordinates to screen coordinates.
	 *
	 * This does have some theorical range limits. Assuming a tile size
	 * of 256x256 pixels (very high def), world coordinates are limited
	 * to a little over +/-8 million, before the int (31-bit) values
	 * returned from this are wrapped. So I see no issues, even if
	 * absolute world coordinates are used.
	 *
	 * @param	wx		World X coordinate.
	 * @param	wy		World Y coordinate.
	 *
	 * @return	Screen coordinates (in integer values).
	 */
	public Point convertWorldToScreen(double wx, double wy) {
		return new Point(
			convertWorldXToScreen(wx),
			convertWorldYToScreen(wy));
	}


	/**
	 * Convert world coordinates to screen coordinates.
	 *
	 * @param	wrect		World area.
	 *
	 * @return	Screen rectangle (in integer values).
	 */
	public Rectangle convertWorldToScreen(Rectangle2D wrect) {
		return convertWorldToScreen(wrect.getX(), wrect.getY(), wrect.getWidth(), wrect.getHeight());
	}


	/**
	 * Convert world coordinates to screen coordinates.
	 *
	 * @param	wx		World X coordinate.
	 * @param	wy		World Y coordinate.
	 * @param	wwidth		World area width.
	 * @param	wheight		World area height.
	 *
	 * @return	Screen rectangle (in integer values).
	 */
	public Rectangle convertWorldToScreen(double wx, double wy, double wwidth, double wheight) {
		return new Rectangle(
			convertWorldXToScreen(wx),
			convertWorldYToScreen(wy),
			(int) (wwidth * SIZE_UNIT_PIXELS),
			(int) (wheight * SIZE_UNIT_PIXELS));
	}


	/**
	 * Determine if an area is in the screen view.
	 *
	 * @param	srect		Screen area.
	 *
	 * @return	<code>true</code> if some part of area in in the
	 *		visible screen, otherwise <code>false</code>.
	 */
	public boolean isInScreen(Rectangle srect) {
		return isInScreen(srect.x, srect.y, srect.width, srect.height);
	}


	/**
	 * Determine if an area is in the screen view.
	 *
	 * @param	sx		Screen X coordinate.
	 * @param	sy		Screen Y coordinate.
	 * @param	swidth		Screen area width.
	 * @param	sheight		Screen area height.
	 *
	 * @return	<code>true</code> if some part of area in in the
	 *		visible screen, otherwise <code>false</code>.
	 */
	public boolean isInScreen(int sx, int sy, int swidth, int sheight) {
		return (((sx >= -swidth) && (sx < sw)) && ((sy >= -sheight) && (sy < sh)));
	}


	/**
	 * Determine if a sprite will draw in the screen.
	 */
	public boolean isSpriteInScreen(Sprite sprite, int sx, int sy) {
		return isInScreen(sx, sy, sprite.getWidth() + 2, sprite.getHeight() + 2);
	}


	/** Draw a sprite in screen given its world coordinates */
	public void draw(Sprite sprite, double wx, double wy) {
		Point p = convertWorldToScreen(wx, wy);

		int spritew = sprite.getWidth() + 2;
		int spriteh = sprite.getHeight() + 2;

		if (((p.x >= -spritew) && (p.x < sw)) && ((p.y >= -spriteh) && (p.y < sh))) {
			sprite.draw(g, p.x, p.y);
		}
	}

	public void drawInScreen(Sprite sprite, int sx, int sy) {
		sprite.draw(g, sx, sy);
	}

	public Sprite createString(String text, Color textColor) {
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
		        .getDefaultConfiguration();
		Image image = gc.createCompatibleImage(g.getFontMetrics().stringWidth(text) + 2, 16, Transparency.BITMASK);
		Graphics g2d = image.getGraphics();

		g2d.setColor(Color.black);
		g2d.drawString(text, 0, 9);
		g2d.drawString(text, 0, 11);
		g2d.drawString(text, 2, 9);
		g2d.drawString(text, 2, 11);

		g2d.setColor(textColor);
		g2d.drawString(text, 1, 10);
		return new ImageSprite(image);
	}

	private int positionStringOfSize(String text, int width) {
		String[] words = text.split(" ");

		int i = 1;
		// Bugfix: Prevent NPE for empty text intensifly@gmx.com
		String textUnderWidth = "";
		if (words != null) {
			textUnderWidth = words[0];
		}

		while ((i < words.length) && (g.getFontMetrics().stringWidth(textUnderWidth + " " + words[i]) < width)) {
			textUnderWidth = textUnderWidth + " " + words[i];
			i++;
		}

		if ((textUnderWidth.length() == 0) && (words.length > 1)) {
			textUnderWidth = words[1];
		}

		if (g.getFontMetrics().stringWidth(textUnderWidth) > width) {
			return (int) ((float) width / (float) g.getFontMetrics().stringWidth(textUnderWidth) * textUnderWidth
			        .length());
		}

		return textUnderWidth.length();
	}

	// Added support formatted text displaying #keywords in another color
	// intensifly@gmx.com
	// ToDo: optimize the alghorithm, it's a little long ;)

	/**
	 * Formats a text by changing the  color of words starting with {@link #clone()}.S
	 *
	 * @param line the text
	 * @param fontNormal the font
	 * @param colorNormal normal color (for non-special text)
	 */
	public AttributedString formatLine(String line, Font fontNormal, Color colorNormal) {
		Font specialFont = fontNormal.deriveFont(Font.ITALIC);

		// tokenize the string
		List<String> list = Arrays.asList(line.split(" "));

		// recreate the string without the # characters
		StringBuilder temp = new StringBuilder();
		for (String tok : list) {
			if (tok.startsWith("#")) {
				tok = tok.substring(1);
			}
			temp.append(tok + " ");
		}

		// create the attribute string with the formatation
		AttributedString aStyledText = new AttributedString(temp.toString());
		int s = 0;
		for (String tok : list) {
			Font font = fontNormal;
			Color color = colorNormal;
			if (tok.startsWith("##")) {
				tok = tok.substring(1);
			} else if (tok.startsWith("#")) {
				tok = tok.substring(1);
				font = specialFont;
				color = Color.blue;
			}
			if (tok.length() > 0) {
				aStyledText.addAttribute(TextAttribute.FONT, font, s, s + tok.length() + 1);
				aStyledText.addAttribute(TextAttribute.FOREGROUND, color, s, s + tok.length() + 1);
			}
			s += tok.length() + 1;
		}

		return (aStyledText);

	}

	public Sprite createTextBox(String text, int width, Color textColor, Color fillColor, boolean isTalking) {
		java.util.List<String> lines = new java.util.LinkedList<String>();

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
			int nlpos;

			/*
			 * Hard line breaks
			 */
			if (((nlpos = text.indexOf('\n', 1)) != -1) && (nlpos < pos)) {
				pos = nlpos;
			}

			lines.add(text.substring(0, pos).trim());
			text = text.substring(pos);
			i++;
		}

		int numLines = lines.size();
		int lineLengthPixels = 0;

		for (String line : lines) {
			int lineWidth = g.getFontMetrics().stringWidth(line);
			if (lineWidth > lineLengthPixels) {
				lineLengthPixels = lineWidth;
			}
		}

		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
		        .getDefaultConfiguration();

		int imageWidth = ((lineLengthPixels + delta < width) ? lineLengthPixels + delta : width) + 4;
		int imageHeight = 16 * numLines;

		// Workaround for X-Windows not supporting images of height 0 pixel.
		if (imageHeight == 0) {
			imageHeight = 1;
			logger.warn("Created textbox for empty text");
		}

		Image image = gc.createCompatibleImage(imageWidth, imageHeight, Transparency.BITMASK);

		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (fillColor != null) {
			Composite xac = g2d.getComposite();
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);
			g2d.setComposite(ac);
			g2d.setColor(fillColor);
			g2d.fillRoundRect(10, 0, ((lineLengthPixels < width) ? lineLengthPixels : width) + 3, 16 * numLines - 1, 4,
			        4);
			g2d.setColor(textColor);
			if (isTalking) {
				g2d.drawRoundRect(10, 0, ((lineLengthPixels < width) ? lineLengthPixels : width) + 3,
				        16 * numLines - 1, 4, 4);
			} else {
				float dash[] = { 4, 2 };
				BasicStroke newStroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1, dash, 0);
				Stroke oldStroke = g2d.getStroke();
				g2d.setStroke(newStroke);
				g2d.drawRect(10, 0, ((lineLengthPixels < width) ? lineLengthPixels : width) + 3, 16 * numLines - 1);
				g2d.setStroke(oldStroke);
			}
			g2d.setComposite(xac);
			if (isTalking) {
				g2d.setColor(fillColor);
				Polygon p = new Polygon();
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
		for (String line : lines) {
			AttributedString aStyledText = formatLine(line, g2d.getFont(), textColor);

			if (fillColor == null) {
				g2d.setColor(Color.black);
				g2d.drawString(aStyledText.getIterator(), 1, 2 + i * 16 + 9);
				g2d.drawString(aStyledText.getIterator(), 1, 2 + i * 16 + 11);
				g2d.drawString(aStyledText.getIterator(), 3, 2 + i * 16 + 9);
				g2d.drawString(aStyledText.getIterator(), 3, 2 + i * 16 + 11);
			}
			g2d.setColor(textColor);

			g2d.drawString(aStyledText.getIterator(), 2 + delta, 2 + i * 16 + 10);
			i++;
		}

		return new ImageSprite(image);
	}

	//
	//

// SOON:
//	protected class EntityViewComparator implements Comparator<Entity2DView> {
//		//
//		// Comparator
//		//
//
//		public int compare(Entity2DView view1, Entity2DView view2) {
//return 0;
//		}
//	}
}
