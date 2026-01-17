/***************************************************************************
 *                 Copyright © 2003-2026 - Faiumoni e. V.                  *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { marauroa } from "marauroa"
import { stendhal } from "../../stendhal";

import { LoopedSoundSource } from "../../entity/LoopedSoundSource";


// server doesn't distinguish between music & looped sound effects so we
// need make some mappings to tell client which directory to use
const sfxLoops: {[name: string]: boolean} = {
	"clock-1": true,
	"fire-1": true,
	"sleep-1": true
}

/**
 * Manages playing looping sounds from entity sources.
 */
export class LoopedSoundSourceManager {

	/** Detected sound sources in current zone. */
	private sources: {[id: string]: any} = {};

	/** Singleton instance. */
	private static instance: LoopedSoundSourceManager;


	/**
	 * Retrieves singleton instance.
	 *
	 * @return
	 *     LoopedSoundSourceManager.
	 */
	static get(): LoopedSoundSourceManager {
		if (!LoopedSoundSourceManager.instance) {
			LoopedSoundSourceManager.instance = new LoopedSoundSourceManager();
		}
		return LoopedSoundSourceManager.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Determines whether sound file should be opened from music or sounds directory.
	 *
	 * @param path {string}
	 *   Path sto sound file.
	 * @return {boolean}
	 *   `true` if "path" denotes a music sound file.
	 */
	private isMusic(path: string): boolean {
		return !path.startsWith("loop/") && !path.startsWith("weather/") && !sfxLoops[path];
	}

	/**
	 * Retrieves the looped sound sources for the current zone.
	 *
	 * @return {object}
	 *   Sound sources detected in current zone.
	 */
	getSources(): {[id: string]: any} {
		return this.sources;
	}

	/**
	 * Adds a new looped sound source to be played.
	 *
	 * @param source {entity.LoopedSoundSource.LoopedSoundSource}
	 *   Sound source.
	 * @return {boolean}
	 *   `true` if addition succeeded.
	 */
	addSource(source: LoopedSoundSource): boolean {
		const id = source["id"];
		if (!marauroa.me) {
			console.warn("tried to add looped sound source with ID '" + id + "' before player was ready");
			return false;
		}
		if (this.sources[id]) {
			console.warn("tried to add looped sound source with existing ID '" + id + "'");
			return true;
		}

		let snd: any;
		const layer = source["layer"];
		if (this.isMusic(source["sound"])) {
			snd = stendhal.sound.playLocalizedMusic(source["x"], source["y"], source["radius"], layer,
					source["sound"], source["volume"]);
		} else {
			snd = stendhal.sound.playLocalizedLoop(source["x"], source["y"], source["radius"], layer,
					source["sound"], source["volume"]);
		}

		if (!snd) {
			console.error("failed to add looped sound source with ID '" + id + "'");
			return false;
		}

		this.sources[id] = {layer: layer, sound: snd};
		return true;
	}

	/**
	 * Removes a currently playing sound source.
	 *
	 * @param id {string}
	 *   Sound source identifier.
	 * @return {boolean}
	 *   `true` if removal succeeded.
	 */
	removeSource(id: string): boolean {
		const source = this.sources[id];
		if (typeof(source) === "undefined") {
			console.warn("tried to remove unknown looped sound source with ID '" + id + "'");
			return true;
		}

		// FIXME: doesn't always delete reference
		delete this.sources[id];
		const errmsg = [];
		if (!stendhal.sound.stop(source.layer, source.sound)) {
			errmsg.push("failed to stop looped sound source with ID '" + id + "' ("
					+ source.sound.src + ")");
		}
		if (this.sources[id]) {
			errmsg.push("failed to remove looped sound source with ID '" + id + "' ("
					+ source.sound.src + ")");
		}

		if (errmsg.length > 0) {
			for (const msg of errmsg) {
				console.error(msg);
			}
			return false;
		}
		return true;
	}

	/**
	 * Stops all playing looped sounds.
	 *
	 * FIXME: not all sounds stopped/removed
	 *
	 * @return {boolean}
	 *   `true` if the sources list is empty & all removed sources returned successful.
	 */
	removeAll(): boolean {
		let removed = true;
		for (const id in this.sources) {
			removed = removed && this.removeSource(id);
		}
		// FIXME: not all sounds being removed
		if (!removed) {
			this.sources = {};
		}
		return Object.keys(this.sources).length == 0;
	}

	/**
	 * Retrieves a list of looped sound sources.
	 *
	 * @return {entity.LoopedSoundSource.LoopedSoundSource[]}
	 *   All sound source entities in current zone.
	 */
	private getZoneEntities(): LoopedSoundSource[] {
		const ents: LoopedSoundSource[] = [];
		if (stendhal.zone.entities) {
			for (const ent of stendhal.zone.entities) {
				if (ent instanceof LoopedSoundSource) {
					ents.push(ent);
				}
			}
		}
		return ents;
	}

	/**
	 * Called after zone is created to make sure looped sound sources are added properly.
	 */
	onZoneReady() {
		for (const ent of this.getZoneEntities()) {
			if (!ent.isLoaded()) {
				this.addSource(ent);
			}
		}
	}

	/**
	 * Adjusts volume level for each looped sound source in current zone.
	 *
	 * @param x {number}
	 *   The new X coordinate of listening entity.
	 * @param y {number}
	 *   The new Y coordinate of listening entity.
	 */
	onDistanceChanged(x: number, y: number) {
		for (const ent of this.getZoneEntities()) {
			if (ent.isLoaded()) {
				const layerName = stendhal.sound.checkLayer(ent["layer"]);
				const snd = this.sources[ent["id"]].sound;
				stendhal.sound.adjustForDistance(layerName, snd, ent["radius"],
						ent["x"], ent["y"], x, y);
			}
		}
	}
}
