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

import singletons from "../util/SingletonRepo";

declare var marauroa: any;
declare var stendhal: any;

stendhal.config = stendhal.config || singletons.getConfigManager();


export interface Sound extends HTMLAudioElement {
	basevolume: number;
	radius: number;
	x: number;
	y: number;
}

export class SoundManager {

	private static instance: SoundManager;

	public readonly layers: string[] = ["music", "ambient", "creature", "sfx", "gui"];
	private cacheGlobal: {[source: string]: HTMLAudioElement} = {};
	private cache: {[source: string]: HTMLAudioElement} = {};
	private active: {[layer: string]: Sound[]} = {
		["music"]: [],
		["ambient"]: [],
		["creature"]: [],
		["sfx"]: [],
		["gui"]: []
	};


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
	 * Retrieves active sounds.
	 *
	 * @param includeGui
	 *     Will include sounds from the gui layer (default: false).
	 */
	getActive(includeGui=false): Sound[] {
		const active: Sound[] = [];
		for (const layerName of this.layers) {
			if (layerName === "gui" && !includeGui) {
				continue;
			}
			for (const snd of this.active[layerName]) {
				active.push(snd);
			}
		}
		return active;
	}

	/**
	 * Initializes an audio object & loads into the cache.
	 *
	 * @param id
	 *     Identifier string used to retrieve from cache.
	 * @param filename
	 *     Path to sound file.
	 * @param global
	 *     Store in session cache instead of map.
	 * @return
	 *     New audio object.
	 */
	private load(id: string, filename: string, global=false): HTMLAudioElement {
		const snd = new Audio(filename);
		snd.autoplay = false;
		// load into cache
		if (global) {
			// globally cached sounds are not removed on map change
			this.cacheGlobal[id] = snd;
		} else {
			this.cache[id] = snd;
		}
		return snd;
	}

	/**
	 * Sets event handlers for when sound finishes.
	 *
	 * @param layer
	 *     Channel index sound will play on.
	 * @param sound
	 *     The playing sound.
	 */
	private onSoundAdded(layer: number, sound: Sound) {
		const layerName = this.layers[layer];
		sound.onended = (e) => {
			// remove from active sounds
			const idx = this.active[layerName].indexOf(sound);
			if (idx > -1) {
				this.active[layerName].splice(idx, 1);
			}
		};
		// FIXME: loops should be preserved if sound continues on map change
		this.active[layerName].push(sound);
	}

	/**
	 * Plays a sound.
	 *
	 * @param soundName
	 *     Sound file basename.
	 * @param layer
	 *     Channel index sound will play on.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @param loop
	 *     Whether or not sound should be looped.
	 * @return
	 *     The new sound instance.
	 */
	private playEffect(soundName: string, layer: number, volume=1.0, loop=false): any {
		const muted = !stendhal.config.getBoolean("ui.sound");
		if (muted && !loop) {
			// don't add non-looping sounds when muted
			return;
		}
		// default to GUI layer
		if (layer < 0 || layer >= this.layers.length) {
			console.warn("tried to add sound to non-existent layer: " + layer);
			layer = this.layers.indexOf("gui");
		}
		// check volume sanity
		volume = this.normVolume(volume);
		// apply master channel level
		const actualvolume = volume * this.getVolume();

		// check cache first
		let snd = this.cache[soundName] || this.cacheGlobal[soundName];
		if (!snd) {
			// add new sound to cache
			snd = this.load(soundName,
					stendhal.paths.sounds + "/" + soundName + ".ogg");
		}
		if (!this.cache[soundName]) {
			// add globally cached sound to map cache
			this.cache[soundName] = snd;
		}

		// create a copy so multiple instances can be played simultaneously
		const scopy = <Sound> snd.cloneNode();
		scopy.autoplay = true;
		scopy.basevolume = volume;
		scopy.volume = actualvolume;
		scopy.loop = loop;
		scopy.muted = muted;

		this.onSoundAdded(layer, scopy);
		return scopy;
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
	 *     Channel index sound will play on.
	 * @param soundName
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @param loop
	 *     Whether or not sound should be looped.
	 * @return
	 *     The new sound instance.
	 */
	playLocalizedEffect(x: number, y: number, radius: number,
			layer: number, soundName: string, volume=1.0, loop=false): any {
		const snd = this.playEffect(soundName, layer, volume, loop);
		if (!snd) {
			return;
		}

		// Further adjustments if the sound has a radius
		if (radius) {
			if (!marauroa.me || !x) {
				// can't calculate distance yet
				snd.volume = 0.0;
			} else {
				this.adjustForDistance(snd, radius, x, y, marauroa.me["_x"],
						marauroa.me["_y"]);
			}
		}

		snd.radius = radius;
		snd.x = x;
		snd.y = y;
		return snd;
	}

	/**
	 * Plays a sound with uniform volume.
	 *
	 * @param soundName
	 *     Sound file basename.
	 * @param layer
	 *     Channel index sound will play on.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @param loop
	 *     Whether or not sound should be looped.
	 * @return
	 *     The new sound instance.
	 */
	playGlobalizedEffect(soundName: string, layer?: number, volume=1.0, loop=false): any {
		// default to gui layer
		if (!layer) {
			layer = this.layers.indexOf("gui");
		}
		return this.playEffect(soundName, layer, volume, loop);
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
	 *     Channel index sound will play on.
	 * @param soundName
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @return
	 *     The new sound instance.
	 */
	playLocalizedLoop(x: number, y: number, radius: number, layer: number,
			soundName: string, volume=1.0): any {
		return this.playLocalizedEffect(x, y, radius, layer, soundName,
				volume, true);
	}

	/**
	 * Loops a sound with uniform volume.
	 *
	 * @param soundName
	 *     Sound file basename.
	 * @param layer
	 *     Channel index sound will play on.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @return
	 *     The new sound instance.
	 */
	playGlobalizedLoop(soundName: string, layer?: number, volume=1.0): any {
		return this.playGlobalizedEffect(soundName, layer, volume, true);
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
	 * @return
	 *     The new sound instance.
	 */
	playLocalizedMusic(x: number, y: number, radius: number,
			layer: number, musicName: string, volume=1.0): any {
		// load into cache so playEffect doesn't look in "data/sounds"
		if (!this.cache[musicName]) {
			this.load(musicName,
					stendhal.paths.music + "/" + musicName + ".ogg");
		}
		return this.playLocalizedLoop(x, y, radius, layer, musicName, volume);
	}

	/**
	 * Loops a sound with uniform volume.
	 *
	 * @param musicName
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @return
	 *     The new sound instance.
	 */
	playGlobalizedMusic(musicName: string, volume=1.0): any {
		// load into cache so playEffect doesn't look in "data/sounds"
		if (!this.cache[musicName]) {
			this.load(musicName,
					stendhal.paths.music + "/" + musicName + ".ogg");
		}
		return this.playGlobalizedLoop(musicName,
				this.layers.indexOf("music"), volume);
	}

	/**
	 * Stops a sound & removes it from active group.
	 *
	 * @param layer
	 *     Channel index sound is playing on.
	 * @param sound
	 *     The sound to be stopped.
	 * @return
	 *     <code>true</code> if succeeded.
	 */
	stop(layer: number, sound: Sound): boolean {
		if (layer < 0 || layer >= this.layers.length) {
			console.error("cannot stop sound on non-existent layer: " + layer);
			return false;
		}

		const layerName = this.layers[layer];
		const group = this.active[layerName];
		const idx = group.indexOf(sound);
		if (sound && idx > -1) {
			sound.pause();
			sound.currentTime = 0;
			if (sound.onended) {
				sound.onended(new Event("stopsound"));
			}
		}
		return this.active[layerName].indexOf(sound) < 0;
	}

	/**
	 * Stops all currently playing sounds.
	 *
	 * @param includeGui
	 *     If <code>true</code>, sounds on the gui layer will stopped
	 *     as well.
	 * @return
	 *     <code>true</code> if all sounds were aborted or paused.
	 */
	stopAll(includeGui=false): boolean {
		let stopped = true;
		for (const layerName of this.layers) {
			if (layerName === "gui" && !includeGui) {
				continue;
			}
			const curLayer = this.active[layerName];
			// XXX: just iterating over indexes doesn't remove all sounds. async issue?
			while (curLayer.length > 0) {
				this.stop(this.layers.indexOf(layerName), curLayer[0]);
			}
			stopped = stopped && this.active[layerName].length == 0;
		}
		return stopped;
	}

	/**
	 * Mutes all currently playing sounds.
	 *
	 * @return
	 *     <code>true</code> if all sounds were muted.
	 */
	muteAll(): boolean {
		let muted = true;
		for (const layerName of this.layers) {
			for (const snd of this.active[layerName]) {
				snd.muted = true;
				muted = muted && snd.muted;
			}
		}
		return muted;
	}

	/**
	 * Unmutes all currently playing sounds.
	 *
	 * @return
	 *     <code>true</code> if all sounds were unmuted.
	 */
	unmuteAll(): boolean {
		let unmuted = true;
		for (const layerName of this.layers) {
			for (const snd of this.active[layerName]) {
				snd.muted = false;
				unmuted = unmuted && !snd.paused && !snd.muted;
			}
		}

		return unmuted;
	}

	/**
	 * Adjusts volume level relative to distance.
	 *
	 * FIXME: hearing distance is slightly further than in Java client
	 *        See: games.stendhal.client.sound.facade.Audible*Area
	 *
	 * @param snd
	 *    The sound to be adjusted.
	 * @param radius
	 *     Radius at which sound can be heard.
	 * @param sx
	 *     X coordinate of sound entity.
	 * @param sy
	 *     Y coordinate of sound entity.
	 * @param ex
	 *     X coordinate of listening entity.
	 * @param ey
	 *     Y coordinate of listening entity.
	 */
	adjustForDistance(snd: Sound, radius: number, sx: number, sy: number,
			ex: number, ey: number) {
		const xdist = ex - sx;
		const ydist = ey - sy;
		const dist2 = xdist * xdist + ydist * ydist;
		const rad2 = radius * radius;
		if (dist2 > rad2) {
			// outside the specified radius
			snd.volume = 0.0;
		} else {
			// The sound api does not guarantee anything about how the volume
			// works, so it does not matter much how we scale it.
			snd.volume = Math.min(rad2 / (dist2 * 20), snd.basevolume) * this.getVolume();
		}
	}

	/**
	 * Normalizes volume level.
	 *
	 * @param vol
	 *     The input volume.
	 * @return
	 *     Level between 0 and 1.
	 */
	private normVolume(vol: number): number {
		return vol < 0 ? 0 : vol > 1 ? 1 : vol;
	}

	/**
	 * Sets volume level.
	 *
	 * FIXME: not all sound get re-adjusted for distance
	 *
	 * @param chan
	 *     Channel name.
	 * @param vol
	 *     Volume level.
	 * @return
	 *     <code>true</code> if volume level was set.
	 */
	setVolume(chan: string, vol: number): boolean {
		const oldvol = stendhal.config.getFloat("ui.sound." + chan + ".volume");
		if (typeof(oldvol) === "undefined" || oldvol === "") {
			return false;
		}
		vol = this.normVolume(vol);
		stendhal.config.set("ui.sound." + chan + ".volume", vol);
		if (chan === "master") {
			// update active sounds
			const vdiff = vol - oldvol;
			for (const layerName of this.layers) {
				// FIXME: DOMException: Index or size is negative or greater than the allowed amount
				try {
					for (const snd of this.active[layerName]) {
						// mute until we are done adjusting sound
						snd.muted = true;
						snd.volume += vdiff;
						if (typeof(snd.radius) === "number"
								&& typeof(snd.x) === "number"
								&& typeof(snd.y) === "number") {
							this.adjustForDistance(snd, snd.radius, snd.x, snd.y,
									marauroa.me["_x"], marauroa.me["_y"]);
						}
						snd.muted = false;
					}
				} catch (e) {
					console.warn("could not update volume for sound", e);
				}
			}
		}
		return true;
	}

	/**
	 * Retrieves volume level.
	 *
	 * @param chan
	 *     Channel name.
	 * @return
	 *     Current volume level for channel.
	 */
	getVolume(chan="master"): number {
		let vol = stendhal.config.getFloat("ui.sound." + chan + ".volume");
		if (typeof(vol) === "undefined" || isNaN(vol) || !isFinite(vol)) {
			console.warn("could not get volume for channel \"" + chan + "\"");
			return 1;
		}
		return this.normVolume(vol);
	}

	/**
	 * Called at startup to pre-cache certain sounds.
	 */
	startupCache() {
		// login sound
		this.load("ui/login",
				stendhal.paths.sounds + "/ui/login.ogg", true);
	}
}
