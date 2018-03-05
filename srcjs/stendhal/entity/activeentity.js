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
 * ActiveEntity
 */
marauroa.rpobjectFactory["activeEntity"] = marauroa.util.fromProto(marauroa.rpobjectFactory["entity"], {

	updatePosition: function(time) {
		var serverX = parseFloat(this["x"]);
		var serverY = parseFloat(this["y"]);
		if (this["_x"] == undefined) {
			this["_x"] = serverX;
		}
		if (this["_y"] == undefined) {
			this["_y"] = serverY;
		}

		if (this["speed"] > 0) {
			var oldX = this["_x"];
			var oldY = this["_y"];
			var movement = this["speed"] * time / 300;
			switch (this["dir"]) {
			case "1":
				this["_y"] = this["_y"] - movement;
				this["_x"] = serverX;
				break;
			case "2":
				this["_x"] = this["_x"] + movement;
				this["_y"] = serverY;
				break;
			case "3": 
				this["_y"] = this["_y"] + movement;
				this["_x"] = serverX;
				break;
			case "4":
				this["_x"] = this["_x"] - movement;
				this["_y"] = serverY;
			}

			// fix desynchronized position (can happen if game tab is in background)
			if (Math.abs(this["_x"] - serverX) > 1.75) {
				this["_x"] = serverX;
			}
			if (Math.abs(this["_y"] - serverY) > 1.75) {
				this["_y"] = serverY;
			}

			if (this.collidesMap() || this.collidesEntities()) {
				this["_x"] = oldX;
				this["_y"] = oldY;
			}
		} else {
			// Restore server coordinates when the entity is not moving
			this["_x"] = serverX;
			this["_y"] = serverY;
		}
	},

	/**
	 * Check if the entity collides with the collision map.
	 */
	collidesMap: function() {
		var startX = Math.floor(this["_x"]);
		var startY = Math.floor(this["_y"]);
		var endX = Math.ceil(this["_x"] + this["width"]);
		var endY = Math.ceil(this["_y"] + this["height"]);
		for (var y = startY; y < endY; y++) {
			for (var x = startX; x < endX; x++) {
				if (stendhal.data.map.collision(x, y)) {
					return true;
				}
			}
		}
		return false;
	},


	/**
	 * Check if the entity with another entity;
	 */
	collidesEntities: function() {
		var thisStartX = Math.floor(this["_x"]);
		var thisStartY = Math.floor(this["_y"]);
		var thisEndX = Math.ceil(this["_x"] + this["width"]);
		var thisEndY = Math.ceil(this["_y"] + this["height"]);

		var i;
		for (i in stendhal.zone.entities) {
			var other = stendhal.zone.entities[i];
			if (!this.isObstacle(other)) {
				continue;
			}
			var otherStartX = Math.floor(other["_x"]);
			var otherStartY = Math.floor(other["_y"]);
			var otherEndX = Math.ceil(other["_x"] + other["width"]);
			var otherEndY = Math.ceil(other["_y"] + other["height"]);
			
			if (thisStartX < otherEndX && thisEndX > otherStartX
				&& thisStartY < otherEndY && thisEndY > otherStartY) {
				return true;
			}
		}

		return false;
	}

});