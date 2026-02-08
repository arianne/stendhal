/***************************************************************************
 *                   (C) Copyright 2003-2026 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Paths } from "./Paths";
import { ImageFilter } from "../sprite/image/ImageFilter";

export class SpriteStore {

	private knownBrokenUrls: {[url: string]: boolean} = {};
	private images: {[filename: string]: CanvasImageSource} = {};

	private knownShadows: {[key: string]: boolean} = {
		"24x32": true,
		"32x32": true,
		"32x48": true,
		"32x48_long": true,
		"48x64": true,
		"48x64_float": true,
		"64x48": true,
		"64x64": true,
		"64x85": true,
		"64x96": true,
		"76x64": true,
		"81x96": true,
		"96x96": true,
		"96x128": true,
		"128x96": true,
		"128x170": true,
		"144x128": true,
		"168x224": true,
		"192x192": true,
		"192x192_float": true,
		"192x256": true,
		"320x440": true,
		"ent": true
	};

	// alternatives for known images that may be considered violent or mature
	private knownSafeSprites: {[filename: string]: boolean} = {
		[Paths.sprites + "/monsters/huge_animal/thing"]: true,
		[Paths.sprites + "/monsters/mutant/imperial_mutant"]: true,
		[Paths.sprites + "/monsters/undead/bloody_zombie"]: true,
		[Paths.sprites + "/npc/deadmannpc"]: true
	};

	// TODO: move to animation.json
	animations: {[key: string]: any} = {
		idea: {
			"love": {delay: 100, offsetX: 24, offsetY: -8}
		}
	};


	/**
	 * Hidden singleton constructor.
	 */
	protected constructor() {
		// do nothing
	}

	get(filename: string): any {
		if (!filename) {
			return {};
		}
		if (filename.indexOf("undefined") > -1) {
			if (!this.knownBrokenUrls[filename]) {
				console.log("Broken image path: ", filename, new Error());
			}
			this.knownBrokenUrls[filename] = true;
			return {};
		}
		if (this.images[filename]) {
			return this.images[filename];
		}
		var temp = new Image();
		temp.onerror = (function(t: HTMLImageElement, store: SpriteStore) {
			return function() {
				if (t.src && !store.knownBrokenUrls[t.src]) {
					console.log("Broken image path:", t.src, new Error());
					store.knownBrokenUrls[t.src] = true;
				}
				const failsafe = store.getFailsafe();
				if (failsafe.src && t.src !== failsafe.src) {
					t.src = failsafe.src;
				}
			};
		})(temp, this);
		temp.src = filename;
		this.images[filename] = temp;
		return temp;
	}

	/**
	 * Rotates an image.
	 *
	 * @param img
	 *   Image to be rotated.
	 * @param angle
	 *   Angle of rotation.
	 */
	private rotate(img: HTMLImageElement, angle: number) {
		const canvas = document.getElementById("drawing-stage")! as HTMLCanvasElement;
		const ctx = canvas.getContext("2d")!;
		// make sure working with blank canvas
		ctx.clearRect(0, 0, canvas.width, canvas.height);
		canvas.width = img.width;
		canvas.height = img.height;

		ctx.translate(canvas.width / 2, canvas.height / 2);
		ctx.rotate(angle * Math.PI / 180);
		ctx.translate(-canvas.width / 2, -canvas.height / 2);
		ctx.drawImage(img, 0, 0);

		img.src = canvas.toDataURL("image/png");
	}

	/**
	 * Retrieves a rotated image.
	 *
	 * @param filename
	 *   Path to target image file.
	 * @param angle
	 *   Angle of rotation.
	 * @return
	 *   HTMLImageElement.
	 */
	getRotated(filename: string, angle: number): any {
		if (angle == 0) {
			return this.get(filename);
		}
		const id = filename + "-rot" + angle;
		if (this.images[id]) {
			return this.get(id);
		}
		// NOTE: cannot use HTMLImageElement.cloneNode here, must get base image then transfer
		//       `src` property when ready
		const img = new Image();
		img.onload = () => {
			img.onload = null;
			this.rotate(img, angle);
		}
		const baseImg = this.get(filename);
		if (baseImg.complete) {
			img.src = baseImg.src;
		} else {
			baseImg.onload = () => {
				baseImg.onload = null;
				img.src = baseImg.src;
			}
		}
		this.images[id] = img;
		return img;
	}

	/**
	 * Adds an image to cache.
	 *
	 * @param {string} id
	 *   Cache identifier.
	 * @param {SpriteImage}
	 *   Image to be cached.
	 */
	cache(id: string, image: CanvasImageSource) {
		this.images[id] = image;
	}

	/**
	 * Used when we only want an image if it was previously cached.
	 *
	 * @param filename
	 *     Full file path.
	 * @return
	 *     HTMLImageElement or undefined.
	 */
	getCached(filename: string): any {
		return this.images[filename];
	}

	/**
	 * Retrieves the failsafe sprite.
	 *
	 * @return
	 *     HTMLImageElement with failsafe image data.
	 */
	getFailsafe(): HTMLImageElement {
		const filename = Paths.sprites + "/failsafe.png";
		let failsafe = this.images[filename];
		if (failsafe) {
		} else {
			failsafe = new Image();
			failsafe.src = filename;
			this.images[filename] = failsafe;
		}
		return failsafe as HTMLImageElement;
	}

	/**
	 * Checks cached images for a valid filename.
	 *
	 * @param filename
	 *     Image filename to be checked.
	 * @return
	 *     Path to image or failsafe image file.
	 */
	checkPath(filename: string): string {
		return this.get(filename).src;
	}


	/**
	 * @param {string} fileName
	 * @param {string} filter
	 * @param {number=} param
	 */
	getFiltered(fileName: string, filter: string, param?: number) {
		const img = this.get(fileName);
		if (!img.complete || img.width === 0 || img.height === 0) {
			return img;
		}
		const filteredName = fileName + " " + filter + " " + param;
		let filtered = this.images[filteredName];
		if (typeof(filtered) === "undefined") {
			const canvas = document.createElement("canvas") as any;
			canvas.width  = img.width;
			canvas.height = img.height;
			const ctx = canvas.getContext("2d")!;
			ctx.drawImage(img, 0, 0);
			const imgData = ctx.getImageData(0, 0, img.width, img.height);
			new ImageFilter().filter(imgData, filter, param);
			ctx.putImageData(imgData, 0, 0);
			canvas.complete = true;
			this.images[filteredName] = filtered = canvas;
		}

		return filtered;
	}


	/**
	 * Retrieves a shadow sprite if the style is available.
	 *
	 * @param shadowStyle
	 *     Style of shadow to get from cache.
	 * @return
	 *     Image sprite or <code>undefined</code>.
	 */
	getShadow(shadowStyle: string): any {
		if (this.knownShadows[shadowStyle]) {
			const img = new Image();
			img.src = Paths.sprites + "/shadow/" + shadowStyle + ".png";
			return img;
		}
		return undefined;
	}

	/**
	 * Checks if there is a "safe" image available for sprite.
	 *
	 * @param filename
	 *     The sprite image base file path.
	 * @return
	 *     <code>true</code> if a known safe image is available.
	 */
	hasSafeImage(filename: string): boolean {
		return this.knownSafeSprites[filename] == true;
	}

	/**
	 * Called at startup to pre-cache certain images.
	 */
	startupCache() {
		// failsafe image
		this.getFailsafe();
		// tutorial profile
		this.get(Paths.sprites + "/npc/floattingladynpc.png");
		// achievement assets
		this.get(Paths.gui + "/banner_background.png");
		for (const cat of ["commerce", "deathmatch", "experience", "fighting", "friend",
				"interior_zone", "item", "obtain", "outside_zone", "production", "quest",
				"quest_ados_items", "quest_kill_blordroughs", "quest_kirdneh_item",
				"quest_mithrilbourgh_enemy_army", "quest_semos_monster", "special",
				"underground_zone"]) {
			this.get(Paths.achievements + "/" + cat + ".png");
		}
	}
}

/**
 * Hidden class to create the singleton instance internally.
 */
class SpriteStoreInternal extends SpriteStore {

	/** Singleton instance. */
	private static instance: SpriteStore;

	/**
	 * Retrieves singleton instance.
	 */
	static get(): SpriteStore {
		if (!SpriteStoreInternal.instance) {
			SpriteStoreInternal.instance = new SpriteStore();
		}
		return SpriteStoreInternal.instance;
	}
}

// SpriteStore singleton instance
export const store = SpriteStoreInternal.get();
