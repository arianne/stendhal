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
 * General entity
 */
marauroa.rpobjectFactory.entity = marauroa.util.fromProto(marauroa.rpobjectFactory._default, { 
	minimapShow: false,
	minimapStyle: "rgb(200,255,200)",
	zIndex: 10000,

	set: function(key, value) {
		marauroa.rpobjectFactory.entity.proto.set.apply(this, arguments);
		if (key == 'name') {
			if (typeof(this['title']) == "undefined") {
				this['title'] = value;
			}
		} else if (['x', 'y', 'height', 'width'].indexOf(key) > -1) {
			this[key] = parseInt(value);
		} else {
			this[key] = value;
		}
	},

	/**
	 *  Ensure that the drawing code can rely on _x and _y
	 */
	updatePosition: function(time) {
		if (this._y == undefined) {
			this._y = this.y;
		}
		if (this._x == undefined) {
			this._x = this.x;
		}
	},

	draw: function(ctx) {
		if (this.sprite) {
			this.drawSprite(ctx);
		}
	},

	/**
	 * draws a standard sprite
	 */
	drawSprite: function(ctx) {
		var image = stendhal.data.sprites.get(this.sprite.filename);
		if (image.complete) {
			var localX = this.x * 32;
			var localY = this.y * 32;
			var offsetX = this.sprite.offsetX || 0;
			var offsetY = this.sprite.offsetY || 0;
			var width = this.sprite.width || image.width;
			var height = this.sprite.height || image.height;
			ctx.drawImage(image, offsetX, offsetY, width, height, localX, localY, width, height);
		}
	},

	onclick: function(x, y) {
		marauroa.log.debug(this, x, y);
		var action = {
				"type": "look", 
				"target": "#" + this.id
			};
		marauroa.clientFramework.sendAction(action);
	}
});



marauroa.rpobjectFactory._default = marauroa.rpobjectFactory.entity;