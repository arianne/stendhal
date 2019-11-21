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

	/**
	 * preloads images
	 *
	 * @param images image url to load
	 * @param callback callback to invoke
	 * @constructor
	 */
	// Start http://www.webreference.com/programming/javascript/gr/column3/
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

	// End http://www.webreference.com/programming/javascript/gr/column3/

stendhal.data.map = {

	currentZoneName: "",

	offsetX : 0,
	offsetY : 0,
	zoneSizeX : -1,
	zoneSizeY : -1,
	sizeX : 20,
	sizeY : 15,

	tileWidth : 32,
	tileHeight : 32,
	zoom : 100,

	aImages : -1,
	layerNames : -1,
	layers : -1,
	firstgids : -1,

	drawingError : false,
	targetTileWidth : 0,
	targetTileHeight : 0,


	/**
	 * Returns the index of the tileset a tile belongs to.
	 */
	getTilesetForGid: function(value) {
		if (value < this.gidsindex.length) {
			return this.gidsindex[value];
		} else {
			return this.gidsindex[this.gidsindex.length - 1] + 1;
		}
	},

	onTransfer: function(zoneName, content) {
		stendhal.data.map.currentZoneName = zoneName;
		stendhal.data.map.firstgids = [];
		stendhal.data.map.layers = [];
		stendhal.data.map.layerNames = [];

		var body = document.getElementById("body");
		body.style.cursor = "wait";
		console.log("load map");


		// tilesets, 0_floor, 1_terrain, 2_object, 3_roof, 4_roof_add,	blend_ground, blend_roof, protection, collision, data_map
		stendhal.data.map.decodeTileset(content, "tilesets");
		stendhal.data.map.decodeMapLayer(content, "0_floor");
		stendhal.data.map.decodeMapLayer(content, "1_terrain");
		stendhal.data.map.decodeMapLayer(content, "2_object");
		stendhal.data.map.decodeMapLayer(content, "3_roof");
		stendhal.data.map.decodeMapLayer(content, "4_roof_add");
		stendhal.data.map.protection = stendhal.data.map.decodeMapLayer(content, "protection");
		stendhal.data.map.collisionData = stendhal.data.map.decodeMapLayer(content, "collision");
	},

	decodeTileset: function(content, name) {
		var layerData = content[name];
		var deserializer = marauroa.Deserializer.fromBase64(layerData);
		var amount = deserializer.readInt();

		var images = [];
		for (var i = 0; i < amount; i++) {
			var name = deserializer.readString();
			var source = deserializer.readString();
			var firstgid = deserializer.readInt()

			var filename = "/" + source.replace(/\.\.\/\.\.\//g, "");
			images.push(filename);
			stendhal.data.map.firstgids.push(firstgid);
		}

		// create a lookup table from gid to tileset index for a significant performance reasons
		stendhal.data.map.gidsindex = [];
		var pos, lastStart = 0, i;
		for (pos = 0; pos < parseInt(stendhal.data.map.firstgids.length, 10); pos++) {
			for (i=lastStart; i<stendhal.data.map.firstgids[pos]; i++) {
				stendhal.data.map.gidsindex.push(pos - 1);
			}
			lastStart = stendhal.data.map.firstgids[pos];
		}

		new ImagePreloader(images, function() {
			var body = document.getElementById("body");
			body.style.cursor = "auto";
		});
	},

	decodeMapLayer: function(content, name) {
		var layerData = content[name];
		if (!layerData) {
			return;
		}
		var deserializer = marauroa.Deserializer.fromDeflatedBase64(layerData);
		deserializer.readString(); // zone name
		stendhal.data.map.zoneSizeX = deserializer.readInt();
		stendhal.data.map.zoneSizeY = deserializer.readInt();
		var layerRaw = deserializer.readByteArray();

		var layer = [];
		for (var i = 0; i < stendhal.data.map.zoneSizeX * stendhal.data.map.zoneSizeY * 4 - 3; i=i+4) {
			var tileId = (layerRaw.getUint8(i) >>> 0)
				+ (layerRaw.getUint8(i + 1) << 8)
				+ (layerRaw.getUint8(i + 2) << 16)
				+ (layerRaw.getUint8(i + 3) << 24);
			layer.push(tileId);
		}

		stendhal.data.map.layerNames.push(name);
		stendhal.data.map.layers.push(layer);

		return layer;
	},

	collision: function(x, y) {
		return this.collisionData[y * stendhal.data.map.zoneSizeX + x] != 0;
	},

	isProtected: function(x, y) {
		return this.protection[y * stendhal.data.map.zoneSizeX + x] != 0;
	}
};
