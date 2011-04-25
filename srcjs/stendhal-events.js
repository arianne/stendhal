marauroa.rpeventFactory.private_text = marauroa.util.fromProto(marauroa.rpeventFactory._default);
marauroa.rpeventFactory.private_text.execute = function(rpobject) {
	stendhal.ui.chatLog.addLine(this.texttype.toLowerCase(), this.text);
}

marauroa.rpeventFactory.text = marauroa.util.fromProto(marauroa.rpeventFactory._default);
marauroa.rpeventFactory.text.execute = function(rpobject) {
	rpobject.say(this.text);
}

marauroa.rpeventFactory.sound_event = marauroa.util.fromProto(marauroa.rpeventFactory._default);
marauroa.rpeventFactory.sound_event.execute = function(rpobject) {
	var sound = new Audio();
	sound.autoplay = true;
	sound.src = "/data/sounds/" + this.sound + ".ogg";
}
