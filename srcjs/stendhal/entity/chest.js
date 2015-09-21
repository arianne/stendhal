"use strict";

marauroa.rpobjectFactory.chest = marauroa.util.fromProto(marauroa.rpobjectFactory.entity, {
	zIndex: 5000,
	
	set: function(key, value) {
		marauroa.rpobjectFactory.entity.set.apply(this, arguments);
		if (key === "open") {
			this.sprite.offsetY = 32;
		}
	},
	
	unset: function(key) {
		marauroa.rpobjectFactory.entity.proto.unset.call(this, key);
		if (key === "open") {
			this.sprite.offsetY = 0;
		}
	},
	
	sprite: {
		filename: "/data/sprites/chest.png",
		height: 32,
		width: 32,
	},
	
	isVisibleToAction: function(filter) {
		return true;
	},
});