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

var marauroa = window.marauroa = window.marauroa || {};
var stendhal = window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};

/**
 * mini map
 */
stendhal.ui.minimap = {

	width: 128,
	height: 128,
	titleHeight: 15,
	minimumScale: 2,

	zoneChange: function() {
		stendhal.ui.minimap.mapWidth = stendhal.data.map.zoneSizeX;
		stendhal.ui.minimap.mapHeight = stendhal.data.map.zoneSizeY;
		stendhal.ui.minimap.scale = Math.max(stendhal.ui.minimap.minimumScale, Math.min(stendhal.ui.minimap.height / stendhal.ui.minimap.mapHeight, stendhal.ui.minimap.width / stendhal.ui.minimap.mapWidth));
		stendhal.ui.minimap.createBackgroundImage();
	},

	updateBasePosition: function() {
		stendhal.ui.minimap.xOffset = 0;
		stendhal.ui.minimap.yOffset = 0;

		var imageWidth = stendhal.ui.minimap.mapWidth * stendhal.ui.minimap.scale;
		var imageHeight = stendhal.ui.minimap.mapHeight * stendhal.ui.minimap.scale;

		var xpos = Math.round((marauroa.me["x"] * stendhal.ui.minimap.scale) + 0.5) - stendhal.ui.minimap.width / 2;
		var ypos = Math.round((marauroa.me["y"] * stendhal.ui.minimap.scale) + 0.5) - stendhal.ui.minimap.width / 2;

		if (imageWidth > stendhal.ui.minimap.width) {
			// need to pan width
			if ((xpos + stendhal.ui.minimap.width) > imageWidth) {
				// x is at the screen border
				stendhal.ui.minimap.xOffset = imageWidth - stendhal.ui.minimap.width;
			} else if (xpos > 0) {
				stendhal.ui.minimap.xOffset = xpos;
			}
		}

		if (imageHeight > stendhal.ui.minimap.height) {
			// need to pan height
			if ((ypos + stendhal.ui.minimap.height) > imageHeight) {
				// y is at the screen border
				stendhal.ui.minimap.yOffset = imageHeight - stendhal.ui.minimap.height;
			} else if (ypos > 0) {
				stendhal.ui.minimap.yOffset = ypos;
			}
		}
	},
	
	draw: function() {
		stendhal.ui.minimap.scale = 10;
		
		stendhal.ui.minimap.zoneChange();
		stendhal.ui.minimap.updateBasePosition();
		var canvas = document.getElementById("minimap");
		
		var ctx = canvas.getContext("2d");
		// IE does not support ctx.resetTransform(), so use the following workaround:
		ctx.setTransform(1, 0, 0, 1, 0, 0);

		// The area outside of the map
		ctx.fillStyle = "#606060";
		ctx.fillRect(0, 0, stendhal.ui.minimap.width, stendhal.ui.minimap.height);
		
		ctx.translate(Math.round(-stendhal.ui.minimap.xOffset), Math.round(-stendhal.ui.minimap.yOffset));
		stendhal.ui.minimap.drawBackground(ctx);
		stendhal.ui.minimap.drawEntities(ctx);
	},
	
	drawBackground: function(ctx) {
		ctx.save();
		// imageSmoothingEnabled is the standard property but browsers haven't
		// yet caught up
		ctx.imageSmoothingEnabled = false;
		ctx.mozImageSmoothingEnabled = false;
		ctx.msImageSmoothingEnabled = false;
		
		ctx.scale(stendhal.ui.minimap.scale, stendhal.ui.minimap.scale);
		if (stendhal.ui.minimap.bgImage) {
			ctx.drawImage(stendhal.ui.minimap.bgImage, 0, 0);
		}
		ctx.restore();
	},
	
	createBackgroundImage: function() {
		var width = stendhal.ui.minimap.mapWidth;
		var height = stendhal.ui.minimap.mapHeight;
		if (width <= 0 || height <= 0) {
			return;
		}
		
		if (stendhal.data.map.collisionData !== stendhal.ui.minimap.lastZone) {
			stendhal.ui.minimap.lastZone = stendhal.data.map.collisionData;
			stendhal.ui.minimap.bgImage = document.createElement("canvas");
			var ctx = stendhal.ui.minimap.bgImage.getContext("2d");
			var imgData = ctx.createImageData(width, height);

			for (var y = 0; y < height; y++) {
				for (var x = 0; x < width; x++) {
					// RGBA array. Find the actual position
					var pos = 4 * (y * width + x);
					if (stendhal.data.map.collision(x, y)) {
						// red collision
						imgData.data[pos] = 255;
					} else if (stendhal.data.map.isProtected(x, y)) {
						// light green for protection
						imgData.data[pos] = 202;
						imgData.data[pos + 1] = 230;
						imgData.data[pos + 2] = 202;
					} else {
						// light gray elsewhere
						imgData.data[pos] = 224;
						imgData.data[pos + 1] = 224;
						imgData.data[pos + 2] = 224;
					}
					imgData.data[pos + 3] = 255;
				}
			}
			stendhal.ui.minimap.bgImage.width  = width;
			stendhal.ui.minimap.bgImage.height = height;

			ctx.putImageData(imgData, 0, 0);
		}
	},
	
	drawEntities: function(ctx) {
		ctx.fillStyle = "rgb(255,0,0)";
		ctx.strokeStyle = "rgb(0,0,0)";

		for (var i in marauroa.currentZone) {
			var o = marauroa.currentZone[i];
			if (typeof(o["x"]) != "undefined" && typeof(o["y"]) != "undefined" && (o.minimapShow || (marauroa.me["adminlevel"] && marauroa.me["adminlevel"] >= 600))) {
				// not supported by IE <= 8
				if (typeof(ctx.fillText) != "undefined") {
//					stendhal.ui.minimap.ctx.fillText(o.id, o.x * stendhal.ui.minimap.scale, o.y * stendhal.ui.minimap.scale);
				}
				if (typeof(o.minimapStyle) != "undefined") {
					ctx.strokeStyle = o.minimapStyle;
				} else {
					ctx.strokeStyle = "rgb(128, 128, 128)";
				}
				ctx.strokeRect(o["x"] * stendhal.ui.minimap.scale, o["y"] * stendhal.ui.minimap.scale, o["width"] * stendhal.ui.minimap.scale, o["height"] * stendhal.ui.minimap.scale);
			}
		}
	},
	
	onClick: function(e) {
		var x = Math.floor((e.offsetX + stendhal.ui.minimap.xOffset) / stendhal.ui.minimap.scale);
		var y = Math.floor((e.offsetY + stendhal.ui.minimap.yOffset) / stendhal.ui.minimap.scale);
		if (!stendhal.data.map.collision(x, y)) {
			var action = {
					type: "moveto",
					x: x.toString(),
					y: y.toString()
			};
			marauroa.clientFramework.sendAction(action);
		}
		document.getElementById("chatinput").focus();
	}
};
