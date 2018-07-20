/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    * 
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

"use strict";

var stendhal = window.stendhal = window.stendhal || {};
stendhal.data = stendhal.data || {};

stendhal.data.sprites = {
	get: function(filename) {
		if (typeof(this[filename]) != "undefined") {
			this[filename].counter++;
			return this[filename];
		}
		var temp = new Image;
		temp.counter = 0;
		temp.src = filename;
		this[filename] = temp;
		return temp;
	},

	/** deletes all objects that have not been accessed since this method was called last time */
	// TODO: call clean on map change
	clean: function() {
		for (var i in this) {
			console.log(typeof(i));
			if (typeof(i) == "Image") {
				if (this[i].counter > 0) {
					this[i].counter = 0;
				} else {
					delete(this[i]);
				}
			}
		}
	},
	
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
	getAreaOf: function(image, width, height, offsetX, offsetY) {
		offsetX = offsetX || 0;
		offsetY = offsetY || 0;
		if ((image.width === width) && (image.height === height)
				&& (offsetX === 0) && (offsetY === 0)) {
			return image;
		}
		var canvas = document.createElement("canvas");
		canvas.width  = width;
		canvas.height = height;
		var ctx = canvas.getContext("2d");
		ctx.drawImage(image, offsetX, offsetY, width, height, 0, 0, width, height);
		// Firefox would be able to use the canvas directly as a drag image, but
		// Chrome does not. This should work in any standards compliant browser.
		var newImage = new Image();
		newImage.src = canvas.toDataURL("image/png");
		return newImage;
	},
	
	/**
	 * @param {string} fileName
	 * @param {string} filter
	 * @param {number=} param
	 */
	getFiltered: function(fileName, filter, param) {
		var img = this.get(fileName);
		var filterFn;
		if (typeof(filter) === "undefined"
			|| !(filterFn = stendhal.data.sprites.filter[filter])
			|| img.width === 0 || img.height === 0) {
			return img;
		}
		var filteredName = fileName + " " + filter + " " + param;
		var filtered = this[filteredName];
		if (typeof(filtered) === "undefined") {
			var canvas = document.createElement("canvas");
			canvas.width  = img.width;
			canvas.height = img.height;
			var ctx = canvas.getContext("2d");
			ctx.drawImage(img, 0, 0);
			var imgData = ctx.getImageData(0, 0, img.width, img.height);
			var data = imgData.data;
			filterFn(data, param);
			ctx.putImageData(imgData, 0, 0);
			this[filteredName] = filtered = canvas;
		}
		
		return filtered;
	},
	
	/** Image filters */
	filter: {
		// Helper functions
		/**
		 * @param {Number} rgb
		 * @return {Array<Number>}
		 */
		splitrgb: function(rgb) {
			rgb &= 0xffffff;
			var b = rgb & 0xff;
			rgb >>>= 8;
			var g = rgb & 0xff;
			rgb >>>= 8;
			return [rgb, g, b];
		},
		
		/**
		 * @param {Array<Number>} rgb
		 * @return {Array<Number>}
		 */
		rgb2hsl: function(rgb) {
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
		hsl2rgb: function(hsl) {
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

				var rf = this.hue2rgb(this.limitHue(h + 1/3), tmp2, tmp1);
				var gf = this.hue2rgb(this.limitHue(h), tmp2, tmp1);
				var bf = this.hue2rgb(this.limitHue(h - 1/3), tmp2, tmp1);

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
		hue2rgb: function(hue, val1, val2) {
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
		limitHue: function(hue) {
			var res = hue;
			if (res < 0) {
				res += 1;
			} else if (res > 1) {
				res -= 1;
			}
			return res;
		}
	},
}

// *** Image filters. Prevent the closure compiler from mangling the names. ***
stendhal.data.sprites.filter['trueColor'] = function(data, color) {
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
}
