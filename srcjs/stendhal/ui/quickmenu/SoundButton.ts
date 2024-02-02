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

import { QuickMenuButton } from "./QuickMenuButton";
import { singletons } from "../../SingletonRepo";

declare var stendhal: any;


/**
 * Button to toggle sound on/off.
 */
export class SoundButton extends QuickMenuButton {

	constructor() {
		super("sound");
	}

	/**
	 * Updates button icon.
	 */
	public override update() {
		this.setImageBasename(stendhal.config.getBoolean("client.sound") ? "sound" : "sound-disabled");
	}

	protected override onClick(evt: Event) {
		singletons.getSoundManager().toggleSound();
	}
}
