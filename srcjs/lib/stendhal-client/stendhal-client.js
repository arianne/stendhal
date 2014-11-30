Polymer("stendhal-client", {
	ready: function() {
		console.log("Starting up client...");
		stendhal.main.startup;
		console.log("Finished starting up client...");
	}
});