"use strict";

Polymer("stendhal-buddylist", {
	"buddies": [],
	"addBuddy": function(buddy) {
		this.buddies.push(buddy);
	},
    "clear": function() {
    	this.buddies = [];
    },
    "hasBuddy": function(buddy) {
    	//TODO: iterate
    }
});