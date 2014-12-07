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

/**
 * Item
 */
marauroa.rpobjectFactory.blood = marauroa.util.fromProto(marauroa.rpobjectFactory.entity, {

	minimapShow: false,
	zIndex: 2000,

	init: function() {
		this.sprite = {
			height: 32,
			width: 32,
			filename: "/data/sprites/combat/blood_red.png"
		}
	},

	set: function(key, value) {
		marauroa.rpobjectFactory.blood.proto.set.apply(this, arguments);
		if (key == "amount") {
			this.sprite.offsetY = parseInt(value) * 32;
		}
	}
});

