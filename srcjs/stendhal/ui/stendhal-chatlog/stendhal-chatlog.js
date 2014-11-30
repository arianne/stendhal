Polymer("stendhal-chatlog", {
	logEntries: [],
	ready: function() {
	},
	addEntry: function(type, time, message) {
		newEntry = {"time": time, "message": message, "type": type};
		logEntries.push(newEntry);
	},
});