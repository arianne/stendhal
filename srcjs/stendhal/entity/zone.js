/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
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
stendhal.zone = stendhal.zone || {};


/**
 * stendhal zone
 */
stendhal.zone = {
	getEntitiesAt: function(x, y, filter) {
		const xGrid = x / 32;
		const yGrid = y / 32;
		const entities = [];
		for (const i in stendhal.zone.entities) {
			const obj = stendhal.zone.entities[i];
			if (obj.isVisibleToAction(filter)
					&& (obj["_x"] <= xGrid) && (obj["_y"] <= yGrid)
					&& (obj["_x"] + (obj["width"] || 1) >= xGrid)
					&& (obj["_y"] + (obj["height"] || 1) >= yGrid)) {

				entities.push(obj);
			}
		}

		return entities;
	},

	entityAt: function(x, y, filter) {
		let res = stendhal.zone.ground;
		for (const obj of stendhal.zone.getEntitiesAt(x, y, filter)) {
			res = obj;
		}

		// If we found an entity, return it
		if (res != stendhal.zone.ground) {
			return res;
		}

		// Otherwise we check the draw area
		for (var i in stendhal.zone.entities) {
			let obj = stendhal.zone.entities[i];
			if (!obj.isVisibleToAction(filter) || !obj["drawHeight"]) {
				continue;
			}
			let localX = obj["_x"] * 32;
			let localY = obj["_y"] * 32;
			let drawHeight = obj["drawHeight"];
			let drawWidth = obj["drawWidth"];
			var drawX = ((obj["width"] * 32) - drawWidth) / 2;
			var drawY = (obj["height"] * 32) - drawHeight;

			if ((localX + drawX <= x) && (localX + drawX + drawWidth >= x)
				&& (localY + drawY <= y) && (localY + drawY + drawHeight >= y)) {

				res = obj;
			}
		}

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
};
