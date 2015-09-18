"use strict";

Polymer({
	is: "stendhal-chatlog",

	properties: {
		logEntries: {
			type: Array,
			value: []
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

		var newEntry = {"time": time, "message": message, "type": "log" + type};
		this.push('logEntries', newEntry);
		
		var isAtBottom = (this.scrollHeight - this.clientHeight) == this.scrollTop;
		this.async(function () {
			if (isAtBottom) {
				this.scrollTop = this.scrollHeight;
			}
		});
	},

	/**
	 * clears the chat log
	 */
	clear: function() {
		this.logEntries = [];
	}
});