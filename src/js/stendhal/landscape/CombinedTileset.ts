/***************************************************************************
 *                (C) Copyright 2022-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Canvas, RenderingContext2D } from "util/Types";

/**
 * a combined tileset with group layers using one single tileset image.
 *
 * For example: The layers 0_floor, 1_terrain and 2_object are all below the players.
 * So instead of drawing up to 3 images per tile on every frame, we created a combined
 * tile rewrote the layer information to point to that tile.
 *
 * We use one large tileset for the combined tiles in order to be compatible with WebGL.
 */
export class CombinedTileset {
	public readonly canvas: Canvas;
	public readonly ctx: RenderingContext2D;
	public readonly tilesPerRow: number;

	constructor(numberOfTiles: number, public readonly combinedLayers: number[][], tileWidth: number, tileHeight: number) {

		// The original approach used an a very wide image of 32 pixel height.
		// But both Firefox and Chrome are limit the dimension of an image to 2^15 pixels.
		this.tilesPerRow = Math.ceil(Math.sqrt(numberOfTiles));
		this.canvas = new OffscreenCanvas(
			tileWidth * this.tilesPerRow,
			tileHeight * this.tilesPerRow);
		this.ctx = this.canvas.getContext("2d")!;
	}

}
