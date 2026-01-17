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

import { RPEvent } from "marauroa"

import { SoundID } from "../data/sound/SoundID";
import { SoundManager } from "../data/sound/SoundManager";

import { marauroa } from "marauroa"
import { stendhal } from "../stendhal";


export class SoundEvent extends RPEvent {

	sound?: string;
	sound_id?: string;
	volume!: number;
	radius?: number;
	layer!: string;


	execute(entity: any) {
		if (!marauroa.me) {
			return;
		}
		let radius = SoundManager.DEFAULT_RADIUS;
		if (typeof(this["radius"]) === "number") {
			radius = this["radius"];
		}
		if (!marauroa.me.isInSoundRange(radius, entity)) {
			// too far away to hear so don't load
			return;
		}

		let volume = 1;
		// Adjust by the server specified volume, if any
		if (this.hasOwnProperty("volume")) {
			// NOTE: server uses int in range 1-100 while HTMLAudioElement uses float in range 0-1
			volume *= this["volume"] / 100;
		}

		let sound = this["sound"];
		// get sound from ID
		if (this["sound_id"]) {
			sound = SoundID[this["sound_id"]];
		}

		stendhal.sound.playLocalizedEffect(entity["_x"], entity["_y"], radius, this["layer"], sound,
				volume);
	}
}
