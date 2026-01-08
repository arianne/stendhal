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

import { stendhal } from "../../stendhal";

import { QuickMenuButton } from "./QuickMenuButton";
import { UIComponentEnum } from "../UIComponentEnum";


/**
 * Button to toggle sound on/off.
 */
export class SoundButton extends QuickMenuButton {

	constructor() {
		super("sound", UIComponentEnum.QMSound);
	}

	/**
	 * Updates button image.
	 */
	public override update() {
		this.setImageBasename(stendhal.config.getBoolean("sound") ? "sound" : "sound-disabled");
	}

	/**
	 * Toggles sound muted state when clicked/tapped.
	 */
	protected override onClick(evt: Event) {
		stendhal.sound.toggleSound();
	}
}
