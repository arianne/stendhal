"use strict";

Polymer("stendhal-minimap", {
	
	width: 128,
	height: 128,
	titleHeight: 15,
	minimumScale: 2,

	zoneChange: function() {
		this.mapWidth = /*marauroa.currentZone.width;*/ 128;
		this.mapHeight = /*marauroa.currentZone.height;*/ 64;
		this.scale = Math.max(this.minimumScale, Math.min(this.height / this.mapHeight, this.width / this.mapWidth));
		/*final int width = Math.min(WIDTH, mapWidth * scale);
		final int height = Math.min(HEIGHT, mapHeight * scale);*/
	},

	updateBasePosition: function() {
		this.xOffset = 0;
		this.yOffset = 0;

		var imageWidth = this.mapWidth * this.scale
		var imageHeight = this.mapHeight * this.scale

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
	
	drawEntities: function() {
		this.scale = 10;
		
		this.zoneChange();
		this.updateBasePosition();
		
		var canvas = this.$.minimap;
		this.ctx = canvas.getContext("2d");
		this.ctx.fillStyle = "rgb(224,224,224)";
		this.ctx.fillRect(0, 0, canvas.width, canvas.height);
		this.ctx.fillStyle = "rgb(255,0,0)";
		this.ctx.strokeStyle = "rgb(0,0,0)";

		this.ctx.translate(Math.round(-this.xOffset), Math.round(-this.yOffset));

		for (var i in marauroa.currentZone) {
			var o = marauroa.currentZone[i];
			if (typeof(o.x) != "undefined" && typeof(o.y) != "undefined" && (o.minimapShow || (marauroa.me.adminlevel && marauroa.me.adminlevel >= 600))) {
				// not supported by IE <= 8
				if (typeof(this.ctx.fillText) != "undefined") {
//					this.ctx.fillText(o.id, o.x * this.scale, o.y * this.scale);
				}
				if (typeof(o.minimapStyle) != "undefined") {
					this.ctx.strokeStyle = o.minimapStyle;
				} else {
					this.ctx.strokeStyle = "rgb(128, 128, 128)";
				}
				this.ctx.strokeRect(o.x * this.scale, o.y * this.scale, o.width * this.scale, o.height * this.scale);
			}
		}

		this.ctx.translate(Math.round(this.xOffset), Math.round(this.yOffset));
	}
});