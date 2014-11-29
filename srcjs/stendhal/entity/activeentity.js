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
 * ActiveEntity
 */
marauroa.rpobjectFactory.activeEntity = marauroa.util.fromProto(marauroa.rpobjectFactory.entity, {

	updatePosition: function(time) {
		var serverX = parseFloat(this.x);
		var serverY = parseFloat(this.y);
		if (this._x == undefined) {
			this._x = serverX;
		}
		if (this._y == undefined) {
			this._y = serverY;
		}

		if (this.speed > 0) {
			var oldX = this._x;
			var oldY = this._y;
			var movement = this.speed * time / 300;
			switch (this.dir) {
			case "1":
				this._y = this._y - movement;
				this._x = serverX;
				break;
			case "2":
				this._x = this._x + movement;
				this._y = serverY;
				break;
			case "3": 
				this._y = this._y + movement;
				this._x = serverX;
				break;
			case "4":
				this._x = this._x - movement;
				this._y = serverY;
			}
			if (this.collidesMap()) {
				this._x = oldX;
				this._y = oldY;
			}
		} else {
			// Restore server coordinates when the entity is not moving
			this._x = serverX;
			this._y = serverY;
		}
	},

	/**
	 * Check if the entity collides with the collision map.
	 */
	collidesMap: function() {
		var startX = Math.floor(this._x);
		var startY = Math.floor(this._y);
		var endX = Math.ceil(this._x + this.width);
		var endY = Math.ceil(this._y + this.height);
		for (var y = startY; y < endY; y++) {
			for (var x = startX; x < endX; x++) {
				if (stendhal.data.map.collision(x, y)) {
					return true;
				}
			}
		}
		return false;
	}
});