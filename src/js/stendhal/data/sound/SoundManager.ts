/***************************************************************************
 *                 Copyright © 2003-2024 - Faiumoni e. V.                  *
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

import { SoundFactory } from "./SoundFactory";
import { SoundObject } from "./SoundFactory";
import { SoundLayer } from "./SoundLayer";

import { singletons } from "../../SingletonRepo";

import { ui } from "../../ui/UI";


/**
 * Manages playing sounds & music.
 *
 * TODO:
 * - rework to handle looped sounds/music better (without gap)
 * - move "<js-root>/data/sound" directory to "<js-root>/sound"
 */
export class SoundManager {

	/** Distance at which entity sounds can be heard. */
	public static readonly DEFAULT_RADIUS = 23;

	/** Layer names & ordering. */
	readonly layers: string[];
	/** Session cache. */
	private cacheGlobal: {[source: string]: SoundObject};
	/** Cache for current map. */
	private cache: {[source: string]: SoundObject};
	/** Actively playing sounds. */
	private active: {[layer: string]: SoundObject[]};

	/** Music instance played globally. */
	private globalMusic?: SoundObject;

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
		this.layers = SoundLayer.names();
		this.cacheGlobal = {};
		this.cache = {};
		this.active = {};
		for (const layerName of this.layers) {
			this.active[layerName] = [];
		}
	}

	/**
	 * Retrieves active sounds.
	 *
	 * @param includeGui {boolean}
	 *   Will include sounds from the gui layer (default: false).
	 * @return {data.SoundFactory.SoundObject[]}
	 *   Array of active sounds.
	 */
	getActive(includeGui=false): SoundObject[] {
		const active: SoundObject[] = [];
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
	 * Retrieves a single active sound.
	 *
	 * @param soundName {string}
	 *   Basename of sound file.
	 * @param includeGui {boolean}
	 *   Will include sounds from the gui layer (default: false).
	 * @return {data.SoundFactory.SoundObject}
	 *   Active sound instance or `undefined`.
	 */
	getActiveByName(soundName: string, includeGui=false): SoundObject|undefined {
		for (const sound of this.getActive(includeGui)) {
			if (soundName === sound.basename) {
				return sound;
			}
		}
	}

	/**
	 * Initializes an audio object & loads into the cache.
	 *
	 * @param id {string}
	 *   Identifier string used to retrieve from cache.
	 * @param filename {string}
	 *   Path to sound file.
	 * @param global {boolean}
	 *   Store in session cache instead of map (default: false).
	 * @return {data.SoundFactory.SoundObject}
	 *   New audio object.
	 */
	private load(id: string, filename: string, global=false): SoundObject {
		const snd = SoundFactory.create(filename);
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
	 *
	 * @return {string[]}
	 *   Array of layer names.
	 */
	getLayerNames(): string[] {
		return this.layers;
	}

	/**
	 * Checks for valid layer.
	 *
	 * @param layer {any}
	 *   Layer name or index.
	 * @return {string}
	 *   Layer name or `undefined`.
	 */
	checkLayer(layer: any): string {
		let layerIndex = -1;
		const ltype = typeof(layer);
		if (ltype === "number") {
			layerIndex = layer;
		} else if (ltype === "string") {
			if (this.layers.indexOf(layer) > -1) {
				return layer;
			}
			if (!Number.isNaN(layer)) {
				layerIndex = parseInt(layer, 10);
			}
		}
		let layerName = this.layers[layerIndex];
		if (!layerName) {
			console.error("invalid sound layer:", layer, new Error());
		}
		return layerName;
	}

	/**
	 * Retrieves array index of layer name.
	 *
	 * @param layerName {string}
	 *   Name of layer or index representation as string.
	 * @return {number}
	 *   Index or -1 if not found.
	 */
	getLayerIndex(layerName: string): number {
		// layer name parameter may be string representation of index (e.g. "0")
		let layerIndex = parseInt(layerName, 10);
		if (Number.isNaN(layerIndex)) {
			layerIndex = this.layers.indexOf(layerName);
		}
		if (layerIndex < 0 || layerIndex >= this.layers.length) {
			console.error("invalid sound layer:", layerName, new Error());
			layerIndex = -1;
		}
		return layerIndex;
	}

	/**
	 * Sets event handlers for when sound finishes.
	 *
	 * @param layerName {string}
	 *   Name of layer sound will play on.
	 * @param sound {data.SoundFactory.SoundObject}
	 *   The playing sound.
	 */
	private onSoundAdded(layerName: string, sound: SoundObject) {
		sound.onended = (e) => {
			// remove from active sounds
			const idx = this.active[layerName].indexOf(sound);
			if (idx > -1) {
				this.active[layerName].splice(idx, 1);
			}
		};
		this.active[layerName].push(sound);
	}

	/**
	 * Plays a sound.
	 *
	 * @param soundName {string}
	 *   Sound file basename.
	 * @param layerName {string}
	 *   Name of layer sound will play on.
	 * @param volume {number}
	 *   Volume level between 0.0 and 1.0 (default 1.0).
	 * @param loop {boolean}
	 *   Whether or not sound should be looped (default: false).
	 * @return {data.SoundFactory.SoundObject}
	 *   The new sound instance or `undefined`.
	 */
	private playEffect(soundName: string, layerName: string, volume=1.0, loop=false): SoundObject|undefined {
		const muted = !stendhal.config.getBoolean("sound");
		if (muted && !loop) {
			// don't add non-looping sounds when muted
			return;
		}

		// check volume sanity
		volume = this.normVolume(volume);
		// apply layer volume adjustments
		const volActual = this.getAdjustedVolume(layerName, volume);

		// check cache first
		let snd = this.cache[soundName] || this.cacheGlobal[soundName];
		if (!snd) {
			// add new sound to cache
			snd = this.load(soundName, stendhal.paths.sounds + "/" + soundName + ".ogg");
		}

		if (!this.cache[soundName]) {
			// add globally cached sounds to map cache
			this.cache[soundName] = snd;
		}

		if (layerName === "gui" && !this.cacheGlobal[soundName]) {
			// keep gui sounds in global cache
			this.cacheGlobal[soundName] = snd;
		}

		// create a copy so multiple instances can be played simultaneously
		const scopy = snd.cloneNode() as SoundObject;
		scopy.autoplay = true;
		scopy.basevolume = volume;
		scopy.volume = Math.min(volActual, volume);
		scopy.loop = loop;
		scopy.muted = muted;
		scopy.basename = soundName;

		this.onSoundAdded(layerName, scopy);
		return scopy;
	}

	/**
	 * Plays a sound with volume relative to distance.
	 *
	 * TODO: re-order parameters to put sound name first
	 *
	 * @param x {number}
	 *   X coordinate of sound source.
	 * @param y {number}
	 *   Y coordinate of sound source.
	 * @param radius {radius}
	 *   Radius at which sound can be heard.
	 * @param layer {string|number}
	 *   Channel name or index sound will play on.
	 * @param soundName {string}
	 *   Sound file basename.
	 * @param volume {number}
	 *   Volume level between 0.0 and 1.0 (default: 1.0).
	 * @param loop {boolean}
	 *   Whether or not sound should be looped (default: false).
	 * @return {data.SoundFactory.SoundObject}
	 *   The new sound instance or `undefined`.
	 */
	playLocalizedEffect(x: number, y: number, radius: number, layer: string|number, soundName: string,
			volume=1.0, loop=false): SoundObject|undefined {
		const layerName = this.checkLayer(layer);
		if (!layerName) {
			return;
		}
		const snd = this.playEffect(soundName, layerName, volume, loop);
		if (!snd) {
			return;
		}

		// Further adjustments if the sound has a radius
		if (radius) {
			if (!marauroa.me || !x) {
				// can't calculate distance yet
				snd.volume = 0.0;
			} else {
				this.adjustForDistance(layerName, snd, radius, x, y, marauroa.me["_x"], marauroa.me["_y"]);
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
	 * @param soundName {string}
	 *   Sound file basename.
	 * @param layer {string|number}
	 *   Channel name or index sound will play on (default: "gui").
	 * @param volume {number}
	 *   Volume level between 0.0 and 1.0 (default: 1.0).
	 * @param loop {boolean}
	 *   Whether or not sound should be looped (default: false).
	 * @return {data.SoundFactory.SoundObject}
	 *   The new sound instance or `undefined`.
	 */
	playGlobalizedEffect(soundName: string, layer: string|number="gui", volume=1.0, loop=false): SoundObject|undefined {
		const layerName = this.checkLayer(layer);
		if (!layerName) {
			return;
		}
		return this.playEffect(soundName, layerName, volume, loop);
	}

	/**
	 * Loops a sound with volume relative to distance.
	 *
	 * TODO: re-order parameters to put sound name first
	 *
	 * @param x {number}
	 *   X coordinate of sound source.
	 * @param y {number}
	 *   Y coordinate of sound source.
	 * @param radius {number}
	 *   Radius at which sound can be heard.
	 * @param layer {string|number}
	 *   Channel name or index sound will play on.
	 * @param soundName {string}
	 *   Sound file basename.
	 * @param volume {number}
	 *   Volume level between 0.0 and 1.0.
	 * @return {data.SoundFactory.SoundObject}
	 *   The new sound instance or `undefind`.
	 */
	playLocalizedLoop(x: number, y: number, radius: number, layer: string|number, soundName: string,
			volume=1.0): SoundObject|undefined {
		return this.playLocalizedEffect(x, y, radius, layer, soundName, volume, true);
	}

	/**
	 * Loops a sound with uniform volume.
	 *
	 * @param soundName {string}
	 *   Sound file basename.
	 * @param layer {string|number}
	 *   Channel name or index sound will play on (default: index of "gui").
	 * @param volume {number}
	 *   Volume level between 0.0 and 1.0 (default: 1.0).
	 * @return {data.SoundFactory.SoundObject}
	 *   The new sound instance or `undefined`.
	 */
	playGlobalizedLoop(soundName: string, layer?: string|number, volume=1.0): SoundObject|undefined {
		return this.playGlobalizedEffect(soundName, layer, volume, true);
	}

	/**
	 * Loops a sound with volume relative to distance.
	 *
	 * TODO: re-order parameters to put music name first
	 *
	 * @param x {number}
	 *   X coordinate of sound source.
	 * @param y {number}
	 *   Y coordinate of sound source.
	 * @param radius {number}
	 *   Radius at which sound can be heard.
	 * @param layer {string|number}
	 *   Channel on which to be played.
	 * @param musicName {string}
	 *   Sound file basename.
	 * @param volume {number}
	 *   Volume level between 0.0 and 1.0.
	 * @return {data.SoundFactory.SoundObject}
	 *   The new sound instance or `undefined`.
	 */
	playLocalizedMusic(x: number, y: number, radius: number, layer: string|number, musicName: string,
			volume=1.0): SoundObject|undefined {
		// load into cache so playEffect doesn't look in "data/sounds"
		if (!this.cache[musicName]) {
			this.load(musicName, stendhal.paths.music + "/" + musicName + ".ogg");
		}
		return this.playLocalizedLoop(x, y, radius, layer, musicName, volume);
	}

	/**
	 * Loops a sound with uniform volume.
	 *
	 * @param musicName {string}
	 *   Sound file basename.
	 * @param volume {number}
	 *   Volume level between 0.0 and 1.0 (default: 1.0).
	 * @return {data.SoundFactory.SoundObject}
	 *   The new sound instance or `undefined`.
	 */
	playGlobalizedMusic(musicName: string, volume=1.0): SoundObject|undefined {
		// load into cache so playEffect doesn't look in "data/sounds"
		if (!this.cache[musicName]) {
			this.load(musicName, stendhal.paths.music + "/" + musicName + ".ogg");
		}
		return this.playGlobalizedLoop(musicName, "music", volume);
	}

	/**
	 * Loops a sound with uniform volume that continues playing accross zone changes.
	 *
	 * Any currently playing instance will be stopped before starting a new one. Also, calling without
	 * parameters will stop current instance if playing.
	 *
	 * @param musicName {string}
	 *   Sound file basename. If value `undefined`, `null`, or empty string music will be stopped.
	 * @param volume {number}
	 *   Volume level between 0.0 and 1.0.
	 */
	playSingleGlobalizedMusic(musicName: string, volume=1.0) {
		if (this.globalMusic && musicName === this.globalMusic.basename) {
			// continue playing when changing maps if music is the same
			return;
		}
		if (this.globalMusic) {
			// NOTE: parameter value MUST be `data.SoundFactory.SoundObject` instance as it isn't included in layer array
			this.stop("music", this.globalMusic);
			if (!musicName) {
				// just stop if name not provided
				this.globalMusic = undefined;
				return;
			}
		}
		if (musicName) {
			this.globalMusic = this.playGlobalizedMusic(musicName, volume);
			if (!this.globalMusic) {
				console.error("failed to play global music:", musicName, new Error());
				return;
			}
			this.globalMusic.onended = null;
			// remove from active list to prevent stopping when player changes zone
			const activeIndex = this.active["music"].indexOf(this.globalMusic);
			if (activeIndex > -1) {
				this.active["music"].splice(activeIndex, 1);
			}
			if (this.active["music"].indexOf(this.globalMusic) > -1) {
				console.warn("failed to remove global music from active list, will be stopped when player changes zones");
			}
		}
	}

	/**
	 * Stops a sound & removes it from active group.
	 *
	 * @param layer {string|number}
	 *   Channel name or index sound is playing on.
	 * @param sound {string|data.SoundFactory.SoundObject}
	 *   Sound name or instance to be stopped.
	 * @return {boolean}
	 *   `true` if succeeded.
	 */
	stop(layer: string|number, sound: string|SoundObject): boolean {
		const layerName = this.checkLayer(layer);
		if (this.getLayerIndex(layerName) < 0) {
			return false;
		}
		const isString = typeof(sound) === "string";
		// use this value to avoid error "Argument of type 'string | SoundObject' is not assignable to parameter of type 'SoundObject'"
		const sSound = !isString ? sound as SoundObject : this.getActiveByName(sound as string);
		if (!sSound) {
			console.error("cannot stop unknown sound:",
					isString ? sound as string : (sound as SoundObject).basename, new Error());
			return false;
		}
		if (this.active[layerName].indexOf(sSound) > -1 || sSound == this.globalMusic) {
			sSound.pause();
			sSound.currentTime = 0;
			if (sSound.onended) {
				sSound.onended(new Event("stopsound"));
			}
		}
		return this.active[layerName].indexOf(sSound) < 0;
	}

	/**
	 * Stops all currently playing sounds.
	 *
	 * NOTE: this if for map changes & should not include single global music instance
	 *
	 * @param includeGui {boolean}
	 *   If `true`, sounds on the gui layer will stopped as well (default: false).
	 * @return {boolean}
	 *   `true` if all sounds were aborted or paused.
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
				this.stop(layerName, curLayer[0]);
			}
			stopped = stopped && this.active[layerName].length == 0;
		}
		return stopped;
	}

	/**
	 * Mutes all currently playing sounds.
	 *
	 * @return {boolean}
	 *   `true` if all sounds were muted.
	 */
	muteAll(): boolean {
		let muted = true;
		for (const layerName of this.layers) {
			for (const snd of this.active[layerName]) {
				snd.muted = true;
				muted = muted && snd.muted;
			}
		}
		// global music is not tracked in layer array
		if (this.globalMusic) {
			this.globalMusic.muted = true;
			muted = muted && this.globalMusic.muted;
		}
		return muted;
	}

	/**
	 * Unmutes all currently playing sounds.
	 *
	 * @return {boolean}
	 *   `true` if all sounds were unmuted.
	 */
	unmuteAll(): boolean {
		let unmuted = true;
		for (const layerName of this.layers) {
			for (const snd of this.active[layerName]) {
				snd.muted = false;
				unmuted = unmuted && !snd.paused && !snd.muted;
			}
		}
		// global music is not tracked in layer array
		if (this.globalMusic) {
			this.globalMusic.muted = false;
			unmuted = unmuted && !this.globalMusic.paused && !this.globalMusic.muted;
		}
		return unmuted;
	}

	/**
	 * Adjusts volume level relative to distance.
	 *
	 * FIXME: hearing distance is slightly further than in Java client
	 *        See: games.stendhal.client.sound.facade.Audible*Area
	 *
	 * @param layerName {string}
	 *   Name of layer this sound is played on.
	 * @param snd {data.SoundFactory.SoundObject}
	 *   The sound to be adjusted.
	 * @param radius {number}
	 *   Radius at which sound can be heard.
	 * @param sx {number}
	 *   X coordinate of sound entity.
	 * @param sy {number}
	 *   Y coordinate of sound entity.
	 * @param ex {number}
	 *   X coordinate of listening entity.
	 * @param ey {number}
	 *   Y coordinate of listening entity.
	 */
	adjustForDistance(layerName: string, snd: SoundObject, radius: number, sx: number, sy: number,
			ex: number, ey: number) {
		const xdist = ex - sx;
		const ydist = ey - sy;
		const dist2 = xdist * xdist + ydist * ydist;
		const rad2 = radius * radius;
		if (dist2 > rad2) {
			// outside the specified radius
			snd.volume = 0.0;
		} else {
			const volMax = this.getAdjustedVolume(layerName, snd.basevolume);
			// The sound api does not guarantee anything about how the volume
			// works, so it does not matter much how we scale it.
			snd.volume = this.normVolume(Math.min(rad2 / (dist2 * 20), volMax));
		}
	}

	/**
	 * Normalizes volume level.
	 *
	 * @param vol {number}
	 *   The input volume.
	 * @return {number}
	 *   Normalized volume level between 0.0 and 1.0.
	 */
	private normVolume(vol: number): number {
		if (isNaN(vol) || !isFinite(vol)) {
			console.warn("Tried to set invalid volume level: " + vol, new Error());
			vol = 1;
		}
		return vol < 0 ? 0 : vol > 1 ? 1 : vol;
	}

	/**
	 * Calculates actual volume against layer volume levels.
	 *
	 * @param layerName {string}
	 *   String identifier of layer sound is playing on.
	 * @param volBase {number}
	 *   The sounds base volume level.
	 * @return {number}
	 *   Volume level adjusted with "master" & associated layer.
	 */
	private getAdjustedVolume(layerName: string, volBase: number): number {
		let volActual = stendhal.config.getInt("sound.master.volume");
		if (typeof(volActual) === "number") {
			// convert to float in range between 0-1
			volActual /= 100;
		} else {
			volActual = 1;
		}
		let lvol = stendhal.config.getInt("sound." + layerName + ".volume");
		if (typeof(lvol) === "number") {
			// convert to float in range between 0-1
			lvol /= 100;
		} else {
			console.warn("cannot adjust volume for layer \"" + layerName + "\"");
			return volActual;
		}
		return volActual * lvol;
	}

	/**
	 * Applies the adjusted volume level to a sound.
	 *
	 * @param layerName {string}
	 *   String identifier of layer sound is playing on.
	 * @param snd {data.SoundFactory.SoundObject}
	 *   The sound to be adjusted.
	 */
	private applyAdjustedVolume(layerName: string, snd: SoundObject) {
		snd.volume = this.normVolume(this.getAdjustedVolume(layerName, snd.basevolume));
	}

	/**
	 * Sets layer volume level.
	 *
	 * TODO: return numeric value for determining what went wrong
	 *
	 * @param layerName {string}
	 *   Name of layer being adjusted.
	 * @param vol {number}
	 *   Volume level between 0.0 and 1.0.
	 * @return
	 *   `true` if volume level was set.
	 */
	setVolume(layerName: string, vol: number): boolean {
		let volOld = stendhal.config.getInt("sound." + layerName + ".volume");
		if (typeof(volOld) === "number") {
			// convert to float in range between 0-1
			volOld /= 100;
		} else {
			return false;
		}

		stendhal.config.set("sound." + layerName + ".volume", Math.floor(this.normVolume(vol) * 100));

		const layerSet = layerName === "master" ? this.layers : [layerName];
		for (const l of layerSet) {
			const layerSounds = this.active[l];
			if (typeof(layerSounds) === "undefined") {
				continue;
			}
			for (const snd of layerSounds) {
				if (typeof(snd.radius) === "number"
						&& typeof(snd.x) === "number"
						&& typeof(snd.y) === "number") {
					this.adjustForDistance(layerName, snd, snd.radius,
							snd.x, snd.y, marauroa.me["_x"], marauroa.me["_y"]);
				} else {
					this.applyAdjustedVolume(layerName, snd);
				}
			}
		}
		// global music is not tracked in layer array
		if (this.globalMusic && (layerName === "music" || layerName === "master")) {
			this.applyAdjustedVolume(layerName, this.globalMusic);
		}
		return true;
	}

	/**
	 * Retrieves layer volume level.
	 *
	 * @param layerName {string}
	 *   Layer string identifier (default: "master").
	 * @return {number}
	 *   Current volume level of layer (returns 1 on error).
	 */
	getVolume(layerName="master"): number {
		// NOTE: config value is integer in range between 0-100
		let vol = stendhal.config.getInt("sound." + layerName + ".volume");
		if (typeof(vol) === "undefined" || isNaN(vol) || !isFinite(vol)) {
			console.warn("could not get volume for channel \"" + layerName + "\"");
			return 1;
		}
		return this.normVolume(vol / 100);
	}

	/**
	 * Toggles muted state of sound system.
	 */
	toggleSound() {
		stendhal.config.set("sound", !stendhal.config.getBoolean("sound"));
		this.onStateChanged();
	}

	/**
	 * Called when sound enabled/disabled state is changed.
	 */
	onStateChanged() {
		if (stendhal.config.getBoolean("sound")) {
			if (!this.unmuteAll()) {
				let errmsg = "Failed to unmute sounds:";
				for (const snd of this.getActive()) {
					if (snd && snd.src && snd.muted) {
						errmsg += "\n- " + snd.src;
					}
				}
				console.warn(errmsg);
			}
		} else {
			if (!this.muteAll()) {
				let errmsg = "Failed to mute sounds:";
				for (const snd of this.getActive()) {
					if (snd && snd.src && !snd.muted) {
						errmsg += "\n- " + snd.src;
					}
				}
				console.warn(errmsg);
			}
		}
		// notify client
		ui.onSoundUpdate();
	}

	/**
	 * Can be called when configuration values change.
	 */
	onConfigUpdate() {
		for (const layerName of ["master", ...this.layers]) {
			let vol = stendhal.config.getInt("sound." + layerName + ".volume");
			if (typeof(vol) === "number") {
				// convert to float in range between 0-1
				vol /= 100;
			} else {
				console.warn("Unrecognized volume value for layer \"" + layerName + "\":", vol);
				// default to 100%
				vol = 1.0;
			}
			this.setVolume(layerName, vol);
		}
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
