"use strict";

Polymer("stendhal-buddylist", {
	buddies: [],

	update: function(data) {
		this.clear();
		for (var buddy in data) {
			if (data.hasOwnProperty(buddy)) {
				var entry = {"name": buddy};
				if (data[buddy] == "true") {
					entry.isOnline = true;
					entry.status = "online";
				} else {
					entry.isOnline = false;
					entry.status = "offline";
				}
			}
			this.buddies.push(entry);
		}
		this.sort();
	},
	
	sort: function() {
		this.buddies.sort(function compare(a, b) {
			if (a.isOnline) {
				if (!b.isOnline) {
					return -1;
				}
			} else {
				if (b.isOnline) {
					return 1;
				}
			}

			if (a.name < b.name) {
				return -1;
			}
			if (a.name > b.name) {
				return 1;
			}
			return 0;
		});
	},

	setBuddyStatus: function(buddy, status) {
		this.removeBuddy(buddy);
		var newEntry = {"name": buddy, "status": status};
		this.buddies.push(newEntry);
	},

    clear: function() {
    	this.buddies = [];
    },

    hasBuddy: function(buddy) {
    	var arrayLength = this.buddies.length;
    	for (var i = 0; i < arrayLength; i++) {
    	    if(this.buddies[i].name === buddy) {
    	    	return true;
    	    }
    	}
    },

    removeBuddy: function(buddy) {
    	var arrayLength = this.buddies.length;
    	for (var i = 0; i < arrayLength; i++) {
    		if (this.buddies[i].name === buddy) {
    			this.buddies.splice(i, 1);
    			return;
    		}
    	}
    }
    
});