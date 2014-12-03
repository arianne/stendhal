"use strict";

Polymer("stendhal-buddylist", {
	buddies: [],

	// TODO: don't rebuilt the buddylist completely on every turn,
	//       but implement an observer
	update: function(data) {
		this.buddies = [];
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

	/**
	 * sorts the buddy list
	 */
	sort: function() {
		this.buddies.sort(function compare(a, b) {
			// online buddies first
			if (a.isOnline) {
				if (!b.isOnline) {
					return -1;
				}
			} else {
				if (b.isOnline) {
					return 1;
				}
			}

			// sort by name
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
		this.sort();
	},

    hasBuddy: function(buddy) {
    	var arrayLength = this.buddies.length;
    	for (var i = 0; i < arrayLength; i++) {
    	    if(this.buddies[i].name === buddy) {
    	    	return true;
    	    }
    	}
    	return false;
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