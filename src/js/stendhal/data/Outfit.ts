/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Pair } from "../util/Pair";
import { StringUtil } from "../util/StringUtil";
import { Paths } from "./Paths";

import { stendhal } from "../stendhal";
import { ImageMetadata } from "sprite/image/ImageMetadata";
import { MultilayerImageLoader } from "sprite/image/MultilayerImageLoader";


/**
 * Represents an entity's sprite layers.
 */
export class Outfit {

	/** Sprite layers. */
	private readonly layers: {[name: string]: number};
	/** Layer coloring values. */
	private coloring?: {[name: string]: number};


	/**
	 * Creates a new outfit.
	 *
	 * @param layers
	 *   Sprite layers.
	 */
	constructor(layers: {[layer: string]: number}={}) {
		this.layers = layers;
	}

	/**
	 * Builds an outfit from string values.
	 *
	 * @param {string} layers
	 *   Outfit layers definition in "layer=index,..." format.
	 * @param {string=} coloring
	 *   Outfit colors definition in "layer=color,..." format.
	 * @returns {Outfit}
	 *   New outfit.
	 */
	static build(layers: string, coloring?: string): Outfit {
		const olayers: {[name: string]: number} = {};
		for (const l of layers.split(",")) {
			if (l.includes("=")) {
				const temp = l.split("=");
				olayers[temp[0]] = Number.parseInt(temp[1], 10);
			}
		}
		const outfit = new Outfit(olayers);
		if (coloring) {
			const ocoloring: {[name: string]: number} = {};
			for (const c of coloring.split(",")) {
				if (c.includes("=")) {
					const temp = c.split("=");
					ocoloring[temp[0]] = Number.parseInt(temp[1], 10);
				}
			}
			outfit.setColoring(ocoloring);
		}
		return outfit;
	}

	/**
	 * Sets a layer's sprite index.
	 *
	 * @param name
	 *   Layer name.
	 * @param index
	 *   Sprite image index.
	 */
	public setLayer(name: string, index: number) {
		this.layers[name] = index;
	}

	/**
	 * Unsets a layer's sprite index.
	 *
	 * @param name
	 *   Layer name.
	 */
	public unsetLayer(name: string) {
		delete this.layers[name];
	}

	/**
	 * Retrieves the index value of a specified layer.
	 *
	 * @param name
	 *   Layer name.
	 * @return
	 *   Sprite image index.
	 */
	public getLayerIndex(name: string): number|undefined {
		return this.layers[name];
	}

	/**
	 * Retrieves sorted layer info.
	 *
	 * @return {util.Pair.Pair<string, number>[]}
	 *   Layers info.
	 */
	public getLayers(): Pair<string, number>[] {
		const layers: Pair<string, number>[] = [];
		// only include valid layers
		for (const name of stendhal.data.outfit.getLayerNames()) {
			let index = this.layers[name];
			if (index != undefined) {
				layers.push(new Pair(name, index));
			}
		}
		return layers;
	}

	/**
	 * Sets coloring info for this outfit.
	 *
	 * @param coloring {object}
	 *   Color values indexed by layer name.
	 */
	public setColoring(coloring: {[name: string]: number}) {
		this.coloring = coloring;
	}

	/**
	 * Retrieves coloring info for this outfit.
	 *
	 * @return {object}
	 *   Color values indexed by layer name or `undefined`.
	 */
	public getColoring(): {[name: string]: number}|undefined {
		return this.coloring;
	}

	/**
	 * Retrieves coloring for a single layer.
	 *
	 * @param name {string}
	 *   Layer name.
	 * @return {number}
	 *   Color value or `undefined`.
	 */
	public getLayerColor(name: string): number|undefined {
		if (stendhal.data.outfit.isSkinLayer(name)) {
			name = "skin";
		}
		return this.coloring && name in this.coloring ? this.coloring[name] : undefined;
	}

	/**
	 * Retrieves signature identifying this outfit.
	 */
	public getSignature(): string {
		const lsig: string[] = [];
		const csig: string[] = [];
		for (const layer of this.getLayers()) {
			lsig.push(layer.first + "=" + layer.second);
		}
		if (this.coloring) {
			for (const name of Object.keys(this.coloring)) {
				csig.push(name + "=" + this.coloring[name]);
			}
		}
		return "outfit(" + lsig.join(",") + ")" + (csig ? " colors(" + csig.join(",") + ")" : "")
				+ (stendhal.config.getBoolean("effect.no-nude") ? " no-nude" : "");
	}

	/**
	 * Compares outfit signatures for equality.
	 *
	 * @param {any} obj
	 *   The object to compare against this outfit.
	 */
	public equals(obj: any): boolean {
		if (!(obj instanceof Outfit)) {
			return false;
		}
		return obj.getSignature() === this.getSignature();
	}

	/**
	 * Creates a sprite sheet from layer information.
	 *
	 * TODO:
	 *   - use dress layer based on body type
	 */
	async toImage(): Promise<ImageBitmap> {
		let parts: ImageMetadata[] = [];
		let outfitLayers = this.getLayers();
		const detailIndex = this.getLayerIndex("detail") || 0;
		if (stendhal.data.outfit.detailHasRearLayer(detailIndex)) {
			outfitLayers = [new Pair("detail-rear", detailIndex), ...outfitLayers];
		}
		for (const l of outfitLayers) {
			let layerName = l.first;
			let suffix = "";
			if (layerName.endsWith("-rear")) {
				suffix = "-rear";
				layerName = layerName.replace(/-rear$/, "");
			}
			const layerIndex = l.second;
			let layerPath = Paths.sprites + "/outfit/" + layerName + "/"
					+ StringUtil.padLeft(""+layerIndex, "0", 3) + suffix;
			if (layerName === "body" && stendhal.config.getBoolean("effect.no-nude")) {
				// FIXME: some non-player pickable bodies don't have "no-nude" variant
				layerPath += "-nonude";
			}
			layerPath += ".png";
			let filter = undefined;
			let coloring = this.getLayerColor(layerName);
			if (coloring) {
				filter = "trueColor";
			}
			parts.push(new ImageMetadata(layerPath, filter, coloring));
		}

		return await new MultilayerImageLoader(parts).load();
	}
}
