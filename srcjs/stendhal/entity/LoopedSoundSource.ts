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

import { InvisibleEntity } from "./InvisibleEntity";

declare var stendhal: any;


export class LoopedSoundSource extends InvisibleEntity {

	private audio?: HTMLAudioElement;
	private loaded = false;


	override set(key: string, value: object) {
		super.set(key, value);

		// FIXME:
		// - better way to detect when entity is fully loaded?
		// - does not get loaded at login if mauraroa.me isn't set
		/* Disabled until looped sounds can cleaned up & volume scaled with player movement
		if (!this.loaded) {
			for (const prop of [this["x"], this["y"], this["sound"],
					this["radius"], this["layer"], this["volume"]]) {
				this.loaded = typeof(prop) !== "undefined";
				if (!this.loaded) {
					break;
				}
			}

			if (this.loaded) {
				this.onLoaded();
			}
		}
		*/
	}

	/**
	 * Plays sound once all required attributes are loaded.
	 */
	onLoaded() {
		const snd = this["sound"];
		if (snd && (!this.audio || snd !== this.audio.src)) {
			const x = this["x"], y = this["y"];
			if (typeof(x) !== "undefined" && typeof(y) !== "undefined") {
				// start new audio
				// FIXME: distinguish between music & looped sounds effects
				this.audio = stendhal.ui.sound.playLocalizedMusic(x, y,
						this["radius"], this["layer"], snd, this["volume"]);
			}
		}
	}
}
