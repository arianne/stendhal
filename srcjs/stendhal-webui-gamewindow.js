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

	

	var lastMap = ""

	var offsetX = 0;
	var offsetY = 0;
	var sizeX = 23;
	var sizeY = 17

	var tileWidth = 32;
	var tileHeight = 32;
	var zoom = 100;

	var aImages;
	var layerNames;
	var layers;
	var firstgids;
	var numberOfXTiles;
	var numberOfYTiles;

	var drawingError = false;
	var drawingLayer = 0;
	var targetTileWidth = 0;
	var targetTileHeight = 0;

	var counter = 0;
	var starttime = new Date().getTime();
	var balloonY = 0;

	function draw() {
		var startTime = new Date().getTime();
		counter++;
		var canvas = document.getElementById("gamewindow");
		canvas.style.display = "none";
		targetTileWidth = Math.floor(tileWidth * zoom / 100);
		targetTileHeight = Math.floor(tileHeight * zoom / 100);
		canvas.width = sizeX * targetTileWidth;
		canvas.height = sizeY * targetTileHeight;
		drawingError = false;
		drawingLayer = 0;

		var ctx = canvas.getContext("2d");
		ctx.globalAlpha = 1.0;

		for (var drawingLayer=0; drawingLayer < layers.length; drawingLayer++) {
			var name = layerNames[drawingLayer];
			if (name != "protection" && name != "collision" && name != "objects") {
				paintLayer(ctx, drawingLayer);
			}
		}

		balloonY--;
		if (balloonY < -64) {
			balloonY = (sizeY * targetTileHeight);
		}

		ctx.globalAlpha = 1.0;
		ctx.drawImage(aImages[aImages.length - 4],
				0, 0, 48, 64,
				(sizeX * targetTileWidth) / 2 - 24, balloonY, 48, 64);


		canvas.style.display = "block";

		setTimeout("draw()", Math.max(48 - (new Date().getTime()-startTime), 1));
	}

	function paintLayer(ctx, drawingLayer) {
		var layer = layers[drawingLayer];
		for (var y=0; y < sizeY; y++) {
			for (var x=0; x < sizeX; x++) {
				var gid = layer[(offsetY + y) * numberOfXTiles + (offsetX + x)];
				if (gid > 0) {
					var tileset = getTilesetForGid(gid);
					var base = firstgids[tileset];
					var idx = gid - base;
					var tilesetWidth = aImages[tileset].width;

					try {
						if (aImages[tileset].height > 0) {
							ctx.drawImage(aImages[tileset],
								(idx * tileWidth) % tilesetWidth, Math.floor((idx * tileWidth) / tilesetWidth) * tileHeight, tileWidth, tileHeight, 
								x * targetTileWidth, y * targetTileHeight, targetTileWidth, targetTileHeight);
						}
					} catch (e) {
						marauroa.log.error(e);
						drawingError = true;
					}
				}
			}
		}
	}

	/**
	 * Returns the index of the tileset a tile belongs to.
	 */
	function getTilesetForGid(value) {
		var pos;
		for (pos = 0; pos < firstgids.length; pos++) {
			if (value < firstgids[pos]) {
				break;
			}
		}
		return pos - 1;
	}

	var httpRequest;
	function makeRequest(url, callback) {
		if (window.XMLHttpRequest) {
			httpRequest = new XMLHttpRequest();
		} else if (window.ActiveXObject) {
			try {
				httpRequest = new ActiveXObject("Msxml2.XMLHTTP");
			} catch (e) {
				httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
			}
		}
		if (httpRequest.overrideMimeType) {
			httpRequest.overrideMimeType('text/xml');
		}
		httpRequest.onreadystatechange = callback;
		httpRequest.open('GET', url, true);
		httpRequest.send(null);
	}


	/**
	 * parses the map file, loads the tileset and resizes the canvas.
	 */
	function parseMap() {
		if (httpRequest.readyState != 4) {
			return;
		}
		if (httpRequest.status != 200) {
			marauroa.log.error("Error: Could not find map file.");
			return;
		}
		var xmldoc = httpRequest.responseXML;
		var root = xmldoc.getElementsByTagName('map').item(0);
		var images = new Array;
		firstgids = new Array;
		layers = new Array;
		layerNames = new Array;

		tileWidth = +root.getAttribute("tilewidth");
		tileHeight = +root.getAttribute("tileheight");

		for (var iNode = 0; iNode < root.childNodes.length; iNode++) {
			var node = root.childNodes.item(iNode);
			if (node.nodeName == "tileset") {
				filename = getTilesetFilename(node)
				images.push(filename);
				firstgids.push(node.getAttribute("firstgid"));
			} else if (node.nodeName == "layer") {
				var layerName = node.getAttribute("name");
				var data = node.getElementsByTagName("data")[0];
				var mapData = data.firstChild.nodeValue.trim();
				var decoder = new JXG.Util.Unzip(JXG.Util.Base64.decodeAsArray(mapData));
				var data = decoder.unzip()[0][0];
				readLayer(layerName, data);
			}
		}
		images.push("/data/sprites/outfit/detail_1.png")
		images.push("/data/sprites/outfit/detail_2.png")
		images.push("/data/sprites/outfit/detail_3.png")
		images.push("/data/sprites/outfit/detail_4.png")
		new ImagePreloader(images, draw);

		numberOfXTiles = root.getAttribute("width")
		numberOfYTiles = root.getAttribute("height")
	}

	function getTilesetFilename(node) {
		var image = node.getElementsByTagName("image");
		var name = node.getAttribute("name");
		if (image.length > 0) {
			name = image[0].getAttribute("source")
		}
		return "/" + name.replace(/\.\.\/\.\.\//g, "");
	}

	/**
	 * reads the tile information for a layer
	 */
	function readLayer(name, dataString) {
		var layer = new Array;
		data = dataString;
		for (var i = 0; i < data.length - 3; i=i+4) {
			var tileId = (data.charCodeAt(i) >>> 0)
				+ (data.charCodeAt(i + 1) << 8)
				+ (data.charCodeAt(i + 2) << 16)
				+ (data.charCodeAt(i + 3) << 24);
			layer.push(tileId)
		}
		layerNames.push(name);
		layers.push(layer);
	}

	function load(location) {
		if (lastMap != location) {
			lastMap = location;
			var body = document.getElementById("body")
			body.style.cursor = "wait";
			makeRequest("/tiled/" + escape(location), parseMap);
		}
	}
