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

marauroa.rpobjectFactory["gate"] = marauroa.util.fromProto(marauroa.rpobjectFactory["entity"], {
	zIndex: 5000,

	set: function(key, value) {
		marauroa.rpobjectFactory["entity"].set.apply(this, arguments);
		if (key === "resistance") {
			this["locked"] = parseInt(value, 10) !== 0;
		} else if (key === "image" || key === "orientation") {
			// Force re-evaluation of the sprite
			delete this["_image"];
		}
	},

	buildActions: function(list) {
		var id = this["id"];
		list.push({
			title: (this["locked"]) ? "Open" : "Close",
			action: function(entity) {
				var action = {
					"type": "use",
					"target": "#" + id,
					"zone": marauroa.currentZoneName,
				};
				marauroa.clientFramework.sendAction(action);
			}
		});
	},

	draw: function(ctx) {
		if (this._image == undefined) {
			var filename = "/data/sprites/doors/" + this["image"] + "_" + this["orientation"] + ".png";
			this._image = stendhal.data.sprites.get(filename);
		}
		if (this._image.height) {
			var xOffset = -32 * Math.floor(this._image.width / 32 / 2);
			var height = this._image.height / 2;
			var yOffset = -32 * Math.floor(height / 32 / 2);
			var localX = this["_x"] * 32 + xOffset;
			var localY = this["_y"] * 32 + yOffset;
			var yStart = (this["locked"]) ? height : 0;
			ctx.drawImage(this._image, 0, yStart, this._image.width, height, localX, localY, this._image.width, height);
		}
	},

	isVisibleToAction: function(filter) {
		return true;
	},

	onclick: function(x, y) {
		var action = {
			"type": "use",
			"target": "#" + this["id"],
			"zone": marauroa.currentZoneName
		};
		marauroa.clientFramework.sendAction(action);
	}
});
