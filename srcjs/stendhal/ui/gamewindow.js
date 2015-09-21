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
			
/*			this.ctx.font = "14px Arial";
			this.ctx.fillStyle = "#000000";
			this.ctx.fillText((1000/20) - (new Date().getTime()-startTime) + "  " + Math.floor(1000 / (new Date().getTime()-startTime)), 10, 10);
			console.log((1000/20) - (new Date().getTime()-startTime), Math.floor(1000 / (new Date().getTime()-startTime)))
*/
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
					var tilesetWidth = stendhal.data.map.aImages[tileset].width;

					try {
						if (stendhal.data.map.aImages[tileset].height > 0) {
							this.ctx.drawImage(stendhal.data.map.aImages[tileset],
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

	onclick: function(e) {
		stendhal.zone.entityAt(e.offsetX + stendhal.ui.gamewindow.offsetX, 
				e.offsetY + stendhal.ui.gamewindow.offsetY).onclick(e.offsetX, e.offsetY);
		document.getElementById("chatbar").focus();
	},
	
	// Mouse handling
	onMouseDown: (function() {
		var draggedEntity;
		var startX;
		var startY;
		var inTrueDrag = false;
		
		function _onMouseDown(e) {
			e.srcElement.addEventListener("mousemove", onDrag);
			e.srcElement.addEventListener("mouseup", onMouseUp);
			startX = e.offsetX;
			startY = e.offsetY;
			
			// Drags outside the game area can confuse the state, and leave
			// garbage behind for the next click
			inTrueDrag = false;
			
			var x = e.offsetX + stendhal.ui.gamewindow.offsetX;
			var y = e.offsetY + stendhal.ui.gamewindow.offsetY;
			var entity = stendhal.zone.entityAt(x, y);
			// Not really necessarily dragged, or anything that can be dragged, but
			// a potential dragged object
			draggedEntity = entity;
		}
		
		function onMouseUp(e) {
			// Drags outside the game area can confuse the state, and a click
			// can i
			if (inTrueDrag) {
				var action = {
					"type": "drop",
					"source_path": draggedEntity.getIdPath(),
					"x": Math.floor((e.offsetX + stendhal.ui.gamewindow.offsetX) / 32).toString(),
					"y": Math.floor((e.offsetY + stendhal.ui.gamewindow.offsetY) / 32).toString(),
					"zone" : marauroa.currentZoneName
				};
				marauroa.clientFramework.sendAction(action);
			} else {
				// It was a click rather than a drag
				e.srcElement.removeEventListener("mousemove", onDrag);
				draggedEntity.onclick(e.offsetX, e.offsetY);
			}
			e.srcElement.removeEventListener("mouseup", onMouseUp);
			cleanUp();
		}
		
		function onDrag(e) {
			var xDiff = startX - e.offsetX;
			var yDiff = startY - e.offsetY;
			// // The mouse has moved a bit. Check if it's something that can
			// be dragged, or just forget about it.
			if (xDiff * xDiff + yDiff * yDiff > 5) {
				if (draggedEntity.type === "item") {
					// Start a real drag
					inTrueDrag = true;
				} else {
					// Nothing that can be dragged, and it's not a click either.
					// We are not interested in mouseup anymore.
					e.srcElement.removeEventListener("mouseup", onMouseUp);
					cleanUp();
				}
				// Not needed anymore
				e.srcElement.removeEventListener("mousemove", onDrag);
			}
		}
		
		function cleanUp() {
			draggedEntity = null;
			inTrueDrag = false;
			document.getElementById("chatbar").focus();
		}
		
		return _onMouseDown;
	})(),
};