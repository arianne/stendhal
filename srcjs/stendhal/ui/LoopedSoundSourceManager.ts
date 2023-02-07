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

import { Sound } from "./SoundManager";
import { SoundManager } from "./SoundManager";

import { LoopedSoundSource } from "../entity/LoopedSoundSource";

declare var marauroa: any;
declare var stendhal: any;


// server doesn't distinguish between music & looped sound effects so we
// need make some mappings to tell client which directory to use
const sfxLoops: {[name: string]: boolean} = {
	"clock-1": true,
	"fire-1": true,
	"sleep-1": true
}

export class LoopedSoundSourceManager {

	private readonly sndMan = SoundManager.get();

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
	 * Determines whether sound file should be opened from music or sounds
	 * directory.
	 */
	private isMusic(filename: string): boolean {
		return !filename.startsWith("loop/")
				&& !filename.startsWith("weather/")
				&& !sfxLoops[filename];
	}

	/**
	 * Retrieves the looped sound sources for the current zone.
	 *
	 * @return
	 *     Sound sources.
	 */
	getSources(): {[id: string]: any} {
		return this.sources;
	}

	/**
	 * Adds a new looped sound source to be played.
	 *
	 * @param source
	 *     Sound source.
	 * @return
	 *     <code>true</code> if addition succeeded.
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
			snd = this.sndMan.playLocalizedMusic(source["x"], source["y"],
					source["radius"], layer, source["sound"], source["volume"]);
		} else {
			snd = this.sndMan.playLocalizedLoop(source["x"], source["y"],
					source["radius"], layer, source["sound"], source["volume"]);
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
	 * @param id
	 *     Sound source identifier.
	 * @return
	 *     <code>true</code> if removal succeeded.
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
		if (!this.sndMan.stop(source.layer, source.sound)) {
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
	 * @return
	 *     <code>true</code> if the sources list is empty & all removed
	 *     sources returned successful.
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
	 * @return
	 *     All <code>LoopedSoundSource</code> entities in current zone.
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
	 * This is called after zone is created to make sure looped sound
	 * sources are added properly.
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
	 * @param x
	 *     The new X coordinate of listening entity.
	 * @param y
	 *     The new Y coordinate of listening entity.
	 */
	onDistanceChanged(x: number, y: number) {
		for (const ent of this.getZoneEntities()) {
			if (ent.isLoaded()) {
				const layername = this.sndMan.getLayerName(ent["layer"]);
				const snd = this.sources[ent["id"]].sound;
				this.sndMan.adjustForDistance(layername, snd, ent["radius"],
						ent["x"], ent["y"], x, y);
			}
		}
	}
}
