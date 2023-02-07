/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { EmptySprite } from "./EmptySprite";
import { Sprite } from "./Sprite";

/**
 * A sprite to be displayed on the screen. Note that a sprite contains no state
 * information, i.e. its just the image and not the location. This allows us to
 * use a single sprite in lots of different places without having to store
 * multiple copies of the image.
 *
 * @author Kevin Glass
 */
export class ImageSprite implements Sprite {

    /** The image to be drawn for this sprite. */
    private image: CanvasImageSource;

    /**
     * The identifier reference.
     */
    private reference?: object;

    /**
     * Create a new sprite based on an image.
     *
     * @param image
     *            The image that is this sprite.
     * @param reference
     *            The sprite reference, or null.
     */
    public constructor (image: CanvasImageSource | Sprite, reference?: object) {
        if (image instanceof Sprite) {
            this.image = ImageSprite.createCompatibleImage(image);
        } else {
            this.image = image;
        }
        this.reference = reference;
    }

    private static createCompatibleImage(sprite: Sprite): HTMLCanvasElement {
        const canvas = document.createElement('canvas');
        const g = canvas.getContext('2d');
        canvas.width = sprite.getWidth();
        canvas.height = sprite.getHeight();
        sprite.draw(g!, 0, 0);
        return canvas;
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
    public createRegion(x: number, y: number, width: number, height: number, ref?: object): Sprite {
        const iwidth = this.getWidth();
        const iheight = this.getHeight();

        if ((x >= iwidth) || (y >= iheight)) {
            // Outside of image (nothing to draw)
            return new EmptySprite(width, height, ref);
        }

        // Exclude regions outside the original image
        width = Math.min(width, iwidth);
        height = Math.min(height, iheight);

        // Full copy method (the memory hog)
        const canvas = document.createElement('canvas');
        const g = canvas.getContext('2d');
        canvas.width = width;
        canvas.height = height;

        this.draw(g!, 0, 0, x, y, width, height);

        return new ImageSprite(canvas, ref);
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
    public draw(g: CanvasRenderingContext2D, destx: number, desty: number): void;

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
    public draw(g: CanvasRenderingContext2D, destx: number, desty: number, x: number, y: number, w: number, h: number): void;
    public draw(g: CanvasRenderingContext2D, destx: number, desty: number, x?: number, y?: number, w?: number, h?: number): void {
            if (arguments.length === 7) {
            g.drawImage(this.image, destx, desty, destx + w!, desty + h!, x!, y!, x! + w!, y! + h!);
        } else {
            g.drawImage(this.image, destx, desty);
        }
    }

    /**
     * Get the height of the drawn sprite.
     *
     * @return The height in pixels of this sprite
     */
    public getHeight(): number {
        return this.image.height as number;
    }

    /**
     * Get the sprite reference. This identifier is an externally opaque object
     * that implements equals() and hashCode() to uniquely/repeatably reference
     * a keyed sprite.
     *
     * @return The reference identifier, or <code>null</code> if not
     *         referencable.
     */
    public getReference(): object | undefined {
        return this.reference;
    }

    /**
     * Get the width of the drawn sprite.
     *
     * @return The width in pixels of this sprite
     */
    public getWidth(): number {
        return this.image.width as number;
    }

    public isConstant(): boolean {
        return true;
    }

    /**
     * Retrieves a single frame from the image.
     *
     * @param xIndex
     * 		Horizontal index.
     * @param yIndex
     * 		Vertical index.
     * @return
     * 		Cropped Sprite.
     */
    public getFrame(xIndex: number, yIndex: number): Sprite {
        const w = this.getWidth() / 3;
        const h = this.getHeight() / 4;
        const x = w * xIndex;
        const y = h * yIndex;

        return this.createRegion(x, y, w, h, undefined);
    }
}
