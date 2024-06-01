/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/


/**
 * A playable sound.
 */
export interface SoundObject extends HTMLAudioElement {
	/** Base volume level unique to this sound. */
	basevolume: number;
	/** Distance at which sound can be heard. */
	radius?: number;
	/** Coordinate of sound entity on X axis. */
	x?: number;
	/** Coordinate of sound entity on Y axis. */
	y?: number;
	/** String identifier. */
	basename?: string;
}

/**
 * Factory for creating sound objects.
 */
export class SoundFactory {

	/**
	 * Hidden constructor (use `SoundFactory.create`).
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Creates a new sound object.
	 *
	 * @param src {string}
	 *   Sound filename path (default: `undefined`).
	 */
	static create(src?: string): SoundObject {
		const sound = new Audio(src) as SoundObject;
		sound.basevolume = sound.volume;
		return sound;
	}
}
