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

import { stendhal } from "../stendhal";
import { LandscapeRenderingStrategy } from "./LandscapeRenderingStrategy";
import { Canvas } from "util/Types";
import { TileMap } from "../data/TileMap";
import { CombinedTilesetFactory } from "./CombinedTilesetFactory";
import { CombinedTileset } from "./CombinedTileset";

export class CombinedTilesetRenderingStrategy extends LandscapeRenderingStrategy {

	public onMapLoaded(map: TileMap): void {
		let combinedTilesetFactory = new CombinedTilesetFactory(map);
		stendhal.data.map.combinedTileset = combinedTilesetFactory.combine();
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
			stendhal.data.map.combinedTileset,
			0,
			tileOffsetX, tileOffsetY, targetTileWidth, targetTileHeight);

		gamewindow.drawEntities();

		this.drawLayer(
			canvas,
			stendhal.data.map.combinedTileset,
			1,
			tileOffsetX, tileOffsetY, targetTileWidth, targetTileHeight);
	}

	drawLayer(
			canvas: Canvas,
			combinedTileset: CombinedTileset, layerNo: number,
			tileOffsetX: number, tileOffsetY: number, targetTileWidth: number, targetTileHeight: number): void {
		if (!combinedTileset) {
			return;
		}
		let ctx = canvas.getContext("2d")!;

		const layer = combinedTileset.combinedLayers[layerNo];
		const yMax = Math.min(tileOffsetY + canvas.height / targetTileHeight + 1, stendhal.data.map.zoneSizeY);
		const xMax = Math.min(tileOffsetX + canvas.width / targetTileWidth + 1, stendhal.data.map.zoneSizeX);

		for (let y = tileOffsetY; y < yMax; y++) {
			for (let x = tileOffsetX; x < xMax; x++) {
				let index = layer[y * stendhal.data.map.zoneSizeX + x];
				if (index > -1) {

					try {
						const pixelX = x * targetTileWidth;
						const pixelY = y * targetTileHeight;

						ctx.drawImage(combinedTileset.canvas,

							(index % combinedTileset.tilesPerRow) * stendhal.data.map.tileWidth,
							Math.floor(index / combinedTileset.tilesPerRow) * stendhal.data.map.tileHeight,

							stendhal.data.map.tileWidth, stendhal.data.map.tileHeight,
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
