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
 * windows for items: character, bag, keyring
 */
stendhal.ui.equip = {
	slots: ["head", "lhand", "rhand", "finger", "armor", "cloak", "legs", "feet"],

	update: function() {
		for (var i in this.slots) {
			var s = marauroa.me[this.slots[i]];
			if (typeof(s) != "undefined") {
				var o = s.first();
				if (typeof(o) != "undefined") {
					document.getElementById(this.slots[i]).style.backgroundImage = "url(" + stendhal.server + "/data/sprites/items/" + o['class'] + "/" + o.subclass + ".png" + ")";
				} else {
					document.getElementById(this.slots[i]).style.backgroundImage = "none";
				}
			} else {
				document.getElementById(this.slots[i]).style.backgroundImage = "none";
			}
		}
	}
}

stendhal.ui.bag = {
	update: function() {
		stendhal.ui.itemContainerWindow.render("bag", 12);
	}
}

stendhal.ui.keyring = {
	update: function() {
		stendhal.ui.itemContainerWindow.render("keyring", 8);
	}
}

stendhal.ui.itemContainerWindow = {
	render: function(name, size) {
		var cnt = 0;
		for (var i in marauroa.me[name]) {
			if (!isNaN(i)) {
				var o = marauroa.me[name][i];
				document.getElementById(name + cnt).style.backgroundImage = "url(" + stendhal.server + "/data/sprites/items/" + o['class'] + "/" + o.subclass + ".png " + ")";
				cnt++;
			}
		}
		for (var i = cnt; i < size; i++) {
			document.getElementById(name + i).style.backgroundImage = "none";
		}
	}
}
