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
 * RPEntity
 */
marauroa.rpobjectFactory.rpentity = marauroa.util.fromProto(marauroa.rpobjectFactory.entity);
marauroa.rpobjectFactory.rpentity.drawY = 0;
marauroa.rpobjectFactory.rpentity.spritePath = "";
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
marauroa.rpobjectFactory.rpentity.draw = function(ctx, offsetX, offsetY) {
	var localX = this.x * 32 - offsetX;
	var localY = this.y * 32 - offsetY;

	var filename;
	if (typeof(this.outfit) != "undefined") {
		// TODO: draw complete outfit into an outfit cache
		filename = "/data/sprites/outfit/player_base_" + (this.outfit % 100) + ".png";
	} else {
		filename = "/data/sprites/" + this.spritePath + "/" + this["class"];
		if (typeof(this.subclass) != "undefined") {
			filename = filename + "/" + this["subclass"];
		}
		filename = filename + ".png";
	}
	var image = stendhal.data.sprites.get(filename);
	if (image.complete) {
		// TODO: animate
		// TODO: smooth walking on sub tiles
		var drawHeight = image.height / 4;
		var drawWidth = image.width / 3;
		var drawX = ((this.width * 32) - drawWidth) / 2;
		var drawY = (this.height * 32) - drawHeight;
		ctx.drawImage(image, 0, (this.dir - 1) * drawHeight, drawWidth, drawHeight, localX + drawX, localY + drawY, drawWidth, drawHeight);
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
marauroa.rpobjectFactory.rpentity.spritePath = "monsters";

/**
 * NPC
 */
marauroa.rpobjectFactory.npc = marauroa.util.fromProto(marauroa.rpobjectFactory.rpentity);
marauroa.rpobjectFactory.npc.minimapStyle = "rgb(0,0,255)";
marauroa.rpobjectFactory.npc.spritePath = "npc";




marauroa.rpobjectFactory._default = marauroa.rpobjectFactory.entity;