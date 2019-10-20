/***************************************************************************
 *                   (C) Copyright 2003-2018 - Stendhal                    *
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

(function() {

marauroa.rpobjectFactory["game_board"] = marauroa.util.fromProto(marauroa.rpobjectFactory["entity"], {
	zIndex: 100,

	init: function() {
		this.sprite = {
			height: 32 * 3,
			width: 32 * 3
		};
	},

	set: function(key, value) {
		marauroa.rpobjectFactory["entity"].set.apply(this, arguments);
		if (key === "class") {
			this.sprite.filename = "data/sprites/gameboard/"
				+ this["class"] + ".png";
		}
	},

	isVisibleToAction: function(filter) {
		return false;
	},

	getCursor: function(x, y) {
		return "url(/data/sprites/cursor/walk.png) 1 3, auto";
	}

});

}());
