marauroa.rpobjectFactory.entity = function() {}
marauroa.rpobjectFactory.entity.minimapStyle = "rgb(200,255,200)";
marauroa.rpobjectFactory.entity.prototype = marauroa.rpobjectFactory._default;

marauroa.rpobjectFactory.player = function() {
	this.minimapStyle = "rgb(255,255,255)";
};
marauroa.rpobjectFactory.player.prototype = marauroa.rpobjectFactory._default;

marauroa.rpobjectFactory.creature = function() {
	this.minimapStyle = "rgb(255,0,0)";
};
marauroa.rpobjectFactory.creature.prototype = marauroa.rpobjectFactory._default;

marauroa.rpobjectFactory.portal = function() {
	this.minimapStyle = "rgb(0,0,0)";
};
marauroa.rpobjectFactory.portal.prototype = marauroa.rpobjectFactory._default;

marauroa.rpobjectFactory.npc = function() {
	this.minimapStyle = "rgb(0,0,255)";
};
marauroa.rpobjectFactory.npc.prototype = marauroa.rpobjectFactory._default;
