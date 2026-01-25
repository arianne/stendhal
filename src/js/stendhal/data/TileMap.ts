/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Deserializer } from "marauroa"
import { stendhal } from "../stendhal";

import { Paths } from "./Paths";

import { LandscapeRenderingStrategy } from "../landscape/LandscapeRenderingStrategy";
import { CombinedTilesetRenderingStrategy } from "../landscape/CombinedTilesetRenderingStrategy";
import { IndividualTilesetRenderingStrategy } from "../landscape/IndividualTilesetRenderingStrategy";
import { ParallaxBackground } from "../landscape/ParallaxBackground";


export class TileMap {

	public currentZoneName = "";

	public offsetX = 0;
	public offsetY = 0;
	public zoneSizeX = -1;
	public zoneSizeY = -1;
	public sizeX = 20;
	public sizeY = 15;

	public tileWidth = 32;
	public tileHeight = 32;
	public zoom = 100;

	public tilesetFilenames: string[] = [];
	public aImages = -1;
	public layerNames: any = -1;
	public layers: any = -1;
	public firstgids: any = -1;
	public gidsindex: any = [];

	public drawingError = false;
	public targetTileWidth = 0;
	public targetTileHeight = 0;

	public readonly layerGroups = [
		["0_floor", "1_terrain", "2_object"],
		["3_roof", "4_roof_add"]
	];
	public readonly blendLayers = ["blend_ground", "blend_roof"]
	public layerGroupIndexes: any;

	public strategy: LandscapeRenderingStrategy;
	public protection: any;
	public collisionData: any;

	// alternatives for known images that may be considered violent or mature
	public knownSafeTilesets: string[] = [
		Paths.tileset + "/item/armor/bloodied_small_axe",
		Paths.tileset + "/item/blood/floor_stain",
		Paths.tileset + "/item/blood/floor_stains_2",
		Paths.tileset + "/item/blood/nsew_stains",
		Paths.tileset + "/item/blood/small_stains"
	];

	public parallax: ParallaxBackground;
	public parallaxImage?: string;
	public ignoredTiles: string[];

	/** Singleton instance. */
	private static instance: TileMap;


	/**
	 * Retrieves singleton instance.
	 */
	static get(): TileMap {
		if (!TileMap.instance) {
			TileMap.instance = new TileMap();
		}
		return TileMap.instance;
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
		this.parallax = ParallaxBackground.get();
		this.ignoredTiles = [];
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
		this.decodeMapLayer(content, "blend_ground");
		this.decodeMapLayer(content, "blend_roof");
		this.protection = this.decodeMapLayer(content, "protection");
		this.collisionData = this.decodeMapLayer(content, "collision");
		this.layerGroupIndexes = this.mapLayerGroup();

		this.strategy.onMapLoaded(this);

		if (this.parallaxImage) {
			this.parallax.setImage(this.parallaxImage, this.zoneSizeX * this.tileWidth,
					this.zoneSizeY * this.tileHeight);
		}
	}

	decodeTileset(content: any, name: string) {
		var layerData = content[name];
		var deserializer = Deserializer.fromBase64(layerData);
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
			const basename = filename.replace(/\.png$/, "").replace(/^\/tileset\//, "");
			const targetname = Paths.tileset + "/" + basename;

			if (this.ignoredTiles.indexOf(basename) > -1) {
				// server has specified that certain tiles should not be drawn on this map
				continue;
			}
			if (!stendhal.config.getBoolean("effect.blood") && this.hasSafeTileset(targetname)) {
				this.tilesetFilenames.push(targetname + "-safe.png");
			} else {
				this.tilesetFilenames.push(targetname + ".png");
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
		var layerData: any;
		if (name === "0_floor" && stendhal.config.getBoolean("effect.parallax")) {
			// check for parallax-supporive floor layer
			layerData = content["0_floor_parallax"];
		}
		if (!layerData) {
			layerData = content[name];
		}
		if (!layerData) {
			return;
		}
		var deserializer = Deserializer.fromDeflatedBase64(layerData);
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

	/**
	 * Sets or unsets parallax image.
	 *
	 * @param {string=} name
	 *   Background image filename.
	 */
	setParallax(name?: string) {
		this.parallaxImage = name;
		if (typeof(name) === "undefined") {
			this.parallax.reset();
		}
	}

	/**
	 * Adds tilesets to be ignored when drawing.
	 *
	 * @param {string=} tilesets
	 *   Comma-delimited string. If empty or `undefined`, ignored tilesets list will be reset.
	 */
	setIgnoredTiles(tilesets?: string) {
		if (!tilesets) {
			this.ignoredTiles = [];
			return;
		}
		for (let t of tilesets.split(",")) {
			t = t.trim();
			if (t !== "") {
				this.ignoredTiles.push(t);
			}
		}
	}
}
