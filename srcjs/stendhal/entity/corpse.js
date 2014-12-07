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

marauroa.rpobjectFactory.corpse = marauroa.util.fromProto(marauroa.rpobjectFactory.entity, {

	minimapShow: false,
	zIndex: 5500,

	set: function(key, value) {
		marauroa.rpobjectFactory.corpse.proto.set.apply(this, arguments);

		this.sprite = this.sprite || {};
		if (stendhal.config.gamescreen.blood && (key == "image")) {
			this.sprite.filename = "/data/sprites/corpse/" + value + ".png";
		} else if (!stendhal.config.gamescreen.blood && (key == "harmless_image")) {
			this.sprite.fFilename = "/data/sprites/corpse/" + value + ".png";
		}
	}
});