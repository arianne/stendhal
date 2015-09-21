"use strict";

marauroa.rpobjectFactory.domesticanimal = marauroa.util.fromProto(marauroa.rpobjectFactory.rpentity, {
	drawSprite: function(ctx, filename) {
		var localX = this._x * 32;
		var localY = this._y * 32;
		var image = stendhal.data.sprites.get(this.imagePath);
		if (image.complete) {
			var nFrames = 3;
			var nDirections = 4;
			var yRow = this.dir - 1;
			if (this.weight >= this.largeWeight) {
				yRow += 4;
			}
			this.drawHeight = image.height / nDirections / 2;
			this.drawWidth = image.width / nFrames;
			var drawX = ((this.width * 32) - this.drawWidth) / 2;
			var frame = 0;
			if (this.speed > 0) {
				// % Works normally with *floats* (just whose bright idea was
				// that?), so use floor() as a workaround
				frame = Math.floor(Date.now() / 100) % nFrames;
			}
			var drawY = (this.height * 32) - this.drawHeight;
			ctx.drawImage(image, frame * this.drawWidth, yRow * this.drawHeight, this.drawWidth, this.drawHeight, localX + drawX, localY + drawY, this.drawWidth, this.drawHeight);
		}
	},
});

marauroa.rpobjectFactory.sheep = marauroa.util.fromProto(marauroa.rpobjectFactory.domesticanimal, {
	imagePath: "/data/sprites/sheep.png",
	largeWeight: 60
});

marauroa.rpobjectFactory.cat = marauroa.util.fromProto(marauroa.rpobjectFactory.domesticanimal, {
	imagePath: "/data/sprites/cat.png",
	largeWeight: 20
});

marauroa.rpobjectFactory.baby_dragon = marauroa.util.fromProto(marauroa.rpobjectFactory.domesticanimal, {
	imagePath: "/data/sprites/baby_dragon.png",
	largeWeight: 20,
	// A default title that does not have an underscore. Named pets set their
	// own title anyway
	title: "baby dragon"
});