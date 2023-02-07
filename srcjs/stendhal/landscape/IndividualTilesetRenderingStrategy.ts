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

declare var stendhal: any;

import { LandscapeRenderingStrategy } from "./LandscapeRenderingStrategy";
import { ImagePreloader } from "../data/ImagePreloader";
import { Chat } from "../util/Chat";


export class IndividualTilesetRenderingStrategy extends LandscapeRenderingStrategy {

	private targetTileWidth = 32;
	private targetTileHeight = 32;

	constructor() {
		super();
		setTimeout(() => {
			Chat.log("client", "Using IndividualTilesetRenderingStrategy");
		}, 1000);
	}

	public onMapLoaded(_map: any): void {
		// do nothing
		console.log("Using IndividualTilesetRenderingStrategy.")
	}

	public onTilesetLoaded(): void {
		new ImagePreloader(stendhal.data.map.tilesetFilenames, function() {
			let body = document.getElementById("body")!;
			body.style.cursor = "auto";
		});
	}

	public render(
		canvas: HTMLCanvasElement, gamewindow: any,
		tileOffsetX: number, tileOffsetY: number, targetTileWidth: number, targetTileHeight: number): void {

		this.targetTileWidth = targetTileWidth;
		this.targetTileHeight = targetTileHeight;

		for (var drawingLayer=0; drawingLayer < stendhal.data.map.layers.length; drawingLayer++) {
			var name = stendhal.data.map.layerNames[drawingLayer];
			if (name !== "protection" && name !== "collision" && name !== "objects"
				&& name !== "blend_ground" && name !== "blend_roof") {
				this.paintLayer(canvas, drawingLayer, tileOffsetX, tileOffsetY);
			}
			if (name === "2_object") {
				gamewindow.drawEntities();
			}
		}
	}

	private paintLayer(canvas: HTMLCanvasElement, drawingLayer: number,
		tileOffsetX: number, tileOffsetY: number) {
		const layer = stendhal.data.map.layers[drawingLayer];
		const yMax = Math.min(tileOffsetY + canvas.height / this.targetTileHeight + 1, stendhal.data.map.zoneSizeY);
		const xMax = Math.min(tileOffsetX + canvas.width / this.targetTileWidth + 1, stendhal.data.map.zoneSizeX);
		let ctx = canvas.getContext("2d")!;

		for (let y = tileOffsetY; y < yMax; y++) {
			for (let x = tileOffsetX; x < xMax; x++) {
				let gid = layer[y * stendhal.data.map.zoneSizeX + x];
				const flip = gid & 0xE0000000;
				gid &= 0x1FFFFFFF;

				if (gid > 0) {
					const tileset = stendhal.data.map.getTilesetForGid(gid);
					const base = stendhal.data.map.firstgids[tileset];
					const idx = gid - base;

					try {
						if (stendhal.data.map.aImages[tileset].height > 0) {
							this.drawTile(ctx, stendhal.data.map.aImages[tileset], idx, x, y, flip);
						}
					} catch (e) {
						console.error(e);
					}
				}
			}
		}
	}

	private drawTile(ctx: CanvasRenderingContext2D, tileset: HTMLImageElement, idx: number, x: number, y: number, flip = 0) {
		const tilesetWidth = tileset.width;
		const tilesPerRow = Math.floor(tilesetWidth / stendhal.data.map.tileWidth);
		const pixelX = x * this.targetTileWidth;
		const pixelY = y * this.targetTileHeight;

		if (flip === 0) {
			ctx.drawImage(tileset,
					(idx % tilesPerRow) * stendhal.data.map.tileWidth,
					Math.floor(idx / tilesPerRow) * stendhal.data.map.tileHeight,
					stendhal.data.map.tileWidth, stendhal.data.map.tileHeight,
					pixelX, pixelY,
					this.targetTileWidth, this.targetTileHeight);
		} else {
			ctx.translate(pixelX, pixelY);
			// an ugly hack to restore the previous transformation matrix
			const restore = [[1, 0, 0, 1, -pixelX, -pixelY]];

			if ((flip & 0x80000000) !== 0) {
				// flip horizontally
				ctx.transform(-1, 0, 0, 1, 0, 0);
				ctx.translate(-this.targetTileWidth, 0);

				restore.push([-1, 0, 0, 1, 0, 0]);
				restore.push([1, 0, 0, 1, this.targetTileWidth, 0]);
			}
			if ((flip & 0x40000000) !== 0) {
				// flip vertically
				ctx.transform(1, 0, 0, -1, 0, 0);
				ctx.translate(0, -this.targetTileWidth);

				restore.push([1, 0, 0, -1, 0, 0]);
				restore.push([1, 0, 0, 1, 0, this.targetTileHeight]);
			}
			if ((flip & 0x20000000) !== 0) {
				// Coordinate swap
				ctx.transform(0, 1, 1, 0, 0, 0);
				restore.push([0, 1, 1, 0, 0, 0]);
			}

			this.drawTile(ctx, tileset, idx, 0, 0);

			restore.reverse();
			for (const args of restore) {
				ctx.transform(args[0], args[1], args[2], args[3], args[4], args[5]);
			}
		}
	}

}
