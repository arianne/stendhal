/***************************************************************************
 *                (C) Copyright 2022-2026 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { LandscapeRenderingStrategy } from "./LandscapeRenderingStrategy";
import { Canvas } from "util/Types";
import { TileMap } from "../data/TileMap";
import { CombinedTilesetFactory } from "./CombinedTilesetFactory";
import { CombinedTileset } from "./CombinedTileset";

export class CombinedTilesetRenderingStrategy extends LandscapeRenderingStrategy {
	private combinedTileset?: CombinedTileset;
	private map!: TileMap;

	public onMapLoaded(map: TileMap): void {
		this.map = map;
		let combinedTilesetFactory = new CombinedTilesetFactory(map);
		this.combinedTileset = combinedTilesetFactory.combine();
	}

	public onTilesetLoaded(): void {
		let body = document.getElementById("body")!;
		body.style.cursor = "auto";
	}

	public render(
		canvas: Canvas, gamewindow: any,
		tileOffsetX: number, tileOffsetY: number, targetTileWidth: number, targetTileHeight: number): void {

		this.drawLayer(
			canvas,
			this.combinedTileset,
			0,
			tileOffsetX, tileOffsetY, targetTileWidth, targetTileHeight);

		gamewindow.drawEntities();

		this.drawLayer(
			canvas,
			this.combinedTileset,
			1,
			tileOffsetX, tileOffsetY, targetTileWidth, targetTileHeight);
	}

	drawLayer(
			canvas: Canvas,
			combinedTileset: CombinedTileset|undefined, layerNo: number,
			tileOffsetX: number, tileOffsetY: number, targetTileWidth: number, targetTileHeight: number): void {
		if (!combinedTileset) {
			return;
		}
		let ctx = canvas.getContext("2d")!;

		const layer = combinedTileset.combinedLayers[layerNo];
		const yMax = Math.min(tileOffsetY + canvas.height / targetTileHeight + 1, this.map.zoneSizeY);
		const xMax = Math.min(tileOffsetX + canvas.width / targetTileWidth + 1, this.map.zoneSizeX);

		for (let y = tileOffsetY; y < yMax; y++) {
			for (let x = tileOffsetX; x < xMax; x++) {
				let index = layer[y * this.map.zoneSizeX + x];
				if (index > -1) {

					try {
						const pixelX = x * targetTileWidth;
						const pixelY = y * targetTileHeight;

						ctx.drawImage(combinedTileset.canvas,

							(index % combinedTileset.tilesPerRow) * this.map.tileWidth,
							Math.floor(index / combinedTileset.tilesPerRow) * this.map.tileHeight,

							this.map.tileWidth, this.map.tileHeight,
							pixelX, pixelY,
							targetTileWidth, targetTileHeight);
					} catch (e) {
						console.error(e);
					}
				}
			}
		}
	}

}
