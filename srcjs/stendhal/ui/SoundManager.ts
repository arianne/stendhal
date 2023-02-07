/***************************************************************************
 *                    Copyright © 2003-2023 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { singletons } from "../SingletonRepo";

declare var marauroa: any;
declare var stendhal: any;


export interface Sound extends HTMLAudioElement {
	basevolume: number;
	radius: number;
	x: number;
	y: number;
}

export class SoundManager {

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

	/** Singleton instance. */
	private static instance: SoundManager;


	/**
	 * Retrieves singleton instance.
	 */
	static get(): SoundManager {
		if (!SoundManager.instance) {
			SoundManager.instance = new SoundManager();
		}
		return SoundManager.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
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
	 * Retrieves list of valid layer string identifiers.
	 */
	public getLayerNames(): string[] {
		return this.layers;
	}

	/**
	 * Retrieves the string identifier of the associated layer index.
	 */
	public getLayerName(layer: number): string {
		// default to GUI
		let layername = "gui";
		if (layer >= 0 && layer < this.layers.length) {
			layername = this.layers[layer];
		} else {
			console.warn("unknown layer index: " + layer);
		}
		return layername;
	}

	/**
	 * Sets event handlers for when sound finishes.
	 *
	 * @param layername
	 *     Name of layer sound will play on.
	 * @param sound
	 *     The playing sound.
	 */
	private onSoundAdded(layername: string, sound: Sound) {
		sound.onended = (e) => {
			// remove from active sounds
			const idx = this.active[layername].indexOf(sound);
			if (idx > -1) {
				this.active[layername].splice(idx, 1);
			}
		};
		// FIXME: loops should be preserved if sound continues on map change
		this.active[layername].push(sound);
	}

	/**
	 * Plays a sound.
	 *
	 * @param soundname
	 *     Sound file basename.
	 * @param layername
	 *     Name of layer sound will play on.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @param loop
	 *     Whether or not sound should be looped.
	 * @return
	 *     The new sound instance.
	 */
	private playEffect(soundname: string, layername: string, volume=1.0,
			loop=false): any {
		const muted = !stendhal.config.getBoolean("ui.sound");
		if (muted && !loop) {
			// don't add non-looping sounds when muted
			return;
		}

		// check volume sanity
		volume = this.normVolume(volume);
		// apply layer volume adjustments
		const actualvolume = this.getAdjustedVolume(layername, volume);

		// check cache first
		let snd = this.cache[soundname] || this.cacheGlobal[soundname];
		if (!snd) {
			// add new sound to cache
			snd = this.load(soundname,
					stendhal.paths.sounds + "/" + soundname + ".ogg");
		}

		if (!this.cache[soundname]) {
			// add globally cached sounds to map cache
			this.cache[soundname] = snd;
		}

		if (layername === "gui" && !this.cacheGlobal[soundname]) {
			// keep gui sounds in global cache
			this.cacheGlobal[soundname] = snd;
		}

		// create a copy so multiple instances can be played simultaneously
		const scopy = <Sound> snd.cloneNode();
		scopy.autoplay = true;
		scopy.basevolume = volume;
		scopy.volume = Math.min(actualvolume, volume);
		scopy.loop = loop;
		scopy.muted = muted;

		this.onSoundAdded(layername, scopy);
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
	 * @param soundname
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @param loop
	 *     Whether or not sound should be looped.
	 * @return
	 *     The new sound instance.
	 */
	playLocalizedEffect(x: number, y: number, radius: number,
			layer: number, soundname: string, volume=1.0, loop=false): any {
		const layername = this.getLayerName(layer);
		const snd = this.playEffect(soundname, layername, volume, loop);
		if (!snd) {
			return;
		}

		// Further adjustments if the sound has a radius
		if (radius) {
			if (!marauroa.me || !x) {
				// can't calculate distance yet
				snd.volume = 0.0;
			} else {
				this.adjustForDistance(layername, snd, radius, x, y,
						marauroa.me["_x"], marauroa.me["_y"]);
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
	 * @param soundname
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
	playGlobalizedEffect(soundname: string, layer?: number, volume=1.0, loop=false): any {
		// default to gui layer
		if (typeof(layer) === "undefined") {
			layer = this.layers.indexOf("gui");
		}
		return this.playEffect(soundname, this.getLayerName(layer), volume, loop);
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
	 * @param soundname
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @return
	 *     The new sound instance.
	 */
	playLocalizedLoop(x: number, y: number, radius: number, layer: number,
			soundname: string, volume=1.0): any {
		return this.playLocalizedEffect(x, y, radius, layer, soundname,
				volume, true);
	}

	/**
	 * Loops a sound with uniform volume.
	 *
	 * @param soundname
	 *     Sound file basename.
	 * @param layer
	 *     Channel index sound will play on.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @return
	 *     The new sound instance.
	 */
	playGlobalizedLoop(soundname: string, layer?: number, volume=1.0): any {
		return this.playGlobalizedEffect(soundname, layer, volume, true);
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
	 * @param musicname
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @return
	 *     The new sound instance.
	 */
	playLocalizedMusic(x: number, y: number, radius: number,
			layer: number, musicname: string, volume=1.0): any {
		// load into cache so playEffect doesn't look in "data/sounds"
		if (!this.cache[musicname]) {
			this.load(musicname,
					stendhal.paths.music + "/" + musicname + ".ogg");
		}
		return this.playLocalizedLoop(x, y, radius, layer, musicname, volume);
	}

	/**
	 * Loops a sound with uniform volume.
	 *
	 * @param musicname
	 *     Sound file basename.
	 * @param volume
	 *     Volume level between 0.0 and 1.0.
	 * @return
	 *     The new sound instance.
	 */
	playGlobalizedMusic(musicname: string, volume=1.0): any {
		// load into cache so playEffect doesn't look in "data/sounds"
		if (!this.cache[musicname]) {
			this.load(musicname,
					stendhal.paths.music + "/" + musicname + ".ogg");
		}
		return this.playGlobalizedLoop(musicname,
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
	 * @param layername
	 *    String name of layer this sound is played on.
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
	adjustForDistance(layername: string, snd: Sound, radius: number,
			sx: number, sy: number, ex: number, ey: number) {
		const xdist = ex - sx;
		const ydist = ey - sy;
		const dist2 = xdist * xdist + ydist * ydist;
		const rad2 = radius * radius;
		if (dist2 > rad2) {
			// outside the specified radius
			snd.volume = 0.0;
		} else {
			const maxvol = this.getAdjustedVolume(layername, snd.basevolume);
			// The sound api does not guarantee anything about how the volume
			// works, so it does not matter much how we scale it.
			snd.volume = this.normVolume(Math.min(rad2 / (dist2 * 20), maxvol));
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
	 * Calculates actual volume against layer volume levels.
	 *
	 * @param layername
	 *     String identifier of layer sound is playing on.
	 * @param basevol
	 *     The sounds base volume level.
	 * @return
	 *     Volume level adjusted with "master" & associated layer.
	 */
	private getAdjustedVolume(layername: string, basevol: number): number {
		let actualvol = basevol * stendhal.config.getFloat("ui.sound.master.volume");
		const lvol = stendhal.config.getFloat("ui.sound." + layername + ".volume");
		if (typeof(lvol) !== "number") {
			console.warn("cannot adjust volume for layer \"" + layername + "\"");
			return actualvol;
		}
		return actualvol * lvol;
	}

	/**
	 * Applies the adjusted volume level to a sound.
	 *
	 * @param layername
	 *     String identifier of layer sound is playing on.
	 * @param snd
	 *     The sound to be adjusted.
	 */
	private applyAdjustedVolume(layername: string, snd: Sound) {
		snd.volume = this.normVolume(this.getAdjustedVolume(layername, snd.basevolume));
	}

	/**
	 * Sets layer volume level.
	 *
	 * TODO: return numeric value for determining what went wrong
	 *
	 * @param layername
	 *     Name of layer being adjusted.
	 * @param vol
	 *     Volume level.
	 * @return
	 *     <code>true</code> if volume level was set.
	 */
	setVolume(layername: string, vol: number): boolean {
		const oldvol = stendhal.config.getFloat("ui.sound." + layername + ".volume");
		if (typeof(oldvol) === "undefined" || oldvol === "") {
			return false;
		}

		stendhal.config.set("ui.sound." + layername + ".volume", this.normVolume(vol));

		const layerset = layername === "master" ? this.layers : [layername];
		for (const l of layerset) {
			const layersounds = this.active[l];
			if (typeof(layersounds) === "undefined") {
				continue;
			}
			for (const snd of layersounds) {
				if (typeof(snd.radius) === "number"
						&& typeof(snd.x) === "number"
						&& typeof(snd.y) === "number") {
					this.adjustForDistance(layername, snd, snd.radius,
							snd.x, snd.y, marauroa.me["_x"], marauroa.me["_y"]);
				} else {
					this.applyAdjustedVolume(layername, snd);
				}
			}
		}
		return true;
	}

	/**
	 * Retrieves layer volume level.
	 *
	 * @param layername
	 *     Layer string identifier.
	 * @return
	 *     Current volume level of layer.
	 */
	getVolume(layername="master"): number {
		let vol = stendhal.config.getFloat("ui.sound." + layername + ".volume");
		if (typeof(vol) === "undefined" || isNaN(vol) || !isFinite(vol)) {
			console.warn("could not get volume for channel \"" + layername + "\"");
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
