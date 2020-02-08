/***************************************************************************
 *                   (C) Copyright 2003-2020 - Stendhal                    *
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
 * VisibleEntity
 */
marauroa.rpobjectFactory["visible_entity"] = marauroa.util.fromProto(marauroa.rpobjectFactory["entity"], {
	zIndex: 1,

	init: function() {
		this.sprite = {
			height: 32,
			width: 32
		};
	},

	set: function(key, value) {
		marauroa.rpobjectFactory["visible_entity"].proto.set.apply(this, arguments);
		if (key === "class" || key === "subclass" || key === "_name") {
			this.sprite.filename = "/data/sprites/"
				+ (this["class"] || "") + "/"
				+ (this["subclass"] || "") + "/"
				+ (this["_name"] || "") + ".png";
		} else if (key === "state") {
			this.sprite.offsetY = value * 32;
		}
	},

	isVisibleToAction: function(filter) {
		return true;
	},

	getCursor: function(x, y) {
		return "url(/data/sprites/cursor/look.png) 1 3, auto";
	}

});

marauroa.rpobjectFactory["plant_grower"] = marauroa.rpobjectFactory["visible_entity"];
marauroa.rpobjectFactory["block"] = marauroa.rpobjectFactory["visible_entity"];
