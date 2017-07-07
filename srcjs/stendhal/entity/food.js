/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    * 
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

"use strict";

var marauroa = window.marauroa = window.marauroa || {};
var stendhal = window.stendhal = window.stendhal || {};

marauroa.rpobjectFactory["food"] = marauroa.util.fromProto(marauroa.rpobjectFactory["entity"], {
	zIndex: 5000,

	set: function(key, value) {
		marauroa.rpobjectFactory["entity"].set.apply(this, arguments);
		if (key === "amount") {
			this._amount = parseInt(value, 10);
		}
		// TODO: play sound effect
	},
	
	draw: function(ctx) {
		var image = stendhal.data.sprites.get("/data/sprites/food.png");
		if (image.height) {
			var localX = this["x"] * 32;
			var localY = this["y"] * 32;
			var offset = this._amount * 32;
			ctx.drawImage(image, 0, offset, 32, 32, localX, localY, 32, 32);
		}
	},
	
	onclick: function(x, y) {
		var action = {
				"type": "look",
				"target": "#" + this["id"]
			};
		marauroa.clientFramework.sendAction(action);
	},
	
	isVisibleToAction: function(filter) {
		return true;
	},
});