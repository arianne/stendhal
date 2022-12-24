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
	active: [],

	/**
	 * Plays a sound with volume relative to distance.
	 *
	 * @param x
	 *     X coordinate of sound source.
	 * @param y
	 *     Y coordinate of sound source.
	 * @param radius
	 *     Radius at which sound can be heard.
	 * @param soundName
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 */
	playLocalizedEffect: function(x, y, radius, layer, soundName, volume) {
		if (!stendhal.config.getBoolean("ui.sound")) {
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
		sound.src = stendhal.paths.sounds + "/" + soundName + ".ogg";

		stendhal.ui.sound.onSoundAdd(sound);
	},

	/**
	 * Plays a sound with uniform volume.
	 *
	 * @param soundName
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 */
	playGlobalizedEffect: function(soundName, volume) {
		if (!stendhal.config.getBoolean("ui.sound")) {
			return;
		}

		const sound = new Audio(stendhal.paths.sounds + "/" + soundName + ".ogg");
		if (volume != null) {
			sound.volume = volume;
		}
		sound.autoplay = true;

		stendhal.ui.sound.onSoundAdd(sound);
	},

	/**
	 * Stops all currently playing sounds.
	 *
	 * @return
	 *     <code>true</code> if all sounds were aborted.
	 */
	stopAll: function() {
		for (let idx = stendhal.ui.sound.active.length; idx >= 0 ; idx--) {
			const snd = stendhal.ui.sound.active[idx];
			// sound may have ended during this call
			if (snd) {
				snd.pause();
			}
		}

		stendhal.ui.sound.active.splice(0, stendhal.ui.sound.active.length);
		return stendhal.ui.sound.active.length == 0;
	},

	/**
	 * Sets event handlers for when sound finishes.
	 *
	 * @param sound
	 *     The playing sound.
	 */
	onSoundAdd: function(sound) {
		sound.onended = (e) => {
			// remove from active sounds
			const idx = stendhal.ui.sound.active.indexOf(sound);
			if (idx > -1) {
				stendhal.ui.sound.active.splice(idx, 1);
			}
		};
		stendhal.ui.sound.active.push(sound);
	},
};
