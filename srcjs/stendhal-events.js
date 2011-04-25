marauroa.rpeventFactory.private_text = marauroa.util.fromProto(marauroa.rpeventFactory._default);
marauroa.rpeventFactory.private_text.execute = function(rpobject) {
	stendhal.ui.chatLog.addLine(this.texttype.toLowerCase(), this.text);
}

marauroa.rpeventFactory.text = marauroa.util.fromProto(marauroa.rpeventFactory._default);
marauroa.rpeventFactory.text.execute = function(rpobject) {
	rpobject.say(this.text);
}
