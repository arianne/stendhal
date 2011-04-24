	// Start http://www.webreference.com/programming/javascript/gr/column3/ 
	function ImagePreloader(images, callback) {
		// store the call-back
		this.callback = callback;

		// initialize internal state.
		this.nLoaded = 0;
		this.nProcessed = 0;
		aImages = new Array;

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
		aImages.push(oImage);

		// set up event handlers for the Image object
		oImage.onload = ImagePreloader.prototype.onload;
		oImage.onerror = ImagePreloader.prototype.onerror;
		oImage.onabort = ImagePreloader.prototype.onabort;

		// assign pointer back to this.
		oImage.oImagePreloader = this;
		oImage.bLoaded = false;

		// assign the .src property of the Image object
		oImage.src = image;
	}

	ImagePreloader.prototype.onComplete = function() {
		this.nProcessed++;
		if (this.nProcessed == this.nImages) {
			this.callback();
		}
	}

	ImagePreloader.prototype.onload = function() {
		this.bLoaded = true;
		this.oImagePreloader.nLoaded++;
		this.oImagePreloader.onComplete();
	}

	ImagePreloader.prototype.onerror = function() {
		this.bError = true;
		this.oImagePreloader.onComplete();
		marauroa.log.error("Error loading " + this.src);
	}

	ImagePreloader.prototype.onabort = function() {
		this.bAbort = true;
		this.oImagePreloader.onComplete();
		marauroa.log.error("Loading " + this.src + " was aborted");
	}

	// End http://www.webreference.com/programming/javascript/gr/column3/

stendhal.ui.gamewindow = {

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

	draw: function() {
		var startTime = new Date().getTime();
		var canvas = document.getElementById("gamewindow");
		canvas.style.display = "none";
		this.targetTileWidth = Math.floor(this.tileWidth * this.zoom / 100);
		this.targetTileHeight = Math.floor(this.tileHeight * this.zoom / 100);
		canvas.width = this.sizeX * this.targetTileWidth;
		canvas.height = this.sizeY * this.targetTileHeight;
		this.drawingError = false;
		this.drawingLayer = 0;

		var ctx = canvas.getContext("2d");
		ctx.globalAlpha = 1.0;

		for (var drawingLayer=0; drawingLayer < this.layers.length; drawingLayer++) {
			var name = this.layerNames[drawingLayer];
			if (name != "protection" && name != "collision" && name != "objects") {
				this.paintLayer(ctx, drawingLayer);
			}
		}
/*
		balloonY--;
		if (balloonY < -64) {
			balloonY = (sizeY * targetTileHeight);
		}

		ctx.globalAlpha = 1.0;
		ctx.drawImage(aImages[aImages.length - 4],
				0, 0, 48, 64,
				(this.sizeX * this.targetTileWidth) / 2 - 24, balloonY, 48, 64);
*/

		canvas.style.display = "block";

		setTimeout(function() {
			stendhal.ui.gamewindow.draw.apply(stendhal.ui.gamewindow, arguments);
		}, Math.max(48 - (new Date().getTime()-startTime), 1));
	},

	paintLayer: function(ctx, drawingLayer) {
		var layer = this.layers[drawingLayer];
		for (var y=0; y < Math.min(this.zoneSizeY, this.sizeY); y++) {
			for (var x=0; x < Math.min(this.zoneSizeX, this.sizeX); x++) {
				var gid = layer[(this.offsetY + y) * this.numberOfXTiles + (this.offsetX + x)];
				if (gid > 0) {
					var tileset = this.getTilesetForGid(gid);
					var base = this.firstgids[tileset];
					var idx = gid - base;
					var tilesetWidth = aImages[tileset].width;

					try {
						if (aImages[tileset].height > 0) {
							ctx.drawImage(aImages[tileset],
								(idx * this.tileWidth) % tilesetWidth, Math.floor((idx * this.tileWidth) / tilesetWidth) * this.tileHeight, 
								this.tileWidth, this.tileHeight, 
								x * this.targetTileWidth, y * this.targetTileHeight, 
								this.targetTileWidth, this.targetTileHeight);
						}
					} catch (e) {
						marauroa.log.error(e);
						this.drawingError = true;
					}
				}
			}
		}
	},

	/**
	 * Returns the index of the tileset a tile belongs to.
	 */
	getTilesetForGid: function(value) {
		var pos;
		for (pos = 0; pos < this.firstgids.length; pos++) {
			if (value < this.firstgids[pos]) {
				break;
			}
		}
		return pos - 1;
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
			stendhal.ui.gamewindow.parseMap.apply(stendhal.ui.gamewindow, arguments);
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
			marauroa.log.error("Error: Could not find map file.");
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
				var filename = this.getTilesetFilename(node)
				images.push(filename);
				this.firstgids.push(node.getAttribute("firstgid"));
			} else if (node.nodeName == "layer") {
				var layerName = node.getAttribute("name");
				var data = node.getElementsByTagName("data")[0];
				var mapData = data.firstChild.nodeValue.trim();
				var decoder = new JXG.Util.Unzip(JXG.Util.Base64.decodeAsArray(mapData));
				var data = decoder.unzip()[0][0];
				this.readLayer(layerName, data);
			}
		}
		images.push("/data/sprites/outfit/detail_1.png")
		images.push("/data/sprites/outfit/detail_2.png")
		images.push("/data/sprites/outfit/detail_3.png")
		images.push("/data/sprites/outfit/detail_4.png")
		new ImagePreloader(images, function() {
			stendhal.ui.gamewindow.draw.apply(stendhal.ui.gamewindow, arguments);
		});

		this.numberOfXTiles = root.getAttribute("width")
		this.numberOfYTiles = root.getAttribute("height")
	},

	getTilesetFilename: function(node) {
		var image = node.getElementsByTagName("image");
		var name = node.getAttribute("name");
		if (image.length > 0) {
			name = image[0].getAttribute("source")
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
			layer.push(tileId)
		}
		this.layerNames.push(name);
		this.layers.push(layer);
	},

	load: function(location) {
		if (this.lastMap != location) {
			this.lastMap = location;
			var body = document.getElementById("body")
			body.style.cursor = "wait";
			var temp = /([^_]*)_([^_]*)_(.*)/(location);
			if (temp[1] == "int") {
				temp[1] = "interiors"
			} else {
				temp[1] = "Level " + temp[1];
			}
			this.requestMap("/tiled/" + escape(temp[1]) + "/" + escape(temp[2]) + "/" + escape(temp[3]) + ".tmx");
		}
	}
}