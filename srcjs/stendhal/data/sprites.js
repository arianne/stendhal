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

window.stendhal = window.stendhal || {};
stendhal.data = stendhal.data || {};

stendhal.data.sprites = {
	get: function(filename) {
		if (typeof(this[filename]) != "undefined") {
			this[filename].counter++;
			return this[filename];
		}
		var temp = new Image;
		temp.counter = 0;
		temp.src = filename;
		this[filename] = temp;
		return temp;
	},

	/** deletes all objects that have not been accessed since this method was called last time */
	// TODO: call clean on map change
	clean: function() {
		for (var i in this) {
			marauroa.log.debug(typeof(i));
			if (typeof(i) == "Image") {
				if (this[i].counter > 0) {
					this[i].counter = 0;
				} else {
					delete(this[i]);
				}
			}
		}
	}
}
