"use strict";

Polymer("stendhal-buddylist", {
	"buddies": [],
	
	"setBuddyStatus": function(buddy, status) {
		this.removeBuddy(buddy);
		var newEntry = {"name": buddy, "status": status};
		this.buddies.push(newEntry);
	},

    "clear": function() {
    	this.buddies = [];
    },
    
    "hasBuddy": function(buddy) {
    	var arrayLength = this.buddies.length;
    	for (var i = 0; i < arrayLength; i++) {
    	    if(this.buddies[i].name === buddy) {
    	    	return true;
    	    }
    	}
    },
    
    "removeBuddy": function(buddy) {
    	var arrayLength = this.buddies.length;
    	for (var i = 0; i < arrayLength; i++) {
    		if (this.buddies[i].name === buddy) {
    			this.buddies.splice(i, 1);
    			return;
    		}
    	}
    }
    
});