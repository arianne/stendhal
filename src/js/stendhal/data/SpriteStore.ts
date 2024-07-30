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

declare var stendhal: any;

import { Paths } from "./Paths";


class SpriteImage extends Image {
	// number of times the image has been accessed after initial creation
	counter = 0;
}

export class SpriteStore {

	private knownBrokenUrls: {[url: string]: boolean} = {};
	private images: {[filename: string]: SpriteImage} = {};

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
	private animations: {[key: string]: any} = {
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
			this.images[filename].counter++;
			return this.images[filename];
		}
		var temp = new Image() as SpriteImage;
		// TypeError: Image constructor: 'new' is required
		//~ var temp = new SpriteImage();
		temp.counter = 0;
		temp.onerror = (function(t: SpriteImage, store: SpriteStore) {
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

	getWithPromise(filename: string): any {
		return new Promise((resolve) => {
			if (typeof(this.images[filename]) != "undefined") {
				this.images[filename].counter++;
				resolve(this.images[filename]);
			}

			const image = new Image() as SpriteImage;
			// TypeError: Image constructor: 'new' is required
			//~ const image = new SpriteImage();
			image.counter = 0;
			this.images[filename] = image;
			image.onload = () => resolve(image);
			image.src = filename;
		});
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
		const canvas = <HTMLCanvasElement> document.getElementById("drawing-stage")!;
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
		const img = new Image() as SpriteImage;
		img.counter = 0;
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
	cache(id: string, image: SpriteImage) {
		image.counter = 0;
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
			failsafe.counter++;
		} else {
			failsafe = new Image() as SpriteImage;
			// TypeError: Image constructor: 'new' is required
			//~ failsafe = new SpriteImage();
			failsafe.counter = 0;
			failsafe.src = filename;
			this.images[filename] = failsafe;
		}
		return failsafe;
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

	/** deletes all objects that have not been accessed since this method was called last time */
	// TODO: call clean on map change
	clean() {
		for (var i in this.images) {
			console.log(typeof(this.images[i]));
			if (this.images[i] instanceof SpriteImage) {
				if (this.images[i].counter > 0) {
					this.images[i].counter--;
				} else {
					delete(this.images[i]);
				}
			}
		}
	}

	/**
	 * Get an image element whose image data is an area of a specified image.
	 * If the area matches the original image, the image itself is returned.
	 * Otherwise <em>a copy</em> of the image data is returned. This is meant
	 * to be used for obtaining the drag image for drag and drop.
	 *
	 * @param image original image
	 * @param width width of the area
	 * @param height height of the area
	 * @param {number=} offsetX optional. left x coordinate of the area
	 * @param {number=} offsetY optional. top y coordinate of the area
	 */
	getAreaOf(image: HTMLImageElement, width: number, height: number,
			offsetX?: number, offsetY?: number): any {
		try {
			offsetX = offsetX || 0;
			offsetY = offsetY || 0;
			if ((image.width === width) && (image.height === height)
					&& (offsetX === 0) && (offsetY === 0)) {
				return image;
			}
			var canvas = document.createElement("canvas") as HTMLCanvasElement;
			canvas.width  = width;
			canvas.height = height;
			var ctx = canvas.getContext("2d")!;
			ctx.drawImage(image, offsetX, offsetY, width, height, 0, 0, width, height);
			// Firefox would be able to use the canvas directly as a drag image, but
			// Chrome does not. This should work in any standards compliant browser.
			var newImage = new Image();
			newImage.src = canvas.toDataURL("image/png");
			return newImage;
		} catch (err) {
			if (err instanceof DOMException) {
				return this.getFailsafe();
			} else {
				// don't ignore other errors
				throw err;
			}
		}

		return {};
	}

	/**
	 * @param {string} fileName
	 * @param {string} filter
	 * @param {number=} param
	 */
	getFiltered(fileName: string, filter: string, param?: number) {
		const img = this.get(fileName);
		let filterFn;
		if (typeof(filter) === "undefined"
			|| !(filterFn = this.filter[filter])
			|| !img.complete || img.width === 0 || img.height === 0) {
			return img;
		}
		const filteredName = fileName + " " + filter + " " + param;
		let filtered: SpriteImage = this.images[filteredName];
		if (typeof(filtered) === "undefined") {
			const canvas = document.createElement("canvas") as any;
			canvas.width  = img.width;
			canvas.height = img.height;
			const ctx = canvas.getContext("2d")!;
			ctx.drawImage(img, 0, 0);
			const imgData = ctx.getImageData(0, 0, img.width, img.height);
			const data = imgData.data;
			filterFn(data, param);
			ctx.putImageData(imgData, 0, 0);
			canvas.complete = true;
			this.images[filteredName] = filtered = canvas as SpriteImage;
		}

		return filtered;
	}

	/**
	 * @param {string} fileName
	 * @param {string} filter
	 * @param {number=} param
	 */
	getFilteredWithPromise(fileName: string, filter: string, param?: number) {
		const imgPromise = this.getWithPromise(fileName);
		return imgPromise.then(function (img: HTMLImageElement) {
			let filterFn: Function;
			if (typeof(filter) === "undefined"
				|| !(filterFn = stendhal.data.sprites.filter[filter])
				|| !img.complete || img.width === 0 || img.height === 0) {
				return img;
			}
			const filteredName = fileName + " " + filter + " " + param;
			let filtered: SpriteImage = stendhal.data.sprites.images[filteredName];
			if (typeof(filtered) === "undefined") {
				const canvas = document.createElement("canvas") as any;
				canvas.width  = img.width;
				canvas.height = img.height;
				const ctx = canvas.getContext("2d")!;
				ctx.drawImage(img, 0, 0);
				const imgData = ctx.getImageData(0, 0, img.width, img.height);
				const data = imgData.data;
				filterFn(data, param);
				ctx.putImageData(imgData, 0, 0);
				canvas.complete = true;
				stendhal.data.sprites.images[filteredName] = filtered = canvas as SpriteImage;
			}

			return filtered;
		});
	}


	/** Image filters */
	filter: {[key: string]: Function} = {
		// Helper functions
		/**
		 * @param {Number} rgb
		 * @return {Array<Number>}
		 */
		splitrgb: function(rgb: number): number[] {
			rgb &= 0xffffff;
			var b = rgb & 0xff;
			rgb >>>= 8;
			var g = rgb & 0xff;
			rgb >>>= 8;
			return [rgb, g, b];
		},

		mergergb: function(rgbArray: number[]): number {
			const r = rgbArray[0] << 16;
			const g = rgbArray[1] << 8;
			return 0xffffff & (r | g | rgbArray[2]);
		},

		/**
		 * @param {Array<Number>} rgb
		 * @return {Array<Number>}
		 */
		rgb2hsl: function(rgb: number[]): number[] {
			var r = rgb[0] / 255;
			var g = rgb[1] / 255;
			var b = rgb[2] / 255;

			var max, min, maxVar;
			// Find the max and minimum colors, and remember which one it was
			if (r > g) {
				max = r;
				min = g;
				maxVar = 0;
			} else {
				max = g;
				min = r;
				maxVar = 1;
			}
			if (b > max) {
				max = b;
				maxVar = 2;
			} else if (b < min) {
				min = b;
			}

			// lightness
			var l = (max + min) / 2;
			var s, h;

			// saturation
			var diff = max - min;
			if (diff < 0.000001) {
				s = 0;
				// hue not really defined, but set it to something reasonable
				h = 0;
			} else {
				if (l < 0.5) {
					s = diff / (max + min);
				} else {
					s = diff / (2 - max - min);
				}

				// hue
				if (maxVar === 0) {
					h = (g - b) / diff;
				} else if (maxVar === 1) {
					h = 2 + (b - r) / diff;
				} else {
					h = 4 + (r - g) / diff;
				}
				// Normalize to range [0, 1]. It's more useful than the usual 360
				h /= 6;
			}

			return [h, s, l];
		},

		/**
		 * @param {Array<Number>} hsl
		 * @return {Array<Number>}
		 */
		hsl2rgb: function(hsl: number[]): number[] {
			var r, g, b;
			var h = hsl[0];
			var s = hsl[1];
			var l = hsl[2];

			if (s < 0.0000001) {
				r = g = b = Math.floor(255 * l);
			} else {
				var tmp1, tmp2;
				if (l < 0.5) {
					tmp1 = l * (1 + s);
				} else {
					tmp1 = l + s - l * s;
				}
				tmp2 = 2 * l - tmp1;

				var rf = stendhal.data.sprites.filter.hue2rgb(this.limitHue(h + 1/3), tmp2, tmp1);
				var gf = stendhal.data.sprites.filter.hue2rgb(this.limitHue(h), tmp2, tmp1);
				var bf = stendhal.data.sprites.filter.hue2rgb(this.limitHue(h - 1/3), tmp2, tmp1);

				r = Math.floor(255 * rf) & 0xff;
				g = Math.floor(255 * gf) & 0xff;
				b = Math.floor(255 * bf) & 0xff;
			}

			return [r, g, b];
		},

		/**
		 * @param {Number} hue
		 * @param {Number} val1
		 * @param {Number} val2
		 */
		hue2rgb: function(hue: number, val1: number, val2: number): number {
			var res = hue;
			if (6 * hue < 1) {
				res = val1 + (val2 - val1) * 6 * hue;
			} else if (2 * hue < 1) {
				res = val2;
			} else if (3 * hue < 2) {
				res = val1 + (val2 - val1) * (2/3 - hue) * 6;
			} else {
				res = val1;
			}

			return res;
		},

		/**
		 * @param {Number} hue
		 */
		limitHue: function(hue: number): number {
			var res = hue;
			if (res < 0) {
				res += 1;
			} else if (res > 1) {
				res -= 1;
			}
			return res;
		}
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
		// weather
		for (const weather of ["clouds", "fog", "fog_heavy", "rain", "rain_heavy",
				"rain_light", "snow", "snow_heavy", "snow_light", "wave"]) {
			this.get(Paths.weather + "/" + weather + ".png");
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
// *** Image filters. Prevent the closure compiler from mangling the names. ***
store.filter['trueColor'] = function(data: any, color: number) {
	var hslColor = stendhal.data.sprites.filter.rgb2hsl(stendhal.data.sprites.filter.splitrgb(color));
	var end = data.length;
	for (var i = 0; i < end; i += 4) {
		var rgb = [data[i], data[i + 1], data[i + 2]];
		var hsl = stendhal.data.sprites.filter.rgb2hsl(rgb);
		// Adjust the brightness
		var adj = hslColor[2] - 0.5; // [-0.5, 0.5]
		var tmp = hsl[2] - 0.5; // [-0.5, 0.5]
		// tweaks the middle lights either upward or downward, depending
		// on if source lightness is high or low
		var l = hsl[2] - 2.0 * adj * ((tmp * tmp) - 0.25);
		var resultHsl = [hslColor[0], hslColor[1], l];
		var resultRgb = stendhal.data.sprites.filter.hsl2rgb(resultHsl);
		data[i] = resultRgb[0];
		data[i+1] = resultRgb[1];
		data[i+2] = resultRgb[2];
	}
};
