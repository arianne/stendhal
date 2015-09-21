"use strict";

marauroa.rpobjectFactory.gate = marauroa.util.fromProto(marauroa.rpobjectFactory.entity, {
	zIndex: 5000,
	
	set: function(key, value) {
		marauroa.rpobjectFactory.entity.set.apply(this, arguments);
		if (key === "resistance") {
			this.locked = parseInt(value) != 0;
		} else if (key === "image" || key === "orientation") {
			// Force re-evaluation of the sprite
			delete this["_image"];
		}
	},
	
	draw: function(ctx) {
		if (this._image == undefined) {
			 var filename = "/data/sprites/doors/" + this.image + "_" + this.orientation + ".png";
			 this._image = stendhal.data.sprites.get(filename);
		}
		if (this._image.complete) {
			var xOffset = -32 * Math.floor(this._image.width / 32 / 2);
			var height = this._image.height / 2;
			var yOffset = -32 * Math.floor(height / 32 / 2);
			var localX = this._x * 32 + xOffset;
			var localY = this._y * 32 + yOffset;
			var yStart = (this.locked) ? height : 0;
			ctx.drawImage(this._image, 0, yStart, this._image.width, height, localX, localY, this._image.width, height);
		}
	},
	
	isVisibleToAction: function(filter) {
		return true;
	},
	
	onclick: function(x, y) {
		var action = {
			"type": "use",
			"target": "#" + this.id
		};
		marauroa.clientFramework.sendAction(action);
	}
});
