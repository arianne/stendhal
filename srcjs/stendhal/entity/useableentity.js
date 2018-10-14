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

marauroa.rpobjectFactory["useable_entity"] = marauroa.util.fromProto(marauroa.rpobjectFactory["entity"], {
	zIndex: 3000,
	action: "use",

	init: function() {
		this.sprite = {
			height: 32,
			width: 32
		};
	},

	set: function(key, value) {
		marauroa.rpobjectFactory["entity"].set.apply(this, arguments);
		if (key === "class" || key === "name") {
			this.sprite.filename = "/data/sprites/"
				+ this["class"] + "/" + this["_name"] + ".png";
		}
		if (key === "state") {
			this.sprite.offsetY = this["state"] * 32;
		}
	},

	isVisibleToAction: function(filter) {
		return true;
	},

});

}());
