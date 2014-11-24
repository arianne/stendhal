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
stendhal.ui = stendhal.ui || {};

/**
 * game window aka world view
 */
stendhal.ui.gamewindow = {
	offsetX: 0,
	offsetY: 0,

	draw: function() {
		var startTime = new Date().getTime();
		var canvas = document.getElementById("gamewindow");
		canvas.style.display = "none";
		this.targetTileWidth = 32;
		this.targetTileHeight = 32;
		canvas.width = stendhal.data.map.sizeX * this.targetTileWidth;
		canvas.height = stendhal.data.map.sizeY * this.targetTileHeight;
		this.drawingError = false;

		this.ctx = canvas.getContext("2d");
		this.ctx.globalAlpha = 1.0;

		for (var drawingLayer=0; drawingLayer < stendhal.data.map.layers.length; drawingLayer++) {
			var name = stendhal.data.map.layerNames[drawingLayer];
			if (name != "protection" && name != "collision" && name != "objects") {
				this.paintLayer(drawingLayer);
			}
			if (name == "2_object") {
				this.drawEntities();
			}
		}
		this.drawEntitiesTop();

		canvas.style.display = "block";

		setTimeout(function() {
			stendhal.ui.gamewindow.draw.apply(stendhal.ui.gamewindow, arguments);
		}, Math.max((1000/20) - (new Date().getTime()-startTime), 1));
	},

	paintLayer: function(drawingLayer) {
		var layer = stendhal.data.map.layers[drawingLayer];
		for (var y=0; y < Math.min(stendhal.data.map.zoneSizeY, stendhal.data.map.sizeY); y++) {
			for (var x=0; x < Math.min(stendhal.data.map.zoneSizeX, stendhal.data.map.sizeX); x++) {
				var gid = layer[(this.offsetY + y) * stendhal.data.map.numberOfXTiles + (this.offsetX + x)];
				if (gid > 0) {
					var tileset = stendhal.data.map.getTilesetForGid(gid);
					var base = stendhal.data.map.firstgids[tileset];
					var idx = gid - base;
					var tilesetWidth = aImages[tileset].width;

					try {
						if (aImages[tileset].height > 0) {
							this.ctx.drawImage(aImages[tileset],
								(idx * stendhal.data.map.tileWidth) % tilesetWidth, Math.floor((idx * stendhal.data.map.tileWidth) / tilesetWidth) * stendhal.data.map.tileHeight, 
								stendhal.data.map.tileWidth, stendhal.data.map.tileHeight, 
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

	// TODO: sort marauroa.currentZone[i] by z-order and position
	drawEntities: function() {
		var i;
		for (i in marauroa.currentZone) {
			if (typeof(marauroa.currentZone[i].draw) != "undefined") {
				marauroa.currentZone[i].draw(this.ctx, this.offsetX, this.offsetY);
			}
		}
	},

	// TODO: sort marauroa.currentZone[i] by z-order and position
	drawEntitiesTop: function() {
		var i;
		for (i in marauroa.currentZone) {
			if (typeof(marauroa.currentZone[i].drawTop) != "undefined") {
				marauroa.currentZone[i].drawTop(this.ctx, this.offsetX, this.offsetY);
			}
		}
	}
}