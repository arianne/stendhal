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
marauroa.rpobjectFactory.entity = marauroa.util.fromProto(marauroa.rpobjectFactory._default);
marauroa.rpobjectFactory.entity.minimapShow = false;
marauroa.rpobjectFactory.entity.minimapStyle = "rgb(200,255,200)";
marauroa.rpobjectFactory.entity.set = function(key, value) {
	marauroa.rpobjectFactory.entity.proto.set.apply(this, arguments);
	if (key == 'name') {
		if (typeof(this['title']) == "undefined") {
			this['title'] = value;
		}
	} else {
		this[key] = value;
	}
}
// Ensure that the drawing code can rely on _x and _y
marauroa.rpobjectFactory.entity.updatePosition = function(time) {
	if (this._y == undefined) {
		this._y = parseFloat(this.y);
	}
	if (this._x == undefined) {
		this._x = parseFloat(this.x);
	}
}



/**
 * Item
 */
marauroa.rpobjectFactory.item = marauroa.util.fromProto(marauroa.rpobjectFactory.entity);
marauroa.rpobjectFactory.item.minimapStyle = "rgb(0,255,0)";



/**
 * Portal
 */
marauroa.rpobjectFactory.portal = marauroa.util.fromProto(marauroa.rpobjectFactory.entity);
marauroa.rpobjectFactory.portal.minimapShow = true;
marauroa.rpobjectFactory.portal.minimapStyle = "rgb(0,0,0)";


/**
 * ActiveEntity
 */
marauroa.rpobjectFactory.activeEntity = marauroa.util.fromProto(marauroa.rpobjectFactory.entity);
marauroa.rpobjectFactory.activeEntity.updatePosition = function(time) {
	var serverX = parseFloat(this.x);
	var serverY = parseFloat(this.y);
	if (this._x == undefined) {
		this._x = serverX;
	}
	if (this._y == undefined) {
		this._y = serverY;
	}

	if (this.speed > 0) {
		var oldX = this._x;
		var oldY = this._y;
		var movement = this.speed * time / 300;
		switch (this.dir) {
		case "1":
			this._y = this._y - movement;
			this._x = serverX;
			break;
		case "2":
			this._x = this._x + movement;
			this._y = serverY;
			break;
		case "3": 
			this._y = this._y + movement;
			this._x = serverX;
			break;
		case "4":
			this._x = this._x - movement;
			this._y = serverY;
		}
		if (this.collidesMap()) {
			this._x = oldX;
			this._y = oldY;
		}
	} else {
		// Restore server coordinates when the entity is not moving
		this._x = serverX;
		this._y = serverY;
	}
}
/** Check if the entity collides with the collision map. */
marauroa.rpobjectFactory.activeEntity.collidesMap = function() {
	var startX = Math.floor(this._x);
	var startY = Math.floor(this._y);
	var endX = Math.ceil(this._x + parseFloat(this.width));
	var endY = Math.ceil(this._y + parseFloat(this.height));
	for (var y = startY; y < endY; y++) {
		for (var x = startX; x < endX; x++) {
			if (stendhal.data.map.collision(x, y)) {
				return true;
			}
		}
	}
	return false;
}

/**
 * RPEntity
 */
marauroa.rpobjectFactory.rpentity = marauroa.util.fromProto(marauroa.rpobjectFactory.activeEntity);
marauroa.rpobjectFactory.rpentity.drawY = 0;
marauroa.rpobjectFactory.rpentity.spritePath = "";
marauroa.rpobjectFactory.rpentity.titleStyle = "#FFFFFF";
marauroa.rpobjectFactory.rpentity.set = function(key, value) {
	marauroa.rpobjectFactory.rpentity.proto.set.apply(this, arguments);
	if (key == "text") {
		this.say(value);
	}
}

/** says a text */
marauroa.rpobjectFactory.rpentity.say = function (text) {
	if (marauroa.me.isInHearingRange(this)) {
		if (text.match("^!me") == "!me") {
			stendhal.ui.chatLog.addLine("emote", text.replace(/^!me/, this.title));
		} else {
			stendhal.ui.chatLog.addLine("normal", this.title + ": " + text);
		}
	}
}

/** draw RPEntities */
marauroa.rpobjectFactory.rpentity.draw = function(ctx) {
	var filename;
	if (typeof(this.outfit) != "undefined") {
		filename = "/data/sprites/outfit/player_base_" + (this.outfit % 100) + ".png";
		this.drawSprite(ctx, filename)
		filename = "/data/sprites/outfit/dress_" + (Math.floor(this.outfit/100) % 100) + ".png";
		this.drawSprite(ctx, filename)
		filename = "/data/sprites/outfit/head_" + (Math.floor(this.outfit/10000) % 100) + ".png";
		this.drawSprite(ctx, filename)
		filename = "/data/sprites/outfit/hair_" + (Math.floor(this.outfit/1000000) % 100) + ".png";
		this.drawSprite(ctx, filename)
	} else {
		filename = "/data/sprites/" + this.spritePath + "/" + this["class"];
		if (typeof(this.subclass) != "undefined") {
			filename = filename + "/" + this["subclass"];
		}
		filename = filename + ".png";
		this.drawSprite(ctx, filename)
	}
}


marauroa.rpobjectFactory.rpentity.drawSprite = function(ctx, filename) {
	var localX = this._x * 32;
	var localY = this._y * 32;
	var image = stendhal.data.sprites.get(filename);
	if (image.complete) {
		// TODO: animate
		var drawHeight = image.height / 4;
		var drawWidth = image.width / 3;
		var drawX = ((this.width * 32) - drawWidth) / 2;
		var drawY = (this.height * 32) - drawHeight;
		ctx.drawImage(image, 0, (this.dir - 1) * drawHeight, drawWidth, drawHeight, localX + drawX, localY + drawY, drawWidth, drawHeight);
	}
}


marauroa.rpobjectFactory.rpentity.drawTop = function(ctx) {
	var localX = this._x * 32;
	var localY = this._y * 32;
	if (typeof(this.title) != "undefined") {
		var textMetrics = ctx.measureText(this.title);
		ctx.font = "14px Arial";
		ctx.fillStyle = "#A0A0A0";
		ctx.fillText(this.title, localX + (this.width * 32 - textMetrics.width) / 2+2, localY - 32);
		ctx.fillStyle = this.titleStyle;
		ctx.fillText(this.title, localX + (this.width * 32 - textMetrics.width) / 2, localY - 32);
	}
}

/**
 * Player
 */
marauroa.rpobjectFactory.player = marauroa.util.fromProto(marauroa.rpobjectFactory.rpentity);
marauroa.rpobjectFactory.player.minimapShow = true;
marauroa.rpobjectFactory.player.minimapStyle = "rgb(255, 255, 255)";
marauroa.rpobjectFactory.player.dir = 3;



/** Is this player an admin? */
marauroa.rpobjectFactory.player.isAdmin = function() {
	return (typeof(this.adminlevel) != "undefined" && this.adminlevel > 600);
}

/** Can the player hear this chat message? */
marauroa.rpobjectFactory.player.isInHearingRange = function(entity) {
	return (this.isAdmin() || ((Math.abs(this.x - entity.x) < 15) && (Math.abs(this.y - entity.y) < 15)));
}



/**
 * Creature
 */
marauroa.rpobjectFactory.creature = marauroa.util.fromProto(marauroa.rpobjectFactory.rpentity);
marauroa.rpobjectFactory.creature.minimapStyle = "rgb(255,255,0)";
marauroa.rpobjectFactory.creature.spritePath = "monsters";
marauroa.rpobjectFactory.creature.titleStyle = "#A00000";

/**
 * NPC
 */
marauroa.rpobjectFactory.npc = marauroa.util.fromProto(marauroa.rpobjectFactory.rpentity);
marauroa.rpobjectFactory.npc.minimapStyle = "rgb(0,0,255)";
marauroa.rpobjectFactory.npc.spritePath = "npc";
marauroa.rpobjectFactory.npc.titleStyle = "#0000A0";



marauroa.rpobjectFactory._default = marauroa.rpobjectFactory.entity;