stendhal.ui.gamewindow.svgdraw = function() {	
			var canvas = document.getElementById("chat");
			stendhal.ui.gamewindow.svgdata = '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:ev="http://www.w3.org/2001/xml-events" version="1.1" baseProfile="full" width="'
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
			canvas.innerHTML = stendhal.ui.gamewindow.svgdata;
//			this.drawEntitiesTop();
		}

stendhal.ui.gamewindow.svgpaintLayer = function(drawingLayer) {
			var layer = stendhal.data.map.layers[drawingLayer];
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
									+ ' 32 32" width="32" height="32">'


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
