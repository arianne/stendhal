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
	activeLoops: [],

	/**
	 * Plays a sound with volume relative to distance.
	 *
	 * @param x
	 *     X coordinate of sound source.
	 * @param y
	 *     Y coordinate of sound source.
	 * @param radius
	 *     Radius at which sound can be heard.
	 * @param layer
	 *     Channel on which to be played (currently not supported).
	 * @param soundName
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @param loop
	 *     Whether or not sound should be looped.
	 * @return
	 *     HTMLAudioElement.
	 */
	playLocalizedEffect: function(x, y, radius, layer, soundName, volume,
			loop=false) {
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

		if (volume > 1) {
			volume = 1.0;
		} else if (volume < 0) {
			volume = 0.0;
		}

		var sound = new Audio();
		sound.autoplay = true;
		sound.volume = volume;
		sound.src = stendhal.paths.sounds + "/" + soundName + ".ogg";
		sound.loop = loop;

		stendhal.ui.sound.onSoundAdd(sound);
		return sound;
	},

	/**
	 * Loops a sound with volume relative to distance.
	 *
	 * @param x
	 *     X coordinate of sound source.
	 * @param y
	 *     Y coordinate of sound source.
	 * @param radius
	 *     Radius at which sound can be heard.
	 * @param layer
	 *     Channel on which to be played (currently not supported).
	 * @param soundName
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @return
	 *     HTMLAudioElement.
	 */
	playLocalizedLoop: function(x, y, radius, layer, soundName, volume) {
		return stendhal.ui.sound.playLocalizedEffect(x, y, radius, layer,
				soundName, volume, true);
	},

	/**
	 * Loops a sound with volume relative to distance.
	 *
	 * @param x
	 *     X coordinate of sound source.
	 * @param y
	 *     Y coordinate of sound source.
	 * @param radius
	 *     Radius at which sound can be heard.
	 * @param layer
	 *     Channel on which to be played (currently not supported).
	 * @param musicName
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @return
	 *     HTMLAudioElement.
	 */
	playLocalizedMusic: function(x, y, radius, layer, musicName, volume) {
		const music = stendhal.ui.sound.playLocalizedLoop(x, y, radius,
				layer, musicName, volume);
		if (music) {
			music.src = stendhal.paths.music + "/" + musicName + ".ogg";
		}
		return music;
	},

	/**
	 * Plays a sound with uniform volume.
	 *
	 * @param soundName
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @param loop
	 *     Whether or not sound should be looped.
	 * @return
	 *     HTMLAudioElement.
	 */
	playGlobalizedEffect: function(soundName, volume, loop=false) {
		if (!stendhal.config.getBoolean("ui.sound")) {
			return;
		}

		const sound = new Audio(stendhal.paths.sounds + "/" + soundName + ".ogg");
		if (volume != null) {
			if (volume > 1) {
				volume = 1.0;
			} else if (volume < 0) {
				volume = 0.0;
			}
			sound.volume = volume;
		}
		sound.autoplay = true;
		sound.loop = loop;

		stendhal.ui.sound.onSoundAdd(sound);
		return sound;
	},

	/**
	 * Loops a sound with uniform volume.
	 *
	 * @param soundName
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @return
	 *     HTMLAudioElement.
	 */
	playGlobalizedLoop: function(soundName, volume) {
		return stendha.ui.sound.playGlobalizedEffect(soundName, volume,
				true);
	},

	/**
	 * Loops a sound with uniform volume.
	 *
	 * @param musicName
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @return
	 *     HTMLAudioElement.
	 */
	playGlobalizedMusic: function(musicName, volume) {
		const music = stendhal.ui.sound.playGlobalizedLoop(musicName, volume);
		if (music) {
			music.src = stendhal.paths.music + "/" + musicName + ".ogg";
		}
		return music;
	},

	/**
	 * Stops all currently playing sounds.
	 *
	 * @param clean
	 *     If <code>true</code>, removes all loops instead of just
	 *     pausing.
	 * @return
	 *     <code>true</code> if all sounds were aborted.
	 */
	stopAll: function(clean) {
		for (let idx = stendhal.ui.sound.active.length; idx >= 0; idx--) {
			const snd = stendhal.ui.sound.active[idx];
			// sound may have ended during this call
			if (snd) {
				snd.pause();
			}
		}
		let stoppedLoops = true;
		for (let idx = stendhal.ui.sound.activeLoops.length; idx >=0; idx--) {
			const snd = stendhal.ui.sound.activeLoops[idx];
			if (snd) {
				snd.pause();
			}
			stoppedLoops = stoppedLoops && (!snd || snd.paused);
		}

		if (clean === true) {
			stendhal.ui.sound.activeLoops.splice(0,
					stendhal.ui.sound.activeLoops.length);
			stoppedLoops = stendhal.ui.sound.activeLoops.length == 0;
		}

		stendhal.ui.sound.active.splice(0, stendhal.ui.sound.active.length);
		return stendhal.ui.sound.active.length == 0 && stoppedLoops;
	},

	/**
	 * Sets event handlers for when sound finishes.
	 *
	 * @param sound
	 *     The playing sound.
	 */
	onSoundAdd: function(sound) {
		sound.onended = (e) => {
			if (!sound.loop) {
				// remove from active sounds
				const idx = stendhal.ui.sound.active.indexOf(sound);
				if (idx > -1) {
					stendhal.ui.sound.active.splice(idx, 1);
				}
			}
		};

		if (sound.loop) {
			// FIXME: should be removed from this list when destroyed or
			//        preserved if sound continues on map change
			stendhal.ui.sound.activeLoops.push(sound);
		} else {
			stendhal.ui.sound.active.push(sound);
		}
	},
};
