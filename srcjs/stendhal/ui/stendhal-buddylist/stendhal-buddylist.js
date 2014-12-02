"use strict";

Polymer("stendhal-buddylist", {
	"buddies": [],
	
	"setBuddyStatus": function(buddy, status) {
		if(this.hasBuddy(buddy)) {
			this.removeBuddy(buddy);
		}
		var newEntry = {"name": buddy, "status":status};
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
    	var found = false;
    	var indexFound;
    	for (var i = 0; i < arrayLength; i++) {
    		if(this.buddies[i].name === buddy) {
    			indexFound = i;
    			found = true;
    		}
    	}
    	if(found) {
    		this.buddies = this.buddies.splice(indexFound, 1);
    	}
    }
    
});