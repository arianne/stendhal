/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

declare var marauroa: any;
declare var stendhal: any;

import { Paths } from "./Paths";

import { LandscapeRenderingStrategy, CombinedTilesetRenderingStrategy } from "../landscape/LandscapeRenderingStrategy";
import { IndividualTilesetRenderingStrategy } from "../landscape/IndividualTilesetRenderingStrategy";


export class Map {

	private currentZoneName = "";

	private offsetX = 0;
	private offsetY = 0;
	private zoneSizeX = -1;
	private zoneSizeY = -1;
	private sizeX = 20;
	private sizeY = 15;

	private tileWidth = 32;
	private tileHeight = 32;
	private zoom = 100;

	private tilesetFilenames: string[] = [];
	private aImages = -1;
	private layerNames: any = -1;
	private layers: any = -1;
	private firstgids: any = -1;
	private gidsindex: any = [];

	private drawingError = false;
	private targetTileWidth = 0;
	private targetTileHeight = 0;

	private readonly layerGroups: any = [
		["0_floor", "1_terrain", "2_object"],
		["3_roof", "4_roof_add"]
	];
	private layerGroupIndexes: any;

	private strategy: LandscapeRenderingStrategy;
	private protection: any;
	private collisionData: any;

	// alternatives for known images that may be considered violent or mature
	private knownSafeTilesets: string[] = [
		Paths.tileset + "/item/armor/bloodied_small_axe",
		Paths.tileset + "/item/blood/floor_stain",
		Paths.tileset + "/item/blood/floor_stains_2",
		Paths.tileset + "/item/blood/nsew_stains",
		Paths.tileset + "/item/blood/small_stains"
	];

	/** Singleton instance. */
	private static instance: Map;


	/**
	 * Retrieves singleton instance.
	 */
	static get(): Map {
		if (!Map.instance) {
			Map.instance = new Map();
		}
		return Map.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		if (window.location.search.indexOf("old") > -1) {
			this.strategy = new IndividualTilesetRenderingStrategy();
		} else {
			this.strategy = new CombinedTilesetRenderingStrategy();
		}
	}

	/**
	 * Returns the index of the tileset a tile belongs to.
	 */
	getTilesetForGid(value: number): any {
		if (value < this.gidsindex.length) {
			return this.gidsindex[value];
		} else {
			return this.gidsindex[this.gidsindex.length - 1] + 1;
		}
	}

	onTransfer(zoneName: string, content: any) {
		this.currentZoneName = zoneName;
		this.firstgids = [];
		this.layers = [];
		this.layerNames = [];

		var body = document.getElementById("body")!;
		body.style.cursor = "wait";
		console.log("load map");


		// tilesets, 0_floor, 1_terrain, 2_object, 3_roof, 4_roof_add,	blend_ground, blend_roof, protection, collision, data_map
		this.decodeTileset(content, "tilesets");
		this.decodeMapLayer(content, "0_floor");
		this.decodeMapLayer(content, "1_terrain");
		this.decodeMapLayer(content, "2_object");
		this.decodeMapLayer(content, "3_roof");
		this.decodeMapLayer(content, "4_roof_add");
		this.protection = this.decodeMapLayer(content, "protection");
		this.collisionData = this.decodeMapLayer(content, "collision");
		this.layerGroupIndexes = this.mapLayerGroup();

		this.strategy.onMapLoaded(this);

		marauroa.me.onEnterZone();
	}

	decodeTileset(content: any, name: string) {
		var layerData = content[name];
		var deserializer = marauroa.Deserializer.fromBase64(layerData);
		var amount = deserializer.readInt() as number;

		this.tilesetFilenames = [];
		for (var i = 0; i < amount; i++) {
			var name = deserializer.readString() as string;
			var source = deserializer.readString() as string;
			var firstgid = deserializer.readInt() as number;

			// Security Note: The following line triggers a false positive.
			// This is not input validation. It just rewrites a path used by the
			// Java client to a path matching the webserver directory layout.
			var filename = "/" + source.replace(/\.\.\/\.\.\//g, "");

			let baseFilename = filename.replace(/\.png$/, "").replace("/tileset", Paths.tileset);
			if (!stendhal.config.getBoolean("gamescreen.blood") && this.hasSafeTileset(baseFilename)) {
				this.tilesetFilenames.push(baseFilename + "-safe.png");
			} else {
				this.tilesetFilenames.push(filename);
			}

			this.firstgids.push(firstgid);
		}

		// create a lookup table from gid to tileset index for significant performance reasons
		this.gidsindex = [];
		var pos: number, lastStart = 0, i: number;
		for (pos = 0; pos < parseInt(this.firstgids.length, 10); pos++) {
			for (i = lastStart; i < this.firstgids[pos]; i++) {
				this.gidsindex.push(pos - 1);
			}
			lastStart = this.firstgids[pos];
		}

		this.strategy.onTilesetLoaded();
	}

	decodeMapLayer(content: any, name: string): number[]|undefined {
		var layerData = content[name];
		if (!layerData) {
			return;
		}
		var deserializer = marauroa.Deserializer.fromDeflatedBase64(layerData);
		deserializer.readString(); // zone name
		this.zoneSizeX = deserializer.readInt();
		this.zoneSizeY = deserializer.readInt();
		var layerRaw = deserializer.readByteArray();

		var layer: number[] = [];
		for (var i = 0; i < this.zoneSizeX * this.zoneSizeY * 4 - 3; i=i+4) {
			var tileId = layerRaw.getUint32(i, true);
			layer.push(tileId);
		}

		this.layerNames.push(name);
		this.layers.push(layer);

		return layer;
	}

	collision(x: number, y: number): boolean {
		return this.collisionData[y * this.zoneSizeX + x] != 0;
	}

	isProtected(x: number, y: number): boolean {
		return this.protection[y * this.zoneSizeX + x] != 0;
	}

	mapLayerGroup(): any {
		let res = [];
		for (let layers of this.layerGroups) {
			let group = [];
			for (let layer of layers) {
				let index = this.layerNames.indexOf(layer);
				if (index > - 1) {
					group.push(index);
				}
			}
			if (group) {
				res.push(group);
			}
		}
		return res;
	}

	/**
	 * Checks if there is an alternative "safe" tileset image available.
	 *
	 * @param filename
	 *     The tileset image base file path.
	 * @return
	 *     <code>true</code> if a known safe image is available.
	 */
	hasSafeTileset(filename: string) {
		return this.knownSafeTilesets.indexOf(filename) > -1;
	}
}
