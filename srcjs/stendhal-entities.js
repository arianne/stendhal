marauroa.rpobjectFactory.entity = function() {}
marauroa.rpobjectFactory.entity.minimapStyle = "rgb(200,255,200)";
marauroa.rpobjectFactory.entity.prototype = marauroa.rpobjectFactory._default;

marauroa.rpobjectFactory.item = function() {
	this.minimapStyle = "rgb(0,255,0)";
};
marauroa.rpobjectFactory.item.prototype = new marauroa.rpobjectFactory.entity;

marauroa.rpobjectFactory.player = function() {
	this.minimapStyle = "rgb(255,255,255)";
};
marauroa.rpobjectFactory.player.prototype = new marauroa.rpobjectFactory.entity;

marauroa.rpobjectFactory.creature = function() {
	this.minimapStyle = "rgb(255,255,0)";
};
marauroa.rpobjectFactory.creature.prototype = new marauroa.rpobjectFactory.entity;

marauroa.rpobjectFactory.portal = function() {
	this.minimapStyle = "rgb(0,0,0)";
};
marauroa.rpobjectFactory.portal.prototype = new marauroa.rpobjectFactory.entity;

marauroa.rpobjectFactory.npc = function() {
	this.minimapStyle = "rgb(0,0,255)";
};
marauroa.rpobjectFactory.npc.prototype = new marauroa.rpobjectFactory.entity;



marauroa.rpobjectFactory.createRPObject = function(rpclass) {
	var ctor = this.entity;
	if (typeof(this[rpclass]) != "undefined") {
		ctor = this[rpclass];
	}
	return new ctor;
}

