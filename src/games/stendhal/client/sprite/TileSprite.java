/*
 * @(#) src/games/stendhal/client/sprite/TileSprite.java
 *
 * $Id$
 */

package games.stendhal.client.sprite;

//
//

import java.awt.Graphics;

/**
 * This is a sprite that is a tile region of another sprite.
 */
public class TileSprite implements Sprite {
	/**
	 * The identifier reference.
	 */
	protected Object reference;

	/**
	 * The underyling sprite.
	 */
	protected Sprite sprite;

	/**
	 * The tile height.
	 */
	protected int height;

	/**
	 * The tile width.
	 */
	protected int width;

	/**
	 * The tile X coordinate.
	 */
	protected int x;

	/**
	 * The tile Y coordinate.
	 */
	protected int y;

	/**
	 * Create a tile region of another sprite.
	 * 
	 * <strong>NOTE: The sprite passed is not copied, and must not be modified
	 * while this instance exists (unless you are sure you know what you are
	 * doing).</strong>
	 * 
	 * @param sprite
	 *            The source sprite.
	 * @param x 
	 * @param y 
	 * @param width 
	 * @param height 
	 * 
	 * @throws IllegalArgumentException
	 *             If the region if beyond the source sprite's bounds.
	 */
	public TileSprite(final Sprite sprite, int x, int y, int width, int height) {
		this(sprite, x, y, width, height, createReference(sprite, x, y, width,
				height));
	}

	/**
	 * Create a tile region of another sprite.
	 * 
	 * <strong>NOTE: The sprite passed is not copied, and must not be modified
	 * while this instance exists (unless you are sure you know what you are
	 * doing).</strong>
	 * 
	 * @param sprite
	 *            The source sprite.
	 * @param x 
	 * @param y 
	 * @param width 
	 * @param height 
	 * 
	 * @param reference
	 *            The sprite identifier reference.
	 * 
	 * @throws IllegalArgumentException
	 *             If the region if beyond the source sprite's bounds.
	 */
	public TileSprite(final Sprite sprite, int x, int y, int width, int height,
			Object reference) {
		this.sprite = sprite;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.reference = reference;
	}

	//
	// TileSprite
	//

	/**
	 * Create a tile sprite reference.
	 * @param sprite 
	 * @param x 
	 * @param y 
	 * @param width 
	 * @param height 
	 * @return object that is used as reference
	 * 
	 * 
	 */
	public static TSRef createReference(final Sprite sprite, int x, int y,
			int width, int height) {
		Object ref = sprite.getReference();

		if (ref == null) {
			return null;
		}

		return new TSRef(ref, x, y, width, height);
	}

	/**
	 * Get the underlying sprite.
	 * 
	 * @return The underlying sprite.
	 */
	public Sprite getSprite() {
		return sprite;
	}

	//
	// Sprite
	//

	/**
	 * Copy the sprite. This does not do a deep copy, so the underlying sprite
	 * it is made of are shared.
	 * 
	 * @return A new copy of the sprite.
	 */
	public Sprite copy() {
		return new TileSprite(getSprite(), x, y, width, height);
	}

	/**
	 * Create a sub-region of this sprite. <strong>NOTE: This does not use
	 * caching.</strong>
	 * 
	 * @param x
	 *            The starting X coordinate.
	 * @param y
	 *            The starting Y coordinate.
	 * @param width
	 *            The region width.
	 * @param height
	 *            The region height.
	 * @param ref
	 *            The sprite reference.
	 * 
	 * @return A new sprite.
	 */
	public Sprite createRegion(final int x, final int y, final int width,
			final int height, final Object ref) {
		// TODO: Calculate intersect area, and avoid the extra level
		return new TileSprite(this, x, y, width, height, ref);
	}

	/**
	 * Draw the sprite onto the graphics context provided.
	 * 
	 * @param g
	 *            The graphics context on which to draw the sprite
	 * @param x
	 *            The x location at which to draw the sprite
	 * @param y
	 *            The y location at which to draw the sprite
	 */
	public void draw(final Graphics g, final int x, final int y) {
		sprite.draw(g, x, y, this.x, this.y, width, height);
	}

	/**
	 * Draws the image.
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
	public void draw(final Graphics g, final int destx, final int desty,
			final int x, final int y, final int w, final int h) {
		sprite.draw(g, destx, desty, x + this.x, y + this.y,
				Math.min(w, width), Math.min(h, height));
	}

	/**
	 * Get the height of the drawn sprite.
	 * 
	 * @return The height in pixels of this sprite.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get the sprite reference. This identifier is an externally opaque object
	 * that implements equals() and hashCode() to uniquely/repeatably reference
	 * a keyed sprite.
	 * 
	 * @return The reference identifier, or <code>null</code> if not
	 *         referencable.
	 */
	public Object getReference() {
		return reference;
	}

	/**
	 * Get the width of the drawn sprite.
	 * 
	 * @return The width in pixels of this sprite.
	 */
	public int getWidth() {
		return width;
	}

	//
	//

	/**
	 * An opaque sprite reference for a tile region.
	 */
	protected static class TSRef {
		protected Object parent;

		protected int x;

		protected int y;

		protected int width;

		protected int height;

		public TSRef(Object parent, int x, int y, int width, int height) {
			this.parent = parent;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		//
		// Object
		//

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			if (obj instanceof TSRef) {
				TSRef tsref = (TSRef) obj;

				if (!parent.equals(tsref.parent)) {
					return false;
				}

				if (x != tsref.x) {
					return false;
				}

				if (y != tsref.y) {
					return false;
				}

				if (width != tsref.width) {
					return false;
				}

				if (height != tsref.height) {
					return false;
				}

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			return parent.hashCode() ^ x ^ y;
		}

		@Override
		public String toString() {
			return parent + "[" + x + "," + y + "/" + width + "x" + height
					+ "]";
		}
	}
}
