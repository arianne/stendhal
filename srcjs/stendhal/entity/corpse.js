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

	draw: function(ctx) {
		var name = "image";
		if (!stendhal.config.gamescreen.blood) {
			name = "harmless_image";
		}
		var filename = "/data/sprites/corpse/" + this[name] + ".png";
		this.drawSprite(ctx, filename);
	},

	drawSprite: function(ctx, filename) {
		var localX = this._x * 32;
		var localY = this._y * 32;
		var image = stendhal.data.sprites.get(filename);
		if (image.complete) {
			ctx.drawImage(image, localX, localY, image.width, image.height);
		}
	}
});