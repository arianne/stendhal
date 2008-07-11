package games.stendhal.client;

import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.EntityView;
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
	int SIZE_UNIT_PIXELS = 32;

	/** @return screen width in world units. */
	double getViewWidth();

	/** @return screen height in world units .*/
	double getViewHeight();

	/** Prepare screen for the next frame to be rendered and move it if needed .*/
	void nextFrame();

	/**
	 * Add a legacy dialog to the screen.
	 *
	 * @param panel
	 *            The dialog to add.
	 */
	void addDialog(final WtPanel panel);

	/**
	 * Add an entity.
	 *
	 * @param entity
	 *            An entity.
	 */
	void addEntity(Entity entity);

	/**
	 * Remove an entity.
	 *
	 * @param entity
	 *            An entity.
	 */
	void removeEntity(final Entity entity);

	/**
	 * @return the Graphics2D object in case you want to operate it directly.
	 * Ex. GUI
	 */
	Graphics2D expose();

	/**
	 * Center the view.
	 */
	 void center();

	Entity2DView createView(final Entity entity);

	/*
	 * Draw the screen.
	 */
	void draw();

	/**
	 * Get the view X world coordinate.
	 *
	 * @return The X coordinate of the left side.
	 */
	double getViewX();

	/**
	 * Get the view Y world coordinate.
	 *
	 * @return The Y coordinate of the left side.
	 */
	double getViewY();

	/**
	 * Sets the world size.
	 *
	 * @param width
	 *            The world width.
	 * @param height
	 *            The height width.
	 */
	void setMaxWorldSize(double width, double height);

	/**
	 * Set the offline indication state.
	 *
	 * @param offline
	 *            <code>true</code> if offline.
	 */
	void setOffline(boolean offline);

	/**
	 * Adds a text bubble at a give position of the specified type.
	 * 
	 * @param x The screen X coordinate.
	 * @param y The screen Y coordinate.
	 * @param text The textual content
	 * @param type The notificationType 
	 * @param isTalking Is it a talking text bubble
	 * @see games.stendhal.common.NotificationType
	 * 
	 */
	void addText(double x, double y, String text,
			NotificationType type, boolean isTalking);

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
	void addText(final double x, final double y,
			final String text, final Color color, final boolean isTalking);

	/**
	 * Add a text bubble.
	 *
	 * @param x
	 *            The screen X coordinate.
	 * @param y
	 *            The screen Y coordinate.
	 * @param text
	 *            The text.
	 * @param type
	 *            The type of notification text.
	 * @param talking
	 *            Is it is a talking text bubble.
	 */
	void addText(final int x, final int y, final String text,
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
	 * @param isTalking
	 *            Is it is a talking text bubble.
	 */
	void addText(int sx, int sy, final String text,
			final Color color, final boolean isTalking);

	/**
	 * Removes a text bubble.
	 * @param entity The text to be removed.
	 */
	void removeText(Text entity);

	/**
	 * Removes all objects from the screen.
	 */
	void removeAll();

	/**
	 * Clears the screen.
	 */
	void clear();

	/**
	 * Removes all the text entities.
	 */
	void clearTexts();

	/**
	 * Gets an entity view at given coordinates.
	 *
	 * @param x
	 *            The X world coordinate.
	 * @param y
	 *            The Y world coordinate.
	 *
	 * @return The entity view, or <code>null</code> if none found.
	 */
	EntityView getEntityViewAt(double x, double y);

	/**
	 * Get a movable entity view at given coordinates.
	 *
	 * @param x
	 *            The X world coordinate.
	 * @param y
	 *            The Y world coordinate.
	 *
	 * @return The entity view, or <code>null</code> if none found.
	 */
	EntityView getMovableEntityViewAt(final double x,
			final double y);

	/**
	 * Get the text bubble at specific coordinates.
	 *
	 * @param x
	 *            The X world coordinate.
	 * @param y
	 *            The Y world coordinate.
	 * @return the text bubble at the given coorodinate or <code>null</code> if not found.
	 *
	 */
	Text getTextAt(double x, double y);

	/**
	 * Convert world X coordinate to screen view coordinate.
	 *
	 * @param wx
	 *            World X coordinate.
	 *
	 * @return Screen X coordinate (in integer value).
	 */
	int convertWorldXToScreenView(double wx);

	/**
	 * Convert world Y coordinate to screen view coordinate.
	 *
	 * @param wy
	 *            World Y coordinate.
	 *
	 * @return Screen Y coordinate (in integer value).
	 */
	int convertWorldYToScreenView(double wy);

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
	Point convertWorldToScreenView(double wx, double wy);

	/**
	 * Convert world coordinates to screen coordinates.
	 *
	 * @param wrect
	 *            World area.
	 *
	 * @return Screen rectangle (in integer values).
	 */
	Rectangle convertWorldToScreenView(Rectangle2D wrect);

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
	Rectangle convertWorldToScreenView(double wx, double wy,
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
	 boolean isInScreen(Rectangle srect);

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
	boolean isInScreen(int sx, int sy, int swidth, int sheight);

	/**
	 * Draws a sprite in screen given its world coordinates.
	 * 
	 * @param sprite
	 *            to be drawn
	 * @param wx
	 *            x in world coordinates
	 * @param wy
	 *            y in world coordinates
	 * 
	 */
	void draw(Sprite sprite, double wx, double wy);

	void drawInScreen(Sprite sprite, int sx, int sy);

	/**
	 * Creates a sprite representation of some text.
	 *
	 * @param text
	 *            The text.
	 * @param type
	 *            The type.
	 *
	 * @return A sprite.
	 */
	Sprite createString(final String text,
			final NotificationType type);

	/**
	 * Create a sprite representation of some text.
	 *
	 * @param text
	 *            The text.
	 * @param textColor
	 *            The text color.
	 *
	 * @return A sprite.
	 */
	Sprite createString(String text, Color textColor);

	/**
	 * Draw a text string (like <em>Graphics</em><code>.drawString()</code>)
	 * only with an outline border. <p>The area drawn extends 1 pixel out on all
	 * side from what would normal be drawn by drawString().
	 * The outline color is calculated from the text color value.
	 *
	 * @param g
	 *            The graphics context.
	 * @param textColor
	 *            The text color.
	 * @param text
	 *            The text to draw.
	 * @param x
	 *            The X position.
	 * @param y
	 *            The Y position.
	 */
	void drawOutlineString(final Graphics g,
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
	void drawOutlineString(final Graphics g,
			final Color textColor, final Color outlineColor, final String text,
			final int x, final int y);

	/**
	 * Formats a text by changing the color of words starting with
	 * #.
	 *
	 * @param line
	 *            the text
	 * @param fontNormal
	 *            the font
	 * @param colorNormal
	 *            normal color (for non-special text)
	 * @return the formatted string
	 */
	AttributedString formatLine(String line, Font fontNormal,
			Color colorNormal);

	Sprite createTextBox(String text, int width,
			Color textColor, Color fillColor, boolean isTalking);

	/**
	 * Convert a world unit value to a screen unit value.
	 *
	 * @param w
	 *            World value.
	 *
	 * @return A screen value (in pixels).
	 */
	int convertWorldToScreen(double w);

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
	Point2D convertScreenToWorld(final int x, final int y);

	/**
	 * Convert screen view coordinates to world coordinates.
	 *
	 * @param p
	 *            The screen view coordinates.
	 *
	 * @return World coordinates.
	 */
	Point2D convertScreenViewToWorld(final Point p);

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
	Point2D convertScreenViewToWorld(final int x, final int y);

	/**
	 * Get the full screen height in pixels.
	 *
	 * @return The height.
	 */
	int getScreenHeight();

	/**
	 * Get the full screen width in pixels.
	 *
	 * @return The width.
	 */
	int getScreenWidth();

	/**
	 * Get the view height in pixels.
	 *
	 * @return The view height.
	 */
	int getScreenViewHeight();

	/**
	 * Get the view width in pixels.
	 *
	 * @return The view width.
	 */
	int getScreenViewWidth();

	/**
	 * Get the view X screen coordinate.
	 *
	 * @return The X coordinate of the left side.
	 */
	int getScreenViewX();

	/**
	 * Get the view Y screen coordinate.
	 *
	 * @return The Y coordinate of the left side.
	 */
	int getScreenViewY();

	/**
	 * The user position changed. This sets the target coordinates that the
	 * screen centers on.
	 *
	 * @param x
	 *            The X coordinate (in world units).
	 * @param y
	 *            The Y coordinate (in world units).
	 */
	void positionChanged(final double x, final double y);

}