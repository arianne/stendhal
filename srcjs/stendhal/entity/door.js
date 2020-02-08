/***************************************************************************
 *                      (C) Copyright 2020 - Stendhal                      *
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

/**
 * Portal
 */
marauroa.rpobjectFactory["door"] = marauroa.util.fromProto(marauroa.rpobjectFactory["portal"], {
	zIndex: 5000,

	draw: function(ctx) {
		var imagePath = "/data/sprites/doors/" + this["class"] + ".png";
		var image = stendhal.data.sprites.get(imagePath);
		if (image.height) {
			var x = (this["x"] - 1) * 32;
			var y = (this["y"] - 1) * 32;
			var height = image.height / 2;

			var offsetY = height;
			if (this["open"] === "") {
				offsetY = 0;
			}
			ctx.drawImage(image, 0, offsetY, image.width, height, x, y, image.width, height);
		}
	},

	buildActions: function(list) {
		list.push({
			title: "Look",
			type: "look"
		});
		list.push({
			title: "Use",
			type: "use"
		});
	},

	isVisibleToAction: function(filter) {
		return true;
	},


	/**
	 * Create the default action for this entity. If the entity specifies a
	 * default action description, interpret it as an action command.
	 */
	getDefaultAction: function() {
		return {
			"type": "moveto",
			"x": "" + this["x"],
			"y": "" + this["y"],
			"zone": marauroa.currentZoneName
		};
	},

	getCursor: function(x, y) {
		return "url(/data/sprites/cursor/portal.png) 1 3, auto";
	}

});
