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
	 * is this entity visible to a specific action
	 *
	 * @param filter 0: short left click
	 * @return true, if the entity is visible, false otherwise
	 */
	isVisibleToAction: function(filter) {
		return false;
	},

	/**
	 *  Ensure that the drawing code can rely on _x and _y
	 */
	updatePosition: function(time) {
		// The position of non active entities can change too, so always copy
		// the position
		this._y = this.y;
		this._x = this.x;
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
		this.drawSpriteAt(ctx, this.x * 32, this.y * 32);
	},
	
	drawSpriteAt: function(ctx, x, y) {
		var image = stendhal.data.sprites.get(this.sprite.filename);
		if (image.complete) {
			var offsetX = this.sprite.offsetX || 0;
			var offsetY = this.sprite.offsetY || 0;
			var width = this.sprite.width || image.width;
			var height = this.sprite.height || image.height;
			ctx.drawImage(image, offsetX, offsetY, width, height, x, y, width, height);
		}
	},
	
	/**
	 * Draws text in specified color with black outline. Setting the font is the
	 * caller's responsibility.
	 * 
	 * @param ctx graphics context
	 * @param color text inner color
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	drawOutlineText: function(ctx, text, color, x, y) {
		ctx.lineWidth = 2;
		ctx.strokeStyle = "black";
		ctx.fillStyle = color;
		ctx.lineJoin = "round";
		ctx.strokeText(text, x, y);
		ctx.fillText(text, x, y);
	},

	/**
	 * gets the container path identifying the item
	 *
	 * @returns
	 */
	getIdPath: function() {
		var object = this;
		var res = "";
		while (object) {
			res = object.id + "\t" + res;
			var slot = object._parent;
			if (!slot) {
				break;
			}
			res = slot._name + "\t" + res;
			object = slot._parent;
		}
		return "[" + res.substr(0, res.length - 1) + "]";
	},

	/** 
	 * says a text
	 */
	say: function (text) {
		if (marauroa.me.isInHearingRange(this)) {
			stendhal.ui.chatLog.addLine("normal", text);
		}
	},
	
	/**
	 * Create the default action for this entity. If the entity specifies a
	 * default action description, interpret it as an action command.
	 */
	getDefaultAction: function() {
		// Map descriptive command names to the real commands
		var actionAliases = {
			"look_closely" : "use",
			"read" : "look"
		};
		
		var actionCommand = "look";
		var act = this["action"];
		if (typeof(act) === "string") {
			if (actionAliases.hasOwnProperty(act)) {
				actionCommand = actionAliases[act];
			} else {
				actionCommand = act;
			}
		}
		return {
			"type": actionCommand,
			"target": "#" + this.id
		};
	},

	onclick: function(x, y) {
		marauroa.clientFramework.sendAction(this.getDefaultAction());
	}
});



marauroa.rpobjectFactory._default = marauroa.rpobjectFactory.entity;