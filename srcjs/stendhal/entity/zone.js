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
 * adds methods to the marauroa zone
 */
stendhal.zone.entityAt = function(x, y) {
	var res = stendhal.zone.ground;
	for (var i in marauroa.currentZone) {
		if (marauroa.currentZone.hasOwnProperty(i) && typeof(marauroa.currentZone[i]) != "function") {
			var obj = marauroa.currentZone[i];
			if ((obj._x <= x) && (obj._y <= y)
				&& (obj._x + obj.width >= x) && (obj._y + obj.height >= y)) {

				// TODO: z-index
				res = obj;
			}
		}
		
		// TODO: check draw area
	}

	return res;
}