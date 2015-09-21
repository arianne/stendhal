"use strict";

marauroa.rpobjectFactory.sign = marauroa.util.fromProto(marauroa.rpobjectFactory.entity, {
	zIndex: 5000,
	"class": "default",
	
	draw: function(ctx) {
		if (!this.imagePath) {
			this.imagePath = "/data/sprites/signs/" + this["class"] + ".png";
		}
		var image = stendhal.data.sprites.get(this.imagePath);
		if (image.complete) {
			var localX = this.x * 32;
			var localY = this.y * 32;
			ctx.drawImage(image, localX, localY);
		}
	},
	
	isVisibleToAction: function(filter) {
		return true;
	},
});