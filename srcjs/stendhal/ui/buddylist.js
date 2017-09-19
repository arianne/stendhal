/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

"use strict";

window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};

/**
 * buddylist
 */
stendhal.ui.buddyList = {

	buddies: [],

	// TODO: don't rebuilt the buddylist completely on every turn,
	//       but implement an observer
	update: function() {
		var data = marauroa.me.buddies;
		var buddies = []
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
			buddies.push(entry);
		}
		stendhal.ui.buddyList.sort(buddies);

		var html = "";
		for (var i = 0; i < buddies.length; i++) {
			html += "<li class=" + buddies[i].status + ">" + stendhal.ui.html.esc(buddies[i].name) + "</li>";
		}
		
		if (stendhal.ui.buddyList.lastHtml != html) {
			var buddyListUL = document.getElementById("buddyListUL");
			buddyListUL.innerHTML = html;
			stendhal.ui.buddyList.lastHtml = html;
		}
		stendhal.ui.buddyList.buddies = buddies;
	},

	/**
	 * sorts the buddy list
	 */
	sort: function(buddies) {
		buddies.sort(function compare(a, b) {
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
		stendhal.ui.buddyList.removeBuddy(buddy);
		var newEntry = {"name": buddy, "status": status};
		stendhal.ui.buddyList.buddies.push(newEntry);
		stendhal.ui.buddyList.sort();
	},

    hasBuddy: function(buddy) {
    	var arrayLength = stendhal.ui.buddyList.buddies.length;
    	for (var i = 0; i < arrayLength; i++) {
    	    if(stendhal.ui.buddyList.buddies[i].name === buddy) {
    	    	return true;
    	    }
    	}
    	return false;
    },

    removeBuddy: function(buddy) {
    	var arrayLength = stendhal.ui.buddyList.buddies.length;
    	for (var i = 0; i < arrayLength; i++) {
    		if (stendhal.ui.buddyList.buddies[i].name === buddy) {
    			stendhal.ui.buddyList.buddies.splice(i, 1);
    			return;
    		}
    	}
    }
}