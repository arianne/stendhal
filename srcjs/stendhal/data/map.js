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

	lastMap : "",

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
	numberOfXTiles : -1,
	numberOfYTiles : -1,

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

	httpRequest: -1,
	requestMap: function(url) {
		if (window.XMLHttpRequest) {
			this.httpRequest = new XMLHttpRequest();
		} else if (window.ActiveXObject) {
			try {
				this.httpRequest = new ActiveXObject("Msxml2.XMLHTTP");
			} catch (e) {
				this.httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
			}
		}
		if (this.httpRequest.overrideMimeType) {
			this.httpRequest.overrideMimeType('text/xml');
		}
		this.httpRequest.onreadystatechange = function() {
			stendhal.data.map.parseMap.apply(stendhal.data.map, arguments);
		};
		this.httpRequest.open('GET', url, true);
		this.httpRequest.send(null);
	},


	/**
	 * parses the map file, loads the tileset and resizes the canvas.
	 */
	parseMap: function() {
		if (this.httpRequest.readyState != 4) {
			return;
		}
		if (this.httpRequest.status != 200) {
			console.error("Error: Could not find map file.");
			return;
		}
		var xmldoc = this.httpRequest.responseXML;
		var root = xmldoc.getElementsByTagName('map').item(0);
		var images = new Array;
		this.firstgids = new Array;
		this.layers = new Array;
		this.layerNames = new Array;

		this.tileWidth = +root.getAttribute("tilewidth");
		this.tileHeight = +root.getAttribute("tileheight");
		this.zoneSizeX = +root.getAttribute("width");
		this.zoneSizeY = +root.getAttribute("height");

		for (var iNode = 0; iNode < root.childNodes.length; iNode++) {
			var node = root.childNodes.item(iNode);
			if (node.nodeName == "tileset") {
				var filename = this.getTilesetFilename(node);
				images.push(filename);
				this.firstgids.push(node.getAttribute("firstgid"));
			} else if (node.nodeName == "layer") {
				var layerName = node.getAttribute("name");
				var data = node.getElementsByTagName("data")[0];
				var mapData = data.firstChild.nodeValue.trim();
				var decoder = new JXG.Util.Unzip(JXG.Util.Base64.decodeAsArray(mapData));
				var data = decoder.unzip()[0][0];
				this.readLayer(layerName, data);
				if (layerName == "collision") {
					this.collisionData = this.layers[this.layers.length - 1];
				} else if (layerName === "protection") {
					this.protection = this.layers[this.layers.length - 1];
				}
			}
		}
		new ImagePreloader(images, function() {
			var body = document.getElementById("body");
			body.style.cursor = "auto";
		});

		this.numberOfXTiles = root.getAttribute("width");
		this.numberOfYTiles = root.getAttribute("height");

		// create a lookup table from gid to tileset index for a significant performance reasons
		this.gidsindex = new Array;
		var pos, lastStart = 0, i;
		for (pos = 0; pos < parseInt(this.firstgids.length, 10); pos++) {
			for (i=lastStart; i<this.firstgids[pos]; i++) {
				this.gidsindex.push(pos - 1);
			}
			lastStart = parseInt(this.firstgids[pos], 10);
		}
	},

	getTilesetFilename: function(node) {
		var image = node.getElementsByTagName("image");
		var name = node.getAttribute("name");
		if (image.length > 0) {
			name = image[0].getAttribute("source");
		}
		return "/" + name.replace(/\.\.\/\.\.\//g, "");
	},

	/**
	 * reads the tile information for a layer
	 */
	readLayer: function(name, dataString) {
		var layer = new Array;
		var data = dataString;
		for (var i = 0; i < data.length - 3; i=i+4) {
			var tileId = (data.charCodeAt(i) >>> 0)
				+ (data.charCodeAt(i + 1) << 8)
				+ (data.charCodeAt(i + 2) << 16)
				+ (data.charCodeAt(i + 3) << 24);
			layer.push(tileId);
		}
		this.layerNames.push(name);
		this.layers.push(layer);
	},

	load: function(locat) {
		var filename = "";
		if (this.lastMap != locat) {
			this.lastMap = locat;
			var body = document.getElementById("body");
			body.style.cursor = "wait";
			console.log(locat);
			var temp = /([^_]*)_([^_]*)_(.*)/.exec(locat);
			if (temp) {
				if (temp[1] == "int") {
					temp[1] = "interiors";
				} else {
					temp[1] = "Level " + temp[1];
				}
				filename = "/tiled/" + escape(temp[1]) + "/" + escape(temp[2]) + "/" + escape(temp[3]) + ".tmx";
			} else {
				var temp = /[^_]*_(.*)/.exec(locat);
				filename = "/tiled/interiors/abstract/" + escape(temp[1]) + ".tmx";
			}
			this.requestMap(filename);
		}
	},
	
	collision: function(x, y) {
		return this.collisionData[y * stendhal.data.map.numberOfXTiles + x] != 0;
	},
	
	isProtected: function(x, y) {
		return this.protection[y * stendhal.data.map.numberOfXTiles + x] != 0;
	}
};