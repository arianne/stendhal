"use strict";

Polymer("stendhal-chatlog", {
	logEntries: [],

	addEntry: function(type, message) {
		var date = new Date();
		var time = "" + date.getHours() + ":";
		if (date.getHours < 10) {
			time = "0" + time;
		}
		if (date.getMinutes() < 10) {
			time = time + "0";
		};
		time = time + date.getMinutes();

		var newEntry = {"time": time, "message": message, "type": type};
		logEntries.push(newEntry);
	}
});