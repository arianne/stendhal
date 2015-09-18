marauroa.rpobjectFactory.sheep = marauroa.util.fromProto(marauroa.rpobjectFactory.rpentity, {
	drawSprite: function(ctx, filename) {
		var localX = this._x * 32;
		var localY = this._y * 32;
		var image = stendhal.data.sprites.get("/data/sprites/sheep.png");
		if (image.complete) {
			var nFrames = 3;
			var nDirections = 4;
			var yRow = this.dir - 1;
			if (this.weight >= 60) {
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

	onclick: function(x, y) {
		marauroa.log.debug(this, x, y);
		var action = {
				"type": "look",
				"target": "#" + this.id
			};
		marauroa.clientFramework.sendAction(action);
	}
});