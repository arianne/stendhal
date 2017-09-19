/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
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
 * Item
 */
marauroa.rpobjectFactory.unknown = marauroa.util.fromProto(marauroa.rpobjectFactory.entity, {
	zIndex: 1,

	init: function() {
		marauroa.rpobjectFactory.unknown.proto.init.apply(this, arguments);
		var that = this;
		setTimeout(function() {
			console.log("Unknown entity", that._rpclass, that.x, that.y);
		}, 1);
	},
	
	isVisibleToAction: function(filter) {
		return (marauroa.me.adminlevel && marauroa.me.adminlevel >= 600);
	}
});

marauroa.rpobjectFactory._default = marauroa.rpobjectFactory.unknown;