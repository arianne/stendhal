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

/**
 * Unknown entity
 */
marauroa.rpobjectFactory["unknown"] = marauroa.util.fromProto(marauroa.rpobjectFactory["entity"], {
	zIndex: 1,

	init: function() {
		marauroa.rpobjectFactory["unknown"].proto.init.apply(this, arguments);
		var that = this;
		setTimeout(function() {
			if (that["_rpclass"]) {
				console.log("Unknown entity", that["_rpclass"], "at", marauroa.currentZoneName, that["x"], that["y"], "is", that);
			}
		}, 1);
	},

	isVisibleToAction: function(filter) {
		return (marauroa.me["adminlevel"] && marauroa.me["adminlevel"] >= 600);
	}
});

marauroa.rpobjectFactory["_default"] = marauroa.rpobjectFactory["unknown"];
