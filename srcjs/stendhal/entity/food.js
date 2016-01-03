"use strict";

marauroa.rpobjectFactory.food = marauroa.util.fromProto(marauroa.rpobjectFactory.entity, {
	set: function(key, value) {
		marauroa.rpobjectFactory.entity.set.apply(this, arguments);
		if (key === "amount") {
			this._amount = parseInt(value);
		}
	},
	
	draw: function(ctx) {
		var image = stendhal.data.sprites.get("/data/sprites/food.png");
		if (image.complete) {
			var localX = this.x * 32;
			var localY = this.y * 32;
			var offset = this._amount * 32;
			ctx.drawImage(image, 0, offset, 32, 32, localX, localY, 32, 32);
		}
	},
	
	onclick: function(x, y) {
		console.log(this, x, y);
		var action = {
				"type": "look",
				"target": "#" + this.id
			};
		marauroa.clientFramework.sendAction(action);
	},
	
	isVisibleToAction: function(filter) {
		return true;
	},
});