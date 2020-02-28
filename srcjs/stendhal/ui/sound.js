/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

"use strict";

var stendhal = window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};

stendhal.ui.sound = {
	layers: ["music", "ambient", "creature", "sfx", "gui"],

	playLocalizedEffect: function(x, y, radius, layer, soundName, volume) {
		if (!stendhal.config.sound.play) {
			return;
		}

		// Further adjustments if the sound has a radius
		if (radius) {
			if (!marauroa.me || !x) {
				// Can't calculate the distance yet. Ignore the sound.
				return;
			}

			var xdist = marauroa.me["_x"] - x;
			var ydist = marauroa.me["_y"] - y;
			var dist2 = xdist * xdist + ydist * ydist;
			if (dist2 > radius * radius) {
				// Outside the specified radius
				return;
			}
			// The sound api does not guarantee anything about how the volume
			// works, so it does not matter much how we scale it.
			volume *= Math.min(radius * radius / (dist2 * 20), 1);
		}

		var sound = new Audio();
		sound.autoplay = true;
		sound.volume = volume;
		sound.src = "/data/sounds/" + soundName + ".ogg";
	},

	playGlobalizedEffect: function(soundName, volume) {
		if (!stendhal.config.sound.play) {
			return;
		}

		const sound = new Audio("/data/sounds/" + soundName + ".ogg");
		if (volume != null) {
			sound.volume = volume;
		}
		sound.autoplay = true;
	}
};
