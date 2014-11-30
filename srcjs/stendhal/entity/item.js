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
marauroa.rpobjectFactory.item = marauroa.util.fromProto(marauroa.rpobjectFactory.entity, {

	minimapStyle: "rgb(0,255,0)",

	set: function(key, value) {
		marauroa.rpobjectFactory.item.proto.set.apply(this, arguments);

		this.sprite = this.sprite || {height: 32, width: 32};
		this.sprite.filename = "/data/sprites/items/" + this.class + "/" + this.subclass + ".png";
	}
});

