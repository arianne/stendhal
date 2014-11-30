"use strict";

Polymer("stendhal-chatlog", {
	logEntries: [],
	
	ready: function() {
		// TODO: cleanup compatibility code
		stendhal.ui.chatLog.addLine = function(type, message) {
			document.getElementById("chat").addLine(type, message);
		}
		stendhal.ui.chatLog.clear = function() {
			document.getElementById("chat").clear();
		}
	},

	/**
	 * adds an entry to the chat log
	 *
	 * @param type of entry
	 * @param message message
	 */
	addLine: function(type, message) {
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
		this.logEntries.push(newEntry);
	},

	/**
	 * clears the chat log
	 */
	clear: function() {
		this.logEntries = [];
	}
});