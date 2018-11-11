/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.sprite;

import java.awt.Graphics;

/**
 * This is a sprite that is a tile region of another sprite.
 */
class TileSprite implements Sprite {
	/**
	 * The identifier reference.
	 */
	private Object reference;

	/**
	 * The underlying sprite.
	 */
	private Sprite sprite;

	/**
	 * The tile height.
	 */
	private int height;

	/**
	 * The tile width.
	 */
	private int width;

	/**
	 * The tile X coordinate.
	 */
	private int x;

	/**
	 * The tile Y coordinate.
	 */
	private int y;

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
	TileSprite(final Sprite sprite, final int x, final int y, final int width, final int height,
			final Object reference) {
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
	static TSRef createReference(final Sprite sprite, final int x, final int y,
			final int width, final int height) {
		final Object ref = sprite.getReference();

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
	@Override
	public Sprite createRegion(final int x, final int y, final int width,
			final int height, final Object ref) {
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
	@Override
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
	@Override
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
	@Override
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
	@Override
	public Object getReference() {
		return reference;
	}

	/**
	 * Get the width of the drawn sprite.
	 *
	 * @return The width in pixels of this sprite.
	 */
	@Override
	public int getWidth() {
		return width;
	}

	//
	//

	/**
	 * An opaque sprite reference for a tile region.
	 */
	protected static class TSRef {
		private Object parent;

		private int x;

		private int y;

		private int width;

		private int height;

		private TSRef(final Object parent, final int x, final int y, final int width, final int height) {
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
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}

			if (obj instanceof TSRef) {
				final TSRef tsref = (TSRef) obj;

				if (!parent.equals(tsref.parent)) {
					return false;
				}

				return (x == tsref.x) && (y == tsref.y)
						&& (width == tsref.width) && (height == tsref.height);
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

	@Override
	public boolean isConstant() {
		return sprite.isConstant();
	}
}
