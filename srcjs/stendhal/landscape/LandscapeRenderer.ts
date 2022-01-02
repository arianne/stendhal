/***************************************************************************
 *                   (C) Copyright 2022 - Faiumoni e. V.                   *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

declare var stendhal: any;

export class LandscapeRenderer {

	public layers: number[][] = [];
	public tileset?: HTMLCanvasElement;

	constructor(private targetTileWidth: number, private targetTileHeight: number) {}

	drawLayer(canvas: HTMLCanvasElement, ctx: CanvasRenderingContext2D, layerNo: number, 
			tileOffsetX: number, tileOffsetY: number): void {
		if (!this.tileset) {
			return;
		}
		const layer = this.layers[layerNo];
		const yMax = Math.min(tileOffsetY + canvas.height / this.targetTileHeight + 1, stendhal.data.map.zoneSizeY);
		const xMax = Math.min(tileOffsetX + canvas.width / this.targetTileWidth + 1, stendhal.data.map.zoneSizeX);

		for (let y = tileOffsetY; y < yMax; y++) {
			for (let x = tileOffsetX; x < xMax; x++) {
				let gid = layer[y * stendhal.data.map.zoneSizeX + x];
				if (gid > 0) {
					const tileset = stendhal.data.map.getTilesetForGid(gid);
					const base = stendhal.data.map.firstgids[tileset];
					const idx = gid - base;

					try {
						this.drawTile(ctx, idx, x, y);
					} catch (e) {
						console.error(e);
					}
				}
			}
		}
	}

	private drawTile(ctx: CanvasRenderingContext2D, idx: number, x: number, y: number) {
		const pixelX = x * this.targetTileWidth;
		const pixelY = y * this.targetTileHeight;

		ctx.drawImage(this.tileset!,
			idx * stendhal.data.map.tileWidth, 0,
			stendhal.data.map.tileWidth, stendhal.data.map.tileHeight,
			pixelX, pixelY,
			this.targetTileWidth, this.targetTileHeight);
	}

}