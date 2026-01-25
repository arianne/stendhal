/***************************************************************************
 *                (C) Copyright 2022-2024 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { stendhal } from "../stendhal";

import { LandscapeRenderingStrategy } from "./LandscapeRenderingStrategy";
import { ImageCache } from "../sprite/image/ImageCache";
import { Chat } from "../util/Chat";
import { TileMap } from "../data/TileMap";
import { Canvas, RenderingContext2D } from "util/Types";


export class IndividualTilesetRenderingStrategy extends LandscapeRenderingStrategy {

	private targetTileWidth = 32;
	private targetTileHeight = 32;
	private imageCache = new ImageCache();
	private map!: TileMap;

	constructor() {
		super();
		window.setTimeout(() => {
			Chat.log("client", "Using IndividualTilesetRenderingStrategy");
		}, 1000);
	}

	public onMapLoaded(map: TileMap): void {
		// do nothing
		console.log("Using IndividualTilesetRenderingStrategy.")
		this.map = map;
		this.imageCache.close();
		this.imageCache = new ImageCache();
		this.imageCache.load(map.tilesetFilenames);
	}

	public onTilesetLoaded(): void {
		let body = document.getElementById("body")!;
		body.style.cursor = "auto";
	}

	public render(
		canvas: Canvas, gamewindow: any,
		tileOffsetX: number, tileOffsetY: number, targetTileWidth: number, targetTileHeight: number): void {

		this.targetTileWidth = targetTileWidth;
		this.targetTileHeight = targetTileHeight;

		for (var drawingLayer=0; drawingLayer < this.map.layers.length; drawingLayer++) {
			var name = this.map.layerNames[drawingLayer];
			if (name !== "protection" && name !== "collision" && name !== "objects"
				&& name !== "blend_ground" && name !== "blend_roof") {
				this.paintLayer(canvas, drawingLayer, tileOffsetX, tileOffsetY);
			}
			if (name === "2_object") {
				gamewindow.drawEntities();
			}
		}
	}

	private paintLayer(canvas: Canvas, drawingLayer: number,
		tileOffsetX: number, tileOffsetY: number) {
		const layer = this.map.layers[drawingLayer];
		const yMax = Math.min(tileOffsetY + canvas.height / this.targetTileHeight + 1, this.map.zoneSizeY);
		const xMax = Math.min(tileOffsetX + canvas.width / this.targetTileWidth + 1, this.map.zoneSizeX);
		let ctx = canvas.getContext("2d")! as RenderingContext2D;

		for (let y = tileOffsetY; y < yMax; y++) {
			for (let x = tileOffsetX; x < xMax; x++) {
				let gid = layer[y * this.map.zoneSizeX + x];
				const flip = gid & 0xE0000000;
				gid &= 0x1FFFFFFF;

				if (gid > 0) {
					const tileset = this.map.getTilesetForGid(gid);
					const base = this.map.firstgids[tileset];
					const idx = gid - base;

					try {
						let image = this.imageCache.images[this.map.tilesetFilenames[tileset]];
						if (image) {
							this.drawTile(ctx, image, idx, x, y, flip);
						}
					} catch (e) {
						console.error(e);
					}
				}
			}
		}
	}

	private drawTile(ctx: RenderingContext2D, tileset: ImageBitmap, idx: number, x: number, y: number, flip = 0) {
		const tilesetWidth = tileset.width;
		const tilesPerRow = Math.floor(tilesetWidth / this.map.tileWidth);
		const pixelX = x * this.targetTileWidth;
		const pixelY = y * this.targetTileHeight;

		if (flip === 0) {
			ctx.drawImage(tileset,
					(idx % tilesPerRow) * this.map.tileWidth,
					Math.floor(idx / tilesPerRow) * this.map.tileHeight,
					this.map.tileWidth, this.map.tileHeight,
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
