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

import { CombinedTileset } from "./CombinedTileset";

export class CombinedTilesetFactory {

	constructor(private map: any) {}

	combine(): CombinedTileset {
		let combinedTilesToIndex: Map<string, number> = new Map();
		let combinedLayers: number[][] = [];
		let index = 0;

		for (let x = 0; x < this.map.zoneSizeX; x++) {
			for (let y = 0; y < this.map.zoneSizeY; y++) {
				for (let group = 0; group < this.map.layerGroupIndexes.length; group++) {
					let layers = this.map.layerGroupIndexes[group];
					let combinedTile = []
					for (let layer of layers) {
						let gid = this.map.layers[layer][y * this.map.zoneSizeX + x];
						if (gid > 0) {
							combinedTile.push(gid);
						}
					}
					// use json string because of equality
					let key = JSON.stringify(combinedTile);
					let value = combinedTilesToIndex.get(key);
					if (value === undefined) {
						value = index;
						combinedTilesToIndex.set(key, value);
						index++;
					}
					if (combinedLayers[group] === undefined) {
						combinedLayers[group] = [];
					}
					combinedLayers[group][y * this.map.zoneSizeX + x] = value
				}
			}
		}
		console.log(this.map, combinedTilesToIndex, combinedLayers);
		let combinedTileset = new CombinedTileset(combinedTilesToIndex.size);
		// let ctImageLoader = new CombinedTilesetImageLoader(combinedTileset);
		// ctImageLoader.load(this.map, combinedTilesToIndex);
		return combinedTileset;
	}
}