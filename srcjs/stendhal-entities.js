/**
 * General entity
 */
marauroa.rpobjectFactory.entity = function() {}
marauroa.rpobjectFactory.entity.minimapStyle = "rgb(200,255,200)";
marauroa.rpobjectFactory.entity.prototype = marauroa.rpobjectFactory._default;



/**
 * Item
 */
marauroa.rpobjectFactory.item = function() {
	this.minimapStyle = "rgb(0,255,0)";
};
marauroa.rpobjectFactory.item.prototype = new marauroa.rpobjectFactory.entity;



/**
 * Player
 */
marauroa.rpobjectFactory.player = function() {
	/** minimap style */
	this.minimapStyle = "rgb(255, 255, 255)";

	/** updates a property value */
	this.set = function(key, value) {
		marauroa.rpobjectFactory._default.set.apply(this, arguments);
		if (key == "text") {
			this.say(value);
		}
	}

	/** says a text */
	this.say = function (text) {
		if (text.match("^!me") == "!me") {
			stendhal.ui.chatLog.addLine("emote", text.replace(/^!me/, this.title));
		} else {
			stendhal.ui.chatLog.addLine("normal", this.title + ": " + text);
		}
	}
};
marauroa.rpobjectFactory.player.prototype = new marauroa.rpobjectFactory.entity;



/**
 * Creature
 */
marauroa.rpobjectFactory.creature = function() {
	this.minimapStyle = "rgb(255,255,0)";
};
marauroa.rpobjectFactory.creature.prototype = new marauroa.rpobjectFactory.entity;



/**
 * Portal
 */
marauroa.rpobjectFactory.portal = function() {
	this.minimapStyle = "rgb(0,0,0)";
};
marauroa.rpobjectFactory.portal.prototype = new marauroa.rpobjectFactory.entity;



/**
 * NPC
 */
marauroa.rpobjectFactory.npc = function() {
	this.minimapStyle = "rgb(0,0,255)";
};
marauroa.rpobjectFactory.npc.prototype = new marauroa.rpobjectFactory.entity;




/** factory method */
marauroa.rpobjectFactory.createRPObject = function(rpclass) {
	var ctor = this.entity;
	if (typeof(this[rpclass]) != "undefined") {
		ctor = this[rpclass];
	}
	return new ctor;
}

