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

	private loaded = false;


	override set(key: string, value: object) {
		super.set(key, value);

		if (!this.loaded && this["id"] && this["x"] && this["y"]
				&& this["sound"] && this["volume"] && this["layer"]
				&& this["radius"]) {
			this.onLoaded();
			this.loaded = true;
		}
	}

	/**
	* Plays sound once all required attributes are loaded.
	*/
	private onLoaded() {
		// TODO:
		//~ if (this["sound"]) {
			//~ const x = this["x"], y = this["y"];
			//~ if (typeof(x) !== "undefined" && typeof(y) !== "undefined") {
				//~ // start new audio
				//~ // FIXME: distinguish between music & looped sounds effects
				//~ stendhal.ui.sound.playLocalizedMusic(x, y, this["radius"],
					//~ this["layer"], this["sound"], this["volume"]);
			//~ }
		//~ }
	}
}
