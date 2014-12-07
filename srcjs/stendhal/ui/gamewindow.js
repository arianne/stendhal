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
	/** screen offsets in pixels. */
	offsetX: 0,
	offsetY: 0,
	timeStamp: new Date().getTime(),

	draw: function() {
		var startTime = new Date().getTime();

		if (marauroa.me && document.visibilityState == "visible") {

			var canvas = document.getElementById("gamewindow");
			canvas.style.display = "none";
			this.targetTileWidth = 32;
			this.targetTileHeight = 32;
			canvas.width = stendhal.data.map.sizeX * this.targetTileWidth;
			canvas.height = stendhal.data.map.sizeY * this.targetTileHeight;
			this.drawingError = false;

			this.ctx = canvas.getContext("2d");
			this.ctx.globalAlpha = 1.0;
			this.adjustView(canvas);
			
			var tileOffsetX = Math.floor(this.offsetX / this.targetTileWidth);
			var tileOffsetY = Math.floor(this.offsetY / this.targetTileHeight);

			for (var drawingLayer=0; drawingLayer < stendhal.data.map.layers.length; drawingLayer++) {
				var name = stendhal.data.map.layerNames[drawingLayer];
				if (name != "protection" && name != "collision" && name != "objects"
					&& name != "blend_ground" && name != "blend_roof") {
					this.paintLayer(canvas, drawingLayer, tileOffsetX, tileOffsetY);
				}
				if (name == "2_object") {
					this.drawEntities();
				}
			}
			this.drawEntitiesTop();

			canvas.style.display = "block";
		}
		setTimeout(function() {
			stendhal.ui.gamewindow.draw.apply(stendhal.ui.gamewindow, arguments);
		}, Math.max((1000/20) - (new Date().getTime()-startTime), 1));
	},

	paintLayer: function(canvas, drawingLayer, tileOffsetX, tileOffsetY) {
		var layer = stendhal.data.map.layers[drawingLayer];
		var yMax = Math.min(tileOffsetY + canvas.height / this.targetTileHeight + 1, stendhal.data.map.zoneSizeY);
		var xMax = Math.min(tileOffsetX + canvas.width / this.targetTileWidth + 1, stendhal.data.map.zoneSizeX);
		for (var y = tileOffsetY; y < yMax; y++) {
			for (var x = tileOffsetX; x < xMax; x++) {
				var gid = layer[y * stendhal.data.map.numberOfXTiles + x];
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
								x * this.targetTileWidth,
								y * this.targetTileHeight,
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

	drawEntities: function() {
		var currentTime = new Date().getTime();
		var time = currentTime - this.timeStamp;
		this.timeStamp = currentTime;
		for (var i in stendhal.zone.entities) {
			var entity = stendhal.zone.entities[i];
			if (typeof(entity.draw) != "undefined") {
				entity.updatePosition(time);
				entity.draw(this.ctx);
			}
		}
	},

	drawEntitiesTop: function() {
		var i;
		for (i in stendhal.zone.entities) {
			if (typeof(stendhal.zone.entities[i].drawTop) != "undefined") {
				stendhal.zone.entities[i].drawTop(this.ctx);
			}
		}
	},
	
	adjustView: function(canvas) {
		// Coordinates for a screen centered on player
		var centerX = marauroa.me._x * this.targetTileWidth + this.targetTileWidth / 2 - canvas.width / 2;
		var centerY = marauroa.me._y * this.targetTileHeight + this.targetTileHeight / 2 - canvas.height / 2;

		// Keep the world within the screen view
		centerX = Math.min(centerX, stendhal.data.map.zoneSizeX * this.targetTileWidth - canvas.width);
		centerX = Math.max(centerX, 0);
		
		centerY = Math.min(centerY, stendhal.data.map.zoneSizeY * this.targetTileHeight - canvas.height);
		centerY = Math.max(centerY, 0);

		this.offsetX = Math.round(centerX);
		this.offsetY = Math.round(centerY);
		this.ctx.translate(-this.offsetX, -this.offsetY);
	},

	onclick: function(event) {
		// https://stackoverflow.com/questions/55677/ by Ryan Artecona
		function relMouseCoords(event){
		    var totalOffsetX = 0;
		    var totalOffsetY = 0;
		    var canvasX = 0;
		    var canvasY = 0;
		    var currentElement = event.target;
		    do {
		        totalOffsetX += currentElement.offsetLeft - currentElement.scrollLeft;
		        totalOffsetY += currentElement.offsetTop - currentElement.scrollTop;
		    }
		    while(currentElement = currentElement.offsetParent)

		    canvasX = event.pageX - totalOffsetX;
		    canvasY = event.pageY - totalOffsetY;

		    return {x:canvasX, y:canvasY}
		}
		// end
		
		var pos = relMouseCoords(event);
		stendhal.zone.entityAt(pos.x + stendhal.ui.gamewindow.offsetX, 
				pos.y + stendhal.ui.gamewindow.offsetY).onclick(pos.x, pos.y);
	}
}