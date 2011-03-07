/**
 * General entity
 */
marauroa.rpobjectFactory.entity = marauroa.util.fromProto(marauroa.rpobjectFactory._default);
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
 * Player
 */
marauroa.rpobjectFactory.player = marauroa.util.fromProto(marauroa.rpobjectFactory.entity);
marauroa.rpobjectFactory.player.minimapStyle = "rgb(255, 255, 255)";

/** updates a property value */
marauroa.rpobjectFactory.player.set = function(key, value) {
	marauroa.rpobjectFactory.player.proto.set.apply(this, arguments);
	if (key == "text") {
		this.say(value);
	}
}

/** says a text */
marauroa.rpobjectFactory.player.say = function (text) {
	if (text.match("^!me") == "!me") {
		stendhal.ui.chatLog.addLine("emote", text.replace(/^!me/, this.title));
	} else {
		stendhal.ui.chatLog.addLine("normal", this.title + ": " + text);
	}
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
marauroa.rpobjectFactory.portal.minimapStyle = "rgb(0,0,0)";



/**
 * NPC
 */
marauroa.rpobjectFactory.npc = marauroa.util.fromProto(marauroa.rpobjectFactory.entity);
marauroa.rpobjectFactory.npc.minimapStyle = "rgb(0,0,255)";




marauroa.rpobjectFactory._default = marauroa.rpobjectFactory.entity;