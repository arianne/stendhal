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
import { CombinedTilesetImageLoader } from "./CombinedTilesetImageLoader";

export class CombinedTilesetFactory {

	constructor(private map: any) {}

	combine(): CombinedTileset {
		let combinedTilesToIndex: Map<string, number> = new Map();
		let indexToCombinedTiles: Map<number, number[]> = new Map();
		let combinedLayers: number[][] = [];
		let index = 0;

		// initialize the combinedLayers array
		for (let group = 0; group < this.map.layerGroupIndexes.length; group++) {
			combinedLayers[group] = [];
		}

		// lets examine every tile on both the floor and the roof layer-group
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

					// most of the tiles on the roof layer are empty, so we add a special case here
					// to skip drawing completely transparent tiles later.
					if (combinedTile.length === 0) {
						combinedLayers[group][y * this.map.zoneSizeX + x] = -1
						continue;
					}

					// use json string as map key because of equality
					let key = JSON.stringify(combinedTile);
					let value = combinedTilesToIndex.get(key);
					if (value === undefined) {
						value = index;
						combinedTilesToIndex.set(key, value);
						indexToCombinedTiles.set(value, combinedTile);
						index++;
					}
					combinedLayers[group][y * this.map.zoneSizeX + x] = value
				}
			}
		}
		let combinedTileset = new CombinedTileset(combinedTilesToIndex.size, combinedLayers);

		// console.log(this.map, combinedTilesToIndex, combinedLayers);
		// document.getElementsByTagName("body")[0].append(combinedTileset.canvas);

		let ctImageLoader = new CombinedTilesetImageLoader(this.map, indexToCombinedTiles, combinedTileset);
		ctImageLoader.load();
		return combinedTileset;
	}
}
