package games.stendhal.client;

import java.awt.Graphics;

/**
 * An empty (non-drawing) sprite.
 * used by TileStore and Spritestore
  */
public class EmptySprite implements Sprite {
	/**
	 * A unique reference object.
	 */
	private static final Object	REF	= new Object();

	/**
	 * Copy the sprite.
	 *
	 * @return	A new copy of the sprite.
	 */
	public Sprite copy() {
		return this;
	}

	/**
	 * Draw the sprite onto the graphics context provided
	 * 
	 * @param g
	 *            The graphics context on which to draw the sprite
	 * @param x
	 *            The x location at which to draw the sprite
	 * @param y
	 *            The y location at which to draw the sprite
	 */
	public void draw(Graphics g, int x, int y) {
	}

	/**
	 * Draws the image
	 * 
	 * @param g
	 *            the graphics context where to draw to
	 * @param destx
	 *            destination x
	 * @param desty
	 *            destination y
	 * @param x
	 *            the source x
	 * @param y
	 *            the source y
	 * @param w
	 *            the width
	 * @param h
	 *            the height
	 */
	public void draw(Graphics g, int destx, int desty, int x, int y, int w, int h) {
	}

	/**
	 * Get the height of the drawn sprite
	 * 
	 * @return The height in pixels of this sprite
	 */
	public int getHeight() {
		return GameScreen.SIZE_UNIT_PIXELS;
	}

	/**
	 * Get the sprite reference. This identifier is an externally
	 * opaque object that implements equals() and hashCode() to
	 * uniquely/repeatably reference a keyed sprite.
	 *
	 * @return	The reference identifier, or <code>null</code>
	 *		if not referencable.
	 */
	public Object getReference() {
		return REF;
	}

	/**
	 * Get the width of the drawn sprite
	 * 
	 * @return The width in pixels of this sprite
	 */
	public int getWidth() {
		return GameScreen.SIZE_UNIT_PIXELS;
	}
}