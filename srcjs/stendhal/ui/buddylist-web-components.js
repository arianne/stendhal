/***************************************************************************
 *                   (C) Copyright 2003-2014 - Stendhal                    *
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
	update: function() {
		var div = document.getElementById("buddyList");
		for (var buddy in marauroa.me.buddies) {
			if (marauroa.me.buddies.hasOwnProperty(buddy)) {
				var status;
				if (marauroa.me.buddies[buddy] == "true") {
					status = "online";
				} else {
					status = "offline";
				}
				div.setBuddyStatus(buddy, status);
			}
		}
	}
}