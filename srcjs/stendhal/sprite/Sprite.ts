/***************************************************************************
 *                   (C) Copyright 2003-2021 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/
 
 /**
  * A sprite to be displayed on the screen. Note that a sprite contains no state
  * information, i.e. its just the image and not the location. This allows us to
  * use a single sprite in lots of different places without having to store
  * multiple copies of the image.
  */
export abstract class Sprite {

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
    abstract createRegion(x: number, y: number, width: number, height: number, ref: object): Sprite;
 
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
    abstract draw(g: CanvasRenderingContext2D, x: number, y: number): void;
 
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
    abstract draw(g: CanvasRenderingContext2D, destx: number, desty: number, x: number, y: number, w: number, h: number): void;

    /**
     * Get the height of the drawn sprite.
     *
     * @return The height in pixels of this sprite
     */
    abstract getHeight(): number;

    /**
     * Get the sprite reference. This identifier is an externally opaque object
     * that implements equals() and hashCode() to uniquely/repeatably reference
     * a keyed sprite.
     *
     * @return The reference identifier, or <code>null</code> if not
     *         referencable.
     */
    abstract getReference(): object | undefined;

    /**
     * Get the width of the drawn sprite.
     *
     * @return The width in pixels of this sprite
     */
    abstract getWidth(): number;

    /**
     * Check whether the sprite won't change between draws.
     *
     * @return <code>true</code> if different draws of the sprite always
     * have the same result.
     */
    abstract isConstant(): boolean;
}
