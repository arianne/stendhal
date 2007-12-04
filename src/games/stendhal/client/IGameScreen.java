package games.stendhal.client;

import games.stendhal.client.entity.Entity;
import games.stendhal.client.gui.j2d.Text;
import games.stendhal.client.gui.j2d.entity.Entity2DView;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.common.NotificationType;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;

public interface IGameScreen {

	/** The width / height of one tile. */
	public static final int SIZE_UNIT_PIXELS = 32;

	/** Returns screen width in world units */
	public abstract double getViewWidth();

	/** Returns screen height in world units */
	public abstract double getViewHeight();

	/** Prepare screen for the next frame to be rendered and move it if needed */
	public abstract void nextFrame();

	/**
	 * Add a legacy dialog to the screen.
	 *
	 * @param panel
	 *            The dialog to add.
	 */
	public abstract void addDialog(final WtPanel panel);

	/**
	 * Add an entity.
	 *
	 * @param entity
	 *            An entity.
	 */
	public abstract void addEntity(Entity entity);

	/**
	 * Remove an entity.
	 *
	 * @param entity
	 *            An entity.
	 */
	public abstract void removeEntity(final Entity entity);

	/**
	 * Returns the Graphics2D object in case you want to operate it directly.
	 * Ex. GUI
	 */
	public abstract Graphics2D expose();

	/**
	 * Center the view.
	 */
	public abstract void center();

	public abstract Entity2DView createView(final Entity entity);

	/*
	 * Draw the screen.
	 */
	public abstract void draw();

	/**
	 * Get the view X world coordinate.
	 *
	 * @return The X coordinate of the left side.
	 */
	public abstract double getViewX();

	/**
	 * Get the view Y world coordinate.
	 *
	 * @return The Y coordinate of the left side.
	 */
	public abstract double getViewY();

	/**
	 * Sets the world size.
	 *
	 * @param width
	 *            The world width.
	 * @param height
	 *            The height width.
	 */
	public abstract void setMaxWorldSize(double width, double height);

	/**
	 * Set the offline indication state.
	 *
	 * @param offline
	 *            <code>true</code> if offline.
	 */
	public abstract void setOffline(boolean offline);

	/**
	 * Add a text bubble.
	 *
	 *
	 *
	 */
	public abstract void addText(double x, double y, String text,
			NotificationType type, boolean isTalking);

	/**
	 * Add a text bubble.
	 *
	 *
	 *
	 */
	public abstract void addText(final double x, final double y,
			final String text, final Color color, final boolean talking);

	/**
	 * Add a text bubble.
	 *
	 * @param sx
	 *            The screen X coordinate.
	 * @param sy
	 *            The screen Y coordinate.
	 * @param text
	 *            The text.
	 * @param type
	 *            The type of notification text.
	 * @param talking
	 *            Is it is a talking text bubble.
	 */
	public abstract void addText(final int sx, final int sy, final String text,
			final NotificationType type, final boolean talking);

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
	 * @param talking
	 *            Is it is a talking text bubble.
	 */
	public abstract void addText(int sx, int sy, final String text,
			final Color color, final boolean isTalking);

	/**
	 * Remove a text bubble.
	 */
	public abstract void removeText(Text entity);

	/**
	 * Remove all objects from the screen.
	 */
	public abstract void removeAll();

	/**
	 * Clear the screen.
	 */
	public abstract void clear();

	/**
	 * Removes all the text entities.
	 */
	public abstract void clearTexts();

	/**
	 * Get an entity view at given coordinates.
	 *
	 * @param x
	 *            The X world coordinate.
	 * @param y
	 *            The Y world coordinate.
	 *
	 * @return The entity view, or <code>null</code> if none found.
	 */
	public abstract Entity2DView getEntityViewAt(double x, double y);

	/**
	 * Get an entity view that is movable at given coordinates.
	 *
	 * @param x
	 *            The X world coordinate.
	 * @param y
	 *            The Y world coordinate.
	 *
	 * @return The entity view, or <code>null</code> if none found.
	 */
	public abstract Entity2DView getMovableEntityViewAt(final double x,
			final double y);

	/**
	 * Get the text bubble at specific coordinates.
	 *
	 *
	 *
	 */
	public abstract Text getTextAt(double x, double y);

	/**
	 * Convert world X coordinate to screen view coordinate.
	 *
	 * @param wx
	 *            World X coordinate.
	 *
	 * @return Screen X coordinate (in integer value).
	 */
	public abstract int convertWorldXToScreenView(double wx);

	/**
	 * Convert world Y coordinate to screen view coordinate.
	 *
	 * @param wy
	 *            World Y coordinate.
	 *
	 * @return Screen Y coordinate (in integer value).
	 */
	public abstract int convertWorldYToScreenView(double wy);

	/**
	 * Convert world coordinates to screen view coordinates.
	 *
	 * This does have some theorical range limits. Assuming a tile size of
	 * 256x256 pixels (very high def), world coordinates are limited to a little
	 * over +/-8 million, before the int (31-bit) values returned from this are
	 * wrapped. So I see no issues, even if absolute world coordinates are used.
	 *
	 * @param wx
	 *            World X coordinate.
	 * @param wy
	 *            World Y coordinate.
	 *
	 * @return Screen view coordinates (in integer values).
	 */
	public abstract Point convertWorldToScreenView(double wx, double wy);

	/**
	 * Convert world coordinates to screen coordinates.
	 *
	 * @param wrect
	 *            World area.
	 *
	 * @return Screen rectangle (in integer values).
	 */
	public abstract Rectangle convertWorldToScreenView(Rectangle2D wrect);

	/**
	 * Convert world coordinates to screen coordinates.
	 *
	 * @param wx
	 *            World X coordinate.
	 * @param wy
	 *            World Y coordinate.
	 * @param wwidth
	 *            World area width.
	 * @param wheight
	 *            World area height.
	 *
	 * @return Screen rectangle (in integer values).
	 */
	public abstract Rectangle convertWorldToScreenView(double wx, double wy,
			double wwidth, double wheight);

	/**
	 * Determine if an area is in the screen view.
	 *
	 * @param srect
	 *            Screen area.
	 *
	 * @return <code>true</code> if some part of area in in the visible
	 *         screen, otherwise <code>false</code>.
	 */
	public abstract boolean isInScreen(Rectangle srect);

	/**
	 * Determine if an area is in the screen view.
	 *
	 * @param sx
	 *            Screen X coordinate.
	 * @param sy
	 *            Screen Y coordinate.
	 * @param swidth
	 *            Screen area width.
	 * @param sheight
	 *            Screen area height.
	 *
	 * @return <code>true</code> if some part of area in in the visible
	 *         screen, otherwise <code>false</code>.
	 */
	public abstract boolean isInScreen(int sx, int sy, int swidth, int sheight);

	/** Draw a sprite in screen given its world coordinates */
	public abstract void draw(Sprite sprite, double wx, double wy);

	public abstract void drawInScreen(Sprite sprite, int sx, int sy);

	/**
	 * Create a sprite representation of some text.
	 *
	 * @param text
	 *            The text.
	 * @param type
	 *            The type.
	 *
	 * @return A sprite.
	 */
	public abstract Sprite createString(final String text,
			final NotificationType type);

	/**
	 * Create a sprite representation of some text.
	 *
	 * @param text
	 *            The text.
	 * @param color
	 *            The text color.
	 *
	 * @return A sprite.
	 */
	public abstract Sprite createString(String text, Color textColor);

	/**
	 * Draw a text string (like <em>Graphics</em><code>.drawString()</code>)
	 * only with an outline border. The area drawn extends 1 pixel out on all
	 * side from what would normal be drawn by drawString().
	 *
	 * @param g
	 *            The graphics context.
	 * @param textColor
	 *            The text color.
	 * @param outlineColor
	 *            The outline color.
	 * @param text
	 *            The text to draw.
	 * @param x
	 *            The X position.
	 * @param y
	 *            The Y position.
	 */
	public abstract void drawOutlineString(final Graphics g,
			final Color textColor, final String text, final int x, final int y);

	/**
	 * Draw a text string (like <em>Graphics</em><code>.drawString()</code>)
	 * only with an outline border. The area drawn extends 1 pixel out on all
	 * side from what would normal be drawn by drawString().
	 *
	 * @param g
	 *            The graphics context.
	 * @param textColor
	 *            The text color.
	 * @param outlineColor
	 *            The outline color.
	 * @param text
	 *            The text to draw.
	 * @param x
	 *            The X position.
	 * @param y
	 *            The Y position.
	 */
	public abstract void drawOutlineString(final Graphics g,
			final Color textColor, final Color outlineColor, final String text,
			final int x, final int y);

	/**
	 * Formats a text by changing the color of words starting with
	 * {@link #clone()}.S
	 *
	 * @param line
	 *            the text
	 * @param fontNormal
	 *            the font
	 * @param colorNormal
	 *            normal color (for non-special text)
	 */
	public abstract AttributedString formatLine(String line, Font fontNormal,
			Color colorNormal);

	public abstract Sprite createTextBox(String text, int width,
			Color textColor, Color fillColor, boolean isTalking);

	/**
	 * Convert a world unit value to a screen unit value.
	 *
	 * @param w
	 *            World value.
	 *
	 * @return A screen value (in pixels).
	 */
	public abstract int convertWorldToScreen(double w);

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
	public abstract Point2D convertScreenToWorld(final int x, final int y);

	/**
	 * Convert screen view coordinates to world coordinates.
	 *
	 * @param p
	 *            The screen view coordinates.
	 *
	 * @return World coordinates.
	 */
	public abstract Point2D convertScreenViewToWorld(final Point p);

	/**
	 * Convert screen view coordinates to world coordinates.
	 *
	 * @param x
	 *            The screen view X coordinate.
	 * @param y
	 *            The screen view Y coordinate.
	 *
	 * @return World coordinates.
	 */
	public abstract Point2D convertScreenViewToWorld(final int x, final int y);

	/**
	 * Get the full screen height in pixels.
	 *
	 * @return The height.
	 */
	public abstract int getScreenHeight();

	/**
	 * Get the full screen width in pixels.
	 *
	 * @return The width.
	 */
	public abstract int getScreenWidth();

	/**
	 * Get the view height in pixels.
	 *
	 * @return The view height.
	 */
	public abstract int getScreenViewHeight();

	/**
	 * Get the view width in pixels.
	 *
	 * @return The view width.
	 */
	public abstract int getScreenViewWidth();

	/**
	 * Get the view X screen coordinate.
	 *
	 * @return The X coordinate of the left side.
	 */
	public abstract int getScreenViewX();

	/**
	 * Get the view Y screen coordinate.
	 *
	 * @return The Y coordinate of the left side.
	 */
	public abstract int getScreenViewY();

	/**
	 * The user position changed. This sets the target coordinates that the
	 * screen centers on.
	 *
	 * @param x
	 *            The X coordinate (in world units).
	 * @param y
	 *            The Y coordinate (in world units).
	 */
	public abstract void positionChanged(final double x, final double y);

}