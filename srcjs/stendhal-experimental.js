stendhal.ui.gamewindow.svgdraw = function() {	
			var canvas = document.getElementById("chat");
			stendhal.ui.gamewindow.pattern = {};
			var header = '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:ev="http://www.w3.org/2001/xml-events" version="1.1" baseProfile="full" width="'
					+ (stendhal.data.map.sizeX * this.targetTileWidth) + 'px" height="'
					+ (stendhal.data.map.sizeY * this.targetTileHeight) + '" viewBox="0 0 '
					+ (stendhal.data.map.sizeX * this.targetTileWidth) + ' '
					+ (stendhal.data.map.sizeY * this.targetTileHeight) + '">\r\n';

			for (var drawingLayer=0; drawingLayer < stendhal.data.map.layers.length; drawingLayer++) {
				stendhal.ui.gamewindow.svgdata = stendhal.ui.gamewindow.svgdata + "\r\n <!-- layer " + drawingLayer + " -->";
				var name = stendhal.data.map.layerNames[drawingLayer];
				if (name != "protection" && name != "collision" && name != "objects") {
					this.svgpaintLayer(drawingLayer);
				}
				if (name = "2_object") {
//					this.drawEntities();
				}
			}
			
			var pattern = stendhal.ui.gamewindow.dumpPattern();
			canvas.innerHTML = stendhal.ui.html.esc(header + pattern + stendhal.ui.gamewindow.svgdata);
//			this.drawEntitiesTop();
		}

stendhal.ui.gamewindow.dumpPattern = function() {
	var res = "";
	for (var gid in stendhal.ui.gamewindow.pattern) {
		if (stendhal.ui.gamewindow.pattern.hasOwnProperty(gid)) {
			var patternWidth = stendhal.ui.gamewindow.pattern[gid].width; 
			var patternHeight = stendhal.ui.gamewindow.pattern[gid].height; 
			var tileset = stendhal.data.map.getTilesetForGid(parseInt(gid));
			var base = stendhal.data.map.firstgids[tileset];
			var idx = gid - base;
			var tilesetWidth = aImages[tileset].width;

			try {
				if (aImages[tileset].height > 0) {

					res = res
						+ '<pattern id="p' 
						+ gid
						+ '" width="32" height="32"  patternUnits="userSpaceOnUse"><svg x="0" y="0"'
						+ ' preserveAspectRatio="xMinYMin meet" viewBox="'
						+ ((idx * stendhal.data.map.tileWidth) % tilesetWidth)
						+ ' '
						+ Math.floor((idx * stendhal.data.map.tileWidth) / tilesetWidth) * stendhal.data.map.tileHeight
						+ ' '
						+ (stendhal.data.map.tileWidth * patternWidth)
						+ ' '
						+ (stendhal.data.map.tileHeight * patternHeight)
						+ '" width="'
						+ (stendhal.data.map.tileWidth * patternWidth)
						+ '" height="'
						+ (stendhal.data.map.tileWidth * patternHeight)
						+ '">'
						+ '<image x="0" y="0" width="'
						+ aImages[tileset].width
						+ '" height="'
						+ aImages[tileset].height
						+ '" xlink:href="' + aImages[tileset].src + '" /></svg></pattern>'
				}
			} catch (e) {
				marauroa.log.error(e);
				this.drawingError = true;
			}
		}
	}
	return res;
}

stendhal.ui.gamewindow.svgpaintLayer = function(drawingLayer) {
			var layer = stendhal.data.map.layers[drawingLayer];
			for (var y=0; y < stendhal.data.map.zoneSizeY; y++) {
				for (var x=0; x < stendhal.data.map.zoneSizeX; x++) {
					var gid = layer[y * stendhal.data.map.numberOfXTiles + x];
					if (gid > 0) {
						var rect = stendhal.ui.gamewindow.discoverRect(layer, x, y);
						if (typeof(rect) != "undefined") {
							stendhal.ui.gamewindow.cleanRect(layer, rect);
							stendhal.ui.gamewindow.pattern[gid] = {width: 1, height: 1};
							stendhal.ui.gamewindow.svgdata = stendhal.ui.gamewindow.svgdata
								+ '<rect x="'
								+ (x * this.targetTileWidth)
								+ '" y="'
								+ (y * this.targetTileHeight)
								+ '" width="'
								+ (stendhal.data.map.tileWidth * (rect.x2 - x + 1))
								+ '" height="'
								+ (stendhal.data.map.tileHeight * (rect.y2 - y + 1))
								+ '" fill="url(#p'
								+ gid
								+ ') none"/>'
						}
					}
				}
			}
			

			for (var y=0; y < stendhal.data.map.zoneSizeY; y++) {
				for (var x=0; x < stendhal.data.map.zoneSizeX; x++) {
										
					var gid = layer[(this.offsetY + y) * stendhal.data.map.numberOfXTiles + (this.offsetX + x)];
					if (gid > 0) {
						var tileset = stendhal.data.map.getTilesetForGid(gid);
						var base = stendhal.data.map.firstgids[tileset];
						var idx = gid - base;
						var tilesetWidth = aImages[tileset].width;

						try {
							if (aImages[tileset].height > 0) {
								stendhal.ui.gamewindow.svgdata = stendhal.ui.gamewindow.svgdata
									+ '<svg x="'
									+ (x * this.targetTileWidth)
									+ '" y="'
									+ (y * this.targetTileHeight)
									+ '" preserveAspectRatio="xMinYMin meet" viewBox="'
									+ ((idx * stendhal.data.map.tileWidth) % tilesetWidth)
									+ ' '
									+ Math.floor((idx * stendhal.data.map.tileWidth) / tilesetWidth) * stendhal.data.map.tileHeight
									+ ' '
									+ stendhal.data.map.tileWidth
									+ ' '
									+ stendhal.data.map.tileHeight
									+ '" width="'
									+ stendhal.data.map.tileWidth
									+ '" height="'
									+ stendhal.data.map.tileHeight
									+ '">'


									+ '<image x="0" y="0" width="'
									+ aImages[tileset].width
									+ '" height="'
									+ aImages[tileset].height
									+ '" xlink:href="' + aImages[tileset].src + '" /></svg>'
							}
						} catch (e) {
							marauroa.log.error(e);
							this.drawingError = true;
						}
					}
				}
			}
		}

stendhal.ui.gamewindow.discoverRect = function(layer, x0, y0) {

		var mapWidth = stendhal.data.map.numberOfXTiles;
		var gid = layer[y0 * mapWidth + x0];

		var expandX = true;
		var expandY = true;

		var xM = x0;
		var yM = y0;

		while (expandX || expandY) {

			if (expandX) {
				xM++;
				for (var y = y0; y <= Math.min(yM, stendhal.data.map.numberOfYTiles); y++) {
					if (gid != layer[y * mapWidth + xM]) {
						expandX = false;
						xM--;
						break;
					}
				}
			}
			if (expandY) {
				yM++;
				for (var x = x0; x <= Math.min(xM, stendhal.data.map.numberOfXTiles); x++) {
					if (gid != layer[yM * mapWidth + x]) {
						expandY = false;
						yM--
						break;
					}
				}
			}
		}
		if ((x0 < xM) || (y0 < yM)) {
			return {x1: x0, y1: y0, x2: xM, y2: yM};
		}
	}


stendhal.ui.gamewindow.cleanRect = function(layer, rect) {
	for (var y = rect.y1; y <= rect.y2; y++) {
		for (var x = rect.x1; x <= rect.x2; x++) {
			layer[y * stendhal.data.map.numberOfXTiles + x] = 0;
		}
	}
}