"use strict";

Polymer({
	is: "stendhal-minimap",
	
	width: 128,
	height: 128,
	titleHeight: 15,
	minimumScale: 2,

	listeners: {
		"click": "onclick"
	},

	zoneChange: function() {
		this.mapWidth = stendhal.data.map.zoneSizeX;
		this.mapHeight = stendhal.data.map.zoneSizeY;
		this.scale = Math.max(this.minimumScale, Math.min(this.height / this.mapHeight, this.width / this.mapWidth));
		this.createBackgroundImage();
	},

	updateBasePosition: function() {
		this.xOffset = 0;
		this.yOffset = 0;

		var imageWidth = this.mapWidth * this.scale;
		var imageHeight = this.mapHeight * this.scale;

		var xpos = Math.round((marauroa.me.x * this.scale) + 0.5) - this.width / 2;
		var ypos = Math.round((marauroa.me.y * this.scale) + 0.5) - this.width / 2;

		if (imageWidth > this.width) {
			// need to pan width
			if ((xpos + this.width) > imageWidth) {
				// x is at the screen border
				this.xOffset = imageWidth - this.width;
			} else if (xpos > 0) {
				this.xOffset = xpos;
			}
		}

		if (imageHeight > this.height) {
			// need to pan height
			if ((ypos + this.height) > imageHeight) {
				// y is at the screen border
				this.yOffset = imageHeight - this.height;
			} else if (ypos > 0) {
				this.yOffset = ypos;
			}
		}
	},
	
	draw: function() {
		this.scale = 10;
		
		this.zoneChange();
		this.updateBasePosition();
		var canvas = this.$.minimap;
		
		var ctx = canvas.getContext("2d");
		ctx.resetTransform();
		// The area outside of the map
		ctx.fillStyle = "#606060";
		ctx.fillRect(0, 0, this.width, this.height);
		
		ctx.translate(Math.round(-this.xOffset), Math.round(-this.yOffset));
		this.drawBackground(ctx);
		this.drawEntities(ctx);
	},
	
	drawBackground: function(ctx) {
		ctx.save();
		// imageSmoothingEnabled is the standard property but browsers haven't
		// yet caught up
		ctx.imageSmoothingEnabled = false;
		ctx.mozImageSmoothingEnabled = false;
		ctx.msImageSmoothingEnabled = false;
		
		ctx.scale(this.scale, this.scale);
		if (this.bgImage) {
			ctx.drawImage(this.bgImage, 0, 0);
		}
		ctx.restore();
	},
	
	createBackgroundImage: function() {
		var width = this.mapWidth;
		var height = this.mapHeight;
		if (width <= 0 || height <= 0) {
			return;
		}
		
		if (stendhal.data.map.collisionData !== this.lastZone) {
			this.lastZone = stendhal.data.map.collisionData;
			this.bgImage = document.createElement("canvas");
			var ctx = this.bgImage.getContext("2d");
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
			this.bgImage.width  = width;
			this.bgImage.height = height;

			ctx.putImageData(imgData, 0, 0);
		}
	},
	
	drawEntities: function(ctx) {
		ctx.fillStyle = "rgb(255,0,0)";
		ctx.strokeStyle = "rgb(0,0,0)";

		for (var i in marauroa.currentZone) {
			var o = marauroa.currentZone[i];
			if (typeof(o.x) != "undefined" && typeof(o.y) != "undefined" && (o.minimapShow || (marauroa.me.adminlevel && marauroa.me.adminlevel >= 600))) {
				// not supported by IE <= 8
				if (typeof(ctx.fillText) != "undefined") {
//					this.ctx.fillText(o.id, o.x * this.scale, o.y * this.scale);
				}
				if (typeof(o.minimapStyle) != "undefined") {
					ctx.strokeStyle = o.minimapStyle;
				} else {
					ctx.strokeStyle = "rgb(128, 128, 128)";
				}
				ctx.strokeRect(o.x * this.scale, o.y * this.scale, o.width * this.scale, o.height * this.scale);
			}
		}
	},
	
	onclick: function(e) {
		var x = Math.floor((e.offsetX + this.xOffset) / this.scale);
		var y = Math.floor((e.offsetY + this.yOffset) / this.scale);
		if (!stendhal.data.map.collision(x, y)) {
			var action = {
					type: "moveto",
					x: x.toString(),
					y: y.toString()
			};
			marauroa.clientFramework.sendAction(action);
		}
		document.getElementById("chatbar").focus();
	}
});