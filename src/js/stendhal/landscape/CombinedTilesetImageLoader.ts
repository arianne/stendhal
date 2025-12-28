/***************************************************************************
 *                (C) Copyright 2022-2025 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Canvas, RenderingContext2D } from "util/Types";
import { TileMap } from "../data/TileMap";
import { TileStore } from "../data/TileStore";
import { MapOfSets } from "../util/MapOfSets";
import { CombinedTileset } from "./CombinedTileset";

declare var stendhal: any;

export class CombinedTilesetImageLoader {

	private tileUsedAtIndex!: MapOfSets<number, number>
	private tilesetImages: HTMLImageElement[] = [];
	private animations: any = {};
	private landscapeAnimationMap: any;
	private drawCanvas: Canvas;
	private drawCtx: RenderingContext2D;
	private blendCanvas: Canvas;
	private blendCtx: RenderingContext2D;

	constructor(
			private map: TileMap,
			private indexToCombinedTiles: Map<number, number[]>,
			private combinedTileset: CombinedTileset) {
		this.landscapeAnimationMap = TileStore.get().getLandscapeMap();

		this.drawCanvas = new OffscreenCanvas(this.map.tileWidth, this.map.tileHeight);
		this.blendCanvas = new OffscreenCanvas(this.map.tileWidth, this.map.tileHeight);
		this.drawCtx = this.drawCanvas.getContext("2d")!;
		this.blendCtx = this.blendCanvas.getContext("2d")!;
	}


	private calculateTileUsedAtIndex(): void {
		this.tileUsedAtIndex = new MapOfSets<number, number>();
		for (let [index, combinedTile] of this.indexToCombinedTiles.entries()) {
			for (let tile of combinedTile) {
				let gid = tile & 0x1FFFFFFF;
				this.tileUsedAtIndex.add(gid, index);
			}
		}
	}


	private calculateUsedTilesets(gids: IterableIterator<number>): Set<number> {
		let usedTilesets = new Set<number>();
		for (let gid of gids) {
			usedTilesets.add(this.map.getTilesetForGid(gid));
		}
		return usedTilesets;
	}


	private loadTileset(tileset: number) {
		let img = document.createElement("img");
		img.onload = () => {
			this.drawTileset(tileset);
		}

		const tsname = this.map.tilesetFilenames[tileset];
		if (!tsname) {
			return;
		}
		img.src = tsname + "?v=" + stendhal.data.build.version;

		if (this.landscapeAnimationMap) {
			const animation = this.landscapeAnimationMap[tsname];
			if (animation) {
				this.animations[tileset] = animation;
			}
		}

		this.tilesetImages[tileset] = img;
	}


	private drawTileset(tileset: number): void {
		let firstGid = this.map.firstgids[tileset];
		let image = this.tilesetImages[tileset];
		let numberOfTiles = image.width / this.map.tileWidth * image.height / this.map.tileHeight;

		let lastGid = firstGid + numberOfTiles;
		for (let gid = firstGid; gid <= lastGid; gid++) {
			let indexes = this.tileUsedAtIndex.get(gid);
			if (indexes === undefined) {
				continue;
			}
			for (let index of indexes) {
				this.drawCombinedTileAtIndex(index);
			}
		}
	}


	private drawCombinedTileAtIndex(index: number) {
		let tiles = this.indexToCombinedTiles.get(index)!;

		let x = index % this.combinedTileset.tilesPerRow;
		let y = Math.floor(index / this.combinedTileset.tilesPerRow);
		const pixelX = x * this.map.tileWidth;
		const pixelY = y * this.map.tileHeight;

		if (false && stendhal.ui.gamewindow.HSLFilter) {
			this.drawCombinedTileWithBlend(tiles, pixelX, pixelY);
		} else {
			this.drawCombinedTileWithoutBlend(this.combinedTileset.ctx, tiles, pixelX, pixelY);
		}
		document.getElementsByTagName("body")[0].append(this.combinedTileset.canvas as HTMLCanvasElement);
	}

	private drawCombinedTileWithoutBlend(ctx: RenderingContext2D, tiles: number[], pixelX: number, pixelY: number) {
		ctx.clearRect(pixelX, pixelY, this.map.tileWidth, this.map.tileHeight);
		for (let [i, tile] of tiles.entries()) {
			if (i === 0) {
				continue; // skip blend layer
			}
			this.drawTile(ctx, tile, pixelX, pixelY);
		}
	}

	private drawCombinedTileWithBlend(tiles: number[], pixelX: number, pixelY: number) {

		this.drawCombinedTileWithoutBlend(this.drawCtx, tiles, 0, 0);

		this.blendCtx.globalCompositeOperation = "source-over";
		this.blendCtx.clearRect(0, 0, this.map.tileWidth, this.map.tileHeight);
		this.blendCtx.drawImage(this.drawCanvas, 0, 0);

		this.blendCtx.globalCompositeOperation = "multiply";
		this.blendCtx.fillStyle = stendhal.ui.gamewindow.HSLFilter;
		this.blendCtx.fillRect(0, 0, this.map.tileWidth, this.map.tileHeight);

		let tile = tiles[0];
		this.blendCtx.globalCompositeOperation = "soft-light";
		if (tile > 0) {
			this.drawTile(this.blendCtx, tile, 0, 0);
		} else {
			this.blendCtx.fillStyle = "black";
			this.blendCtx.fillRect(0, 0, this.map.tileWidth, this.map.tileHeight);
		}

		// restore transparency which was lost during multiply and soft-light composition
		this.blendCtx.globalCompositeOperation = "destination-in";
		this.blendCtx.drawImage(this.drawCanvas, 0, 0);

		this.combinedTileset.ctx.clearRect(pixelX, pixelY, this.map.tileWidth, this.map.tileHeight);
		this.combinedTileset.ctx.drawImage(this.blendCanvas, pixelX, pixelY);
	}

	private drawTile(ctx: RenderingContext2D, tile: number, pixelX: number, pixelY: number) {
		let flip = tile & 0xE0000000;
		let gid = tile & 0x1FFFFFFF;
		let tileset = this.map.getTilesetForGid(gid);
		let image = this.tilesetImages[tileset];
		if (!image || !image.height) {
			return;
		}

		let base = this.map.firstgids[tileset];
		let tileIndexInTileset = gid - base;
		this.drawImageTile(ctx, pixelX, pixelY, image, tileIndexInTileset, flip);
	}

	private drawImageTile(ctx: RenderingContext2D, pixelX: number, pixelY: number, tilesetImage: HTMLImageElement, tileIndexInTileset: number, flip: number) {
		const tilesetWidth = tilesetImage.width;
		const tilesPerRow = Math.floor(tilesetWidth / this.map.tileWidth);

		if (flip === 0) {
			ctx.drawImage(tilesetImage,
					(tileIndexInTileset % tilesPerRow) * this.map.tileWidth,
					Math.floor(tileIndexInTileset / tilesPerRow) * this.map.tileHeight,
					this.map.tileWidth, this.map.tileHeight,
					pixelX, pixelY,
					this.map.tileWidth, this.map.tileHeight);

		} else {
			ctx.translate(pixelX, pixelY);
			// an ugly hack to restore the previous transformation matrix
			const restore = [[1, 0, 0, 1, -pixelX, -pixelY]];

			if ((flip & 0x80000000) !== 0) {
				// flip horizontally
				ctx.transform(-1, 0, 0, 1, 0, 0);
				ctx.translate(-this.map.tileWidth, 0);

				restore.push([-1, 0, 0, 1, 0, 0]);
				restore.push([1, 0, 0, 1, this.map.tileWidth, 0]);
			}
			if ((flip & 0x40000000) !== 0) {
				// flip vertically
				ctx.transform(1, 0, 0, -1, 0, 0);
				ctx.translate(0, -this.map.tileWidth);

				restore.push([1, 0, 0, -1, 0, 0]);
				restore.push([1, 0, 0, 1, 0, this.map.tileHeight]);
			}
			if ((flip & 0x20000000) !== 0) {
				// Coordinate swap
				ctx.transform(0, 1, 1, 0, 0, 0);
				restore.push([0, 1, 1, 0, 0, 0]);
			}

			ctx.drawImage(tilesetImage,
				(tileIndexInTileset % tilesPerRow) * this.map.tileWidth,
				Math.floor(tileIndexInTileset / tilesPerRow) * this.map.tileHeight,
				this.map.tileWidth, this.map.tileHeight,
				0, 0,
				this.map.tileWidth, this.map.tileHeight);

			restore.reverse();
			for (const args of restore) {
				ctx.transform.apply(ctx, args as any);
			}
		}
	}


	load() {
		console.log("CombinedTilesetImageLoader.load()");
		this.calculateTileUsedAtIndex();
		let usedTilesets = this.calculateUsedTilesets(this.tileUsedAtIndex.keys());
		for (let tileset of usedTilesets) {
			this.loadTileset(tileset);
		}

	}
}
