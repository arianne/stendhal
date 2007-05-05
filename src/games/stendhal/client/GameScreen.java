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
import java.util.List;
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

	private BufferStrategy strategy;

	private Graphics2D g;

	/**
	 * Client.
	 */
	protected StendhalClient	client;

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
	 * The screen pan X movement.
	 */
	private int	dx;

	/**
	 * The screen pan Y movement.
	 */
	private int	dy;

	/**
	 * The current screen view X.
	 */
	private int	svx;

	/**
	 * The current screen view Y.
	 */
	private int	svy;


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
		dx = 0;
		dy = 0;

		g = (Graphics2D) strategy.getDrawGraphics();
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
	 * Returns the Graphics2D object in case you want to operate it directly.
	 * Ex. GUI
	 */
	public Graphics2D expose() {
		return g;
	}


	/**
	 * Update the view position to center the target position.
	 *
	 * @param	immediate	Center on the coodinates immediately.
	 */
	protected void adjustView() {
		/*
		 * If too far away, just center
		 */
		if((Math.abs(dvx) > sw) || (Math.abs(dvy) > sh)) {
			center();
		} else {
			if(dvx == 0) {
				dx = 0;
			} else if(dvx > 0) {
				if(dvx > (sw / 4)) {
					// Accelerate (max 16)
					if(dx < 16) {
						dx++;
					}
				} else if(dvx > (sw / 8)) {
					// No change
				} else if(dvx > (sw / 16)) {
					// Slow down slowly
					dx = (dx * 7) / 8;

					if(dx == 0) {
						dx = 1;
					}
				} else {
					// Slow down quickly
					dx = (dx * 3) / 4;

					if(dx == 0) {
						dx = 1;
					}
				}
			} else {
				if(dvx < (sw / -4)) {
					// Accelerate (max 16)
					if(dx > -16) {
						dx--;
					}
				} else if(dvx < (sw / -8)) {
					// No change
				} else if(dvy < (sh / -16)) {
					// Slow down slowly
					dy = (dy * 7) / 8;

					if(dy == 0) {
						dy = 1;
					}
				} else {
					// Slow down quickly
					dy = (dy * 3) / 4;

					if(dy == 0) {
						dy = 1;
					}
				}
			}


			if(dvy == 0) {
				dy = 0;
			} else if(dvy > 0) {
				if(dvy > (sh / 4)) {
					// Accelerate (max 20)
					if(dy < 20) {
						dy++;
					}
				} else if(dvy > (sh / 8)) {
					// No change
				} else {
					// Slow down
					if(dy > 1) {
						dy /= 2;
					}
				}
			} else {
				if(dvy < (sh / -4)) {
					// Accelerate (max 20)
					if(dy > -20) {
						dy--;
					}
				} else if(dvy < (sh / -8)) {
					// No change
				} else {
					// Slow down
					if(dy < -1) {
						dy /= 2;
					}
				}
			}


			/*
			 * Adjust view
			 */
			svx += dx;
			dvx -= dx;

			svy += dy;
			dvy -= dy;

			throttleViewPan();
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

		throttleViewPan();
	}


	/**
	 * Limit the pan speed to no more than the distances to the goal.
	 * The will prevent overshoots (also stops on target hit).
	 */
	protected void throttleViewPan() {
		if(dx > 0) {
			if(dx > dvx) {
				dx = dvx;
			}
		} else if(dx < 0) {
			if(dx < dvx) {
				dx = dvx;
			}
		}

		if(dy > 0) {
			if(dy > dvy) {
				dy = dvy;
			}
		} else if(dy < 0) {
			if(dy < dvy) {
				dy = dvy;
			}
		}
	}


	/**
	 * Center the view.
	 */
	public void center() {
		svx += dvx;
		svy += dvy;

		dvx = 0;
		dvy = 0;

		dx = 0;
		dy = 0;
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

		gameLayers.draw(this, set + "_0_floor", x, y, w, h);
		gameLayers.draw(this, set + "_1_terrain", x, y, w, h);
		gameLayers.draw(this, set + "_2_object", x, y, w, h);
		gameObjects.draw(this);
		gameLayers.draw(this, set + "_3_roof", x, y, w, h);
		gameLayers.draw(this, set + "_4_roof_add", x, y, w, h);
		gameObjects.drawHPbar(this);
		gameObjects.drawText(this);

		baseframe.draw(expose());

		if (offline && (blinkOffline > 0)) {
			offlineIcon.draw(screen.expose(), 560, 420);
		}

		if (blinkOffline < -10) {
			blinkOffline = 20;
		} else {
			blinkOffline--;
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
		this.x = (int) x;
		this.y = (int) y;

		calculateView();
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
	 * Clear the screen.
	 */
	public void clear() {
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidthInPixels(), getHeightInPixels());
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
			(int) (wx * SIZE_UNIT_PIXELS) - svx,
			(int) (wy * SIZE_UNIT_PIXELS) - svy);
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
			(int) (wx * SIZE_UNIT_PIXELS) - svx,
			(int) (wy * SIZE_UNIT_PIXELS) - svy,
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

}
