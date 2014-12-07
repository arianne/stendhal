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
window.stendhal = window.stendhal || {};
stendhal.zone = stendhal.zone || {};


/**
 * stendhal zone
 */
stendhal.zone = {
	entityAt: function(x, y, filter) {
		x = x / 32;
		y = y / 32;
		var res = stendhal.zone.ground;
		for (var i in stendhal.zone.entities) {
			var obj = stendhal.zone.entities[i];
			if (obj.isVisibleToAction(filter) && (obj._x <= x) && (obj._y <= y)
				&& (obj._x + obj.width >= x) && (obj._y + obj.height >= y)) {

				res = obj;
			}
		}
		// TODO: check draw area
	
		return res;
	},
	
	sortEntities: function() {
		this.entities = [];
		for (var i in marauroa.currentZone) {
			if (marauroa.currentZone.hasOwnProperty(i) && typeof(marauroa.currentZone[i]) != "function") {
				this.entities.push(marauroa.currentZone[i]);
			}
		}

		this.entities.sort(function(entity1, entity2) {
			var rv = entity1.zIndex - entity2.zIndex;
			if (rv == 0) {
				rv = (entity1.y + entity1.height) - (entity2.y + entity2.height);
				if (rv == 0) {
					rv = entity1.id - entity2.id;
				}
			}

			return rv;
		});
	}
}