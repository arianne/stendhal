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
	cache: {},
	active: [],
	activeLoops: [],

	/**
	 * Initializes an audio object & loads into the cache.
	 *
	 * @param id
	 *     Identifier string used to retrieve from cache.
	 * @param filename
	 *     Path to sound file.
	 * @return
	 *     New audio object.
	 */
	load: function(id, filename) {
		const snd = new Audio(filename);
		snd.autoplay = false;
		// load into cache
		stendhal.ui.sound.cache[id] = snd;
		return snd;
	},

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

		volume = typeof(volume) !== "undefined" ? volume : 1.0;

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

		// check cache first
		let sound = stendhal.ui.sound.cache[soundName];
		if (sound) {
			// TODO: handle HTMLAudioElement.error
			if (!sound.paused && !sound.ended) {
				if (sound.loop) {
					// don't play loops over each other
					return sound;
				}
				// create a new sound instead of interrupting the cached one
				const addSound = new Audio(sound.src);
				addSound.autoplay = true;
				addSound.volume = sound.volume;
				stendhal.ui.sound.onSoundAdd(addSound);
				return addSound;
			}
		} else {
			// add new sound to cache
			sound = stendhal.ui.sound.load(soundName,
					stendhal.paths.sounds + "/" + soundName + ".ogg");
		}

		sound.autoplay = true;
		sound.volume = volume;
		sound.loop = loop;

		// must be started manually if autoplay has already ocurred
		if (sound.hasplayed) {
			if (!sound.paused && !sound.ended) {
				sound.pause();
			}
			sound.currentTime = 0;
			sound.play();
		}

		sound.hasplayed = true;
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
		// load into cache so playLocalizedEffect doesn't look in "data/sounds"
		stendhal.ui.sound.load(musicName,
				stendhal.paths.music + "/" + musicName + ".ogg");
		return stendhal.ui.sound.playLocalizedLoop(x, y, radius,
				layer, musicName, volume);
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

		volume = typeof(volume) !== "undefined" ? volume : 1.0;
		if (volume > 1) {
			volume = 1.0;
		} else if (volume < 0) {
			volume = 0.0;
		}

		// check cache first
		let sound = stendhal.ui.sound.cache[soundName];
		if (sound) {
			// TODO: handle HTMLAudioElement.error
			if (!sound.paused && !sound.ended) {
				if (sound.loop) {
					// don't play loops over each other
					return sound;
				}
				// create a temporary sound instead of interrupting the cached one
				const addSound = new Audio(sound.src);
				addSound.autoplay = true;
				addSound.volume = sound.volume;
				stendhal.ui.sound.onSoundAdd(addSound);
				return addSound;
			}
		} else {
			// add new sound to cache
			sound = stendhal.ui.sound.load(soundName,
					stendhal.paths.sounds + "/" + soundName + ".ogg");
		}

		sound.autoplay = true;
		sound.volume = volume;
		sound.loop = loop;

		// must be started manually if autoplay has already ocurred
		if (sound.hasplayed) {
			if (!sound.paused && !sound.ended) {
				sound.pause();
			}
			sound.currentTime = 0;
			sound.play();
		}

		sound.hasplayed = true;
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
		// load into cache so playGlobalizedEffect doesn't look in "data/sounds"
		stendhal.ui.sound.load(musicName,
				stendhal.paths.music + "/" + musicName + ".ogg");
		return stendhal.ui.sound.playGlobalizedLoop(musicName, volume);
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

	/**
	 * Called at startup to pre-cache certain sounds.
	 */
	startupCache: function() {
		// login sound
		stendhal.ui.sound.load("ui/login",
				stendhal.paths.sounds + "/ui/login.ogg");
	}
};
