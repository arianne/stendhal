/***************************************************************************
 *                (C) Copyright 2003-2021 - Faiumoni e. V.                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Tileset } from "sprite/Tileset";

 
/**
 * This is a helper base class to render a layer.
 */
export abstract class LayerRenderer {

    protected width = 0;
    protected height = 0;

    /** @return the width in world units */
    public getWidth(): number {
        return this.width;
    }

    /** @return the height in world units */
    public getHeight(): number {
        return this.height;
    }

    /**
     * Render the layer.
     *
     * @param g The graphics to draw to
     * @param x starting x coordinate in world units
     * @param y starting y coordinate in world units
     * @param w width in world units
     * @param h height in world units
     */
    public abstract draw(g: CanvasRenderingContext2D, x: number, y: number, w: number, h: number): void;

    /**
     * Set the tiles used for rendering.
     *
     * @param tileset tile set
     */
    public abstract setTileset(tileset: Tileset): void;
}
