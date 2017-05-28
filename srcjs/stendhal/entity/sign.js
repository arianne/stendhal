/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
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

marauroa.rpobjectFactory["sign"] = marauroa.util.fromProto(marauroa.rpobjectFactory["entity"], {
	zIndex: 5000,
	"class": "default",
	
	draw: function(ctx) {
		if (!this.imagePath) {
			this.imagePath = "/data/sprites/signs/" + this["class"] + ".png";
		}
		var image = stendhal.data.sprites.get(this.imagePath);
		if (image.height) {
			var localX = this["x"] * 32;
			var localY = this["y"] * 32;
			ctx.drawImage(image, localX, localY);
		}
	},
	
	isVisibleToAction: function(filter) {
		return true;
	},
});