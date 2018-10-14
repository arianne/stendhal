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

/**
 * Creature
 */
marauroa.rpobjectFactory["growing_entity_spawner"] = marauroa.util.fromProto(marauroa.rpobjectFactory["entity"], {
	zIndex: 3000,

	/**
	 * is this entity visible to a specific action
	 *
	 * @param filter 0: short left click
	 * @return true, if the entity is visible, false otherwise
	 */
	isVisibleToAction: function(filter) {
		return true;
	},

	buildActions: function(list) {
		if (!this["menu"]) {
			list.push({
				title: "Harvest",
				type: "use",
			});
		}
		marauroa.rpobjectFactory["growing_entity_spawner"].proto.buildActions.apply(this, arguments);
	},

	onclick: function(x, y) {
		var action = {
			"type": "use",
			"target": "#" + this["id"],
			"zone": marauroa.currentZoneName
		};
		marauroa.clientFramework.sendAction(action);
	},

	/**
	 * draw RPEntities
	 */
	draw: function(ctx) {
		var localX = this["x"] * 32;
		var localY = this["y"] * 32;
		var image = stendhal.data.sprites.get("data/sprites/" + this["class"] + ".png");
		if (image.height) { // image.complete is true on missing image files
			var count = parseInt(this["max_ripeness"], 10) + 1;
			var drawHeight = image.height / count;
			var yRow = this["ripeness"];
			ctx.drawImage(image, 0, yRow * drawHeight, image.width, drawHeight,
					localX, localY - drawHeight + 32, image.width, drawHeight);
		}
	}
});
