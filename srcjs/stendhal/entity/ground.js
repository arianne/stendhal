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

stendhal.zone = stendhal.zone || {};

/**
 * a pseudo entity which represents the ground
 */
stendhal.zone.ground = {

	isVisibleToAction: function(filter) {
		return false;
	},

	
	/**
	 * Calculates whether the click was close enough to a zone border to trigger
	 * a zone change.
	 *
	 * @param x x of click point in world coordinates
	 * @param y y of click point in world coordinates
	 * @return Direction of the zone to change to, <code>null</code> if no zone change should happen
	 */
	calculateZoneChangeDirection: function(x, y) {
		if (x < 15) {
			return "4"; // LEFT
		}
		if (x > stendhal.data.map.zoneSizeX * 32 - 15) {
			return "2"; // RIGHT
		}
		if (y < 15) {
			return "1"; // UP
		}
		if (y > stendhal.data.map.zoneSizeY * 32 - 15) {
			return "3"; // DOWN
		}
		return null;
	},

	onclick: function(x, y) {
		var gameX = x + stendhal.ui.gamewindow.offsetX;
		var gameY = y + stendhal.ui.gamewindow.offsetY;
		var action = {
			"type": "moveto", 
			"x": "" + Math.floor(gameX / 32),
			"y": "" + Math.floor(gameY / 32)
		};
		var extend = this.calculateZoneChangeDirection(gameX, gameY);
		if (extend) {
			action.extend = extend;
		}
		marauroa.clientFramework.sendAction(action);
	}

};