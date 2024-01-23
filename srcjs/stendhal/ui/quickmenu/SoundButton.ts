/***************************************************************************
 *                       Copyright © 2024 - Stendhal                       *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ButtonBase } from "./ButtonBase";
import { singletons } from "../../SingletonRepo";

declare var stendhal: any;


/**
 * Button to toggle sound on/off.
 */
export class SoundButton extends ButtonBase {

	constructor() {
		super("sound");
	}

	/**
	 * Updates button icon.
	 */
	public override update() {
		this.setImageBasename(stendhal.config.getBoolean("ui.sound") ? "sound" : "sound-disabled");
	}

	private toggleSound() {
		const enabled = !stendhal.config.getBoolean("ui.sound");
		stendhal.config.set("ui.sound", enabled);
		this.update();

		// update sound manager
		const soundMan = singletons.getSoundManager();
		if (enabled) {
			if (!soundMan.unmuteAll()) {
				let errmsg = "Failed to unmute sounds:";
				for (const snd of soundMan.getActive()) {
					if (snd && snd.src && snd.muted) {
						errmsg += "\n- " + snd.src;
					}
				}
				console.warn(errmsg);
			}
		} else {
			if (!soundMan.muteAll()) {
				let errmsg = "Failed to mute sounds:";
				for (const snd of soundMan.getActive()) {
					if (snd && snd.src && !snd.muted) {
						errmsg += "\n- " + snd.src;
					}
				}
				console.warn(errmsg);
			}
		}
	}

	protected override onClick(evt: Event) {
		this.toggleSound();
	}
}
