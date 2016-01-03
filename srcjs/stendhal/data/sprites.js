/***************************************************************************
 *                   (C) Copyright 2003-2014 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
"use strict";

window.stendhal = window.stendhal || {};
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
	 * @param offsetX optional. left x coordinate of the area
	 * @param offsetY optional. top y coordinate of the area
	 */
	getAreaOf: function(image, width, height, offsetX, offsetY) {
		offsetX = offsetX || 0;
		offsetY = offsetY || 0;
		if (image.width == width && image.height == height && offsetX == 0 && offsetY == 0) {
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
	}
}
