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

/**
 * RPEntity
 */
marauroa.rpobjectFactory.rpentity = marauroa.util.fromProto(marauroa.rpobjectFactory.activeEntity, {
	drawY: 0,
	spritePath: "",
	titleStyle: "#FFFFFF",

	set: function(key, value) {
		marauroa.rpobjectFactory.rpentity.proto.set.apply(this, arguments);
		if (key == "text") {
			this.say(value);
		}
	},

	/** 
	 * says a text
	 */
	say: function (text) {
		if (marauroa.me.isInHearingRange(this)) {
			if (text.match("^!me") == "!me") {
				stendhal.ui.chatLog.addLine("emote", text.replace(/^!me/, this.title));
			} else {
				stendhal.ui.chatLog.addLine("normal", this.title + ": " + text);
			}
		}
	},

	/** 
	 * draw RPEntities
	 */
	draw: function(ctx) {
		var filename;
		if (typeof(this.outfit) != "undefined") {
			filename = "/data/sprites/outfit/player_base_" + (this.outfit % 100) + ".png";
			this.drawSprite(ctx, filename)
			filename = "/data/sprites/outfit/dress_" + (Math.floor(this.outfit/100) % 100) + ".png";
			this.drawSprite(ctx, filename)
			filename = "/data/sprites/outfit/head_" + (Math.floor(this.outfit/10000) % 100) + ".png";
			this.drawSprite(ctx, filename)
			filename = "/data/sprites/outfit/hair_" + (Math.floor(this.outfit/1000000) % 100) + ".png";
			this.drawSprite(ctx, filename)
		} else {
			filename = "/data/sprites/" + this.spritePath + "/" + this["class"];
			if (typeof(this.subclass) != "undefined") {
				filename = filename + "/" + this["subclass"];
			}
			filename = filename + ".png";
			this.drawSprite(ctx, filename)
		}
	}
});

marauroa.rpobjectFactory.rpentity.drawSprite = function(ctx, filename) {
	var localX = this._x * 32;
	var localY = this._y * 32;
	var image = stendhal.data.sprites.get(filename);
	if (image.complete) {
		// TODO: animate
		var drawHeight = image.height / 4;
		var drawWidth = image.width / 3;
		var drawX = ((this.width * 32) - drawWidth) / 2;
		var drawY = (this.height * 32) - drawHeight;
		ctx.drawImage(image, 0, (this.dir - 1) * drawHeight, drawWidth, drawHeight, localX + drawX, localY + drawY, drawWidth, drawHeight);
	}
}


marauroa.rpobjectFactory.rpentity.drawTop = function(ctx) {
	var localX = this._x * 32;
	var localY = this._y * 32;
	if (typeof(this.title) != "undefined") {
		var textMetrics = ctx.measureText(this.title);
		ctx.font = "14px Arial";
		ctx.fillStyle = "#A0A0A0";
		ctx.fillText(this.title, localX + (this.width * 32 - textMetrics.width) / 2+2, localY - 32);
		ctx.fillStyle = this.titleStyle;
		ctx.fillText(this.title, localX + (this.width * 32 - textMetrics.width) / 2, localY - 32);
	}
}
