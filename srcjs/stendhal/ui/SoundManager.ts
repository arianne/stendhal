/***************************************************************************
 *                    Copyright © 2003-2022 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

declare var marauroa: any;
declare var stendhal: any;


interface Sound extends HTMLAudioElement {
	hasplayed: boolean;
}

export class SoundManager {

	private static instance: SoundManager;

	private layers: string[] = ["music", "ambient", "creature", "sfx", "gui"];
	private cache: {[source: string]: Sound} = {};
	private active: Sound[] = [];
	private activeLoops: Sound[] = [];


	static get(): SoundManager {
		if (!SoundManager.instance) {
			SoundManager.instance = new SoundManager();
		}
		return SoundManager.instance;
	}

	private constructor() {
		// do nothing
	}

	/**
	 * Retrieves finite active sounds.
	 */
	getActive(): Sound[] {
		return this.active;
	}

	/**
	 * Retrieves looping active sounds.
	 */
	getActiveLoops(): Sound[] {
		return this.activeLoops;
	}

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
	private load(id: string, filename: string): Sound {
		const snd = <Sound> new Audio(filename);
		snd.autoplay = false;
		// load into cache
		this.cache[id] = snd;
		return snd;
	}

	/**
	 * Sets event handlers for when sound finishes.
	 *
	 * @param sound
	 *     The playing sound.
	 */
	private onSoundAdded(sound: Sound) {
		sound.onended = (e) => {
			if (!sound.loop) {
				// remove from active sounds
				const idx = this.active.indexOf(sound);
				if (idx > -1) {
					this.active.splice(idx, 1);
				}
			}
		};

		if (sound.loop) {
			// FIXME: should be removed from this list when destroyed or
			//        preserved if sound continues on map change
			this.activeLoops.push(sound);
		} else {
			this.active.push(sound);
		}
	}

	/**
	 * Plays a sound.
	 *
	 * @param soundName
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @param loop
	 *     Whether or not sound should be looped.
	 */
	private playEffect(soundName: string, volume=1.0, loop=false) {
		// check volume sanity
		if (volume > 1) {
			volume = 1.0;
		} else if (volume < 0) {
			volume = 0.0;
		}

		// check cache first
		let sound = this.cache[soundName];
		if (sound) {
			// TODO: handle HTMLAudioElement.error
			if (!sound.paused && !sound.ended) {
				if (sound.loop) {
					// don't play loops over each other
					return;
				}
				// create a temporary sound instead of interrupting the cached one
				const addSound = <Sound> new Audio(sound.src);
				addSound.autoplay = true;
				addSound.volume = sound.volume;
				this.onSoundAdded(addSound);
				return;
			}
		} else {
			// add new sound to cache
			sound = this.load(soundName,
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
		this.onSoundAdded(sound);
	}

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
	 */
	playLocalizedEffect(x: number, y: number, radius: number,
			layer: number, soundName: string, volume=1.0, loop=false) {
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

		this.playEffect(soundName, volume, loop);
	}

	/**
	 * Plays a sound with uniform volume.
	 *
	 * @param soundName
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @param loop
	 *     Whether or not sound should be looped.
	 */
	playGlobalizedEffect(soundName: string, volume=1.0, loop=false) {
		if (!stendhal.config.getBoolean("ui.sound")) {
			return;
		}
		this.playEffect(soundName, volume, loop);
	}

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
	 */
	playLocalizedLoop(x: number, y: number, radius: number, layer: number,
			soundName: string, volume=1.0) {
		this.playLocalizedEffect(x, y, radius, layer, soundName, volume,
				true);
	}

	/**
	 * Loops a sound with uniform volume.
	 *
	 * @param soundName
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 */
	playGlobalizedLoop(soundName: string, volume=1.0) {
		this.playGlobalizedEffect(soundName, volume, true);
	}

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
	 */
	playLocalizedMusic(x: number, y: number, radius: number,
			layer: number, musicName: string, volume=1.0) {
		// load into cache so playLocalizedEffect doesn't look in "data/sounds"
		if (!this.cache[musicName]) {
			this.load(musicName,
					stendhal.paths.music + "/" + musicName + ".ogg");
		}
		this.playLocalizedLoop(x, y, radius, layer, musicName, volume);
	}

	/**
	 * Loops a sound with uniform volume.
	 *
	 * @param musicName
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 */
	playGlobalizedMusic(musicName: string, volume=1.0) {
		// load into cache so playGlobalizedEffect doesn't look in "data/sounds"
		if (!this.cache[musicName]) {
			this.load(musicName,
					stendhal.paths.music + "/" + musicName + ".ogg");
		}
		this.playGlobalizedLoop(musicName, volume);
	}

	/**
	 * Stops all currently playing sounds.
	 *
	 * @param clean
	 *     If <code>true</code>, removes loops instead of pausing.
	 * @return
	 *     <code>true</code> if all sounds were aborted or paused.
	 */
	stopAll(clean: boolean=false): boolean {
		// XXX: pausing active effects may be unnecessary since we are
		//      removing them from memory below
		for (let idx = this.active.length; idx >= 0; idx--) {
			const snd = this.active[idx];
			// sound may have ended during this call
			if (snd) {
				snd.pause();
			}
		}
		let stoppedLoops = true;
		for (let idx = this.activeLoops.length; idx >=0; idx--) {
			const snd = this.activeLoops[idx];
			if (snd) {
				// FIXME: should use mute instead of pause
				snd.pause();
			}
			stoppedLoops = stoppedLoops && (!snd || snd.paused);
		}

		if (clean) {
			this.activeLoops.splice(0, this.activeLoops.length);
			stoppedLoops = this.activeLoops.length == 0;
		}

		this.active.splice(0, this.active.length);
		return this.active.length == 0 && stoppedLoops;
	}

	/**
	 * Mutes all currently playing sounds.
	 *
	 * @return
	 *     <code>true</code> if all sounds were muted.
	 */
	muteAll(): boolean {
		return this.stopAll();
	}

	/**
	 * Unmutes all currently playing sounds.
	 *
	 * @return
	 *     <code>true</code> if all sounds were unmuted.
	 */
	unmuteAll(): boolean {
		let unmuted = true;
		for (const snd of this.activeLoops) {
			snd.muted = false;
			if (snd.paused) {
				snd.play();
			}
			unmuted = unmuted && !snd.paused && !snd.muted;
		}

		return unmuted;
	}

	/**
	 * Called at startup to pre-cache certain sounds.
	 */
	startupCache() {
		// login sound
		this.load("ui/login",
				stendhal.paths.sounds + "/ui/login.ogg");
	}
}
