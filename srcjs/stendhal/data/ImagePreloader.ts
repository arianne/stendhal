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

declare var stendhal: any;


/*
 * Start: http://www.webreference.com/programming/javascript/gr/column3/
 * ( https://web.archive.org/web/20110910064905/http://www.webreference.com:80/programming/javascript/gr/column3/index.html )
 *
function ImagePreloader(images, callback) {
	// store the call-back
	this.callback = callback;

	// initialize internal state.
	this.nLoaded = 0;
	this.nProcessed = 0;
	stendhal.data.map.aImages = new Array;

	// record the number of images.
	this.nImages = images.length;

	// for each image, call preload()
	for ( var i = 0; i < images.length; i++) {
		this.preload(images[i]);
	}
}

ImagePreloader.prototype.preload = function(image) {
	// create new Image object and add to array
	var oImage = new Image;
	stendhal.data.map.aImages.push(oImage);

	// set up event handlers for the Image object
	oImage.onload = ImagePreloader.prototype.onload;
	oImage.onerror = ImagePreloader.prototype.onerror;
	oImage.onabort = ImagePreloader.prototype.onabort;

	// assign pointer back to this.
	oImage.oImagePreloader = this;
	oImage.bLoaded = false;

	// assign the .src property of the Image object
	oImage.src = image;
};

ImagePreloader.prototype.onComplete = function() {
	this.nProcessed++;
	if (this.nProcessed == this.nImages) {
		this.callback();
	}
};

ImagePreloader.prototype.onload = function() {
	this.bLoaded = true;
	this.oImagePreloader.nLoaded++;
	this.oImagePreloader.onComplete();
};

ImagePreloader.prototype.onerror = function() {
	this.bError = true;
	this.oImagePreloader.onComplete();
	console.error("Error loading " + this.src);
};

ImagePreloader.prototype.onabort = function() {
	this.bAbort = true;
	this.oImagePreloader.onComplete();
	console.error("Loading " + this.src + " was aborted");
};
 *
 * End: http://www.webreference.com/programming/javascript/gr/column3/
 */


/**
 * preloads images
 *
 * @param images image url to load
 * @param callback callback to invoke
 * @constructor
 */
export class ImagePreloader extends Function {

	oImagePreloader: ImagePreloader;
	src?: string;
	callback: Function;

	// internal state
	bLoaded: boolean;
	bError: boolean;
	bAbort: boolean;
	nLoaded: number;
	nProcessed: number;

	// number of images
	nImages: number;


	constructor(images: string[], callback: Function) {
		super();

		this.oImagePreloader = {} as ImagePreloader;
		// store the call-back
		this.callback = callback;

		// initialize internal state.
		this.bLoaded = false;
		this.bError = false;
		this.bAbort = false;
		this.nLoaded = 0;
		this.nProcessed = 0;
		stendhal.data.map.aImages = new Array();

		// record the number of images.
		this.nImages = images.length;

		// for each image, call preload()
		for (let i = 0; i  < images.length; i++) {
			this.preload(images[i]);
		}
	}

	preload(image: string) {
		// create new Image object and add to array
		const oImage = new Image() as any;
		stendhal.data.map.aImages.push(oImage);

		// set up event handlers for the Image object
		oImage.onload = ImagePreloader.prototype.onload;
		oImage.onerror = ImagePreloader.prototype.onerror;
		oImage.onabort = ImagePreloader.prototype.onabort;

		// assign pointer back to this
		oImage.oImagePreloader = this;
		oImage.bLoaded = false;

		// assign the .src property of the Image object
		oImage.src = image;
	}

	onComplete() {
		this.nProcessed++;
		if (this.nProcessed == this.nImages) {
			this.callback();
		}
	}

	onload() {
		this.bLoaded = true;
		this.oImagePreloader.nLoaded++;
		this.oImagePreloader.onComplete();
	}

	onerror() {
		this.bError = true;
		this.oImagePreloader.onComplete();
		console.error("Error loading " + this.src);
	}

	onabort() {
		this.bAbort = true;
		this.oImagePreloader.onComplete();
		console.error("Loading " + this.src + " was aborted");
	}
}
