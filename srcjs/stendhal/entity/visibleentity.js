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

	isVisibleToAction: function(filter) {
		return true;
	},

	getCursor: function(x, y) {
		return "url(/data/sprites/cursor/look.png) 1 3, auto";
	}

});

marauroa.rpobjectFactory["plant_grower"] = marauroa.rpobjectFactory["visible_entity"];
