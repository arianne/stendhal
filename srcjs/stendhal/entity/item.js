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
 * Item
 */
marauroa.rpobjectFactory.item = marauroa.util.fromProto(marauroa.rpobjectFactory.entity, {


	minimapShow: false,
	minimapStyle: "rgb(0,255,0)",
	zIndex: 7000,

	init: function() {
		this.sprite = {
			height: 32,
			width: 32
		};
	},

	isVisibleToAction: function(filter) {
		return true;
	},

	set: function(key, value) {
		marauroa.rpobjectFactory.item.proto.set.apply(this, arguments);
		if (key == "class" || key == "subclass") {
			this.sprite.filename = "/data/sprites/items/" 
				+ this.class + "/" + this.subclass + ".png";
		}
	},
	
	draw: function(ctx) {
		this.drawAt(ctx, this.x * 32, this.y * 32);
	},
	
	drawAt: function(ctx, x, y) {
		if (this.sprite) {
			this.drawSpriteAt(ctx, x, y);
			var text = this.formatQuantity();
			var textMetrics = ctx.measureText(text);
			this.drawOutlineText(ctx, text, "white", x + (32 - textMetrics.width) / 2, y + 6);
		}
	},
	
	formatQuantity: function() {
		if (!this.quantity || this.quantity === "1") {
			return "";
		}
		if (this.quantity > 10000000) {
			return Math.floor(this.quantity / 1000000) + "m"; 
		}
		if (this.quantity > 10000) {
			return Math.floor(this.quantity / 1000) + "k"; 
		}
		return this.quantity;
	}
});

