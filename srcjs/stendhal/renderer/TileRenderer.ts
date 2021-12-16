/***************************************************************************
 *                 (C) Copyright 2003-2021 Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Sprite } from "sprite/Sprite";
import { Tileset } from "sprite/Tileset";
import { LayerRenderer } from "./LayerRenderer";

 
/**
 * This is a helper class to render coherent tiles based on the tileset. This
 * should be replaced by independent tiles as soon as possible .
 */
class TileRenderer extends LayerRenderer {

    /** Tileset used for the map data */
    private tileset?: Tileset;

    /** Raw map data. Indices of tiles in the tileset. */
    private map?: number[];

    /** The map data converted to tile references */
    private spriteMap?: Sprite[];

    /**
     * Sets the data that will be rendered.
     * @param in the stream to read from
     * @throws IOException
     *
     * @throws ClassNotFoundException
     */
    /* TODO
    public setMapData(layer: LayerDefinition): void {
        this.width = layer.getWidth();
        this.height = layer.getHeight();
        this.map = layer.expose();
    }*/

    /**
     * Set the tileset.
     *
     * @param tileset
     *            The tileset.
     */
    public setTileset(tileset: Tileset): void {
        this.tileset = tileset;
    }

    /**
     * Initialize the sprite map from the tileset and the map data.
     *
     * @return true if the map is ready to be used, false otherwise.
     */
    private initSpriteMap(): boolean {
        if (this.spriteMap) {
            return true;
        }

        if (!this.tileset || !this.map) {
            return false;
        }
            
        // Cache sprites
        this.spriteMap = new Array<Sprite>(this.map.length);

        for (let i = this.spriteMap.length; i > 0; i--) {
            this.spriteMap[i] = this.tileset.getSprite(this.map[i]);
        }
        return true;
    }

    public draw(g: CanvasRenderingContext2D, x: number, y: number, w: number, h: number): void {
        if (!this.initSpriteMap() || !this.spriteMap) {
            return;
        }

        const SIZE_UNIT_PIXELS = 32;  // TODO this is defined in IGameScreen
        const endX = Math.min(x + w, this.getWidth());
        const endY = Math.min(y + h, this.getHeight());

        let sy = y * SIZE_UNIT_PIXELS;
        for (let j = y; j < endY; j++) {
            let mapidx = (j * this.width) + x;
            let sx = x * SIZE_UNIT_PIXELS;

            for (let i = x; i < endX; i++) {
                this.spriteMap[mapidx].draw(g, sx, sy);
                mapidx++;
                sx += SIZE_UNIT_PIXELS;
            }
            sy += SIZE_UNIT_PIXELS;
        }
    }
}
