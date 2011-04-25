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
	if (key == "text") {
		this.say(value);
	}
}


/** says a text */
marauroa.rpobjectFactory.entity.say = function (text) {
	if (marauroa.me.isInHearingRange(this)) {
		if (text.match("^!me") == "!me") {
			stendhal.ui.chatLog.addLine("emote", text.replace(/^!me/, this.title));
		} else {
			stendhal.ui.chatLog.addLine("normal", this.title + ": " + text);
		}
	}
}

/**
 * Item
 */
marauroa.rpobjectFactory.item = marauroa.util.fromProto(marauroa.rpobjectFactory.entity);
marauroa.rpobjectFactory.item.minimapStyle = "rgb(0,255,0)";


/**
 * Player
 */
marauroa.rpobjectFactory.player = marauroa.util.fromProto(marauroa.rpobjectFactory.entity);
marauroa.rpobjectFactory.player.minimapShow = true;
marauroa.rpobjectFactory.player.minimapStyle = "rgb(255, 255, 255)";


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
marauroa.rpobjectFactory.creature = marauroa.util.fromProto(marauroa.rpobjectFactory.entity);
marauroa.rpobjectFactory.creature.minimapStyle = "rgb(255,255,0)";



/**
 * Portal
 */
marauroa.rpobjectFactory.portal = marauroa.util.fromProto(marauroa.rpobjectFactory.entity);
marauroa.rpobjectFactory.portal.minimapShow = true;
marauroa.rpobjectFactory.portal.minimapStyle = "rgb(0,0,0)";



/**
 * NPC
 */
marauroa.rpobjectFactory.npc = marauroa.util.fromProto(marauroa.rpobjectFactory.entity);
marauroa.rpobjectFactory.npc.minimapStyle = "rgb(0,0,255)";




marauroa.rpobjectFactory._default = marauroa.rpobjectFactory.entity;