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
	timeStamp: Date.now(),
	textSprites: [],
	
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
					this.drawTextSprites();
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
						console.error(e);
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
	
	drawTextSprites: function(ctx) {
		for (var i = 0; i < this.textSprites.length; i++) {
			var sprite = this.textSprites[i];
			var remove = sprite.draw(this.ctx);
			if (remove) {
				this.textSprites.splice(i, 1);
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
	
	addTextSprite: function(sprite) {
		this.textSprites.push(sprite);
	},
	
	// Mouse click handling
	onMouseDown: (function() {
		var entity;
		var startX;
		var startY;
		
		function _onMouseDown(e) {
			e.target.addEventListener("mousemove", onDrag);
			e.target.addEventListener("mouseup", onMouseUp);
			startX = e.offsetX;
			startY = e.offsetY;
			
			var x = e.offsetX + stendhal.ui.gamewindow.offsetX;
			var y = e.offsetY + stendhal.ui.gamewindow.offsetY;
			entity = stendhal.zone.entityAt(x, y);
		}
		
		function onMouseUp(e) {
			entity.onclick(e.offsetX, e.offsetY);
			cleanUp(e);
		}
		
		function onDrag(e) {
			var xDiff = startX - e.offsetX;
			var yDiff = startY - e.offsetY;
			// It's not really a click if the mouse has moved too much.
			if (xDiff * xDiff + yDiff * yDiff > 5) {
				cleanUp(e);
			}
		}
		
		function cleanUp(e) {
			entity = null;
			e.target.removeEventListener("mouseup", onMouseUp);
			e.target.removeEventListener("mousemove", onDrag);
			document.getElementById("chatbar").focus();
		}
		
		return _onMouseDown;
	})(),
	
	// ***************** Drag and drop ******************
	onDragStart: function(e) {
		var draggedEntity = stendhal.zone.entityAt(e.offsetX + stendhal.ui.gamewindow.offsetX,
				e.offsetY + stendhal.ui.gamewindow.offsetY);
		if (draggedEntity.type === "item") {
			var img = stendhal.data.sprites.getAreaOf(stendhal.data.sprites.get(draggedEntity.sprite.filename), 32, 32);
			e.dataTransfer.setDragImage(img, 0, 0);
			e.dataTransfer.setData("text/x-stendhal-item", draggedEntity.getIdPath());
		} else if (draggedEntity.type === "corpse") {
			e.dataTransfer.setDragImage(stendhal.data.sprites.get(draggedEntity.sprite.filename), 0, 0);
			e.dataTransfer.setData("text/x-stendhal-corpse", draggedEntity.getIdPath());
		} else {
			e.preventDefault();
		}
	},
	
	onDragOver: function(e) {
		e.preventDefault(); // Necessary. Allows us to drop.
		e.dataTransfer.dropEffect = "move";
		return false;
	},
	
	onDrop: function(e) {
		var data = e.dataTransfer.getData("text/x-stendhal-item") || e.dataTransfer.getData("text/x-stendhal-corpse");
		if (data) {
			var action = {
				"x": Math.floor((e.offsetX + stendhal.ui.gamewindow.offsetX) / 32).toString(),
				"y": Math.floor((e.offsetY + stendhal.ui.gamewindow.offsetY) / 32).toString(),
				// FIXME: This is not necessarily true. What to do when the drag
				// started on previous zone?
				// "zone" : marauroa.currentZoneName
			};
			var id = data.substr(1, data.length - 2);
			var drop = /\t/.test(id);
			if (drop) {
				action["type"] = "drop";
				action["source_path"] = data;
			} else {
				action["type"] = "displace";
				action["baseitem"] = id;
			};
			marauroa.clientFramework.sendAction(action);
		}
		e.stopPropagation();
	}
};