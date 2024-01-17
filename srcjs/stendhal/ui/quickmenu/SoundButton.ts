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

declare var stendhal: any;


/**
 * Button to toggle sound on/off.
 */
export class SoundButton extends ButtonBase {

	// TODO: create better sound image


	constructor() {
		super("sound");
		this.setOnClick(() => {
			// TODO: move sound toggling function here
			stendhal.main.toggleSound();
			this.update();
		});
		this.update();
	}

	private update() {
		this.setImageBasename(stendhal.config.getBoolean("ui.sound") ? "sound" : "sound-disabled");
	}
}
